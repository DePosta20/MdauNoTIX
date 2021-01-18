package mdausoft.co.tz.mdaunotix.Utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveUtils {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveUtils(Drive mDriveService) {
        this.mDriveService = mDriveService;
    }

    public Task<String> create_file(String file_path, String mimeType){
        return Tasks.call(mExecutor, () -> {
            File fileMetadata = new File();
            fileMetadata.setName("TEST");
            System.out.println(file_path + " " + mimeType);
            java.io.File file = new java.io.File(file_path);
            FileContent mediaContent = new FileContent(mimeType, file);
            File myFile = null;
            try {
                myFile = mDriveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (myFile == null){
                throw new IOException("Null result when requesting file Creation");
            }
            return myFile.getId();
        });
    }
    public Task<String> createFile() {
        return Tasks.call(mExecutor, () -> {
            File metadata = new File()
                    .setParents(Collections.singletonList("root"))
                    .setMimeType("text/plain")
                    .setName("Untitled file");

            File googleFile = mDriveService.files().create(metadata).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }
            return googleFile.getId();
        });
    }
}
