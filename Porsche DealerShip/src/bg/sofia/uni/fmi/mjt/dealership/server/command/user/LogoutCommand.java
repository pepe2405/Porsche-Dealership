package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public class LogoutCommand implements Command {
    private final Logger logger = Logger.getInstance();

    @Override
    public DataContainer execute() {
        logger.log("User logged out");
        return new DataContainer("User logged out successfully", UserRole.UNAUTHORIZED, null);
    }
}
