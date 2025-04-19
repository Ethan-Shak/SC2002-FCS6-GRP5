import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ApplicationApprovalManager implements IApplicationApprovalManager {
    // Map to track applications by applicant NRIC
    private static Map<String, BTOApplication> applications = new HashMap<>();
    
    @Override
    public boolean applyForProject(Applicant applicant, BTOProject project) {
        // Check if applicant already has an application
        if (applications.containsKey(applicant.getNRIC())) {
            System.out.println("Error: You have already applied for a project.");
            return false;
        }
        
        // Check if project is visible to the applicant
        if (!project.isVisible(applicant)) {
            System.out.println("Error: This project is not visible to you.");
            return false;
        }
        
        // Check if applicant is eligible for the project based on marital status and age
        if (!isEligibleForProject(applicant, project)) {
            System.out.println("Error: You are not eligible for this project.");
            return false;
        }
        
        // Check if application period is open
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(project.getApplicationOpeningDate()) || now.isAfter(project.getApplicationClosingDate())) {
            System.out.println("Error: Application period is not open for this project.");
            return false;
        }
        
        // Create and store the application
        try {
            BTOApplication application = new BTOApplication(applicant, project);
            applications.put(applicant.getNRIC(), application);
            
            // Update the applicant's application reference
            applicant.setApplication(application);
            
            System.out.println("Application submitted successfully for " + project.getProjectName());
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean withdrawApplication(Applicant applicant, BTOProject project) {
        // Check if applicant has an application
        BTOApplication application = applications.get(applicant.getNRIC());
        if (application == null) {
            System.out.println("Error: You do not have an active application.");
            return false;
        }
        
        // Check if the application is for the specified project
        if (!application.getProject().equals(project)) {
            System.out.println("Error: Your application is not for this project.");
            return false;
        }
        
        // Immediately approve withdrawal
        return approveWithdrawal(applicant);
    }
    
    // Helper method to check if an applicant is eligible for a project
    private boolean isEligibleForProject(Applicant applicant, BTOProject project) {
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
    
    // Method to approve a withdrawal request
    public boolean approveWithdrawal(Applicant applicant) {
        BTOApplication application = applications.get(applicant.getNRIC());
        if (application != null) {
            applications.remove(applicant.getNRIC());
            applicant.setApplication(null);
            System.out.println("Application withdrawn successfully for " + applicant.getName());
            return true;
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
} 