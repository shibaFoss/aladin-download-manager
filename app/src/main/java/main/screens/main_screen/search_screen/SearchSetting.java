package main.screens.main_screen.search_screen;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import main.app.App;
import main.dialog_factory.MessageBox;
import main.utilities.HiddenUtility;
import main.utilities.UiUtils;
import net.fdm.R;

import java.net.URL;

/**
 * SearchSetting class represent a set of browser settings to the user.
 */
public class SearchSetting implements View.OnClickListener {

    private SearchScreen searchScreen;
    private Dialog dialog;
    private TextView forward, savefrom, sharePage, copyUrl, desktopMode, browserUserAgent, moreSettings;

    public SearchSetting(SearchScreen searchScreen) {
        this.searchScreen = searchScreen;
        this.init();
    }

    public void show() {
        if (dialog != null) dialog.show();
        initHiddenFeature();
    }

    public void close() {
        if (dialog != null) dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(searchScreen.getMainScreen(), R.layout.browser_settings);

        forward = (TextView) dialog.findViewById(R.id.forward_bnt);
        savefrom = (TextView) dialog.findViewById(R.id.savefrom_bnt);
        sharePage = (TextView) dialog.findViewById(R.id.share_bnt);
        copyUrl = (TextView) dialog.findViewById(R.id.copy_bnt);
        desktopMode = (TextView) dialog.findViewById(R.id.desktop_mode_bnt);
        browserUserAgent = (TextView) dialog.findViewById(R.id.browsing_user_agent_bnt);
        moreSettings = (TextView) dialog.findViewById(R.id.more_settings_bnt);

        registerClickEvents();
    }

    private void initHiddenFeature() {
        if (App.isPowerUser()) {
            savefrom.setVisibility(View.VISIBLE);
        } else {
            savefrom.setVisibility(View.GONE);
        }
    }

    private void registerClickEvents() {
        forward.setOnClickListener(this);
        savefrom.setOnClickListener(this);
        sharePage.setOnClickListener(this);
        copyUrl.setOnClickListener(this);
        desktopMode.setOnClickListener(this);
        browserUserAgent.setOnClickListener(this);
        moreSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int clickedId = view.getId();
        if (clickedId == forward.getId())
            forwardPage();
        else if (clickedId == savefrom.getId())
            savefrom();
        else if (clickedId == sharePage.getId())
            sharePage();
        else if (clickedId == copyUrl.getId())
            copyUrl();
        else if (clickedId == desktopMode.getId())
            desktopMode();
        else if (clickedId == browserUserAgent.getId())
            browserUserAgent();
        else if (clickedId == moreSettings.getId())
            moreSettings();
    }

    private void savefrom() {
        close();

        WebEngine webEngine = searchScreen.webEngine;
        String url = webEngine.getCurrentWebUrl();
        try {
            URL webAddress = new URL(url);
            if (webAddress.getHost().contains("youtube.com")) {
                String youtubeVideoId = HiddenUtility.getYoutubeVideoId(webAddress.toString());
                if (youtubeVideoId != null) {
                    String youtubeVideoUrl = "http://www.ssyoutube.com/watch?v=" + youtubeVideoId;
                    webEngine.loadUrl(youtubeVideoUrl);
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void trendingRequest() {
        close();
        new TrendingRequestDialog(searchScreen.getMainScreen())
                .setWebAddress(searchScreen.webEngine.getCurrentWebUrl())
                .setWebTitle(searchScreen.webView.getTitle())
                .show();
    }

    private void moreSettings() {
        close();
        MessageBox messageBox = MessageBox.getInstance(searchScreen.getMainScreen());
        messageBox.setMessage("The feature will coming soon.");
        messageBox.setOkButtonText("OK");
        messageBox.show();
    }

    private void browserUserAgent() {
        close();
        new UserAgentChanger(searchScreen).show();
    }

    private void desktopMode() {
        close();
        String userAgent = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
        searchScreen.webView.getSettings().setUserAgentString(userAgent);
        searchScreen.webView.reload();
    }

    private void copyUrl() {
        close();
        ClipboardManager clipboard =
                (ClipboardManager) searchScreen.getMainScreen().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("URL", searchScreen.webView.getUrl());
        clipboard.setPrimaryClip(clip);
        searchScreen.getMainScreen().vibrator.vibrate(10);
        searchScreen.getMainScreen().toast("Copied");
    }

    private void sharePage() {
        close();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        try {
            Uri data = Uri.parse(searchScreen.webView.getUrl());
            intent.setData(data);
            searchScreen.getMainScreen().startActivity(intent);
        } catch (Exception error) {
            error.printStackTrace();
            MessageBox messageBox = MessageBox.getInstance(searchScreen.getMainScreen());
            messageBox.setMessage(searchScreen.getMainScreen().getString(R.string.share_failed_dialog));
            messageBox.show();
        }
    }

    private void forwardPage() {
        close();
        if (searchScreen.webView.canGoForward())
            searchScreen.webView.goForward();
    }
}
