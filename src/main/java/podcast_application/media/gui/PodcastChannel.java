package podcast_application.media.gui;

import podcast_application.xml.model.Channel;
import podcast_application.xml.model.Item;
import podcast_application.xml.write.WriteEpisodes;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PodcastChannel extends ImageView {
    private List<PodcastEpisode> episodes = new ArrayList<>();
    private File directory, episodesList;


    public PodcastChannel(Channel channel, String imagePath) {
//        super(new Image(channel.getImage(), 75, 75, false, true));
        super(new Image("file:" + imagePath, 75, 75, false, true));

        this.directory = new File("./Podcasts/" + channel.getTitle());
        this.episodesList = new File(directory.getPath() + "/episodes.xml");

        populateList(channel.getItems());
    }
    public void saveEpisodes() {
        WriteEpisodes writer = new WriteEpisodes();
        writer.setFile(episodesList.getPath());
        try {
            writer.saveEpisodes(episodes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void populateList(List<Item> list) {
        for (Item m : list)
            episodes.add(new PodcastEpisode(m, directory.getPath()));
    }


    public List<PodcastEpisode> getEpisodes() {
        return episodes;
    }

    public PodcastEpisode getEpisode(int i) {
        return episodes.get(i);
    }

}
