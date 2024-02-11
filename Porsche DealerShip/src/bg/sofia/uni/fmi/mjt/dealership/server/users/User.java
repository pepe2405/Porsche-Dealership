package bg.sofia.uni.fmi.mjt.dealership.server.users;

public class User {
    private final String username;
    private String password;
    private final UserRole role;

    User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }
}
