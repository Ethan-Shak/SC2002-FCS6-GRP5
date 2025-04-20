import java.time.LocalDateTime;

public class Enquiry {
    private int enquiryID;
    private Applicant applicant;
    private BTOProject project;
    private String content;
    private String reply;
    private LocalDateTime timestamp;
    private boolean isEdited;
    private LocalDateTime lastEditedTime;

    public Enquiry(int enquiryID, Applicant applicant, BTOProject project, String content) {
        this.enquiryID = enquiryID;
        this.applicant = applicant;
        this.project = project;
        this.content = content;
        this.reply = null;
        this.timestamp = LocalDateTime.now();
        this.isEdited = false;
        this.lastEditedTime = null;
    }

    public int getEnquiryID() {
        return enquiryID;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public BTOProject getProject() {
        return project;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.isEdited = true;
        this.lastEditedTime = LocalDateTime.now();
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return this.reply;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public LocalDateTime getLastEditedTime() {
        return lastEditedTime;
    }

    @Override
    public String toString() {
        String result = "Enquiry #" + enquiryID + " - " + project.getProjectName() + "\n";
        result += "Content: " + content + "\n";
        result += "Submitted: " + timestamp.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        if (isEdited) {
            result += "\nLast edited: " + lastEditedTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        
        return result;
    }
} 