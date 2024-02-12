package bg.sofia.uni.fmi.mjt.dealership.server.command.testdrives;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReserveTestDriveCommandTest {
    private static final String VIN = "vin";
    private static final String MODEL = "911";
    private static final int YEAR = 2024;
    private static final String DRIVE = "AWD";
    private static final int CYLINDERS = 6;
    private static final double DISPLACEMENT = 3.0;
    private static final String TRANSMISSION = "Manual";
    private static final String CUSTOMER_WORD = "customer";
    private static final String STAFF_WORD = "staff";

    private static ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests;
    private static BlockingQueue<User> queue;
    private static Car car;
    private static User user;

    @Mock
    private CarInventoryServiceImpl carInventory;
    @InjectMocks
    private ReserveTestDriveCommand reserveTestDriveCommand;

    @AfterAll
    static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @BeforeAll
    static void setup() {
        testDriveRequests = new ConcurrentHashMap<>();
        queue = new LinkedBlockingQueue<>();
        car = new Car(VIN, MODEL, YEAR, DRIVE, CYLINDERS, DISPLACEMENT, TRANSMISSION);
        user = new User(CUSTOMER_WORD, CUSTOMER_WORD, UserRole.CUSTOMER);
    }

    @Test
    void testExecuteSuccessfully() {
        reserveTestDriveCommand = new ReserveTestDriveCommand(testDriveRequests, carInventory, VIN, user);
        when(carInventory.getCarByVin(car.vin())).thenReturn(car);

        DataContainer result = reserveTestDriveCommand.execute();

        verify(carInventory, times(1)).getCarByVin(car.vin());
        assertEquals("Test drive reserved for vin by customer", result.message(),
                "ReserveTestDriveCommand execute method does not return the expected message");
    }

    @Test
    void testExecuteCarNotFound() {
        reserveTestDriveCommand = new ReserveTestDriveCommand(testDriveRequests, carInventory, VIN, user);
        when(carInventory.getCarByVin(car.vin())).thenThrow(new CarInventoryException("There isn't a car with this VIN."));

        DataContainer result = reserveTestDriveCommand.execute();

        verify(carInventory, times(1)).getCarByVin(car.vin());
        assertEquals("There isn't a car with this VIN.", result.message(),
                "ReserveTestDriveCommand execute method does not return the expected message");
    }
}
