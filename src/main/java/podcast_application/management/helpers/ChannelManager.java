package podcast_application.management.helpers;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import podcast_application.database.DatabaseManager;
import podcast_application.media.gui.PodcastChannel;

import java.util.HashMap;
import java.util.Map;

public class ChannelManager {
    private static ChannelManager instance = null;
    private ListView<PodcastChannel> channelListView;
    private Map<String, PodcastChannel> channelsMap = new HashMap<>();

    public ListView<PodcastChannel> getChannelListView() { return channelListView; }

    public void setChannelListView(ListView<PodcastChannel> channelListView) {
        this.channelListView = channelListView;
        for(PodcastChannel pod : channelListView.getItems())
            channelsMap.put(pod.getChannelTitle(), pod);
    }
    public void addChannel(PodcastChannel channel) {
        Platform.runLater(() -> {
            channelListView.getItems().add(channel);
            channelsMap.put(channel.getChannelTitle(), channel);
            DatabaseManager.getInstance().addSubscription(channel.getChannelTitle(), channel.getSourceRSS());
        });
    }

    public void removeChannel(PodcastChannel channel) {
        Platform.runLater(() -> {
            channelListView.getItems().remove(channel);
            channelsMap.remove(channel);
            DatabaseManager.getInstance().removeSubscription(channel.getChannelTitle());
        });
    }

    public PodcastChannel removeAndGetChannel(PodcastChannel channel) {
        removeChannel(channel);
        return getSelected();
    }

    public PodcastChannel getSelected() {
        return channelListView.getSelectionModel().getSelectedItem();
    }

    public Map<String, PodcastChannel> getChannelsMap() { return channelsMap; }

    private ChannelManager() {}

    public static ChannelManager getInstance() {
        if(instance == null)
            instance = new ChannelManager();
        return instance;
    }
}
