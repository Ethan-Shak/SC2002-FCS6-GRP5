public class SingpassAccount {
    private String username;
    private String password;

    public SingpassAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public boolean changePassword(String currentPassword, String newPassword) {
        if (!authenticate(currentPassword)) {
            return false;
        }
        this.password = newPassword;
        return true;
    }

    public String getPassword() {
        return this.password;
    }
}