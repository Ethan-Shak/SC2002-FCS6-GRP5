import java.util.List;

public interface IEnquiryManager {
    // For applicants
    Enquiry submitEnquiry(Applicant applicant, BTOProject project, String content);
    boolean updateEnquiry(int enquiryId, String newMessage);
    boolean deleteEnquiry(int enquiryId); // APPLICANTS will use this to DELETE their enquiries
    List<Enquiry> getEnquiriesForApplicant(Applicant applicant); // For APPLICANT to use
    String getReply(int enquiryId); // For APPLICANTS to view replies to their enqueries

    // For officers
    boolean replyToEnquiry(int enquiryId, String message); // Officers will use this to REPLY to enquiries
    List<Enquiry> getEnquiriesForProject(BTOProject project); // For OFFICER to use
}