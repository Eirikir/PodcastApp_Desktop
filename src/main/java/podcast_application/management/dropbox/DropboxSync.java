package podcast_application.management.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Upon application start; iterate through all available files / folders FROM dropbox
 * and replace local ones if they are older.
 *
 * Upon application end; iterate through all local files / folders and replace those
 * in dropbox which are older.
 */
public class DropboxSync {
    private final String ACCESS_TOKEN = "u--SDACy5YgAAAAAAAAuDJmpl4csWGxxH6OCTAw-zBA7EI2Q4hA9eVVLg0KykZdN";
    private DbxClientV2 client;

    public DropboxSync() {
        // create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        client = new DbxClientV2(config, ACCESS_TOKEN);



        try {
            // get current account info
            FullAccount account = client.users().getCurrentAccount();
            System.out.println(account.getName().getDisplayName());

 /*           List<Metadata> files = retrieveAllFiles(client);
            for(Metadata meta : files) {
                System.out.println("Name: "+ meta.getName());
            } */

            ListFolderResult files = getFiles();
            for (Metadata meta : files.getEntries()) {
                System.out.println("name: "+meta.getName());
                if(meta.getName().equals("subscriptions.data")) {
//                    System.out.println("Exists"+meta.toStringMultiline());
//                    FileMetadata t = (FileMetadata) meta;
//                    t.getClientModified();
                }
                if(meta instanceof FileMetadata) {
                    FileMetadata file = (FileMetadata) meta;
                    System.out.println("File: " + file.getName() + ", date: "+file.getClientModified());
                    System.out.println("File path: "+file.getPathDisplay());

                    // new
//                    File newFile = file;
//                    Files.copy(Paths.get(file));
                    downloadFile(new File("./Podcasts"+file.getPathDisplay()), file.getPathDisplay());
                }
                else if(meta instanceof FolderMetadata)
                    System.out.println("Folder: "+((FolderMetadata)meta).getName());
            }



//            uploadFile(new File("./Podcasts/subscriptions.data"), "/subscriptions.data");
//            uploadFile(new File("./Podcasts/StarTalk Radio/episodes.data"), "/StarTalk Radio/episodes.data");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

/*
    private List<Metadata> retrieveAllFiles(DbxClientV2 client) throws IOException, DbxException
    {
        List<Metadata> result = new ArrayList<Metadata>();
        ListFolderResult resultList = client.files().listFolderBuilder("").withRecursive(true).start();

        while (true) {

            for (Metadata metadata : resultList.getEntries()) {
                result.add(metadata);
            }

            if (!resultList.getHasMore()) {
                break;
            }
            System.out.println("Cycle ");
            resultList = client.files().listFolderContinue(resultList.getCursor());
        }
        return result;
    }
*/
    private ListFolderResult getFiles() throws DbxException {
        // Get files and folder metadata from Dropbox root directory
//        ListFolderResult result = client.files().listFolder("");
        ListFolderResult result = client.files().listFolderBuilder("").withRecursive(true).start();
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }

            if(!result.getHasMore())
                break;

//            result = client.files().listFolderContinue(result.getCursor());
        }

        return result;
//        return client.files().listFolderContinue(result.getCursor());
    }

    private void downloadFile(File targetFile, String sourcePath) {
        try (OutputStream out = new FileOutputStream(targetFile)) {
            FileMetadata metadata = client.files().downloadBuilder(sourcePath).download(out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void uploadFile(File file, String targetPath) {
        try (InputStream in = new FileInputStream(file)) {
            FileMetadata metadata = client.files().uploadBuilder(targetPath).uploadAndFinish(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
/*        try (InputStream in = new FileInputStream("./Podcasts/subscriptions.data")) {
            FileMetadata metadata = client.files().uploadBuilder("/subscriptions.data").uploadAndFinish(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        } */
    }


}
