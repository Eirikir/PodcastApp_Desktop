package podcast_application.media.gui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import podcast_application.management.helpers.ChannelImage;
import podcast_application.management.helpers.ChannelManager;
import podcast_application.management.data.model.Channel;
import podcast_application.management.data.read.FeedParser;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class AddChannelDialog extends TextInputDialog {

    public AddChannelDialog() {
        setTitle("Add New Channel");
        setHeaderText(null);

        // Set the icon (must be included in the project).
        setGraphic(new ImageView(this.getClass().getResource("/images/add_icon-32.png").toString()));


        Label text = new Label("Enter the URL to the RSS feed you wish to add...");

        TextField input = new TextField();
        input.setPromptText("Enter the URL to the RSS feed you wish to add...");

        input.setText("http://billburr.libsyn.com/rss"); // just for development

        VBox content = new VBox(text, input);


        getDialogPane().setContent(content);

        // request focus on input
        Platform.runLater(() -> input.requestFocus());

        // Convert the result to a string value when the 'OK' button is pressed
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return input.getText();
            }
            return null;
        });

        // convert the result to string value
        Optional<String> result = showAndWait();
        if (result.isPresent()){
//            System.out.println("URL: " + result.get());
            Channel channel = processURL(result.get());
            if(channel != null) {
                String imgPath = ChannelImage.getInstance().getChannelImage(channel.getTitle(), channel.getImage());
                PodcastChannel tmp = new PodcastChannel(channel, imgPath);
                ChannelManager.getInstance().addChannel(tmp);
/*                Platform.runLater(() -> {
                    channels.getItems().add(tmp);
                    ChannelManager.getInstance().addChannel(tmp);
                }); */

//                channels.getItems().add(tmp);
//                channels.fireEvent(new ListView.EditEvent<PodcastChannel>(channels, ListView.editCommitEvent(), tmp,channels.getItems().size()));
            }
        }
    }

    private Channel processURL(String url) {
        Channel channel = null;
        try {
            URL fileUrl = new URL(url);

            // create local tempfile from source url
            File tmpFile = new File("./Podcasts/tmp.rss");
            ReadableByteChannel rbc = Channels.newChannel(fileUrl.openStream());
            FileOutputStream fos = new FileOutputStream(tmpFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();


            channel = FeedParser.getInstance().parseFEED(tmpFile);
            channel.setLink(url);

            // create channel directory (if missing) and copy local file into dir
            String channelPath = "./Podcasts/"+channel.getTitle();
            if(Files.notExists(Paths.get(channelPath)))
                Files.createDirectory(Paths.get(channelPath));
            Files.copy(tmpFile.toPath(), Paths.get(channelPath+"/channel.rss"),
                    StandardCopyOption.REPLACE_EXISTING);

            // delete tmp file
            tmpFile.delete();

//            System.out.println("channel name: " + channel.getTitle());
//            System.out.println("episodes size: "+ channel.getItems().size());
        } catch (Exception e) {
            showErrorAlert(e.getMessage());
        }

        return channel;
    }

    private void showErrorAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText(content);
//        alert.setContentText("The url you've provided does not seem to exist!");
        alert.show();
    }

}
