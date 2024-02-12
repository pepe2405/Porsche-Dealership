package bg.sofia.uni.fmi.mjt.dealership.server.command.testdrives;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The ReserveTestDriveCommand class implements the Command interface and provides the functionality
 * for reserving a test drive for a car.
 * It uses an instance of CarInventoryServiceImpl to get the car by VIN.
 * It maintains the test drive requests and the user who performs the operation.
 * It provides a method to execute the command which reserves a test drive for the car and returns a DataContainer
 * with the result of the operation.
 * If the car is not found, it returns a DataContainer with an error message.
 * If the operation is successful, it logs the operation and returns a DataContainer with a success message.
 */
public class ReserveTestDriveCommand implements Command {
    private final LoggerImpl logger = LoggerImpl.getInstance();

    private final ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests;
    private final CarInventoryServiceImpl carInventory;
    private final String vin;
    private final User user;

    public ReserveTestDriveCommand(ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests,
                                   CarInventoryServiceImpl carInventory, String vin, User user) {
        this.testDriveRequests = testDriveRequests;
        this.carInventory = carInventory;
        this.vin = vin;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        try {
            Car car = carInventory.getCarByVin(vin);
            testDriveRequests.computeIfAbsent(car, q -> new LinkedBlockingQueue<>()).add(user);
            logger.log("Test drive reserved.");
            return new DataContainer("Test drive reserved for " + car.vin() + " by " + user.getUsername(), user);
        } catch (CarInventoryException e) {
            return new DataContainer(e.getMessage(), user);
        }
    }
}
