package main.settings;

import main.app.App;
import main.data_holder.BaseWritableObject;

import java.io.File;
import java.io.Serializable;

import static android.os.Environment.getExternalStorageDirectory;
import static async_job.AsyncJob.*;

/**
 * <b>UserSettings:</b> The class is extensively used by the app itself.
 * The class just do one thing : Save the user-settings and read the settings from the sdcard.
 * <p>
 * You can get the intense on the class from the {@link App#getUserSettings()} method.
 *
 * @author Shiba.
 */
public final class UserSettings extends BaseWritableObject implements Serializable {

    public static final String applicationPath = getExternalStorageDirectory() + "/" + "Aladin DM";
    public static final String downloadCachePath = applicationPath + "/.caches(don't_delete)";
    public static final String settingPath = downloadCachePath + "/.settings";
    public static final String settingFileName = "user_settings.io";

    private static final long serialVersionUID = 2969849984460435356L;

    //<DownloadSystem>
    private boolean fileCatalog;
    private boolean smartDownload;
    private boolean autoResume;
    private boolean clipboardMonitoring;
    private boolean turboBooster;
    private boolean onlyWifi;
    private String downloadPath;
    private int bufferSize;
    private int downloadPart;
    private int maxRunningTask;
    private int maxErrors;
    private int progressTimer;
    private String userAgent;
    private boolean urlFilter;
    private boolean ttsNotification;
    //</DownloadSystem>

    //<SearchSetting>
    private boolean javascript;
    private boolean loadImage;
    private boolean desktopMode;
    private int searchEngine;
    private String webUserAgent;
    //</SearchSetting>

    /**
     * The public method of UserSettings.
     * <b>NOTE : </b> do not use the public constructor to create a new intense of the class.
     * There must be only one intense of the class, that are automatically cared by app itself.
     */
    public UserSettings() {
    }

    /**
     * Read the user_setting object file from the disk.
     * @param readingFileListener after read the object from the file the listener will be called.
     */
    public static void readFromDisk(final OnReadingFileListener readingFileListener) {
        doInBackground(new BackgroundJob() {
            @Override
            public void doInBackground() {
                final UserSettings userSettingsObject;
                userSettingsObject = (UserSettings) readObject(new File(settingPath, settingFileName));

                doInMainThread(new MainThreadJob() {
                    @Override
                    public void doInUIThread() {
                        readingFileListener.onReadingFinish(userSettingsObject);
                    }
                });
            }
        });
    }

    /**
     * Validate all the base folders of the App.
     */
    public static boolean[] validateBaseFolders() {
        boolean result[] = new boolean[3];
        File appDirectory = new File(applicationPath);
        File downloadCacheDirectory = new File(downloadCachePath);
        File settingDirectory = new File(settingPath);

        if (!appDirectory.exists()) result[0] = appDirectory.mkdirs();
        if (!downloadCacheDirectory.exists()) result[1] = downloadCacheDirectory.mkdirs();
        if (!settingDirectory.exists()) result[2] = settingDirectory.mkdirs();

        return result;
    }

    /**
     * Save the object in the sdcard disk memory.
     */
    public void updateInDisk() {
        doInBackground(new BackgroundJob() {
            @Override
            public void doInBackground() {
                writeObject(UserSettings.this, settingPath, settingFileName);
            }
        });
    }

    /**
     * Reset all the settings to the default values.
     */
    public void resetDealtSettings() {
        setAutoResume(true);
        setClipboardMonitoring(true);
        setSmartDownload(true);
        setFileCatalog(true);
        setTtsNotification(true);

        setDownloadPath(applicationPath);
        setBufferSize((1024 * 4));
        setMaxRunningTask(3);
        setDownloadPart(2);
        setMaxErrors(1024);
        setProgressTimer(1);
        setTurboBooster(true);
        setOnlyWifi(false);
        setUserAgent("");
        setUrlFilter(false);

        setJavascript(true);
        setLoadImage(true);
        setDesktopMode(false);
        setSearchEngine(0);
        setWebUserAgent("");
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        this.updateInDisk();
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        this.updateInDisk();
    }

    public int getDownloadPart() {
        return downloadPart;
    }

    public void setDownloadPart(int downloadPart) {
        this.downloadPart = downloadPart;
        this.updateInDisk();
    }

    public int getMaxRunningTask() {
        return maxRunningTask;
    }

    public void setMaxRunningTask(int maxRunningTask) {
        this.maxRunningTask = maxRunningTask;
        this.updateInDisk();
    }

    public int getMaxErrors() {
        return maxErrors;
    }

    public void setMaxErrors(int maxErrors) {
        this.maxErrors = maxErrors;
        updateInDisk();
    }

    public int getProgressTimer() {
        return progressTimer;
    }

    public void setProgressTimer(int progressTimer) {
        this.progressTimer = progressTimer;
        updateInDisk();
    }

    public boolean isFileCatalog() {
        return fileCatalog;
    }

    public void setFileCatalog(boolean fileCatalog) {
        this.fileCatalog = fileCatalog;
        this.updateInDisk();
    }

    public boolean isSmartDownload() {
        return smartDownload;
    }

    public void setSmartDownload(boolean smartDownload) {
        this.smartDownload = smartDownload;
        this.updateInDisk();
    }

    public boolean isAutoResume() {
        return autoResume;
    }

    public void setAutoResume(boolean autoResume) {
        this.autoResume = autoResume;
        this.updateInDisk();
    }

    public boolean isTtsNotification() {
        return ttsNotification;
    }

    public void setTtsNotification(boolean ttsNotification) {
        this.ttsNotification = ttsNotification;
        this.updateInDisk();
    }

    public boolean isEnableTurboBooster() {
        return turboBooster;
    }

    public void setTurboBooster(boolean enableAutoSpeedController) {
        this.turboBooster = enableAutoSpeedController;
        this.updateInDisk();
    }

    public boolean isOnlyWifi() {
        return onlyWifi;
    }

    public void setOnlyWifi(boolean onlyWifi) {
        this.onlyWifi = onlyWifi;
        this.updateInDisk();
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        this.updateInDisk();
    }

    public boolean isJavascript() {
        return javascript;
    }

    public void setJavascript(boolean javascript) {
        this.javascript = javascript;
        updateInDisk();
    }

    public boolean isLoadImage() {
        return loadImage;
    }

    public void setLoadImage(boolean loadImage) {
        this.loadImage = loadImage;
        updateInDisk();
    }

    public boolean isDesktopMode() {
        return desktopMode;
    }

    public void setDesktopMode(boolean desktopMode) {
        this.desktopMode = desktopMode;
        updateInDisk();
    }

    public int getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(int searchEngine) {
        this.searchEngine = searchEngine;
        updateInDisk();
    }

    public String getWebUserAgent() {
        return webUserAgent;
    }

    public void setWebUserAgent(String webUserAgent) {
        this.webUserAgent = webUserAgent;
        this.updateInDisk();
    }

    public boolean isUrlFilter() {
        return urlFilter;
    }

    public void setUrlFilter(boolean urlFilter) {
        this.urlFilter = urlFilter;
        updateInDisk();
    }

    public boolean isClipboardMonitoring() {
        return clipboardMonitoring;
    }

    public void setClipboardMonitoring(boolean clipboardMonitoring) {
        this.clipboardMonitoring = clipboardMonitoring;
        this.updateInDisk();
    }
}
