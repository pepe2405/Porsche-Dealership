package bg.sofia.uni.fmi.mjt.dealership.server.service;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.FileRepositoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.repository.FileRepository;
import bg.sofia.uni.fmi.mjt.dealership.server.repository.FileRepositoryImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The CarInventoryServiceImpl class is an implementation of the CarInventoryService interface.
 * It provides methods for managing the car inventory.
 */
public class CarInventoryServiceImpl implements CarInventoryService {
    private static final String FILE_PATH = "cars.json";

    private final LoggerImpl logger;
    private final FileRepository<Car> carFileRepository;
    private Set<Car> cars = Collections.synchronizedSet(new HashSet<>());

    public CarInventoryServiceImpl(FileRepository<Car> carFileRepository) {
        this.logger = LoggerImpl.getInstance();
        this.carFileRepository = carFileRepository;

        loadFromFile();
    }

    public CarInventoryServiceImpl() {
        this.logger = LoggerImpl.getInstance();
        this.carFileRepository = new FileRepositoryImpl<>(FILE_PATH);

        loadFromFile();
    }

    @Override
    public void addCar(Car car) {
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

    @Override
    public void removeCar(String vin) {
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

    @Override
    public Car getCarByVin(String vin) {
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

    @Override
    public List<String> getAllAvailableCars() {
        return cars.stream()
            .map(Car::vin)
            .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        cars.clear();
        saveToFile();
    }

    private synchronized void saveToFile() {
        try {
            carFileRepository.saveAll(cars);
        } catch (FileRepositoryException e) {
            logger.logError("Error while saving to file", e);
            System.err.println("Error while saving to file: " + e.getMessage());
        }
    }

    private synchronized void loadFromFile() {
        try {
            cars = Collections.synchronizedSet(new HashSet<>(carFileRepository.readAllCars()));
        } catch (FileRepositoryException e) {
            logger.logError("Error while loading from file", e);
            System.err.println("Error while loading from file: " + e.getMessage());
        }
    }
}