import java.util.ArrayList;
import java.util.List;

public class HDBOfficer extends Applicant {
    private List<BTOProject> assignedProjects;
    private String registrationStatus;

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
        this.registrationStatus = "Pending"; // Default status
        this.assignedProjects = new ArrayList<>();
    }

    public void registerForProject(BTOProject project) {
        if (registrationStatus.equals("Pending")) {
            // Check if the officer is eligible to register for this project
            if (!OfficerEligibilityManager.isEligibleForProject(this, project)) {
                return;
            }
            
            if (!assignedProjects.contains(project)) {
                assignedProjects.add(project);
                registrationStatus = "Approved";
                System.out.println("HDB Officer approved for project: " + project.getProjectName());
            }
        } else {
            System.out.println("Registration already processed.");
        }
    }

    public List<BTOProject> getAssignedProjects() {
        return new ArrayList<>(assignedProjects);
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }
}