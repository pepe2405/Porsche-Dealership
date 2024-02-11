package bg.sofia.uni.fmi.mjt.dealership.server;

import bg.sofia.uni.fmi.mjt.dealership.server.car.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.dealership.server.car.CarInventory;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.command.CommandParser;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PorscheDealershipServer {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 4056;

    private ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests = new ConcurrentHashMap<>();
    private final Logger logger;
    private final UserManager userManager;
    private final CarInventory carInventory;
    private final ExecutorService executorService;

    private ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private final Gson gson = new GsonBuilder().create();

    public PorscheDealershipServer() {
        this.userManager = new UserManager();
        this.carInventory = new CarInventory();
        this.logger = Logger.getInstance();
        logger.clearLogFile();
        this.executorService = Executors.newCachedThreadPool();
    }

    void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            handleClientRequests(selector);
        } catch (IOException e) {
            logger.logError("Server channel closed unexpectedly", e);
            safelyShutdown();
        }
    }

    private void handleClientRequests(Selector selector) throws IOException {
        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isReadable()) {
                    handleReadableKey(key);
                } else if (key.isAcceptable()) {
                    handleAcceptableKey(key);
                }

                keyIterator.remove();
            }
        }
    }

    private void handleReadableKey(SelectionKey key) throws IOException {
        System.out.println("Reading from client");
        SocketChannel sc = (SocketChannel) key.channel();

        buffer.clear();
        int r = sc.read(buffer);
        if (r < 0) {
            System.out.println("Client has closed the connection");
            sc.close();
            return;
        }

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        String json = new String(bytes, StandardCharsets.UTF_8).trim();
        DataContainer dataContainer = gson.fromJson(json, DataContainer.class);
        System.out.println("Received: " + dataContainer.message());

        DataContainer serverDataContainer = handleServerRequest(dataContainer);
        json = gson.toJson(serverDataContainer);
        socketToUserMap.put(sc, Optional.ofNullable(serverDataContainer.user()));

        buffer.clear();
        buffer.put(json.getBytes());
        buffer.flip();
        sc.write(buffer);
    }

    private void handleAcceptableKey(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(key.selector(), SelectionKey.OP_READ);
        System.out.println("Connection Accepted: " + accept.getLocalAddress());
    }

    private DataContainer handleServerRequest(DataContainer dataContainer) {
        String clientInput = dataContainer.message();
        Command command = CommandParser.parse(userManager, carInventory,
            clientInput, dataContainer.user(), dataContainer.role(), socketToUserMap, testDriveRequests);
        if (command == null) {
            return new DataContainer("Invalid command", dataContainer.role(), dataContainer.user());
        }
        return command.execute();
    }

    private void safelyShutdown() {
        for (var socketChannel : socketToUserMap.keySet()) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                logger.logError("Error while closing socket channel", e);
            }
        }
        socketToUserMap.clear();
        testDriveRequests.clear();
        executorService.shutdown();
        logger.log("Server has been safely shut down");
    }

    public static void main(String[] args) {
        PorscheDealershipServer server = new PorscheDealershipServer();
        server.start();
    }
}
