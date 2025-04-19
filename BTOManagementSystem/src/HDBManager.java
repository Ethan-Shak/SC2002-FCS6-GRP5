import java.util.ArrayList;
import java.util.List;

public class HDBManager extends User {
    private List<BTOProject> managedProjects;

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
} 