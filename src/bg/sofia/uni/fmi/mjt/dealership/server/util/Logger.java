package bg.sofia.uni.fmi.mjt.dealership.server.util;

/**
 * The Logger interface provides methods for logging messages.
 * It allows logging of general messages, error messages with exceptions, and warning messages.
 * It also provides a method to clear the log file.
 */
public interface Logger {
    static final String FILE_PATH = "log.csv";

    /**
     * Logs a general message.
     *
     * @param message the message to be logged
     */
    public void log(String message);

    /**
     * Logs an error message along with the exception.
     *
     * @param message the error message to be logged
     * @param exception the exception to be logged
     */
    public void logError(String message, Exception exception);

    /**
     * Logs a warning message.
     *
     * @param message the warning message to be logged
     */
    public void logWarning(String message);

    /**
     * Clears the log file.
     */
    public void clearLogFile();
}