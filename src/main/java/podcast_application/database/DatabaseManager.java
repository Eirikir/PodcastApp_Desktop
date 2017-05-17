package podcast_application.database;

import podcast_application.management.data.model.EpisodeTracking;
import podcast_application.management.data.read.OPMLParser;
import podcast_application.management.data.write.OPMLBuilder;
import podcast_application.management.dropbox.DropboxManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager instance = null;
    private SubscriptionsDB subscriptionsDB;
    private PlaylistDB playlistDB;
    private Map<String, ChannelDB> channelDatabaseList = new HashMap<>();
    private boolean subscriptionsAltered = false; // to know whether we should save / sync db
    private boolean useDropbox = false; // should we sync through dropbox?
    private final String BASE_PATH = "./Podcasts/",
            SUBSCRIPTIONS_PATH = BASE_PATH+"subscriptions.opml", PLAYLIST_PATH = BASE_PATH+"playlist.opml",
            DB_NAME = "/db.dat";

    private DatabaseManager() {
        playlistDB = loadPlaylist();

        // load subscriptions file
        loadSubscriptions();
    }

    private PlaylistDB loadPlaylist() {
        File file = new File(PLAYLIST_PATH);
        if(!file.exists())
            return new PlaylistDB();

        PlaylistDB db = new OPMLParser().readPlaylist(file);

        return db;

    }

    private void loadSubscriptions() {
        if(Files.notExists(Paths.get(BASE_PATH))) // create 'Podcasts' dir if not present
            new File(BASE_PATH).mkdir();


        DropboxManager dropboxManager = new DropboxManager();

        File subFile = new File(SUBSCRIPTIONS_PATH);
        // does the file exist locally?
        if(!subFile.exists()) { // no local file
            if(!useDropbox)
                writeDefaultFiles();

            else if (!dropboxManager.getFile(subFile)) // no dropbox file
                writeDefaultFiles();

//            writeDefaultFiles();
        }

//        subscriptionsDB = new ReadWriteSubscriptions().readSubscriptionsFile(subFile);
        subscriptionsDB = new OPMLParser().readSubscriptions(subFile);


//        readSubscriptionsFile(subFile);

        if(useDropbox)
            dropboxManager.syncFile(subFile); // sync file based on last modification

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
        if(channelDatabaseList.containsKey(path))
            return channelDatabaseList.get(path);

        String dbPath = BASE_PATH + path + DB_NAME;
        if(Files.notExists(Paths.get(dbPath))) {
            ChannelDB db = new ChannelDB();
            channelDatabaseList.put(path, db);
            return db;
        }

        ChannelDB db = null;
        channelDatabaseList.put(path, db);

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
    public PlaylistDB getPlaylistDB() { return playlistDB; }

    public void addSubscription(String title, String link) {
        subscriptionsDB.addSubscription(title, link);
        if(!subscriptionsAltered)
            subscriptionsAltered = true;
    }

    public void removeSubscription(String title) {
        subscriptionsDB.removeSubscription(title);
        if(!subscriptionsAltered)
            subscriptionsAltered = true;

        // remove local files for subscription
        File dir = new File("./Podcasts/"+title+"/");
        if (dir.exists()) {
            for (File f : dir.listFiles())
                f.delete();
            dir.delete();
        }
        else
            System.out.println("not found...");
    }

    public void storeSubscriptions() {
        if(!subscriptionsAltered)
            return;

        File file = new File(SUBSCRIPTIONS_PATH);

        // save altered database
//        new OPMLBuilder().write(file, subscriptionsDB);
        new OPMLBuilder().writeSubscriptions(file, subscriptionsDB);

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
