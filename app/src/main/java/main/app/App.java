package main.app;

import android.app.Application;

import java.io.File;
import java.io.Serializable;

import libs.remember_lib.Remember;
import main.data_holder.BookmarkLoader;
import main.download_manager.DownloadSystem;
import main.parse.ParseServer;
import main.screens.music_screen.AudioManager;
import main.settings.OnReadingFileListener;
import main.settings.UserSettings;
import main.user_tracking.UserTracker;

public class App extends Application implements Serializable {

    public static boolean IS_DEBUGGING_MODE = true;

    private static BookmarkLoader bookmarkLoader;
    private static DownloadSystem downloadSystem;

    private AudioManager audioManager;
    private GlobalClasses globalClasses;

    private UserSettings userSettings;
    private UserTracker userTracker;


    public static boolean isPowerUser() {
        return new File(UserSettings.applicationPath, "i_am_power_user.io").exists();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        ParseServer.initialize(this);
        Remember.init(this, getPackageName());

        UserSettings.validateBaseFolders();
        initUserAccount();

        initBookmarkLoader();
        initDownloadSystem();

        globalClasses = new GlobalClasses();
        audioManager = new AudioManager(this);
    }

    private void initUserAccount() {
        UserSettings.readFromDisk(new OnReadingFileListener() {
            @Override
            public void onReadingFinish(UserSettings userSettings) {
                App.this.userSettings = userSettings;
                if (App.this.userSettings == null) {
                    App.this.userSettings = new UserSettings();
                    App.this.userSettings.resetDealtSettings();
                    App.this.userSettings.updateInDisk();
                }
            }
        });
    }

    private void initBookmarkLoader() {
        bookmarkLoader = BookmarkLoader.readFromDisk();
    }

    private void initDownloadSystem() {
        downloadSystem = new DownloadSystem();
        downloadSystem.prepare(this);
    }

    public AudioManager getAudioManager() {
        if (audioManager == null) {
            audioManager = new AudioManager(this);
        }
        return this.audioManager;
    }

    public UserSettings getUserSettings() {
        if (this.userSettings == null) {
            return new UserSettings();
        } else {
            return this.userSettings;
        }
    }

    public BookmarkLoader getBookmarkLoader() {
        return bookmarkLoader;
    }

    public DownloadSystem getDownloadSystem() {
        return downloadSystem;
    }

    public UserTracker getUserTracker() {
        if (userTracker == null) {
            this.userTracker = new UserTracker(this);
        }
        return this.userTracker;
    }


    public GlobalClasses getGlobalClasses() {
        return globalClasses;
    }
}
