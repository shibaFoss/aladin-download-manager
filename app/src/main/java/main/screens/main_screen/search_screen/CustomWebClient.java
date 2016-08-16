package main.screens.main_screen.search_screen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import main.download_manager.WantedDownloadUrls;
import main.screens.main_screen.MainScreen;
import main.utilities.FileCatalog;
import net.fdm.R;

public class CustomWebClient extends WebViewClient {

    private WebEngine webEngine;
    private WebVideoUriParser webVideoUriParser;
    private SearchScreen searchScreen;
    private MainScreen mainScreen;


    public CustomWebClient(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.searchScreen = webEngine.searchScreen;
        this.mainScreen = searchScreen.getMainScreen();
        this.webVideoUriParser = new WebVideoUriParser(this);
    }


    public WebVideoUriParser getWebVideoUriParser() {
        if (this.webVideoUriParser == null)
            this.webVideoUriParser = new WebVideoUriParser(this);

        return this.webVideoUriParser;
    }


    public WebEngine getWebEngine() {
        return this.webEngine;
    }


    @Override
    public void onPageStarted(WebView webView, String url, Bitmap favicon) {
        webEngine.setWebUrlToTitle(url);
        searchScreen.favoriteButton.setImageDrawable(mainScreen.getDrawableImage(R.drawable.ic_love_bookmark_unpress));
    }


    @Override
    public void onPageFinished(WebView webView, String pageUrl) {
        if (searchScreen.isBookmarkSaved(pageUrl)) {
            ImageButton favoriteButton = searchScreen.favoriteButton;
            favoriteButton.setImageDrawable(mainScreen.getDrawableImage(R.drawable.ic_love_bookmark_press));
        }

        String fileName = URLUtil.guessFileName(pageUrl, null, null);

        //show a download option if it is a downloadable file.
        for (String format : FileCatalog.ALl_DOWNLOADABLE_FORMAT) {
            if (fileName.endsWith("." + format)) {
                webEngine.downloadListener.showDownloadOption(pageUrl, fileName);
            }
        }
    }


    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        String fileName = URLUtil.guessFileName(url, null, null);

        for (String format : FileCatalog.ALl_DOWNLOADABLE_FORMAT) {
            if (fileName.endsWith("." + format)) {
                webEngine.downloadListener.showDownloadOption(url, fileName);
                return true; //we don't need to  load the video url so return it as true.
            }
        }

        try {
            for (String wantedRes : WantedDownloadUrls.wantedDownloadUrls) {
                if (url.startsWith(wantedRes)) {
                    webEngine.downloadListener.showDownloadOption(url, "Youtube Video playback.mp4");
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

        try {
            if (url.contains("rtsp")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                webEngine.searchScreen.startActivity(intent);

            } else if (url.contains("market://details?id")) {
                webEngine.searchScreen.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

            } else if (url.contains("whatsapp://")) {
                webEngine.searchScreen.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

            } else {
                webEngine.loadUrl(url);
            }
        } catch (Exception error) {
            error.printStackTrace();
            this.webEngine.loadUrl(url);
        }

        return false;
    }


    @Override
    public void onLoadResource(WebView view, String resourceUri) {
        try {
            webVideoUriParser.addUrlResource(resourceUri);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    @Override
    public android.webkit.WebResourceResponse shouldInterceptRequest(WebView view, final String resourceUrl) {
        try {
            webVideoUriParser.addUrlResource(resourceUrl);
        } catch (Exception error) {
            error.printStackTrace();
        }
        return null;
    }


}

