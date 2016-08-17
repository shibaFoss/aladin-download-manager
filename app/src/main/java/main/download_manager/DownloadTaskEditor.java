package main.download_manager;

import android.app.Dialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.fdm.R;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import libs.async_job.AsyncJob;
import libs.async_job.AsyncJob.BackgroundJob;
import libs.async_job.AsyncJob.MainThreadJob;
import main.app.App;
import main.dialog_factory.FilePickerDialog;
import main.dialog_factory.MessageBox;
import main.dialog_factory.ProgressDialog;
import main.screens.BaseScreen;
import main.screens.main_screen.downloads_screen.DownloadRefreshLinker;
import main.user_tracking.UserTracker;
import main.utilities.NetworkUtils;
import main.utilities.UiUtils;

import static libs.async_job.AsyncJob.doInBackground;
import static libs.async_job.AsyncJob.doInMainThread;
import static main.utilities.DeviceTool.humanReadableSizeOf;
import static main.utilities.DeviceTool.move;
import static main.utilities.FileCatalog.getSubDirectoryBy;
import static main.utilities.NetworkUtils.getOriginalRedirectedUrl;

/**
 * DownloadTaskEditor represents a dialog , from where user configures the entire download model and send
 * the Model to the {@link DownloadSystem}. It also opens two child dialogs {@link FilePickerDialog} and
 * {@link DownloadTaskAdvanceEditor} respectively to configure a DownloadModel
 * After configure it send the DownloadModel to the DownloadSystem to start a DownloadTask
 * with the DownloadModel.
 *
 * @author shibaprasad
 * @version 1.8
 */
public final class DownloadTaskEditor implements View.OnClickListener {

    /**
     * The variable is used to identity id the download file size
     * has been calculated from the server or not.
     */
    public static final int SIZE_NOT_MEASURED = -4;

    /**
     * The variable is used to identify if the the program is currently
     * busy at calculating the file size or not.
     */
    public boolean isWaitingForFileSize = false;

    /**
     * The variable is used to identify if the class has been requested
     * for doing a force assemble on the downlaodModel.
     */
    private boolean isForceAssemble = false;

    private BaseScreen baseScreen;
    private App app;

    private Dialog dialog;
    private TextView fileSizePreview, downloadBnt, cancelBnt;
    private EditText fileUrlEdit, fileNameEdit;
    private ImageView pathSelectorBnt, advanceSettingBnt;

    private DownloadModel downloadModel;

    /**
     * This are variable that is used when a force assemble operation
     * is requested.
     */
    private String oldFileName, oldFilePath;


    /**
     * Public constructor.
     *
     * @param baseScreen the base_screen
     */
    public DownloadTaskEditor(BaseScreen baseScreen) {
        this.baseScreen = baseScreen;
        this.app = baseScreen.app;
        this.downloadModel = generateDefaultDownloadModel();
        this.init();
    }

    /**
     * Generate a default download model object with it's default value.
     */
    private DownloadModel generateDefaultDownloadModel() {
        DownloadModel model = new DownloadModel();
        model.filePath = baseScreen.app.getUserSettings().getDownloadPath();
        model.totalFileLength = SIZE_NOT_MEASURED;
        model.uiProgressInterval = 1000 * baseScreen.app.getUserSettings().getProgressTimer();
        model.bufferSize = baseScreen.app.getUserSettings().getBufferSize();
        model.numberOfPart = baseScreen.app.getUserSettings().getDownloadPart();
        model.isCatalogEnable = baseScreen.app.getUserSettings().isFileCatalog();
        model.isAutoResumeEnable = baseScreen.app.getUserSettings().isAutoResume();
        model.isTtsNotificationEnable = baseScreen.app.getUserSettings().isTtsNotification();
        model.isSmartDownload = baseScreen.app.getUserSettings().isSmartDownload();
        return model;
    }

