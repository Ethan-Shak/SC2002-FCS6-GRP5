public class SingpassAccount {
    private String username; // Username is NRIC
    private String password;

    public SingpassAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public void resetPassword(String newPassword) {
        this.password = newPassword;
    }
}