package main.dialog_factory;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import net.fdm.R;

import libs.async_job.AsyncJob;
import main.utilities.UiUtils;

/**
 * This class very useful if you want to show a progressDialog
 * that matching the application theme, then use this class.
 *
 * @author shibaprasad
 * @version 1.2
 */
public final class ProgressDialog {

    protected TextView progressMessage;
    private Dialog dialog;

    public ProgressDialog(Context context, boolean cancelable, String progressText) {
        dialog = UiUtils.generateNewDialog(context, R.layout.progress_layout_dialog);

        dialog.setCancelable(cancelable);

        progressMessage = (TextView) dialog.findViewById(R.id.title);
        progressMessage.setText(progressText);
    }

    public void showInMainThread() {
        AsyncJob.doInMainThread(new AsyncJob.MainThreadJob() {
            @Override
            public void doInUIThread() {
                show();
            }
        });
    }

    public void show() {
        try {
            this.dialog.show();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void closeInMainThread() {
        try {
            close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void close() {
        this.dialog.dismiss();
    }

    public Dialog getDialog() {
        return this.dialog;
    }

    public TextView getProgressMessageView() {
        return this.progressMessage;
    }

    public void setProgressMessage(String message) {
        progressMessage.setText(message);
    }
}
