import java.util.Map;

public interface IOfficerApprovalManager {
    Map<HDBOfficer, BTOProject> getOfficerRegistration(BTOProject project); // Returns a list of officers tagged to ONE project.
    void approveProjectApplication(Map.Entry<HDBOfficer, BTOProject> applicationEntry);
    void rejectOfficerRegistration(HDBOfficer officer);
    boolean checkOfficerSlotAvailability(BTOProject project);
}
