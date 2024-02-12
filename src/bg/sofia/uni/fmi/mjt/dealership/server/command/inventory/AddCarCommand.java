package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;

/**
 * The AddCarCommand class implements the Command interface and provides the functionality
 * for adding a car to the inventory.
 * It uses an instance of CarInventoryServiceImpl to add the car to the inventory.
 * It uses an instance of LoggerImpl to log the operation.
 * It maintains the car to be added and the user who performs the operation.
 * It provides a method to execute the command which adds the car to the
 * inventory and returns a DataContainer
 * with the result of the operation.
 * If the operation is successful, it logs the operation and returns a DataContainer with a success message.
 * If the operation fails due to a CarInventoryException, it logs a warning
 * and returns a DataContainer with an error message.
 */
public class AddCarCommand implements Command {
    private final LoggerImpl logger = LoggerImpl.getInstance();

    private final CarInventoryServiceImpl carInventory;
    private final Car car;
    private final User user;

    public AddCarCommand(CarInventoryServiceImpl carInventory, Car car, User user) {
        this.carInventory = carInventory;
        this.car = car;
        this.user = user;
    }

    @Override
    public DataContainer execute() {
        try {
            validateCar();
            carInventory.addCar(car);
            logger.log("Adding car by " + user.getUsername());
            return new DataContainer("Car added successfully", user);
        } catch (CarInventoryException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), user);
        } catch (IllegalArgumentException e) {
            logger.logWarning(e.getMessage());
            return new DataContainer(e.getMessage(), user);
        }
    }

    private void validateCar() {
        if (car != null) {
            validateVin();
            validateModel();
            validateYear();
            validateDrive();
            validateCylinders();
            validateDisplacement();
            validateTransmission();
        }
    }

    private void validateVin() {
        if (car.vin() == null || car.vin().isBlank()) {
            throw new IllegalArgumentException("VIN cannot be null or empty");
        }
    }

    private void validateModel() {
        if (car.model() == null || car.model().isBlank()) {
            throw new IllegalArgumentException("Model cannot be null or empty");
        }
    }

    private void validateYear() {
        if (car.year() < 0) {
            throw new IllegalArgumentException("Year cannot be negative");
        }
    }

    private void validateDrive() {
        if (car.drive() == null || car.drive().isBlank()) {
            throw new IllegalArgumentException("Drive cannot be null or empty");
        }
    }

    private void validateCylinders() {
        final int minCylinders = 4;
        final int maxCylinders = 8;
        if (car.cylinders() < minCylinders || car.cylinders() > maxCylinders) {
            throw new IllegalArgumentException("Cylinders cannot be lower than 4 or higher than 8");
        }
    }

    private void validateDisplacement() {
        final double maxDisplacement = 5.0;
        final double minDisplacement = 1.0;
        if (car.displacement() < minDisplacement || car.displacement() > maxDisplacement) {
            throw new IllegalArgumentException("Displacement must be between 1.0 and 5.0 liters");
        }
    }

    private void validateTransmission() {
        if (car.transmission() == null || car.transmission().isBlank()) {
            throw new IllegalArgumentException("Transmission cannot be null or empty");
        }
    }

}
