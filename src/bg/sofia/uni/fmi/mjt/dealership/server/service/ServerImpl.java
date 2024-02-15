package bg.sofia.uni.fmi.mjt.dealership.server.service;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.util.LoggerImpl;
import bg.sofia.uni.fmi.mjt.dealership.server.command.Command;
import bg.sofia.uni.fmi.mjt.dealership.server.command.CommandParser;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
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

/**
 * The ServerImpl class implements the Server interface and provides the functionality
 * for the server side of the dealership application.
 * It uses Gson to serialize and deserialize data to and from JSON format.
 * It uses a ByteBuffer to read/write data from/to the client.
 * It maintains the current user and their role, and a flag to indicate whether the server is working.
 * It provides methods to start the server, handle client requests, and close the server.
 * It also provides methods to handle user input and print help messages based on the user's role.
 */
public class ServerImpl implements Server {

    private final LoggerImpl logger;
    private final UserManagerServiceImpl userManagerServiceImpl;
    private final CarInventoryServiceImpl carInventory;
    private final Gson gson;
    private final String serverHost;
    private final int serverPort;

    private final ConcurrentMap<SocketChannel, Optional<User>> socketToUserMap;
    private final ConcurrentMap<Car, BlockingQueue<User>> testDriveRequests;
    private final ByteBuffer buffer;

    public ServerImpl(String host, int port) {
        logger = LoggerImpl.getInstance();
        logger.clearLogFile();
        userManagerServiceImpl = new UserManagerServiceImpl();
        carInventory = new CarInventoryServiceImpl();
        gson = new GsonBuilder().create();
        serverHost = host;
        serverPort = port;

        socketToUserMap = new ConcurrentHashMap<>();
        testDriveRequests = new ConcurrentHashMap<>();
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    @Override
    public void start() {
        ServerSocketChannel serverSocketChannel = null;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(serverHost, serverPort));
            serverSocketChannel.configureBlocking(false);

            final Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            handleClientRequests(selector);
        } catch (IOException e) {
            logger.logError("Error occurred while starting server.", e);
            throw new RuntimeException(e);
        } finally {
            closeServer(serverSocketChannel);
        }
    }

    private void handleClientRequests(Selector selector) throws IOException {
        System.out.println("Server started");

        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }

            final Set<SelectionKey> selectedKeys = selector.selectedKeys();
            final Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                final SelectionKey key = keyIterator.next();

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
        final SocketChannel socketChannel = (SocketChannel) key.channel();

        final DataContainer clientMessage = readClientMessage(socketChannel);
        if (clientMessage == null) {
            System.out.println("Client has closed the connection: " + socketChannel.getLocalAddress());
            socketChannel.close();
            socketToUserMap.remove(socketChannel);
            return;
        }
        System.out.println("Received: " + clientMessage.message());

        final DataContainer messageResponse = handleClientRequest(clientMessage);
        final String messageResponseJson = gson.toJson(messageResponse);
        socketToUserMap.put(socketChannel, Optional.ofNullable(messageResponse.user()));

        sendServerResponse(messageResponseJson, socketChannel);
    }

    private void sendServerResponse(String messageResponseJson, SocketChannel socketChannel) throws IOException {
        buffer.clear();
        buffer.put(messageResponseJson.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }

    private DataContainer readClientMessage(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        int readBytesCount = 0;

        try {
            readBytesCount = socketChannel.read(buffer);
        } catch (IOException e) {
            return null;
        }

        if (readBytesCount < 0) {
            return null;
        }

        buffer.flip();
        final byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        final String clientMessage = new String(bytes, StandardCharsets.UTF_8).trim();

        return gson.fromJson(clientMessage, DataContainer.class);
    }

    private void handleAcceptableKey(SelectionKey key) throws IOException {
        final ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        final SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(key.selector(), SelectionKey.OP_READ);
        System.out.println("Connection Accepted: " + accept.getLocalAddress());
    }

    private DataContainer handleClientRequest(DataContainer dataContainer) {
        final Command command = CommandParser.parse(dataContainer, userManagerServiceImpl,
                                                    carInventory, socketToUserMap, testDriveRequests);
        if (command == null) {
            return new DataContainer("Invalid command. Type help to see available commands.",
                dataContainer.user());
        }

        return command.execute();
    }

    private void closeServer(ServerSocketChannel serverSocketChannel) {
        if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
            try {
                serverSocketChannel.close();
            } catch (IOException ioe) {
                logger.logError("Error occurred while closing server.", ioe);
                throw new RuntimeException(ioe);
            }
        }
    }
}
