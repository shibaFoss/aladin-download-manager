package main.screens.main_screen.downloads_screen;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import main.dialog_factory.YesNoDialog;
import main.download_manager.DownloadModel;
import main.download_manager.DownloadSystem;
import main.download_manager.DownloadTaskEditor;
import main.screens.main_screen.MainScreen;
import main.utilities.UiUtils;
import net.fdm.R;

import java.io.File;

import static main.utilities.DeviceTool.getMimeType;

public class MoreRunningDownloadOptions implements View.OnClickListener {

    private MainScreen mainScreen;
    private DownloadModel downloadModel;

    private Dialog dialog;
    private TextView openFileBnt;
    private TextView replaceLinkBnt;
    private TextView forceAssembleBnt;
    private TextView propertyBnt;


    public MoreRunningDownloadOptions(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.init();
    }

    public void showOptionsFor(DownloadModel downloadModel) {
        this.downloadModel = downloadModel;
        if (dialog != null) dialog.show();
    }

    public void closeOptions() {
        if (dialog != null) dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(mainScreen, R.layout.running_download_more_option_dialog);

        openFileBnt = (TextView) dialog.findViewById(R.id.open_file_bnt);
        replaceLinkBnt = (TextView) dialog.findViewById(R.id.replace_link_bnt);
        forceAssembleBnt = (TextView) dialog.findViewById(R.id.force_assemble_bnt);
        propertyBnt = (TextView) dialog.findViewById(R.id.download_property_bnt);

        openFileBnt.setOnClickListener(this);
        replaceLinkBnt.setOnClickListener(this);
        forceAssembleBnt.setOnClickListener(this);
        propertyBnt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == openFileBnt.getId()) openFile(downloadModel);
        else if (view.getId() == replaceLinkBnt.getId()) replaceDownlaodLink(downloadModel);
        else if (view.getId() == forceAssembleBnt.getId()) forceAssemble(downloadModel);
        else if (view.getId() == propertyBnt.getId()) propertyDetail(downloadModel);
    }

    private void openFile(DownloadModel downloadModel) {
        closeOptions();
        File file = new File(downloadModel.filePath, downloadModel.fileName);
        String mimeType = getMimeType(Uri.fromFile(file).toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), mimeType);

        BaseIntentChooser mediaOpenChooser = new BaseIntentChooser(mainScreen) {
            @Override
            public void onStartActivity(Intent intent, String packageName) {
                //noting to do.
            }
        };
        mediaOpenChooser.setIntent(intent);
        mediaOpenChooser.show();
    }

    private void replaceDownlaodLink(final DownloadModel downloadModel) {
        closeOptions();
        DownloadSystem downloadSystem = mainScreen.app.getDownloadSystem();
        if (!downloadSystem.matchesRunningTaskWith(downloadModel)) {
            DownloadRefreshLinker downloadRefreshLinker = new DownloadRefreshLinker(mainScreen);
            downloadRefreshLinker.registerOnSaveListener(new DownloadRefreshLinker.OnSaveListener() {

                @Override
                public void onSave(DownloadRefreshLinker downloadRefreshLinker, String refreshedLink) {
                    downloadRefreshLinker.close();
                    downloadModel.fileUrl = refreshedLink;
                    downloadModel.updateDataInDisk();
                }
            });

            downloadRefreshLinker.setDownloadModel(downloadModel);
            downloadRefreshLinker.show();
        } else {
            mainScreen.vibrator.vibrate(20);
            mainScreen.showSimpleMessageBox(mainScreen.getString(R.string.feature_can_not_run_on_running_task));
        }
    }

    private void forceAssemble(final DownloadModel downloadModel) {
        closeOptions();
        DownloadSystem system = mainScreen.app.getDownloadSystem();
        if (system.matchesRunningTaskWith(downloadModel)) {
            mainScreen.vibrator.vibrate(20);
            mainScreen.showSimpleMessageBox(mainScreen.getString(R.string.feature_can_not_run_on_running_task));
            return;
        }

        final YesNoDialog confirmDialog = new YesNoDialog(mainScreen);
        confirmDialog.clickListener = new YesNoDialog.ClickListener() {
            @Override
            public void onYes(TextView view) {
                confirmDialog.close();
                DownloadTaskEditor downloadTaskEditor = new DownloadTaskEditor(mainScreen);
                downloadTaskEditor.prepareForceAssemble(downloadModel);
                downloadTaskEditor.show();
            }

            @Override
            public void onNo(TextView view) {
                confirmDialog.close();
            }
        };

        confirmDialog.setMessage("Force assemble can lead to unexpected error or download failure." +
                " Do you still want to continue ?");
        confirmDialog.yesButton.setText("Yes");
        confirmDialog.noButton.setText("No");
        confirmDialog.show();
    }

    private void propertyDetail(DownloadModel downloadModel) {
        closeOptions();
        DownloadDetailViewer downloadDetailViewer = new DownloadDetailViewer(mainScreen, downloadModel);
        downloadDetailViewer.show();
    }
}
