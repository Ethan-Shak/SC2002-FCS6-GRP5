public class HDBOfficer extends Applicant  {
    private BTOProject assignedProject;
    private OfficerRegistrationManager officerRegistrationManager;
    private ProjectEnquiryManager projectEnquiryManager;
    private BookingOpsManager bookingOpsManager;
    private RegistrationStatus registrationStatus; // whether registered to project

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.registrationStatus = RegistrationStatus.PENDING; // Default status
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