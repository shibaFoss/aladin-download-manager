package main.screens.popup_screen;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;
import main.download_manager.DownloadTaskEditor;
import main.key_database.KeyStore;
import main.screens.BaseScreen;

public final class DownloadPopupScreen extends BaseScreen {

    private String fileName;
    private String fileUrl;
    private DownloadTaskEditor downloadTaskEditor;

    @Override
    public int getLayout() {
        return -1;
    }

    @Override
    public void onLayoutLoad() {
        setUpWindowConfiguration();
        initDownloadManagerDialog();
    }

    @Override
    public void onAfterLayoutLoad() {
        getFileUrl_FileName();
        if (downloadTaskEditor == null) initDownloadManagerDialog();

        downloadTaskEditor.setFileUrl(fileUrl);
        downloadTaskEditor.setFileName(fileName);
        downloadTaskEditor.show();
    }

    @Override
    public void onPauseScreen() {

    }

    @Override
    public void onResumeScreen() {

    }

    @Override
    public void onExitScreen() {
        downloadTaskEditor.close();
        finish();
    }

    @Override
    public void onClearMemory() {

    }

    @Override
    public void onScreenOptionChange(Configuration configuration) {

    }

    private void initDownloadManagerDialog() {
        downloadTaskEditor = new DownloadTaskEditor(this);
        downloadTaskEditor.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
    }

    private void setUpWindowConfiguration() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
    }

    private void getFileUrl_FileName() {
        Intent intent = getIntent();
        fileName = intent.getStringExtra(KeyStore.FILE_NAME) + "";
        fileUrl = intent.getStringExtra(KeyStore.FILE_URL) + "";
    }

}
