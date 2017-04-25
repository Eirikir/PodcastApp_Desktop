package podcast_application.xml.model;

public class Item {
    private String title, description, date, link, duration, progress;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
