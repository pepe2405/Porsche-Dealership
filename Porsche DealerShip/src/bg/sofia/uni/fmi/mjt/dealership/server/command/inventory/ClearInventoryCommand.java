package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.car.CarInventory;
import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public class ClearInventoryCommand implements Command {
    private final CarInventory carInventory;
    private final User user;
    private final Logger logger = Logger.getInstance();

    public ClearInventoryCommand(CarInventory carInventory, User user) {
        this.carInventory = carInventory;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        carInventory.clear();
        logger.log("Cleared inventory by " + user.getUsername());
        return new DataContainer("Inventory cleared successfully", UserRole.STAFF, user);
    }
}