package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.car.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.car.CarInventory;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public class AddCarCommand implements Command {
    private final CarInventory carInventory;
    private final Car car;
    private final User user;
    private final Logger logger = Logger.getInstance();

    public AddCarCommand(CarInventory carInventory, Car car, User user) {
        this.carInventory = carInventory;
        this.car = car;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        try {
            carInventory.addCar(car);
            logger.log("Adding car by " + user.getUsername());
            return new DataContainer("Car added successfully",
                UserRole.STAFF, user);
        } catch (CarInventoryException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer("There is a problem, while trying to add a car.",
                UserRole.STAFF, user);
        }
    }
}
