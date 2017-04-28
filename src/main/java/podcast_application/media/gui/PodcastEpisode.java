package podcast_application.media.gui;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.text.Text;
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

public class PodcastEpisode extends BorderPane {
    private Media episode = null;
    private String basePath, fileName;
    private String title, description, pubDate, link;
    private Duration progress, duration;
    private boolean downloading = false;
    private Button fileBtn;
    private File localFile;
    private ProgressIndicator progressIndicator;

    public PodcastEpisode(Item item, String basePath) {
        super();

        title = item.getTitle();
        description = item.getDescription();
        pubDate = item.getDate();
        link = item.getLink();
        fileName = parseFileName();

        // set local file location
        localFile = new File(basePath+"/"+fileName);
        basePath = basePath;

        progress = Formatter.STRING_TO_DURATION(item.getProgress());
        duration = Formatter.STRING_TO_DURATION(item.getDuration());

        // GUI
        setUpUI();
    }

    private void setUpUI() {
        VBox leftBox = new VBox();
        Label titleLabel = new Label(title);
        titleLabel.setId("episodeTitleLabel");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        HBox subBox = new HBox(10);
        Label dateLabel = new Label(Formatter.FORMAT_DATE(pubDate));
        dateLabel.getStyleClass().add("mediaLabel");
        HBox.setHgrow(dateLabel, Priority.ALWAYS);
        Label durationLabel = new Label(Formatter.FORMAT_TIME(duration));
        durationLabel.getStyleClass().add("mediaLabel");

        dateLabel.setId("episodeDateLabel");
        durationLabel.setId("episodeDurationLabel");

        subBox.getChildren().addAll(dateLabel, durationLabel);


        leftBox.getChildren().addAll(titleLabel, subBox);

        StackPane rightBox = new StackPane();
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        createFileBtn();

        progressIndicator = new ProgressIndicator();
        progressIndicator.getStyleClass().add("downloadIndicator");
        progressIndicator.setVisible(false);


        rightBox.getChildren().addAll(fileBtn, progressIndicator);

        setLeft(leftBox);
        setRight(rightBox);
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

//                if(localFile == null)
//                    localFile = new File(basePath+"/"+fileName);

//                System.out.println("File exists: "+localFile.exists());
                if(localFile.exists()) { // delete file
                    if(localFile.delete()) {
                        System.out.println("File deleted");
                        fileBtn.setId("downloadBtn");

                        // nullify media source
                        episode = null;
                    } else {
                        System.out.println("Could not delete");
                    }


//                    localFile = null;

                } else {    // download file
                    if(!downloading)
                        downloadFile();
                }

            }
        });
    }

    public void returnValueFromDownload(int value) {
        if(value == 1) { // succeeded
            fileBtn.setId("deleteBtn");
            episode = episode = new Media(localFile.toURI().toString());

        } else { // failed
            fileBtn.setId("downloadBtn");
        }

        progressIndicator.setVisible(false);
        fileBtn.setVisible(true);

        downloading = false;
    }

    public void updateDownloadProgress(double value) {
        progressIndicator.setProgress(value);
    }

    private void downloadFile() {
        downloading = true;
        fileBtn.setVisible(false);
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(0);

        System.out.println("Downloading file: "+fileName);
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
