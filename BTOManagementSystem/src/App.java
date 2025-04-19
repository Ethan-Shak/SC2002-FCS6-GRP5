import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class App {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        // Load data at startup
        ApplicantManager.loadApplicantsFromCSV("ApplicantList.csv");
        ManagerManager.loadManagersFromCSV("ManagerList.csv");
        OfficerManager.loadOfficersFromCSV("OfficerList.csv");
        ProjectManager.loadProjectsFromCSV("ProjectList.csv");
        
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
                
                handleLoggedInChoice(choice);
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
        System.out.println("1. View Projects");
        if (currentUser instanceof HDBManager) {
            System.out.println("2. Toggle Project Visibility");
        }
        if (currentUser instanceof Applicant) {
            System.out.println("2. Apply for Project");
            System.out.println("3. Withdraw Application");
        }
        System.out.println((currentUser instanceof Applicant ? "4" : "3") + ". Change Password");
        System.out.println((currentUser instanceof Applicant ? "5" : "4") + ". Logout");
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

    private static void displayProjects() {
        List<BTOProject> projects = ProjectManager.getAllProjects();
        
        if (projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }
        
        System.out.println("\n=== Available Projects ===");
        
        if (currentUser instanceof HDBManager) {
            // Managers can see all projects regardless of visibility
            for (BTOProject project : projects) {
                displayProjectDetails(project, true);
            }
        } else if (currentUser instanceof Applicant) {
            // Applicants can only see projects visible to their user group
            for (BTOProject project : projects) {
                if (project.isVisible((Applicant)currentUser)) {
                    displayProjectDetails(project, false);
                }
            }
        } else if (currentUser instanceof HDBOfficer) {
            // Officers can see all projects they are assigned to
            for (BTOProject project : projects) {
                if (project.getOfficers().contains(currentUser)) {
                    displayProjectDetails(project, false);
                }
            }
        }
    }
    
    private static void displayProjectDetails(BTOProject project, boolean showVisibility) {
        System.out.println("\nProject: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighbourhood());
        System.out.println("Room Types: " + project.getRoomType());
        
        if (showVisibility) {
            System.out.println("Visibility: " + (project.getVisibility() ? "On" : "Off"));
        }
        
        System.out.println("Application Period: " + 
            project.getApplicationOpeningDate().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
            " to " + 
            project.getApplicationClosingDate().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        System.out.println("Manager: " + (project.getManager() != null ? project.getManager().getName() : "Not assigned"));
        System.out.println("Officers: " + project.getNumberOfOfficers());
    }
    
    private static void toggleProjectVisibility() {
        List<BTOProject> projects = ProjectManager.getAllProjects();
        
        if (projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }
        
        System.out.println("\n=== Projects ===");
        for (int i = 0; i < projects.size(); i++) {
            BTOProject project = projects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() + 
                " (Visibility: " + (project.getVisibility() ? "On" : "Off") + ")");
        }
        
        System.out.print("Enter project number to toggle visibility: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= projects.size()) {
                BTOProject project = projects.get(choice - 1);
                project.setVisibility(!project.getVisibility());
                System.out.println("Project visibility toggled to: " + (project.getVisibility() ? "On" : "Off"));
            } else {
                System.out.println("Invalid project number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static void handleLoggedInChoice(int choice) {
        if (currentUser instanceof Applicant) {
            switch (choice) {
                case 1:
                    displayProjects();
                    break;
                case 2:
                    applyForProject();
                    break;
                case 3:
                    withdrawApplication();
                    break;
                case 4:
                    handleChangePassword();
                    break;
                case 5:
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } else if (currentUser instanceof HDBManager) {
            switch (choice) {
                case 1:
                    displayProjects();
                    break;
                case 2:
                    toggleProjectVisibility();
                    break;
                case 3:
                    handleChangePassword();
                    break;
                case 4:
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } else if (currentUser instanceof HDBOfficer) {
            switch (choice) {
                case 1:
                    displayProjects();
                    break;
                case 2:
                    handleChangePassword();
                    break;
                case 3:
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void applyForProject() {
        if (!(currentUser instanceof Applicant)) {
            System.out.println("This option is only available for applicants.");
            return;
        }
        
        Applicant applicant = (Applicant) currentUser;
        
        // Check if applicant already has an application
        if (applicant.getApplication() != null) {
            System.out.println("You have already applied for a project.");
            return;
        }
        
        // Display available projects
        List<BTOProject> projects = ProjectManager.getAllProjects();
        List<BTOProject> eligibleProjects = new ArrayList<>();
        
        System.out.println("\n=== Available Projects ===");
        int index = 1;
        for (BTOProject project : projects) {
            if (project.isVisible(applicant)) {
                System.out.println(index + ". " + project.getProjectName() + 
                    " (" + project.getNeighbourhood() + ")");
                eligibleProjects.add(project);
                index++;
            }
        }
        
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects available for you.");
            return;
        }
        
        System.out.print("Enter project number to apply: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= eligibleProjects.size()) {
                BTOProject selectedProject = eligibleProjects.get(choice - 1);
                
                // Apply for the project
                ApplicationApprovalManager approvalManager = new ApplicationApprovalManager();
                if (approvalManager.applyForProject(applicant, selectedProject)) {
                    System.out.println("Application submitted successfully.");
                }
            } else {
                System.out.println("Invalid project number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void withdrawApplication() {
        if (!(currentUser instanceof Applicant)) {
            System.out.println("This option is only available for applicants.");
            return;
        }
        
        Applicant applicant = (Applicant) currentUser;
        
        // Check if applicant has an application
        BTOApplication application = applicant.getApplication();
        if (application == null) {
            System.out.println("You do not have an active application.");
            return;
        }
        
        BTOProject project = application.getProject();
        System.out.println("You have an application for " + project.getProjectName() + ".");
        System.out.print("Do you want to withdraw your application? (y/n): ");
        
        String response = scanner.nextLine().toLowerCase();
        if (response.equals("y") || response.equals("yes")) {
            ApplicationApprovalManager approvalManager = new ApplicationApprovalManager();
            if (approvalManager.withdrawApplication(applicant, project)) {
                System.out.println("Withdrawal request submitted successfully.");
            }
        }
    }
}
