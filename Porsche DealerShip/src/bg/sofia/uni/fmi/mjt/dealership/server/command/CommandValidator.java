package bg.sofia.uni.fmi.mjt.dealership.server.command;

import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandValidator {
    private static final Map<String, Integer> ALLOWED_COMMANDS = initializeAllowedCommands();
    private static final Set<String> STAFF_COMMANDS = initializeStaffCommands();
    private static final Set<String> CUSTOMER_COMMANDS = initializeCustomerCommands();
    private static final Set<String> UNAUTHORIZED_COMMANDS = initializeUnauthorizedCommands();

    private static CommandValidator instance;

    private CommandValidator() {
    }

    public static CommandValidator getInstance() {
        if (instance == null) {
            instance = new CommandValidator();
        }
        return instance;
    }

    private static Map<String, Integer> initializeAllowedCommands() {
        Map<String, Integer> allowedCommands = new HashMap<>();

        allowedCommands.put("login", 3);
        allowedCommands.put("register", 4);
        allowedCommands.put("logout", 1);
        allowedCommands.put("update-password", 3);
        allowedCommands.put("delete-user", 1);

        allowedCommands.put("view-all-cars", 1);
        allowedCommands.put("view-car", 2);
        allowedCommands.put("reserve-test-drive", 2);

        allowedCommands.put("add-car", 8);
        allowedCommands.put("remove-car", 2);
        allowedCommands.put("clear-inventory", 1);
        allowedCommands.put("accept-test-drive", 2);

        return allowedCommands;
    }

    private static Set<String> initializeStaffCommands() {
        return Set.of("add-car", "remove-car", "logout", "update-password", "clear-inventory", "delete-user", "accept-test-drive");
    }

    private static Set<String> initializeCustomerCommands() {
        return Set.of("view-all-cars", "view-car", "logout", "update-password", "delete-user", "reserve-test-drive");
    }

    private static Set<String> initializeUnauthorizedCommands() {
        return Set.of("login", "register");
    }

    public boolean isValidCommand(String input, UserRole role) {
        if (input == null || input.isBlank()) {
            return false;
        }

        String[] commandLine = input.split("\\s+");
        String command = commandLine[0];

        if (!ALLOWED_COMMANDS.containsKey(command) || commandLine.length != ALLOWED_COMMANDS.get(command)) {
            return false;
        }

        return switch (role) {
            case CUSTOMER -> CUSTOMER_COMMANDS.contains(command);
            case STAFF -> STAFF_COMMANDS.contains(command);
            case UNAUTHORIZED -> UNAUTHORIZED_COMMANDS.contains(command);
        };
    }
}
