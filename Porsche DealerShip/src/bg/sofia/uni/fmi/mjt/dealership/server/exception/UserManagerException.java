package bg.sofia.uni.fmi.mjt.dealership.server.exception;

public class UserManagerException extends Exception {
    public UserManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserManagerException(String message) {
        super(message);
    }
}
