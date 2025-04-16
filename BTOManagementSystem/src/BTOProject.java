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

    public String getProjectName() {
        return projectName;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public boolean isVisible(Applicant applicant) {
        if (!isVisible) {
            return false;
        }
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE && applicant.getAge() >= 35 && roomType == RoomType.TWO_ROOM) {
            return true;
        }
        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED && applicant.getAge() >= 21) {
            return true;
        }
        return false;
    }
}