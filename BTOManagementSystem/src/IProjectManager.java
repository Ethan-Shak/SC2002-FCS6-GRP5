import java.util.List;

public interface IProjectManager {
    BTOProject createProject(String projectName, String neighbourhood, RoomType roomType, HDBManager manager);
    void editProject(BTOProject project, String projectName, String neighbourhood, RoomType roomType, HDBManager manager);
    void deleteProject(BTOProject project);
    void setProjectVisibility(BTOProject project, boolean visible);
    List<BTOProject> getAllProjects();
    List<BTOProject> getOwnProjects(HDBManager manager);
}
