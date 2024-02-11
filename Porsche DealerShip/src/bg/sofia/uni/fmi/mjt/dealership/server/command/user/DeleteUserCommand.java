package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserManager;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public class DeleteUserCommand implements Command {
    private final UserManager userManager;
    private final User user;
    private final Logger logger = Logger.getInstance();

    public DeleteUserCommand(UserManager userManager, User user) {
        this.userManager = userManager;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        try {
            logger.log("Deleting user");
            userManager.deleteUser(user.getUsername());
            return new DataContainer("User deleted successfully", UserRole.UNAUTHORIZED, null);
        } catch (UserManagerException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), UserRole.UNAUTHORIZED, user);
        }
    }
}