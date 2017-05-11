package podcast_application.management.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class ChannelImage {
    private static ChannelImage channelImage = null;
    private final String IMAGE_FILE = "/image.jpg";

    private ChannelImage() {}

    public String getChannelImage(String channelName, String imageURL) {
//        String channelPath = "./Podcasts/"+channelName;
        String imagePath = "./Podcasts/"+channelName+IMAGE_FILE;
        File imgFile = new File(imagePath);

        if(!imgFile.exists()) {
            System.out.println("Needs to download image!");
            try {
                // using Java NIO to transfer file
                URL url = new URL(imageURL);
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(imgFile);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return imagePath;
    }

    public static ChannelImage getInstance() {
        if(channelImage == null)
            channelImage = new ChannelImage();
        return channelImage;
    }
}
