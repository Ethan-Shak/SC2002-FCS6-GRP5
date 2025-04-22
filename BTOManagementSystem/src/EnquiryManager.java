import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnquiryManager {
    private static Map<Integer, Enquiry> enquiries = new HashMap<>();
    private static int nextEnquiryID = 1;
    
    // Submit a new enquiry
    public static Enquiry submitEnquiry(Applicant applicant, BTOProject project, String content) {
        int enquiryID = nextEnquiryID++;
        Enquiry enquiry = new Enquiry(enquiryID, applicant, project, content);
        enquiries.put(enquiryID, enquiry);
        return enquiry;
    }
    
    // Get all enquiries for an applicant
    public static List<Enquiry> getEnquiriesForApplicant(Applicant applicant) {
        List<Enquiry> applicantEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getApplicant().equals(applicant)) {
                applicantEnquiries.add(enquiry);
            }
        }
        return applicantEnquiries;
    }
    
    // Get all enquiries for a project
    public static List<Enquiry> getEnquiriesForProject(BTOProject project) {
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
    public static boolean updateEnquiry(int enquiryID, String newContent) {
        Enquiry enquiry = enquiries.get(enquiryID);
        if (enquiry == null) {
            return false;
        }
        
        enquiry.setContent(newContent);
        return true;
    }
    
    // Delete an enquiry
    public static boolean deleteEnquiry(int enquiryID) {
        if (!enquiries.containsKey(enquiryID)) {
            return false;
        }
        
        enquiries.remove(enquiryID);
        return true;
    }
    
    // Get all enquiries
    public static List<Enquiry> getAllEnquiries() {
        return new ArrayList<>(enquiries.values());
    }
    
    // Respond to an enquiry
    public static boolean respondToEnquiry(int enquiryID, String response, User responder) {
        Enquiry enquiry = enquiries.get(enquiryID);
        if (enquiry == null) {
            return false;
        }
        
        // Check if responder is authorized (either manager of the project or an officer assigned to it)
        BTOProject project = enquiry.getProject();
        boolean isAuthorized = false;
        
        if (responder instanceof HDBManager) {
            isAuthorized = project.getManager().equals(responder);
        } else if (responder instanceof HDBOfficer) {
            isAuthorized = project.getOfficers().contains(responder);
        }
        
        if (!isAuthorized) {
            return false;
        }
        
        enquiry.setResponse(response, responder);
        return true;
    }
    
    // Get all enquiries for a project that need responses
    public static List<Enquiry> getUnansweredEnquiriesForProject(BTOProject project) {
        List<Enquiry> unansweredEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getProject().equals(project) && !enquiry.hasResponse()) {
                unansweredEnquiries.add(enquiry);
            }
        }
        return unansweredEnquiries;
    }
    
    // Get all enquiries for a project that have responses
    public static List<Enquiry> getAnsweredEnquiriesForProject(BTOProject project) {
        List<Enquiry> answeredEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getProject().equals(project) && enquiry.hasResponse()) {
                answeredEnquiries.add(enquiry);
            }
        }
        return answeredEnquiries;
    }
    
    // Get all enquiries that need responses (for managers to view all)
    public static List<Enquiry> getAllUnansweredEnquiries() {
        List<Enquiry> unansweredEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (!enquiry.hasResponse()) {
                unansweredEnquiries.add(enquiry);
            }
        }
        return unansweredEnquiries;
    }
} 