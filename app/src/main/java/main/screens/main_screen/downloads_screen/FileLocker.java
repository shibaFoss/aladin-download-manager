package main.screens.main_screen.downloads_screen;

import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import main.download_manager.DownloadModel;
import main.screens.BaseScreen;
import main.utilities.UiUtils;
import net.fdm.R;

public class FileLocker {

    private BaseScreen baseScreen;
    private DownloadModel downloadModel;
    private CompleteDownloadListAdapter competeDownloadListAdapter;

    private Dialog dialog;

    public FileLocker(BaseScreen baseScreen, DownloadModel downloadModel) {
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

    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.new_password_dialog);

        TextView title, cancel, lock;
        final EditText password, repeatPassword;

        title = (TextView) dialog.findViewById(R.id.title);
        password = (EditText) dialog.findViewById(R.id.password);
        repeatPassword = (EditText) dialog.findViewById(R.id.repeat_password);

        cancel = (TextView) dialog.findViewById(R.id.cancel_bnt);
        lock = (TextView) dialog.findViewById(R.id.lock_bnt);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String passwordText = password.getText().toString();
                    String repeatPasswordText = repeatPassword.getText().toString();

                    if (passwordText.toLowerCase().equals(repeatPasswordText.toLowerCase())) {
                        downloadModel.lockPassword = Integer.parseInt(passwordText);
                        downloadModel.isLock = true;
                        downloadModel.changeToCompleteModel();
                        competeDownloadListAdapter.notifyDataChange();
                        close();
                    } else {
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
