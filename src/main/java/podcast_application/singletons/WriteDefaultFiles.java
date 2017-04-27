package podcast_application.singletons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class WriteDefaultFiles {
    private final String BASE_TARGET_PATH,
            CHANNELS_FILE_SOURCE = "/defaultFiles/channels.xml",
            STARTALK_FILE_SOURCE = "/defaultFiles/StarTalk Radio/episodes.xml",
            NERDIST_FILE_SOURCE = "/defaultFiles/The Nerdist/episodes.xml",
            CHANNELS_FILE_TARGET = "./Allo/channels.xml";

    public WriteDefaultFiles(String baseTargetPath) {
        this.BASE_TARGET_PATH = baseTargetPath;

        writeFile(CHANNELS_FILE_SOURCE,"/channels.xml");

        new File(baseTargetPath + "/StarTalk Radio/").mkdir();
        writeFile(STARTALK_FILE_SOURCE, "/StarTalk Radio/episodes.xml");

        new File(baseTargetPath + "/The Nerdist/").mkdir();
        writeFile(NERDIST_FILE_SOURCE, "/The Nerdist/episodes.xml");
    }

    private void writeFile(String fileSource, String fileTarget) {
        try {
            InputStream is = getClass().getResourceAsStream(fileSource);
            OutputStream out = new FileOutputStream(BASE_TARGET_PATH + fileTarget);

            int read = 0;
            byte[] bytes = new byte[512];
            while((read = is.read(bytes)) != -1)
                out.write(bytes, 0, read);

            is.close();
            out.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
