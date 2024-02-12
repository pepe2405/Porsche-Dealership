package bg.sofia.uni.fmi.mjt.dealership.server.command;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.GetAllAvailableCarsCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.testdrives.AcceptTestDriveCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.testdrives.ReserveTestDriveCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.AddCarCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.ClearInventoryCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.GetCarByVinCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.inventory.RemoveCarCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.CreateUserCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.DeleteUserCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.LogoutCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.command.user.UpdateUserCommand;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.service.UserManagerServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * The CommandParser class is responsible for parsing commands from the DataContainer.
 * It uses instances of UserManagerServiceImpl and CarInventoryServiceImpl
 * to handle user and car related commands respectively.
 * It maintains a map of SocketChannel to User for handling user sessions
 * and a map of Car to BlockingQueue of User for handling test drive requests.
 * It provides a static method parse() to parse the command from the DataContainer
 * and return a Command object.
 * It provides several private methods to handle different commands such as
 * register, delete-user, update-user, add-car, clear-inventory, view-car,
 * view-all-cars, remove-car, login, logout, reserve-test-drive, and accept-test-drive.
 * If the command is invalid, it logs a warning and returns null.
 */
public class CommandParser {
    private static final LoggerImpl LOGGER = LoggerImpl.getInstance();

    public static Command parse(DataContainer dataContainer, UserManagerServiceImpl userManagerServiceImpl,
                                CarInventoryServiceImpl carInventory,
                                ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap,
                                ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests) {
        if (!CommandValidator.isValidCommand(dataContainer.message(), dataContainer.user())) {
            LOGGER.logWarning("Invalid command");
            return null;
        }

        return handleCommand(dataContainer, userManagerServiceImpl, carInventory, socketToUserMap, testDriveRequests);
    }

    private static Command handleCommand(DataContainer dataContainer, UserManagerServiceImpl userManagerServiceImpl,
                                         CarInventoryServiceImpl carInventory,
                                         ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap,
                                         ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests) {
        switch (extractCommand(dataContainer)) {
            case "register":
                return handleRegister(userManagerServiceImpl, dataContainer);
            case "delete-user":
                return handleDeleteUser(userManagerServiceImpl, dataContainer);
            case "update-password":
                return handleUpdateUser(userManagerServiceImpl, dataContainer);
            case "add-car":
                return handleAddCar(carInventory, dataContainer);
            case "clear-inventory":
                return handleClearInventory(carInventory, dataContainer);
            case "view-car":
                return handleViewCar(carInventory, dataContainer);
            case "view-all-cars":
                return handleViewAllCars(carInventory, dataContainer);
            case "remove-car":
                return handleRemoveCar(carInventory, dataContainer);
            case "login":
                return handleLogin(userManagerServiceImpl, dataContainer);
            case "logout":
                return handleLogout();
            case "reserve-test-drive":
                return handleReserveTestDrive(testDriveRequests, carInventory, dataContainer);
            case "accept-test-drive":
                return handleAcceptTestDrive(testDriveRequests, socketToUserMap, carInventory, dataContainer);
        }
        LOGGER.logWarning("Invalid command");
        return null;
    }

    private static String extractCommand(DataContainer dataContainer) {
        return dataContainer.message().split("\\s+")[0];
    }

    private static CreateUserCommand handleRegister(UserManagerServiceImpl userManagerServiceImpl,
                                                    DataContainer dataContainer) {
        final Iterator<String> iterator = getCommandParamsIterator(dataContainer.message());
        final String username = iterator.next();
        final String password = iterator.next();

        try {
            UserRole role = UserRole.valueOf(iterator.next().toUpperCase());
            return new CreateUserCommand(userManagerServiceImpl, username, password, role);
        } catch (IllegalArgumentException e) {
            return new CreateUserCommand(userManagerServiceImpl, username, password, null);
        }
    }

    private static DeleteUserCommand handleDeleteUser(UserManagerServiceImpl userManagerServiceImpl,
                                                      DataContainer dataContainer) {
        return new DeleteUserCommand(userManagerServiceImpl, dataContainer.user());
    }

