public class OfficerRegistrationManager implements IOfficerRegistrationManager {
    @Override
    public void registerAsOfficer(HDBOfficer officer, BTOProject project) { 
        if (isEligibleForRegistration(officer, project)) {
            officer.setRegistrationStatus(RegistrationStatus.PENDING);
            HDBManager projManager = project.getManager();
            projManager.addOfficerProjectApplication(officer, project); 
        } else {
            officer.setRegistrationStatus(RegistrationStatus.REJECTED);
        }
    }

    @Override
    public RegistrationStatus viewRegistrationStatus(HDBOfficer officer, BTOProject project) { // HDBManager will update registrationStatus if they approve it.
        return officer.getRegistrationStatus();
    }

    @Override
    public boolean isEligibleForRegistration(HDBOfficer officer, BTOProject project) { 
        var application = officer.getApplication();
        if (application != null) { // If this officer has an ongoing application we check if its for the project we are registering as an officer for.
            if (application.getProject() == project) {
                return false;
            }
        }

        // If officer has assigned project already, wont be eligible for registration.
        return officer.getAssignedProject() == null;


    }
}