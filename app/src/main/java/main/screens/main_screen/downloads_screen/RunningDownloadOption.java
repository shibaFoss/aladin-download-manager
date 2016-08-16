package main.screens.main_screen.downloads_screen;

import android.app.Dialog;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import main.dialog_factory.YesNoDialog;
import main.download_manager.DownloadModel;
import main.download_manager.DownloadTaskEditor;
import main.download_manager.DownloadTaskStarter;
import main.screens.main_screen.MainScreen;
import main.utilities.UiUtils;
import net.fdm.R;

public class RunningDownloadOption implements View.OnClickListener {

    private MainScreen mainScreen;
    private DownloadModel downloadModel;

    private Dialog dialog;
    private TextView pauseBnt;
    private TextView resumeBnt;
    private TextView restartBnt;
    private TextView removeBnt;
    private TextView deleteBnt;
    private TextView moreOptionBnt;

    public RunningDownloadOption(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.init();
    }

    public void showOptionsFor(DownloadModel downloadModel) {
        this.downloadModel = downloadModel;
        if (dialog != null) {
            dialog.show();
        }
    }

    public void closeOptions() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == pauseBnt.getId()) pause(downloadModel);
        else if (view.getId() == resumeBnt.getId()) resume(downloadModel);
        else if (view.getId() == restartBnt.getId()) restart(downloadModel);
        else if (view.getId() == removeBnt.getId()) remove(downloadModel);
        else if (view.getId() == deleteBnt.getId()) delete(downloadModel);
        else if (view.getId() == moreOptionBnt.getId()) moreOptions(downloadModel);
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(mainScreen, R.layout.running_download_option_dialog);

        pauseBnt = (TextView) dialog.findViewById(R.id.pause_bnt);
        resumeBnt = (TextView) dialog.findViewById(R.id.resume_bnt);
        restartBnt = (TextView) dialog.findViewById(R.id.restart_bnt);
        removeBnt = (TextView) dialog.findViewById(R.id.remove_bnt);
        deleteBnt = (TextView) dialog.findViewById(R.id.delete_bnt);
        moreOptionBnt = (TextView) dialog.findViewById(R.id.more_setting_bnt);

        initClickEventsOf(new View[]{pauseBnt, resumeBnt, restartBnt, removeBnt, deleteBnt, moreOptionBnt});
    }

    private void initClickEventsOf(View[] views) {
        for (View view : views)
            view.setOnClickListener(this);
    }

    private void pause(DownloadModel downloadModel) {
        closeOptions();
        DownloadTaskStarter.pauseTask(mainScreen, downloadModel);
    }

    private void resume(DownloadModel downloadModel) {
        closeOptions();
        DownloadTaskStarter.addOrResumeTask(mainScreen, downloadModel);
    }

    private void restart(final DownloadModel downloadModel) {
        closeOptions();
        final YesNoDialog yesNoDialog = new YesNoDialog(mainScreen);
        yesNoDialog.clickListener = new YesNoDialog.ClickListener() {
            @Override
            public void onYes(TextView view) {
                yesNoDialog.close();
                DownloadTaskEditor downloadTaskEditor = new DownloadTaskEditor(mainScreen);
                downloadTaskEditor.setFileName(downloadModel.fileName);
                downloadTaskEditor.setFilePath(downloadModel.filePath);
                downloadTaskEditor.setFileUrl(downloadModel.fileUrl);

                DownloadTaskStarter.deleteTask(mainScreen, downloadModel);
                downloadTaskEditor.show();
            }

            @Override
            public void onNo(TextView view) {
                yesNoDialog.close();
            }
        };
        yesNoDialog.setMessage("Do you want to restart the download?");
        yesNoDialog.yesButton.setText("Yes");
        yesNoDialog.noButton.setText("No");
        yesNoDialog.show();
    }

    private void remove(final DownloadModel downloadModel) {
        closeOptions();
        final YesNoDialog yesNoDialog = new YesNoDialog(mainScreen);
        yesNoDialog.clickListener = new YesNoDialog.ClickListener() {
            @Override
            public void onYes(TextView view) {
                yesNoDialog.close();
                DownloadTaskStarter.removeTask(mainScreen, downloadModel);
            }

            @Override
            public void onNo(TextView view) {
                yesNoDialog.close();
            }
        };
        yesNoDialog.setMessage("Do you want to remove the download from list?");
        yesNoDialog.yesButton.setText("Yes");
        yesNoDialog.noButton.setText("No");
        yesNoDialog.show();
    }

    private void delete(final DownloadModel downloadModel) {
        closeOptions();
        final YesNoDialog yesNoDialog = new YesNoDialog(mainScreen);
        yesNoDialog.clickListener = new YesNoDialog.ClickListener() {
            @Override
            public void onYes(TextView view) {
                yesNoDialog.close();
                DownloadTaskStarter.deleteTask(mainScreen, downloadModel);
            }

            @Override
            public void onNo(TextView view) {
                yesNoDialog.close();
            }
        };
        yesNoDialog.setMessage(Html.fromHtml("Do you want delete the download file? "));
        yesNoDialog.yesButton.setText("Yes");
        yesNoDialog.noButton.setText("No");
        yesNoDialog.show();
    }

    private void moreOptions(final DownloadModel downloadModel) {
        new MoreRunningDownloadOptions(mainScreen).showOptionsFor(downloadModel);
        closeOptions();
    }

}
