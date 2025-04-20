import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ManagerController {
    private static Map<String, HDBManager> managers = new HashMap<>(); // NRIC as key
    private static final String CSV_FILE = "ManagerList.csv";

    public static void loadManagersFromCSV(String filePath) {
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

                    HDBManager manager = new HDBManager(name, nric, age, maritalStatus, password);
                    managers.put(nric, manager);
                }
            }
            System.out.println("Successfully loaded managers from CSV file.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error parsing data: " + e.getMessage());
        }
    }

    public static boolean authenticateManager(String nric, String password) {
        HDBManager manager = managers.get(nric);
        if (manager == null) {
            System.out.println("Login failed: Manager not found");
            return false;
        }

        SingpassAccount account = manager.getSingpassAccount();
        if (account.authenticate(password)) {
            System.out.println("Login successful");
            return true;
        }

        System.out.println("Login failed: Wrong password");
        return false;
    }

    public static HDBManager getManager(String nric) {
        return managers.get(nric);
    }

    public static HDBManager getManagerByName(String name) {
        for (HDBManager manager : managers.values()) {
            if (manager.getName().equals(name)) {
                return manager;
            }
        }
        return null;
    }

    public static void saveManagersToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Write header
            writer.println("Name,NRIC,Age,MaritalStatus,Password");
            
            // Write data for each manager
            for (HDBManager manager : managers.values()) {
                writer.printf("%s,%s,%d,%s,%s%n",
                    manager.getName(),
                    manager.getNRIC(),
                    manager.getAge(),
                    manager.getMaritalStatus(),
                    manager.getSingpassAccount().getPassword());
            }
            System.out.println("Successfully saved managers to CSV file.");
        } catch (IOException e) {
            System.out.println("Error saving to CSV file: " + e.getMessage());
        }
    }

    public static void updateManagerPassword(String nric, String newPassword) {
        HDBManager manager = managers.get(nric);
        if (manager != null) {
            SingpassAccount account = manager.getSingpassAccount();
            if (account.changePassword(account.getPassword(), newPassword)) {
                saveManagersToCSV();
            }
        }
    }
} 