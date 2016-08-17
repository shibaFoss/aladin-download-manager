package main.download_manager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import net.fdm.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import libs.async_job.AsyncJob.BackgroundJob;
import libs.async_job.AsyncJob.MainThreadJob;
import main.app.App;
import main.download_manager.DownloadPart.DownloadStatusListener;
import main.key_database.KeyStore;
import main.screens.main_screen.MainScreen;

import static libs.async_job.AsyncJob.doInBackground;
import static libs.async_job.AsyncJob.doInMainThread;
import static main.download_manager.DownloadStatus.CLOSE;
import static main.download_manager.DownloadStatus.COMPLETE;
import static main.download_manager.DownloadStatus.DOWNLOADING;
import static main.download_manager.DownloadTaskEditor.SIZE_NOT_MEASURED;
import static main.download_manager.DownloadTools.getChunkSize;
import static main.download_manager.DownloadTools.getFileSize;
import static main.download_manager.DownloadTools.getFormatted;
import static main.download_manager.DownloadTools.getFormattedPercentage;
import static main.download_manager.DownloadTools.getSmartNumberOfDownloadParts;
import static main.parse.ParseServer.trackDownloadInfo;
import static main.utilities.NetworkUtils.isNetworkAvailable;

/**
 * DownloadTask: The class is responsible for handling a single download task.
 * It simply has some public methods to control the lifecycle of the download task.
 */
public final class DownloadTask implements DownloadStatusListener {

    /**
     * The download model that is the main model of the entire download task.
     */
    public DownloadModel model;

    /**
     * The download status indicator. it is used for identifying
     * the current download status of the download task.
     */
    private int DOWNLOAD_STATUS = CLOSE;

    /**
     * It is interface that gives update to the implementor classes about the
     * current download status.
     */
    private DownloadStatusListener downloadStatusListener;

    private App app;
    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    //The original file destination of the downlaod task.
    private File destinationFile;

    //Number of download parts are used in this download task.
    private DownloadPart[] totalDownloadParts;

    private DownloadUiManager uiManager;
    private CountDownTimer progressUpdater;

    //Total number of downlaod errors that has been counted during this session.
    private int totalErrorCount = 0;

    //A indicator that is used to identify if any download error related advise
    //has been shown to user or not.
    private boolean isDownloadErrorAdviseShown = false;


    /**
     * Public constructor
     *
     * @param context the context that created this downlaod task.
     */
    public DownloadTask(Context context) {
        this.context = context;
    }


    public void initialize(DownloadModel downloadModel, DownloadUiManager uiManager) {
        if (downloadModel == null)
            throw new NullPointerException("DownloadModel could not be null.");

        this.uiManager = uiManager;
        this.model = downloadModel;
        this.destinationFile = new File(model.filePath, model.fileName);

        this.initNotification();
        this.initProgressTimer();
        this.model.updateDataInDisk();

        this.model.extraText = "Waiting...To Join";
        this.updateDownloadUIProgress();
    }

