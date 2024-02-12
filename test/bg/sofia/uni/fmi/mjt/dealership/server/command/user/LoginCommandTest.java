package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;
import bg.sofia.uni.fmi.mjt.dealership.server.service.UserManagerServiceImpl;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginCommandTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final UserRole ROLE = UserRole.CUSTOMER;

    private User user = new User(USERNAME, PASSWORD, ROLE);

    @Mock
    private UserManagerServiceImpl userManagerService;
    @InjectMocks
    private LoginCommand loginCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    void testExecuteSuccessful() {
        loginCommand = new LoginCommand(userManagerService, USERNAME, PASSWORD);

        when(userManagerService.getUser(USERNAME)).thenReturn(user);
        when(userManagerService.isValidUser(USERNAME, PASSWORD)).thenReturn(true);

        DataContainer dataContainer = loginCommand.execute();

        verify(userManagerService, times(1)).isValidUser(USERNAME, PASSWORD);
        assertEquals("User logged in successfully", dataContainer.message(),
                "Login command should return a success message when logging in a user successfully");
    }

    @Test
    void testExecuteInvalidArguments() {
        loginCommand = new LoginCommand(userManagerService, USERNAME, PASSWORD);

        when(userManagerService.getUser(USERNAME)).thenReturn(user);
        when(userManagerService.isValidUser(USERNAME, PASSWORD)).thenReturn(false);

        DataContainer dataContainer = loginCommand.execute();

        verify(userManagerService, times(1)).isValidUser(USERNAME, PASSWORD);
        assertEquals("Invalid username or password", dataContainer.message(),
                "Login command should return a error message when there is no such user");
    }

    @Test
    void testExecuteErrorDetected() {
        loginCommand = new LoginCommand(userManagerService, USERNAME, PASSWORD);

        when(userManagerService.getUser(USERNAME)).thenThrow(new UserManagerException("There is no such user"));

        DataContainer dataContainer = loginCommand.execute();

        verify(userManagerService, times(0)).isValidUser(USERNAME, PASSWORD);
        assertEquals("There is no such user", dataContainer.message(),
                "Login command should return a error message when there is no such user");
    }
}
