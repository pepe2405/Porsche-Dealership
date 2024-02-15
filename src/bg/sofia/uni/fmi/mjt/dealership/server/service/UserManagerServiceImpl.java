package bg.sofia.uni.fmi.mjt.dealership.server.service;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.FileRepositoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;
import bg.sofia.uni.fmi.mjt.dealership.server.repository.FileRepository;
import bg.sofia.uni.fmi.mjt.dealership.server.repository.FileRepositoryImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.util.PasswordEncryptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The UserManagerServiceImpl class is an implementation of the UserManagerService interface.
 * It provides methods for managing users in the dealership application.
 * It uses Gson to serialize and deserialize user data to and from JSON format.
 * It maintains a map of users, keyed by username.
 * It provides methods to validate users, retrieve a user by username, create, delete, and update users.
 * It also provides methods to save and load user data from a file.
 */
public class UserManagerServiceImpl implements UserManagerService {
    private static final String FILE_PATH = "users.json";

    private final LoggerImpl logger;
    private final FileRepository<User> usersFileRepository;
    private final Gson gson;

    private Map<String, User> users = new HashMap<>();

    public UserManagerServiceImpl(FileRepository<User> usersFileRepository) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.logger = LoggerImpl.getInstance();
        this.usersFileRepository = usersFileRepository;

        loadFromFile();
    }

    public UserManagerServiceImpl() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.logger = LoggerImpl.getInstance();
        this.usersFileRepository = new FileRepositoryImpl<>(FILE_PATH);

        loadFromFile();
    }

    public Map<String, User> getUsers() {
        return users;
    }

    @Override
    public synchronized void createUser(String username, String password, UserRole role) {
        if (username == null || username.isBlank() ||
            password == null || password.isBlank() ||
            role == null) {
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

    @Override
    public synchronized void deleteUser(String username) {
        if (username == null) {
            throw new UserManagerException("Username cannot be null");
        }

        if (!users.containsKey(username)) {
            throw new UserManagerException("User with this username does not exist");
        }

        users.remove(username);

        saveToFile();
    }

    @Override
    public synchronized void updateUser(String username, String oldPassword, String newPassword) {
        if (username == null || username.isBlank() ||
            oldPassword == null || oldPassword.isBlank() ||
            newPassword == null || newPassword.isBlank()) {
            throw new UserManagerException("Username and new password cannot be null");
        }

        if (!users.containsKey(username)) {
            throw new UserManagerException("User with this username does not exist");
        }

        newPassword = PasswordEncryptor.encryptData(newPassword);
        oldPassword = PasswordEncryptor.encryptData(oldPassword);

        if (!users.get(username).getPassword().equals(oldPassword)) {
            throw new UserManagerException("Old password is incorrect");
        }

        users.get(username).updatePassword(newPassword);
        saveToFile();
    }

    @Override
    public boolean isValidUser(String username, String password) {
        if (username == null || password == null ||
            username.isBlank() || password.isBlank()) {
            throw new UserManagerException("Username and password cannot be null or empty");
        }

        password = PasswordEncryptor.encryptData(password);
        return users.containsKey(username) && users.get(username).getPassword().equals(password);
    }

    @Override
    public User getUser(String username) {
        if (username == null || username.isBlank() || !users.containsKey(username)) {
            throw new UserManagerException("User with this username does not exist");
        }

        return users.get(username);
    }

    private void saveToFile() {
        try {
            usersFileRepository.saveAll(users.values());
        } catch (FileRepositoryException e) {
            logger.logError("Error while saving to file", e);
            System.err.println("Error while saving to file: " + e.getMessage());
        }
    }

    private synchronized void loadFromFile() {
        try {
            users = Collections.synchronizedMap(usersFileRepository.readAllUsers());
        } catch (FileRepositoryException e) {
            logger.logError("Error while loading from file", e);
            System.err.println("Error while loading from file: " + e.getMessage());
        }
    }
}
