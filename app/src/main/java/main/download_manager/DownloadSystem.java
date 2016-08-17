package main.download_manager;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import libs.async_job.AsyncJob;
import main.app.App;
import main.key_database.KeyStore;
import main.settings.UserSettings;

import static main.download_manager.DownloadModel.FILE_FORMAT;

/**
 * DownloadSystem is the main class that run the whole download mechanism. It provide us some basic methods
 * to interact withe the download tasks. The class itself is very simple but the underline concept of the class
 * is not. So changing another class can cause the system go unstable.
 *
 * @author shibaprasad
 * @version 1.2
 */
public final class DownloadSystem implements DownloadTask.DownloadStatusListener, Serializable {

    public boolean isSystemRunning = false;
    private App app;
    private int maxRunningDownloads = 3;
    private DownloadService downloadService;
    private DownloadUiManager downloadUiManager;
    private ArrayList<DownloadModel> totalIncompleteDownloadModels;
    private ArrayList<DownloadModel> totalCompleteDownloadModels;
    private ArrayList<DownloadTask> waitingDownloadList = new ArrayList<>();
    private ArrayList<DownloadTask> runningDownloadList = new ArrayList<>();
    private CountDownTimer downloadRunningTimer;
    private Vibrator vibrator;

    public void prepare(App app) {
        this.app = app;
        this.vibrator = (Vibrator) app.getSystemService(Context.VIBRATOR_SERVICE);

        this.totalIncompleteDownloadModels = DownloadModelParser.getIncompleteDownloadModels();
        this.totalCompleteDownloadModels = DownloadModelParser.getCompleteDownloadModels();
        this.downloadUiManager = new DownloadUiManager(this);
    }

    public void startDownloadSystem(DownloadService downloadService) {
        this.downloadService = downloadService;
        isSystemRunning = true;
        prepareDownloadRunningTimer();
        if (downloadRunningTimer != null) downloadRunningTimer.start();
    }

    private void prepareDownloadRunningTimer() {
        if (downloadRunningTimer == null) {
            downloadRunningTimer = new CountDownTimer(3000, 3000) {
                @Override
                public void onTick(long tick) {
                }

                @Override
                public void onFinish() {
                    if (isSystemRunning) {
                        pullNewDownloadTask();

                        if (downloadRunningTimer != null) downloadRunningTimer.start();
                    }
                }
            };
        }
    }

