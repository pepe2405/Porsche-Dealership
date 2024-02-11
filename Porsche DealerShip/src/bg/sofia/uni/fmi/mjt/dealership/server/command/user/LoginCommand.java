package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.EncryptionException;
import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserManager;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public class LoginCommand implements Command {
    private final UserManager userManager;
    private final String username;
    private final String password;
    private final Logger logger = Logger.getInstance();

    public LoginCommand(UserManager userManager, String username, String password) {
        this.userManager = userManager;
        this.username = username;
        this.password = password;
    }

    @Override
    public DataContainer execute() {
        try {
            User user = userManager.getUser(username);
            if (userManager.isValidUser(username, password)) {
                logger.log("User logged in");
                return new DataContainer("User logged in successfully", user.getRole(), user);
            } else {
                logger.logWarning("Invalid username or password");
                return new DataContainer("Invalid username or password", UserRole.UNAUTHORIZED, null);
            }
        } catch (UserManagerException | EncryptionException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), UserRole.UNAUTHORIZED, null);
        }
    }
}
