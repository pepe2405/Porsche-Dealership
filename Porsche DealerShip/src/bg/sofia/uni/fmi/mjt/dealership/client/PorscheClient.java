package bg.sofia.uni.fmi.mjt.dealership.client;

import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;
import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PorscheClient {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 4056;
    private static final String EXIT_COMMAND = "exit";
    private static final String HELP_COMMAND = "help";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private UserRole role = UserRole.UNAUTHORIZED;
    private User user = null;
    private boolean isServerWorking = true;

    Scanner scanner = new Scanner(System.in);
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void start() {
        try (SocketChannel socketChannel = connectToServer(SERVER_HOST, SERVER_PORT)) {
            executorService.submit(() -> {
                while (isServerWorking) {
                    try {
                        readServerResponse(socketChannel);
                    } catch (IOException e) {
                        System.err.println("Error reading server response: " + e.getMessage());
                        isServerWorking = false;
                    }
                }
            });

            while (isServerWorking) {
                String clientInput = scanner.nextLine();
                try {
                    handleUserInput(clientInput, socketChannel);
                } catch (IOException e) {
                    System.err.println("Error sending user input to server: " + e.getMessage());
                    isServerWorking = false;
                }
            }
            scanner.close();
            executorService.shutdown();
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleUserInput(String clientInput, SocketChannel socketChannel) throws IOException {
        DataContainer serverRequest = new DataContainer(clientInput, role, user);
        if (clientInput.equalsIgnoreCase(EXIT_COMMAND)) {
            disconnectFromServer(socketChannel);
            return;
        }

        if (clientInput.equalsIgnoreCase(HELP_COMMAND)) {
//            printHelp();
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

    private void readServerResponse(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        socketChannel.read(buffer);
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        DataContainer dataContainer = gson.fromJson(new String(bytes, StandardCharsets.UTF_8), DataContainer.class);
        user = dataContainer.user();
        role = dataContainer.role();

        System.out.println("Response from server:" + System.lineSeparator() + dataContainer.message());
    }

    private SocketChannel connectToServer(String host, int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(host, port));
        System.out.println("[CONNECTED] Connected to the server.");
        isServerWorking = true;
        return socketChannel;
    }

    private void disconnectFromServer(SocketChannel socketChannel) throws IOException {
        socketChannel.close();
        System.out.println("[DISCONNECTED] Disconnected from the server.");
        isServerWorking = false;
    }

    public static void main(String[] args) {
        PorscheClient client = new PorscheClient();
        client.start();
    }
}
