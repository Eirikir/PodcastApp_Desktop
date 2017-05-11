package podcast_application.media.gui;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import podcast_application.management.helpers.Formatter;

public class MediaBar extends HBox {
    private MediaPlayer mediaPlayer;
    private Button playButton;
    private Slider timeSlider, volumeSlider;
    private Label playTime;
    private Duration duration;

    public MediaBar(MediaPlayer mediaPlayer) {
        super();
        setId("mediaBar");
        this.mediaPlayer = mediaPlayer;
        setAlignment(Pos.CENTER);
        setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(this, Pos.CENTER);

        createPlayButton();
        getChildren().add(playButton);

        // Add spacer
        Label spacer = new Label("   ");
        getChildren().add(spacer);

        // Add time slider
        createTimeSlider();
        getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.getStyleClass().add("mediaLabel");
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        getChildren().add(playTime);

        // Add the volume label
/*        Label volumeLabel = new Label("Vol: ");
        volumeLabel.getStyleClass().add("mediaLabel");
        getChildren().add(volumeLabel);
*/
        Label volumeLabel = new Label();
//        volumeLabel.getStyleClass().add("mediaBtnClass");
        volumeLabel.setId("volumeLabel");
        getChildren().add(volumeLabel);

        // Add Volume slider
        createVolumeSlider();
        getChildren().add(volumeSlider);
    }

    private void createPlayButton() {
//        playButton  = new Button(">");
        playButton = new Button();
        playButton.getStyleClass().add("mediaBtnClass");
        playButton.setId("playBtn");

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                MediaPlayer.Status status = mediaPlayer.getStatus();

                if (status == MediaPlayer.Status.UNKNOWN  || status == MediaPlayer.Status.HALTED)
                {
                    // don't do anything in these states
                    return;
                }

                if ( status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED)
                {
                    /*
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    } */
                    mediaPlayer.play();
                } else {
                    mediaPlayer.pause();
                }
            }
        });
    }

    private void createTimeSlider() {
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);

        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });
    }

    private void createVolumeSlider() {
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);

        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });
    }

    public void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    String timeValues = Formatter.FORMAT_TIME(currentTime) + "/" + Formatter.FORMAT_TIME(duration);
                    playTime.setText(timeValues);
//                    playTime.setText(Formatter.FORMAT_TIME_VALUES(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int)Math.round(mediaPlayer.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    public Slider getTimeSlider() { return timeSlider; }
    public Slider getVolumeSlider() { return volumeSlider; }
    public Label getPlayTime() { return playTime; }
    public Button getPlayButton() { return playButton; }

    public void setDuration(Duration duration) { this.duration = duration; }
    public void togglePlay(boolean pause) {
        String tmp = (pause) ? "playBtn" : "pauseBtn";
        playButton.setId(tmp);
//        playButton.setText(tmp);
    }
}
