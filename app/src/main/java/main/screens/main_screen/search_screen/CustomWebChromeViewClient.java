package main.screens.main_screen.search_screen;

import android.app.Dialog;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import net.fdm.R;

/**
 * WebChromeViewClient is the client class receive some useful callbacks from webview that this class is working for.
 * such as progress Callbacks etc.
 */
public class CustomWebChromeViewClient extends WebChromeClient {

    public WebEngine webEngine;
    private Dialog customViewContainer;
    private HtmlVideoParser htmlVideoParser;


    /**
     * Public constructor.
     *
     * @param webEngine the web engine is which this webChromeViewClient class is working for.
     */
    public CustomWebChromeViewClient(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.htmlVideoParser = new HtmlVideoParser(this);
    }


    @Override
    public void onProgressChanged(WebView view, int progress) {
        webEngine.updateProgressBar(progress);

        ProgressBar progressBar = webEngine.searchScreen.loadingProgress;

        if (progressBar.getProgress() == 100 || progressBar.getProgress() > 100) {
            progressBar.setVisibility(View.GONE);
            webEngine.searchScreen.stopButton.setImageDrawable(
                    webEngine.searchScreen.getMainScreen().getDrawableImage(R.drawable.ic_web_loading_reload));
        } else {
            webEngine.searchScreen.stopButton.setImageDrawable(
                    webEngine.searchScreen.getMainScreen().getDrawableImage(R.drawable.ic_web_loading_stop));
        }

        super.onProgressChanged(view, progress);
    }


    @Override
    public void onReceivedTitle(WebView webView, String title) {
        webEngine.hideVirtualKeyboard();
        webEngine.setWebUrlToTitle(webView.getUrl());
    }


    public void onShowCustomView(final View customView, CustomViewCallback customViewCallback) {
        super.onShowCustomView(customView, customViewCallback);

        try {
            if (this.htmlVideoParser != null) {
                htmlVideoParser.catchVideo(customView, customViewCallback);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public void onHideCustomView() {
        try {
            if (customViewContainer != null) {
                customViewContainer.dismiss();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

}