    private void pullNewDownloadTask() {
        try {
            setMaxRunningDownload(app.getUserSettings());
        } catch (Exception error) {
            error.printStackTrace();
        }

        if (runningDownloadList.size() < maxRunningDownloads) {
            if (waitingDownloadList.size() > 0) {
                final DownloadTask removedWaitingTask = waitingDownloadList.remove(0);
                try {
                    AsyncJob.doInBackground(new AsyncJob.BackgroundJob() {
                        @Override
                        public void doInBackground() {
                            try {
                                removedWaitingTask.startDownload();
                                getDownloadService().makeServiceForeground();
                            } catch (Exception error) {
                                error.printStackTrace();
                                removedWaitingTask.model.extraText = "";
                                removedWaitingTask.updateDownloadUIProgress();

                                AsyncJob.doInMainThread(new AsyncJob.MainThreadJob() {
                                    @Override
                                    public void doInUIThread() {
                                        vibrator.vibrate(30);
                                        Toast.makeText(app, "Failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        }
    }

    public void setMaxRunningDownload(UserSettings userSettings) {
        this.maxRunningDownloads = userSettings.getMaxRunningTask();
    }

    public DownloadService getDownloadService() {
        return this.downloadService;
    }

    public void stopDownloadSystem() {
        isSystemRunning = false;
    }

    public App getApp() {
        return this.app;
    }

    public int getTotalNumberOfRunningTask() {
        return this.runningDownloadList.size();
    }

    public DownloadUiManager getDownloadUiManager() {
        return this.downloadUiManager;
    }

    public ArrayList<DownloadModel> getTotalIncompleteDownloadModels() {
        return this.totalIncompleteDownloadModels;
    }

    public ArrayList<DownloadModel> getTotalCompleteDownloadModels() {
        return this.totalCompleteDownloadModels;
    }

    public boolean matchesRunningTaskWith(DownloadModel downloadModel) {
        for (DownloadTask runningDownloadTask : runningDownloadList) {
            DownloadModel runningModel = runningDownloadTask.model;
            if (runningModel.fileName.equals(downloadModel.fileName)) {
                return true;
            }
        }
        return false;
    }

    public void addOrResumeDownload(Context context, Intent intent) {
        DownloadModel intendedDownloadModel = getDownloadModelFromIntent(intent);
        if (isAlive(intendedDownloadModel)) {
            //the intent download model is alive. so we need to search the matching download model from the list.
            DownloadModel existingDownloadModel = searchMatchingIncompleteDownloadModel(intendedDownloadModel);
            if (isAlive(existingDownloadModel)) {
                //we have found the existing download model in the list.
                resumeDownloadTask(context, existingDownloadModel);
            } else {
                //No download model are founded, so it is a new download task requested.
                addDownloadTask(context, intendedDownloadModel);
            }
        } else {
            vibrator.vibrate(40);
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private DownloadModel getDownloadModelFromIntent(Intent intent) {
        DownloadModel intendedDownloadModel = (DownloadModel) intent.getSerializableExtra(KeyStore.DOWNLOAD_MODEL);
        if (intendedDownloadModel != null) return intendedDownloadModel;
        else return null;
    }

    private boolean isAlive(Object object) {
        return object != null;
    }

    public DownloadModel searchMatchingIncompleteDownloadModel(DownloadModel requestModel) {
        for (DownloadModel downloadModel : totalIncompleteDownloadModels) {
            if (requestModel.fileName.equals(downloadModel.fileName)) {
                return downloadModel;
            }
        }
        return null;
    }

    private void resumeDownloadTask(Context context, DownloadModel existingDownloadModel) {
        //check if the download task with the download model is running or in waiting list.
        DownloadTask existingDownloadTask = searchDownloadTask(existingDownloadModel);

        if (isAlive(existingDownloadTask)) {

            //can not resume because it is already running list.
            vibrator.vibrate(40);
            Toast.makeText(context, "Already running", Toast.LENGTH_SHORT).show();

        } else {

            //no running download mode are found, resume the model.
            DownloadTask downloadTask = generateDownloadTask(context, existingDownloadModel);

            if (isAlive(downloadTask)) {
                //add the download task to waiting list as this is the new download task.
                waitingDownloadList.add(downloadTask);

                Toast.makeText(context, "Resumed", Toast.LENGTH_SHORT).show();

            } else {
                vibrator.vibrate(40);
                Toast.makeText(context, "Resume failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addDownloadTask(Context context, DownloadModel downloadModel) {
        downloadModel.id = generateUniqueId();
        DownloadTask generatedDownloadTask = generateDownloadTask(context, downloadModel);

        if (isAlive(generatedDownloadTask)) {
            this.waitingDownloadList.add(generatedDownloadTask);
            this.totalIncompleteDownloadModels.add(generatedDownloadTask.model);
            this.downloadUiManager.addIncompleteDownloadLayout(generatedDownloadTask.model);

            //make vibrate and make a toast to user.
            vibrator.vibrate(20);
            Toast.makeText(context, "Download added", Toast.LENGTH_LONG).show();
        }
    }

    private DownloadTask searchDownloadTask(DownloadModel downloadModel) {
        for (DownloadTask runningDownloadTask : runningDownloadList) {
            DownloadModel runningModel = runningDownloadTask.model;
            if (runningModel.fileName.equals(downloadModel.fileName)) {
                return runningDownloadTask;
            }
        }
        for (DownloadTask waitingDownloadTask : runningDownloadList) {
            DownloadModel waitingModel = waitingDownloadTask.model;
            if (waitingModel.fileName.equals(downloadModel.fileName)) {
                return waitingDownloadTask;
            }
        }
        return null;
    }

    private DownloadTask generateDownloadTask(Context context, DownloadModel downloadModel) {
        try {
            DownloadTask downloadTask = new DownloadTask(context);
            downloadTask.initialize(downloadModel, downloadUiManager);
            downloadTask.setApp(app);
            downloadTask.setDownloadStatusListener(this);
            return downloadTask;
        } catch (Exception error) {
            error.printStackTrace();
            return null;
        }
    }

    private int generateUniqueId() {
        int id = 0;
        for (DownloadModel model : totalIncompleteDownloadModels) {
            if (model.id == id || model.id > id) {
                id = model.id + 1;
            }
        }
        return id;
    }

    public void pauseDownload(Context context, Intent intent) {
        pauseWithToast(context, intent, true);
    }

    private void pauseWithToast(Context context, Intent intent, boolean showToast) {
        DownloadModel downloadModel = getDownloadModelFromIntent(intent);
        if (isAlive(downloadModel)) {
            DownloadModel matchedDownloadModel = searchMatchingIncompleteDownloadModel(downloadModel);
            if (isAlive(matchedDownloadModel)) {
                if (pause(matchedDownloadModel)) { //pause operation was successful.
                    if (showToast)
                        Toast.makeText(context, "Paused", Toast.LENGTH_SHORT).show();
                } else {
                    if (showToast) {
                        vibrator.vibrate(40);
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private boolean pause(DownloadModel downloadModel) {
        for (DownloadTask runningDownloadTask : runningDownloadList) {
            DownloadModel runningModel = runningDownloadTask.model;
            if (runningModel.fileName.equals(downloadModel.fileName)) {
                runningDownloadTask.cancelDownload();
                return true;
            }
        }

        for (DownloadTask waitingDownloadTask : runningDownloadList) {
            DownloadModel waitingModel = waitingDownloadTask.model;
            if (waitingModel.fileName.equals(downloadModel.fileName)) {
                waitingDownloadTask.cancelDownload();//close this download from safety of accidental download start
                waitingDownloadList.remove(waitingDownloadTask);
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public void restartDownload(Context context, Intent intent) {
        DownloadModel intendedDownloadModel = getDownloadModelFromIntent(intent);
        if (isAlive(intendedDownloadModel)) {
            deleteDownload(context, intent);

            DownloadModel newDownloadModel = new DownloadModel();
            newDownloadModel.id = intendedDownloadModel.id;
            newDownloadModel.fileName = intendedDownloadModel.fileName;
            newDownloadModel.filePath = intendedDownloadModel.filePath;
            newDownloadModel.fileUrl = intendedDownloadModel.fileUrl;
            newDownloadModel.totalFileLength = intendedDownloadModel.totalFileLength;
            deleteDownload(context, intent);
            addDownloadTask(context, newDownloadModel);
        }
    }

    public void deleteDownload(Context context, Intent intent) {
        DownloadModel downloadModel = getDownloadModelFromIntent(intent);
        if (downloadModel != null) {
            DownloadModel matchedDownloadModel = searchMatchingIncompleteDownloadModel(downloadModel);

            if (matchedDownloadModel != null) {
                pause(matchedDownloadModel);
                delete(matchedDownloadModel);

                removeFromTotalIncompleteDownloadModelList(matchedDownloadModel);
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            } else {
                vibrator.vibrate(40);
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void delete(DownloadModel downloadModel) {
        File downloadFile = new File(downloadModel.filePath, downloadModel.fileName);

        DownloadTask downloadTask = searchDownloadTask(downloadModel);
        if (downloadTask != null) {
            if (runningDownloadList.contains(downloadTask)) runningDownloadList.remove(downloadTask);
            if (waitingDownloadList.contains(downloadTask)) waitingDownloadList.remove(downloadTask);
        }

        downloadModel.deleteFromDisk(FILE_FORMAT);

        if (downloadFile.exists()) {
            downloadFile.delete(); //the download file.
        }
    }

    private void removeFromTotalIncompleteDownloadModelList(DownloadModel downloadModel) {
        int index = totalIncompleteDownloadModels.indexOf(downloadModel);
        totalIncompleteDownloadModels.remove(downloadModel);
        downloadUiManager.removeIncompleteDownloadLayout(index);
    }

    public void removeDownloadTask(DownloadModel downloadModel) {
        if (downloadModel != null) {
            DownloadModel matchedDownloadModel = searchMatchingIncompleteDownloadModel(downloadModel);

            if (matchedDownloadModel != null) {
                pause(matchedDownloadModel);
                downloadModel.isOnlyResume = true;

                DownloadTask downloadTask = searchDownloadTask(downloadModel);
                if (downloadTask != null) {
                    if (runningDownloadList.contains(downloadTask)) runningDownloadList.remove(downloadTask);
                    if (waitingDownloadList.contains(downloadTask)) waitingDownloadList.remove(downloadTask);
                }

                downloadModel.deleteFromDisk(FILE_FORMAT);
                removeFromTotalIncompleteDownloadModelList(matchedDownloadModel);
            }
        }
    }

    @Override
    public void onStatusUpdate(DownloadTask downloadTask, int downloadStatus) {
        if (downloadStatus == DownloadStatus.CLOSE) {
            runningDownloadList.remove(downloadTask);

        } else if (downloadStatus == DownloadStatus.DOWNLOADING) {
            runningDownloadList.add(downloadTask);

        } else if (downloadStatus == DownloadStatus.COMPLETE) {
            DownloadModel downloadModel = searchMatchingIncompleteDownloadModel(downloadTask.model);
            if (downloadModel != null) {
                //remove from incomplete/running download task list.
                removeFromTotalIncompleteDownloadModelList(downloadModel);

                //add to complete download task list.
                totalCompleteDownloadModels.add(0, downloadModel);
                downloadUiManager.updateCompleteDownloadList();
            }
        }
    }

}
