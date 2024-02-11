package bg.sofia.uni.fmi.mjt.dealership.server.logger;

import java.awt.Label;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String FILE_PATH = "log.csv";
    private static Logger instance;
    private PrintWriter writer;

    private Logger() {
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

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        LocalDateTime now = LocalDateTime.now();
        writer.println(printTime() + " " + message);
        writer.flush();
    }

    public void logError(String message, Exception exception) {
        LocalDateTime now = LocalDateTime.now();
        writer.println("[ERROR] " + printTime() + " " + message);
        exception.printStackTrace(writer);
        writer.flush();
    }

    public void logWarning(String message) {
        LocalDateTime now = LocalDateTime.now();
        writer.println("[WARNING] " + printTime() + " " + message);
        writer.flush();
    }

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
