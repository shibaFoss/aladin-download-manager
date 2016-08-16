package main.screens.main_screen.downloads_screen;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import main.dialog_factory.YesNoDialog;
import main.dialog_factory.YesNoDialog.OnYesClick;
import main.download_manager.DownloadModel;
import main.download_manager.DownloadTaskStarter;
import main.screens.main_screen.MainScreen;
import main.utilities.UiUtils;
import net.fdm.R;

import java.io.File;

import static main.utilities.DeviceTool.getMimeType;

public class CompleteDownloadOptions implements View.OnClickListener {

    private CompleteDownloadScreen completeDownloadScreen;
    private MainScreen mainScreen;
    private Dialog dialog;

    private TextView openButton;
    private TextView lockButton;
    private TextView shareWithWorld;
    private TextView shareFile;
    private TextView renameButton;
    private TextView removeButton;
    private TextView deleteButton;
    private TextView detailButton;

    private DownloadModel downloadModel;

    public CompleteDownloadOptions(CompleteDownloadScreen completeDownloadScreen) {
        this.completeDownloadScreen = completeDownloadScreen;
        this.mainScreen = completeDownloadScreen.getMainScreen();
        this.init();
    }

    public void show(DownloadModel downloadModel) {
        this.downloadModel = downloadModel;
        if (dialog != null) {
            updateLockButtonText(downloadModel);
            dialog.show();
        }
    }


    public void close() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == openButton.getId()) {
            openClick(downloadModel);
        } else if (view.getId() == lockButton.getId()) {
            lockFile(downloadModel);
        } else if (view.getId() == shareWithWorld.getId()) {
            shareWithWorldClick(downloadModel);
        } else if (view.getId() == shareFile.getId()) {
            shareFileClick(downloadModel);
        } else if (view.getId() == renameButton.getId()) {
            renameClick(downloadModel);
        } else if (view.getId() == removeButton.getId()) {
            removeClick(downloadModel);
        } else if (view.getId() == deleteButton.getId()) {
            deleteClick(downloadModel);
        } else if (view.getId() == detailButton.getId()) {
            detailClick(downloadModel);
        }
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(mainScreen, R.layout.complete_download_option_dialog);

        openButton = (TextView) dialog.findViewById(R.id.open_bnt);
        lockButton = (TextView) dialog.findViewById(R.id.lock_bnt);
        shareWithWorld = (TextView) dialog.findViewById(R.id.share_with_world_bnt);
        shareFile = (TextView) dialog.findViewById(R.id.share_file_bnt);
        renameButton = (TextView) dialog.findViewById(R.id.rename_bnt);
        removeButton = (TextView) dialog.findViewById(R.id.remove_bnt);
        deleteButton = (TextView) dialog.findViewById(R.id.delete_bnt);
        detailButton = (TextView) dialog.findViewById(R.id.download_property_bnt);

        initializeClickEvent(new View[]{openButton, lockButton, shareWithWorld, shareFile, renameButton, removeButton,
                deleteButton, detailButton});
    }

    private void updateLockButtonText(DownloadModel downloadModel) {
        if (downloadModel.isLock) {
            lockButton.setText("Unlock");
        } else {
            lockButton.setText("Lock");
        }
    }

    private void openClick(DownloadModel downloadModel) {
        if (downloadModel.isLock) {
            close();
            FileUnlock fileUnlock = new FileUnlock(mainScreen, downloadModel);
            fileUnlock.setCompeteDownloadListAdapter(completeDownloadScreen.completeDownloadListAdapter);
            fileUnlock.showToOpenFile(this);
            fileUnlock.show();
        } else {
            openFile(downloadModel);
        }
    }

    public void openFile(DownloadModel downloadModel) {
        close();
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

    private void lockFile(DownloadModel downloadModel) {
        if (downloadModel.isLock) {
            close();
            FileUnlock fileUnlock = new FileUnlock(mainScreen, downloadModel);
            fileUnlock.setCompeteDownloadListAdapter(completeDownloadScreen.completeDownloadListAdapter);
            fileUnlock.show();
        } else {
            close();
            FileLocker fileLocker = new FileLocker(mainScreen, downloadModel);
            fileLocker.setCompeteDownloadListAdapter(completeDownloadScreen.completeDownloadListAdapter);
            fileLocker.show();
        }
    }

    private void shareWithWorldClick(DownloadModel downloadModel) {

    }

    private void shareFileClick(DownloadModel downloadModel) {
        File file = new File(downloadModel.filePath, downloadModel.fileName);

        String mimeType = getMimeType(Uri.fromFile(file).toString());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setDataAndType(Uri.fromFile(file), mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        BaseIntentChooser fileShareChooser = new BaseIntentChooser(mainScreen) {
            @Override
            public void onStartActivity(Intent intent, String packageName) {
                //noting to do.
            }
        };
        fileShareChooser.setIntent(intent);
        fileShareChooser.show();
    }

    private void renameClick(final DownloadModel downloadModel) {
        close();
        RenameDownloadFile renameDownloadFile = new RenameDownloadFile(mainScreen, downloadModel);
        renameDownloadFile.show();
    }

    private void removeClick(final DownloadModel downloadModel) {
        close();
        DownloadTaskStarter.removeCompleteTask(mainScreen, downloadModel);
    }

    private void deleteClick(final DownloadModel downloadModel) {
        close();
        YesNoDialog yesNoDialog = YesNoDialog.getYesNoDialog(mainScreen, new OnYesClick() {
            @Override
            public void onYesClick(YesNoDialog dialog) {
                DownloadTaskStarter.deleteCompleteTask(mainScreen, downloadModel);
            }
        });
        yesNoDialog.setMessage("Delete the file from SD Card?");
        yesNoDialog.yesButton.setText("Yes");
        yesNoDialog.noButton.setText("No");
        yesNoDialog.show();
    }

    private void detailClick(final DownloadModel downloadModel) {
        close();
        DownloadDetailViewer downloadDetailViewer = new DownloadDetailViewer(mainScreen, downloadModel);
        downloadDetailViewer.show();
    }

    private void initializeClickEvent(View[] views) {
        for (View view : views)
            view.setOnClickListener(this);
    }

}
