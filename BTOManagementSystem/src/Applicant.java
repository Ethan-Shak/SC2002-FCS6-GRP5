public class Applicant extends User {
    private BTOApplication application;

    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.application = null;
    }

    public void applyForProject(BTOProject project) {
        if (application == null) {
            application = new BTOApplication(this, project);
            System.out.println("Application submitted for " + project.getProjectName());
        } else {
            System.out.println("Error: Already applied for a project.");
        }
    }

    public BTOApplication getApplication() {
        return application;
    }
}