package podcast_application.singletons;

import javafx.scene.control.Button;
import javafx.scene.media.Media;
import podcast_application.media.gui.PodcastEpisode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
//        pool.submit(new DownloadTask(sourcePath, targetFile));
        Future<Integer> future = pool.submit(new DownloadFuture(sourcePath, targetFile));
        try {
            int value = future.get();
            episode.returnValueFromDownload(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        pool.submit(new DownloadFuture(sourcePath, targetFile));
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

class DownloadFuture implements Callable {
    private String sourcePath;
    private File targetFile;

    public DownloadFuture(String sourcePath, File targetFile) {
        this.sourcePath = sourcePath;
        this.targetFile = targetFile;
    }

    @Override
    public Integer call() {
        try {
            URL url = new URL(sourcePath);
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream(targetFile);
//            FileOutputStream fos = new FileOutputStream(new File(targetPath));
            byte[] buf = new byte[512];
            while (true) {
                int len = in.read(buf);
                if (len == -1) {
                    break;
                }
                fos.write(buf, 0, len);
            }

            in.close();
            fos.flush();
            fos.close();

            return 1;

//            episode = new Media(localFile.toURI().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}

class DownloadTask implements Runnable {
    private String sourcePath;
    private File targetFile;

    public DownloadTask(String sourcePath, File targetFile) {
        this.sourcePath = sourcePath;
        this.targetFile = targetFile;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(sourcePath);
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream(targetFile);
//            FileOutputStream fos = new FileOutputStream(new File(targetPath));
            byte[] buf = new byte[512];
            while (true) {
                int len = in.read(buf);
                if (len == -1) {
                    break;
                }
                fos.write(buf, 0, len);
            }

            in.close();
            fos.flush();
            fos.close();

//            episode = new Media(localFile.toURI().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
