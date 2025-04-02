public class User {
    private String name;
    private String nric;
    private int age;
    private MaritalStatus maritalStatus;
    private String password;
    private SingpassAccount singpassAccount;

    public User(String name, String nric, int age, MaritalStatus maritalStatus) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = "password";
        this.singpassAccount = new SingpassAccount(nric, password);
    }

    public String getName() { return name; }
    public String getNRIC() { return nric; }
    public int getAge() { return age; }
    public MaritalStatus getMaritalStatus() { return maritalStatus; }
    public SingpassAccount getSingpassAccount() { return singpassAccount; }
}
