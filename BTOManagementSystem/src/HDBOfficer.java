public class HDBOfficer extends Applicant {
    private BTOProject assignedProject;
    private OfficerRegistrationManager officerRegistrationManager;
    private EnquiryManager enquiryManager;
    private BookingOpsManager bookingOpsManager;
    private RegistrationStatus registrationStatus; // whether registered to project

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
        this.registrationStatus = RegistrationStatus.PENDING; // Default status
    }

    public void registerForProject(BTOProject project) {
        if (registrationStatus.equals(RegistrationStatus.PENDING)) {
            this.assignedProject = project;
            this.registrationStatus = RegistrationStatus.APPROVED;
            System.out.println("HDB Officer approved for project: " + project.getProjectName());
        } else {
            System.out.println("Registration already processed.");
        }
    }


    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus regStat) {
        this.registrationStatus = regStat;
    }
    

    public void setAssignedProject(BTOProject project) {
        
    }
}