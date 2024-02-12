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

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AcceptTestDriveCommandTest {
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
    private static ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap;
    private static BlockingQueue<User> queue;
    private static User customer;
    private static Car car;
    private static User staff;

    @Mock
    private SocketChannel socketChannel;
    @Mock
    private CarInventoryServiceImpl carInventory;
    @InjectMocks
    private static AcceptTestDriveCommand acceptTestDriveCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @BeforeAll
    static void setUp() {
        testDriveRequests = new ConcurrentHashMap<>();
        socketToUserMap = new ConcurrentHashMap<>();
        staff = new User(STAFF_WORD, STAFF_WORD, UserRole.STAFF);
        car = new Car(VIN, MODEL, YEAR, DRIVE, CYLINDERS, DISPLACEMENT, TRANSMISSION);
        queue = new LinkedBlockingQueue<>();
        customer = new User(CUSTOMER_WORD, CUSTOMER_WORD, UserRole.CUSTOMER);
        testDriveRequests.put(car, queue);
    }

    @Test
    void testAcceptTestDriveCommandSuccessfully() {
        queue.add(customer);

        acceptTestDriveCommand = new AcceptTestDriveCommand(testDriveRequests, socketToUserMap, staff, VIN, carInventory);
        when(carInventory.getCarByVin(VIN)).thenReturn(car);

        DataContainer dataContainer = acceptTestDriveCommand.execute();

        verify(carInventory, times(1)).getCarByVin(VIN);
        assertEquals("Test drive accepted for user: customer for car with VIN: vin", dataContainer.message(),
                "Accept test drive command should return a success message when accepting a test drive successfully");
    }

    @Test
    void testAcceptTestDriveCommandEmptyQueue() {
        testDriveRequests.put(car, queue);
        acceptTestDriveCommand = new AcceptTestDriveCommand(testDriveRequests, socketToUserMap, staff, VIN, carInventory);
        when(carInventory.getCarByVin(VIN)).thenReturn(car);

        DataContainer dataContainer = acceptTestDriveCommand.execute();

        verify(carInventory, times(1)).getCarByVin(VIN);
        assertEquals("No users in queue for test drive for car with VIN: vin", dataContainer.message(),
                "Accept test drive command should return an error message when there are no users in the queue");
    }

    @Test
    void testAcceptTestDriveCommandCarNotFound() {
        when(carInventory.getCarByVin(VIN)).thenThrow(new CarInventoryException("Car with VIN: vin not found"));

        acceptTestDriveCommand = new AcceptTestDriveCommand(testDriveRequests, socketToUserMap, staff, VIN, carInventory);

        DataContainer dataContainer = acceptTestDriveCommand.execute();

        assertEquals("Car with VIN: vin not found", dataContainer.message(),
                "Accept test drive command should return an error message when the car is not found");
    }

}
