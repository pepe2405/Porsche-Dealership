package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.EncryptionException;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserManager;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public class CreateUserCommand implements Command {
    private final UserManager userManager;
    private final String username;
    private final String password;
    private final UserRole role;
    private final Logger logger = Logger.getInstance();

    public CreateUserCommand(UserManager userManager, String username, String password, UserRole role) {
        this.userManager = userManager;
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
            userManager.createUser(username, password, role);
            User user = userManager.getUser(username);
            return new DataContainer("User created successfully", role, user);
        } catch (UserManagerException | EncryptionException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), UserRole.UNAUTHORIZED, null);
        }
    }
}
