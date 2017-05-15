package podcast_application.media.gui;

import java.util.List;

public interface ChannelInterface {

    public String getChannelTitle();
    public int getAmountOfEpisodes();
    public List<PodcastEpisode> getEpisodes();
    public PodcastEpisode getEpisode(int idx);
    public String getChannelDescription();
    public void save();
}
