package podcast_application.media.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.text.*;
import javafx.util.Duration;
import podcast_application.management.helpers.DownloadManager;
import podcast_application.management.helpers.Formatter;
import podcast_application.management.data.model.Episode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PodcastEpisode extends BorderPane {
    private Media episode = null;
    private String fileName;
    private Path filePath;
    private String title, description, pubDate, link, guid, channelName;
    private Duration progress, duration;
    private boolean downloading = false, isInPlaylist = false;
    private Button fileBtn;

    // for details view
    Label descLabel;
    boolean showDetails = false;

    // id's for file button
    private final String ID_DOWNLOAD = "downloadBtn", ID_DELETE = "deleteBtn", ID_PLAYING = "playingBtn";
    private ProgressIndicator progressIndicator;

    public PodcastEpisode(Episode item, String basePath, String channelName) {
        super();

        guid = item.getGuid();
        title = item.getTitle();
        description = item.getDescription();
        pubDate = Formatter.FORMAT_DATE_ROME(item.getDate());
        link = item.getLink();
        fileName = parseFileName();
        filePath = Paths.get(basePath + "/" + fileName);
        this.channelName = channelName;

        this.isInPlaylist = item.getIsInPlaylist();

        progress = Formatter.STRING_TO_DURATION(item.getProgress());
        duration = Formatter.STRING_TO_DURATION(item.getDuration());

        // GUI
//        setUpUI();
        loadGUI();
    }

    public boolean getIsInPlaylist() { return isInPlaylist; }

    public void toggleDetails() {
        showDetails = (showDetails) ? false : true;
        descLabel.setWrapText(showDetails);
    }

    private void loadGUI() {

        Button progressBtn = new Button();
        progressBtn.setPadding(new Insets(0,20,0,0));
        progressBtn.getStyleClass().add("progressBtnClass");
        if(progress == Duration.ZERO)
            progressBtn.setId("progressUnplayed");
        else
            progressBtn.setId("progressNotDone");

        // Title and overview info
        Label titleLabel = new Label(title);
        titleLabel.setId("episodeTitleLabel");
        titleLabel.setMaxWidth(Double.MAX_VALUE);

/*        Button infoBtn = new Button();
        infoBtn.getStyleClass().add("smallBtnClass");
        infoBtn.setId("infoBtnDown");
*/
        HBox headBox = new HBox(10, titleLabel);

        Label dateLabel = new Label(pubDate);
        dateLabel.getStyleClass().add("mediaLabel");
        HBox.setHgrow(dateLabel, Priority.ALWAYS);
        Label durationLabel = new Label(Formatter.FORMAT_TIME(duration));
        durationLabel.getStyleClass().add("mediaLabel");

        dateLabel.setId("episodeDateLabel");
        durationLabel.setId("episodeDurationLabel");

        HBox subBox = new HBox(10, dateLabel, durationLabel);
        VBox centerBox = new VBox(headBox, subBox);

        // download / remove btn & progress indicator
        createFileBtn();

        progressIndicator = new ProgressIndicator();
        progressIndicator.getStyleClass().add("downloadIndicator");
        progressIndicator.setVisible(false);

        StackPane stackBox = new StackPane(fileBtn, progressIndicator);
        stackBox.setPrefSize(30,30);
        stackBox.setAlignment(Pos.CENTER_RIGHT);

        // playlist button
        Button playlistBtn = createPlayListBtn();

        HBox rightBox = new HBox(playlistBtn, stackBox);

        HBox textBox = new HBox();

        descLabel = new Label(description);
        descLabel.getStyleClass().add("mediaLabel");
        descLabel.setId("descLabel");
        descLabel.setWrapText(false);
        HBox.setHgrow(descLabel, Priority.ALWAYS);

        descLabel.setTextAlignment(TextAlignment.JUSTIFY);
        descLabel.setPadding(new Insets(5,0,0,0));

        textBox.getChildren().add(descLabel);
        textBox.setPrefWidth(200);
        textBox.setMaxWidth(Double.MAX_VALUE);


        setLeft(progressBtn);
        setAlignment(progressBtn, Pos.CENTER_LEFT);
        setCenter(centerBox);
        setRight(rightBox);
        setBottom(textBox);

//        setMargin(descLabel, new Insets(5,0,0,0));
        setPadding(new Insets(5,0,0,0));

/*
        infoBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String tmp = infoBtn.getId();
                if(tmp.equals("infoBtnDown")) {
                    infoBtn.setId("infoBtnUp");
                    descLabel.setWrapText(true);
                    return;
                }

                infoBtn.setId("infoBtnDown");
                descLabel.setWrapText(false);
            }
        });
*/
    }


    // set up file button; download / delete depending upon whether local file exists
    private void createFileBtn() {
        fileBtn = new Button();
        fileBtn.getStyleClass().add("fileBtnClass");

        if(Files.exists(filePath)) {
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

            }
        });
    }

    private Button createPlayListBtn() {
        Button playlistBtn = new Button();
        playlistBtn.getStyleClass().add("smallBtnClass");

        if(isInPlaylist) {
            playlistBtn.setId("playListBtnRemove");
            Playlist.getInstance().addEpisode(this);
        }
        else {
            playlistBtn.setId("playListBtnAdd");
        }

        playlistBtn.setOnAction((e) -> {
            Playlist list = Playlist.getInstance();
            isInPlaylist = list.containsEpisode(this);

            if(isInPlaylist) {
                list.removeEpisode(this);
                playlistBtn.setId("playListBtnAdd");

            } else {
                list.addEpisode(this);
                playlistBtn.setId("playListBtnRemove");
            }

        });

        return playlistBtn;
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

//        System.out.println("link: "+link);
//        System.out.println("filepath: "+filePath.toString());

        DownloadManager.getInstance().addTask(link, filePath.toString(), this);
    }

    private String parseFileName() {
        int startIdx = 0, endIdx = 0;
        String tmp = "";
        try {
            startIdx = link.lastIndexOf("/");
            endIdx = link.lastIndexOf(".m");
            tmp = link.substring(startIdx, endIdx + 4);
        } catch (StringIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            System.out.println("Link: "+link);
        }
        return tmp;
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
    public String getFileName() { return fileName; }
    public void setProgress(Duration progress) { this.progress = progress; }
    public boolean updateProgress(Duration progress) {
        if(progress.equals(this.progress))
            return false;
        setProgress(progress);
        return true;
    }
    public Duration getProgress() { return progress; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPubDate() { return pubDate; }
    public String getLink() { return link; }
    public String getGuid() { return guid; }

    public String getChannelName() { return channelName; }
    public boolean canMediaBeStreamed() {
        return (!link.startsWith("https"));
    }
    public boolean isLocalFilePresent() {
        return (Files.exists(filePath));
    }


    @Override
    public String toString() {
        return title;
    }
}
