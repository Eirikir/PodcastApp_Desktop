package podcast_application.management.data.model;

public class PlaylistObject {
    private String guid, channel, link;
    public PlaylistObject(String channel, String link) {
        this.channel = channel;
        this.link = link;
    }

    public String getChannel() { return channel; }
    public String getLink() { return link; }
}
