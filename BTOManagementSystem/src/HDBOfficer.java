public class HDBOfficer extends User {
    private BTOProject assignedProject;
    private String registrationStatus;

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
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