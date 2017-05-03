package podcast_application.singletons;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.media.Media;
import podcast_application.media.gui.PodcastEpisode;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.*;

public class DownloadManager {
    private static DownloadManager downloadManager = null;
    private ExecutorService pool;

    private DownloadManager() {
        pool = Executors.newFixedThreadPool(5);
    }


    public void addTask(String sourcePath, String targetPath, PodcastEpisode episode) {
        pool.submit(new DownloadTask(sourcePath, targetPath, episode));
    }

    public void shutDownManager() {
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static DownloadManager getInstance() {
        if(downloadManager == null)
            downloadManager = new DownloadManager();
        return downloadManager;
    }
}


class DownloadTask implements Runnable {
    private String sourcePath;
    private String targetPath;
    private PodcastEpisode episode;

    public DownloadTask(String sourcePath, String targetPath, PodcastEpisode episode) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.episode = episode;
    }


    @Override
    public void run() {
        BufferedInputStream in = null;
        FileOutputStream out = null;

        int returnValue = 0;

        try {
            URL url = new URL(sourcePath);
            URLConnection connection = url.openConnection();

//            System.out.println("Down: "+sourcePath);

            int fileSize = connection.getContentLength();
            if (fileSize < 0) {
                System.out.println("Could not get the file size");
            } else {
                System.out.println("File size: " + fileSize);
            }

            in = new BufferedInputStream(url.openStream());
            out = new FileOutputStream(targetPath);

            byte[] data = new byte[1024];
            int count;
            double sumCount = 0.0;

            while((count = in.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);


                sumCount += count;
                if(fileSize > 0)
                    episode.updateDownloadProgress(sumCount / fileSize);
//                    System.out.println("Percentage: "+(sumCount / fileSize * 100.0) + "%");
            }


//            episode.returnValueFromDownload(1);
            returnValue = 1;

        } catch (Exception ex) {
            ex.printStackTrace();
            returnValue = 0;
        }

        try {
            if(in != null)
                in.close();
            if(out != null) {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        episode.returnValueFromDownload(returnValue);
    }

}
