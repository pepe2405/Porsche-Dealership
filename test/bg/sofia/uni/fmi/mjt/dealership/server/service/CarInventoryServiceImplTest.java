package bg.sofia.uni.fmi.mjt.dealership.server.service;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.exception.FileRepositoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarInventoryServiceImplTest {
    private static final String FIRST_VIN = "firstVin";
    private static final String SECOND_VIN = "secondVin";
    private static final String MODEL = "911";
    private static final int YEAR = 2024;
    private static final String DRIVE = "AWD";
    private static final int CYLINDERS = 6;
    private static final double DISPLACEMENT = 3.0;
    private static final String TRANSMISSION = "Manual";

    private Car car = new Car(FIRST_VIN, MODEL, YEAR, DRIVE, CYLINDERS, DISPLACEMENT, TRANSMISSION);
    private Car secondCar = new Car(SECOND_VIN, MODEL, YEAR, DRIVE, CYLINDERS, DISPLACEMENT, TRANSMISSION);

    private FileRepository<Car> carFileRepository = mock(FileRepository.class);
    private CarInventoryService carInventory = new CarInventoryServiceImpl(carFileRepository);

    @Test
    void testAddCarSuccessfully() {
        carInventory.addCar(car);
        assertEquals(1, carInventory.getAllAvailableCars().size(),
            "The car should be added successfully.");
    }

    @Test
    void testAddCarWithNullCar() {
        assertThrows(CarInventoryException.class, () -> carInventory.addCar(null),
            "Add car should throw an exception when the car is null.");
    }

    @Test
    void testAddCarWithExistingCar() {
        carInventory.addCar(car);
        assertThrows(CarInventoryException.class, () -> carInventory.addCar(car),
            "Add car should throw an exception when the car already exists.");
    }

    @Test
    void testRemoveCarSuccessfully() {
        carInventory.addCar(car);
        carInventory.removeCar(FIRST_VIN);
        assertEquals(0, carInventory.getAllAvailableCars().size(),
            "Remove car should be removed successfully.");
    }

    @Test
    void testRemoveCarWithNullVin() {
        assertThrows(CarInventoryException.class, () -> carInventory.removeCar(null),
            "Remove car should throw an exception when the vin is null.");
    }

    @Test
    void testRemoveCarWithNonExistingVin() {
        assertThrows(CarInventoryException.class, () -> carInventory.removeCar(FIRST_VIN),
            "Remove car should throw an exception when the car does not exist.");
    }

    @Test
    void testGetCarByVinSuccessfully() {
        carInventory.addCar(car);
        assertEquals(car, carInventory.getCarByVin(FIRST_VIN),
            "Get car by vin should return the car successfully.");
    }

    @Test
    void testGetCarByVinWithNullVin() {
        assertThrows(CarInventoryException.class, () -> carInventory.getCarByVin(null),
            "Get car by vin should throw an exception when the vin is null.");
    }

    @Test
    void testGetCarByVinWithNonExistingVin() {
        assertThrows(CarInventoryException.class, () -> carInventory.getCarByVin(FIRST_VIN),
            "Get car by vin should throw an exception when the car does not exist.");
    }

    @Test
    void testGetAllAvailableCars() {
        carInventory.addCar(car);
        carInventory.addCar(secondCar);
        List<String> expected = Arrays.asList(car.vin(), secondCar.vin());
        assertEquals(expected, carInventory.getAllAvailableCars(),
            "Get all available cars should return all cars successfully.");
    }

    @Test
    void testClearSuccessfully() {
        carInventory.addCar(car);
        carInventory.addCar(secondCar);
        carInventory.clear();
        assertEquals(0, carInventory.getAllAvailableCars().size(),
            "Clear should remove all cars successfully.");
    }
}
