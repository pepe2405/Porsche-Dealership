package bg.sofia.uni.fmi.mjt.dealership.server.command.user;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteUserCommandTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final UserRole ROLE = UserRole.CUSTOMER;

    private User user = new User(USERNAME, PASSWORD, ROLE);

    @Mock
    private UserManagerServiceImpl userManagerService;
    @InjectMocks
    private DeleteUserCommand deleteUserCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    void testExecuteSuccessful() {
        deleteUserCommand = new DeleteUserCommand(userManagerService, user);

        DataContainer dataContainer = deleteUserCommand.execute();

        verify(userManagerService, times(1)).deleteUser(user.getUsername());
        assertEquals("User deleted successfully", dataContainer.message(),
                "Delete user command should return a success message when deleting a user successfully");
    }

    @Test
    void testExecuteErrorDetected() {
        doThrow(new UserManagerException("There is no such user")).when(userManagerService).deleteUser(USERNAME);
        deleteUserCommand = new DeleteUserCommand(userManagerService, user);

        DataContainer dataContainer = deleteUserCommand.execute();

        verify(userManagerService, times(1)).deleteUser(user.getUsername());
        assertEquals("There is no such user", dataContainer.message(),
                "Delete user command should return a error message when there is no such user");

    }
}
