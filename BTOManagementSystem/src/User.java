public class User {
    private String name;
    private String nric;
    private int age;
    private MaritalStatus maritalStatus;
    private String password;
    private SingpassAccount singpassAccount;

    public User(String name, String nric, int age, MaritalStatus maritalStatus) {
        if (!validateNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC format! It must start with S or T, followed by 7 digits and an uppercase letter.");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be a positive value.");
        }
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = "password";
        this.singpassAccount = new SingpassAccount(nric, password);
    }

    public String getName() {
        return name;
    }

    public String getNRIC() {
        return nric;
    }

    public int getAge() {
        return age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public SingpassAccount getSingpassAccount() {
        return singpassAccount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be a positive value.");
        }
        this.age = age;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }
        this.password = newPassword;
    }

    private boolean validateNRIC(String nric) {
        return nric.matches("^[ST]\\d{7}[A-Z]$");
    }

    @Override
    public String toString() {
        return String.format("Name: %s, NRIC: %s, Age: %d, Marital Status: %s", 
            name, nric, age, maritalStatus);
    }
}