package podcast_application;

import podcast_application.media.MediaControl;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import podcast_application.singletons.DownloadManager;

public class Main extends Application {
    //    private static final String MEDIA_URL =
//            "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";
    MediaControl mediaControl;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Podcast Player");
        primaryStage.getIcons().add(new Image("/images/microphone-1.png"));
        Group root = new Group();
        Scene scene = new Scene(root, 640, 341);
        scene.getStylesheets().add("/css/styles.css");


        mediaControl = new MediaControl();
        scene.setRoot(mediaControl);

        primaryStage.setScene(scene);
        primaryStage.show();

//        new TestRAF();
//        new DropboxSync();
    }

    @Override
    public void stop() {
        //       System.out.println("Exiting");
        if(mediaControl.getHasBeenStarted())
            mediaControl.save();
        mediaControl.stopMediaPlayer();
        DownloadManager.getInstance().shutDownManager(); // stops executorservice
    }


    public static void main(String[] args) {
        launch(args);
    }
}

