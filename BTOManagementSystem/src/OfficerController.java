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
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5) {
                    String name = values[0].trim();
                    String nric = values[1].trim();
                    int age = Integer.parseInt(values[2].trim());
                    MaritalStatus maritalStatus = MaritalStatus.valueOf(values[3].trim().toUpperCase());
                    String password = values[4].trim();
                    String projectName = values.length > 5 ? values[5].trim() : null;

                    HDBOfficer officer = new HDBOfficer(name, nric, age, maritalStatus, password);
                    officer.setLoadedFromCSV(true);
                    
                    if (projectName != null && !projectName.isEmpty()) {
                        BTOProject project = ProjectManager.getProjectByName(projectName);
                        if (project != null) {
                            // Set the pending project and status as if they went through normal registration
                            officer.registerForProject(project);
                            officer.approveRegistration();
                            System.out.println("Loaded officer " + name + " and assigned to project " + projectName);
                        } else {
                            System.out.println("Warning: Project " + projectName + " not found for officer " + name);
                        }
                    }
                    
                    officers.put(nric, officer);
                }
            }
            System.out.println("Officers loaded successfully from " + filename);
        } catch (IOException e) {
            System.out.println("Error loading officers from CSV: " + e.getMessage());
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