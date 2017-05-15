package podcast_application.management.data.model;

import podcast_application.database.ChannelDB;

import java.util.List;

public class Channel {
    private String title, link, description, language, date, image;
    private List<Episode> items;
    private ChannelDB database;

    public ChannelDB getDatabase() {
        return database;
    }

    public void setDatabase(ChannelDB database) {
        this.database = database;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setItems(List<Episode> items) { this.items = items; }
    public List<Episode> getItems() { return items; }

}
