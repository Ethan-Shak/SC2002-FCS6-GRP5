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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void loadProjectsFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 13) { // We need all the project details
                    String projectName = data[0].trim();
                    String neighborhood = data[1].trim();
                    RoomType type1 = RoomType.valueOf(data[2].trim().replace("-", "_").toUpperCase());
                    int unitsType1 = Integer.parseInt(data[3].trim());
                    double priceType1 = Double.parseDouble(data[4].trim());
                    RoomType type2 = RoomType.valueOf(data[5].trim().replace("-", "_").toUpperCase());
                    int unitsType2 = Integer.parseInt(data[6].trim());
                    double priceType2 = Double.parseDouble(data[7].trim());
                    LocalDate openingDate = LocalDate.parse(data[8].trim(), DATE_FORMATTER);
                    LocalDate closingDate = LocalDate.parse(data[9].trim(), DATE_FORMATTER);
                    String managerName = data[10].trim();
                    int officerSlots = Integer.parseInt(data[11].trim());
                    String[] officerNames = data[12].trim().split(",");
                    
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
                                project.addOfficer(officer);
                                officer.registerForProject(project);
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

    public static BTOProject getProject(String projectName) {
        return projects.get(projectName);
    }

    public static List<BTOProject> getAllProjects() {
        return new ArrayList<>(projects.values());
    }
} 