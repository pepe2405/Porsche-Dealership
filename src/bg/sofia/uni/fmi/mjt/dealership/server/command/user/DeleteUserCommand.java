package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.service.UserManagerServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;

/**
 * The DeleteUserCommand class implements the Command interface and provides the functionality
 * for deleting a user.
 * It uses an instance of UserManagerServiceImpl to delete the user.
 * It uses an instance of LoggerImpl to log the operation.
 * It maintains the user to be deleted.
 * It provides a method to execute the command which deletes the user and returns a DataContainer
 * with the result of the operation.
 * If the operation is successful, it logs the operation and returns a DataContainer with a success message.
 * If the operation fails due to a UserManagerException, it logs a warning
 * and returns a DataContainer with an error message.
 */
public class DeleteUserCommand implements Command {
    private final UserManagerServiceImpl userManagerServiceImpl;
    private final User user;
    private final LoggerImpl logger = LoggerImpl.getInstance();

    public DeleteUserCommand(UserManagerServiceImpl userManagerServiceImpl, User user) {
        this.userManagerServiceImpl = userManagerServiceImpl;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        try {
            logger.log("Deleting user");
            userManagerServiceImpl.deleteUser(user.getUsername());
            return new DataContainer("User deleted successfully", null);
        } catch (UserManagerException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), user);
        }
    }
}