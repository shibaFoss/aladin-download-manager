package main.screens.main_screen.setting_screen;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.fdm.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import main.dialog_factory.FilePickerDialog;
import main.download_manager.DownloadService;
import main.download_manager.DownloadSystem;
import main.screens.about_us_screen.AboutUsScreen;
import main.screens.main_screen.BaseNestedScreen;
import main.screens.main_screen.feedback_screen.FeedbackScreen;
import main.settings.UserSettings;
import main.utilities.DeviceTool;

public class SettingScreen extends BaseNestedScreen implements View.OnClickListener {

    public View layoutView;
    public TextView downloadDirectory, turboBooster, autoResume, clipboardMonitor;
    public TextView
            downloadParts, smartDownload, fileCatalog,
            bufferSize, maximumRunningTask, progressInterval,
            pauseAfterError, downloadViaWifi, userAgent, urlFilter;
    public TextView bluetoothShare, inviteFriends, feedback, requestFeature, rateUs, aboutUs;
    public ToggleButton smartDownloadToggle, clipboardMonitorToggle, fileCatalogToggle, downloadViaWifiToggle, urlFilterToggle,
            turboBoosterToggle, autoResumeToggle;
    private ImageButton exitButton;
    private MoreSettingClickHandler moreSettingClickHandler;

    @Override
    protected int getScreenLayout() {
        return R.layout.screen_settings;
    }

    @Override
    protected void onAfterLayoutLoad(View layoutView, Bundle savedInstanceState) {
        this.layoutView = layoutView;
        this.init();
    }

    @Override
    protected void onViewCreating(View view) {
        //nothing to do here.
    }

    private void init() {
        this.moreSettingClickHandler = new MoreSettingClickHandler(this);
        this.exitButton = (ImageButton) layoutView.findViewById(R.id.bnt_power_off);

        initDownloadSettings();
        initToggleButtons();
        basicHelpSettings();
        initClickEvents();

        updateToggleCheckStatus();
    }

    private void initDownloadSettings() {
        downloadDirectory = (TextView) layoutView.findViewById(R.id.download_path);
        turboBooster = (TextView) layoutView.findViewById(R.id.turbo_booster);
        autoResume = (TextView) layoutView.findViewById(R.id.auto_resume);
        clipboardMonitor = (TextView) layoutView.findViewById(R.id.clipboard_monitor);

        downloadParts = (TextView) layoutView.findViewById(R.id.download_part);
        smartDownload = (TextView) layoutView.findViewById(R.id.auto_download_part);
        fileCatalog = (TextView) layoutView.findViewById(R.id.file_catalog);

        bufferSize = (TextView) layoutView.findViewById(R.id.buffer_size);
        maximumRunningTask = (TextView) layoutView.findViewById(R.id.running_downloads);
        progressInterval = (TextView) layoutView.findViewById(R.id.progress_interval);

        pauseAfterError = (TextView) layoutView.findViewById(R.id.pause_after_error);
        downloadViaWifi = (TextView) layoutView.findViewById(R.id.only_via_wifi);
        userAgent = (TextView) layoutView.findViewById(R.id.user_agent);
        urlFilter = (TextView) layoutView.findViewById(R.id.auto_filter_url);
    }

    private void initToggleButtons() {
        turboBoosterToggle = (ToggleButton) layoutView.findViewById(R.id.turbo_booster_toggle);
        autoResumeToggle = (ToggleButton) layoutView.findViewById(R.id.auto_resume_toggle);
        clipboardMonitorToggle = (ToggleButton) layoutView.findViewById(R.id.clipboard_monitor_toggle);

        smartDownloadToggle = (ToggleButton) layoutView.findViewById(R.id.auto_download_part_toggle);
        fileCatalogToggle = (ToggleButton) layoutView.findViewById(R.id.file_catalog_toggle);
        downloadViaWifiToggle = (ToggleButton) layoutView.findViewById(R.id.only_via_wifi_toggle);
        urlFilterToggle = (ToggleButton) layoutView.findViewById(R.id.auto_filter_url_toggle);
    }

