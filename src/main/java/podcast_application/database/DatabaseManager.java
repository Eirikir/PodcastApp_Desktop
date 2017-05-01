package podcast_application.database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseManager {
    private static DatabaseManager instance = null;
    private final String DB_NAME = "/db.dat";

    public ChannelDB getDatabase(String path) {
        String dbPath = path + DB_NAME;
        if(Files.notExists(Paths.get(dbPath)))
            return new ChannelDB();

        ChannelDB db = null;

        // read database file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dbPath))) {
            db = (ChannelDB) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return db;
        }
    }

    public void storeDatabase(ChannelDB db, String path) {
        String dbPath = path + DB_NAME;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dbPath))) {
            oos.writeObject(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if(instance == null)
            instance = new DatabaseManager();
        return instance;
    }
}
