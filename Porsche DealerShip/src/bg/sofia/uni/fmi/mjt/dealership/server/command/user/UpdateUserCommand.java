package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.EncryptionException;
import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserManager;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public class UpdateUserCommand implements Command {
    private final UserManager userManager;
    private final String oldPassword;
    private final String newPassword;
    private final User user;
    private final Logger logger = Logger.getInstance();

    public UpdateUserCommand(UserManager userManager, String oldPassword, String newPassword, User user) {
        this.userManager = userManager;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        try {
            logger.log("Updating user");
            userManager.updateUser(user.getUsername(), oldPassword, newPassword);
            return new DataContainer("User updated successfully", user.getRole(), user);
        } catch (UserManagerException | EncryptionException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), UserRole.UNAUTHORIZED, user);
        }
    }
}