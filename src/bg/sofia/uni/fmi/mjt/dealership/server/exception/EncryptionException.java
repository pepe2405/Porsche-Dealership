package bg.sofia.uni.fmi.mjt.dealership.server.exception;

/**
 * The EncryptionException class is a custom exception that extends RuntimeException.
 * It is thrown when there are issues related to encryption in the dealership system.
 */
public class EncryptionException extends RuntimeException {
    /**
     * Constructs a new EncryptionException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the Throwable.getMessage() method)
     */
    public EncryptionException(String message) {
        super(message);
    }

    /**
     * Constructs a new EncryptionException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the Throwable.getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method)
     */
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
