package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.EncryptionException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.service.UserManagerServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;

/**
 * The LoginCommand class implements the Command interface and provides the functionality
 * for logging in a user.
 * It uses an instance of UserManagerServiceImpl to log in the user.
 * It uses an instance of LoggerImpl to log the operation.
 * It maintains the username and password of the user to be logged in.
 * It provides a method to execute the command which logs in the user and returns a DataContainer
 * with the result of the operation.
 * If the operation is successful, it logs the operation and returns a DataContainer with a success message.
 * If the operation fails due to a UserManagerException or EncryptionException, it logs a warning
 * and returns a DataContainer with an error message.
 */
public class LoginCommand implements Command {
    private final LoggerImpl logger = LoggerImpl.getInstance();

    private final UserManagerServiceImpl userManagerServiceImpl;
    private final String username;
    private final String password;

    public LoginCommand(UserManagerServiceImpl userManagerServiceImpl, String username, String password) {
        this.userManagerServiceImpl = userManagerServiceImpl;
        this.username = username;
        this.password = password;
    }

    @Override
    public DataContainer execute() {
        try {
            User user = userManagerServiceImpl.getUser(username);
            if (userManagerServiceImpl.isValidUser(username, password)) {
                logger.log("User logged in");
                return new DataContainer("User logged in successfully", user);
            } else {
                logger.logWarning("Invalid username or password");
                return new DataContainer("Invalid username or password", null);
            }
        } catch (UserManagerException | EncryptionException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), null);
        }
    }
}
