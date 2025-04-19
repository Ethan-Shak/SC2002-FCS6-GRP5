import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BTOProject {
    private String projectName;
    private String neighbourhood;
    private RoomType roomType;
    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;
    private boolean isVisible;
    private HDBManager manager;
    private List<HDBOfficer> officers; 
    private List<MaritalStatus> eligibleGroups;
    private Map<RoomType, Integer> flatInventory;
    private List<Flat> flats;

public BTOProject(String projectName, String neighbourhood, RoomType roomType, HDBManager manager, 
                  LocalDate applicationOpeningDate, LocalDate applicationClosingDate) {
    this.projectName = projectName;
    this.neighbourhood = neighbourhood;
    this.roomType = roomType;
    this.manager = manager;
    this.applicationOpeningDate = applicationOpeningDate;
    this.applicationClosingDate = applicationClosingDate;
    this.flats = new ArrayList<>();
    this.officers = new ArrayList<>();
    this.eligibleGroups = new ArrayList<>();
    this.flatInventory = new HashMap<>();
    this.isVisible = true; // True by default
}

    // Initialize flats based on inventory
    public void setFlatInventory(Map<RoomType, Integer> flatInventory) {  
        this.flatInventory = flatInventory;  
        this.flats.clear();  
        int flatID = 1;  

        for (Map.Entry<RoomType, Integer> entry : flatInventory.entrySet()) {  
            RoomType roomType = entry.getKey();  
            int count = entry.getValue();  

            for (int i = 0; i < count; i++) {  
                Flat flat = new Flat(flatID++, roomType); // Price removed
                flat.setProject(this);  
                flats.add(flat);  
            }  
        }  
    }

    // Get available flats
    public List<Flat> getAvailableFlats() {
        List<Flat> availableFlats = new ArrayList<>();
        for (Flat flat : flats) {
            if (flat.isAvailable()) {
                availableFlats.add(flat);
            }
        }
        return availableFlats;
    }

    // Add officer to project
    public boolean addOfficer(HDBOfficer officer) {
        if (officers.size() >= 10) { // Maximum 10 officers allowed
            return false;
        }
        officers.add(officer);
        return true;
    }

    // Remove officer from project
    public void removeOfficer(HDBOfficer officer) {
        officers.remove(officer);
    }

    // Check if project is visible and eligible for applicant
    public boolean checkVisibility(Applicant applicant) {
        if (!isVisible) {
            return false;
        }
        
        if (!eligibleGroups.contains(applicant.getMaritalStatus())) {
            return false;
        }
        
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE) {
            return applicant.getAge() >= 35 && flatInventory.containsKey(RoomType.TWO_ROOM);
        }
        
        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED) {
            return applicant.getAge() >= 21 && !flatInventory.isEmpty();
        }
        
        return false;
    }

    // Get list of officer names for CSV saving
    public List<String> getAssignedOfficerNames() {
        List<String> names = new ArrayList<>();
        for (HDBOfficer officer : officers) {
            names.add(officer.getName());
        }
        return names;
    }

    // Default getters
    public String getProjectName() { return projectName; }
    public String getNeighbourhood() { return neighbourhood; }
    public RoomType getRoomType() { return roomType; }
    public HDBManager getManager() { return manager; }
    public List<Flat> getFlats() { return flats; }
    public List<HDBOfficer> getOfficers() { return officers; }
    public boolean getVisibility() { return isVisible; }
    public LocalDate getApplicationOpeningDate() { return applicationOpeningDate; }
    public LocalDate getApplicationClosingDate() { return applicationClosingDate; }
    public List<MaritalStatus> getEligibleGroups() { return eligibleGroups; }
    public Map<RoomType, Integer> getFlatInventory() { return flatInventory; }
    public int getNumberOfOfficers() {
        return officers.size();
    }
    
    // Default setters
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public void setManager(HDBManager manager) { this.manager = manager; }
    public void setFlats(List<Flat> flats) { this.flats = flats; }
    public void setOfficers(List<HDBOfficer> officers) { this.officers = officers; }
    public void setVisibility(boolean isVisible) { this.isVisible = isVisible; }
    public void setApplicationOpeningDate(LocalDate applicationOpeningDate) { this.applicationOpeningDate = applicationOpeningDate; }
    public void setApplicationClosingDate(LocalDate applicationClosingDate) { this.applicationClosingDate = applicationClosingDate; }
    public void setEligibleGroups(List<MaritalStatus> eligibleGroups) { this.eligibleGroups = eligibleGroups; }
}