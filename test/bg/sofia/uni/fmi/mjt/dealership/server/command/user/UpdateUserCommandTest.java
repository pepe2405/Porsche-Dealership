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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateUserCommandTest {
    private static final String USERNAME = "username";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";
    private static final UserRole ROLE = UserRole.CUSTOMER;

    private User user = new User(USERNAME, OLD_PASSWORD, ROLE);

    @Mock
    private UserManagerServiceImpl userManagerService;
    @InjectMocks
    private UpdateUserCommand updateUserCommand;

    @AfterAll
    public static void afterAll() {
        LoggerImpl.getInstance().clearLogFile();
    }

    @Test
    void testExecuteSuccessfully() {
        updateUserCommand = new UpdateUserCommand(userManagerService, OLD_PASSWORD, NEW_PASSWORD, user);

        DataContainer dataContainer = updateUserCommand.execute();

        assertEquals("User updated successfully", dataContainer.message(),
                "Update user command should return a success message when updating a user successfully");
    }

    @Test
    void testExecuteErrorDetected() {
        updateUserCommand = new UpdateUserCommand(userManagerService, OLD_PASSWORD, NEW_PASSWORD, user);
        doThrow(new UserManagerException("Old password is incorrect"))
            .when(userManagerService).updateUser(USERNAME, OLD_PASSWORD, NEW_PASSWORD);

        DataContainer dataContainer = updateUserCommand.execute();

        assertEquals("Old password is incorrect", dataContainer.message(),
                "Update user command should return a error message when there is no such user");
    }
}
