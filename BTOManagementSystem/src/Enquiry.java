import java.time.LocalDateTime;

public class Enquiry {
    private int enquiryID;
    private Applicant applicant;
    private BTOProject project;
    private String content;
    private LocalDateTime timestamp;
    private boolean isEdited;
    private LocalDateTime lastEditedTime;
    private String response;
    private User responder;
    private LocalDateTime responseTime;

    public Enquiry(int enquiryID, Applicant applicant, BTOProject project, String content) {
        this.enquiryID = enquiryID;
        this.applicant = applicant;
        this.project = project;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isEdited = false;
        this.lastEditedTime = null;
        this.response = null;
        this.responder = null;
        this.responseTime = null;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public LocalDateTime getLastEditedTime() {
        return lastEditedTime;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response, User responder) {
        this.response = response;
        this.responder = responder;
        this.responseTime = LocalDateTime.now();
    }

    public User getResponder() {
        return responder;
    }

    public LocalDateTime getResponseTime() {
        return responseTime;
    }

    public boolean hasResponse() {
        return response != null;
    }

    @Override
    public String toString() {
        String result = "Enquiry #" + enquiryID + " - " + project.getProjectName() + "\n";
        result += "Content: " + content + "\n";
        result += "Submitted: " + timestamp.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        if (isEdited) {
            result += "\nLast edited: " + lastEditedTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        
        if (hasResponse()) {
            result += "\n\nResponse from " + responder.getName() + " (" + responder.getClass().getSimpleName() + "):";
            result += "\n" + response;
            result += "\nResponded: " + responseTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        
        return result;
    }
} 