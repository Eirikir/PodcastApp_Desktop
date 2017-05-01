package podcast_application.media.gui;

import podcast_application.database.ChannelDB;
import podcast_application.database.DatabaseManager;
import podcast_application.singletons.Formatter;
import podcast_application.xml.model.Channel;
import podcast_application.xml.model.Item;
import podcast_application.xml.write.WriteEpisodes;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PodcastChannel extends ImageView {
    private List<PodcastEpisode> episodes = new ArrayList<>();
    private ChannelDB database;
    private boolean hasDBBeenAltered = false;
    private File directory, episodesList;
    private String channelTitle;


    public PodcastChannel(Channel channel, String imagePath) {
//        super(new Image(channel.getImage(), 75, 75, false, true));
        super(new Image("file:" + imagePath, 75, 75, false, true));

        this.channelTitle = channel.getTitle();

        this.directory = new File("./Podcasts/" + channelTitle);
        this.episodesList = new File(directory.getPath() + "/episodes.xml");

        this.database = channel.getDatabase();

        populateList(channel.getItems());
    }

    public String getChannelTitle() { return channelTitle; }

    // adds episode progress to database
    public void storeEpisodeProgress(PodcastEpisode ep) {
        String dur = Formatter.DURATION_TO_STRING(ep.getProgress());
        System.out.println("storing to "+channelTitle);
        database.addEpisode(ep.getGuid(), Formatter.DURATION_TO_STRING(ep.getProgress()));
        if(!hasDBBeenAltered)
            hasDBBeenAltered = true;
    }

    public void saveEpisodes() {
        if(database.getAmountOfStoredItems() == 0 || !hasDBBeenAltered)
            return;
        System.out.println("Saving episodes of channel '"+channelTitle+"'");
        DatabaseManager.getInstance().storeDatabase(database, directory.getPath());

    }

    private void populateList(List<Item> list) {
        for (Item m : list)
            episodes.add(new PodcastEpisode(m, directory.getPath(), channelTitle));
    }


    public List<PodcastEpisode> getEpisodes() {
        return episodes;
    }

    public PodcastEpisode getEpisode(int i) {
        return episodes.get(i);
    }

}
