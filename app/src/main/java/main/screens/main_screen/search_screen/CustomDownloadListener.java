package main.screens.main_screen.search_screen;

import android.content.DialogInterface;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import main.download_manager.DownloadTaskEditor;

/**
 * Download Listener is the class that listen any call for download. It basically show a dialog with download
 * maker option to user when a download is fired by web view.
 */
public class CustomDownloadListener implements DownloadListener {

    public WebEngine webEngine;
    private String showingDownloadUrl = "";

    public CustomDownloadListener(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        showNewDownloadOption(url, URLUtil.guessFileName(url, contentDisposition, mimeType));
    }

    public void showDownloadOption(String url, String name) {
        String customFileName = name.replaceAll("_", " ").trim();
        showNewDownloadOption(url, customFileName);
    }

    private void showNewDownloadOption(final String url, final String name) {
        if (showingDownloadUrl.equals(url)) return;
        else showingDownloadUrl = url;

        DownloadTaskEditor downloadTaskEditor = new DownloadTaskEditor(webEngine.searchScreen.getMainScreen());
        setUpDismissBehaviorOf(downloadTaskEditor);
        setUpFileNameOf(downloadTaskEditor, url, name);
        downloadTaskEditor.setFileUrl(url);
        downloadTaskEditor.show();
    }

    private void setUpFileNameOf(DownloadTaskEditor downloadTaskEditor, String url, String name) {
        if (name != null) downloadTaskEditor.setFileName(name);
        else downloadTaskEditor.setFileName(URLUtil.guessFileName(url, null, null));
    }

    private void setUpDismissBehaviorOf(DownloadTaskEditor downloadTaskEditor) {
        downloadTaskEditor.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showingDownloadUrl = "";
                webEngine.searchScreen.getMainScreen().showFullscreenAd(true);
            }
        });
    }

}
