import java.util.Scanner;

public class App {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        // Load data at startup
        ApplicantManager.loadApplicantsFromCSV("ApplicantList.csv");
        ManagerManager.loadManagersFromCSV("ManagerList.csv");
        OfficerManager.loadOfficersFromCSV("OfficerList.csv");
        
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                displayMainMenu();
                int choice = getUserChoice();
                
                switch (choice) {
                    case 1:
                        handleLogin();
                        break;
                    case 2:
                        running = false;
                        System.out.println("Thank you for using the BTO Management System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                displayLoggedInMenu();
                int choice = getUserChoice();
                
                switch (choice) {
                    case 1:
                        handleChangePassword();
                        break;
                    case 2:
                        currentUser = null;
                        System.out.println("Logged out successfully.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
        
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n=== BTO Management System ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void displayLoggedInMenu() {
        System.out.println("\n=== Welcome, " + currentUser.getName() + " ===");
        System.out.println("1. Change Password");
        System.out.println("2. Logout");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Invalid choice
        }
    }

    private static void handleLogin() {
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine();
        
        // Validate NRIC format
        if (!nric.matches("^[ST]\\d{7}[A-Z]$")) {
            System.out.println("Invalid NRIC format! It must start with S or T, followed by 7 digits and an uppercase letter.");
            return;
        }
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Try to authenticate as an applicant
        if (ApplicantManager.authenticateApplicant(nric, password)) {
            currentUser = ApplicantManager.getApplicant(nric);
            System.out.println("Welcome, " + currentUser.getName() + "!");
            return;
        }
        
        // Try to authenticate as a manager
        if (ManagerManager.authenticateManager(nric, password)) {
            currentUser = ManagerManager.getManager(nric);
            System.out.println("Welcome, " + currentUser.getName() + "!");
            return;
        }
        
        // Try to authenticate as an officer
        if (OfficerManager.authenticateOfficer(nric, password)) {
            currentUser = OfficerManager.getOfficer(nric);
            System.out.println("Welcome, " + currentUser.getName() + "!");
            return;
        }
        
        System.out.println("Login failed: Invalid credentials");
    }

    private static void handleChangePassword() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match!");
            return;
        }
        
        SingpassAccount account = currentUser.getSingpassAccount();
        if (account.changePassword(currentPassword, newPassword)) {
            // Update password in the appropriate manager based on user type
            String nric = currentUser.getNRIC();
            if (currentUser instanceof HDBOfficer) {
                // Check for HDBOfficer first since it extends Applicant
                OfficerManager.updateOfficerPassword(nric, newPassword);
            } else if (currentUser instanceof HDBManager) {
                ManagerManager.updateManagerPassword(nric, newPassword);
            } else if (currentUser instanceof Applicant) {
                ApplicantManager.updateApplicantPassword(nric, newPassword);
            }
            
            System.out.println("Password changed successfully. Please login again.");
            currentUser = null;
        } else {
            System.out.println("Current password is incorrect!");
        }
    }
}
