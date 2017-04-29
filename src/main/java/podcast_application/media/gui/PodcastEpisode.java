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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PodcastEpisode extends BorderPane {
    private Media episode = null;
    private String fileName;
    private Path filePath;
    private String title, description, pubDate, link;
    private Duration progress, duration;
    private boolean downloading = false;
    private Button fileBtn;
    // id's for file button
    private final String ID_DOWNLOAD = "downloadBtn", ID_DELETE = "deleteBtn", ID_PLAYING = "playingBtn";
//    private File localFile;
    private ProgressIndicator progressIndicator;

    public PodcastEpisode(Item item, String basePath) {
        super();

        title = item.getTitle();
        description = item.getDescription();
        pubDate = item.getDate();
        link = item.getLink();
        fileName = parseFileName();
        filePath = Paths.get(basePath+"/"+fileName);

        // set local file location
//        localFile = new File(filePath.toString());
//        localFile = new File(basePath+"/"+fileName);

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

/*        if(localFile.exists()) {
            episode = new Media(localFile.toURI().toString());
            fileBtn.setId("deleteBtn");
        } */
        if(Files.exists(filePath)) {
//            episode = new Media(filePath.toUri().toString());
            fileBtn.setId(ID_DELETE);
        }
        else {
            fileBtn.setId(ID_DOWNLOAD);
        }

        fileBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(fileBtn.getId().equals(ID_PLAYING)) // if file is in use
                    return;
                try {
                    if (Files.deleteIfExists(filePath)) {
                        System.out.println("File deleted");
                        fileBtn.setId(ID_DOWNLOAD);

                        // nullify media source
                        episode = null;
                    } else {
                        if(!downloading)
                            downloadFile();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*
                if(localFile.exists()) { // delete file
                    if(localFile.delete()) {
                        System.out.println("File deleted");
                        fileBtn.setId("downloadBtn");

                        // nullify media source
                        episode = null;
                    } else {
                        System.out.println("Could not delete");
                    }


                } else {    // download file
                    if(!downloading)
                        downloadFile();
                }
                */

            }
        });
    }


    public void toggleChosen(boolean isChosen) {
        if(isChosen)
            fileBtn.setId(ID_PLAYING);
        else if(Files.notExists(filePath))
            fileBtn.setId(ID_DOWNLOAD);
        else { // release lock on local file, so it may be deleted
            episode = null;
            fileBtn.setId(ID_DELETE);
        }

    }

    public void returnValueFromDownload(int value) {
        if(value == 1) { // succeeded
            fileBtn.setId(ID_DELETE);
//            episode = episode = new Media(localFile.toURI().toString());
//            episode = new Media(filePath.toUri().toString());

        } else { // failed
            fileBtn.setId(ID_DOWNLOAD);
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
//        System.out.println("Target location: "+localFile.getPath());
        DownloadManager.getInstance().addTask(link, filePath.toString(), this);
    }

    private String parseFileName() {
        int idx = link.lastIndexOf("/");
        return link.substring(idx + 1);
    }

    public Media getAsMedia() {
        if(episode == null)
            determineMediaSource();
        return episode;
    }

    private void determineMediaSource() { // set local file, if exists - otherwise use URL
        if (Files.exists(filePath))
            episode = new Media(filePath.toUri().toString());
        else
            episode = new Media(link);
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

    /*
    public String getFormattedPubDate() {
        LocalDateTime tmp = LocalDateTime.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(pubDate));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return tmp.format(formatter);
    }*/

    @Override
    public String toString() {
        return title;
    }
}
