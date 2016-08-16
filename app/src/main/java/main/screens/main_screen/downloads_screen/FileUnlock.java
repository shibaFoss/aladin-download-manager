package main.screens.main_screen.downloads_screen;

import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import main.download_manager.DownloadModel;
import main.screens.BaseScreen;
import main.utilities.UiUtils;
import net.fdm.R;

/**
 * This class unlock downloadModels and open file by password.
 */
public class FileUnlock {

    private BaseScreen baseScreen;
    private DownloadModel downloadModel;
    private CompleteDownloadListAdapter competeDownloadListAdapter;
    private CompleteDownloadOptions downloadOptions;
    private Dialog dialog;
    private TextView title, cancel, unlock;
    private EditText  password;
    private boolean isIntendedForOpen;

    public FileUnlock(BaseScreen baseScreen, DownloadModel downloadModel) {
        this.baseScreen = baseScreen;
        this.downloadModel = downloadModel;
        init();
    }

    public void show() {
        if (dialog != null) dialog.show();
    }

    public void close() {
        if (dialog != null) dialog.dismiss();
    }

    public void setCompeteDownloadListAdapter(CompleteDownloadListAdapter competeDownloadListAdapter) {
        this.competeDownloadListAdapter = competeDownloadListAdapter;
    }

    public void showToOpenFile(CompleteDownloadOptions completeDownloadOptions) {
        this.downloadOptions = completeDownloadOptions;
        isIntendedForOpen = true;
        title.setText("Open File");
        cancel.setText("Cancel");
        unlock.setText("Open");
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.file_unlock_dialog);

        title = (TextView) dialog.findViewById(R.id.title);
        password = (EditText) dialog.findViewById(R.id.fileNameEdit);
        cancel = (TextView) dialog.findViewById(R.id.cancelButton);
        unlock = (TextView) dialog.findViewById(R.id.okButton);

        password.setHint("Password");

        title.setText("Unlock File");
        cancel.setText("Cancel");
        unlock.setText("Unlock");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int enteredPassword = Integer.parseInt(password.getText().toString());
                    if (enteredPassword == downloadModel.lockPassword) {
                        if (isIntendedForOpen) {
                            downloadOptions.openFile(downloadModel);
                            close();
                        } else  {
                            downloadModel.lockPassword = -1;
                            downloadModel.isLock = false;
                            downloadModel.changeToCompleteModel();
                            close();
                            competeDownloadListAdapter.notifyDataChange();
                        }
                    } else  {
                        baseScreen.vibrator.vibrate(20);
                        baseScreen.showSimpleMessageBox("Password does not match.");
                    }
                } catch (NumberFormatException error) {
                    error.printStackTrace();
                    close();
                    baseScreen.vibrator.vibrate(20);
                    baseScreen.showSimpleMessageBox("Something went wrong. Please try again later.");
                }
            }
        });
    }
}
