package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.car.CarInventory;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public class RemoveCarCommand implements Command {
    private final CarInventory carInventory;
    private final String vin;
    private final User user;
    private final Logger logger = Logger.getInstance();

    public RemoveCarCommand(CarInventory carInventory, String vin, User user) {
        this.carInventory = carInventory;
        this.user = user;
        this.vin = vin;
    }

    @Override
    public DataContainer execute() {
        try {
            logger.log("Removing car " + vin + " by " + user.getUsername());
            carInventory.removeCar(vin);
            return new DataContainer("Removing car", UserRole.STAFF , user);
        } catch (CarInventoryException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer("The car you are trying to remove doesn't exist", UserRole.STAFF, user);
        }
    }
}
