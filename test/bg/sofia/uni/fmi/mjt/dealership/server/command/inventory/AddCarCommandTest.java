package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddCarCommandTest {
    private static final String VIN = "vin";
    private static final String MODEL = "911";
    private static final int YEAR = 2024;
    private static final String DRIVE = "AWD";
    private static final int CYLINDERS = 6;
    private static final double DISPLACEMENT = 3.0;
    private static final String TRANSMISSION = "Manual";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private Car car;
    private User user = new User(USERNAME, PASSWORD, UserRole.STAFF);

    @Mock
    private CarInventoryServiceImpl carInventory;
    @InjectMocks
    private AddCarCommand addCarCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    void testExecuteSuccessfully() {
        car = new Car(VIN, MODEL, YEAR, DRIVE, CYLINDERS, DISPLACEMENT, TRANSMISSION);
        addCarCommand = new AddCarCommand(carInventory, car, user);

        DataContainer dataContainer = addCarCommand.execute();

        verify(carInventory, times(1)).addCar(car);
        assertEquals("Car added successfully", dataContainer.message(),
            "Add command should return a success message when adding a car successfully");
    }

    @Test
    void testExecuteInvalidVin() {
        car = new Car("", MODEL, YEAR, DRIVE, CYLINDERS, DISPLACEMENT, TRANSMISSION);
        addCarCommand = new AddCarCommand(carInventory, car, user);

        DataContainer dataContainer = addCarCommand.execute();

        verify(carInventory, times(0)).addCar(car);
        assertEquals("VIN cannot be null or empty", dataContainer.message(),
            "Add command should return an error message when adding a car with invalid VIN");
    }

    @Test
    void testExecuteInvalidModel() {
        car = new Car(VIN, "", YEAR, DRIVE, CYLINDERS, DISPLACEMENT, TRANSMISSION);
        addCarCommand = new AddCarCommand(carInventory, car, user);

        DataContainer dataContainer = addCarCommand.execute();

        verify(carInventory, times(0)).addCar(car);
        assertEquals("Model cannot be null or empty", dataContainer.message(),
            "Add command should return an error message when adding a car with invalid model");
    }

    @Test
    void testExecuteInvalidYear() {
        car = new Car(VIN, MODEL, -1, DRIVE, CYLINDERS, DISPLACEMENT, TRANSMISSION);
        addCarCommand = new AddCarCommand(carInventory, car, user);

        DataContainer dataContainer = addCarCommand.execute();

        verify(carInventory, times(0)).addCar(car);
        assertEquals("Year cannot be negative", dataContainer.message(),
            "Add command should return an error message when adding a car with invalid year");
    }

    @Test
    void testExecuteInvalidDrive() {
        car = new Car(VIN, MODEL, YEAR, "", CYLINDERS, DISPLACEMENT, TRANSMISSION);
        addCarCommand = new AddCarCommand(carInventory, car, user);

        DataContainer dataContainer = addCarCommand.execute();

        verify(carInventory, times(0)).addCar(car);
        assertEquals("Drive cannot be null or empty", dataContainer.message(),
            "Add command should return an error message when adding a car with invalid drive");
    }

    @Test
    void testExecuteInvalidCylinders() {
        car = new Car(VIN, MODEL, YEAR, DRIVE, -1, DISPLACEMENT, TRANSMISSION);
        addCarCommand = new AddCarCommand(carInventory, car, user);

        DataContainer dataContainer = addCarCommand.execute();

        verify(carInventory, times(0)).addCar(car);
        assertEquals("Cylinders cannot be lower than 4 or higher than 8", dataContainer.message(),
            "Add command should return an error message when adding a car with invalid cylinders");
    }

    @Test
    void testExecuteInvalidDisplacement() {
        car = new Car(VIN, MODEL, YEAR, DRIVE, CYLINDERS, -1, TRANSMISSION);
        addCarCommand = new AddCarCommand(carInventory, car, user);

        DataContainer dataContainer = addCarCommand.execute();

        verify(carInventory, times(0)).addCar(car);
        assertEquals("Displacement must be between 1.0 and 5.0 liters", dataContainer.message(),
            "Add command should return an error message when adding a car with invalid displacement");
    }

    @Test
    void testExecuteInvalidTransmission() {
        car = new Car(VIN, MODEL, YEAR, DRIVE, CYLINDERS, DISPLACEMENT, "");
        addCarCommand = new AddCarCommand(carInventory, car, user);

        DataContainer dataContainer = addCarCommand.execute();

        verify(carInventory, times(0)).addCar(car);
        assertEquals("Transmission cannot be null or empty", dataContainer.message(),
            "Add command should return an error message when adding a car with invalid transmission");
    }

    @Test
    void testExecuteCommandNullCar() {
        doThrow(new CarInventoryException("There isn't a car to add.")).when(carInventory).addCar(null);

        DataContainer dataContainer = addCarCommand.execute();

        verify(carInventory, times(1)).addCar(null);
        assertEquals("There isn't a car to add.", dataContainer.message(),
            "Add command should return an error message when adding a null car");
    }
}
