package podcast_application.media;

import podcast_application.media.gui.MediaBar;
import podcast_application.media.gui.PodcastChannel;
import podcast_application.media.gui.PodcastEpisode;
import podcast_application.xml.model.Channel;
import podcast_application.xml.read.ChannelsParser;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MediaControl extends BorderPane {
    private MediaPlayer mediaPlayer;
    private PodcastEpisode currentEpisode;
    private MediaView mediaView;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private PodcastChannel currentChannel;
    private boolean hasStarted = false;

    private void loadChannels() {
        List<PodcastChannel> loadedChannels = new ArrayList<>();
        List<Channel> channels = new ChannelsParser().readChannels();
        for(Channel c : channels)
            loadedChannels.add(new PodcastChannel(c));

        ListView<PodcastChannel> podcastChannelListView = new ListView<>();
        podcastChannelListView.getStyleClass().add("channelList");

        podcastChannelListView.setItems(FXCollections.observableArrayList(loadedChannels));

        podcastChannelListView.setPrefWidth(80);
        podcastChannelListView.setBorder(Border.EMPTY);

        setLeft(podcastChannelListView);

        podcastChannelListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 1) {

                    // Use listview's getSelected item
                    currentChannel = podcastChannelListView.getSelectionModel().getSelectedItem();
//                    System.out.println("Chosen: "+currentChannel);

                    loadEpisodes();

                }
            }
        });

        currentChannel = loadedChannels.get(0);
        podcastChannelListView.getSelectionModel().select(0);
    }

    public MediaControl() {

        loadChannels();

        loadEpisodes();


        currentEpisode = currentChannel.getEpisode(0);
        loadMediaPlayer();


/*
        mediaView = new MediaView(mediaPlayer);
        Pane mvPane = new Pane() {                };
        mvPane.getChildren().add(mediaView);
        mvPane.setStyle("-fx-background-color: black;");
        setCenter(mvPane);
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
                if(event.getClickCount() == 2) {
                    if(hasStarted)
                        currentEpisode.setProgress(mediaPlayer.getCurrentTime());
                    else
                        hasStarted = true;

                    // Use listview's getSelected item
                    currentEpisode = episodeListView.getSelectionModel().getSelectedItem();
                    System.out.println("Chosen: "+currentEpisode);

                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    loadMediaPlayer();

                    mediaPlayer.play();

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

        mediaPlayer.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mediaPlayer.pause();
                    stopRequested = false;
                } else {
                    mediaBar.togglePlay(false);
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            @Override
            public void run() {
                System.out.println("onPaused");
                mediaBar.togglePlay(true);
                currentEpisode.setProgress(mediaPlayer.getCurrentTime());
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

    public void save() {
        currentChannel.saveEpisodes();
        System.out.println("Save done!");

    }

    public void stopMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.dispose();
    }

    public boolean getHasBeenStarted() { return hasStarted; }
}
