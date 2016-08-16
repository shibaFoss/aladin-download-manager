package main.download_manager;

import android.app.Activity;
import android.content.Intent;
import main.app.App;
import main.key_database.KeyStore;
import main.screens.BaseScreen;

import java.io.File;

/**
 * The class gives the ability to start, pause, resume, delete like operation on
 * running/complete download task.
 */
public class DownloadTaskStarter {

    public static void addOrResumeTask(Activity activity, DownloadModel downloadModel) {
        Intent intent = new Intent(activity, DownloadService.class);
        intent.putExtra(KeyStore.DOWNLOAD_REQUEST_CODE, DownloadService.ADD_NEW_DOWNLOAD);
        intent.putExtra(KeyStore.DOWNLOAD_MODEL, downloadModel);
        activity.startService(intent);

        //Track download model.
    }

    public static void pauseTask(Activity activity, DownloadModel downloadModel) {
        Intent intent = new Intent(activity, DownloadService.class);
        intent.putExtra(KeyStore.DOWNLOAD_REQUEST_CODE, DownloadService.PAUSE_DOWNLOAD);
        intent.putExtra(KeyStore.DOWNLOAD_MODEL, downloadModel);
        activity.startService(intent);
    }

    public static void deleteTask(Activity activity, DownloadModel downloadModel) {
        Intent intent = new Intent(activity, DownloadService.class);
        intent.putExtra(KeyStore.DOWNLOAD_REQUEST_CODE, DownloadService.DELETE_DOWNLOAD);
        intent.putExtra(KeyStore.DOWNLOAD_MODEL, downloadModel);
        activity.startService(intent);
    }

    public static void removeTask(BaseScreen baseScreen, DownloadModel downloadModel) {
        DownloadSystem downloadSystem = baseScreen.app.getDownloadSystem();
        downloadSystem.removeDownloadTask(downloadModel);
    }

    /**
     * Do not use this method, instead call {@link #deleteTask(Activity, DownloadModel)} &
     * then {@link #addOrResumeTask(Activity, DownloadModel)}
     */
    @Deprecated
    public static void restartTask(Activity activity, DownloadModel downloadModel) {
        Intent intent = new Intent(activity, DownloadService.class);
        intent.putExtra(KeyStore.DOWNLOAD_REQUEST_CODE, DownloadService.RESTART_DOWNLOAD);
        intent.putExtra(KeyStore.DOWNLOAD_MODEL, downloadModel);
        activity.startService(intent);
    }

    public static void deleteCompleteTask(BaseScreen baseScreen, DownloadModel downloadModel) {
        removeCompleteTask(baseScreen, downloadModel);
        File file = new File(downloadModel.filePath, downloadModel.fileName);
        if (file.exists()) file.delete();
    }

    public static void removeCompleteTask(BaseScreen baseScreen, DownloadModel downloadModel) {
        App app = baseScreen.app;
        DownloadSystem downloadSystem = app.getDownloadSystem();
        downloadModel.deleteFromDisk(DownloadModel.COMPLETE_MODEL);
        downloadSystem.getTotalCompleteDownloadModels().remove(downloadModel);
        DownloadUiManager uiManager = downloadSystem.getDownloadUiManager();
        uiManager.updateCompleteDownloadList();
    }


}
