package bg.sofia.uni.fmi.mjt.dealership.server.exception;

/**
 * The CarInventoryException class is a custom exception that extends RuntimeException.
 * It is thrown when there are issues related to the car inventory in the dealership system.
 */
public class CarInventoryException extends RuntimeException {
    /**
     * Constructs a new CarInventoryException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the Throwable.getMessage() method)
     * @param cause   the cause (which is saved for later retrieval by the Throwable.getCause() method)
     */
    public CarInventoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new CarInventoryException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the Throwable.getMessage() method)
     */
    public CarInventoryException(String message) {
        super(message);
    }
}
