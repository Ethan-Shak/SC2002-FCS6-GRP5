import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnquiryManager implements IEnquiryManager {
    private static Map<Integer, Enquiry> enquiries = new HashMap<>();
    private static int nextEnquiryID = 1;
    
    // Submit a new enquiry
    @Override
    public Enquiry submitEnquiry(Applicant applicant, BTOProject project, String content) {
        int enquiryID = nextEnquiryID++;
        Enquiry enquiry = new Enquiry(enquiryID, applicant, project, content);
        enquiries.put(enquiryID, enquiry);
        return enquiry;
    }
    
    // Get all enquiries for an applicant
    @Override
    public List<Enquiry> getEnquiriesForApplicant(Applicant applicant) {
        List<Enquiry> applicantEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getApplicant().equals(applicant)) {
                applicantEnquiries.add(enquiry);
            }
        }
        return applicantEnquiries;
    }
    
    // Get all enquiries for a project
    @Override
    public List<Enquiry> getEnquiriesForProject(BTOProject project) {
        List<Enquiry> projectEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getProject().equals(project)) {
                projectEnquiries.add(enquiry);
            }
        }
        return projectEnquiries;
    }
    
    // Get a specific enquiry by ID
    public static Enquiry getEnquiry(int enquiryID) {
        return enquiries.get(enquiryID);
    }
    
    // Update an enquiry
    @Override
    public boolean updateEnquiry(int enquiryID, String newContent) {
        Enquiry enquiry = enquiries.get(enquiryID);
        if (enquiry == null) {
            return false;
        }
        
        enquiry.setContent(newContent);
        return true;
    }
    
    // Delete an enquiry
    @Override
    public boolean deleteEnquiry(int enquiryID) {
        if (!enquiries.containsKey(enquiryID)) {
            return false;
        }
        
        enquiries.remove(enquiryID);
        return true;
    }
    
    // For applicants to get replies to their enquiries
    @Override
    public String getReply(int enquiryId) {
        Enquiry enquiry = enquiries.get(enquiryId);
        if (enquiry == null) {
            return null;
        }

        return enquiry.getReply();
    }
    
    // Get all enquiries
    public static List<Enquiry> getAllEnquiries() {
        return new ArrayList<>(enquiries.values());
    }

    // For officers to reply to enquiries
    @Override
    public boolean replyToEnquiry(int enquiryId, String message) {
        Enquiry enquiry = enquiries.get(enquiryId);
        if (enquiry == null) {
            return false;
        }

        enquiry.setReply(message);
        return true;
    }

} 