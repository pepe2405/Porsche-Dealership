package bg.sofia.uni.fmi.mjt.dealership.server.model;

import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;

import java.util.Objects;

/**
 * The User class represents a user in the dealership system.
 * It contains details about the user such as username, password, and user role.
 */
public class User {
    private final String username;
    private String password;
    private final UserRole role;

    public User(String username, String password, UserRole role) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
