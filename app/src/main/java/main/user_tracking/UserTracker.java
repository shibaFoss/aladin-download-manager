package main.user_tracking;

import java.io.Serializable;

import libs.remember_lib.Remember;
import main.app.App;
import main.key_database.KeyStore;
import main.parse.ParseServer;
import main.screens.main_screen.MainScreen;
import main.screens.main_screen.RatingPrompt;

public class UserTracker implements Serializable {

    private App app;
    private String visibleDownloadLink = null;


    public UserTracker(App app) {
        this.app = app;
        this.init();
    }


    private void init() {
        boolean isFirstOpen = Remember.getBoolean("FIRST_VISIT", true);
        if (isFirstOpen) {
            ParseServer.firstOpenTrack(this.app);
        }
    }


    public App getApp() {
        return this.app;
    }


    public void onSlashScreenLaunch() {
        Remember.putInt(KeyStore.TOTAL_APP_LAUNCH, Remember.getInt(KeyStore.TOTAL_APP_LAUNCH, 0) + 1);
    }


    public void promptForAppRating(MainScreen mainScreen) {
        int totalAppLaunch = Remember.getInt(KeyStore.TOTAL_APP_LAUNCH, 0);

        if (Remember.getBoolean(KeyStore.IS_RATED, false)) {
            mainScreen.promptUserForExit();
        } else {
            if (totalAppLaunch > 10) {
                new RatingPrompt(mainScreen).setForExit().show();
            } else {
                if (mainScreen.fullscreenAd.isLoaded()) {
                    mainScreen.showFullscreenAd(true);
                    mainScreen.isExitFullScreenAdRequested = true;
                } else {
                    mainScreen.promptUserForExit();
                }
            }
        }
    }


    public String getVisibleDownloadLink() {
        return visibleDownloadLink;
    }


    public void setVisibleDownloadLink(String link) {
        this.visibleDownloadLink = link;
    }
}


