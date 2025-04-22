import java.util.ArrayList;
import java.util.List;

public class HDBManager extends User {
    private List<BTOProject> managedProjects;
    private ProjectManager projectManager;
    private OfficerApprovalManager officerApprovalManager; // Approve, reject, a list of pending officer registrations.
    private ApplicationApprovalManager applicationApprovalManager;
    private EnquiryManager enquiryManager;

    public HDBManager(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
        this.managedProjects = new ArrayList<>();
    }

    public void addManagedProject(BTOProject project) {
        if (!managedProjects.contains(project)) {
            managedProjects.add(project);
            project.setManager(this);
        }
    }

    public void removeManagedProject(BTOProject project) {
        if (managedProjects.remove(project)) {
            project.setManager(null);
        }
    }

    public List<BTOProject> getManagedProjects() {
        return new ArrayList<>(managedProjects);
    }

    public void addOfficerProjectApplication(HDBOfficer officer, BTOProject project) {
        officerApprovalManager.addOfficerProjectApplication(officer, project);
    }

    public void assignOfficerToProject(HDBOfficer officer, BTOProject project) {
        project.addOfficer(officer);
    }
} 