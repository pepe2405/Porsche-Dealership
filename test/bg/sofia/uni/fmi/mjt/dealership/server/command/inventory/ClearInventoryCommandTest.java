package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ClearInventoryCommandTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private User user = new User(USERNAME, PASSWORD, UserRole.STAFF);

    @Mock
    private CarInventoryServiceImpl carInventory;
    @InjectMocks
    private ClearInventoryCommand clearInventoryCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    void testExecuteSuccessfully() {
        clearInventoryCommand = new ClearInventoryCommand(carInventory, user);

        DataContainer dataContainer = clearInventoryCommand.execute();

        verify(carInventory, times(1)).clear();
        assertEquals("Inventory cleared successfully", dataContainer.message(),
            "Clear command should return a success message when clearing the inventory successfully");
    }

}
