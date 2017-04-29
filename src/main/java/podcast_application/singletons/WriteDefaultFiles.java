package podcast_application.singletons;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriteDefaultFiles {
    private final String BASE_TARGET_PATH,
            CHANNELS_FILE_SOURCE = "/defaultFiles/channels.xml",
            STARTALK_FILE_SOURCE = "/defaultFiles/StarTalk Radio/episodes.xml",
            NERDIST_FILE_SOURCE = "/defaultFiles/The Nerdist/episodes.xml",
            CHANNELS_FILE_TARGET = "./Allo/channels.xml";

    public WriteDefaultFiles(String baseTargetPath) {
        this.BASE_TARGET_PATH = baseTargetPath;

        copyFiles(CHANNELS_FILE_SOURCE,"/channels.xml");

        new File(baseTargetPath + "/StarTalk Radio/").mkdir();
        copyFiles(STARTALK_FILE_SOURCE, "/StarTalk Radio/episodes.xml");

        new File(baseTargetPath + "/The Nerdist/").mkdir();
        copyFiles(NERDIST_FILE_SOURCE, "/The Nerdist/episodes.xml");
    }

    // own implementation of writing UTF-8 encoded files (NOT IN USE)
    private void writeFileUTF(String fileSource, String fileTarget) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream(fileSource), "UTF-8"));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(BASE_TARGET_PATH + fileTarget), "UTF-8"));

            String line = null;
            while((line = reader.readLine()) != null)
                writer.write(line);

            reader.close();
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Using Files.copy, as it preserves encoding
    private void copyFiles(String fileSource, String fileTarget) {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(fileSource);
            Files.copy(is, Paths.get(BASE_TARGET_PATH + fileTarget));

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if(is != null)
                try {
                    is.close();
                    System.out.println("Closing stream");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }
    }
}
