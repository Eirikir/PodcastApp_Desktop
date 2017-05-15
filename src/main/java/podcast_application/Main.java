package podcast_application;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import podcast_application.management.PodcastManager;
import podcast_application.management.dropbox.DropboxManager;
import podcast_application.media.MediaControl;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import podcast_application.media.gui.ChannelInterface;
import podcast_application.media.gui.PodcastChannel;
import podcast_application.management.helpers.ChannelImage;
import podcast_application.management.helpers.ChannelManager;
import podcast_application.management.helpers.DownloadManager;
import podcast_application.management.data.model.Channel;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
/*    public static final String SPLASH_IMAGE =
            "http://fxexperience.com/wp-content/uploads/2010/06/logo.png"; */
    public static final String SPLASH_IMAGE = "/images/preloader_logo.png";
    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private Stage mainStage;
    private static final int SPLASH_WIDTH = 676;
    private static final int SPLASH_HEIGHT = 227;

    // old
    MediaControl mediaControl;

    @Override
    public void init() {
        ImageView splash = new ImageView(new Image(
                SPLASH_IMAGE
        ));
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
        progressText = new Label("Will find friends for peanuts . . .");
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setStyle(
                "-fx-padding: 5; " +
                        "-fx-background-color: cornsilk; " +
                        "-fx-border-width:5; " +
                        "-fx-border-color: " +
                        "linear-gradient(" +
                        "to bottom, " +
                        "chocolate, " +
                        "derive(chocolate, 50%)" +
                        ");"
        );
        splashLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(final Stage initStage) throws Exception {

        final Task<ListView<ChannelInterface>> loadChannels = new Task<ListView<ChannelInterface>>() {
            @Override
            protected ListView<ChannelInterface> call() throws InterruptedException {
                List<ChannelInterface> loadedChannels = new ArrayList<>();

                updateMessage("Loading Podcast channels...");
//                RSSParser parser = new RSSParser(); // new
                PodcastManager parser = PodcastManager.getInstance();

                List<Channel> channels = parser.getChannels();
                for(Channel c : channels) {
                    String imgPath = ChannelImage.getInstance().getChannelImage(c.getTitle(), c.getImage());
                    loadedChannels.add(new PodcastChannel(c, imgPath));
                }


                ListView<ChannelInterface> podcastChannelListView = new ListView<>();
                podcastChannelListView.getStyleClass().add("channelList");

                podcastChannelListView.setItems(FXCollections.observableArrayList(loadedChannels));

                podcastChannelListView.setPrefWidth(80);
                podcastChannelListView.setBorder(Border.EMPTY);

                return podcastChannelListView;
            }
        };

        showSplash(
                initStage,
                loadChannels,
                () -> showMainStage(loadChannels.getValue())
        );

        new Thread(loadChannels).start();
    }

    private void showMainStage(ListView<ChannelInterface> podcastChannelListView) {
        mainStage = new Stage(StageStyle.DECORATED);
        mainStage.setTitle("PodRunner");
        mainStage.getIcons().add(new Image("/images/microphone-1.png"));
        Group root = new Group();
//        Scene scene = new Scene(root, 640, 341);
        Scene scene = new Scene(root, 680, 441);
        scene.getStylesheets().add("/css/styles.css");


        ChannelManager.getInstance().setChannelListView(podcastChannelListView);
        mediaControl = new MediaControl();
//        mediaControl = new MediaControl(podcastChannelListView);
        scene.setRoot(mediaControl);

        mainStage.setScene(scene);
        mainStage.show();

        mainStage.setOnCloseRequest(e -> {
//            if(mediaControl.getHasBeenStarted())
                mediaControl.save();
        });

        // Test
//        new RandomTest();
//        new DropboxSync();
//        new DropboxManager().updateLocalFiles(null);

    }

    private void showSplash(
            final Stage initStage,
            Task<?> task,
            InitCompletionHandler initCompletionHandler
    ) {
        progressText.textProperty().bind(task.messageProperty());
        loadProgress.progressProperty().bind(task.progressProperty());
        task.stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loadProgress.progressProperty().unbind();
                loadProgress.setProgress(1);
                initStage.toFront();
                FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
                fadeSplash.setFromValue(1.0);
                fadeSplash.setToValue(0.0);
                fadeSplash.setOnFinished(actionEvent -> initStage.hide());
                fadeSplash.play();

                initCompletionHandler.complete();
            } // todo add code to gracefully handle other task states.
        });

        Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setAlwaysOnTop(true);
        initStage.show();
    }

    public interface InitCompletionHandler {
        void complete();
    }

    @Override
    public void stop() {
        mediaControl.stopMediaPlayer();
        DownloadManager.getInstance().shutDownManager(); // stops executorservice
    }


    public static void main(String[] args) {
//        LauncherImpl.launchApplication(Main.class, System.property.javafx.preloder, args);
        launch(args);
    }
}

