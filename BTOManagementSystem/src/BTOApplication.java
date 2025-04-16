public class BTOApplication {
    private Applicant applicant;
    private BTOProject project;
    private ApplicationStatus status;

    public BTOApplication(Applicant applicant, BTOProject project) {
        if (!project.isVisible(applicant)) {
            throw new IllegalArgumentException("Error: Project is not visible to this applicant.");
        }
        this.applicant = applicant;
        this.project = project;
        this.status = ApplicationStatus.PENDING;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public BTOProject getProject() {
        return project;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void updateStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
    }
}