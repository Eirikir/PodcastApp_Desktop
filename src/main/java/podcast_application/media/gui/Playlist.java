package podcast_application.media.gui;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import podcast_application.database.DatabaseManager;
import podcast_application.database.PlaylistDB;
import podcast_application.management.data.model.Episode;
import podcast_application.management.data.write.OPMLBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Playlist extends ImageView implements ChannelInterface {
    private static Playlist instance = null;
    private List<PodcastEpisode> episodes = new ArrayList<>();
    private String title, description;
    private boolean hasBeenAltered = false;
    private PlaylistDB playlistDB;

    private Playlist() {
        super(new Image("images/button_200x50.png", 75, 50, true, true));
        title = "Playlist";
        description = "Custom playlist.";
        playlistDB = DatabaseManager.getInstance().getPlaylistDB();
    }

    public String getChannelTitle() { return title; }
    public String getChannelDescription() { return description; }
    public int getAmountOfEpisodes() { return episodes.size(); }
    public List<PodcastEpisode> getEpisodes() { return episodes; }
    public PodcastEpisode getEpisode(int idx) { return episodes.get(idx); }

    public PodcastEpisode getEpisodeById(String guid) {
        return episodes.stream().filter(x -> x.getGuid().equals(guid)).findFirst().get();
    }

    public boolean containsEpisode(PodcastEpisode episode) { return episodes.contains(episode); }

    public void addEpisode(PodcastEpisode episode) {
        episodes.add(episode);
        playlistDB.addToPlaylist(episode.getGuid(), episode.getLink());
        if(!hasBeenAltered) hasBeenAltered = true;
        System.out.println("Adding: "+hasBeenAltered);
    }

    public void removeEpisode(PodcastEpisode episode) {
        episodes.remove(episode);
        playlistDB.removeFromPlaylist(episode.getGuid());
        if(!hasBeenAltered) hasBeenAltered = true;
    }

    public void save() {
        if(!hasBeenAltered)
            return;
        System.out.println("Saving playlist!");

        new OPMLBuilder().writePlaylist(new File("./Podcasts/playlist.opml"), playlistDB);

    }

    public static Playlist getInstance() {
        if(instance == null)
            instance = new Playlist();
        return instance;
    }
}
