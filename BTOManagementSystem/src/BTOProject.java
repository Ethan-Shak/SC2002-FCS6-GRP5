import java.util.ArrayList;
import java.util.List;

public class BTOProject {
    private String projectName;
    private String neighbourhood;
    private RoomType roomType;
    private HDBManager manager;
    private List<Flat> flats;
    private boolean isVisible;

    public BTOProject(String projectName, String neighbourhood, RoomType roomType, HDBManager manager) {
        this.projectName = projectName;
        this.neighbourhood = neighbourhood;
        this.roomType = roomType;
        this.manager = manager;
        this.flats = new ArrayList<>();
        this.isVisible = true;
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

    // Existing getters
    public String getNeighbourhood() {
        return neighbourhood;
    }

    public RoomType getRoomTypes() {
        return roomType;
    }

    public String getProjectName() {
        return projectName;
    }

    public void toggleVisibility(boolean status) {
        this.isVisible = status;
    }
}