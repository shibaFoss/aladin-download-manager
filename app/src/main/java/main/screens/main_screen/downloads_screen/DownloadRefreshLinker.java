package main.screens.main_screen.downloads_screen;

import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import async_job.AsyncJob;
import main.dialog_factory.ProgressDialog;
import main.download_manager.DownloadModel;
import main.screens.BaseScreen;
import main.utilities.NetworkUtils;
import main.utilities.UiUtils;
import net.fdm.R;

import java.net.MalformedURLException;
import java.net.URL;

import static async_job.AsyncJob.BackgroundJob;
import static async_job.AsyncJob.MainThreadJob;

/**
 * DownloadRefresherLinker lets user to input the new download url and save it with
 * the new one. After save this class return the new download url to its implementor
 * class.
 *
 * @author shibaprasad
 * @version 1.3
 */
public class DownloadRefreshLinker implements View.OnClickListener {

    private BaseScreen baseScreen;
    private ProgressDialog progressDialog;

    private Dialog dialog;
    private EditText linkInputField;
    private TextView cancelButton;
    private TextView saveButton;

    private OnSaveListener listener;
    private DownloadModel downloadModel;


    public DownloadRefreshLinker(BaseScreen baseScreen) {
        this.baseScreen = baseScreen;
        this.init();
    }


    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }


    public void close() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    public void registerOnSaveListener(OnSaveListener onSaveListener) {
        this.listener = onSaveListener;
    }


    /**
     * Register the associated download model of the download task.
     */
    public void setDownloadModel(DownloadModel downloadModel) {
        this.downloadModel = downloadModel;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == cancelButton.getId()) close();
        else if (view.getId() == saveButton.getId()) onSaveClick();
    }


    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.download_refresh_link_dialog);

        linkInputField = (EditText) dialog.findViewById(R.id.url);
        cancelButton = (TextView) dialog.findViewById(R.id.cancel_bnt);
        saveButton = (TextView) dialog.findViewById(R.id.replace);

        cancelButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

    }


    /**
     * The save method that is executed after user click the saveButton.
     */
    private void onSaveClick() {
        if (listener != null) {
            progressDialog = new ProgressDialog(baseScreen, false, "Validating ...");
            BackgroundJob backgroundJob = new BackgroundJob() {
                @Override
                public void doInBackground() {
                    progressDialog.showInMainThread();

                    if (validateUrl()) {
                        if (validateFileLength())
                            sentSaveCallback(); //everything is perfect, sent back the save callback.
                        else
                            showErrorMessage(true, baseScreen.getString(R.string.file_size_not_matching_error));
                    } else {
                        showErrorMessage(true, baseScreen.getString(R.string.url_unsupported_protocol_error));
                    }
                }
            };

            AsyncJob.doInBackground(backgroundJob);
        }
    }


    private void sentSaveCallback() {
        AsyncJob.doInMainThread(new MainThreadJob() {
            @Override
            public void doInUIThread() {
                if (progressDialog != null) {
                    progressDialog.close();
                }
                close();
                listener.onSave(DownloadRefreshLinker.this, getUrl());
                baseScreen.showSimpleMessageBox(baseScreen.getString(R.string.congrat_message_link_saving));
            }
        });
    }


    private void showErrorMessage(final boolean closeProgress, final String message) {
        AsyncJob.doInMainThread(new MainThreadJob() {
            @Override
            public void doInUIThread() {
                try {
                    if (closeProgress) {
                        progressDialog.close();
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                }
                baseScreen.vibrator.vibrate(20);
                baseScreen.showSimpleMessageBox(message);
            }
        });
    }


    /**
     * Check if the new download url returns the same file size as the existing task or not.
     *
     * @return the boolean value that indicate either the new file size of the enter url
     * is same as old or not.
     */
    private boolean validateFileLength() {
        if (downloadModel != null) {
            try {
                long newFileLength = NetworkUtils.getFileSize(new URL(getUrl()));
                long oldFileLength = downloadModel.totalFileLength;
                return newFileLength == oldFileLength;
            } catch (Exception error) {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * Check if the user provided or entered url is valid or not.
     *
     * @return the boolean value that indicates the url's validity.
     */
    private boolean validateUrl() {
        try {
            new URL(getUrl()); //If the url is not valid then, it will produce a exception.
            return !(!getUrl().startsWith("http://") && !getUrl().trim().startsWith("https://"));
        } catch (MalformedURLException error) {
            error.printStackTrace();
            return false;
        }
    }


    /**
     * Get user entered new download url.
     *
     * @return the new download url.
     */
    private String getUrl() {
        return linkInputField.getText().toString().trim();
    }


    /**
     * Interface that give a call back event to the implementor class after save the new url.
     */
    public interface OnSaveListener {

        void onSave(DownloadRefreshLinker downloadRefreshLinker, String refreshedLink);
    }
}
