package main.screens.main_screen.search_screen;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import libs.remember_lib.Remember;
import main.dialog_factory.YesNoDialog;
import main.key_database.KeyStore;
import main.screens.main_screen.MainScreen;
import main.utilities.FileCatalog;

import static android.webkit.URLUtil.guessFileName;
import static java.net.URLDecoder.decode;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class WebVideoUriParser implements View.OnClickListener {

    //The variable is useful to know if the system currently showing the youtube
    // alert to the  user.
    public boolean isYoutubeAlertShowing = false;
    private CustomWebClient webClient;
    private WebEngine webEngine;
    private MainScreen mainScreen;
    //The total resource uris that a single web page throws are
    //stored in this array list.
    private ArrayList<String> resourceList = new ArrayList<>();
    //Every time web_view get touched this timer get started for 1.5 sec
    //when the timer get finished it automatically starts the actual url inspection function.
    private CountDownTimer timer;

    public WebVideoUriParser(CustomWebClient webClient) {
        this.webClient = webClient;
        this.init();
    }

    private void init() {
        this.webEngine = webClient.getWebEngine();
        this.mainScreen = webEngine.searchScreen.getMainScreen();
        webEngine.searchScreen.openButton.setOnClickListener(this);
        webEngine.searchScreen.clearButton.setOnClickListener(this);
    }

    public synchronized void addUrlResource(final String resourceUrl) {
        String url;
        try {
            url = decode(resourceUrl, "UTF-8");
            for (String unwantedRes : getUnwantedResources())
                if (url.contains(unwantedRes)) return;

            if (isVideoResourceUrl(resourceUrl)) {
                if (!resourceList.contains(url)) {
                    resourceList.add(url);
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private String[] getUnwantedResources() {
        return new String[]{
                "google-analytics.com",
                "ads.mopub.com",
                "googleads.g.doubleclick.net",
                "cm.g.doubleclick.net",
                "ad.auditude.com",
                "b.scorecardresearch.com",
                "pagead2.googlesyndication.com"
        };
    }

    private boolean isVideoResourceUrl(String resourceUrl) {
        for (String res : getWantedResources()) {
            if (resourceUrl.startsWith(res))
                return true;
        }
        return isVideo(guessFileName(resourceUrl, null, null));
    }

    private String[] getWantedResources() {
        return new String[]{
                "http://player.vimeo.com/play", "https://player.vimeo.com/play"
        };
    }

    private boolean isVideo(String name) {
        for (String format : FileCatalog.VIDEO)
            if (name.toLowerCase().endsWith(format))
                return true;

        return false;
    }

    public void startWebUrlInspectionTimer() {
        //we don't want to parse the youtube videos. because google does not like this.
        if (webEngine.getCurrentWebUrl().contains("youtube.com")) {
            if (!isYoutubeAlertShowing) {
                if (!Remember.getBoolean(KeyStore.IS_YOUTUBE_ALERT_SHOWN, false)) {
                    final YesNoDialog alertPrompt = new YesNoDialog(webEngine.searchScreen.getMainScreen());
                    alertPrompt.setMessage("Warning!\n" +
                            "Aladin DM do not support " +
                            "downloading any content from YouTube due to it's term & conditions.");
                    alertPrompt.setButtonNames("Cancel", "Don't show again");
                    alertPrompt.clickListener = new YesNoDialog.ClickListener() {
                        @Override
                        public void onYes(TextView view) {
                            alertPrompt.close();
                        }

                        @Override
                        public void onNo(TextView view) {
                            alertPrompt.close();
                            Remember.putBoolean(KeyStore.IS_YOUTUBE_ALERT_SHOWN, true);
                        }
                    };
                    alertPrompt.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            isYoutubeAlertShowing = false;
                        }
                    });

                    isYoutubeAlertShowing = true;
                    alertPrompt.show();
                }
            }
            return;
        }

        if (timer != null) {
            timer.cancel();
            timer.start();
        } else {
            initTimer();
            timer.start();
        }
    }

    private void initTimer() {
        timer = new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                startUriInspection();
            }
        };
    }

    private void startUriInspection() {
        try {
            int size = getResourceList().size();
            if (size > 0) {
                if (webEngine.searchScreen.bottomLayout.getVisibility() == View.GONE)
                    webEngine.searchScreen.bottomLayout.setVisibility(View.VISIBLE);

                webEngine.searchScreen.totalVideoFoundPreview.setText(size + " Videos are found.");
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public ArrayList<String> getResourceList() {
        return this.resourceList;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == webEngine.searchScreen.openButton.getId()) {
            openVideos();
        } else if (view.getId() == webEngine.searchScreen.clearButton.getId()) {
            clearResourceArray();
        }
    }

    private void openVideos() {
        if (getResourceList().size() > 0) {
            if (!webEngine.getCurrentWebUrl().contains("youtube.com")) {
                new Videos(webEngine.searchScreen, resourcesAsArray()).show();
            }
        }
    }

    private void clearResourceArray() {
        getResourceList().clear();
        webEngine.searchScreen.bottomLayout.setVisibility(View.GONE);
    }

    private String[] resourcesAsArray() {
        return getResourceList().toArray(new String[getResourceList().size()]);
    }

}
