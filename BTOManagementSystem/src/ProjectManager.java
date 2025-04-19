import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectManager {
    private static Map<String, BTOProject> projects = new HashMap<>(); // Project name as key
    private static final String CSV_FILE = "ProjectList.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    public static void loadProjectsFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                // Use a more robust CSV parsing approach
                List<String> data = parseCSVLine(line);
                if (data.size() >= 13) { // We need all the project details
                    String projectName = data.get(0).trim();
                    String neighborhood = data.get(1).trim();
                    
                    // Parse room types correctly
                    RoomType type1 = parseRoomType(data.get(2).trim());
                    int unitsType1 = Integer.parseInt(data.get(3).trim());
                    double priceType1 = Double.parseDouble(data.get(4).trim());
                    RoomType type2 = parseRoomType(data.get(5).trim());
                    int unitsType2 = Integer.parseInt(data.get(6).trim());
                    double priceType2 = Double.parseDouble(data.get(7).trim());
                    
                    LocalDate openingDate = LocalDate.parse(data.get(8).trim(), DATE_FORMATTER);
                    LocalDate closingDate = LocalDate.parse(data.get(9).trim(), DATE_FORMATTER);
                    String managerName = data.get(10).trim();
                    int officerSlots = Integer.parseInt(data.get(11).trim());
                    String officersStr = data.get(12).trim();
                    
                    // Debug output commented out
                    // System.out.println("Raw officer names from CSV: '" + officersStr + "'");
                    
                    // Parse officer names, handling quoted values
                    String[] officerNames;
                    if (officersStr.startsWith("\"") && officersStr.endsWith("\"")) {
                        // Remove the outer quotes and split by comma
                        officersStr = officersStr.substring(1, officersStr.length() - 1);
                    }
                    officerNames = officersStr.split(",");
                    
                    // Debug output commented out
                    // System.out.println("After processing: '" + officersStr + "'");
                    // System.out.println("Split into: " + officerNames.length + " names");
                    // for (int i = 0; i < officerNames.length; i++) {
                    //     System.out.println("Officer name " + i + ": '" + officerNames[i] + "'");
                    // }
                    
                    // Create the project
                    HDBManager manager = ManagerManager.getManagerByName(managerName);
                    BTOProject project = new BTOProject(projectName, neighborhood, type1, manager);
                    
                    // Set additional project details
                    project.setApplicationOpeningDate(openingDate.atStartOfDay());
                    project.setApplicationClosingDate(closingDate.atStartOfDay());
                    
                    // Add flat inventory
                    Map<RoomType, Integer> flatInventory = new HashMap<>();
                    flatInventory.put(type1, unitsType1);
                    flatInventory.put(type2, unitsType2);
                    project.setFlatInventory(flatInventory);
                    
                    // Add eligible groups based on room types
                    List<MaritalStatus> eligibleGroups = new ArrayList<>();
                    if (type1 == RoomType.TWO_ROOM) {
                        eligibleGroups.add(MaritalStatus.SINGLE);
                    }
                    eligibleGroups.add(MaritalStatus.MARRIED);
                    project.setEligibleGroups(eligibleGroups);
                    
                    // Assign manager to project
                    if (manager != null) {
                        manager.addManagedProject(project);
                    }
                    
                    // Assign officers to project
                    for (String officerName : officerNames) {
                        officerName = officerName.trim();
                        if (!officerName.isEmpty()) {
                            HDBOfficer officer = OfficerManager.getOfficerByName(officerName);
                            if (officer != null) {
                                boolean added = project.addOfficer(officer);
                                if (added) {
                                    officer.registerForProject(project);
                                    // Debug output commented out
                                    // System.out.println("Successfully assigned officer " + officerName + " to project " + projectName);
                                } else {
                                    // Debug output commented out
                                    // System.out.println("Failed to add officer " + officerName + " to project " + projectName + " (project might have reached maximum officers)");
                                }
                            } else {
                                // Debug output commented out
                                // System.out.println("Warning: Officer not found: " + officerName);
                            }
                        }
                    }
                    
                    // Store the project
                    projects.put(projectName, project);
                }
            }
            System.out.println("Successfully loaded projects from CSV file.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error parsing data: " + e.getMessage());
        }
    }
    
    // Helper method to parse CSV line with proper handling of quoted values
    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    // Handle escaped quotes (double quotes)
                    current.append('\"');
                    i++; // Skip the next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // End of field
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        // Add the last field
        result.add(current.toString());
        
        return result;
    }
    
    private static RoomType parseRoomType(String roomTypeStr) {
        try {
            // Remove any spaces and convert to uppercase
            String normalized = roomTypeStr.trim().replace(" ", "").toUpperCase();
            
            // Handle different formats
            switch (normalized) {
                case "2-ROOM":
                case "2ROOM":
                case "2_ROOM":
                    return RoomType.TWO_ROOM;
                case "3-ROOM":
                case "3ROOM":
                case "3_ROOM":
                    return RoomType.THREE_ROOM;
                case "4-ROOM":
                case "4ROOM":
                case "4_ROOM":
                    return RoomType.FOUR_ROOM;
                case "5-ROOM":
                case "5ROOM":
                case "5_ROOM":
                    return RoomType.FIVE_ROOM;
                default:
                    throw new IllegalArgumentException("Invalid room type: " + roomTypeStr);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing room type: " + roomTypeStr, e);
        }
    }

    public static BTOProject getProject(String projectName) {
        return projects.get(projectName);
    }

    public static List<BTOProject> getAllProjects() {
        return new ArrayList<>(projects.values());
    }
} 