package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;

/**
 * The GetCarByVinCommand class implements the Command interface and provides the functionality
 * for getting a car by vin from the inventory.
 * It uses an instance of CarInventoryServiceImpl to get the car by vin from the inventory.
 * It uses an instance of LoggerImpl to log the operation.
 * It maintains the vin of the car to be retrieved and the user who performs the operation.
 * It provides a method to execute the command which gets the car by vin from the
 * inventory and returns a DataContainer with the result of the operation.
 * If the operation is successful, it logs the operation and returns a DataContainer with the car.
 * If the operation fails due to a CarInventoryException, it logs a warning
 * and returns a DataContainer with an error message.
 */
public class GetCarByVinCommand implements Command {
    private final LoggerImpl logger = LoggerImpl.getInstance();

    private final CarInventoryServiceImpl carInventory;
    private final String vin;
    private final User user;

    public GetCarByVinCommand(CarInventoryServiceImpl carInventory, String vin, User user) {
        this.carInventory = carInventory;
        this.vin = vin;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        try {
            logger.log("Showing car by vin: " + vin + " to " + user.getUsername());
            Car result = carInventory.getCarByVin(vin);
            return new DataContainer(result.toString(), user);
        } catch (CarInventoryException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), user);
        }
    }
}