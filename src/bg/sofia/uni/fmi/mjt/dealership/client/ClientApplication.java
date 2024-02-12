package bg.sofia.uni.fmi.mjt.dealership.client;

/**
 * The ClientApplication class is the entry point of the client application.
 * It creates an instance of the ClientImp class and starts the client application.
 */
public class ClientApplication {
    /**
     * The main method is the entry point of the client application.
     * It creates an instance of the ClientImp class and starts the client application.
     *
     * @param args command-line arguments passed to the application. It is not used in this application.
     */
    public static void main(String... args) {
        final String serverHost = "localhost";
        final int serverPort = 7777;

        new ClientImp(serverHost, serverPort).start();
    }
}
