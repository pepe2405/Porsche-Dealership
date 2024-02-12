package bg.sofia.uni.fmi.mjt.dealership.server.command.testdrives;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.service.CarInventoryServiceImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * The AcceptTestDriveCommand class implements the Command interface and provides the functionality
 * for accepting a test drive request.
 * It uses an instance of CarInventoryServiceImpl to get the car by its VIN.
 * It uses an instance of LoggerImpl to log the operation.
 * It maintains a map of test drive requests, a map of socket channels to users,
 * the staff user who performs the operation,
 * the VIN of the car for the test drive, and the car inventory service.
 * It provides a method to execute the command which accepts the test drive request from the queue,
 * sends a notification to the customer, and returns a DataContainer with the result of the operation.
 * If the operation is successful, it logs the operation and returns a DataContainer with a success message.
 * If the operation fails due to a CarInventoryException, it logs a warning
 * and returns a DataContainer with an error message.
 */
public class AcceptTestDriveCommand implements Command {
    private final LoggerImpl logger = LoggerImpl.getInstance();

    private final ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests;
    ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap;
    private final User staff;
    private final String vin;
    private final CarInventoryServiceImpl carInventory;

    public AcceptTestDriveCommand(ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests,
                                  ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap,
                                  User staff, String vin, CarInventoryServiceImpl carInventory) {
        this.testDriveRequests = testDriveRequests;
        this.staff = staff;
        this.vin = vin;
        this.carInventory = carInventory;
        this.socketToUserMap = socketToUserMap;
    }

    @Override
    public DataContainer execute() {
        try {
            Car car = carInventory.getCarByVin(vin);
            BlockingQueue<User> testDriveQueue = testDriveRequests.get(car);
            if (testDriveQueue == null || testDriveQueue.isEmpty()) {
                logger.logWarning("No users in queue for test drive for car with VIN: " + vin);
                return new DataContainer("No users in queue for test drive for car with VIN: " + vin, staff);
            }

            User customer = testDriveQueue.poll();

            if (testDriveQueue.isEmpty()) {
                testDriveRequests.remove(car);
            }
            sendNotificationToCustomer(customer);
            logger.log("Test drive accepted");
            return new DataContainer("Test drive accepted for user: " + customer.getUsername()
                                     + " for car with VIN: " + vin, staff);
        } catch (CarInventoryException e) {
            logger.logWarning("Car not found.");
            return new DataContainer("Car with VIN: " + vin + " not found", staff);
        }
    }

    private void sendNotificationToCustomer(User customer) {
        Gson gson = new Gson();
        String json = gson.toJson(new DataContainer("Test drive accepted for car with VIN: " + vin, customer));

        ByteBuffer buffer = ByteBuffer.wrap(json.getBytes());
        SocketChannel sc = getSocketChannelByUser(customer);
        if (sc != null) {
            try {
                sc.write(buffer);
                logger.log("Notification sent to customer: " + customer.getUsername());
            } catch (IOException e) {
                logger.logWarning("Error while sending notification to customer: " + e.getMessage());
            }
        }
    }

    public SocketChannel getSocketChannelByUser(User user) {
        for (Map.Entry<SocketChannel, Optional<User>> entry : socketToUserMap.entrySet()) {
            if (entry.getValue().isPresent() && entry.getValue().get().equals(user)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
