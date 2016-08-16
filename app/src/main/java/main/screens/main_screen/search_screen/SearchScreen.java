package main.screens.main_screen.search_screen;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;
import main.data_holder.Bookmark;
import main.data_holder.BookmarkLoader;
import main.screens.main_screen.BaseNestedScreen;
import main.utilities.TextUtility;
import net.fdm.R;

/**
 * SearchScreen is the screen where user can use a single tab web browser
 */
public class SearchScreen extends BaseNestedScreen implements View.OnClickListener {

    public EditText webUrl;
    public WebView webView;
    public ProgressBar loadingProgress;

    public TextView totalVideoFoundPreview;
    public ImageButton backButton, stopButton, favoriteButton, settingButton;

    public Button openButton, clearButton;
    public RelativeLayout bottomLayout;

    public WebEngine webEngine;

    @Override
    protected int getScreenLayout() {
        return R.layout.search_screen;
    }

    @Override
    protected void onAfterLayoutLoad(View layoutView, Bundle savedInstanceState) {
        this.init(layoutView);
        this.webEngine = new WebEngine(this);
        this.loadDefaultUrl();
        this.getMainScreen().globalObjectStore.searchScreen = this;
    }

    @Override
    protected void onViewCreating(View view) {

    }

    private void loadDefaultUrl() {
        if (getMainScreen().getIntendedUrl() != null) {
            String[] links = TextUtility.extractLinks(getMainScreen().getIntendedUrl());
            webEngine.loadUrl(links.length > 0 ? links[0] : "www.google.com");
        } else {
            webEngine.loadUrl("http://google.com");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void init(View layoutView) {
        webUrl = (EditText) layoutView.findViewById(R.id.url_input);
        webView = (WebView) layoutView.findViewById(R.id.web_view);
        loadingProgress = (ProgressBar) layoutView.findViewById(R.id.loading_progress);
        backButton = (ImageButton) layoutView.findViewById(R.id.back_bnt);
        stopButton = (ImageButton) layoutView.findViewById(R.id.reload_bnt);
        favoriteButton = (ImageButton) layoutView.findViewById(R.id.favorite_bookmark_bnt);

        settingButton = (ImageButton) layoutView.findViewById(R.id.settings_bnt);

        totalVideoFoundPreview = (TextView) layoutView.findViewById(R.id.preview_title);
        openButton = (Button) layoutView.findViewById(R.id.open_bnt);
        clearButton = (Button) layoutView.findViewById(R.id.clear_bnt);

        bottomLayout = (RelativeLayout) layoutView.findViewById(R.id.bottom_layout);

        backButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        favoriteButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int clickId = view.getId();

        if (clickId == settingButton.getId()) {
            new SearchSetting(this).show();

        } else if (clickId == backButton.getId()) {
            webEngine.backWebView();

        } else if (clickId == stopButton.getId()) {
            webEngine.stopWebLoading();

        } else if (clickId == favoriteButton.getId()) {
            addNewBookmark(webView.getUrl(), webView.getTitle());
        }
    }

    private void addNewBookmark(String url, String name) {
        if (!isBookmarkSaved(url)) {
            AddBookmarkDialog bookmarkDialog = new AddBookmarkDialog(getMainScreen());
            bookmarkDialog.setDefaultValue(url, name);
            bookmarkDialog.setFavoriteButton(this.favoriteButton);
            bookmarkDialog.show();
        } else {
            String message =
                    "The bookmark already has been saved.\n\n" +
                            "You can delete it from bookmark list.";

            getMainScreen().showSimpleMessageBox(message);
        }
    }

    public boolean isBookmarkSaved(String url) {
        BookmarkLoader bookmarkLoader = getMainScreen().app.getBookmarkLoader();
        for (Bookmark bookmark : bookmarkLoader.getBookmarksList()) {
            if (bookmark.url.equals(url)) {
                return true;
            }
        }
        return false;
    }
}
