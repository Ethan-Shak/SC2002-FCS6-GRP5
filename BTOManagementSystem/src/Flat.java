public class Flat {
    private int flatID;
    private RoomType type; 
    private boolean isAvailable; 
    private Applicant assignedApplicant; 

    public Flat(int flatID, RoomType type) {
        this.flatID = flatID;
        this.type = type;
        this.isAvailable = true;
        this.assignedApplicant = null;
    }

    public void bookFlat(Applicant applicant) {
        if (isAvailable) {
            this.assignedApplicant = applicant;
            this.isAvailable = false;
            System.out.println("Flat " + flatID + " booked successfully.");
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
}