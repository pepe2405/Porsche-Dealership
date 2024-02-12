package bg.sofia.uni.fmi.mjt.dealership.server.model;

/**
 * The DataContainer record represents a container for data in the client-server architecture.
 * It contains a message and a User object.
 */
public record DataContainer(String message, User user) {
}
