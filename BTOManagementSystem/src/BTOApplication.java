public class BTOApplication {
    private Applicant applicant;
    private RoomType roomType;
    private BTOProject project;
    private ApplicationStatus applicationStatus;
    private WithdrawalStatus withdrawalStatus;

    public BTOApplication(Applicant applicant, BTOProject project) {
        if (!project.isVisible(applicant)) {
            throw new IllegalArgumentException("Error: Project is not visible to this applicant.");
        }
        this.applicant = applicant;
        this.project = project;
        this.applicationStatus = ApplicationStatus.PENDING;
    }

    // Getters
    public Applicant getApplicant() { return applicant; }
    public RoomType getRoomType() { return roomType; }
    public BTOProject getProject() { return project; }
    public ApplicationStatus getApplicationStatus() { return applicationStatus; }
    public WithdrawalStatus getWithdrawalStatus() { return withdrawalStatus; }

    // Setters
    public void setApplicant(Applicant applicant) { this.applicant = applicant; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public void setProject(BTOProject project) { this.project = project; }
    public void setApplicationStatus(ApplicationStatus applicationStatus) { this.applicationStatus = applicationStatus; }
    public void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) { this.withdrawalStatus = withdrawalStatus; }
}