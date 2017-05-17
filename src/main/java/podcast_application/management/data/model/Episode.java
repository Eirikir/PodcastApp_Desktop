package podcast_application.management.data.model;

import java.util.Date;

public class Episode {
    private String title, description, link, duration, progress, guid;
    private boolean isInPlaylist, isDone;
    private Date date;

    public boolean getIsInPlaylist() { return isInPlaylist; }
    public void setInPlaylist(boolean isInPlaylist) { this.isInPlaylist = isInPlaylist; }

    public boolean getIsDone() { return isDone; }
    public void setIsDone(boolean value) { this.isDone = value; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    @Override
    public String toString() {
/*        return "FeedMessage [title=" + title + ", description=" + description
                + ", link=" + link + ", author=" + author + ", guid=" + guid
                + "]";
                */

        StringBuilder sb = new StringBuilder();

        sb.append("Title: "+title)
                .append("\nDescription: "+description)
                .append("\nPubDate: "+date)
                .append("\nLink: "+link)
                .append("\nDuration: "+ duration)
                .append("\nProgress: "+progress);

        return sb.toString();
    }

}
