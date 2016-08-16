package main.download_manager;

import android.util.Log;
import async_job.AsyncJob;
import main.data_holder.BaseWritableObject;

import java.io.File;
import java.io.Serializable;

import static async_job.AsyncJob.doInBackground;
import static main.settings.UserSettings.downloadCachePath;
import static main.utilities.DeviceTool.mkdirs;

/**
 * DownloadModel : this class is a structural model of a download. It stores all the
 * information of a download during its entire life cycle.
 */
@SuppressWarnings("FieldCanBeLocal")
public class DownloadModel extends BaseWritableObject implements Serializable {

    public static final String FILE_FORMAT = ".model";
    public static final String COMPLETE_MODEL = ".complete_model";
    private static final long serialVersionUID = 296984946043656L;
    private static String TAG = "DownloadModel";

    public int id;
    public String fileUrl;
    public String filePath;
    public String fileName;

    public int downloadStatus = DownloadStatus.CLOSE;

    public String userAgent;
    public String webpage;
    public int bufferSize;
    public int numberOfPart;
    public long chunkSize;
    public int uiProgressInterval;

    public long totalFileLength;
    public long totalByteWroteToDisk;
    public float downloadPercentage;

    public long totalTime;

    public String remainingTime = "";
    public String networkSpeed = "";
    public String extraText = "";

    public int[] partProgressPercentage;
    public long[] downloadPartTotalByteWrite;

    public boolean isRunning;
    public boolean isComplete;

    public boolean isResumeSupport;
    public boolean isCatalogEnable;
    public boolean isAutoResumeEnable;
    public boolean isTtsNotificationEnable;
    public boolean is2GConnection;
    public boolean isSmartDownload;

    public boolean isDeleted;
    public boolean isOnlyResume;
    public boolean isWaitingForNetwork;

    public boolean isLock = false;
    public int lockPassword = -1;

    public void updateDataInDisk() {
        doInBackground(new AsyncJob.BackgroundJob() {
            @Override
            public void doInBackground() {
                //terminate the execution if the model file from the sdcard has deleted.
                if (isDeleted) return;

                remainingTime = "";
                mkdirs(downloadCachePath);
                File downloadModelFile = new File(downloadCachePath, fileName + FILE_FORMAT);
                writeObject(DownloadModel.this, downloadCachePath, downloadModelFile.getName());
            }
        });
    }

    public void changeToCompleteModel() {
        doInBackground(new AsyncJob.BackgroundJob() {
            @Override
            public void doInBackground() {
                mkdirs(downloadCachePath);
                writeObject(DownloadModel.this, downloadCachePath, fileName + COMPLETE_MODEL);
            }
        });
    }

    public void deleteFromDisk(String fileFormat) {
        try {
            // set the value if isDeleted to true... because we can not to
            // write the deleted model by accident in future.
            isDeleted = true;
            File downloadModelFile = new File(downloadCachePath, fileName + fileFormat);
            boolean result = downloadModelFile.delete();

            Log.i(TAG, "deleteFromDisk()... -->> isdDeleted = " + result);

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

}
