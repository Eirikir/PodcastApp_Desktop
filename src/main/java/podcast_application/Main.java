package podcast_application;

import podcast_application.media.MediaControl;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    //    private static final String MEDIA_URL =
//            "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";
    MediaControl mediaControl;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Podcast Player");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/microphone-1.png")));
        Group root = new Group();
        Scene scene = new Scene(root, 640, 341);
        String css = this.getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);


        mediaControl = new MediaControl();
        scene.setRoot(mediaControl);

        primaryStage.setScene(scene);
        primaryStage.show();

//        new TestRAF();
    }

    @Override
    public void stop() {
        //       System.out.println("Exiting");
        mediaControl.save();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

