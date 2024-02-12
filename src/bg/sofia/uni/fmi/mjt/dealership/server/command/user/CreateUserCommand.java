package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.EncryptionException;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.service.UserManagerServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;

/**
 * The CreateUserCommand class implements the Command interface and provides the functionality
 * for creating a user.
 * It uses an instance of UserManagerServiceImpl to create the user.
 * It uses an instance of LoggerImpl to log the operation.
 * It maintains the username, password and role of the user to be created.
 * It provides a method to execute the command which creates the user and returns a DataContainer
 * with the result of the operation.
 * If the operation is successful, it logs the operation and returns a DataContainer with a success message.
 * If the operation fails due to a UserManagerException or EncryptionException, it logs a warning
 * and returns a DataContainer with an error message.
 */
public class CreateUserCommand implements Command {
    private final LoggerImpl logger = LoggerImpl.getInstance();

    private final UserManagerServiceImpl userManagerServiceImpl;
    private final String username;
    private final String password;
    private final UserRole role;

    public CreateUserCommand(UserManagerServiceImpl userManagerServiceImpl,
                             String username, String password, UserRole role) {
        this.userManagerServiceImpl = userManagerServiceImpl;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public DataContainer execute() {
        try {
            if (role == null) {
                throw new UserManagerException("Invalid role");
            }
            logger.log("Creating user");
            userManagerServiceImpl.createUser(username, password, role);
            User user = userManagerServiceImpl.getUser(username);
            return new DataContainer("User created successfully", user);
        } catch (UserManagerException | EncryptionException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), null);
        }
    }
}
