package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
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
public class RemoveCarCommandTest {
    private static final String VIN = "vin";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private User user = new User(USERNAME, PASSWORD, UserRole.CUSTOMER);

    @Mock
    private CarInventoryServiceImpl carInventory;
    @InjectMocks
    private RemoveCarCommand removeCarCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    void testExecuteSuccessfully() {
        removeCarCommand = new RemoveCarCommand(carInventory, VIN, user);

        DataContainer dataContainer = removeCarCommand.execute();

        verify(carInventory, times(1)).removeCar(VIN);
        assertEquals("Removing car", dataContainer.message(),
                "Remove car command should return a success message when removing a car successfully");
    }

    @Test
    void testExecuteErrorDetected() {
        removeCarCommand = new RemoveCarCommand(carInventory, VIN, user);
        doThrow(new CarInventoryException("There isn't a car with this VIN.")).when(carInventory).removeCar(VIN);

        DataContainer dataContainer = removeCarCommand.execute();

        verify(carInventory, times(1)).removeCar(VIN);
        assertEquals("There isn't a car with this VIN.", dataContainer.message(),
            "Remove car command should return a error message when there isn't a car with this VIN");
    }
}
