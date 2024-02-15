package bg.sofia.uni.fmi.mjt.dealership.server.command;

import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandValidatorTest {
    private static final String CUSTOMER_WORD = "customer";
    private static final String STAFF_WORD = "staff";
    private static final String UNAUTHORIZED_WORD = "unauthorized";
    private static final String PASSWORD = "password";

    private static User customer;
    private static User staff;
    private static User unauthorized;

    @BeforeAll
    static void setUp() {
        customer = new User(CUSTOMER_WORD, PASSWORD, UserRole.CUSTOMER);
        staff = new User(STAFF_WORD, PASSWORD, UserRole.STAFF);
        unauthorized = new User(UNAUTHORIZED_WORD, PASSWORD, UserRole.UNAUTHORIZED);
    }

    @Test
    void testIsValidCommandNullInput() {
        assertFalse(CommandValidator.isValidCommand(null, customer),
                "isValidCommand should return false for null input");
    }

    @Test
    void testIsValidCommandBlankInput() {
        assertFalse(CommandValidator.isValidCommand("", customer),
                "isValidCommand should return false for blank input");
    }

    @Test
    void testIsValidCommandInvalidCommand() {
        assertFalse(CommandValidator.isValidCommand("invalid", customer),
                "isValidCommand should return false for invalid command");
    }

    @Test
    void testIsValidCommandInvalidArgCount() {
        assertFalse(CommandValidator.isValidCommand("register", customer),
                "isValidCommand should return false for invalid command length");
    }

    @Test
    void testIsValidCommandCustomerCommandByStaff() {
        assertFalse(CommandValidator.isValidCommand("view-car 1", staff),
                "isValidCommand should return false for customer command by staff");
    }

    @Test
    void testIsValidCommandStaffCommandByCustomer() {
        assertFalse(CommandValidator.isValidCommand("add-car 1 2 3 4 5 6 7 8", customer),
                "isValidCommand should return false for staff command by customer");
    }

    @Test
    void testIsValidCommandUnauthorizedCommandByCustomer() {
        assertFalse(CommandValidator.isValidCommand("login 1 2", customer),
                "isValidCommand should return false for unauthorized command by customer");
    }

    @Test
    void testIsValidCommandUnauthorizedCommandByStaff() {
        assertFalse(CommandValidator.isValidCommand("login 1 2", staff),
                "isValidCommand should return false for unauthorized command by staff");
    }

    @Test
    void testIsValidCommandSuccessfullyUnauthorizedCommand() {
        assertTrue(CommandValidator.isValidCommand("login 1 2", unauthorized),
                "isValidCommand should return true for valid unauthorized command by unauthorized user");
    }

    @Test
    void testIsValidCommandSuccessfullyCustomerCommand() {
        assertTrue(CommandValidator.isValidCommand("view-car 1", customer),
                "isValidCommand should return true for valid customer command by customer");
    }

    @Test
    void testIsValidCommandSuccessfullyStaffCommand() {
        assertFalse(CommandValidator.isValidCommand("add-car 1 2 3 4 5 6 7 8", staff),
                "isValidCommand should return true for valid staff command by staff");
    }

}
