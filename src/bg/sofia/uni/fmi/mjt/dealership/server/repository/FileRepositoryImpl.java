package bg.sofia.uni.fmi.mjt.dealership.server.repository;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.FileRepositoryException;
import bg.sofia.uni.fmi.mjt.dealership.server.model.Car;
import bg.sofia.uni.fmi.mjt.dealership.server.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The FileRepositoryImpl class is an implementation of the FileRepository interface.
 * It provides methods for saving and reading data from files.
 * It uses Gson for serialization and deserialization of data.
 *
 * @param <T> the type of the data to be saved or read
 */
public class FileRepositoryImpl<T> implements FileRepository<T> {

    private final String filePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FileRepositoryImpl(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public synchronized void saveAll(Collection<T> records) {
        try (final FileWriter writer = new FileWriter(filePath)) {
            writer.write(gson.toJson(records));
        } catch (Exception e) {
            throw new FileRepositoryException("Error while writing to file", e);
        }
    }

    @Override
    public synchronized Map<String, User> readAllUsers() {
        File file = handleFile();

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<HashSet<User>>() { }.getType();
            Set<User> usersCopy = gson.fromJson(reader, type);
            Map<String, User> users = new HashMap<>();
            if (usersCopy != null) {
                for (var el : usersCopy) {
                    users.put(el.getUsername(), el);
                }
            }
            return users;
        } catch (Exception e) {
            throw new FileRepositoryException("Error while reading from file", e);
        }
    }

    @Override
    public synchronized Set<Car> readAllCars() {
        File file = handleFile();

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<HashSet<Car>>() { }.getType();
            Set<Car> carsCopy = gson.fromJson(reader, type);
            if (carsCopy != null) {
                return carsCopy;
            } else {
                return new HashSet<>();
            }
        }  catch (Exception e) {
            throw new FileRepositoryException("Error while reading from file", e);
        }
    }

    private File handleFile() {
        final File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new FileRepositoryException("Unable to create file");
                }
            } catch (IOException e) {
                throw new FileRepositoryException("Error while creating file", e);
            }
        }

        return file;
    }
}
