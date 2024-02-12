package bg.sofia.uni.fmi.mjt.dealership.server;

import bg.sofia.uni.fmi.mjt.dealership.server.service.ServerImpl;

/**
 * The ServerApplication class is the entry point of the server side of the application.
 * It creates a new instance of the ServerImpl class and starts the server.
 * This class is responsible for initializing and starting the server side of the application.
 * It uses the ServerImpl class to handle the server operations.
 */
public class ServerApplication {
    /**
     * The main method that starts the server side of the application.
     * It creates a new instance of the ServerImpl class and starts the server.
     *
     * @param args command line arguments, not used in this application
     */
    public static void main(String[] args) {
        final String serverHost = "localhost";
        final int serverPort = 7777;

        new ServerImpl(serverHost, serverPort).start();
    }
}
