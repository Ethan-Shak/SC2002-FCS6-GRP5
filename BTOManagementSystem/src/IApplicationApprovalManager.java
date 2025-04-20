public interface IApplicationApprovalManager {
    boolean applyForProject(Applicant applicant, BTOProject project, RoomType roomType);
    boolean approveWithdrawal(Applicant applicant);
    boolean approveApplication(Applicant applicant);
    boolean rejectApplication(Applicant applicant);
}
