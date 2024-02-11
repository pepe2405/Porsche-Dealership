package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.car.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.car.CarInventory;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public class GetCarByVinCommand implements Command {
    private final CarInventory carInventory;
    private final String vin;
    private final User user;
    private final Logger logger = Logger.getInstance();

    public GetCarByVinCommand(CarInventory carInventory, String vin, User user) {
        this.carInventory = carInventory;
        this.vin = vin;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        try {
            logger.log("Showing car by vin: " + vin + " to " + user.getUsername());
            Car result = carInventory.getCarByVin(vin);
            return new DataContainer(result.toString(), UserRole.CUSTOMER, user);
        } catch (CarInventoryException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer("There was a problem while trying to get a car by vin.",
                UserRole.CUSTOMER, user);
        }
    }
}