public class HDBOfficer extends Applicant {
    private BTOProject assignedProject; // Officers can always access full project details, even when visibility is turned off.
    private RegistrationStatus registrationStatus; // whether registered to project
    
    public OfficerRegistrationManager officerRegistrationManager; // For registering for BTOProject
    public EnquiryManager enquiryManager; 
    public BookingOpsManager bookingOpsManager; // For managing flat booking requests by Applicants.

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
        this.registrationStatus = RegistrationStatus.PENDING; // Default status
        this.assignedProject = null;
        this.officerRegistrationManager = new OfficerRegistrationManager();
        this.enquiryManager = new EnquiryManager();
        this.bookingOpsManager = new BookingOpsManager();
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

    public BTOProject getAssignedProject() {
        return this.assignedProject;
    }

    public void setRegistrationStatus(RegistrationStatus regStat) {
        this.registrationStatus = regStat;
    }

    public void setAssignedProject(BTOProject project) {
        this.assignedProject = project;
    }
}