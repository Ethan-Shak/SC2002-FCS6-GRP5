import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ApplicantManager {
    private static Map<String, Applicant> applicants = new HashMap<>(); // NRIC as key
    private static final String CSV_FILE = "ApplicantList.csv";

    public static void loadApplicantsFromCSV(String filePath) {
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

                    Applicant applicant = new Applicant(name, nric, age, maritalStatus, password);
                    applicants.put(nric, applicant);
                }
            }
            System.out.println("Successfully loaded applicants from CSV file.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error parsing data: " + e.getMessage());
        }
    }

    public static boolean authenticateApplicant(String nric, String password) {
        Applicant applicant = applicants.get(nric);
        if (applicant == null) {
            System.out.println("Login failed: User not found");
            return false;
        }

        SingpassAccount account = applicant.getSingpassAccount();
        if (account.authenticate(password)) {
            System.out.println("Login successful");
            return true;
        }

        System.out.println("Login failed: Wrong password");
        return false;
    }

    public static Applicant getApplicant(String nric) {
        return applicants.get(nric);
    }

    public static void saveApplicantsToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Write header
            writer.println("Name,NRIC,Age,MaritalStatus,Password");
            
            // Write data for each applicant
            for (Applicant applicant : applicants.values()) {
                writer.printf("%s,%s,%d,%s,%s%n",
                    applicant.getName(),
                    applicant.getNRIC(),
                    applicant.getAge(),
                    applicant.getMaritalStatus(),
                    applicant.getSingpassAccount().getPassword());
            }
            System.out.println("Successfully saved applicants to CSV file.");
        } catch (IOException e) {
            System.out.println("Error saving to CSV file: " + e.getMessage());
        }
    }

    public static void updateApplicantPassword(String nric, String newPassword) {
        Applicant applicant = applicants.get(nric);
        if (applicant != null) {
            SingpassAccount account = applicant.getSingpassAccount();
            if (account.changePassword(account.getPassword(), newPassword)) {
                saveApplicantsToCSV();
            }
        }
    }
} 