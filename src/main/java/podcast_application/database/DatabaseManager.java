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
        loadFiles();
//        loadPlaylist();

        // load subscriptions file
//        loadSubscriptions();
    }
/*
    private PlaylistDB loadPlaylist() {
        File file = new File(PLAYLIST_PATH);
        if(!file.exists())
            return new PlaylistDB();

        PlaylistDB db = new OPMLParser().readPlaylist(file);

        return db;

    }
*/
    private void loadPlaylist(DropboxManager dropboxManager) {
        final String PARENT = "/Podcasts/";
        File playFile = new File(PLAYLIST_PATH);

        if(!playFile.exists()) {  // no local file
            if (!useDropbox)
                playlistDB = new PlaylistDB();
            else if(!dropboxManager.getFile(playFile, PARENT))
                playlistDB = new PlaylistDB();
        }



        if(useDropbox)
            dropboxManager.syncFile(playFile, PARENT);

        playlistDB = new OPMLParser().readPlaylist(playFile);

    }

    private void loadFiles() {
        if(Files.notExists(Paths.get(BASE_PATH))) // create 'Podcasts' dir if not present
            new File(BASE_PATH).mkdir();

        DropboxManager dropboxManager = new DropboxManager();

        // code for playlist file
        loadPlaylist(dropboxManager);

        // load subscriptions file
        loadSubscriptions(dropboxManager);
    }

    private void loadSubscriptions(DropboxManager dropboxManager) {
//        if(Files.notExists(Paths.get(BASE_PATH))) // create 'Podcasts' dir if not present
//            new File(BASE_PATH).mkdir();

        final String PARENT = "/Podcasts/";

        File subFile = new File(SUBSCRIPTIONS_PATH);
        // does the file exist locally?
        if(!subFile.exists()) { // no local file
            if(!useDropbox)
                writeDefaultFiles();

            else if (!dropboxManager.getFile(subFile, PARENT)) // no dropbox file
                writeDefaultFiles();
        }

        subscriptionsDB = new OPMLParser().readSubscriptions(subFile);

        if(useDropbox)
            dropboxManager.syncFile(subFile, PARENT); // sync file based on last modification

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
        if(channelDatabaseList.containsKey(path)) // if the channel is already loaded
            return channelDatabaseList.get(path);


        ChannelDB channelDB = null;
        String tmpPath = "/Podcasts/" + (path.replaceAll(" ", "_")) + "_";
        DropboxManager dropboxManager = new DropboxManager();

        File file = new File(BASE_PATH + path + DB_NAME);
        // does the file exist locally?
        if(!file.exists()) { // no local file
            if (!useDropbox)
                channelDB = new ChannelDB();
            else if(!dropboxManager.getFile(file, tmpPath))
                channelDB = new ChannelDB();
        }

        channelDatabaseList.put(path, channelDB);

        if(!file.exists())
            return channelDB;

        // file exist... sync if neccessary and build database from file contents
        if(useDropbox)
            dropboxManager.syncFile(file, tmpPath);

        // read database file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            channelDB = (ChannelDB) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return channelDB;
        }

        // exists in dropbox?
/*        String tmpPath = "/Podcasts/" + (path.replaceAll(" ", "_")) + "_";
        DropboxManager drop = new DropboxManager();
        if(drop.getFile(new File(BASE_PATH + path + DB_NAME), tmpPath))
            System.out.println("hola: "+tmpPath);

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
*/

//        dropbox.syncFile(new File(dbPath), "/Podcasts/"+channelName);
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

    public void syncFiles() { // sync with dropbox
        // sync database
        if(useDropbox) {
//            new DropboxManager().syncFile(file);
            DropboxManager dropbox = new DropboxManager();
            if(subscriptionsAltered)
                dropbox.syncFile(new File(SUBSCRIPTIONS_PATH), "/Podcasts/");

            dropbox.syncFile(new File(PLAYLIST_PATH), "/Podcasts/");

            // channel databases
            String dbPath, channelName;
            for (Map.Entry<String, ChannelDB> entry : channelDatabaseList.entrySet()) {
                dbPath = BASE_PATH + entry.getKey() + DB_NAME; // local path

                channelName = (entry.getKey().replaceAll(" ", "_")) + "_";

                File tmp = new File(dbPath);
                if(tmp.exists()) {
//                    System.out.println("Parent: "+tmp.getParent());
//                    System.out.println("Name: "+tmp.getName());
                    dropbox.syncFile(new File(dbPath), "/Podcasts/"+channelName);
                }
            }
        }
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
//        if(useDropbox)
//            new DropboxManager().syncFile(file);

    }

    public static DatabaseManager getInstance() {
        if(instance == null)
            instance = new DatabaseManager();
        return instance;
    }
}
