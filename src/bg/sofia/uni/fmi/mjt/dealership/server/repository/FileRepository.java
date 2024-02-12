package bg.sofia.uni.fmi.mjt.dealership.server.repository;

import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The FileRepository interface provides methods for saving and reading data from files.
 * It is a generic interface where T represents the type of the data to be saved or read.
 */
public interface FileRepository<T> {

    /**
     * Saves all records of type T to a file.
     *
     * @param records the collection of records to be saved
     * @throws bg.sofia.uni.fmi.mjt.dealership.server.exception.FileRepositoryException if an exception
     * is caught during the file writing process
     */
    void saveAll(Collection<T> records);

    /**
     * Reads all User records from a file.
     *
     * @return a map of all User records, where the key is the username and the value is the User object
     */
    Map<String, User> readAllUsers();

    /**
     * Reads all Car records from a file.
     *
     * @return a set of all Car records
     */
    Set<Car> readAllCars();
}