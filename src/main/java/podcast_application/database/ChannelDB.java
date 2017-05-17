package podcast_application.database;

import podcast_application.management.data.model.EpisodeTracking;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChannelDB implements Serializable {
    private static final long serialVersionUID = 0L;
    private Map<String, EpisodeTracking> episodesTracking = new HashMap<>();

    public Map<String, EpisodeTracking> getEpisodesTracking() { return episodesTracking; }
    public void setEpisodesTracking(Map<String, EpisodeTracking> map) { this.episodesTracking = map; }
    public void addEpisode(String guid, EpisodeTracking progress) {
        if(!episodesTracking.containsKey(guid))
                episodesTracking.put(guid, progress);
        else
            episodesTracking.replace(guid, progress);
    }
    public int getAmountOfStoredItems() { return episodesTracking.size(); }

    public EpisodeTracking getTrackingOfID(String guid) {
        if(episodesTracking.containsKey(guid))
            return episodesTracking.get(guid);
        return new EpisodeTracking("00:00:00"); // not found
//        return null;
    }

    public void printEpisodes() {
        Iterator it = episodesTracking.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
        }
    }
}
