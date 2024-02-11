package bg.sofia.uni.fmi.mjt.dealership.server.command.testdrives;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.car.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.car.CarInventory;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class AcceptTestDriveCommand implements Command {
    private final ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests;
    ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap;
    private final User staff;
    private final String vin;
    private final CarInventory carInventory;
    private final Logger logger = Logger.getInstance();

    public AcceptTestDriveCommand(ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests,
                                  ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap,
                                  User staff, String vin, CarInventory carInventory) {
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
                return new DataContainer("No users in queue for test drive for car with VIN: " + vin,
                    staff.getRole(), staff);
            }

            User customer = testDriveQueue.poll();

            if (testDriveQueue.isEmpty()) {
                testDriveRequests.remove(car);
            }
            sendNotificationToCustomer(customer);
            return new DataContainer("Test drive accepted for user: " + customer.getUsername()
                                     + " for car with VIN: " + vin,
                staff.getRole(), staff);
        } catch (CarInventoryException e) {
            return new DataContainer("Car with VIN: " + vin + " not found", staff.getRole(), staff);
        }
    }

    private void sendNotificationToCustomer(User customer) {
        Gson gson = new Gson();
        String json = gson.toJson(new DataContainer("Test drive accepted for car with VIN: " + vin,
            customer.getRole(), customer));

        ByteBuffer buffer = ByteBuffer.wrap(json.getBytes());
        SocketChannel sc = getSocketChannelByUser(customer);
        if (sc != null) {
            try {
                sc.write(buffer);
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
