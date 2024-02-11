package bg.sofia.uni.fmi.mjt.dealership.server.exception;

public class CarInventoryException extends Exception {
    public CarInventoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public CarInventoryException(String message) {
        super(message);
    }
}
