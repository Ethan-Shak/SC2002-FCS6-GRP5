import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportManager {
    private static ReportManager instance;
    
    private ReportManager() {}
    
    public static ReportManager getInstance() {
        if (instance == null) {
            instance = new ReportManager();
        }
        return instance;
    }
    
    public List<BookingReport> generateAllBookings() {
        List<BookingReport> reports = new ArrayList<>();
        Map<String, Flat> bookings = FlatBookingManager.getAllBookings();
        
        for (Map.Entry<String, Flat> entry : bookings.entrySet()) {
            String applicantNRIC = entry.getKey();
            Flat flat = entry.getValue();
            Applicant applicant = ApplicantController.getApplicant(applicantNRIC);
            
            if (applicant != null) {
                reports.add(new BookingReport(applicant, flat));
            }
        }
        
        return reports;
    }
    
    public List<BookingReport> filterByMaritalStatus(MaritalStatus status) {
        List<BookingReport> reports = new ArrayList<>();
        Map<String, Flat> bookings = FlatBookingManager.getAllBookings();
        
        for (Map.Entry<String, Flat> entry : bookings.entrySet()) {
            String applicantNRIC = entry.getKey();
            Flat flat = entry.getValue();
            Applicant applicant = ApplicantController.getApplicant(applicantNRIC);
            
            if (applicant != null && applicant.getMaritalStatus() == status) {
                reports.add(new BookingReport(applicant, flat));
            }
        }
        
        return reports;
    }
    
    public List<BookingReport> filterByFlatType(RoomType type) {
        List<BookingReport> reports = new ArrayList<>();
        Map<String, Flat> bookings = FlatBookingManager.getAllBookings();
        
        for (Map.Entry<String, Flat> entry : bookings.entrySet()) {
            String applicantNRIC = entry.getKey();
            Flat flat = entry.getValue();
            Applicant applicant = ApplicantController.getApplicant(applicantNRIC);
            
            if (flat.getType() == type) {
                reports.add(new BookingReport(applicant, flat));
            }
        }
        
        return reports;
    }
    
    public List<BookingReport> filterByProject(BTOProject project) {
        List<BookingReport> reports = new ArrayList<>();
        Map<String, Flat> bookings = FlatBookingManager.getAllBookings();
        
        for (Map.Entry<String, Flat> entry : bookings.entrySet()) {
            String applicantNRIC = entry.getKey();
            Flat flat = entry.getValue();
            Applicant applicant = ApplicantController.getApplicant(applicantNRIC);
            
            if (flat.getProject().equals(project)) {
                reports.add(new BookingReport(applicant, flat));
            }
        }
        
        return reports;
    }
    
    public List<BookingReport> filterByAgeRange(int minAge, int maxAge) {
        List<BookingReport> reports = new ArrayList<>();
        Map<String, Flat> bookings = FlatBookingManager.getAllBookings();
        
        for (Map.Entry<String, Flat> entry : bookings.entrySet()) {
            String applicantNRIC = entry.getKey();
            Flat flat = entry.getValue();
            Applicant applicant = ApplicantController.getApplicant(applicantNRIC);
            
            if (applicant != null && applicant.getAge() >= minAge && applicant.getAge() <= maxAge) {
                reports.add(new BookingReport(applicant, flat));
            }
        }
        
        return reports;
    }
} 