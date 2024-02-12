package bg.sofia.uni.fmi.mjt.dealership.server.command.inventory;

import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.GetAllAvailableCarsCommand;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAllAvailableCarsCommandTest {
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";

    private final User user = new User(USERNAME, PASSWORD, UserRole.CUSTOMER);

    @Mock
    private CarInventoryServiceImpl carInventory;
    @InjectMocks
    private GetAllAvailableCarsCommand getAllAvailableCarsCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    void testExecuteSuccessfully() {
        List<String> expected = new ArrayList<>();
        expected.add("Car1");
        expected.add("Car2");
        expected.add("Car3");
        when(carInventory.getAllAvailableCars()).thenReturn(expected);

        DataContainer dataContainer = getAllAvailableCarsCommand.execute();

        verify(carInventory, times(1)).getAllAvailableCars();
        assertEquals(expected.toString(), dataContainer.message(),
                "Get all available cars command should return a list of available cars");
    }
}
