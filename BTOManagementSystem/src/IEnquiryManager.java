public interface IEnquiryManager {
    void submitEnquiry(Applicant applicant, String message);
    void updateEnquiry(String enquiryId, String newMessage);
    void handleEnquiry(String enquiryId); // applicants will use this to delete their enquiries while officers use it to respond
}