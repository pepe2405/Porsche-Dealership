package bg.sofia.uni.fmi.mjt.dealership.server.exception;

/**
 * The UserManagerException class is a custom exception that extends RuntimeException.
 * It is thrown when there are issues related to user management in the dealership system.
 */
public class UserManagerException extends RuntimeException {
    /**
     * Constructs a new UserManagerException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the Throwable.getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method)
     */
    public UserManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UserManagerException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the Throwable.getMessage() method)
     */
    public UserManagerException(String message) {
        super(message);
    }
}
