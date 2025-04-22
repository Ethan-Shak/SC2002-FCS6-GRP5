import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class OfficerEligibilityManager {
    private static Map<String, Boolean> officerEligibilityMap = new HashMap<>();
    
    /**
     * Checks if an HDB Officer is eligible to register for a project.
     * 
     * @param officer The HDB Officer to check
     * @param project The project to check eligibility for
     * @return true if eligible, false otherwise
     */
    public static boolean isEligibleForProject(HDBOfficer officer, BTOProject project) {
        // Check if the officer has applied for this project
        if (hasAppliedForProject(officer, project)) {
            System.out.println("You cannot register for a project that you have applied for.");
            return false;
        }
        
        // Check for overlapping projects
        if (hasOverlappingProject(officer, project)) {
            System.out.println("You cannot register for multiple projects with overlapping application periods.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if an HDB Officer has applied for a project as an applicant.
     * 
     * @param officer The HDB Officer to check
     * @param project The project to check
     * @return true if the officer has applied for the project, false otherwise
     */
    public static boolean hasAppliedForProject(HDBOfficer officer, BTOProject project) {
        // Get all applications for the project
        Map<String, BTOApplication> allApplications = ApplicationApprovalManager.getAllApplications();
        
        // Check if the officer has applied for this project
        for (BTOApplication application : allApplications.values()) {
            if (application.getApplicant().equals(officer) && application.getProject().equals(project)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if an HDB Officer has an overlapping project with the given project.
     * 
     * @param officer The HDB Officer to check
     * @param project The project to check
     * @return true if there is an overlapping project, false otherwise
     */
    private static boolean hasOverlappingProject(HDBOfficer officer, BTOProject project) {
        List<BTOProject> assignedProjects = officer.getAssignedProjects();
        LocalDate newOpeningDate = project.getApplicationOpeningDate();
        LocalDate newClosingDate = project.getApplicationClosingDate();
        
        for (BTOProject assignedProject : assignedProjects) {
            LocalDate existingOpening = assignedProject.getApplicationOpeningDate();
            LocalDate existingClosing = assignedProject.getApplicationClosingDate();
            
            // Check for overlap
            if (!(newClosingDate.isBefore(existingOpening) || newOpeningDate.isAfter(existingClosing))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sets the eligibility status for an officer.
     * 
     * @param officer The officer to set eligibility for
     * @param eligible Whether the officer is eligible
     */
    public static void setOfficerEligibility(HDBOfficer officer, boolean eligible) {
        officerEligibilityMap.put(officer.getNRIC(), eligible);
    }
    
    /**
     * Gets the eligibility status for an officer.
     * 
     * @param officer The officer to get eligibility for
     * @return true if eligible, false otherwise
     */
    public static boolean getOfficerEligibility(HDBOfficer officer) {
        return officerEligibilityMap.getOrDefault(officer.getNRIC(), false);
    }
} 