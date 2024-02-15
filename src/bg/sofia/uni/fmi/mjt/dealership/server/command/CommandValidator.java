package bg.sofia.uni.fmi.mjt.dealership.server.command;

import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The CommandValidator class is responsible for validating commands based on the user's role
 * and the command's requirements.
 * It contains a set of allowed commands for each user role (Customer, Staff, Unauthorized)
 * and a map of commands with their required argument count.
 * The main method is `isValidCommand` which checks if a given command input is valid for a specific user.
 */
public class CommandValidator {
    private static final Map<String, Integer> ALLOWED_COMMANDS = initializeAllowedCommands();
    private static final Set<String> STAFF_COMMANDS = initializeStaffCommands();
    private static final Set<String> CUSTOMER_COMMANDS = initializeCustomerCommands();
    private static final Set<String> UNAUTHORIZED_COMMANDS = initializeUnauthorizedCommands();

    private static final int ONE_ARGUMENT = 1;
    private static final int TWO_ARGUMENTS = 2;
    private static final int THREE_ARGUMENTS = 3;
    private static final int FOUR_ARGUMENTS = 4;
    private static final int EIGHT_ARGUMENTS = 8;

    /**
     * Checks if the provided command input is valid for the given user.
     *
     * @param input the command input string
     * @param user the user who is executing the command
     * @return true if the command is valid, false otherwise
     */
    public static boolean isValidCommand(String input, User user) {
        if (input == null || input.isBlank()) {
            return false;
        }

        final String[] commandLine = input.split("\\s+");
        final String command = commandLine[0];

        if (!ALLOWED_COMMANDS.containsKey(command) || commandLine.length != ALLOWED_COMMANDS.get(command)) {
            return false;
        }
        UserRole role = UserRole.UNAUTHORIZED;
        if (user != null) {
            role = user.getRole();
        }

        return switch (role) {
            case CUSTOMER -> CUSTOMER_COMMANDS.contains(command);
            case STAFF -> STAFF_COMMANDS.contains(command);
            case UNAUTHORIZED -> UNAUTHORIZED_COMMANDS.contains(command);
        };
    }

    private static Map<String, Integer> initializeAllowedCommands() {
        Map<String, Integer> allowedCommands = new HashMap<>();

        allowedCommands.put("login", THREE_ARGUMENTS);
        allowedCommands.put("register", FOUR_ARGUMENTS);
        allowedCommands.put("logout", ONE_ARGUMENT);
        allowedCommands.put("update-password", THREE_ARGUMENTS);
        allowedCommands.put("delete-user", ONE_ARGUMENT);

        allowedCommands.put("view-all-cars", ONE_ARGUMENT);
        allowedCommands.put("view-car", TWO_ARGUMENTS);
        allowedCommands.put("reserve-test-drive", TWO_ARGUMENTS);

        allowedCommands.put("add-car", EIGHT_ARGUMENTS);
        allowedCommands.put("remove-car", TWO_ARGUMENTS);
        allowedCommands.put("clear-inventory", ONE_ARGUMENT);
        allowedCommands.put("accept-test-drive", TWO_ARGUMENTS);

        return allowedCommands;
    }

    private static Set<String> initializeStaffCommands() {
        return Set.of("add-car", "remove-car", "view-all-cars",
            "clear-inventory", "accept-test-drive",
            "logout", "update-password", "delete-user");
    }

    private static Set<String> initializeCustomerCommands() {
        return Set.of("view-all-cars", "view-car", "reserve-test-drive",
            "logout", "update-password", "delete-user");
    }

    private static Set<String> initializeUnauthorizedCommands() {
        return Set.of("login", "register");
    }
}
