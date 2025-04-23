import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficerController {
    private static Map<String, HDBOfficer> officers = new HashMap<>(); // NRIC as key
    private static final String CSV_FILE = "OfficerList.csv";

    public static void loadOfficersFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 5) {
                    String name = data[0].trim();
                    String nric = data[1].trim();
                    int age = Integer.parseInt(data[2].trim());
                    MaritalStatus maritalStatus = MaritalStatus.valueOf(data[3].trim().toUpperCase());
                    String password = data[4].trim();
                    
                    HDBOfficer officer = new HDBOfficer(name, nric, age, maritalStatus, password);
                    System.out.println("Loading officer: " + name + " (NRIC: " + nric + ")");
                    
                    // Check if officer is in OfficerList.csv
                    if (isOfficerInList(nric)) {
                        officer.setLoadedFromCSV(true); // Set the flag for CSV-loaded officers
                        System.out.println("Officer " + name + " found in OfficerList.csv");
                        
                        // If project is specified in CSV, register for that project
                        if (data.length > 5 && !data[5].trim().isEmpty()) {
                            String projectName = data[5].trim();
                            System.out.println("Attempting to assign officer " + name + " to project: " + projectName);
                            BTOProject project = ProjectManager.getProjectByName(projectName);
                            if (project != null) {
                                // For CSV-loaded officers, directly add to project and set as approved
                                // Set the registration status to APPROVED first
                                officer.setRegistrationStatus(RegistrationStatus.APPROVED);
                                
                                // Add the officer to the project's officers list directly
                                project.getOfficers().add(officer);
                                
                                // Add the project to the officer's assigned projects
                                officer.getAssignedProjects().add(project);
                                
                                System.out.println("HDB Officer " + officer.getName() + " automatically approved for project: " + project.getProjectName());
                            } else {
                                System.out.println("Project not found: " + projectName);
                            }
                        } else {
                            System.out.println("No project specified for officer " + name);
                        }
                    } else {
                        System.out.println("Officer " + name + " not found in OfficerList.csv");
                    }
                    
                    officers.put(nric, officer);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading officers from CSV: " + e.getMessage());
        }
    }

    public static boolean authenticateOfficer(String nric, String password) {
        HDBOfficer officer = officers.get(nric);
        if (officer == null) {
            System.out.println("Login failed: Officer not found");
            return false;
        }

        SingpassAccount account = officer.getSingpassAccount();
        if (account.authenticate(password)) {
            System.out.println("Login successful");
            return true;
        }

        System.out.println("Login failed: Wrong password");
        return false;
    }

    public static HDBOfficer getOfficer(String nric) {
        return officers.get(nric);
    }

    public static HDBOfficer getOfficerByName(String name) {
        // Debug output commented out
        // System.out.println("Looking for officer with name: '" + name + "'");
        String normalizedName = name.trim();
        for (HDBOfficer officer : officers.values()) {
            String officerName = officer.getName().trim();
            // Debug output commented out
            // System.out.println("Checking officer: '" + officerName + "'");
            if (officerName.equalsIgnoreCase(normalizedName)) {
                // Debug output commented out
                // System.out.println("Found officer: " + officer.getName());
                return officer;
            }
        }
        // Debug output commented out
        // System.out.println("Officer not found with name: '" + name + "'");
        return null;
    }

    public static void saveOfficersToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Write header
            writer.println("Name,NRIC,Age,MaritalStatus,Password,Projects");
            
            // Write data for each officer
            for (HDBOfficer officer : officers.values()) {
                List<String> projectNames = new ArrayList<>();
                for (BTOProject project : officer.getAssignedProjects()) {
                    projectNames.add(project.getProjectName());
                }
                String projectsStr = String.join(";", projectNames);
                
                writer.printf("%s,%s,%d,%s,%s,%s%n",
                    officer.getName(),
                    officer.getNRIC(),
                    officer.getAge(),
                    officer.getMaritalStatus(),
                    officer.getSingpassAccount().getPassword(),
                    projectsStr);
            }
            System.out.println("Successfully saved officers to CSV file.");
        } catch (IOException e) {
            System.out.println("Error saving to CSV file: " + e.getMessage());
        }
    }

    public static void updateOfficerPassword(String nric, String newPassword) {
        HDBOfficer officer = officers.get(nric);
        if (officer != null) {
            SingpassAccount account = officer.getSingpassAccount();
            if (account.changePassword(account.getPassword(), newPassword)) {
                saveOfficersToCSV();
            }
        }
    }

    public static List<HDBOfficer> getAllOfficers() {
        return new ArrayList<>(officers.values());
    }

    private static boolean isOfficerInList(String nric) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 2 && data[1].trim().equals(nric)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading OfficerList.csv: " + e.getMessage());
        }
        return false;
    }
} 