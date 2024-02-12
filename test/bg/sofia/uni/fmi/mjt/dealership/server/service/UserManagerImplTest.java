package bg.sofia.uni.fmi.mjt.dealership.server.service;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;
import bg.sofia.uni.fmi.mjt.dealership.server.repository.FileRepository;
import bg.sofia.uni.fmi.mjt.dealership.server.util.PasswordEncryptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class UserManagerImplTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";

    private User user = new User(USERNAME, PASSWORD, UserRole.CUSTOMER);

    private FileRepository<User> userFileRepository = mock(FileRepository.class);
    private UserManagerService userManager = new UserManagerServiceImpl(userFileRepository);

    @Test
    void testCreateUserSuccessfully() {
        userManager.createUser(USERNAME, PASSWORD, user.getRole());

        assertEquals(1, userManager.getUsers().size(),
            "The user should be created successfully.");
    }

    @Test
    void testCreateUserWithNullParameter() {
        assertThrows(UserManagerException.class, () -> userManager.createUser(null, PASSWORD, user.getRole()),
            "Create user should throw an exception when there is a null parameter.");
    }

    @Test
    void testCreateUserWithExistingUsername() {
        userManager.createUser(USERNAME, PASSWORD, user.getRole());

        assertThrows(UserManagerException.class, () -> userManager.createUser(USERNAME, NEW_PASSWORD, user.getRole()),
            "Create user should throw an exception when the username already exists.");
    }

    @Test
    void testDeleteUserSuccessfully() {
        userManager.createUser(USERNAME, PASSWORD, user.getRole());
        userManager.deleteUser(USERNAME);

        assertEquals(0, userManager.getUsers().size(),
            "The user should be deleted successfully.");
    }

    @Test
    void testDeleteUserWithNullUsername() {
        assertThrows(UserManagerException.class, () -> userManager.deleteUser(null),
            "Delete user should throw an exception when the username is null.");
    }

    @Test
    void testDeleteUserWithNonExistingUsername() {
        assertThrows(UserManagerException.class, () -> userManager.deleteUser(USERNAME),
            "Delete user should throw an exception when the username does not exist.");
    }

    @Test
    void testUpdateUserSuccessfully() {
        userManager.createUser(USERNAME, PASSWORD, user.getRole());
        userManager.updateUser(USERNAME, PASSWORD, NEW_PASSWORD);

        String hashedPassword = PasswordEncryptor.encryptData(NEW_PASSWORD);

        assertEquals(hashedPassword, userManager.getUsers().get(USERNAME).getPassword(),
            "The user password should be updated successfully.");
    }

    @Test
    void testUpdateUserWithNullParameter() {
        assertThrows(UserManagerException.class, () -> userManager.updateUser(null, PASSWORD, NEW_PASSWORD),
            "Update user should throw an exception when there is a null parameter.");
    }

    @Test
    void testUpdateUserWithNonExistingUsername() {
        assertThrows(UserManagerException.class, () -> userManager.updateUser(USERNAME, PASSWORD, NEW_PASSWORD),
            "Update user should throw an exception when the username does not exist.");
    }

    @Test
    void testUpdateUserWithIncorrectOldPassword() {
        userManager.createUser(USERNAME, PASSWORD, user.getRole());

        assertThrows(UserManagerException.class, () -> userManager.updateUser(USERNAME, NEW_PASSWORD, NEW_PASSWORD),
            "Update user should throw an exception when the old password is incorrect.");
    }

    @Test
    void testIsValidUserSuccessfully() {
        userManager.createUser(USERNAME, PASSWORD, user.getRole());

        assertEquals(true, userManager.isValidUser(USERNAME, PASSWORD),
            "The user should be valid.");
    }

    @Test
    void testIsValidUserWithNullParameter() {
        assertThrows(UserManagerException.class, () -> userManager.isValidUser(null, PASSWORD),
            "Is valid user should throw an exception when there is a null parameter.");
    }

    @Test
    void testIsValidUserWithIncorrectPassword() {
        userManager.createUser(USERNAME, PASSWORD, user.getRole());

        assertEquals(false, userManager.isValidUser(USERNAME, NEW_PASSWORD),
            "The user should be invalid when the password is wrong.");
    }

    @Test
    void testGetUserSuccessfully() {
        userManager.createUser(USERNAME, PASSWORD, user.getRole());

        assertEquals(user, userManager.getUser(USERNAME),
            "Get user should return the user successfully.");
    }

    @Test
    void testGetUserWithNullUsername() {
        assertThrows(UserManagerException.class, () -> userManager.getUser(null),
            "Get user should throw an exception when the username is null.");
    }
}
