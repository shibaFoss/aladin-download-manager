package main.screens.main_screen.search_screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.TextView;
import main.key_database.KeyStore;
import main.utilities.URLUtility;
import remember_lib.Remember;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WebEngine {

    public SearchScreen searchScreen;
    public CustomDownloadListener downloadListener;
    public CustomWebClient webClient;
    public CustomWebChromeViewClient chromeViewClient;


    public WebEngine(SearchScreen searchScreen) {
        this.searchScreen = searchScreen;
        this.initCustomWebHelperClasses();
        this.initWebUrlGoSearch();
        this.initWebSettings();
    }


    public void loadUrl(String webAddress) {
        try {
            this.resetWebLoadingProgress();
            if (this.isUrlProtocol(webAddress)) {
                if (!isHttpProtocol(webAddress)) webAddress = "http://" + webAddress;

                searchScreen.webView.loadUrl(removeSpace(webAddress));
            } else {
                try {
                    for (String domainEnd : URLUtility.ALL_POSSIBLE_WEB_DOMAIN_END) {
                        if (webAddress.toLowerCase().endsWith(domainEnd)) {
                            loadUrl("www." + webAddress);
                            return;
                        }
                    }

                    loadSearchQuery(webAddress);
                } catch (UnsupportedEncodingException error) {
                    error.printStackTrace();
                    searchScreen.getMainScreen().toast("Unsupported search query");
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public void loadSearchQuery(String searchQuery) throws UnsupportedEncodingException {
        String query = URLEncoder.encode(searchQuery, "UTF-8");
        String url;
        url = "http://google.com/m?q=" + query;

        if (searchScreen.getMainScreen().app.getUserSettings().getSearchEngine() == 1)
            url = "www.bing.com/search?q=" + query;

        else if (searchScreen.getMainScreen().app.getUserSettings().getSearchEngine() == 2)
            url = "www.duckduckgo.com/?q=" + query;

        loadUrl(url);
    }


    public void updateProgressBar(int progress) {
        if (searchScreen.loadingProgress.getVisibility() == View.GONE) {
            searchScreen.loadingProgress.setVisibility(View.VISIBLE);
        }
        searchScreen.loadingProgress.setProgress(progress);
    }


    public void hideVirtualKeyboard() {
        getInputMethodManager()
                .hideSoftInputFromWindow(searchScreen.webUrl.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }


    public InputMethodManager getInputMethodManager() {
        return (InputMethodManager) searchScreen.getMainScreen()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    public void resetWebLoadingProgress() {
        searchScreen.loadingProgress.setVisibility(View.VISIBLE);
        searchScreen.loadingProgress.setProgress(0);
        searchScreen.webView.stopLoading();
        hideVirtualKeyboard();
    }


    public void setWebUrlToTitle(String url) {
        if (searchScreen.webUrl != null)
            searchScreen.webUrl.setText(url);

        hideVirtualKeyboard();
    }


    public void stopWebLoading() {
        if (searchScreen.loadingProgress.getVisibility() == View.VISIBLE)
            searchScreen.webView.stopLoading();

        else if (searchScreen.loadingProgress.getVisibility() == View.GONE)
            searchScreen.webView.reload();
    }


    public void backWebView() {
        searchScreen.webView.goBack();
    }


    private String removeSpace(String input) {
        return input.replaceAll(" ", "").trim();
    }


    public String getCurrentWebUrl() {
        return "" + searchScreen.webView.getUrl();
    }


    private boolean isHttpProtocol(String webAddress) {
        return webAddress.startsWith("http://") ||
                webAddress.startsWith("https://");
    }


    private boolean isUrlProtocol(String url) {
        return url.startsWith("www.") ||
                url.startsWith("http://") || url.startsWith("https://");
    }


    private void initCustomWebHelperClasses() {
        downloadListener = new CustomDownloadListener(this);
        webClient = new CustomWebClient(this);
        chromeViewClient = new CustomWebChromeViewClient(this);
    }


    private void initWebUrlGoSearch() {
        searchScreen.webUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView titleView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String enteredText = searchScreen.webUrl.getText().toString();
                    hideVirtualKeyboard();
                    loadUrl(enteredText);
                    return true;
                }
                return false;
            }
        });
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSettings() {
        WebView web = searchScreen.webView;
        web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);

        String userAgentString = web.getSettings().getUserAgentString();
        Remember.putString(KeyStore.DEFAULT_USER_AGENT, userAgentString);

        web.setDownloadListener(downloadListener);
        web.setWebViewClient(webClient);
        web.setWebChromeClient(chromeViewClient);
        web.getSettings().setJavaScriptEnabled(true);

        try {
            if (searchScreen.getMainScreen().app.getUserSettings().isDesktopMode())
                web.getSettings().setUserAgentString(
                        "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
            else {
                String savedUserAgent = searchScreen.getMainScreen().app.getUserSettings().getWebUserAgent();
                if (savedUserAgent != null) {
                    if (savedUserAgent.length() > 2) {
                        web.getSettings().setUserAgentString(savedUserAgent);
                    }
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

        web.getSettings().setSupportZoom(true);
        web.getSettings().setUseWideViewPort(true);
        web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        initUpWebTouchBehaviors();
    }


    private void initUpWebTouchBehaviors() {
        searchScreen.webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View webView, MotionEvent motionEvent) {
                setWebUrlToTitle(searchScreen.webView.getUrl());
                resetWebViewFocus(webView, motionEvent);

                try {
                    WebVideoUriParser webVideoUriParser = webClient.getWebVideoUriParser();
                    if (!getCurrentWebUrl().contains("youtube.com")) {
                        webVideoUriParser.startWebUrlInspectionTimer();
                    } else {
                        searchScreen.bottomLayout.setVisibility(View.GONE);
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                }
                return false;
            }
        });
    }


    private void resetWebViewFocus(View webView, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                if (!webView.hasFocus()) {
                    webView.requestFocus();
                }
                break;
        }
    }

}
