package podcast_application.management.helpers;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import podcast_application.database.DatabaseManager;
import podcast_application.media.gui.ChannelInterface;
import podcast_application.media.gui.PodcastChannel;

import java.util.HashMap;
import java.util.Map;

public class ChannelManager {
    private static ChannelManager instance = null;
    private ListView<ChannelInterface> channelListView;
    private Map<String, ChannelInterface> channelsMap = new HashMap<>();

    public ListView<ChannelInterface> getChannelListView() { return channelListView; }

    public void setChannelListView(ListView<ChannelInterface> channelListView) {
        this.channelListView = channelListView;
        for(ChannelInterface pod : channelListView.getItems())
            channelsMap.put(pod.getChannelTitle(), pod);
    }
    public void addChannel(PodcastChannel channel) {
        Platform.runLater(() -> {
            channelListView.getItems().add(channel);
            channelsMap.put(channel.getChannelTitle(), channel);
            DatabaseManager.getInstance().addSubscription(channel.getChannelTitle(), channel.getSourceRSS());
            System.out.println("adding: "+channel.getSourceRSS());
        });
    }

    public void removeChannel(PodcastChannel channel) {
        Platform.runLater(() -> {
            channelListView.getItems().remove(channel);
            channelsMap.remove(channel);
            DatabaseManager.getInstance().removeSubscription(channel.getChannelTitle());
        });
    }

    public ChannelInterface removeAndGetChannel(PodcastChannel channel) {
        removeChannel(channel);
        return getSelected();
    }

    public ChannelInterface getSelected() {
        return channelListView.getSelectionModel().getSelectedItem();
    }

    public Map<String, ChannelInterface> getChannelsMap() { return channelsMap; }

    private ChannelManager() {}

    public static ChannelManager getInstance() {
        if(instance == null)
            instance = new ChannelManager();
        return instance;
    }
}
