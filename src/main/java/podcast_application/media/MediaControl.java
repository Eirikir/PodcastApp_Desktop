package podcast_application.media;

import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import podcast_application.database.DatabaseManager;
import podcast_application.media.gui.*;
import podcast_application.management.helpers.ChannelManager;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.util.*;

public class MediaControl extends BorderPane {
    private MediaPlayer mediaPlayer;
    private PodcastEpisode currentEpisode;
    PodcastEpisode currentlySelected = null; // for toggling details
    private MediaView mediaView;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Map<String, PodcastChannel> channelsMap = new HashMap<>();
    private PodcastChannel currentChannel;
    private boolean hasStarted = false;
    private ChannelInfoPane channelInfo;



    public MediaControl() {
        ChannelManager channelManager = ChannelManager.getInstance();
        ListView<PodcastChannel> podcastChannelListView = channelManager.getChannelListView();
/*        for(PodcastChannel pod : podcastChannelListView.getItems())
            channelsMap.put(pod.getChannelTitle(), pod);
*/
        channelsMap = channelManager.getChannelsMap();
        setLeft(podcastChannelListView);


        podcastChannelListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {

                    // Use listview's getSelected item
                    currentChannel = podcastChannelListView.getSelectionModel().getSelectedItem();

                    loadEpisodes();
                    channelInfo.changeChannel(currentChannel);

                }

    /*            else if(event.getButton() == MouseButton.SECONDARY) {
                    channelManager.removeChannel(podcastChannelListView.getSelectionModel().getSelectedItem());
                } */
            }
        });


        podcastChannelListView.getSelectionModel().select(0);
        currentChannel = podcastChannelListView.getSelectionModel().getSelectedItem();

        loadEpisodes();

        currentEpisode = currentChannel.getEpisode(0);
        currentEpisode.toggleChosen(true);
        loadMediaPlayer();


/*
        mediaView = new MediaView(mediaPlayer);
        Pane mvPane = new Pane() {                };
        mvPane.getChildren().add(mediaView);
        mvPane.setStyle("-fx-background-color: black;");
        setCenter(mvPane);
*/

        // NEW
        channelInfo = new ChannelInfoPane(currentChannel);
        setTop(channelInfo);

/*        Button addBtn = channelInfo.getAddBtn();

        addBtn.setOnAction(e -> {
            new AddChannelDialog();
        });
*/

    }


    private void loadEpisodes() {
        // list of episodes
        ListView<PodcastEpisode> episodeListView = new ListView<PodcastEpisode>();
        ObservableList<PodcastEpisode> items = FXCollections.observableArrayList(currentChannel.getEpisodes());
        episodeListView.setItems(items);

        episodeListView.getStyleClass().add("episodeList");

        episodeListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) { // right click (NOT IN USE)
                    System.out.println("Clicked");
                }

                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.getClickCount() == 1) { // select episode
                        if (currentlySelected != null)
                            currentlySelected.toggleDetails();

                        currentlySelected = episodeListView.getSelectionModel().getSelectedItem();
                        currentlySelected.toggleDetails();
                    }

                    // load selected episode as media
                    else if (event.getClickCount() == 2) {

                        PodcastEpisode tmp = episodeListView.getSelectionModel().getSelectedItem();

                        // determine whether the new media can be streamed
                        if (!tmp.canMediaBeStreamed() && !tmp.isLocalFilePresent()) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText(null);
                            alert.setTitle("Secure connection!");
                            alert.setContentText("The media you have chosen is using a secure protocol and may not be streamed." +
                                    " In order to listen to this episode you have to download it first!");
                            alert.show();
                            return;
                        }

                        storeProgressOfCurrentEpisode();

                        // Use listview's getSelected item
                        currentEpisode.toggleChosen(false); // release lock on media file
                        currentEpisode = tmp;
                        currentEpisode.toggleChosen(true);

                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                        loadMediaPlayer();

                        mediaPlayer.play();

                    }
                }
            }
        });

        setCenter(episodeListView);
    }


    private void loadMediaPlayer() {
        mediaPlayer = new MediaPlayer(currentEpisode.getAsMedia());
        mediaPlayer.seek(Duration.seconds(currentEpisode.getProgress().toSeconds()));

        MediaBar mediaBar = new MediaBar(mediaPlayer);


        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ov) {
                mediaBar.updateValues();
            }
        });

        mediaPlayer.setOnError(()-> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Media Unsupported!");
            String output = "The media you have chosen is currently unsupported, and cannot be played!"
                    +"\nError message: " + mediaPlayer.getError().getType();
            alert.setContentText(output);
            alert.show();
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mediaPlayer.pause();
                    stopRequested = false;
                } else {
                    mediaBar.togglePlay(false);

                    if(!hasStarted)
                        hasStarted = true; // we have now started the player for the first time
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            @Override
            public void run() {
                System.out.println("onPaused");
                mediaBar.togglePlay(true);
                storeProgressOfCurrentEpisode();
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
//                mediaBar.setDuration(mediaPlayer.getMedia().getDuration());
                // continue from saved location
                mediaPlayer.seek(Duration.seconds(currentEpisode.getProgress().toSeconds()));
                mediaBar.setDuration(currentEpisode.getAsMedia().getDuration());
                mediaBar.updateValues();
//                hasStarted = false;
            }
        });

        mediaPlayer.setCycleCount(1);

/*        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                if (!repeat) {
                    playButton.setText(">");
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
        });
*/

        setBottom(mediaBar);
    }

    private void storeProgressOfCurrentEpisode() {
        // store progress of current episode if needed
        if(currentEpisode.updateProgress(mediaPlayer.getCurrentTime()))
            channelsMap.get(currentEpisode.getChannelName()).storeEpisodeProgress(currentEpisode);
    }

    public void save() {
        storeProgressOfCurrentEpisode();

        // save progress of all modified episodes, in each channel
        for (Map.Entry<String, PodcastChannel> entry : channelsMap.entrySet())
            entry.getValue().saveEpisodes();

        DatabaseManager.getInstance().storeSubscriptions();

        System.out.println("Save done!");

    }

    public void stopMediaPlayer() {

        mediaPlayer.stop();
        mediaPlayer.dispose();
    }

    public boolean getHasBeenStarted() { return hasStarted; }
}
