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


    public void addTask(String sourcePath, File targetFile, PodcastEpisode episode) {
        pool.submit(new DownloadTask(sourcePath, targetFile, episode));
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
    private File targetFile;
    private PodcastEpisode episode;

    public DownloadTask(String sourcePath, File targetFile, PodcastEpisode episode) {
        this.sourcePath = sourcePath;
        this.targetFile = targetFile;
        this.episode = episode;
    }

    @Override
    public void run() {
        BufferedInputStream in = null;
        FileOutputStream out = null;

        try {
            URL url = new URL(sourcePath);
            URLConnection connection = url.openConnection();

            int fileSize = connection.getContentLength();
            if (fileSize < 0) {
                System.out.println("Could not get the file size");
            } else {
                System.out.println("File size: " + fileSize);
            }

            in = new BufferedInputStream(url.openStream());
            out = new FileOutputStream(targetFile);

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


            episode.returnValueFromDownload(1);

//            episode = new Media(localFile.toURI().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            episode.returnValueFromDownload(0);
        } finally {
            if(in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(out != null)
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }


    }

}
