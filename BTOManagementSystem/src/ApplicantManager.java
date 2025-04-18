import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApplicantManager {
    private static Map<String, Applicant> applicants = new HashMap<>(); // NRIC as key

    public static void loadApplicantsFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) { // We only need name, nric, age, maritalstatus (password is handled by SingpassAccount)
                    String name = data[0].trim();
                    String nric = data[1].trim();
                    int age = Integer.parseInt(data[2].trim());
                    MaritalStatus maritalStatus = MaritalStatus.valueOf(data[3].trim().toUpperCase());

                    Applicant applicant = new Applicant(name, nric, age, maritalStatus);
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

        System.out.println("Login failed: Invalid credentials");
        return false;
    }

    public static Applicant getApplicant(String nric) {
        return applicants.get(nric);
    }
} 