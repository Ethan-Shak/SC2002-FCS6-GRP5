import java.util.Map;

public interface IOfficerApprovalManager {
    Map<HDBOfficer, BTOProject> getOfficerRegistration(BTOProject project); // Returns a list of officers tagged to ONE project.
    void approveProjectApplication(Map.Entry<HDBOfficer, BTOProject> applicationEntry);
    void rejectOfficerRegistration(Map.Entry<HDBOfficer, BTOProject> applicationEntry);
    boolean checkOfficerSlotAvailability(BTOProject project);
}
