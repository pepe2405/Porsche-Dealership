package bg.sofia.uni.fmi.mjt.dealership.server.car;

import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryFileException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CarInventory {
    private static final String FILE_PATH = "cars.csv";
    private final CarsFileHandler fileHandler;
    private Set<Car> cars;
    private final Logger logger = Logger.getInstance();

    public CarInventory() {
        this.fileHandler = new CarsFileHandler(FILE_PATH);
        this.cars = Collections.synchronizedSet(new HashSet<>());
        loadFromFile();
    }

    public Set<Car> getCars() {
        return cars;
    }

    public synchronized void saveToFile() {
        try {
            fileHandler.saveToFile(cars);
        } catch (CarInventoryFileException e) {
            logger.logError("Error while saving to file", e);
            System.err.println("Error while saving to file: " + e.getMessage());
        }
    }

    public synchronized void loadFromFile() {
        try {
            cars = fileHandler.loadFromFile();
        } catch (CarInventoryFileException e) {
            logger.logError("Error while loading from file", e);
            System.err.println("Error while loading from file: " + e.getMessage());
        }
    }

    public void addCar(Car car) throws CarInventoryException {
        if (car == null) {
            logger.logWarning("Car cannot be null");
            throw new CarInventoryException("There isn't a car to add.");
        }

        boolean carExists = cars.stream()
            .anyMatch(c -> c.vin().equals(car.vin()));

        if (carExists) {
            logger.logWarning("Car already exists");
            throw new CarInventoryException("This car already exists.");
        }

        cars.add(car);
        saveToFile();
    }

    public void removeCar(String vin) throws CarInventoryException {
        if (vin == null) {
            logger.logWarning("VIN cannot be null");
            throw new CarInventoryException("There isn't a vin number.");
        }

        boolean carExists = cars.stream()
            .anyMatch(c -> c.vin().equals(vin));

        if (!carExists) {
            logger.logWarning("Car with this VIN does not exist");
            throw new CarInventoryException("There isn't a car with this VIN.");
        }

        cars.removeIf(c -> c.vin().equals(vin));
        saveToFile();
    }

    public Car getCarByVin(String vin) throws CarInventoryException {
        if (vin == null) {
            logger.logWarning("VIN cannot be null");
            throw new CarInventoryException("There isn't a vin number.");
        }

        boolean carExists = cars.stream()
            .anyMatch(c -> c.vin().equals(vin));

        if (!carExists) {
            logger.logWarning("Car with this VIN does not exist");
            throw new CarInventoryException("There isn't a car with this VIN.");
        }

        return cars.stream()
            .filter(c -> c.vin().equals(vin))
            .findFirst()
            .orElse(null);
    }

    public List<String> getAllAvailableCars() {
        return cars.stream()
            .map(car -> car.vin())
            .collect(Collectors.toList());
    }

    public void clear() {
        cars.clear();
        saveToFile();
    }
}