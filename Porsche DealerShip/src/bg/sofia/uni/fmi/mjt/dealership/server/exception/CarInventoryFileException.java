package bg.sofia.uni.fmi.mjt.dealership.server.exception;

public class CarInventoryFileException extends Exception {
    public CarInventoryFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CarInventoryFileException(String message) {
        super(message);
    }
}
