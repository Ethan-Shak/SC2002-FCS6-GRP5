import java.util.ArrayList;
import java.util.List;

public class HDBOfficer extends Applicant {
    private List<BTOProject> assignedProjects;
    private RegistrationStatus registrationStatus;
    private BTOProject pendingProject; // Track the project pending approval
    private boolean loadedFromCSV; // Flag to indicate if officer was loaded from CSV

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
        this.registrationStatus = RegistrationStatus.PENDING; // Default status
        this.assignedProjects = new ArrayList<>();
        this.pendingProject = null;
        this.loadedFromCSV = false; // Default to false
    }

    public void registerForProject(BTOProject project) {
        // Check if the officer is eligible to register for this project
        if (!OfficerEligibilityManager.isEligibleForProject(this, project)) {
            return;
        }
        
        if (!assignedProjects.contains(project)) {
            // Set as pending for manual approval
            pendingProject = project;
            registrationStatus = RegistrationStatus.PENDING;
        }
    }

    public void approveRegistration() {
        if (pendingProject != null) {
            // Add the officer to the project's officers list
            pendingProject.addOfficer(this);
            
            // Add the project to the officer's assigned projects
            assignedProjects.add(pendingProject);
            registrationStatus = RegistrationStatus.APPROVED;
            System.out.println("HDB Officer approved for project: " + pendingProject.getProjectName());
            pendingProject = null;
        }
    }

    public void rejectRegistration() {
        if (pendingProject != null) {
            registrationStatus = RegistrationStatus.REJECTED;
            System.out.println("HDB Officer registration rejected for project: " + pendingProject.getProjectName());
            pendingProject = null;
        }
    }

    public List<BTOProject> getAssignedProjects() {
        return new ArrayList<>(assignedProjects);
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public BTOProject getPendingProject() {
        return pendingProject;
    }

    public boolean hasPendingRegistration() {
        return pendingProject != null;
    }
    
    public void setLoadedFromCSV(boolean loadedFromCSV) {
        this.loadedFromCSV = loadedFromCSV;
    }
    
    public boolean isLoadedFromCSV() {
        return loadedFromCSV;
    }

    public void setRegistrationStatus(RegistrationStatus status) {
        this.registrationStatus = status;
    }
}