    private void basicHelpSettings() {
        bluetoothShare = (TextView) layoutView.findViewById(R.id.share_via_bluetooth);
        inviteFriends = (TextView) layoutView.findViewById(R.id.invite_friends);
        feedback = (TextView) layoutView.findViewById(R.id.feedback);
        requestFeature = (TextView) layoutView.findViewById(R.id.request_feature);
        rateUs = (TextView) layoutView.findViewById(R.id.rate_us);
        aboutUs = (TextView) layoutView.findViewById(R.id.about_us);
    }

    private void initClickEvents() {
        exitButton.setOnClickListener(this);

        downloadDirectory.setOnClickListener(this);

        turboBooster.setOnClickListener(this);
        turboBoosterToggle.setOnClickListener(this);

        autoResume.setOnClickListener(this);
        autoResumeToggle.setOnClickListener(this);

        clipboardMonitor.setOnClickListener(this);
        clipboardMonitorToggle.setOnClickListener(this);


        downloadParts.setOnClickListener(this);

        smartDownload.setOnClickListener(moreSettingClickHandler);
        smartDownloadToggle.setOnClickListener(moreSettingClickHandler);

        fileCatalog.setOnClickListener(moreSettingClickHandler);
        fileCatalogToggle.setOnClickListener(moreSettingClickHandler);

        bufferSize.setOnClickListener(moreSettingClickHandler);
        maximumRunningTask.setOnClickListener(moreSettingClickHandler);
        progressInterval.setOnClickListener(moreSettingClickHandler);

        pauseAfterError.setOnClickListener(moreSettingClickHandler);

        downloadViaWifi.setOnClickListener(moreSettingClickHandler);
        downloadViaWifiToggle.setOnClickListener(moreSettingClickHandler);

        userAgent.setOnClickListener(moreSettingClickHandler);

        urlFilter.setOnClickListener(moreSettingClickHandler);
        urlFilterToggle.setOnClickListener(moreSettingClickHandler);

        bluetoothShare.setOnClickListener(this);
        inviteFriends.setOnClickListener(this);
        feedback.setOnClickListener(this);
        requestFeature.setOnClickListener(this);
        rateUs.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
    }

    //Update all the toggle button's check status depending on current user settings.
    public void updateToggleCheckStatus() {
        final UserSettings userSettings = getMainScreen().app.getUserSettings();

        turboBoosterToggle.setChecked(userSettings.isEnableTurboBooster());
        autoResumeToggle.setChecked(userSettings.isAutoResume());
        clipboardMonitorToggle.setChecked(userSettings.isClipboardMonitoring());

        smartDownloadToggle.setChecked(userSettings.isSmartDownload());
        fileCatalogToggle.setChecked(userSettings.isFileCatalog());
        downloadViaWifiToggle.setChecked(userSettings.isOnlyWifi());
        urlFilterToggle.setChecked(userSettings.isUrlFilter());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == downloadDirectory.getId()) downloadDirectory();
        else if (view.getId() == turboBooster.getId()) turboBooster();
        else if (view.getId() == turboBoosterToggle.getId()) turboBooster();
        else if (view.getId() == autoResume.getId() || view.getId() == autoResumeToggle.getId()) autoResume();

        else if (view.getId() == clipboardMonitor.getId()) clipboardMonitor();
        else if (view.getId() == clipboardMonitorToggle.getId()) clipboardMonitor();

        else if (view.getId() == downloadParts.getId()) downloadParts();

