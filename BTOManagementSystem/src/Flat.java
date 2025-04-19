public class Flat {
    private int flatID;
    private RoomType type;
    private double price;
    private boolean isAvailable; 
    private Applicant assignedApplicant; 
    private BTOProject project;

    public Flat(int flatID, RoomType type) {
        this.flatID = flatID;
        this.type = type;
        this.isAvailable = true;
        this.assignedApplicant = null;
    }

    public void setProject(BTOProject project) {
        this.project = project;
    }

    public BTOProject getProject() {
        return project;
    }

    public void bookFlat(Applicant applicant) {
        if (isAvailable) {
            this.assignedApplicant = applicant;
            this.isAvailable = false;
        } else {
            System.out.println("Flat " + flatID + " is already booked.");
        }
    }

    public Applicant getAssignedApplicant() {
        return assignedApplicant;
    }

    public void releaseFlat() {
        if (!isAvailable && assignedApplicant != null) {
            assignedApplicant = null;
            isAvailable = true;
            System.out.println("Flat " + flatID + " is now available.");
        } else {
            System.out.println("Flat " + flatID + " was not booked.");
        }
    }

    public int getFlatID() {
        return flatID;
    }

    public RoomType getType() {
        return type;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
    
    public RoomType getRoomType() { return type; }
    public double getPrice() {return price;}
}