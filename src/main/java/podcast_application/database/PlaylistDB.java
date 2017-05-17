package podcast_application.database;

import java.util.HashMap;
import java.util.Map;

public class PlaylistDB {

    private Map<String, String> playlist = new HashMap<>();

    public Map<String, String> getPlaylist() { return playlist; }
    public void addToPlaylist(String guid, String link) { playlist.put(guid, link); }

    public boolean containsKey(String guid) {
        return playlist.containsKey(guid);
    }
    public void removeFromPlaylist(String guid) { playlist.remove(guid); }
    public int getAmountOfStoredItems() { return playlist.size(); }
}

