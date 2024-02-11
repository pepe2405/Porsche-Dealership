package bg.sofia.uni.fmi.mjt.dealership.server.exception;

public class UserManagerFileException extends Exception {
    public UserManagerFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserManagerFileException(String message) {
        super(message);
    }
}
