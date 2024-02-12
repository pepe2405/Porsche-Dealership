package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LogoutCommandTest {
    private LogoutCommand logoutCommand = new LogoutCommand();

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    void testExecuteSuccessful() {
        DataContainer dataContainer = logoutCommand.execute();

        assertEquals("User logged out successfully", dataContainer.message());
    }
}
