public class BTOApplication {
    private Applicant applicant;
    private RoomType roomType;
    private BTOProject project;
    private ApplicationStatus applicationStatus;

    public BTOApplication(Applicant applicant, BTOProject project, RoomType roomType) throws Exception {
        if (!project.isVisible(applicant)) {
            throw new Exception("Project is not visible to the applicant");
        }
        this.applicant = applicant;
        this.project = project;
        this.roomType = roomType;
        this.applicationStatus = ApplicationStatus.PENDING;
    }

    // Getters
    public Applicant getApplicant() { return applicant; }
    public RoomType getRoomType() { return roomType; }
    public BTOProject getProject() { return project; }
    public ApplicationStatus getApplicationStatus() { return applicationStatus; }

    // Setters
    public void setApplicant(Applicant applicant) { this.applicant = applicant; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public void setProject(BTOProject project) { this.project = project; }
    public void setApplicationStatus(ApplicationStatus applicationStatus) { this.applicationStatus = applicationStatus; }
}