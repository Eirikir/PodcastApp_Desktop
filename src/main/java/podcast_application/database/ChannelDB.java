package podcast_application.database;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChannelDB implements Serializable {
    private static final long serialVersionUID = 0L;
    private Map<String, String> episodesProgress = new HashMap<>();

    public Map<String, String> getEpisodesProgress() { return episodesProgress; }
    public void setEpisodesProgress(Map<String, String> map) { this.episodesProgress = map; }
    public void addEpisode(String guid, String progress) { episodesProgress.put(guid, progress); }
    public int getAmountOfStoredItems() { return episodesProgress.size(); }

    public String getProgressOfID(String guid) {
        if(episodesProgress.containsKey(guid))
            return episodesProgress.get(guid);
        return "00:00:00"; // not found
//        return null;
    }

    public void printEpisodes() {
        Iterator it = episodesProgress.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
        }
    }
}
