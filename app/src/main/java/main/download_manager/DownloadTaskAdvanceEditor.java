package main.download_manager;

import android.app.Dialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import main.screens.BaseScreen;
import main.utilities.UiUtils;
import net.fdm.R;

/**
 * User can configure some extra options to a download task with this class.
 * After successful configuring downlaod settings the class sent back a call to
 * the implementor class.
 */
public final class DownloadTaskAdvanceEditor implements View.OnClickListener {

    private BaseScreen baseScreen;
    private DownloadModel downloadModel;

    private Dialog dialog;
    private TextView previewDownloadPart, previewProgressTimer;
    private CheckBox fileCatalog, autoResume, ttsVoice, slowNetwork, smartDownload;
    private SeekBar downloadParts, progressTimer;

    public DownloadTaskAdvanceEditor(BaseScreen baseScreen, DownloadModel downloadModel) {
        this.baseScreen = baseScreen;
        this.downloadModel = downloadModel;
        this.init();
        this.configureViewsWith(this.downloadModel);
    }

    public void isForceAssemble(boolean isForceAssemble) {
        if (isForceAssemble) {
            fileCatalog.setVisibility(View.GONE);
            smartDownload.setVisibility(View.GONE);
            downloadParts.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == fileCatalog.getId()) {
            downloadModel.isCatalogEnable = fileCatalog.isChecked();

        } else if (viewId == autoResume.getId()) {
            downloadModel.isAutoResumeEnable = autoResume.isChecked();

        } else if (viewId == ttsVoice.getId()) {
            downloadModel.isTtsNotificationEnable = ttsVoice.isChecked();

        } else if (viewId == slowNetwork.getId()) {
            optimizeSlowNetworkConnection();

        } else if (viewId == smartDownload.getId()) {
            downloadModel.isSmartDownload = smartDownload.isChecked();
            if (!smartDownload.isChecked()) {
                dialog.findViewById(R.id.download_part_root).setVisibility(View.VISIBLE);
            } else {
                dialog.findViewById(R.id.download_part_root).setVisibility(View.GONE);
            }
        }
    }

    private void optimizeSlowNetworkConnection() {
        if (slowNetwork.isChecked()) {
            if (!smartDownload.isChecked())
                dialog.findViewById(R.id.download_part_root).setVisibility(View.GONE);

            smartDownload.setVisibility(View.GONE);
            downloadModel.is2GConnection = true;
        } else {
            downloadModel.is2GConnection = false;
            smartDownload.setVisibility(View.VISIBLE);
            onClick(smartDownload);
        }
    }

    public void show() {
        dialog.show();
    }

    public void close() {
        dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.download_setting_advance_dialog);

        fileCatalog = (CheckBox) dialog.findViewById(R.id.file_catalog);
        autoResume = (CheckBox) dialog.findViewById(R.id.auto_resume);
        ttsVoice = (CheckBox) dialog.findViewById(R.id.tts_notification);
        slowNetwork = (CheckBox) dialog.findViewById(R.id.network_2g);
        smartDownload = (CheckBox) dialog.findViewById(R.id.smart_download);
        downloadParts = (SeekBar) dialog.findViewById(R.id.download_parts);
        progressTimer = (SeekBar) dialog.findViewById(R.id.progress_timer_seekbar);

        CheckBox allCheckBoxes[] = new CheckBox[]{
                fileCatalog, autoResume, ttsVoice, slowNetwork, smartDownload};

        for (CheckBox checkBox : allCheckBoxes) checkBox.setOnClickListener(this);

        downloadParts.setOnSeekBarChangeListener(getSeekBarChangeListener());
        progressTimer.setOnSeekBarChangeListener(getUISeekBarChangeListener());

        previewDownloadPart = (TextView) dialog.findViewById(R.id.download_part_preview);
        previewProgressTimer = (TextView) dialog.findViewById(R.id.progress_timer_preview);
    }


    private SeekBar.OnSeekBarChangeListener getUISeekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int index, boolean b) {
                downloadModel.uiProgressInterval = 1000 * (index + 1);
                previewProgressTimer.setText(generatePreviewStringOfProgressTimer(downloadModel));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }

    private String generatePreviewStringOfProgressTimer(DownloadModel downloadModel) {
        return baseScreen.getString(R.string.progress_interval) + " : " + (downloadModel.uiProgressInterval / 1000) + " Sec";
    }

    private SeekBar.OnSeekBarChangeListener getSeekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int index, boolean b) {
                downloadModel.numberOfPart = index + 1;
                previewDownloadPart.setText(getNumberOfDownloadPartsAsString(downloadModel));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }

    private String getNumberOfDownloadPartsAsString(DownloadModel downloadModel) {
        return baseScreen.getString(R.string.download_parts) + " " + downloadModel.numberOfPart;
    }

    private void configureViewsWith(DownloadModel downloadModel) {
        fileCatalog.setChecked(downloadModel.isCatalogEnable);
        autoResume.setChecked(downloadModel.isAutoResumeEnable);
        smartDownload.setChecked(downloadModel.isSmartDownload);
        slowNetwork.setChecked(downloadModel.is2GConnection);
        progressTimer.setProgress((downloadModel.uiProgressInterval / 1000) - 1);
        downloadParts.setProgress(downloadModel.numberOfPart - 1);
        previewDownloadPart.setText(getNumberOfDownloadPartsAsString(downloadModel));
        previewProgressTimer.setText(generatePreviewStringOfProgressTimer(downloadModel));

        onClick(smartDownload);
        onClick(slowNetwork);

        ttsVoice.setChecked(downloadModel.isTtsNotificationEnable);
    }

}
