package main.download_manager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.webkit.URLUtil;
import main.app.App;
import main.settings.UserSettings;
import main.utilities.FileCatalog;
import main.utilities.TextUtility;

/**
 * ClipboardMonitor monitors the clipboardManager's activity.
 * It sends a callback method when user copied some url text.
 *
 * @author shibaprasad
 */
public final class ClipboardMonitor {
    private ClipboardManager clipboardManager;
    private Context context;
    private App app;
    private OnCopyListener onCopyListener;

    public ClipboardMonitor(Context context, App app) {
        this.context = context;
        this.app = app;
        this.init();
    }

    public void setOnCopyListener(OnCopyListener onCopyListener) {
        this.onCopyListener = onCopyListener;
    }

    private void init() {
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                try {
                    UserSettings userSettings = app.getUserSettings();
                    if (userSettings != null) {
                        if (!userSettings.isClipboardMonitoring()) return;
                    }


                    ClipData clipData = clipboardManager.getPrimaryClip();
                    if (clipData == null) return;

                    String copiedText = String.valueOf(clipData.getItemAt(0).getText());
                    String[] extractedLinks = TextUtility.extractLinks(copiedText);
                    for (String link : extractedLinks) {
                        if (link.startsWith("http")) {
                            for (String wantedUrl : WantedDownloadUrls.wantedDownloadUrls) {
                                if (link.startsWith(wantedUrl)) {
                                    //we have found a great file link to download.
                                    if (onCopyListener != null) {
                                        onCopyListener.onCopyUrl("432232.mp4", link);
                                    }
                                    return;
                                }
                            }

                            for (String format : FileCatalog.ALl_DOWNLOADABLE_FORMAT) {
                                String guessFileName = URLUtil.guessFileName(link, null, null);
                                if (guessFileName.toLowerCase().endsWith(format)) {
                                    //we have found a great file link to download.
                                    if (onCopyListener != null) {
                                        onCopyListener.onCopyUrl(guessFileName, link);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });
    }

    public interface OnCopyListener {
        void onCopyUrl(String name, String url);
    }
}
