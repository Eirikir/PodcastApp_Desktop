package podcast_application.database;

import podcast_application.management.data.read.SubscriptionsParser;
import podcast_application.management.data.write.SubscriptionsBuilder;
import podcast_application.management.dropbox.DropboxManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DatabaseManager {
    private static DatabaseManager instance = null;
    private final String DB_NAME = "/db.dat";
    private final String BASE_PATH = "./Podcasts/";
    private SubscriptionsDB subscriptionsDB;
    private boolean subscriptionsAltered = false; // to know whether we should save / sync db
    private boolean useDropbox = true; // should we sync through dropbox?
    private final String SUBSCRIPTIONS_PATH = "./Podcasts/subscriptions.opml";

    private DatabaseManager() {
        // load subscriptions file
        loadSubscriptions();
    }

    private void loadSubscriptions() {
        if(Files.notExists(Paths.get(BASE_PATH))) // create 'Podcasts' dir if not present
            new File(BASE_PATH).mkdir();


        DropboxManager dropboxManager = new DropboxManager();

        File subFile = new File(SUBSCRIPTIONS_PATH);
        // does the file exist locally?
        if(!subFile.exists()) { // no local file
            if (!dropboxManager.getFile(subFile) && useDropbox) // no dropbox file
                writeDefaultFiles();

            writeDefaultFiles();
        }

//        subscriptionsDB = new ReadWriteSubscriptions().readSubscriptionsFile(subFile);
        subscriptionsDB = new SubscriptionsParser().readSubscriptions(subFile);



//        readSubscriptionsFile(subFile);

//        if(useDropbox)
//            dropboxManager.syncFile(subFile); // sync file based on last modification

    }



    private void writeDefaultFiles() {
        final String SUB_FILE = "/subscriptions.opml";

        // Now copy default files
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream("/defaultFiles"+SUB_FILE);
            Files.copy(is, Paths.get(BASE_PATH + SUB_FILE),
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if(is != null)
                try {
                    is.close();
                    System.out.println("Closing stream");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }
    }

    public ChannelDB getChannelDatabase(String path) {
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

    public SubscriptionsDB getSubscriptionsDB() { return subscriptionsDB; }

    public void addSubscription(String title, String link) {
        subscriptionsDB.addSubscription(title, link);
        if(!subscriptionsAltered)
            subscriptionsAltered = true;
    }

    public void removeSubscription(String title) {
        subscriptionsDB.removeSubscription(title);
        if(!subscriptionsAltered)
            subscriptionsAltered = true;
    }

    public void storeSubscriptions() {
        if(!subscriptionsAltered)
            return;

        File file = new File(SUBSCRIPTIONS_PATH);

        // save altered database
        new SubscriptionsBuilder().write(file, subscriptionsDB);

        System.out.println("Subscriptions saved");

        // sync database
        if(useDropbox)
            new DropboxManager().syncFile(file);
    }

    public static DatabaseManager getInstance() {
        if(instance == null)
            instance = new DatabaseManager();
        return instance;
    }
}
