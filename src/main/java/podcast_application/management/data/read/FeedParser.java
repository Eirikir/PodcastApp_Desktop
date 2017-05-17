package podcast_application.management.data.read;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.itunes.AbstractITunesObject;
import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.FeedInformation;
import com.sun.syndication.feed.module.itunes.types.Duration;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import podcast_application.database.ChannelDB;
import podcast_application.database.DatabaseManager;
import podcast_application.database.PlaylistDB;
import podcast_application.management.data.model.Channel;
import podcast_application.management.data.model.Episode;
import podcast_application.management.data.model.EpisodeTracking;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedParser {
    private static FeedParser instance = null;
    private final String LINE_SEPARATOR = "\n";
    private PlaylistDB playlistDB;

    private FeedParser() {}

    public Channel parseFEED(File channelFile) {
        Channel channel = null;
        try {
            URL feedSource = channelFile.toURI().toURL();

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));
            Module module = feed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
            FeedInformation feedInfo = (FeedInformation) module;

            channel = new Channel();
            channel.setTitle(feed.getTitle());

            // format description (some uses HTML tags)
            String formattedDescription = feed.getDescription()
                    .replaceAll("\\<.*?\\>", "").trim();
            channel.setDescription(formattedDescription);
            channel.setDate(feed.getPublishedDate().toString());
            channel.setImage(feedInfo.getImage().toString());
            channel.setLanguage(feed.getLanguage());
//            channel.setLink(feed.getLink());

            ChannelDB db = DatabaseManager.getInstance().getChannelDatabase(feed.getTitle());

            playlistDB = DatabaseManager.getInstance().getPlaylistDB();
            List<Episode> episodes = new ArrayList<>();
            for (Object entry : feed.getEntries()) {
                Episode tmp = parseItem((SyndEntry) entry);
//                String progress = db.getTrackingOfID(tmp.getGuid());
                EpisodeTracking tracking = db.getTrackingOfID(tmp.getGuid());
                tmp.setProgress(tracking.getProgress());
                tmp.setIsDone(tracking.getIsDone());
                episodes.add(tmp);
            }

            channel.setItems(episodes);
            channel.setDatabase(db);


    } catch (Exception e) {
        e.printStackTrace();
    }
    return channel;
    }

    private Episode parseItem(SyndEntry entry) {
        Episode tmp = new Episode();

        tmp.setGuid(entry.getUri());
        tmp.setTitle(entry.getTitle());

        // is the episode in playlist?
        boolean isInPlayList = playlistDB.containsKey(entry.getUri());
        tmp.setInPlaylist(isInPlayList);

        // format description (some uses HTML tags)
        String formattedDescription = entry.getDescription().getValue()
                .replaceAll("\\<.*?\\>", "").trim()     // remove html tags
                .split(LINE_SEPARATOR)[0]                                 // remove redundant information
                ;
        tmp.setDescription(formattedDescription);

        tmp.setDate(entry.getPublishedDate());
        tmp.setLink(((SyndEnclosure) entry.getEnclosures().get(0)).getUrl());
        Duration duration = ((EntryInformation)entry.getModule(AbstractITunesObject.URI)).getDuration();
        String durValue = (duration == null) ? "00:00:00" : duration.toString();
        tmp.setDuration(durValue);

        return tmp;
    }

    public static FeedParser getInstance() {
        if (instance == null)
            instance = new FeedParser();
        return instance;
    }
}
