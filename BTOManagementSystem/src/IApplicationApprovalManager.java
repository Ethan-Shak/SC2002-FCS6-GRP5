public interface IApplicationApprovalManager {
    void approveApplication(Applicant applicant, BTOProject project);
    void rejectApplication(Applicant applicant);
    void approveWithdrawal(Applicant applicant);
    void rejectWithdrawal(Applicant applicant);
}
