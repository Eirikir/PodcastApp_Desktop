package podcast_application.synchronization.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
                if(meta.getName().equals("channels.xml")) {
//                    System.out.println("Exists"+meta.toStringMultiline());
//                    FileMetadata t = (FileMetadata) meta;
//                    t.getClientModified();
                }
                if(meta instanceof FileMetadata)
                    System.out.println("File: "+((FileMetadata)meta).getName());
                else if(meta instanceof FolderMetadata)
                    System.out.println("Folder: "+((FolderMetadata)meta).getName());
            }



            uploadFile(new File("./Podcasts/channels.xml"), "/channels.xml");
            uploadFile(new File("./Podcasts/StarTalk Radio/episodes.xml"), "/StarTalk Radio/episodes.xml");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


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

    private ListFolderResult getFiles() throws DbxException {
        // Get files and folder metadata from Dropbox root directory
        ListFolderResult result = client.files().listFolder("");
//        ListFolderResult result = client.files().listFolderBuilder("").withRecursive(true).start();
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

    private void uploadFile(File file, String targetPath) {
        // Upload "test.txt" to dropbox
        try (InputStream in = new FileInputStream(file)) {
            FileMetadata metadata = client.files().uploadBuilder(targetPath).uploadAndFinish(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
/*        try (InputStream in = new FileInputStream("./Podcasts/channels.xml")) {
            FileMetadata metadata = client.files().uploadBuilder("/channels.xml").uploadAndFinish(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        } */
    }


}
