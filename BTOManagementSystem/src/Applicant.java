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

    public void applyForProject(BTOProject project, RoomType roomType) {
        if (application == null) {
            try {
                application = new BTOApplication(this, project, roomType);
                System.out.println("Application submitted for " + project.getProjectName());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Error: Already applied for a project.");
        }
    }
    
    // Getter for application
    public BTOApplication getApplication() {
        return application;
    }
    
    // Setter for application
    public void setApplication(BTOApplication application) {
        this.application = application;
    }
}