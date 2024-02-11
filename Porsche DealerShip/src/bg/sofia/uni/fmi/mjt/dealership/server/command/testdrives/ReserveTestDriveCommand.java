package bg.sofia.uni.fmi.mjt.dealership.server.command.testdrives;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.car.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.car.CarInventory;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ReserveTestDriveCommand implements Command {
    private final ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests;
    private final CarInventory carInventory;
    private final String vin;
    private final User user;

    public ReserveTestDriveCommand(ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests,
                                   CarInventory carInventory, String vin, User user) {
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
            return new DataContainer("Test drive reserved for " + car.vin() + " by " + user.getUsername(),
                user.getRole(), user);
        } catch (CarInventoryException e) {
            return new DataContainer("Car with VIN: " + vin + " not found", user.getRole(), user);
        }
    }
}