        else if (view.getId() == bluetoothShare.getId()) bluetoothShare();
        else if (view.getId() == inviteFriends.getId()) inviteFriends();
        else if (view.getId() == feedback.getId()) feedback();
        else if (view.getId() == requestFeature.getId()) requestFeature();
        else if (view.getId() == rateUs.getId()) rateUs();
        else if (view.getId() == aboutUs.getId()) aboutUs();
        else if (view.getId() == exitButton.getId()) exitButton();
    }

    private void downloadDirectory() {
        final UserSettings userSettings = getMainScreen().app.getUserSettings();
        final FilePickerDialog filePickerDialog = new FilePickerDialog(getMainScreen());
        filePickerDialog.onPathSelectListener = new FilePickerDialog.OnSelectListener() {
            @Override
            public void onSelect(FilePickerDialog pickerDialog, String selectedPath) {
                pickerDialog.close();
                userSettings.setDownloadPath(selectedPath);
            }
        };
        filePickerDialog.show();
    }

    private void turboBooster() {
        final UserSettings userSettings = getMainScreen().app.getUserSettings();
        if (userSettings.isEnableTurboBooster()) userSettings.setTurboBooster(false);
        else userSettings.setTurboBooster(true);

        turboBoosterToggle.setChecked(userSettings.isEnableTurboBooster());
    }

    private void autoResume() {
        final UserSettings userSettings = getMainScreen().app.getUserSettings();
        if (userSettings.isAutoResume()) userSettings.setAutoResume(false);
        else userSettings.setAutoResume(true);
        autoResumeToggle.setChecked(userSettings.isAutoResume());
    }

    private void clipboardMonitor() {
        final UserSettings settings = getMainScreen().app.getUserSettings();

        if (settings.isClipboardMonitoring())
            settings.setClipboardMonitoring(false);
        else
            settings.setClipboardMonitoring(true);

        clipboardMonitorToggle.setChecked(settings.isClipboardMonitoring());
    }

    private void downloadParts() {
        new DownloadPartEditor(getMainScreen()).show();
    }

    /**
     * Share the apk file of the application via bluetooth.
     */
    private void bluetoothShare() {
        try {
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(getMainScreen().getPackageName());

            final List matchingApkInfo = getMainScreen().getPackageManager().queryIntentActivities(intent, 0);
            for (Object object : matchingApkInfo) {
                ResolveInfo info = (ResolveInfo) object;
                File apkFile = new File(info.activityInfo.applicationInfo.publicSourceDir);
                File shareApkFile = new File(UserSettings.applicationPath,
                        "Aladin DM" + getMainScreen().getVersionName() + ".apk");
                try {
                    DeviceTool.copy(apkFile, shareApkFile);
                    shareViaBluetooth(shareApkFile);
                } catch (IOException error) {
                    error.printStackTrace();
                    shareViaBluetooth(apkFile);
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
            getMainScreen().showSimpleMessageBox("Your device does not support this feature.");
        }
    }

    private void inviteFriends() {
        new FriendInvite(getMainScreen()).show();
    }

    private void feedback() {
        getMainScreen().startActivity(FeedbackScreen.class);
    }

    private void requestFeature() {
        Intent intent = new Intent(getMainScreen(), FeedbackScreen.class);
        intent.putExtra("request_feature", true);
        startActivity(intent);
        getMainScreen().overridePendingTransition(R.anim.screen_enter_anim, R.anim.screen_out_anim);
    }

    private void rateUs() {
        getMainScreen().gotoPlayStore();
    }

    private void aboutUs() {
        getMainScreen().startActivity(AboutUsScreen.class);
    }

    private void exitButton() {
        DownloadSystem downloadSystem = getMainScreen().app.getDownloadSystem();
        int runningTask = downloadSystem.getTotalNumberOfRunningTask();

        if (runningTask == 0) {
            DownloadService downloadService = downloadSystem.getDownloadService();
            downloadService.stopForeground();
            downloadService.stopService(new Intent(getMainScreen(), DownloadService.class));
        }
        downloadSystem.prepare(getMainScreen().app);
        getMainScreen().finish();
    }

    private void shareViaBluetooth(File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        final List matchingApkInfo = getMainScreen().getPackageManager().queryIntentActivities(intent, 0);
        for (Object object : matchingApkInfo) {
            ResolveInfo info = (ResolveInfo) object;
            String appName = (String) info.loadLabel(getMainScreen().getPackageManager());
            if (appName.toLowerCase().equals("bluetooth")) {
                intent.setPackage(info.activityInfo.packageName);
                getMainScreen().startActivity(intent);
            }
        }
    }

}
