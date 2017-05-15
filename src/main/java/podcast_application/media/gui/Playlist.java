package podcast_application.media.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import podcast_application.management.data.model.Episode;

import java.util.ArrayList;
import java.util.List;

public class Playlist extends ImageView implements ChannelInterface {
    private static Playlist instance = null;
    private List<PodcastEpisode> episodes = new ArrayList<>();
    private String title, description;
    private boolean hasBeenAltered = false;

    private Playlist() {
        super(new Image("images/button_200x50.png", 75, 50, true, true));
        title = "Playlist";
        description = "Custom playlist.";
    }

    public String getChannelTitle() { return title; }
    public String getChannelDescription() { return description; }
    public int getAmountOfEpisodes() { return episodes.size(); }
    public List<PodcastEpisode> getEpisodes() { return episodes; }
    public PodcastEpisode getEpisode(int idx) { return episodes.get(idx); }

    public boolean containsEpisode(PodcastEpisode episode) { return episodes.contains(episode); }

    public void addEpisode(PodcastEpisode episode) {
        episodes.add(episode);
        if(!hasBeenAltered) hasBeenAltered = true;
    }

    public void removeEpisode(PodcastEpisode episode) {
        episodes.remove(episode);
        if(!hasBeenAltered) hasBeenAltered = true;
    }

    public void save() {
        if(!hasBeenAltered)
            return;
        System.out.println("Saving playlist!");

    }

    public static Playlist getInstance() {
        if(instance == null)
            instance = new Playlist();
        return instance;
    }
}
