package bg.sofia.uni.fmi.mjt.dealership.server.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The LoggerImpl class is an implementation of the Logger interface.
 * It provides methods for logging messages, error messages with exceptions, and warning messages.
 * It also provides a method to clear the log file.
 * It uses a singleton design pattern, that is thread safe,
 * to ensure only one instance of the logger is created.
 * It writes the log messages to a file specified by FILE_PATH.
 */
public class LoggerImpl implements Logger {

    private static volatile LoggerImpl instance;
    private PrintWriter writer;

    private LoggerImpl() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    System.err.println("Unable to create log file");
                }
            } catch (IOException e) {
                System.err.println("Error while creating log file: " + e.getMessage());
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(FILE_PATH, true);
            writer = new PrintWriter(fileWriter);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static LoggerImpl getInstance() {
        if (instance == null) {
            synchronized (LoggerImpl.class) {
                if (instance == null) {
                    instance = new LoggerImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void log(String message) {
        writer.println(printTime() + " " + message);
        writer.flush();
    }

    @Override
    public void logError(String message, Exception exception) {
        writer.println("[ERROR] " + printTime() + " " + message);
        exception.printStackTrace(writer);
        writer.flush();
    }

    @Override
    public void logWarning(String message) {
        writer.println("[WARNING] " + printTime() + " " + message);
        writer.flush();
    }

    @Override
    public void clearLogFile() {
        try {
            FileWriter fileWriter = new FileWriter(FILE_PATH, false);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private String printTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
        return "[ " + LocalDateTime.now().format(dtf) + " ] ";
    }
}
