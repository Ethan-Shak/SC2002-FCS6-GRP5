public class BookingReport {
    private final Applicant applicant;
    private final Flat flat;
    
    public BookingReport(Applicant applicant, Flat flat) {
        this.applicant = applicant;
        this.flat = flat;
    }
    
    public String formatForDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nApplicant: ").append(applicant.getName())
          .append(" (NRIC: ").append(applicant.getNRIC()).append(")");
        sb.append("\nAge: ").append(applicant.getAge());
        sb.append("\nMarital Status: ").append(applicant.getMaritalStatus());
        sb.append("\nProject: ").append(flat.getProject().getProjectName());
        sb.append("\nFlat Type: ").append(flat.getType());
        return sb.toString();
    }
    
    // Getters for potential future use
    public Applicant getApplicant() {
        return applicant;
    }
    
    public Flat getFlat() {
        return flat;
    }
} 