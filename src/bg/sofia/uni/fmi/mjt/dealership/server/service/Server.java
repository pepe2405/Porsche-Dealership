package bg.sofia.uni.fmi.mjt.dealership.server.service;

/**
 * The Server interface provides methods for starting the server.
 * It defines constants for the server port, host, and buffer size.
 */
public interface Server {


    /**
     * The size of the buffer used by the server.
     */
    static final int BUFFER_SIZE = 4056;

    /**
     * Starts the server.
     */
    void start();
}