package bg.sofia.uni.fmi.mjt.dealership.client;

/**
 * The ClientApplication class is the entry point of the client application.
 * It creates an instance of the ClientImp class and starts the client application.
 */
public interface Client {
    /**
     * The size of the buffer used for reading/writing data from/to the server.
     */
    static final int BUFFER_SIZE = 4056;

    /**
     * The command to exit the client application.
     */
    static final String EXIT_COMMAND = "exit";

    /**
     * The command to display help information.
     */
    static final String HELP_COMMAND = "help";

    /**
     * The command to display the status of the client.
     */
    static final String STATUS_COMMAND = "status";

    /**
     * Starts the client application.
     */
    void start();
}