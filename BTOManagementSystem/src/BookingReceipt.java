import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingReceipt {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private String receiptID;
    private LocalDateTime bookingDateTime;
    private Applicant applicant;
    private Flat flat;
    private BTOProject project;
    private HDBOfficer officer;
    
    public BookingReceipt(Applicant applicant, Flat flat, BTOProject project, HDBOfficer officer) {
        this.receiptID = generateReceiptID();
        this.bookingDateTime = LocalDateTime.now();
        this.applicant = applicant;
        this.flat = flat;
        this.project = project;
        this.officer = officer;
    }
    
    private String generateReceiptID() {
        // Generate a unique receipt ID using timestamp
        return "RCP" + System.currentTimeMillis();
    }
    
    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("\n=== HDB Flat Booking Receipt ===\n");
        receipt.append("Receipt ID: ").append(receiptID).append("\n");
        receipt.append("Booking Date: ").append(bookingDateTime.format(DATE_FORMATTER)).append("\n\n");
        
        // Applicant Details
        receipt.append("Applicant Details:\n");
        receipt.append("  Name: ").append(applicant.getName()).append("\n");
        receipt.append("  NRIC: ").append(applicant.getNRIC()).append("\n");
        receipt.append("  Age: ").append(applicant.getAge()).append("\n");
        receipt.append("  Marital Status: ").append(applicant.getMaritalStatus()).append("\n\n");
        
        // Project Details
        receipt.append("Project Details:\n");
        receipt.append("  Project Name: ").append(project.getProjectName()).append("\n");
        receipt.append("  Neighborhood: ").append(project.getNeighbourhood()).append("\n");
        receipt.append("  Flat Type: ").append(flat.getType()).append("\n");
        receipt.append("  Flat ID: ").append(flat.getFlatID()).append("\n\n");
        
        // Officer Details
        receipt.append("Booking Officer:\n");
        receipt.append("  Name: ").append(officer.getName()).append("\n");
        receipt.append("  NRIC: ").append(officer.getNRIC()).append("\n");
        
        return receipt.toString();
    }
    
    // Getters
    public String getReceiptID() { return receiptID; }
    public LocalDateTime getBookingDateTime() { return bookingDateTime; }
    public Applicant getApplicant() { return applicant; }
    public Flat getFlat() { return flat; }
    public BTOProject getProject() { return project; }
    public HDBOfficer getOfficer() { return officer; }
} 