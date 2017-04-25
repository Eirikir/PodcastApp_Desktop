package podcast_application.media.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.util.Duration;
import podcast_application.singletons.DownloadManager;
import podcast_application.singletons.Formatter;
import podcast_application.xml.model.Item;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PodcastEpisode extends VBox {
    private Media episode = null;
    private String fileName;
    private String title, description, pubDate, link;
    private Duration progress, duration;
    private boolean downloading = false;
    private Button fileBtn;
    private File localFile;

    public PodcastEpisode(Item item, String basePath) {
        super();

        title = item.getTitle();
        description = item.getDescription();
        pubDate = item.getDate();
//        episode = new Media(item.getLink());
        link = item.getLink();
        fileName = parseFileName();

        // set local file location
        localFile = new File(basePath+"/"+fileName);

        progress = Formatter.STRING_TO_DURATION(item.getProgress());
        duration = Formatter.STRING_TO_DURATION(item.getDuration());

        // GUI
        HBox headBox = new HBox();
        Label titleLabel = new Label(title);
//        titleLabel.getStyleClass().add("mediaLabel");
        titleLabel.setId("episodeTitleLabel");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        createFileBtn();


        headBox.getChildren().addAll(titleLabel, fileBtn);


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

    // set up file button; download / delete depending upon whether local file exists
    private void createFileBtn() {
        fileBtn = new Button();
        fileBtn.getStyleClass().add("fileBtnClass");

//        File tmp = new File("./Podcasts/StarTalk Radio/"+fileName);

        if(localFile.exists()) {
            episode = new Media(localFile.toURI().toString());
            fileBtn.setId("deleteBtn");
        }
        else {
            fileBtn.setId("downloadBtn");
        }

        fileBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if(localFile.exists()) { // delete file
                    localFile.delete();
                    fileBtn.setId("downloadBtn");

                    // nullify media source
                    episode = null;
                } else {    // download file
                    if(!downloading)
                        downloadFile();
                }

            }
        });
    }

    public void returnValueFromDownload(int value) {
        if(value == 1) {
            fileBtn.setId("deleteBtn");
            episode = episode = new Media(localFile.toURI().toString());
        }
        downloading = false;
    }

    private void downloadFile() {
        downloading = true;
        System.out.println("Downloading file: "+fileName);
        fileBtn.setId("downloadingBtn");
        DownloadManager.getInstance().addTask(link, localFile, this);
    }

    private String parseFileName() {
        int idx = link.lastIndexOf("/");
        return link.substring(idx + 1);
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
