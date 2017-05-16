package podcast_application.database;

import podcast_application.management.data.model.PlaylistObject;

import java.util.HashMap;
import java.util.Map;

public class PlaylistDB {

    private Map<String, PlaylistObject> playlist = new HashMap<>();

    public Map<String, PlaylistObject> getPlaylist() { return playlist; }
    public void addToPlaylist(String guid, String link, String channel) {
        playlist.put(guid, new PlaylistObject(channel, link));
    }

    public boolean containsKey(String guid) {
        return playlist.containsKey(guid);
    }
    public void removeFromPlaylist(String guid) { playlist.remove(guid); }
    public int getAmountOfStoredItems() { return playlist.size(); }
}

