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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCarByVinCommandTest {
    private static final String VIN = "vin";
    private static final String MODEL = "911";
    private static final int YEAR = 2024;
    private static final String DRIVE = "AWD";
    private static final int CYLINDERS = 6;
    private static final double DISPLACEMENT = 3.0;
    private static final String TRANSMISSION = "Manual";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private User user = new User(USERNAME, PASSWORD, UserRole.CUSTOMER);

    private Car car = new Car(VIN, MODEL, YEAR, DRIVE, CYLINDERS, DISPLACEMENT, TRANSMISSION);

    @Mock
    private CarInventoryServiceImpl carInventory;
    @InjectMocks
    private GetCarByVinCommand getCarByVinCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    void testExecuteSuccessfully() {
        getCarByVinCommand = new GetCarByVinCommand(carInventory, VIN, user);
        when(carInventory.getCarByVin(any())).thenReturn(car);

        DataContainer dataContainer = getCarByVinCommand.execute();

        verify(carInventory, times(1)).getCarByVin(any());
        assertEquals(car.toString(), dataContainer.message(),
                "Expected data container to contain the car");
    }

    @Test
    void testExecuteErrorDetected() {
        getCarByVinCommand = new GetCarByVinCommand(carInventory, VIN, user);
        when(carInventory.getCarByVin(any())).thenThrow(new CarInventoryException("There isn't a vin number."));

        DataContainer dataContainer = getCarByVinCommand.execute();

        verify(carInventory, times(1)).getCarByVin(any());
        assertEquals("There isn't a vin number.", dataContainer.message(),
                "Expected data container message to be the same as the exception message");
    }
}
