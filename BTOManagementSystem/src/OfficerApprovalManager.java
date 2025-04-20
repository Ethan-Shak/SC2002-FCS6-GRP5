import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OfficerApprovalManager implements IOfficerApprovalManager {
    private Map<HDBOfficer, BTOProject> pendingOfficerProjectApplications; // Map to store the officer and the corresponding project they are registering for

    public OfficerApprovalManager() {
        pendingOfficerProjectApplications = new HashMap<>();
    }

    public Map<HDBOfficer, BTOProject> getOfficerRegistration(BTOProject project) {
        return pendingOfficerProjectApplications.entrySet().stream()
                .filter(entry -> entry.getValue().equals(project))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    public boolean checkOfficerSlotAvailability(BTOProject project) {
        return project.getNumberOfOfficers() < 10; // 10 is max size 
    }
    
    public void addOfficerProjectApplication(HDBOfficer officer, BTOProject project) { // Only offrs deemed eligible can call this function from OfficerRegistrationManager
        pendingOfficerProjectApplications.put(officer, project);
    }

    public void approveProjectApplication(Map.Entry<HDBOfficer, BTOProject> applicationEntry) {
        HDBOfficer officer = applicationEntry.getKey();
        BTOProject project = applicationEntry.getValue();

        officer.setRegistrationStatus(RegistrationStatus.APPROVED);
        pendingOfficerProjectApplications.remove(officer);
        officer.setAssignedProject(project);
    }

    public void rejectOfficerRegistration(Map.Entry<HDBOfficer, BTOProject> applicationEntry) {
        HDBOfficer officer = applicationEntry.getKey();

        officer.setRegistrationStatus(RegistrationStatus.REJECTED);
        pendingOfficerProjectApplications.remove(officer);
    }

    public void viewPendingOfficerProjectApplications() { 
        System.out.println("Pending Officer Project Registrations:");
        int count = 1;

        for (HDBOfficer officer : pendingOfficerProjectApplications.keySet()) {
            BTOProject project = pendingOfficerProjectApplications.get(officer);
            System.out.println(count + ". Officer: " + officer.getName() + ", Project: " + project.getProjectName());
            count++;
        }
    }
}
