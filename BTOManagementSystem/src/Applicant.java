public class Applicant extends User {
    private BTOApplication application;

    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
        this.application = null; // No application at start
    }

    public boolean login(String password) {
        SingpassAccount account = getSingpassAccount();
        if (account.authenticate(password)) {
            System.out.println("Login successful");
            return true;
        } else {
            System.out.println("Login failed: Invalid credentials");
            return false;
        }
    }

    public void applyForProject(BTOProject project) {
        if (application == null) {
            application = new BTOApplication(this, project);
            System.out.println("Application submitted for " + project.getProjectName());
        } else {
            System.out.println("Error: Already applied for a project.");
        }
    }
}