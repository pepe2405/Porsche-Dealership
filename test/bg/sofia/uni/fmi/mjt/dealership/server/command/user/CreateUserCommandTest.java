package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateUserCommandTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final UserRole ROLE = UserRole.CUSTOMER;

    private final User user = new User(USERNAME, PASSWORD, ROLE);

    @Mock
    private UserManagerServiceImpl userManagementService;
    @InjectMocks
    private CreateUserCommand createUserCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    public void testExecuteSuccessfully() {
        createUserCommand = new CreateUserCommand(userManagementService, USERNAME, PASSWORD, ROLE);

        DataContainer dataContainer = createUserCommand.execute();

        verify(userManagementService, times(1)).createUser(user.getUsername(), user.getPassword(), user.getRole());
        assertEquals("User created successfully", dataContainer.message(),
            "Create user command should return a success message when creating a user successfully");

    }

    @Test
    public void testExecuteUserInvalidRole() {
        createUserCommand = new CreateUserCommand(userManagementService, USERNAME, PASSWORD, null);

        DataContainer dataContainer = createUserCommand.execute();

        verify(userManagementService, times(0)).createUser(any(), any(), any());
        assertEquals("Invalid role", dataContainer.message(),
            "Create user command should return a error message when the role is invalid");
    }
}
