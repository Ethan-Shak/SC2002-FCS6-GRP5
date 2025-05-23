import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ApplicationApprovalManager implements IApplicationApprovalManager {
    // Map to track applications by applicant NRIC
    private static Map<String, BTOApplication> applications = new HashMap<>();
    
    @Override
    public boolean applyForProject(Applicant applicant, BTOProject project, RoomType roomType) {
        // Check if applicant already has an application
        if (applications.containsKey(applicant.getNRIC())) {
            System.out.println("Error: You have already applied for a project.");
            return false;
        }
        
        // Check if project is visible to the applicant
        if (!project.checkVisibility(applicant)) {
            System.out.println("Error: This project is not visible to you.");
            return false;
        }
        
        // Check if applicant is eligible for the project based on marital status and age
        if (!isEligibleForProject(applicant, project)) {
            System.out.println("Error: You are not eligible for this project.");
            return false;
        }
        
        // Check if application period is open
        LocalDate now = LocalDate.now();
        if (now.isBefore(project.getApplicationOpeningDate()) || now.isAfter(project.getApplicationClosingDate())) {
            if (now.isBefore(project.getApplicationOpeningDate())) {
                System.out.println("Error: Application period is not open for this project yet. It will open on " + 
                    project.getApplicationOpeningDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                System.out.println("Error: Application period has closed for this project. It ended on " + 
                    project.getApplicationClosingDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return false;
        }
        
        // Create and store the application
        try {
            BTOApplication application = new BTOApplication(applicant, project, roomType);
            applications.put(applicant.getNRIC(), application);
            
            // Update the applicant's application reference
            applicant.setApplication(application);
            
            System.out.println("Application submitted successfully for " + project.getProjectName());
            return true;
        } catch (Exception e) {
            System.out.println("Error applying for project: " + e.getMessage());
            return false;
        }
    }
    
    // Method to approve a withdrawal request
    @Override
    public boolean approveWithdrawal(Applicant applicant) {
        // Check if applicant has an application
        BTOApplication application = applications.get(applicant.getNRIC());
        if (application == null) {
            System.out.println("Error: You do not have an active application.");
            return false;
        }
        
        // If the application is in BOOKED status, release the flat first
        if (application.getApplicationStatus() == ApplicationStatus.BOOKED) {
            FlatBookingManager.releaseFlat(applicant.getNRIC());
        } else if (application.getApplicationStatus() == ApplicationStatus.SUCCESSFUL) {
            // For successful applications, update the flat inventory
            BTOProject project = application.getProject();
            RoomType roomType = application.getRoomType();
            
            // Update flat inventory
            Map<RoomType, Integer> flatInventory = project.getFlatInventory();
            int currentCount = flatInventory.getOrDefault(roomType, 0);
            flatInventory.put(roomType, currentCount + 1);
            project.setFlatInventory(flatInventory);
        }
        
        // Remove the application
        applications.remove(applicant.getNRIC());
        applicant.setApplication(null);
        System.out.println("Application withdrawn successfully.");
        return true;
    }
    
    // Helper method to check if an applicant is eligible for a project
    private static boolean isEligibleForProject(Applicant applicant, BTOProject project) {
        // Check if applicant is an HDB Officer
        if (applicant instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) applicant;
            
            // Check if officer is already assigned to this project
            if (officer.getAssignedProjects().contains(project)) {
                System.out.println("Error: As an HDB Officer assigned to this project, you cannot apply for it as an applicant.");
                return false;
            }
            
            // Check if officer has a pending registration for this project
            if (officer.getPendingProject() != null && officer.getPendingProject().equals(project)) {
                System.out.println("Error: As an HDB Officer with a pending registration for this project, you cannot apply for it as an applicant.");
                return false;
            }
        }
        
        // Singles, 35 years old and above, can ONLY apply for 2-Room
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE) {
            if (applicant.getAge() >= 35) {
                // Check if the project has 2-Room flats
                return project.getFlatInventory().containsKey(RoomType.TWO_ROOM);
            }
            return false;
        }
        
        // Married, 21 years old and above, can apply for any flat types
        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED && applicant.getAge() >= 21) {
            // Check if the project has any flats
            return !project.getFlatInventory().isEmpty();
        }
        
        return false;
    }
    
    // Method to get an application by applicant NRIC
    public static BTOApplication getApplication(String applicantNRIC) {
        return applications.get(applicantNRIC);
    }
    
    // Method to get all applications
    public static Map<String, BTOApplication> getAllApplications() {
        return new HashMap<>(applications);
    }
    
    @Override
    public boolean approveApplication(Applicant applicant) {
        BTOApplication application = applications.get(applicant.getNRIC());
        if (application == null) {
            System.out.println("Error: Applicant does not have an active application.");
            return false;
        }
        
        application.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
        System.out.println("Application approved successfully.");
        return true;
    }
    
    @Override
    public boolean rejectApplication(Applicant applicant) {
        BTOApplication application = applications.get(applicant.getNRIC());
        if (application == null) {
            System.out.println("Error: Applicant does not have an active application.");
            return false;
        }
        
        application.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
        System.out.println("Application rejected.");
        return true;
    }

    public static boolean applyForProject(Applicant applicant, BTOProject project) {
        // Check if applicant already has an application
        if (applications.containsKey(applicant.getNRIC())) {
            System.out.println("Error: You already have an active application.");
            return false;
        }
        
        // Check if project is visible to applicant
        if (!project.checkVisibility(applicant)) {
            System.out.println("Error: This project is not available for application.");
            return false;
        }
        
        // Check if applicant is eligible for the project
        if (!isEligibleForProject(applicant, project)) {
            if (applicant instanceof HDBOfficer) {
                HDBOfficer officer = (HDBOfficer) applicant;
                if (officer.getAssignedProjects().contains(project)) {
                    System.out.println("Error: As an HDB Officer assigned to this project, you cannot apply for it as an applicant.");
                    return false;
                }
            }
            System.out.println("Error: You are not eligible for this project based on your marital status and age.");
            return false;
        }
        
        // Check if application period is open
        LocalDate now = LocalDate.now();
        if (now.isBefore(project.getApplicationOpeningDate()) || now.isAfter(project.getApplicationClosingDate())) {
            System.out.println("Error: The application period for this project is currently closed.");
            return false;
        }
        
        // Create and store the application
        try {
            BTOApplication application = new BTOApplication(applicant, project, project.getRoomType());
            applications.put(applicant.getNRIC(), application);
            
            // Update the applicant's application reference
            applicant.setApplication(application);
            
            System.out.println("Application submitted successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("Error applying for project: " + e.getMessage());
            return false;
        }
    }
} 