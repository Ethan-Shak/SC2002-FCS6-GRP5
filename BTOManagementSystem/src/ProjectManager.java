import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProjectManager {
    private static Map<String, BTOProject> projects = new HashMap<>();
    private static final String CSV_FILE = "ProjectList.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    // Load projects from CSV file
    public static void loadProjectsFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                List<String> data = parseCSVLine(line);
                if (data.size() >= 11) {  // Changed to 11 columns (removed selling prices)
                    String projectName = data.get(0).trim();
                    String neighbourhood = data.get(1).trim();

                    RoomType type1 = parseRoomType(data.get(2).trim());
                    int unitsType1 = Integer.parseInt(data.get(3).trim());
                    RoomType type2 = parseRoomType(data.get(4).trim());
                    int unitsType2 = Integer.parseInt(data.get(5).trim());

                    LocalDate openingDate = LocalDate.parse(data.get(6).trim(), DATE_FORMATTER);
                    LocalDate closingDate = LocalDate.parse(data.get(7).trim(), DATE_FORMATTER);
                    String managerName = data.get(8).trim();
                    int officerSlots = Integer.parseInt(data.get(9).trim());
                    String officersStr = data.get(10).trim();

                    String[] officerNames = officersStr.split(",");

                    HDBManager manager = ManagerController.getManagerByName(managerName);
                    BTOProject project = new BTOProject(projectName, neighbourhood, type1, manager, openingDate, closingDate);

                    Map<RoomType, Integer> flatInventory = new HashMap<>();
                    flatInventory.put(type1, unitsType1);
                    flatInventory.put(type2, unitsType2);
                    project.setFlatInventory(flatInventory);

                    List<MaritalStatus> eligibleGroups = new ArrayList<>();
                    if (type1 == RoomType.TWO_ROOM) {
                        eligibleGroups.add(MaritalStatus.SINGLE);
                    }
                    eligibleGroups.add(MaritalStatus.MARRIED);
                    project.setEligibleGroups(eligibleGroups);

                    if (manager != null) {
                        manager.addManagedProject(project);
                    }

                    for (String officerName : officerNames) {
                        officerName = officerName.trim();
                        if (!officerName.isEmpty()) {
                            HDBOfficer officer = OfficerController.getOfficerByName(officerName);
                            if (officer != null) {
                                project.addOfficer(officer);
                                officer.registerForProject(project);
                            }
                        }
                    }

                    projects.put(projectName, project);
                }
            }
            System.out.println("Successfully loaded projects from CSV file.");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();  // Added to see the full error stack trace
        }
    }

    // Save all projects into CSV (Overwrites existing file)
    public static void saveProjectsToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            writer.println("Project Name,Neighborhood,Type 1,Number of units for Type 1,Type 2,Number of units for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer");

            for (BTOProject project : projects.values()) {
                String officersStr = String.join(",", project.getAssignedOfficerNames());

                writer.printf("%s,%s,%s,%d,%s,%d,%s,%s,%s,%d,\"%s\"\n",
                        project.getProjectName(), project.getNeighbourhood(), 
                        project.getRoomType().toString().replace("_", "-"),
                        project.getFlatInventory().getOrDefault(RoomType.TWO_ROOM, 0),
                        RoomType.THREE_ROOM.toString().replace("_", "-"),
                        project.getFlatInventory().getOrDefault(RoomType.THREE_ROOM, 0),
                        project.getApplicationOpeningDate().format(DATE_FORMATTER),
                        project.getApplicationClosingDate().format(DATE_FORMATTER),
                        project.getManager().getName(), project.getNumberOfOfficers(), officersStr);
            }
        } catch (IOException e) {
            System.out.println("Error saving projects to CSV: " + e.getMessage());
        }
    }

    // View all projects
    public static List<BTOProject> getAllProjects() {
        return new ArrayList<>(projects.values());
    }

    // Filter projects by manager
    public static List<BTOProject> getProjectsByManager(HDBManager manager) {
        List<BTOProject> filteredProjects = new ArrayList<>();
        for (BTOProject project : projects.values()) {
            if (project.getManager().equals(manager)) {
                filteredProjects.add(project);
            }
        }
        return filteredProjects;
    }

    // Add a new project
    public static boolean createProject(String projectName, String neighborhood, RoomType roomType,
                                        HDBManager manager, LocalDate openingDate, LocalDate closingDate) {
        if (projects.containsKey(projectName)) {
            System.out.println("Error: A project with this name already exists.");
            return false;
        }

        // Check for conflicting projects using the comprehensive check
        if (managerHasConflictingProject(manager, openingDate, closingDate)) {
            System.out.println("Error: You already have a project with an overlapping application period.");
            System.out.println("You cannot manage multiple projects with overlapping application periods.");
            return false;
        }

        BTOProject newProject = new BTOProject(projectName, neighborhood, roomType, manager, openingDate, closingDate);

        projects.put(projectName, newProject);
        manager.addManagedProject(newProject);
        saveProjectsToCSV();
        System.out.println("Project successfully created: " + projectName);
        return true;
    }

    // Edit an existing project
    public static boolean editProject(String projectName, String newNeighborhood, RoomType newRoomType,
                                  LocalDate newOpeningDate, LocalDate newClosingDate) {
    BTOProject project = projects.get(projectName);
    if (project == null) {
        System.out.println("Error: Project not found.");
        return false;
    }

    if (newNeighborhood != null) project.setNeighbourhood(newNeighborhood);
    if (newRoomType != null) project.setRoomType(newRoomType);
    if (newOpeningDate != null) project.setApplicationOpeningDate(newOpeningDate);
    if (newClosingDate != null) project.setApplicationClosingDate(newClosingDate);

    saveProjectsToCSV();
    System.out.println("Project successfully updated: " + projectName);
    return true;
    }

    // Delete a project
    public static boolean deleteProject(String projectName) {
        BTOProject project = projects.get(projectName);
        if (project == null) {
            System.out.println("Error: Project not found.");
            return false;
        }

        projects.remove(projectName);
        project.getManager().removeManagedProject(project);
        saveProjectsToCSV();
        System.out.println("Project successfully deleted: " + projectName);
        return true;
    }

    // Helper method: prevent managers from handling overlapping projects
    private static boolean managerHasConflictingProject(HDBManager manager, LocalDate newOpeningDate, LocalDate newClosingDate) {
        for (BTOProject project : manager.getManagedProjects()) {
            LocalDate existingOpening = project.getApplicationOpeningDate();
            LocalDate existingClosing = project.getApplicationClosingDate();
            if (!(newClosingDate.isBefore(existingOpening) || newOpeningDate.isAfter(existingClosing))) {
                return true;
            }
        }
        return false;
    }

    // Parse a CSV line, handling quoted values properly
    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    current.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());
        return result;
    }

    // Get a project by its name
    public static BTOProject getProjectByName(String projectName) {
        return projects.get(projectName);
    }

    private static RoomType parseRoomType(String roomTypeStr) {
        try {
            String normalized = roomTypeStr.trim().toUpperCase().replace("-", "_");
    
            switch (normalized) {
                case "2_ROOM":
                case "TWO_ROOM":
                    return RoomType.TWO_ROOM;
                case "3_ROOM":
                case "THREE_ROOM":
                    return RoomType.THREE_ROOM;
                case "4_ROOM":
                case "FOUR_ROOM":
                    return RoomType.FOUR_ROOM;
                case "5_ROOM":
                case "FIVE_ROOM":
                    return RoomType.FIVE_ROOM;
                default:
                    throw new IllegalArgumentException("Invalid room type: " + roomTypeStr);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing room type: " + roomTypeStr, e);
        }
    }
}