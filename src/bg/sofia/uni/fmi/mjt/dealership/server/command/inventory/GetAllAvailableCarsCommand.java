package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;

/**
 * The GetAllAvailableCarsCommand class implements the Command interface and provides the functionality
 * for getting all the available cars from the inventory.
 * It uses an instance of CarInventoryServiceImpl to get all the available cars from the inventory.
 * It uses an instance of LoggerImpl to log the operation.
 * It maintains the user who performs the operation.
 * It provides a method to execute the command which gets all the available cars from the
 * inventory and returns a DataContainer with the result of the operation.
 * It logs the operation and returns a DataContainer with the available cars.
 */
public class GetAllAvailableCarsCommand implements Command {
    private final LoggerImpl logger = LoggerImpl.getInstance();

    private final CarInventoryServiceImpl carInventory;
    private final User user;

    public GetAllAvailableCarsCommand(CarInventoryServiceImpl carInventory, User user) {
        this.carInventory = carInventory;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        logger.log("Showing all the available cars");
        return new DataContainer(carInventory.getAllAvailableCars().toString(), user);
    }
}
