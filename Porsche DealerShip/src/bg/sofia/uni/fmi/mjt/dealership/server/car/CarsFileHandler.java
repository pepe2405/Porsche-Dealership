package bg.sofia.uni.fmi.mjt.dealership.server.car;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.CarInventoryFileException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class CarsFileHandler {
    private final String filePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    CarsFileHandler(String filePath) {
        this.filePath = filePath;
    }

    public void saveToFile(Set<Car> cars) throws CarInventoryFileException {
        try (FileWriter writer = new FileWriter(filePath)) {
            String json = gson.toJson(cars);
            writer.write(json);
        } catch (Exception e) {
            throw new CarInventoryFileException("Error while writing to file", e);
        }
    }

    public Set<Car> loadFromFile() throws CarInventoryFileException {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new CarInventoryFileException("Unable to create file");
                }
            } catch (IOException e) {
                throw new CarInventoryFileException("Error while creating file", e);
            }
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<HashSet<Car>>() { }.getType();
            Set<Car> carsCopy = gson.fromJson(reader, type);
            if (carsCopy != null) {
                return carsCopy;
            } else {
                return new HashSet<>();
            }
        } catch (Exception e) {
            throw new CarInventoryFileException("Error while reading from file", e);
        }
    }
}
