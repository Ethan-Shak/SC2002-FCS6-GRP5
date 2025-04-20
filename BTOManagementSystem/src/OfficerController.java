import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class OfficerController {
    private static Map<String, HDBOfficer> officers = new HashMap<>(); // NRIC as key
    private static final String CSV_FILE = "OfficerList.csv";

    public static void loadOfficersFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) { // We need name, nric, age, maritalstatus, and password
                    String name = data[0].trim();
                    String nric = data[1].trim();
                    int age = Integer.parseInt(data[2].trim());
                    MaritalStatus maritalStatus = MaritalStatus.valueOf(data[3].trim().toUpperCase());
                    String password = data[4].trim();

                    HDBOfficer officer = new HDBOfficer(name, nric, age, maritalStatus, password);
                    officers.put(nric, officer);
                }
            }
            System.out.println("Successfully loaded officers from CSV file.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error parsing data: " + e.getMessage());
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
            writer.println("Name,NRIC,Age,MaritalStatus,Password");
            
            // Write data for each officer
            for (HDBOfficer officer : officers.values()) {
                writer.printf("%s,%s,%d,%s,%s%n",
                    officer.getName(),
                    officer.getNRIC(),
                    officer.getAge(),
                    officer.getMaritalStatus(),
                    officer.getSingpassAccount().getPassword());
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
} 