    private static UpdateUserCommand handleUpdateUser(UserManagerServiceImpl userManagerServiceImpl,
                                                      DataContainer dataContainer) {
        final Iterator<String> iterator = getCommandParamsIterator(dataContainer.message());
        final String oldPassword = iterator.next();
        final String newPassword = iterator.next();

        return new UpdateUserCommand(userManagerServiceImpl, oldPassword, newPassword, dataContainer.user());
    }

    private static AddCarCommand handleAddCar(CarInventoryServiceImpl carInventory,
                                              DataContainer dataContainer) {
        final Iterator<String> iterator = getCommandParamsIterator(dataContainer.message());
        final String vin = iterator.next();

        try {
            final Car car = createCar(iterator, vin);
            return new AddCarCommand(carInventory, car, dataContainer.user());
        } catch (IllegalArgumentException e) {
            return new AddCarCommand(carInventory, null, dataContainer.user());
        }
    }

    private static ClearInventoryCommand handleClearInventory(CarInventoryServiceImpl carInventory,
                                                              DataContainer dataContainer) {
        return new ClearInventoryCommand(carInventory, dataContainer.user());
    }

    private static GetCarByVinCommand handleViewCar(CarInventoryServiceImpl carInventory,
                                                    DataContainer dataContainer) {
        final Iterator<String> iterator = getCommandParamsIterator(dataContainer.message());
        final String vin = iterator.next();

        return new GetCarByVinCommand(carInventory, vin, dataContainer.user());
    }

    private static GetAllAvailableCarsCommand handleViewAllCars(CarInventoryServiceImpl carInventory,
                                                                DataContainer dataContainer) {
        return new GetAllAvailableCarsCommand(carInventory, dataContainer.user());
    }

    private static RemoveCarCommand handleRemoveCar(CarInventoryServiceImpl carInventory,
                                                    DataContainer dataContainer) {
        final Iterator<String> iterator = getCommandParamsIterator(dataContainer.message());
        final String vin = iterator.next();

        return new RemoveCarCommand(carInventory, vin, dataContainer.user());
    }

    private static LoginCommand handleLogin(UserManagerServiceImpl userManagerServiceImpl,
                                            DataContainer dataContainer) {
        final Iterator<String> iterator = getCommandParamsIterator(dataContainer.message());
        final String username = iterator.next();
        final String password = iterator.next();

        return new LoginCommand(userManagerServiceImpl, username, password);
    }

    private static LogoutCommand handleLogout() {
        return new LogoutCommand();
    }

    private static ReserveTestDriveCommand handleReserveTestDrive(ConcurrentMap<Car,
                                                                    BlockingQueue<User>> testDriveRequests,
                                                                  CarInventoryServiceImpl carInventory,
                                                                  DataContainer dataContainer) {
        final Iterator<String> iterator = getCommandParamsIterator(dataContainer.message());
        final String vin = iterator.next();

        return new ReserveTestDriveCommand(testDriveRequests, carInventory, vin, dataContainer.user());
    }

    private static AcceptTestDriveCommand handleAcceptTestDrive(ConcurrentMap<Car,
                                                                    BlockingQueue<User>> testDriveRequests,
                                                                ConcurrentMap<SocketChannel,
                                                                    Optional<User>> socketToUserMap,
                                                                CarInventoryServiceImpl carInventory,
                                                                DataContainer dataContainer) {
        final Iterator<String> iterator = getCommandParamsIterator(dataContainer.message());
        final String vin = iterator.next();

        return new AcceptTestDriveCommand(testDriveRequests, socketToUserMap, dataContainer.user(), vin, carInventory);
    }

    private static Car createCar(Iterator<String> iterator, String vin) {
        try {
            final String model = iterator.next();
            final int year = Integer.parseInt(iterator.next());
            final String drive = iterator.next();
            final int cylinders = Integer.parseInt(iterator.next());
            final double displacement = Double.parseDouble(iterator.next());
            final String transmission = iterator.next();

            return new Car(vin, model, year, drive, cylinders, displacement, transmission);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid car data");
        }
    }

    private static Iterator<String> getCommandParamsIterator(String message) {
        final List<String> commandTokens = Arrays.asList(message.split("\\s+"));
        final Iterator<String> iterator = commandTokens.iterator();

        iterator.next();

        return iterator;
    }
}
