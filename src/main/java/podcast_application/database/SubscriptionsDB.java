package podcast_application.database;

import podcast_application.xml.model.Channel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SubscriptionsDB implements Serializable {
    private static final long serialVersionUID = 0L;
    private Map<String, String> subscriptions = new HashMap<>();

    public Map<String, String> getSubscriptions() { return subscriptions; }
    public void addSubscription(String title, String link) { subscriptions.put(title, link); }
    public void addSubscription(Channel channel) { subscriptions.put(channel.getTitle(), channel.getLink()); }
    public void removeSubscription(String title) { subscriptions.remove(title); }
    public int getAmountOfStoredItems() { return subscriptions.size(); }
    public String getLinkOfID(String title) {
        if(subscriptions.containsKey(title))
            return subscriptions.get(title);
        return null;
    }
}
