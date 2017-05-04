package podcast_application.synchronization.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.util.List;

/**
 * Upon application start; iterate through all available files / folders FROM dropbox
 * and replace local ones if they are older.
 *
 * Upon application end; iterate through all local files / folders and replace those
 * in dropbox which are older.
 */
public class DropboxManager {
    private final String ACCESS_TOKEN = "u--SDACy5YgAAAAAAAAuDJmpl4csWGxxH6OCTAw-zBA7EI2Q4hA9eVVLg0KykZdN";
    private DbxClientV2 client;

    public DropboxManager() {
        // create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public void updateLocalFiles(List<File> files) {

    }

    public void updateDroboxFiles(List<File> files) {

    }
}
