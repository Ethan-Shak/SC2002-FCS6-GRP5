public interface IApplicationApprovalManager {
    boolean applyForProject(Applicant applicant, BTOProject project);
    boolean withdrawApplication(Applicant applicant, BTOProject project);
}
