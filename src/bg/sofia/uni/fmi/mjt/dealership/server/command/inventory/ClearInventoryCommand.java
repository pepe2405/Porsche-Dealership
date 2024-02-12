package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;

/**
 * The ClearInventoryCommand class implements the Command interface and provides the functionality
 * for clearing the car inventory.
 * It uses an instance of CarInventoryServiceImpl to clear the inventory.
 * It uses an instance of LoggerImpl to log the operation.
 * It maintains the user who performs the operation.
 * It provides a method to execute the command which clears the inventory and returns a DataContainer
 * with the result of the operation.
 * After the operation, it logs the operation and returns a DataContainer with a success message.
 */
public class ClearInventoryCommand implements Command {
    private final LoggerImpl logger = LoggerImpl.getInstance();

    private final CarInventoryServiceImpl carInventory;
    private final User user;

    public ClearInventoryCommand(CarInventoryServiceImpl carInventory, User user) {
        this.carInventory = carInventory;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        carInventory.clear();
        logger.log("Cleared inventory by " + user.getUsername());
        return new DataContainer("Inventory cleared successfully", user);
    }
}