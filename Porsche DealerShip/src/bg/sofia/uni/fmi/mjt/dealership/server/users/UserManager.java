package bg.sofia.uni.fmi.mjt.dealership.server.users;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.EncryptionException;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerFileException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String FILE_PATH = "users.csv";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Logger logger = Logger.getInstance();
    private final UsersFileHandler usersFileHandler = new UsersFileHandler(FILE_PATH);

    private Map<String, User> users;

    public UserManager() {
        this.users = new HashMap<>();
        loadFromFile();
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void saveToFile() {
        try {
            usersFileHandler.saveToFile(users);
        } catch (UserManagerFileException e) {
            logger.logError("Error while saving to file", e);
            System.err.println("Error while saving to file: " + e.getMessage());
        }
    }

    public synchronized void loadFromFile() {
        try {
            users = usersFileHandler.loadFromFile();
        } catch (UserManagerFileException e) {
            logger.logError("Error while loading from file", e);
            System.err.println("Error while loading from file: " + e.getMessage());
        }
    }

    public boolean isValidUser(String username, String password) throws UserManagerException, EncryptionException {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new UserManagerException("Username and password cannot be null or empty");
        }

        password = PasswordEncryptor.encryptData(password);
        return users.containsKey(username) && users.get(username).getPassword().equals(password);
    }

    public User getUser(String username) throws UserManagerException {
        if (username == null || username.isBlank() || !users.containsKey(username)) {
            throw new UserManagerException("User with this username does not exist");
        }

        return users.get(username);
    }

    public synchronized void createUser(String username, String password, UserRole role)
        throws UserManagerException, EncryptionException {
        if (username == null || password == null || role == null) {
            throw new UserManagerException("Username, password and role cannot be null");
        }

        if (users.containsKey(username)) {
            throw new UserManagerException("User with this username already exists");
        }

        password = PasswordEncryptor.encryptData(password);

        switch (role) {
            case CUSTOMER -> users.put(username, new User(username, password, UserRole.CUSTOMER));
            case STAFF -> users.put(username, new User(username, password, UserRole.STAFF));
        }

        saveToFile();
    }

    public synchronized void deleteUser(String username) throws UserManagerException {
        if (username == null) {
            throw new UserManagerException("Username cannot be null");
        }

        if (!users.containsKey(username)) {
            throw new UserManagerException("User with this username does not exist");
        }

        users.remove(username);

        saveToFile();
    }

    public synchronized void updateUser(String username, String oldPassword, String newPassword)
        throws UserManagerException, EncryptionException {
        if (username == null || username.isBlank() ||
            oldPassword == null || oldPassword.isBlank() ||
            newPassword == null || newPassword.isBlank()) {
            throw new UserManagerException("Username and new password cannot be null");
        }

        if (!users.containsKey(username)) {
            throw new UserManagerException("User with this username does not exist");
        }

        newPassword = PasswordEncryptor.encryptData(newPassword);

        if (!users.get(username).getPassword().equals(oldPassword)) {
            throw new UserManagerException("Old password is incorrect");
        }

        users.get(username).updatePassword(newPassword);
        saveToFile();
    }
}
