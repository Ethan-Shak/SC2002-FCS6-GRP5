import java.util.HashMap;
import java.util.Map;

public class FlatBookingManager implements IFlatBookingManager {
    // Map to track bookings by applicant NRIC
    private static Map<String, Flat> bookings = new HashMap<>();
    
    @Override
    public boolean bookFlat(Applicant applicant, HDBOfficer officer, Flat flat) {
        // Check if applicant has a successful application
        BTOApplication application = applicant.getApplication();
        if (application == null) {
            System.out.println("Error: Applicant does not have an active application.");
            return false;
        }
        
        // Check if application is successful
        if (application.getApplicationStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("Error: Application is not in SUCCESSFUL status.");
            return false;
        }
        
        // Check if applicant already has a booking
        if (bookings.containsKey(applicant.getNRIC())) {
            System.out.println("Error: Applicant already has a booking.");
            return false;
        }
        
        // Check if officer is assigned to the project
        BTOProject project = application.getProject();
        if (!project.getOfficers().contains(officer)) {
            System.out.println("Error: Officer is not assigned to this project.");
            return false;
        }
        
        // Check if flat belongs to the project
        if (!project.getFlats().contains(flat)) {
            System.out.println("Error: Flat does not belong to this project.");
            return false;
        }
        
        // Check if flat is available
        if (!flat.isAvailable()) {
            System.out.println("Error: Flat is not available.");
            return false;
        }
        
        // Check if flat type matches the applicant's chosen room type
        if (flat.getType() != application.getRoomType()) {
            System.out.println("Error: Flat type does not match the applicant's chosen room type.");
            return false;
        }
        
        // Book the flat
        flat.bookFlat(applicant);
        bookings.put(applicant.getNRIC(), flat);
        
        // Update application status to BOOKED
        application.setApplicationStatus(ApplicationStatus.BOOKED);
        
        // Update flat inventory based on available flats
        Map<RoomType, Integer> flatInventory = new HashMap<>();
        for (Flat f : project.getFlats()) {
            if (f.isAvailable()) {
                RoomType type = f.getType();
                flatInventory.put(type, flatInventory.getOrDefault(type, 0) + 1);
            }
        }
        project.setFlatInventory(flatInventory);
        
        return true;
    }
    
    // Method to get a booking by applicant NRIC
    public static Flat getBooking(String applicantNRIC) {
        return bookings.get(applicantNRIC);
    }
    
    // Method to get all bookings
    public static Map<String, Flat> getAllBookings() {
        return new HashMap<>(bookings);
    }
} 