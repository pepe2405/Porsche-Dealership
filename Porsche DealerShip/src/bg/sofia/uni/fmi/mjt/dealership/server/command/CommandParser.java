package bg.sofia.uni.fmi.mjt.dealership.server.command;

import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.GetAllAvailableCarsCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.testdrives.AcceptTestDriveCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.testdrives.ReserveTestDriveCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.car.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.car.CarInventory;
import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.AddCarCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.ClearInventoryCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.GetCarByVinCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.RemoveCarCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.CreateUserCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.DeleteUserCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.LogoutCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.UpdateUserCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserManager;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class CommandParser {
    private static CommandValidator validator = CommandValidator.getInstance();
    public static Command parse(UserManager userManager, CarInventory carInventory,
                                String input, User user, UserRole role,
                                ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap,
                                ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests) {

        List<String> tokens = Arrays.asList(input.split("\\s+"));
        Iterator<String> iterator = tokens.iterator();
        String command = iterator.next();
        if (!validator.isValidCommand(input, role)) {
            Logger.getInstance().logWarning("Invalid command");
            return null;
        }

        return handleCommand(userManager, carInventory, iterator, user,
            command, socketToUserMap, testDriveRequests);
    }

    private static Command handleCommand(UserManager userManager, CarInventory carInventory,
                                         Iterator<String> iterator, User user, String command,
                                         ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap,
                                         ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests) {
        switch (command) {
            case "register":
                return handleRegister(userManager, iterator);
            case "delete-user":
                return handleDeleteUser(userManager, user);
            case "update-user":
                return handleUpdateUser(userManager, iterator, user);
            case "add-car":
                return handleAddCar(carInventory, iterator, user);
            case "clear-inventory":
                return handleClearInventory(carInventory, user);
            case "view-car":
                return handleViewCar(carInventory, iterator, user);
            case "view-all-cars":
                return handleViewAllCars(carInventory, user);
            case "remove-car":
                return handleRemoveCar(carInventory, iterator, user);
            case "login":
                return handleLogin(userManager, iterator);
            case "logout":
                return handleLogout();
            case "reserve-test-drive":
                return handleReserveTestDrive(testDriveRequests, carInventory, iterator, user);
            case "accept-test-drive":
                return handleAcceptTestDrive(testDriveRequests, socketToUserMap, carInventory, iterator, user);
            default:
                Logger.getInstance().logWarning("Invalid command");
                return null;
        }
    }

    private static CreateUserCommand handleRegister(UserManager userManager, Iterator<String> iterator) {
        String username = iterator.next();
        String password = iterator.next();
        try {
            UserRole role = UserRole.valueOf(iterator.next().toUpperCase());
            return new CreateUserCommand(userManager, username, password, role);
        } catch (IllegalArgumentException e) {
            return new CreateUserCommand(userManager, username, password, null);
        }
    }

    private static DeleteUserCommand handleDeleteUser(UserManager userManager, User user) {
        return new DeleteUserCommand(userManager, user);
    }

    private static UpdateUserCommand handleUpdateUser(UserManager userManager, Iterator<String> iterator, User user) {
        String oldPassword = iterator.next();
        String newPassword = iterator.next();
        return new UpdateUserCommand(userManager, oldPassword, newPassword, user);
    }

    private static LoginCommand handleLogin(UserManager userManager, Iterator<String> iterator) {
        String username = iterator.next();
        String password = iterator.next();
        return new LoginCommand(userManager, username, password);
    }

    private static LogoutCommand handleLogout() {
        return new LogoutCommand();
    }

    private static AddCarCommand handleAddCar(CarInventory carInventory, Iterator<String> iterator, User user) {
        String vin = iterator.next();
        try {
            Car car = createCar(iterator, vin);
            return new AddCarCommand(carInventory, car, user);
        } catch (IllegalArgumentException e) {
            return new AddCarCommand(carInventory, null, user);
        }
    }

    private static ClearInventoryCommand handleClearInventory(CarInventory carInventory, User user) {
        return new ClearInventoryCommand(carInventory, user);
    }

    private static GetCarByVinCommand handleViewCar(CarInventory carInventory, Iterator<String> iterator, User user) {
        String vin = iterator.next();
        return new GetCarByVinCommand(carInventory, vin, user);
    }

    private static RemoveCarCommand handleRemoveCar(CarInventory carInventory, Iterator<String> iterator, User user) {
        String vin = iterator.next();
        return new RemoveCarCommand(carInventory, vin, user);
    }

    private static GetAllAvailableCarsCommand handleViewAllCars(CarInventory carInventory, User user) {
        return new GetAllAvailableCarsCommand(carInventory, user);
    }

    private static ReserveTestDriveCommand handleReserveTestDrive(ConcurrentMap<Car,
                                                                    BlockingQueue<User>> testDriveRequests,
                                                                  CarInventory carInventory, Iterator<String> iterator,
                                                                  User user) {
        String vin = iterator.next();
        return new ReserveTestDriveCommand(testDriveRequests, carInventory, vin, user);
    }

    private static AcceptTestDriveCommand handleAcceptTestDrive(ConcurrentMap<Car,
                                                                BlockingQueue<User>> testDriveRequests,
                                                                ConcurrentMap<SocketChannel,
                                                                    Optional<User>> socketToUserMap,
                                                                CarInventory carInventory, Iterator<String> iterator,
                                                                User user) {
        String vin = iterator.next();
        return new AcceptTestDriveCommand(testDriveRequests, socketToUserMap, user, vin, carInventory);
    }

    private static Car createCar(Iterator<String> iterator, String vin) {
        try {
            String model = iterator.next();
            int year = Integer.parseInt(iterator.next());
            String drive = iterator.next();
            int cylinders = Integer.parseInt(iterator.next());
            double displacement = Double.parseDouble(iterator.next());
            String transmission = iterator.next();

            return new Car(vin, model, year, drive, cylinders,
                displacement, transmission);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid car data");
        }
    }
}
