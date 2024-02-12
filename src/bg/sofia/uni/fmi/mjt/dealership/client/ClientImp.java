package bg.sofia.uni.fmi.mjt.dealership.client;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import bg.sofia.uni.fmi.mjt.dealership.server.model.enums.UserRole;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The ClientImp class implements the Client interface and provides the functionality
 * for the client side of the dealership application.
 * It uses a single-threaded executor service to handle server responses and a scanner to read user input.
 * It uses Gson to serialize and deserialize data to and from JSON format.
 * It uses a ByteBuffer to read/write data from/to the server.
 * It maintains the current user and their role, and a flag to indicate whether the server is working.
 * It provides methods to start the client, connect to the server, disconnect from the server,
 * send messages to the server, and handle server responses.
 * It also provides methods to handle user input and print help messages based on the user's role.
 */
public class ClientImp implements Client {
    private final LoggerImpl logger = LoggerImpl.getInstance();
    private final Gson gson;
    private final ByteBuffer buffer;
    private final String serverHost;
    private final int serverPort;

    private UserRole role;
    private User user;
    private boolean isServerWorking;

    private final Scanner scanner;
    private final ExecutorService executorService;

    public ClientImp(String host, int port) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        serverHost = host;
        serverPort = port;

        role = UserRole.UNAUTHORIZED;
        user = null;
        isServerWorking = true;

        scanner = new Scanner(System.in);
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void start() {
        SocketChannel socketChannel = null;
        try {
            socketChannel = connectToServer(serverHost, serverPort);
            final SocketChannel finalSocketChannel = socketChannel;

            readServerResponse(finalSocketChannel);
            sendServerMessage(socketChannel);
        } catch (UnresolvedAddressException | ConnectException e) {
            logger.logError("Cannot connect to the server", e);
            System.err.println("[ERROR] Unable to connect to the server. Please check the server status.");
        } catch (IOException e) {
            logger.logError("Error occurred with the client.", e);
            System.err.println("[ERROR] There was a problem with the server.");
        } finally {
            closeClient(socketChannel);
        }
    }

    private void readServerResponse(SocketChannel finalSocketChannel) {
        executorService.submit(() -> {
            while (isServerWorking) {
                try {
                    parseServerRequest(finalSocketChannel);
                } catch (IOException e) {
                    System.err.println("Error reading server response: " + e.getMessage());
                    isServerWorking = false;
                }
            }
        });
    }

    private synchronized void parseServerRequest(SocketChannel socketChannel) throws IOException {
        try {
            buffer.clear();
            socketChannel.read(buffer);
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            DataContainer dataContainer = gson.fromJson(new String(bytes, StandardCharsets.UTF_8), DataContainer.class);
            user = dataContainer.user();
            if (user != null) {
                role = dataContainer.user().getRole();
            }

            System.out.println("Response from server:" + System.lineSeparator() + dataContainer.message() +
                                System.lineSeparator());
        } catch (IOException e) {
            logger.logError("Server stopped.", e);
            System.out.println("Connection with the server closed. Exiting the client.");
            executorService.shutdownNow();
            isServerWorking = false;
        }
    }

    private void sendServerMessage(SocketChannel socketChannel) {
        while (isServerWorking) {
            String clientInput = scanner.nextLine();
            try {
                handleUserInput(clientInput, socketChannel);
            } catch (IOException e) {
                System.err.println("Error sending user input to server: " + e.getMessage());
                isServerWorking = false;
            }
        }
    }

    private void handleUserInput(String clientInput, SocketChannel socketChannel) throws IOException {
        DataContainer serverRequest = new DataContainer(clientInput, user);
        if (clientInput.equalsIgnoreCase(EXIT_COMMAND)) {
            disconnectFromServer(socketChannel);
            return;
        }

        if (clientInput.equalsIgnoreCase(HELP_COMMAND)) {
            printHelp();
            return;
        }

        if (clientInput.equalsIgnoreCase(STATUS_COMMAND)) {
            printStatus();
            return;
        }

        sendCommandToServer(serverRequest, socketChannel);
    }

    private void sendCommandToServer(DataContainer serverRequest, SocketChannel socketChannel) throws IOException {
        String json = gson.toJson(serverRequest);
        buffer.clear();
        buffer.put(json.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();
        System.out.println("Command sent to the server! Waiting for response.");
    }

    private void printStatus() {
        if (user != null) {
            System.out.println("You are currently logged in as " + user.getUsername()
                               + " with role " + user.getRole() + System.lineSeparator());
        } else {
            System.out.println("You are not logged in." + System.lineSeparator());
        }
    }

    private void printHelp() {
        switch (role) {
            case UNAUTHORIZED -> printUnauthorizedHelp();
            case STAFF -> printStaffHelp();
            case CUSTOMER -> printCustomerHelp();
        }
    }

    private void printUnauthorizedHelp() {
        System.out.println("""
            You are not authorized to use the system. Please login or register.
            Commands:
            login <username> <password> - login to the system
            register <username> <password> - register a new user
            status - view your status
            help - print this message
            exit - exit the system
            """);
    }

    private void printStaffHelp() {
        System.out.println("""
            You are a staff member. You can add, remove, view cars and accept test drives.
            Commands:
            add-car <vin> <model> <year> <drive> <cylinders> <displacement> <transmission> - add a new car
            remove-car <vin> - remove a car
            view-all-cars - view all cars
            clear-inventory - clear the inventory
            accept-test-drive <vin> - accept a test drive
            update-password <old-password> <new-password> - update your password
            delete-user - delete your account
            status - view your status
            help - print this message
            exit - exit the system
            """);
    }

    private void printCustomerHelp() {
        System.out.println("""
            You are a customer. You can view cars and reserve test drives.
            Commands:
            view-all-cars - view all cars
            view-car <vin> - view a car by vin number
            reserve-test-drive <vin> - reserve a test drive
            update-password <old-password> <new-password> - update your password
            delete-user - delete your account
            status - view your status
            help - print this message
            exit - exit the system
            """);
    }

    private SocketChannel connectToServer(String host, int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(host, port));
        System.out.println("[CONNECTED] Connected to the server. " + System.lineSeparator() +
                           "Type help to view all commands, status to view your status.");
        isServerWorking = true;
        return socketChannel;
    }

    private void disconnectFromServer(SocketChannel socketChannel) throws IOException {
        socketChannel.close();
        System.out.println("[DISCONNECTED] Disconnected from the server.");
        isServerWorking = false;
    }

    private void closeClient(SocketChannel socketChannel) {
        scanner.close();
        executorService.shutdownNow();
        if (socketChannel != null && socketChannel.isOpen()) {
            try {
                socketChannel.close();
            } catch (IOException ioe) {
                logger.logError("Error occurred while closing client.", ioe);
                throw new RuntimeException(ioe);
            }
        }
    }
}
