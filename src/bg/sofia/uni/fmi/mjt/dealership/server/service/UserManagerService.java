package bg.sofia.uni.fmi.mjt.dealership.server.service;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;
import java.util.Map;

/**
 * The UserManagerService interface provides methods for managing users.
 * It allows validating users, retrieving a user by username, creating, deleting, and updating users.
 */
public interface UserManagerService {
    /**
     * Retrieves all users.
     *
     * @return a map of users, keyed by username
     */
    Map<String, User> getUsers();

    /**
     * Validates a user based on username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return true if the user is valid, false otherwise
     * @throws UserManagerException if the username or password is null or empty
     */
    boolean isValidUser(String username, String password) throws IllegalArgumentException;

    /**
     * Retrieves a user by username.
     *
     * @param username the username of the user
     * @return the user with the specified username
     * @throws UserManagerException if the username is null or empty
     */
    User getUser(String username) throws IllegalArgumentException;

    /**
     * Creates a new user.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @param role the role of the new user
     * @throws UserManagerException if the username, password or role is null or empty
     */
    void createUser(String username, String password, UserRole role) throws IllegalArgumentException;

    /**
     * Deletes a user by username.
     *
     * @param username the username of the user to be deleted
     * @throws UserManagerException if the username is null or empty
     */
    void deleteUser(String username) throws IllegalArgumentException;

    /**
     * Updates a user's password.
     *
     * @param username the username of the user
     * @param oldPassword the old password of the user
     * @param newPassword the new password of the user
     * @throws UserManagerException if the username, oldPassword or newPassword is null or empty
     */
    void updateUser(String username, String oldPassword, String newPassword) throws IllegalArgumentException;
}