package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.car.CarInventory;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;

public class GetAllAvailableCarsCommand implements Command {
    private final CarInventory carInventory;
    private final User user;
    private final Logger logger = Logger.getInstance();

    public GetAllAvailableCarsCommand(CarInventory carInventory, User user) {
        this.carInventory = carInventory;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        logger.log("Showing all the available cars");
        return new DataContainer(carInventory.getAllAvailableCars().toString(), user.getRole(), user);
    }
}
