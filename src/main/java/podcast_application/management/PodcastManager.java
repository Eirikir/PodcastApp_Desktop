package podcast_application.management;

import podcast_application.database.DatabaseManager;
import podcast_application.database.SubscriptionsDB;
import podcast_application.management.data.model.Channel;
import podcast_application.management.data.read.FeedParser;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PodcastManager {
    private static PodcastManager instance = null;

    private final String BASE_PATH = "./Podcasts/",
            CHANNEL_FILE = "/channel.rss";
    private final String LINE_SEPARATOR = "\n";
    private List<Channel> channels = new ArrayList<>();

    private PodcastManager() {

        FeedParser feedParser = FeedParser.getInstance();

        // Load base channel info from database
        SubscriptionsDB db = DatabaseManager.getInstance().getSubscriptionsDB();
        for (Map.Entry<String, String> entry : db.getSubscriptions().entrySet()) {
            String title = entry.getKey();
            String sourceRSS = entry.getValue();

            if(Files.notExists(Paths.get(BASE_PATH + title))) // create channel dir if not present
                new File(BASE_PATH + title).mkdir();

            // check whether the channel feed needs to be updated (or created if not present)
            File channelFile = new File(BASE_PATH + title + CHANNEL_FILE);
            updateLocalFile(sourceRSS, channelFile);

            // ... get channel & episode information
            channels.add(feedParser.parseFEED(channelFile));
        }

    }


    private void updateLocalFile(String source, File localFile) {

        try {
            URL url = new URL(source);

            // determine age of files (if source is newer than local; replace it)
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            if(conn.getLastModified() > localFile.lastModified() || !localFile.exists()) {
                ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());

//            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(localFile);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();


//                long tmp = conn.getLastModified();
//                String modified = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(tmp));
//                System.out.println("File modified: " + modified);

                //               fos.close();
                rbc.close();
            }
            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<Channel> getChannels() { return channels; }

    public static PodcastManager getInstance() {
        if(instance == null)
            instance = new PodcastManager();
        return instance;
    }
}
