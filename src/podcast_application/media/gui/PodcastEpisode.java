package podcast_application.media.gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.util.Duration;
import podcast_application.Formatter;
import podcast_application.xml.model.Item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PodcastEpisode extends VBox {
    private Media episode = null;
    private String fileName;
    private String title, description, pubDate, link;
    private Duration progress, duration;

    public PodcastEpisode(Item item) {
        super();

        title = item.getTitle();
        description = item.getDescription();
        pubDate = item.getDate();
//        episode = new Media(item.getLink());
        link = item.getLink();

        progress = Formatter.STRING_TO_DURATION(item.getProgress());
        duration = Formatter.STRING_TO_DURATION(item.getDuration());

        // GUI
        HBox headBox = new HBox();
        Label titleLabel = new Label(title);
//        titleLabel.getStyleClass().add("mediaLabel");
        titleLabel.setId("episodeTitleLabel");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Button downloadBtn = new Button();
        downloadBtn.getStyleClass().add("downloadBtnClass");
        downloadBtn.setId("downloadBtn");
        headBox.getChildren().addAll(titleLabel, downloadBtn);

        HBox subBox = new HBox(10);
        Label dateLabel = new Label(Formatter.FORMAT_DATE(pubDate));
        dateLabel.getStyleClass().add("mediaLabel");
        HBox.setHgrow(dateLabel, Priority.ALWAYS);
        Label durationLabel = new Label(Formatter.FORMAT_TIME(duration));
        durationLabel.getStyleClass().add("mediaLabel");

        dateLabel.setId("episodeDateLabel");
        durationLabel.setId("episodeDurationLabel");


        subBox.getChildren().addAll(dateLabel, durationLabel);

        this.getChildren().addAll(headBox, subBox);
    }

    public Media getAsMedia() {
        if(episode == null)
            episode = new Media(link);
        return episode;
    }
    public Duration getDuration() { return duration; }
    //    public Duration getDuration() { return episode.getDuration(); }
    public String getFileName() { return fileName; }
    public void setProgress(Duration progress) { this.progress = progress; }
    public Duration getProgress() { return progress; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPubDate() { return pubDate; }
    public String getLink() { return link; }

    public String getFormattedPubDate() {
        LocalDateTime tmp = LocalDateTime.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(pubDate));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return tmp.format(formatter);
    }

    /*
        public String getFormattedDuration() {
            long dur = (long) episode.getDuration().toSeconds();
            long hours = dur / 3600;
            long minutes = (dur % 3600) / 60;
            long seconds = dur % 60;

            String tmp = "";
            if(hours > 0)
                tmp = String.format("%01d:%02d:%02d", hours, minutes, seconds);
            else if(minutes > 0)
                tmp = String.format("%02d:%02d", minutes, seconds);
            else if(seconds > 0)
                tmp = String.format("%02d", seconds);

            return tmp;
        }
    */
    @Override
    public String toString() {
        return title;
    }
}
