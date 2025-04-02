public interface IEnquiryManager {
    void submitEnquiry(Applicant applicant, String message);
    void updateEnquiry(String enquiryId, String newMessage);
}