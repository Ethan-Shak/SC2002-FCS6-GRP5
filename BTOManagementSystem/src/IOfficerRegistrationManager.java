public interface IOfficerRegistrationManager {
    void registerAsOfficer(HDBOfficer officer, BTOProject project);
    RegistrationStatus viewRegistrationStatus(HDBOfficer officer, BTOProject project);
    boolean isEligibleForRegistration(HDBOfficer officer, BTOProject project);
}