    /**
     * Initialize the entire class after being open for the first time.
     */
    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.download_task_editor_dialog);
        dialog.setCancelable(false);

        fileSizePreview = (TextView) dialog.findViewById(R.id.name_file_size);
        fileUrlEdit = (EditText) dialog.findViewById(R.id.url);
        fileNameEdit = (EditText) dialog.findViewById(R.id.name);
        downloadBnt = (TextView) dialog.findViewById(R.id.download);
        cancelBnt = (TextView) dialog.findViewById(R.id.cancel_bnt);
        pathSelectorBnt = (ImageView) dialog.findViewById(R.id.file_path);
        advanceSettingBnt = (ImageView) dialog.findViewById(R.id.advance_setting);

        setUrlTextChangeListener();

        pathSelectorBnt.setOnClickListener(this);
        advanceSettingBnt.setOnClickListener(this);
        downloadBnt.setOnClickListener(this);
        cancelBnt.setOnClickListener(this);
    }

    private void setUrlTextChangeListener() {
        fileUrlEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateFileSize();
            }
        });
    }

    /**
     * Update the file size of a valid url.
     */
    private void updateFileSize() {
        if (isForceAssemble) return;

        doInBackground(new BackgroundJob() {
            @Override
            public void doInBackground() {
                isWaitingForFileSize = true;

                try {
                    if (app.getUserSettings().isUrlFilter()) {
                        setFileSizeText("Filtering file url...");
                        final String newRedirectedUrl = getOriginalRedirectedUrl(new URL(getFileUrl()));
                        if (!newRedirectedUrl.equals("-1")) {
                            AsyncJob.doInMainThread(new MainThreadJob() {
                                @Override
                                public void doInUIThread() {
                                    changeFileUrl(newRedirectedUrl);
                                }
                            });
                        }
                    }
                    isWaitingForFileSize = false;
                } catch (IOException error) {
                    error.printStackTrace();
                    isWaitingForFileSize = false;
                }

                isWaitingForFileSize = true;
                setFileSizeText("Waiting for file size...");

                try {
                    downloadModel.totalFileLength = NetworkUtils.getFileSize(new URL(getFileUrl()));
                    String fileSizeInMb = humanReadableSizeOf(downloadModel.totalFileLength);
                    if (fileSizeInMb.equals("-1B")) {
                        fileSizeInMb = "Unknown File Size";
                    }

                    setFileSizeText(fileSizeInMb);

                } catch (MalformedURLException error) {
                    error.printStackTrace();
                    setFileSizeText("Can't connect to the server");
                }
                isWaitingForFileSize = false;
            }
        });
    }

    /**
     * Set the file size at the fileSizePreview field from the mainThread.
     *
     * @param text the text that to be shown on the fileSizePreview.
     */
    private void setFileSizeText(final String text) {
        doInMainThread(new MainThreadJob() {
            @Override
            public void doInUIThread() {
                fileSizePreview.setText(Html.fromHtml("File Size : " + "(" + "<b>" + text + "</b>" + ")"));
            }
        });
    }

    public String getFileUrl() {
        return fileUrlEdit.getText().toString().trim();
    }

    public void setFileUrl(String url) {
        changeFileUrl(url);
        updateFileSize();
    }

    public void changeFileUrl(String url) {
        fileUrlEdit.setText(url.trim());
    }

    public String getFileName() {
        return fileNameEdit.getText().toString().trim();
    }

    public void setFileName(String name) {
        fileNameEdit.setText(name.trim());
    }

    public String getFilePath() {
        return downloadModel.filePath;
    }

    public void setFilePath(String filePath) {
        downloadModel.filePath = filePath.trim();
    }

    public Dialog getDialog() {
        return dialog;
    }

    /**
     * Show the downlaod task editor dialog.
     */
    public void show() {
        try {
            UserTracker userTracker = app.getUserTracker();
            if (userTracker.getVisibleDownloadLink() != null) {
                if (!userTracker.getVisibleDownloadLink().equals(getFileUrl())) {
                    if (dialog != null) {
                        dialog.show();
                        userTracker.setVisibleDownloadLink(getFileUrl());
                    }
                }
            } else {
                if (dialog != null) {
                    dialog.show();
                    userTracker.setVisibleDownloadLink(getFileUrl());
                }
            }
            baseScreen.loadNewFullScreenAd();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    /**
     * Close the download task editor dialog.
     */
    public void close() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                UserTracker userTracker = app.getUserTracker();
                userTracker.setVisibleDownloadLink(null);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    /**
     * System call this method if user clicks any buttons.
     *
     * @param view the view which has been clicked.
     */
    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == pathSelectorBnt.getId()) openPathSelector();
        else if (id == advanceSettingBnt.getId()) openAdvanceSetting();
        else if (id == downloadBnt.getId()) downloadClick();
        else if (id == cancelBnt.getId()) close();
    }

    public void prepareForceAssemble(final DownloadModel downloadModel) {
        this.isForceAssemble = true;
        this.oldFileName = downloadModel.fileName;
        this.oldFilePath = downloadModel.filePath;
        this.downloadModel = downloadModel;
        this.downloadBnt.setText("Save");

        TextView title = (TextView) this.dialog.findViewById(R.id.title);
        title.setText("Force Assemble");

        setFileName(downloadModel.fileName);
        setFileUrl(downloadModel.fileUrl);

        fileUrlEdit.setEnabled(true);
        fileUrlEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReplaceLinkDialog(downloadModel);
            }
        });

        setFileSizeText(humanReadableSizeOf(downloadModel.totalFileLength));
        setFilePath(downloadModel.filePath);
    }

    /**
     * The function is used for replacing a old downloaded file link with a new one.
     */
    private void openReplaceLinkDialog(final DownloadModel downloadModel) {
        DownloadRefreshLinker linkChanger = new DownloadRefreshLinker(baseScreen);
        linkChanger.registerOnSaveListener(new DownloadRefreshLinker.OnSaveListener() {
            @Override
            public void onSave(DownloadRefreshLinker linkChanger, String refreshedLink) {
                linkChanger.close();
                setFileUrl(refreshedLink);
                downloadModel.fileUrl = refreshedLink;
                downloadModel.updateDataInDisk();
            }
        });

        linkChanger.setDownloadModel(downloadModel);
        linkChanger.show();
    }

    private void openPathSelector() {
        FilePickerDialog filePickerDialog = new FilePickerDialog(baseScreen, getFilePath());
        filePickerDialog.onPathSelectListener = new FilePickerDialog.OnSelectListener() {
            @Override
            public void onSelect(FilePickerDialog pickerDialog, String selectedPath) {
                pickerDialog.close();
                setFilePath(selectedPath);
            }
        };
        filePickerDialog.show();
    }

    private void openAdvanceSetting() {
        DownloadTaskAdvanceEditor setting = new DownloadTaskAdvanceEditor(baseScreen, downloadModel);
        setting.isForceAssemble(isForceAssemble);
        setting.show();
    }

    @Deprecated
    private void setFileNameFromSuggestion() {
        String fileUrl = getFileUrl();
        if (fileUrl.length() > 2) {
            String fileName = NetworkUtils.getFileNameFromUrl(fileUrl);
            try {
                String name = URLDecoder.decode(fileName, "UTF-8");
                if (name != null && name.length() > 1) setFileName(name);
            } catch (UnsupportedEncodingException error) {
                error.printStackTrace();
                setFileName(fileName);
            }
        }
    }

    private void downloadClick() {
        //if the program currently busy at calculating the file size from the server,
        //then we will stop the  program and show a simple message to the user.
        if (isWaitingForFileSize) {
            baseScreen.vibrator.vibrate(20);
            baseScreen.showSimpleMessageBox(getString(R.string.wait_file_size_dialog));
            return;
        }

        //after validate the all correct configurations let the program pass on below.
        if (isAllSettingsValidate()) {
            if (isForceAssemble) {
                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(baseScreen, false, getString(R.string.moving_file_wait));

                doInBackground(new BackgroundJob() {
                    @Override
                    public void doInBackground() {
                        forceAssemble(progressDialog);
                        doInMainThread(new MainThreadJob() {
                            @Override
                            public void doInUIThread() {
                                progressDialog.close();
                            }
                        });
                    }
                });
            } else {
                normalDownload();
            }
        }
    }

    private String getString(int moving_file_wait) {
        return baseScreen.getString(moving_file_wait);
    }

    private void forceAssemble(final ProgressDialog progressDialog) {
        showProgress(true, progressDialog, "");
        try {
            showProgress(false, progressDialog, "Configuring...");
            configureDownloadModel();

            //change the file path.
            final File oldDownloadFile = new File(oldFilePath, oldFileName);
            final File newDownloadFile = new File(downloadModel.filePath, downloadModel.fileName);

            showProgress(false, progressDialog, "Checking for matching files...");

            for (File childFiles : newDownloadFile.getParentFile().listFiles()) {
                if (childFiles.getName().equals(downloadModel.fileName)) {
                    doInMainThread(new MainThreadJob() {
                        @Override
                        public void doInUIThread() {
                            baseScreen.showSimpleMessageBox("A file already exits with the file name.");
                        }
                    });
                    return;
                }
            }

            final App app;
            final DownloadSystem downloadSystem;

            app = baseScreen.app;
            downloadSystem = app.getDownloadSystem();

            move(oldDownloadFile, newDownloadFile);

            //delete the original file
            //noinspection ResultOfMethodCallIgnored
            oldDownloadFile.delete();

            //delete the old model file.
            DownloadModel oldModel = new DownloadModel();
            oldModel.fileName = oldFileName;
            oldModel.deleteFromDisk(DownloadModel.FILE_FORMAT);

            //write the new download model file.
            downloadModel.updateDataInDisk();

            doInMainThread(new MainThreadJob() {
                @Override
                public void doInUIThread() {
                    downloadSystem.getDownloadUiManager().updateDownloadProgressWith(downloadModel);
                    progressDialog.close();
                    close();
                    baseScreen.showSimpleMessageBox(getString(R.string.force_assemble_complete));
                }
            });

        } catch (Exception error) {
            error.printStackTrace();
            doInMainThread(new MainThreadJob() {
                @Override
                public void doInUIThread() {
                    progressDialog.close();
                    baseScreen.vibrator.vibrate(30);
                    baseScreen.showSimpleMessageBox(getString(R.string.error_found_try_later));
                }
            });
        }
    }

    private void showProgress(final boolean willShow,
                              final ProgressDialog progressDialog, final String message) {
        doInMainThread(new MainThreadJob() {
            @Override
            public void doInUIThread() {
                progressDialog.setProgressMessage(message);
                if (willShow)
                    progressDialog.show();
            }
        });
    }

    private void normalDownload() {
        configureDownloadModel();
        validateNameAndDownload();
    }

    private void configureDownloadModel() {
        downloadModel.fileName = getFileName();
        downloadModel.fileUrl = getFileUrl();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void validateNameAndDownload() {
        App app = baseScreen.app;
        DownloadSystem downloadSystem = app.getDownloadSystem();

        boolean isFileNameChanged = false;

        while (downloadSystem.searchMatchingIncompleteDownloadModel(this.downloadModel) != null) {
            this.downloadModel.fileName = "[new]" + this.downloadModel.fileName;
            isFileNameChanged = true;
        }

        while (new File(this.downloadModel.filePath, this.downloadModel.fileName).exists()) {
            this.downloadModel.fileName = "[new]" + this.downloadModel.fileName;
            isFileNameChanged = true;
        }

        if (downloadModel.totalFileLength < 1) {
            baseScreen.showSimpleMessageBox("Without file size Aladin DM can not download the file.");
            return;
        }


        if (this.downloadModel.fileName.toLowerCase().endsWith(".apk")) {
            this.downloadModel.numberOfPart = 1;
        }

        if (this.downloadModel.isCatalogEnable) {
            this.downloadModel.filePath = getSubDirectoryBy(this.downloadModel.fileName, this.downloadModel.filePath);
            File downloadDir = new File(this.downloadModel.filePath);
            downloadDir.mkdir();
        }

        //start the download task.
        DownloadTaskStarter.addOrResumeTask(baseScreen, this.downloadModel);

        //download file name has been changed.
        if (isFileNameChanged) {
            setFileName(this.downloadModel.fileName);
            final MessageBox messageBox = MessageBox.getInstance(baseScreen);
            messageBox.setHtmlMessage(Html.fromHtml("Download successfully added, " +
                    "But the file name has changed due to similar file names<br/>" +
                    "New File Name  : <b>" + this.downloadModel.fileName + "</b>"));
            messageBox.clickListener = new MessageBox.ClickListener() {
                @Override
                public void onClick(View view) {
                    close();
                    messageBox.close();
                }
            };
            messageBox.show();
        } else {
            close();
        }
    }

    /**
     * Check either the given the url is a valid url or not.
     *
     * @param fileUrl the file url.
     * @return true if the url is valid, false otherwise.
     */
    private boolean isValidUrl(String fileUrl) {
        try {
            new URL(fileUrl);
        } catch (Exception error) {
            error.printStackTrace();
            return false;
        }

        return true;
    }


    private boolean isAllSettingsValidate() {
        try {
            if (!validateStorage()) {
                baseScreen.vibrator.vibrate(20);
                baseScreen.showSimpleHtmlMessageBox("You do not have sufficient storage to download the file.\n" +
                        "You have total <b>" + getAvailableStorageSpace() + "</b> of space available, But you need <b>" +
                        humanReadableSizeOf(downloadModel.totalFileLength) + "</b> to download this file.");
                return false;
            }
        } catch (Exception error) {
            error.printStackTrace();
            return false;
        }

        if (!isValidUrl(getFileUrl())) {
            baseScreen.vibrator.vibrate(20);
            baseScreen.showSimpleMessageBox("Enter a valid file link.");
            return false;
        }

        if (!getFileUrl().startsWith("http://") && !getFileUrl().trim().startsWith("https://")) {
            baseScreen.vibrator.vibrate(20);
            baseScreen.showSimpleMessageBox("The Url should start with http protocol. " +
                    "This entered protocol is not supported.");
            return false;
        }

        String fileName = fileNameEdit.getText().toString().trim();

        if (fileName.length() < 2) {
            baseScreen.vibrator.vibrate(20);
            baseScreen.showSimpleMessageBox("Enter the file name.");
            return false;
        }

        return true;
    }


    private String getAvailableStorageSpace() {
        File downloadFileDir = new File(getFilePath());
        return humanReadableSizeOf(DownloadTools.getAvailableStorageSpace(downloadFileDir));
    }


    private boolean validateStorage() {
        File downloadFileDir = new File(getFilePath());
        long totalStorage = DownloadTools.getAvailableStorageSpace(downloadFileDir);
        long totalFileLength = downloadModel.totalFileLength;
        return totalFileLength < totalStorage;
    }
}

