import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        // Load data at startup
        ApplicantController.loadApplicantsFromCSV("ApplicantList.csv");
        ManagerController.loadManagersFromCSV("ManagerList.csv");
        OfficerController.loadOfficersFromCSV("OfficerList.csv");
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
            System.out.println("3. Manage Applications");
            System.out.println("4. Change Password");
            System.out.println("5. Logout");
        } else if (currentUser instanceof HDBOfficer) {
            // HDB Officer menu (includes both applicant and officer options)
            System.out.println("2. Apply for Project");
            System.out.println("3. Withdraw Application");
            System.out.println("4. View Application Status");
            System.out.println("5. Book Flat for Applicant");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
        } else if (currentUser instanceof Applicant) {
            // Regular Applicant menu
            System.out.println("2. Apply for Project");
            System.out.println("3. Withdraw Application");
            System.out.println("4. View Application Status");
            System.out.println("5. Change Password");
            System.out.println("6. Logout");
        }
        
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

        // Try to authenticate without showing intermediate error messages
        boolean loginSuccess = false;
        boolean userExists = false;
        
        // Check if user exists in any of the managers
        if (OfficerController.getOfficer(nric) != null) {
            userExists = true;
            if (OfficerController.authenticateOfficer(nric, password)) {
                currentUser = OfficerController.getOfficer(nric);
                loginSuccess = true;
            }
        } else if (ApplicantController.getApplicant(nric) != null) {
            userExists = true;
            if (ApplicantController.authenticateApplicant(nric, password)) {
                currentUser = ApplicantController.getApplicant(nric);
                loginSuccess = true;
            }
        } else if (ManagerController.getManager(nric) != null) {
            userExists = true;
            if (ManagerController.authenticateManager(nric, password)) {
                currentUser = ManagerController.getManager(nric);
                loginSuccess = true;
            }
        }
        
        if (loginSuccess) {
            System.out.println("Welcome, " + currentUser.getName() + "!");
        } else if (userExists) {
            System.out.println("Login failed: Invalid credentials");
        } else {
            System.out.println("Login failed: Not a valid user");
        }
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
                OfficerController.updateOfficerPassword(nric, newPassword);
            } else if (currentUser instanceof HDBManager) {
                ManagerController.updateManagerPassword(nric, newPassword);
            } else if (currentUser instanceof Applicant) {
                ApplicantController.updateApplicantPassword(nric, newPassword);
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
                if (project.checkVisibility((Applicant)currentUser)) {
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
        
        // Display all room types and their availability
        System.out.println("Room Types:");
        Map<RoomType, Integer> flatInventory = project.getFlatInventory();
        for (Map.Entry<RoomType, Integer> entry : flatInventory.entrySet()) {
            RoomType roomType = entry.getKey();
            int units = entry.getValue();
            System.out.println("  - " + roomType + " (" + units + " units available)");
        }
        
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
        if (currentUser instanceof HDBManager) {
            switch (choice) {
                case 1:
                    displayProjects();
                    break;
                case 2:
                    toggleProjectVisibility();
                    break;
                case 3:
                    manageApplications();
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
        } else if (currentUser instanceof HDBOfficer) {
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
                    viewApplicationStatus();
                    break;
                case 5:
                    bookFlatForApplicant();
                    break;
                case 6:
                    handleChangePassword();
                    break;
                case 7:
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } else if (currentUser instanceof Applicant) {
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
                    viewApplicationStatus();
                    break;
                case 5:
                    handleChangePassword();
                    break;
                case 6:
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
            if (project.checkVisibility(applicant)) {
                System.out.println(index + ". " + project.getProjectName() + 
                    " (" + project.getNeighbourhood() + ")");
                
                // Display all eligible room types for this project
                System.out.println("   Available Room Types:");
                Map<RoomType, Integer> flatInventory = project.getFlatInventory();
                for (Map.Entry<RoomType, Integer> entry : flatInventory.entrySet()) {
                    RoomType roomType = entry.getKey();
                    int units = entry.getValue();
                    
                    // Check if applicant is eligible for this room type
                    boolean isEligible = false;
                    if (applicant.getMaritalStatus() == MaritalStatus.SINGLE) {
                        isEligible = applicant.getAge() >= 35 && roomType == RoomType.TWO_ROOM;
                    } else if (applicant.getMaritalStatus() == MaritalStatus.MARRIED) {
                        isEligible = applicant.getAge() >= 21;
                    }
                    
                    if (isEligible && units > 0) {
                        System.out.println("      - " + roomType + " (" + units + " units available)");
                    }
                }
                
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
            int projectChoice = Integer.parseInt(scanner.nextLine());
            if (projectChoice >= 1 && projectChoice <= eligibleProjects.size()) {
                BTOProject selectedProject = eligibleProjects.get(projectChoice - 1);
                
                // Get available room types for the selected project
                Map<RoomType, Integer> flatInventory = selectedProject.getFlatInventory();
                List<RoomType> availableRoomTypes = new ArrayList<>();
                
                System.out.println("\nAvailable Room Types for " + selectedProject.getProjectName() + ":");
                int roomTypeIndex = 1;
                for (Map.Entry<RoomType, Integer> entry : flatInventory.entrySet()) {
                    RoomType roomType = entry.getKey();
                    int units = entry.getValue();
                    
                    // Check if applicant is eligible for this room type
                    boolean isEligible = false;
                    if (applicant.getMaritalStatus() == MaritalStatus.SINGLE) {
                        isEligible = applicant.getAge() >= 35 && roomType == RoomType.TWO_ROOM;
                    } else if (applicant.getMaritalStatus() == MaritalStatus.MARRIED) {
                        isEligible = applicant.getAge() >= 21;
                    }
                    
                    if (isEligible && units > 0) {
                        System.out.println(roomTypeIndex + ". " + roomType + " (" + units + " units available)");
                        availableRoomTypes.add(roomType);
                        roomTypeIndex++;
                    }
                }
                
                if (availableRoomTypes.isEmpty()) {
                    System.out.println("No eligible room types available in this project.");
                    return;
                }
                
                System.out.print("Enter room type number to apply: ");
                int roomTypeChoice = Integer.parseInt(scanner.nextLine());
                if (roomTypeChoice >= 1 && roomTypeChoice <= availableRoomTypes.size()) {
                    RoomType selectedRoomType = availableRoomTypes.get(roomTypeChoice - 1);
                    
                    // Apply for the project with the selected room type
                    ApplicationApprovalManager approvalManager = new ApplicationApprovalManager();
                    if (approvalManager.applyForProject(applicant, selectedProject, selectedRoomType)) {
                        System.out.println("Application submitted successfully for " + selectedProject.getProjectName() + 
                            " with room type " + selectedRoomType);
                    }
                } else {
                    System.out.println("Invalid room type number.");
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
            // Immediately remove the application
            ApplicationApprovalManager approvalManager = new ApplicationApprovalManager();
            if (approvalManager.approveWithdrawal(applicant)) {
                System.out.println("Application withdrawn successfully. You can now apply for another project.");
            }
        }
    }

    private static void viewApplicationStatus() {
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
        ApplicationStatus status = application.getApplicationStatus();
        
        System.out.println("\n=== Application Status ===");
        System.out.println("Project: " + project.getProjectName());
        System.out.println("Neighbourhood: " + project.getNeighbourhood());
        System.out.println("Room Type: " + application.getRoomType());
        
        // Display application status with description
        System.out.println("Application Status: " + status);
        switch (status) {
            case PENDING:
                System.out.println("  - No conclusive decision made about the outcome of the application");
                break;
            case SUCCESSFUL:
                System.out.println("  - Outcome of the application is successful, you are invited to make a flat booking with the HDB Officer");
                break;
            case UNSUCCESSFUL:
                System.out.println("  - Outcome of the application is unsuccessful, you cannot make a flat booking for this application");
                System.out.println("  - You may apply for another project");
                break;
            case BOOKED:
                System.out.println("  - You have secured a unit after a successful application and completed a flat booking with the HDB Officer");
                break;
        }
    }

    private static void manageApplications() {
        if (!(currentUser instanceof HDBManager)) {
            System.out.println("This option is only available for HDB managers.");
            return;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        Map<String, BTOApplication> allApplications = ApplicationApprovalManager.getAllApplications();
        
        if (allApplications.isEmpty()) {
            System.out.println("No applications to manage.");
            return;
        }
        
        System.out.println("\n=== Manage Applications ===");
        System.out.println("1. View Pending Applications");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    handlePendingApplications(manager, allApplications);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void handlePendingApplications(HDBManager manager, Map<String, BTOApplication> allApplications) {
        List<BTOApplication> pendingApplications = new ArrayList<>();
        
        // Filter applications for projects managed by this manager
        for (BTOApplication application : allApplications.values()) {
            if (application.getApplicationStatus() == ApplicationStatus.PENDING &&
                manager.getManagedProjects().contains(application.getProject())) {
                pendingApplications.add(application);
            }
        }
        
        if (pendingApplications.isEmpty()) {
            System.out.println("No pending applications to review.");
            return;
        }
        
        System.out.println("\n=== Pending Applications ===");
        for (int i = 0; i < pendingApplications.size(); i++) {
            BTOApplication application = pendingApplications.get(i);
            BTOProject project = application.getProject();
            Applicant applicant = application.getApplicant();
            
            System.out.println((i + 1) + ". Project: " + project.getProjectName());
            System.out.println("   Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNRIC() + ")");
            System.out.println("   Room Type: " + application.getRoomType());
            System.out.println("   Available Units: " + project.getFlatInventory().get(application.getRoomType()));
        }
        
        System.out.print("\nEnter application number to review (0 to go back): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                return;
            }
            if (choice >= 1 && choice <= pendingApplications.size()) {
                BTOApplication application = pendingApplications.get(choice - 1);
                reviewApplication(application);
            } else {
                System.out.println("Invalid application number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void reviewApplication(BTOApplication application) {
        BTOProject project = application.getProject();
        Applicant applicant = application.getApplicant();
        
        System.out.println("\n=== Review Application ===");
        System.out.println("Project: " + project.getProjectName());
        System.out.println("Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNRIC() + ")");
        System.out.println("Room Type: " + application.getRoomType());
        System.out.println("Available Units: " + project.getFlatInventory().get(application.getRoomType()));
        
        System.out.println("\n1. Approve");
        System.out.println("2. Reject");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    // Check if there are available units
                    int availableUnits = project.getFlatInventory().get(application.getRoomType());
                    if (availableUnits > 0) {
                        application.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
                        // Decrease available units
                        project.getFlatInventory().put(application.getRoomType(), availableUnits - 1);
                        System.out.println("Application approved successfully.");
                    } else {
                        System.out.println("Cannot approve application: No available units.");
                    }
                    break;
                case 2:
                    application.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
                    System.out.println("Application rejected.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static void bookFlatForApplicant() {
        if (!(currentUser instanceof HDBOfficer)) {
            System.out.println("This option is only available for HDB officers.");
            return;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        
        // Get applicant NRIC
        System.out.print("Enter applicant NRIC: ");
        String applicantNRIC = scanner.nextLine();
        
        // Validate NRIC format
        if (!applicantNRIC.matches("^[ST]\\d{7}[A-Z]$")) {
            System.out.println("Invalid NRIC format! It must start with S or T, followed by 7 digits and an uppercase letter.");
            return;
        }
        
        // Get applicant
        Applicant applicant = ApplicantController.getApplicant(applicantNRIC);
        if (applicant == null) {
            System.out.println("Applicant not found.");
            return;
        }
        
        // Check if applicant has an application
        BTOApplication application = applicant.getApplication();
        if (application == null) {
            System.out.println("Applicant does not have an active application.");
            return;
        }
        
        // Check if application is successful
        if (application.getApplicationStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("Applicant's application is not in SUCCESSFUL status.");
            return;
        }
        
        // Check if applicant already has a booking
        Flat existingBooking = FlatBookingManager.getBooking(applicantNRIC);
        if (existingBooking != null) {
            System.out.println("Applicant already has a booking.");
            return;
        }
        
        // Get project
        BTOProject project = application.getProject();
        
        // Check if officer is assigned to the project
        if (!project.getOfficers().contains(officer)) {
            System.out.println("You are not assigned to this project.");
            return;
        }
        
        // Get the room type the applicant applied for
        RoomType appliedRoomType = application.getRoomType();
        
        // Display available flats of the same room type
        List<Flat> availableFlats = project.getAvailableFlats();
        List<Flat> matchingFlats = new ArrayList<>();
        
        for (Flat flat : availableFlats) {
            if (flat.getType() == appliedRoomType) {
                matchingFlats.add(flat);
            }
        }
        
        if (matchingFlats.isEmpty()) {
            System.out.println("No available " + appliedRoomType + " flats in this project.");
            return;
        }
        
        System.out.println("\n=== Available " + appliedRoomType + " Flats ===");
        for (int i = 0; i < matchingFlats.size(); i++) {
            Flat flat = matchingFlats.get(i);
            System.out.println((i + 1) + ". Flat ID: " + flat.getFlatID() + ", Type: " + flat.getType());
        }
        
        System.out.print("Enter flat number to book: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= matchingFlats.size()) {
                Flat selectedFlat = matchingFlats.get(choice - 1);
                
                // Book the flat
                FlatBookingManager bookingManager = new FlatBookingManager();
                if (bookingManager.bookFlat(applicant, officer, selectedFlat)) {
                    System.out.println("Flat booked successfully for " + applicant.getName());
                }
            } else {
                System.out.println("Invalid flat number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}
