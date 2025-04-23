import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.HashMap;

public class App {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        // Load data at startup
        ApplicantController.loadApplicantsFromCSV("ApplicantList.csv");
        ManagerController.loadManagersFromCSV("ManagerList.csv");
        ProjectManager.loadProjectsFromCSV("ProjectList.csv");  // Load projects first
        OfficerController.loadOfficersFromCSV("OfficerList.csv");  // Then load officers
        
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
            System.out.println("2. Create New Project");
            System.out.println("3. Edit Project");
            System.out.println("4. Delete Project");
            System.out.println("5. Toggle Project Visibility");
            System.out.println("6. Manage Applications");
            System.out.println("7. Manage Enquiries");
            System.out.println("8. Manage Officer Registrations");
            System.out.println("9. Change Password");
            System.out.println("10. Logout");
        } else if (currentUser instanceof HDBOfficer) {
            // HDB Officer menu (includes both applicant and officer options)
            System.out.println("2. Apply for Project");
            System.out.println("3. Withdraw Application");
            System.out.println("4. View Application Status");
            System.out.println("5. Book Flat for Applicant");
            System.out.println("6. Register for Project as Officer");
            System.out.println("7. View Registration Status");
            System.out.println("8. Manage Enquiries");
            System.out.println("9. Change Password");
            System.out.println("10. Logout");
        } else if (currentUser instanceof Applicant) {
            // Regular Applicant menu
            System.out.println("2. Apply for Project");
            System.out.println("3. Withdraw Application");
            System.out.println("4. View Application Status");
            System.out.println("5. Manage Enquiries");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
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
        
        if (currentUser instanceof HDBManager) {
            // Show filtering options for managers
            System.out.println("\n=== Project Filter Options ===");
            System.out.println("1. View All Projects");
            System.out.println("2. View My Projects Only");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        System.out.println("\n=== All Projects ===");
                        for (BTOProject project : projects) {
                            displayProjectDetails(project, true);
                        }
                        break;
                    case 2:
                        HDBManager manager = (HDBManager) currentUser;
                        List<BTOProject> managedProjects = ProjectManager.getProjectsByManager(manager);
                        if (managedProjects.isEmpty()) {
                            System.out.println("You are not managing any projects.");
                            return;
                        }
                        System.out.println("\n=== My Projects ===");
                        for (BTOProject project : managedProjects) {
                            displayProjectDetails(project, true);
                        }
                        break;
                    case 3:
                        return; // Return to main menu
                    default:
                        System.out.println("Invalid choice. Showing all projects.");
                        System.out.println("\n=== All Projects ===");
                        for (BTOProject project : projects) {
                            displayProjectDetails(project, true);
                        }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Showing all projects.");
                System.out.println("\n=== All Projects ===");
                for (BTOProject project : projects) {
                    displayProjectDetails(project, true);
                }
            }
        } else if (currentUser instanceof Applicant) {
            // Applicants can only see projects visible to their user group
            System.out.println("\n=== Available Projects ===");
            for (BTOProject project : projects) {
                if (project.checkVisibility((Applicant)currentUser)) {
                    displayProjectDetails(project, false);
                }
            }
        } else if (currentUser instanceof HDBOfficer) {
            // Officers can see all projects they are assigned to
            System.out.println("\n=== Assigned Projects ===");
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
            project.getApplicationOpeningDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
            " to " + 
            project.getApplicationClosingDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
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
                    createNewProject();
                    break;
                case 3:
                    editProject();
                    break;
                case 4:
                    deleteProject();
                    break;
                case 5:
                    toggleProjectVisibility();
                    break;
                case 6:
                    manageApplications();
                    break;
                case 7:
                    manageEnquiriesAsManager();
                    break;
                case 8:
                    manageOfficerRegistrations();
                    break;
                case 9:
                    handleChangePassword();
                    break;
                case 10:
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
                    registerForProjectAsOfficer();
                    break;
                case 7:
                    viewOfficerRegistrationStatus();
                    break;
                case 8:
                    manageEnquiriesAsOfficer();
                    break;
                case 9:
                    handleChangePassword();
                    break;
                case 10:
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
                    manageEnquiries();
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

    private static void manageEnquiries() {
        if (!(currentUser instanceof Applicant)) {
            System.out.println("This option is only available for applicants.");
            return;
        }
        
        Applicant applicant = (Applicant) currentUser;
        
        boolean running = true;
        while (running) {
            System.out.println("\n=== Manage Enquiries ===");
            System.out.println("1. Submit New Enquiry");
            System.out.println("2. View My Enquiries");
            System.out.println("3. Edit Enquiry");
            System.out.println("4. Delete Enquiry");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        submitEnquiry(applicant);
                        break;
                    case 2:
                        viewEnquiries(applicant);
                        break;
                    case 3:
                        editEnquiry(applicant);
                        break;
                    case 4:
                        deleteEnquiry(applicant);
                        break;
                    case 5:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private static void submitEnquiry(Applicant applicant) {
        // Display available projects
        List<BTOProject> projects = ProjectManager.getAllProjects();
        List<BTOProject> eligibleProjects = new ArrayList<>();
        
        System.out.println("\n=== Available Projects ===");
        int index = 1;
        for (BTOProject project : projects) {
            if (project.checkVisibility(applicant)) {
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
        
        System.out.print("Enter project number to submit enquiry: ");
        try {
            int projectChoice = Integer.parseInt(scanner.nextLine());
            if (projectChoice >= 1 && projectChoice <= eligibleProjects.size()) {
                BTOProject selectedProject = eligibleProjects.get(projectChoice - 1);
                
                System.out.print("Enter your enquiry: ");
                String content = scanner.nextLine();
                
                if (content.trim().isEmpty()) {
                    System.out.println("Enquiry cannot be empty.");
                    return;
                }
                
                Enquiry enquiry = EnquiryManager.submitEnquiry(applicant, selectedProject, content);
                System.out.println("Enquiry submitted successfully with ID: " + enquiry.getEnquiryID());
            } else {
                System.out.println("Invalid project number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void viewEnquiries(Applicant applicant) {
        List<Enquiry> enquiries = EnquiryManager.getEnquiriesForApplicant(applicant);
        
        if (enquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries.");
            return;
        }
        
        System.out.println("\n=== Your Enquiries ===");
        for (Enquiry enquiry : enquiries) {
            System.out.println("\n" + enquiry.toString());
        }
    }
    
    private static void editEnquiry(Applicant applicant) {
        List<Enquiry> enquiries = EnquiryManager.getEnquiriesForApplicant(applicant);
        
        if (enquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries.");
            return;
        }
        
        System.out.println("\n=== Your Enquiries ===");
        for (Enquiry enquiry : enquiries) {
            System.out.println("\n" + enquiry.toString());
        }
        
        System.out.print("\nEnter enquiry ID to edit (0 to cancel): ");
        try {
            int enquiryID = Integer.parseInt(scanner.nextLine());
            if (enquiryID == 0) {
                return;
            }
            
            Enquiry enquiry = EnquiryManager.getEnquiry(enquiryID);
            if (enquiry == null || !enquiry.getApplicant().equals(applicant)) {
                System.out.println("Invalid enquiry ID or you don't own this enquiry.");
                return;
            }
            
            System.out.print("Enter new enquiry content: ");
            String newContent = scanner.nextLine();
            
            if (newContent.trim().isEmpty()) {
                System.out.println("Enquiry cannot be empty.");
                return;
            }
            
            if (EnquiryManager.updateEnquiry(enquiryID, newContent)) {
                System.out.println("Enquiry updated successfully.");
            } else {
                System.out.println("Failed to update enquiry.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void deleteEnquiry(Applicant applicant) {
        List<Enquiry> enquiries = EnquiryManager.getEnquiriesForApplicant(applicant);
        
        if (enquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries.");
            return;
        }
        
        System.out.println("\n=== Your Enquiries ===");
        for (Enquiry enquiry : enquiries) {
            System.out.println("\n" + enquiry.toString());
        }
        
        System.out.print("\nEnter enquiry ID to delete (0 to cancel): ");
        try {
            int enquiryID = Integer.parseInt(scanner.nextLine());
            if (enquiryID == 0) {
                return;
            }
            
            Enquiry enquiry = EnquiryManager.getEnquiry(enquiryID);
            if (enquiry == null || !enquiry.getApplicant().equals(applicant)) {
                System.out.println("Invalid enquiry ID or you don't own this enquiry.");
                return;
            }
            
            System.out.print("Are you sure you want to delete this enquiry? (y/n): ");
            String confirm = scanner.nextLine().toLowerCase();
            
            if (confirm.equals("y") || confirm.equals("yes")) {
                if (EnquiryManager.deleteEnquiry(enquiryID)) {
                    System.out.println("Enquiry deleted successfully.");
                } else {
                    System.out.println("Failed to delete enquiry.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static void createNewProject() {
        if (!(currentUser instanceof HDBManager)) {
            System.out.println("This option is only available for HDB managers.");
            return;
        }

        HDBManager manager = (HDBManager) currentUser;
        List<BTOProject> managedProjects = manager.getManagedProjects();
        
        System.out.println("\n=== Create New Project ===");
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine().trim();
        
        System.out.print("Enter neighborhood: ");
        String neighborhood = scanner.nextLine().trim();
        
        // Get unit counts for both room types
        System.out.println("\nEnter number of units for each room type:");
        System.out.print("Number of TWO_ROOM units: ");
        int twoRoomUnits;
        try {
            twoRoomUnits = Integer.parseInt(scanner.nextLine());
            if (twoRoomUnits < 0) {
                System.out.println("Number of units cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of units.");
            return;
        }
        
        System.out.print("Number of THREE_ROOM units: ");
        int threeRoomUnits;
        try {
            threeRoomUnits = Integer.parseInt(scanner.nextLine());
            if (threeRoomUnits < 0) {
                System.out.println("Number of units cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of units.");
            return;
        }
        
        if (twoRoomUnits == 0 && threeRoomUnits == 0) {
            System.out.println("Error: At least one room type must have units.");
            return;
        }
        
        System.out.print("Enter application opening date (d/M/yyyy): ");
        LocalDate openingDate;
        try {
            openingDate = LocalDate.parse(scanner.nextLine(), java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"));
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use d/M/yyyy (e.g., 4/5/2024).");
            return;
        }
        
        System.out.print("Enter application closing date (d/M/yyyy): ");
        LocalDate closingDate;
        try {
            closingDate = LocalDate.parse(scanner.nextLine(), java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"));
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use d/M/yyyy (e.g., 4/5/2024).");
            return;
        }
        
        if (closingDate.isBefore(openingDate)) {
            System.out.println("Error: Closing date must be after opening date.");
            return;
        }
        
        // Create the project with TWO_ROOM type first (we'll add THREE_ROOM in the next step)
        if (ProjectManager.createProject(projectName, neighborhood, RoomType.TWO_ROOM, manager, openingDate, closingDate)) {
            // Set the flat inventory with both room types
            Map<RoomType, Integer> flatInventory = new HashMap<>();
            if (twoRoomUnits > 0) {
                flatInventory.put(RoomType.TWO_ROOM, twoRoomUnits);
            }
            if (threeRoomUnits > 0) {
                flatInventory.put(RoomType.THREE_ROOM, threeRoomUnits);
            }
            
            BTOProject project = ProjectManager.getAllProjects().stream()
                .filter(p -> p.getProjectName().equals(projectName))
                .findFirst()
                .orElse(null);
                
            if (project != null) {
                project.setFlatInventory(flatInventory);
                ProjectManager.saveProjectsToCSV();
            }
        }
    }

    private static void editProject() {
        if (!(currentUser instanceof HDBManager)) {
            System.out.println("This option is only available for HDB managers.");
            return;
        }

        HDBManager manager = (HDBManager) currentUser;
        List<BTOProject> managedProjects = ProjectManager.getProjectsByManager(manager);
        
        if (managedProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
        
        System.out.println("\n=== Edit Project ===");
        System.out.println("Select a project to edit:");
        for (int i = 0; i < managedProjects.size(); i++) {
            BTOProject project = managedProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName());
        }
        
        System.out.print("Enter project number: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > managedProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
            
            BTOProject project = managedProjects.get(choice - 1);
            
            System.out.println("\nWhat would you like to edit?");
            System.out.println("1. Neighborhood");
            System.out.println("2. Room Type");
            System.out.println("3. Application Period");
            System.out.println("4. Back");
            
            System.out.print("Enter your choice: ");
            int editChoice = Integer.parseInt(scanner.nextLine());
            
            switch (editChoice) {
                case 1:
                    System.out.print("Enter new neighborhood: ");
                    String newNeighborhood = scanner.nextLine().trim();
                    ProjectManager.editProject(project.getProjectName(), newNeighborhood, null, null, null);
                    break;
                case 2:
                    System.out.println("\nAvailable room types:");
                    System.out.println("1. TWO_ROOM");
                    System.out.println("2. THREE_ROOM");
                    System.out.print("Enter new room type (1-2): ");
                    int typeChoice = Integer.parseInt(scanner.nextLine());
                    RoomType newRoomType = (typeChoice == 1) ? RoomType.TWO_ROOM : RoomType.THREE_ROOM;
                    ProjectManager.editProject(project.getProjectName(), null, newRoomType, null, null);
                    break;
                case 3:
                    System.out.print("Enter new opening date (d/M/yyyy): ");
                    LocalDate newOpeningDate = LocalDate.parse(scanner.nextLine(), 
                        java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"));
                    System.out.print("Enter new closing date (d/M/yyyy): ");
                    LocalDate newClosingDate = LocalDate.parse(scanner.nextLine(), 
                        java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"));
                    
                    if (newClosingDate.isBefore(newOpeningDate)) {
                        System.out.println("Error: Closing date must be after opening date.");
                        return;
                    }
                    
                    ProjectManager.editProject(project.getProjectName(), null, null, newOpeningDate, newClosingDate);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteProject() {
        if (!(currentUser instanceof HDBManager)) {
            System.out.println("This option is only available for HDB managers.");
            return;
        }

        HDBManager manager = (HDBManager) currentUser;
        List<BTOProject> managedProjects = ProjectManager.getProjectsByManager(manager);
        
        if (managedProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
        
        System.out.println("\n=== Delete Project ===");
        System.out.println("Select a project to delete:");
        for (int i = 0; i < managedProjects.size(); i++) {
            BTOProject project = managedProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName());
        }
        
        System.out.print("Enter project number (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > managedProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
            
            BTOProject project = managedProjects.get(choice - 1);
            System.out.print("Are you sure you want to delete " + project.getProjectName() + "? (y/n): ");
            String confirm = scanner.nextLine().toLowerCase();
            
            if (confirm.equals("y") || confirm.equals("yes")) {
                ProjectManager.deleteProject(project.getProjectName());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static void manageEnquiriesAsManager() {
        if (!(currentUser instanceof HDBManager)) {
            System.out.println("This option is only available for HDB managers.");
            return;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        
        boolean running = true;
        while (running) {
            System.out.println("\n=== Manage Enquiries ===");
            System.out.println("1. View All Unanswered Enquiries");
            System.out.println("2. View My Project Enquiries");
            System.out.println("3. Respond to Enquiry");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        viewAllUnansweredEnquiries();
                        break;
                    case 2:
                        viewManagerProjectEnquiries(manager);
                        break;
                    case 3:
                        respondToEnquiry(manager);
                        break;
                    case 4:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private static void manageEnquiriesAsOfficer() {
        if (!(currentUser instanceof HDBOfficer)) {
            System.out.println("This option is only available for HDB officers.");
            return;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        
        boolean running = true;
        while (running) {
            System.out.println("\n=== Manage Enquiries ===");
            System.out.println("1. View Project Enquiries");
            System.out.println("2. Respond to Enquiry");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        viewOfficerProjectEnquiries(officer);
                        break;
                    case 2:
                        respondToEnquiry(officer);
                        break;
                    case 3:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private static void viewAllUnansweredEnquiries() {
        List<Enquiry> unansweredEnquiries = EnquiryManager.getAllUnansweredEnquiries();
        
        if (unansweredEnquiries.isEmpty()) {
            System.out.println("No unanswered enquiries.");
            return;
        }
        
        System.out.println("\n=== All Unanswered Enquiries ===");
        for (Enquiry enquiry : unansweredEnquiries) {
            System.out.println("\n" + enquiry.toString());
        }
    }
    
    private static void viewManagerProjectEnquiries(HDBManager manager) {
        List<BTOProject> managedProjects = manager.getManagedProjects();
        
        if (managedProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
        
        System.out.println("\n=== Select Project to View Enquiries ===");
        for (int i = 0; i < managedProjects.size(); i++) {
            BTOProject project = managedProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName());
        }
        
        System.out.print("Enter project number: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= managedProjects.size()) {
                BTOProject selectedProject = managedProjects.get(choice - 1);
                
                System.out.println("\n=== Unanswered Enquiries for " + selectedProject.getProjectName() + " ===");
                List<Enquiry> unansweredEnquiries = EnquiryManager.getUnansweredEnquiriesForProject(selectedProject);
                if (unansweredEnquiries.isEmpty()) {
                    System.out.println("No unanswered enquiries for this project.");
                } else {
                    for (Enquiry enquiry : unansweredEnquiries) {
                        System.out.println("\n" + enquiry.toString());
                    }
                }
                
                System.out.println("\n=== Answered Enquiries for " + selectedProject.getProjectName() + " ===");
                List<Enquiry> answeredEnquiries = EnquiryManager.getAnsweredEnquiriesForProject(selectedProject);
                if (answeredEnquiries.isEmpty()) {
                    System.out.println("No answered enquiries for this project.");
                } else {
                    for (Enquiry enquiry : answeredEnquiries) {
                        System.out.println("\n" + enquiry.toString());
                    }
                }
            } else {
                System.out.println("Invalid project number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void viewOfficerProjectEnquiries(HDBOfficer officer) {
        List<BTOProject> assignedProjects = officer.getAssignedProjects();
        
        if (assignedProjects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            return;
        }
        
        System.out.println("\n=== Select Project to View Enquiries ===");
        for (int i = 0; i < assignedProjects.size(); i++) {
            BTOProject project = assignedProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName());
        }
        
        System.out.print("Enter project number: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= assignedProjects.size()) {
                BTOProject selectedProject = assignedProjects.get(choice - 1);
                
                System.out.println("\n=== Unanswered Enquiries for " + selectedProject.getProjectName() + " ===");
                List<Enquiry> unansweredEnquiries = EnquiryManager.getUnansweredEnquiriesForProject(selectedProject);
                if (unansweredEnquiries.isEmpty()) {
                    System.out.println("No unanswered enquiries for this project.");
                } else {
                    for (Enquiry enquiry : unansweredEnquiries) {
                        System.out.println("\n" + enquiry.toString());
                    }
                }
                
                System.out.println("\n=== Answered Enquiries for " + selectedProject.getProjectName() + " ===");
                List<Enquiry> answeredEnquiries = EnquiryManager.getAnsweredEnquiriesForProject(selectedProject);
                if (answeredEnquiries.isEmpty()) {
                    System.out.println("No answered enquiries for this project.");
                } else {
                    for (Enquiry enquiry : answeredEnquiries) {
                        System.out.println("\n" + enquiry.toString());
                    }
                }
            } else {
                System.out.println("Invalid project number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void respondToEnquiry(User responder) {
        List<Enquiry> enquiries;
        if (responder instanceof HDBManager) {
            HDBManager manager = (HDBManager) responder;
            System.out.println("\n=== Select Project ===");
            List<BTOProject> managedProjects = manager.getManagedProjects();
            
            if (managedProjects.isEmpty()) {
                System.out.println("You are not managing any projects.");
                return;
            }
            
            for (int i = 0; i < managedProjects.size(); i++) {
                BTOProject project = managedProjects.get(i);
                System.out.println((i + 1) + ". " + project.getProjectName());
            }
            
            System.out.print("Enter project number: ");
            try {
                int projectChoice = Integer.parseInt(scanner.nextLine());
                if (projectChoice >= 1 && projectChoice <= managedProjects.size()) {
                    BTOProject selectedProject = managedProjects.get(projectChoice - 1);
                    enquiries = EnquiryManager.getUnansweredEnquiriesForProject(selectedProject);
                } else {
                    System.out.println("Invalid project number.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
        } else {
            HDBOfficer officer = (HDBOfficer) responder;
            System.out.println("\n=== Select Project ===");
            List<BTOProject> assignedProjects = officer.getAssignedProjects();
            
            if (assignedProjects.isEmpty()) {
                System.out.println("You are not assigned to any projects.");
                return;
            }
            
            for (int i = 0; i < assignedProjects.size(); i++) {
                BTOProject project = assignedProjects.get(i);
                System.out.println((i + 1) + ". " + project.getProjectName());
            }
            
            System.out.print("Enter project number: ");
            try {
                int projectChoice = Integer.parseInt(scanner.nextLine());
                if (projectChoice >= 1 && projectChoice <= assignedProjects.size()) {
                    BTOProject selectedProject = assignedProjects.get(projectChoice - 1);
                    enquiries = EnquiryManager.getUnansweredEnquiriesForProject(selectedProject);
                } else {
                    System.out.println("Invalid project number.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
        }
        
        if (enquiries.isEmpty()) {
            System.out.println("No unanswered enquiries for this project.");
            return;
        }
        
        System.out.println("\n=== Select Enquiry to Respond ===");
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". " + enquiry.toString());
        }
        
        System.out.print("Enter enquiry number: ");
        try {
            int enquiryChoice = Integer.parseInt(scanner.nextLine());
            if (enquiryChoice >= 1 && enquiryChoice <= enquiries.size()) {
                Enquiry selectedEnquiry = enquiries.get(enquiryChoice - 1);
                
                System.out.print("Enter your response: ");
                String response = scanner.nextLine();
                
                if (response.trim().isEmpty()) {
                    System.out.println("Response cannot be empty.");
                    return;
                }
                
                if (EnquiryManager.respondToEnquiry(selectedEnquiry.getEnquiryID(), response, responder)) {
                    System.out.println("Response submitted successfully.");
                } else {
                    System.out.println("Failed to submit response.");
                }
            } else {
                System.out.println("Invalid enquiry number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static void registerForProjectAsOfficer() {
        if (!(currentUser instanceof HDBOfficer)) {
            System.out.println("This option is only available for HDB officers.");
            return;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        
        System.out.println("\n=== Register for Project ===");
        
        // Get list of visible projects
        List<BTOProject> visibleProjects = ProjectManager.getAllProjects();
        if (visibleProjects.isEmpty()) {
            System.out.println("No projects available for registration.");
            return;
        }
        
        // Display available projects
        System.out.println("\nAvailable Projects:");
        for (int i = 0; i < visibleProjects.size(); i++) {
            BTOProject project = visibleProjects.get(i);
            System.out.printf("%d. %s (Neighbourhood: %s)\n", 
                i + 1, project.getProjectName(), project.getNeighbourhood());
        }
        
        // Get project selection
        System.out.print("\nSelect project number (0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        if (choice == 0) {
            return;
        }
        
        if (choice < 1 || choice > visibleProjects.size()) {
            System.out.println("Invalid project number.");
            return;
        }
        
        BTOProject selectedProject = visibleProjects.get(choice - 1);
        
        // First register the officer for the project
        if (officer.registerForProject(selectedProject)) {
            System.out.println("Registration request submitted for project: " + selectedProject.getProjectName());
            System.out.println("Waiting for manager approval...");
        }
    }

    private static void viewOfficerRegistrationStatus() {
        if (!(currentUser instanceof HDBOfficer)) {
            System.out.println("This option is only available for HDB officers.");
            return;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        RegistrationStatus status = officer.getRegistrationStatus();
        List<BTOProject> assignedProjects = officer.getAssignedProjects();
        BTOProject pendingProject = officer.getPendingProject();
        
        System.out.println("\n=== Officer Registration Status ===");
        System.out.println("Current Status: " + status);
        
        if (pendingProject != null) {
            System.out.println("\nPending Registration:");
            System.out.println("- Project: " + pendingProject.getProjectName() + " (" + pendingProject.getNeighbourhood() + ")");
            System.out.println("  Application Period: " + 
                pendingProject.getApplicationOpeningDate().format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy")) + 
                " to " + 
                pendingProject.getApplicationClosingDate().format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy")));
            System.out.println("  Status: Waiting for manager approval");
        }
        
        if (!assignedProjects.isEmpty()) {
            System.out.println("\nApproved Projects:");
            for (BTOProject project : assignedProjects) {
                System.out.println("- " + project.getProjectName() + " (" + project.getNeighbourhood() + ")");
                System.out.println("  Application Period: " + 
                    project.getApplicationOpeningDate().format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy")) + 
                    " to " + 
                    project.getApplicationClosingDate().format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy")));
            }
        }
    }

    private static void manageOfficerRegistrations() {
        if (!(currentUser instanceof HDBManager)) {
            System.out.println("This option is only available for HDB managers.");
            return;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        List<BTOProject> managedProjects = manager.getManagedProjects();
        
        if (managedProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
        
        System.out.println("\n=== Manage Officer Registrations ===");
        System.out.println("Select a project to view pending registrations:");
        for (int i = 0; i < managedProjects.size(); i++) {
            BTOProject project = managedProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName());
        }
        
        System.out.print("Enter project number: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= managedProjects.size()) {
                BTOProject selectedProject = managedProjects.get(choice - 1);
                handlePendingRegistrations(selectedProject);
            } else {
                System.out.println("Invalid project number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void handlePendingRegistrations(BTOProject project) {
        List<HDBOfficer> pendingOfficers = new ArrayList<>();
        
        // Get all officers from the system
        for (HDBOfficer officer : OfficerController.getAllOfficers()) {
            if (officer.hasPendingRegistration() && officer.getPendingProject().equals(project)) {
                pendingOfficers.add(officer);
            }
        }
        
        if (pendingOfficers.isEmpty()) {
            System.out.println("No pending officer registrations for this project.");
            return;
        }
        
        System.out.println("\n=== Pending Officer Registrations for " + project.getProjectName() + " ===");
        for (int i = 0; i < pendingOfficers.size(); i++) {
            HDBOfficer officer = pendingOfficers.get(i);
            System.out.println((i + 1) + ". Officer: " + officer.getName() + " (NRIC: " + officer.getNRIC() + ")");
        }
        
        System.out.print("\nEnter officer number to review (0 to go back): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                return;
            }
            if (choice >= 1 && choice <= pendingOfficers.size()) {
                HDBOfficer selectedOfficer = pendingOfficers.get(choice - 1);
                reviewOfficerRegistration(selectedOfficer);
            } else {
                System.out.println("Invalid officer number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void reviewOfficerRegistration(HDBOfficer officer) {
        System.out.println("\n=== Review Officer Registration ===");
        System.out.println("Officer: " + officer.getName() + " (NRIC: " + officer.getNRIC() + ")");
        System.out.println("Project: " + officer.getPendingProject().getProjectName());
        
        System.out.println("\n1. Approve");
        System.out.println("2. Reject");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    officer.approveRegistration();
                    System.out.println("Officer registration approved.");
                    break;
                case 2:
                    officer.rejectRegistration();
                    System.out.println("Officer registration rejected.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}
