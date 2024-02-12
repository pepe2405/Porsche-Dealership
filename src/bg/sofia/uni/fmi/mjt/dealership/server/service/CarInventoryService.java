package bg.sofia.uni.fmi.mjt.dealership.server.service;

import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;

import java.util.List;

/**
 * The CarInventoryService interface provides methods for managing the car inventory.
 * It allows adding and removing cars, retrieving a car by its VIN,
 * getting all available cars, and clearing the inventory.
 */
public interface CarInventoryService {

    /**
     * Adds a new car to the inventory.
     *
     * @param car the car to be added
     * @throws CarInventoryException if the car is null or already exists in the inventory
     */
    void addCar(Car car) throws CarInventoryException;

    /**
     * Removes a car from the inventory by its VIN.
     *
     * @param vin the VIN of the car to be removed
     * @throws CarInventoryException if the VIN is null or no car with this VIN exists in the inventory
     */
    void removeCar(String vin) throws CarInventoryException;

    /**
     * Retrieves a car from the inventory by its VIN.
     *
     * @param vin the VIN of the car to be retrieved
     * @return the car with the specified VIN, or null if no such car exists in the inventory
     * @throws CarInventoryException if the VIN is null or no car with this VIN exists in the inventory
     */
    Car getCarByVin(String vin) throws CarInventoryException;

    /**
     * Retrieves all available cars in the inventory.
     *
     * @return a list of all available cars in the inventory
     */
    List<String> getAllAvailableCars();

    /**
     * Clears the inventory, removing all cars.
     */
    void clear();
}