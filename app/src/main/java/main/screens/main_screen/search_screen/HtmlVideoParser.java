package main.screens.main_screen.search_screen;

import android.net.Uri;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;
import main.dialog_factory.YesNoDialog;
import main.screens.BaseScreen;
import main.screens.main_screen.MainScreen;
import main.utilities.NetworkUtils;

import java.lang.reflect.Field;

public class HtmlVideoParser {

    private CustomWebChromeViewClient webChromeViewClient;
    private WebEngine webEngine;
    private MainScreen mainScreen;

    public HtmlVideoParser(CustomWebChromeViewClient webChromeViewClient) {
        this.webChromeViewClient = webChromeViewClient;
        this.init();
    }

    private void init() {
        this.webEngine = webChromeViewClient.webEngine;
        this.mainScreen = webEngine.searchScreen.getMainScreen();
    }

    public void catchVideo(View customView, WebChromeClient.CustomViewCallback customViewCallback) {
        if (customView instanceof FrameLayout) {
            catchVideoUri((FrameLayout) customView);
        }
    }

    private void catchVideoUri(FrameLayout customView) {
        try {
            View childView = customView.getFocusedChild();
            if (childView instanceof VideoView) {
                Uri videoUri = null;
                Field uriField = VideoView.class.getDeclaredField("mUri");
                uriField.setAccessible(true);
                videoUri = (Uri) uriField.get(childView);
                final String videoUrl = videoUri.toString();
                promptDownloadOption(mainScreen, webEngine, videoUrl);

            } else if (childView instanceof SurfaceView) {
                Uri videoUri = null;
                Class videoSurfaceView = Class.forName("android.webkit.HTML5VideoFullScreen$VideoSurfaceView");

                Field html5VideoFullscreen = videoSurfaceView.getDeclaredField("this$0");
                html5VideoFullscreen.setAccessible(true);

                Object html5VideoFullscreenInstance = html5VideoFullscreen.get(customView.getFocusedChild());
                @SuppressWarnings("rawtypes")
                Class html5VideoClass = html5VideoFullscreen.getType().getSuperclass();

                Field uriField = html5VideoClass.getDeclaredField("mUri");
                uriField.setAccessible(true);
                videoUri = (Uri) uriField.get(html5VideoFullscreenInstance);
                final String videoUrl = videoUri.toString();
                promptDownloadOption(mainScreen, webEngine, videoUrl);
            }
        } catch (Exception error) {
            error.printStackTrace();
            mainScreen.showSimpleMessageBox("The video is protected.");
        }
    }

    public static void promptDownloadOption(BaseScreen baseScreen, final WebEngine webEngine, final String videoUrl) {
        if (videoUrl != null && videoUrl.length() > 4) {
            final YesNoDialog yesNoDialog = new YesNoDialog(baseScreen);
            yesNoDialog.setMessage("Do you want to download the video?");
            yesNoDialog.clickListener = new YesNoDialog.ClickListener() {
                @Override
                public void onYes(TextView view) {
                    yesNoDialog.close();
                    String videoName = NetworkUtils.getFileNameFromUrl(videoUrl);
                    if (videoName.toLowerCase().endsWith(".bin"))
                        videoName = videoName.split(".bin")[0] + ".mp4";

                    webEngine.downloadListener.
                            showDownloadOption(videoUrl, videoName);
                }

                @Override
                public void onNo(TextView view) {
                    yesNoDialog.close();
                }
            };
            yesNoDialog.show();
        }
    }
}
