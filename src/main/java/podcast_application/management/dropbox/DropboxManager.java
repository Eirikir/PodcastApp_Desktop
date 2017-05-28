package podcast_application.management.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
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
        try {
            ListFolderResult boxFiles = getFiles();

            String folder = "";
            for (Metadata meta : boxFiles.getEntries()) {

                if(meta instanceof FileMetadata) {
                    FileMetadata tmp = (FileMetadata) meta;
//                    System.out.println(folder + tmp.getPathDisplay());
                    System.out.println(tmp.getPathDisplay());
                }

                // instance of folder; set this as base path
                else if(meta instanceof FolderMetadata) {
                    folder = meta.getName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDroboxFiles(List<File> files) {

    }

    public boolean getFile(File file, String parent) {
        boolean ret = false;
//        String path = "/"+file.getParent().substring(2)+"/"+file.getName();
        String path = parent + file.getName();

        try {
            Metadata meta = client.files().getMetadata(path);
            downloadFile(file, path);
            ret = true;

        } catch (GetMetadataErrorException e) {
            if (e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound()) {

                // file not found
            } else {
                e.printStackTrace();
            }
        } catch (DbxException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public void syncFile(File file, String parent) {
//        String path = "/"+file.getParent().substring(2)+"/"+file.getName();
        String path = parent + file.getName();


//        System.out.println(path);

        try {
            Metadata meta = client.files().getMetadata(path);

            FileMetadata fileMeta = (FileMetadata) meta;

            // compare files
            System.out.println("DropFile: "+ fileMeta.getClientModified().toString());
//            System.out.println("localFile: "+ new Date(file.lastModified()).toString());
//            Files.getAttribute(Paths.get(file.getPath()));

            // local file info
            Path filePath = Paths.get(file.getPath());
            BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
            Date creation = new Date(attr.creationTime().toMillis());
            Date accesss = new Date(attr.lastAccessTime().toMillis());
            Date modified = new Date(attr.lastModifiedTime().toMillis());
//            System.out.println("local - creation: "+ creation);
//            System.out.println("local - access: "+ modified);
//            System.out.println("local - modified: "+ modified);

            // should file in dropbox be updated?
            if(fileMeta.getClientModified().before(new Date(file.lastModified()))) {
                uploadFile(file, path);
//                System.out.println("updated dropbox file");
            }

            // .. or the local one?
            else {
                downloadFile(file, path);
                System.out.println("updated local file");
            }


//                uploadFile(file, path);

        } catch (GetMetadataErrorException e) {
            if (e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound()) {

                // file not found, upload local one
                uploadFile(file, path);
            } else {
                e.printStackTrace();
            }
        } catch (DbxException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private ListFolderResult getFiles() throws DbxException {
        // Get files and folder metadata from Dropbox root directory
        ListFolderResult result = client.files().listFolderBuilder("").withRecursive(true).start();
/*        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }

            if(!result.getHasMore())
                break;
        }
*/
        return result;
    }

    private void downloadFile(File targetFile, String sourcePath) {
        try (OutputStream out = new FileOutputStream(targetFile)) {
            FileMetadata metadata = client.files().downloadBuilder(sourcePath).download(out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void uploadFile(File sourceFile, String targetPath) {
        try (InputStream in = new FileInputStream(sourceFile)) {

//            FileMetadata metadata = client.files().uploadBuilder(targetPath).uploadAndFinish(in);
            FileMetadata metadata = client.files().uploadBuilder(targetPath).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
