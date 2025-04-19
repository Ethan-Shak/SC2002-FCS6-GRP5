public class HDBOfficer extends Applicant {
    private BTOProject assignedProject;
    private String registrationStatus;

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
        this.registrationStatus = "Pending"; // Default status
    }

    public void registerForProject(BTOProject project) {
        if (registrationStatus.equals("Pending")) {
            this.assignedProject = project;
            registrationStatus = "Approved";
            System.out.println("HDB Officer approved for project: " + project.getProjectName());
        } else {
            System.out.println("Registration already processed.");
        }
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }
}