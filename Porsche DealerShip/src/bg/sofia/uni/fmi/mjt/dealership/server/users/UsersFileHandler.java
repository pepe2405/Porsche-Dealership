package bg.sofia.uni.fmi.mjt.dealership.server.users;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.UserManagerFileException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class UsersFileHandler {
    private String filePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public UsersFileHandler(String filePath) {
        this.filePath = filePath;
    }

    public synchronized void saveToFile(Map<String, User> users) throws UserManagerFileException {
        try (FileWriter writer = new FileWriter(filePath)) {
            Set<User> usersCopy;
            synchronized (users) {
                usersCopy = new HashSet<>(users.values());
            }
            String json = gson.toJson(usersCopy);
            writer.write(json);
        } catch (Exception e) {
            throw new UserManagerFileException("Error while writing to file", e);
        }
    }

    public synchronized Map<String, User> loadFromFile() throws UserManagerFileException {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new UserManagerFileException("Unable to create file");
                }
            } catch (IOException e) {
                throw new UserManagerFileException("Error while creating file", e);
            }
        }

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
            throw new UserManagerFileException("Error while reading from file", e);
        }
    }
}
