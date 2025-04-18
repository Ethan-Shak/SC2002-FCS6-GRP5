import java.util.Scanner;

public class App {
    private static Scanner scanner = new Scanner(System.in);
    private static Applicant currentUser = null;

    public static void main(String[] args) {
        // Load applicants data at startup
        ApplicantManager.loadApplicantsFromCSV("ApplicantList.csv");
        
        boolean running = true;
        while (running) {
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
        }
        
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n=== BTO Management System ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
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
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (ApplicantManager.authenticateApplicant(nric, password)) {
            currentUser = ApplicantManager.getApplicant(nric);
            System.out.println("Welcome, " + currentUser.getName() + "!");
            // Here you can add a call to a method that handles the logged-in user menu
            // For example: handleLoggedInMenu();
        }
    }
}
