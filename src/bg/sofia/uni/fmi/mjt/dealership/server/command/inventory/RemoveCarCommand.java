package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;

/**
 * The RemoveCarCommand class implements the Command interface and provides the functionality
 * for removing a car from the inventory.
 * It uses an instance of CarInventoryServiceImpl to remove the car from the inventory.
 * It uses an instance of LoggerImpl to log the operation.
 * It maintains the VIN of the car to be removed and the user who performs the operation.
 * It provides a method to execute the command which removes the car from the
 * inventory and returns a DataContainer with the result of the operation.
 * If the operation is successful, it logs the operation and returns a DataContainer with a success message.
 * If the operation fails due to a CarInventoryException, it logs a warning
 * and returns a DataContainer with an error message.
 */
public class RemoveCarCommand implements Command {
    private final CarInventoryServiceImpl carInventory;
    private final String vin;
    private final User user;
    private final LoggerImpl logger = LoggerImpl.getInstance();

    public RemoveCarCommand(CarInventoryServiceImpl carInventory, String vin, User user) {
        this.carInventory = carInventory;
        this.user = user;
        this.vin = vin;
    }

    @Override
    public DataContainer execute() {
        try {
            logger.log("Removing car " + vin + " by " + user.getUsername());
            carInventory.removeCar(vin);
            return new DataContainer("Removing car" , user);
        } catch (CarInventoryException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), user);
        }
    }
}
