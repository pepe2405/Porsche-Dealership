package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;

/**
 * The LogoutCommand class implements the Command interface and provides the functionality
 * for logging out a user.
 * It uses an instance of LoggerImpl to log the operation.
 * It provides a method to execute the command which logs out the user and returns a DataContainer
 * with the result of the operation.
 * After the operation, it logs the operation and returns a DataContainer with a success message.
 */
public class LogoutCommand implements Command {
    private final LoggerImpl logger = LoggerImpl.getInstance();

    @Override
    public DataContainer execute() {
        logger.log("User logged out");
        return new DataContainer("User logged out successfully", null);
    }
}
