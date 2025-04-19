import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BTOProject {
    private String projectName;
    private String neighbourhood;
    private RoomType roomType;
    private LocalDateTime applicationOpeningDate;
    private LocalDateTime applicationClosingDate;
    private boolean isVisible;
    private HDBManager manager;
    private List<HDBOfficer> officers; //
    private List<MaritalStatus> eligibleGroups;
    private Map<RoomType, Integer> flatInventory;
    private List<Flat> flats;

    public BTOProject(String projectName, String neighbourhood, RoomType roomType, HDBManager manager) {
        this.projectName = projectName;
        this.neighbourhood = neighbourhood;
        this.roomType = roomType;
        this.manager = manager;
        this.flats = new ArrayList<>();
        this.officers = new ArrayList<>();
        this.eligibleGroups = new ArrayList<>();
        this.flatInventory = new HashMap<>();
        this.isVisible = true; // True by default
        
        // Initialize flats based on inventory
        int flatID = 1;
        for (Map.Entry<RoomType, Integer> entry : flatInventory.entrySet()) {
            RoomType type = entry.getKey();
            int count = entry.getValue();
            
            for (int i = 0; i < count; i++) {
                flats.add(new Flat(flatID++, type));
            }
        }
    }

    public List<Flat> getAvailableFlats() {
        List<Flat> availableFlats = new ArrayList<>();
        for (Flat flat : flats) {
            if (flat.isAvailable()) {
                availableFlats.add(flat);
            }
        }
        return availableFlats;
    }

    public boolean addOfficer(HDBOfficer offr) { // returns false if add fails
        if (officers.size() >= 10) { // check if project already has 10.
            return false;
        }
        officers.add(offr);
        return true;
    }

    public void removeOfficer(HDBOfficer offr) {
        officers.remove(offr);
    }

    public boolean checkVisibility(Applicant applicant) {
        // If project is not visible, return false
        if (!isVisible) {
            return false;
        }
        
        // Check if the applicant's marital status is in the eligible groups
        if (!eligibleGroups.contains(applicant.getMaritalStatus())) {
            return false;
        }
        
        // Singles, 35 years old and above, can ONLY apply for 2-Room
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE) {
            return applicant.getAge() >= 35 && flatInventory.containsKey(RoomType.TWO_ROOM);
        }
        
        // Married, 21 years old and above, can apply for any flat types
        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED) {
            return applicant.getAge() >= 21 && !flatInventory.isEmpty();
        }
        
        return false;
    }

    public int getNumberOfOfficers() {
        return officers.size(); 
    }

    // Default getters
    public String getProjectName() { return projectName; }
    public String getNeighbourhood() { return neighbourhood; }
    public RoomType getRoomType() { return roomType; }
    public HDBManager getManager() { return manager; }
    public List<Flat> getFlats() { return flats; }
    public List<HDBOfficer> getOfficers() { return officers; }
    public boolean getVisibility() { return isVisible; }
    public LocalDateTime getApplicationOpeningDate() { return applicationOpeningDate; }
    public LocalDateTime getApplicationClosingDate() { return applicationClosingDate; }
    public List<MaritalStatus> getEligibleGroups() { return eligibleGroups; }
    public Map<RoomType, Integer> getFlatInventory() { return flatInventory; }

    // Default setters
    public void setProjectName(String projectName) { this.projectName =  projectName; }
    public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public void setManager(HDBManager manager) { this.manager = manager; }
    public void setFlats(List<Flat> flats) { this.flats = flats; }
    public void setOfficers(List<HDBOfficer> officers) { this.officers = officers; }
    public void setVisibility(boolean isVisible) { this.isVisible = isVisible; }
    public void setApplicationOpeningDate(LocalDateTime applicationOpeningDate) { this.applicationOpeningDate = applicationOpeningDate; }
    public void setApplicationClosingDate(LocalDateTime applicationClosingDate) { this.applicationClosingDate = applicationClosingDate; }
    public void setEligibleGroups(List<MaritalStatus> eligibleGroups) { this.eligibleGroups = eligibleGroups; }
    public void setFlatInventory(Map<RoomType, Integer> flatInventory) { 
        this.flatInventory = flatInventory;
        
        // Initialize flats based on inventory
        this.flats.clear();
        int flatID = 1;
        
        for (Map.Entry<RoomType, Integer> entry : flatInventory.entrySet()) {
            RoomType roomType = entry.getKey();
            int count = entry.getValue();
            
            for (int i = 0; i < count; i++) {
                flats.add(new Flat(flatID++, roomType));
            }
        }
    }
}