    private void initNotification() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.ic_download_notification);
        notificationBuilder.setContentTitle(this.model.fileName);

        Intent intent = new Intent(context, MainScreen.class);
        intent.putExtra(KeyStore.OPEN_DOWNLOAD_FRAGMENT, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);
    }

    private void initProgressTimer() {
        int intervalTime = model.uiProgressInterval;
        progressUpdater = new CountDownTimer((1000 * 60), intervalTime) {
            @Override
            public void onTick(long tickTime) {
                updateDownloadProgress();
            }


            @Override
            public void onFinish() {
                if (DOWNLOAD_STATUS == DOWNLOADING) this.start();
            }
        };
    }

    public void updateDownloadUIProgress() {
        doInMainThread(new MainThreadJob() {
            @Override
            public void doInUIThread() {
                if (uiManager != null) {
                    uiManager.updateDownloadProgressWith(model);
                }
            }
        });
    }

    private void updateDownloadProgress() {
        doInBackground(new BackgroundJob() {
            @Override
            public void doInBackground() {
                if (DOWNLOAD_STATUS == DOWNLOADING) {
                    model.isRunning = true;
                    increaseTotalTime();
                    showSystemNotification(false);
                    updateDownloadUIProgress();
                    watchNetworkConnection();
                } else {
                    progressUpdater.cancel();
                    if (DOWNLOAD_STATUS == CLOSE) {
                        model.isRunning = false;
                        notificationManager.cancel(model.id);
                    }
                }
            }
        });
    }

    private void increaseTotalTime() {
        if (!model.isWaitingForNetwork)
            model.totalTime += model.uiProgressInterval;
    }

    //Show the download progress in a system notification.
    private void showSystemNotification(boolean isCompleteDownload) {
        calculateDownloadProgress();
        configureNotification(isCompleteDownload);
        notificationManager.notify(model.id, notificationBuilder.build());
    }

    private void watchNetworkConnection() {
        for (DownloadPart downloadPart : totalDownloadParts)
            downloadPart.validateDownloadSettings();

        if (model.isWaitingForNetwork) {
            if (isNetworkAvailable(context)) {
                model.isWaitingForNetwork = false;
                model.extraText = "";
                startAllDownloadThreads();
            }
        }
    }

    private void calculateDownloadProgress() {
        model.totalByteWroteToDisk = 0;

        for (DownloadPart part : totalDownloadParts) {
            model.partProgressPercentage[part.partNumber] = part.downloadPercentage;
            model.downloadPartTotalByteWrite[part.partNumber] = part.downloadedByte;
            model.totalByteWroteToDisk += part.downloadedByte;
        }

        model.downloadPercentage = (model.totalByteWroteToDisk * 100.0f) / model.totalFileLength;

        calculateRemainingTime();
        calculateNetworkSpeed();

        if (model.downloadPercentage > 100.0f)
            model.downloadPercentage = 100.0f;
    }

    private void configureNotification(boolean isCompleteDownload) {
        if (isCompleteDownload) {
            model.isComplete = true;
            model.downloadPercentage = 100.0f;
            notificationBuilder.setSmallIcon(R.drawable.ic_completed);
            notificationBuilder.setContentText("Download Complete : 100%");
        } else {
            if (model.isWaitingForNetwork) {
                notificationBuilder.setContentText("Downloading : " + "Waiting for network...");
                notificationBuilder.setProgress(100, (int) model.downloadPercentage, true);
            } else {
                notificationBuilder.setContentText("Downloading : " +
                        getFormattedPercentage(model) + "% " + model.remainingTime);
                notificationBuilder.setProgress(100, (int) model.downloadPercentage, false);
            }
        }
    }

    private void startAllDownloadThreads() {
        for (DownloadPart downloadPart : totalDownloadParts) {
            if (downloadPart.downloadStatus != DownloadStatus.DOWNLOADING) {
                downloadPart.startDownload();
            }
        }
    }

    private void calculateRemainingTime() {
        double totalTime = (double) this.model.totalTime;
        double totalDownloadedByte = (double) this.model.totalByteWroteToDisk;
        double totalFileLength = (double) this.model.totalFileLength;
        double remainingTime = ((totalTime / totalDownloadedByte) * (totalFileLength - totalDownloadedByte));
        this.model.remainingTime = DownloadTools.calculateTime((long) totalTime) + "/" +
                DownloadTools.calculateTime((long) remainingTime);
    }

    private void calculateNetworkSpeed() {
        double byteReadPerSec = 1.0;

        try {
            byteReadPerSec = (model.totalByteWroteToDisk * 1000) / model.totalTime;
        } catch (Exception error) {
            error.printStackTrace();
        }

        byteReadPerSec *= 1.6;

        if (byteReadPerSec < 1024)
            model.networkSpeed = getFormatted(byteReadPerSec) + "B/s";
        else if (byteReadPerSec > 1024)
            model.networkSpeed = getFormatted((byteReadPerSec / 1024) + 2) + "Kb/s";
        else if (byteReadPerSec > 1024 * 1024)
            model.networkSpeed = getFormatted(byteReadPerSec / (1024 * 1024)) + "Mb/s";
    }

    public void startDownload() throws Exception {
        configure();
        createEmptyDestinationFile();
        startAllDownloadThreads();

        model.extraText = "";
        model.isRunning = true;

        model.updateDataInDisk();

        updateDownloadStatus(DOWNLOADING);
        updateDownloadUIProgress();
        progressUpdater.start();
    }

    private void configure() throws Exception {
        //skip configuring model if it is a old download task.
        if (model.totalByteWroteToDisk < 1) configuredDownloadModel(new URL(model.fileUrl));
        this.totalDownloadParts = generateDownloadParts(model.chunkSize);
    }

    private void createEmptyDestinationFile() throws IOException {
        doInBackground(new BackgroundJob() {
            @Override
            public void doInBackground() {
                if (model.isDeleted) return; //terminate the function if the model has deleted from the app.

                try {
                    if (!destinationFile.exists())
                        new RandomAccessFile(destinationFile, "rw").setLength(model.totalFileLength);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Update the current download status with the new download status code.
    private void updateDownloadStatus(int downloadStatus) {
        DOWNLOAD_STATUS = downloadStatus;
        model.downloadStatus = downloadStatus;

        if (downloadStatusListener != null)
            downloadStatusListener.onStatusUpdate(this, DOWNLOAD_STATUS);
    }

    private void configuredDownloadModel(URL url) throws MalformedURLException {
        if (model.totalFileLength == SIZE_NOT_MEASURED)
            model.totalFileLength = getFileSize(url);

        model.isResumeSupport = true;

        if (model.is2GConnection) {
            model.numberOfPart = 1;
            model.bufferSize = 1024;
        } else {
            if (model.isSmartDownload)
                model.numberOfPart = getSmartNumberOfDownloadParts(model.totalFileLength);
        }

        model.chunkSize = getChunkSize(true, model.totalFileLength, model.numberOfPart);
        model.partProgressPercentage = new int[model.numberOfPart];
        model.downloadPartTotalByteWrite = new long[model.numberOfPart];
    }

    private DownloadPart[] generateDownloadParts(long chunkSize) throws Exception {
        if (chunkSize < 1) throw new Exception("Chunk size can not be < 1");

        DownloadPart[] totalDownloadParts = new DownloadPart[model.numberOfPart];

        long startPoint = 0;
        for (int partIndex = 0; partIndex < model.numberOfPart; partIndex++) {
            DownloadPart downloadPart = DownloadTools.getDownloadPart(this);
            downloadPart.initialize(partIndex, startPoint, chunkSize);
            totalDownloadParts[partIndex] = downloadPart;
            startPoint += chunkSize;
        }
        return totalDownloadParts;
    }

    public void cancelDownload() {
        cancelAllDownloadThreads();

        model.extraText = "";
        model.isRunning = false;

        model.updateDataInDisk();
        updateDownloadStatus(CLOSE);
        updateDownloadUIProgress();
    }

    public Context getContext() {
        return this.context;
    }

    public File getDestinationFile() {
        return this.destinationFile;
    }

    //-------------- CONFIGURE DOWNLOAD TASK ----------------------------------------------------------------//

    public void setDownloadStatusListener(DownloadStatusListener listener) {
        this.downloadStatusListener = listener;
    }

    public App getApp() {
        return this.app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    private void cancelAllDownloadThreads() {
        for (DownloadPart part : totalDownloadParts) part.cancelDownload();
    }

    //========================== Callbacks from downloadParts ==========================//

    @Override
    public synchronized void onTerminate(DownloadPart downloadPart) {
        showDownloadErrorAdvise(downloadPart);

        if (!autoResumeDownloadPart(downloadPart)) {
            cancelDownload();
        }

        //update the download model to the sd card.
        model.updateDataInDisk();

        //don't delete the downloaded file from sdcard if the onlyResume
        if (!model.isOnlyResume) {
            if (model.isDeleted) {
                if (destinationFile.exists()) {
                    destinationFile.delete();
                }
            }
        }
    }

    @Override
    public void onComplete(DownloadPart downloadPart) {
        model.partProgressPercentage[downloadPart.partNumber] = 100;

        for (DownloadPart part : totalDownloadParts)
            if (part.downloadStatus != COMPLETE) return;

        model.deleteFromDisk(DownloadModel.FILE_FORMAT);
        model.isRunning = false;
        model.extraText = "";
        model.changeToCompleteModel();

        showSystemNotification(true);

        updateDownloadStatus(CLOSE);
        updateDownloadStatus(COMPLETE);

        //vibrate the notification with the setting vibrator strength.
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(30);

        trackDownloadInfo(context, model);
    }

    private void showDownloadErrorAdvise(DownloadPart downloadPart) {
        if (!isDownloadErrorAdviseShown) {
            if (downloadPart.downloadError != null) {
                if (downloadPart.downloadError instanceof FileNotFoundException) {
                    cancelDownload();
                    uiManager.openReplaceLinkAdvise(model);
                    isDownloadErrorAdviseShown = true;
                }
            }
        }
    }

    private boolean autoResumeDownloadPart(DownloadPart downloadPart) {
        boolean autoResume = false;

        //the auto resume function is on then start the download part again
        if (model.isAutoResumeEnable) {
            if (DOWNLOAD_STATUS == DOWNLOADING) {
                int maxErrorRestriction = app.getUserSettings().getMaxErrors();
                if (totalErrorCount < maxErrorRestriction) {
                    if (isNetworkAvailable(context)) {
                        downloadPart.startDownload();

                        totalErrorCount++;

                        model.isWaitingForNetwork = false;
                        model.extraText = "";
                    } else {
                        model.isWaitingForNetwork = true;
                        model.extraText = "Waiting for network...";
                    }

                    autoResume = true;
                }
            }
        }
        return autoResume;
    }


    public interface DownloadStatusListener {

        void onStatusUpdate(DownloadTask task, int downloadStatus);
    }

}

