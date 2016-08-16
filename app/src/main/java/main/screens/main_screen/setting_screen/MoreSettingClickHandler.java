package main.screens.main_screen.setting_screen;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import async_job.AsyncJob;
import main.dialog_factory.ProgressDialog;
import main.dialog_factory.YesNoDialog;
import main.settings.UserSettings;

import static async_job.AsyncJob.doInBackground;
import static async_job.AsyncJob.doInMainThread;

//Helper class that helps receiving the extra setting's on_click event.
public class MoreSettingClickHandler implements View.OnClickListener {

    private SettingScreen settingScreen;

    public MoreSettingClickHandler(SettingScreen settingScreen) {
        this.settingScreen = settingScreen;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == settingScreen.smartDownload.getId())  autoDownloadPart();
        else if (view.getId() == settingScreen.smartDownloadToggle.getId())  autoDownloadPart();
        else if (view.getId() == settingScreen.fileCatalog.getId()) autoFileCatalog();
        else if (view.getId() == settingScreen.fileCatalogToggle.getId())  autoFileCatalog();
        else if (view.getId() == settingScreen.bufferSize.getId())  downloadBufferSize();
        else if (view.getId() == settingScreen.maximumRunningTask.getId()) runningDownloads();
        else if (view.getId() == settingScreen.progressInterval.getId()) progressInterval();
        else if (view.getId() == settingScreen.pauseAfterError.getId())  pauseAfterError();
        else if (view.getId() == settingScreen.downloadViaWifi.getId()) downloadViaWfi();
        else if (view.getId() == settingScreen.userAgent.getId()) userAgent();
        else if (view.getId() == settingScreen.urlFilter.getId()) urlFilter();
        else if (view.getId() == settingScreen.urlFilterToggle.getId()) urlFilter();
    }

    private void resetSetting() {
        final YesNoDialog yesNoDialog = new YesNoDialog(settingScreen.getMainScreen());
        yesNoDialog.clickListener = new YesNoDialog.ClickListener() {
            @Override
            public void onYes(TextView view) {
                yesNoDialog.close();
                final ProgressDialog progress;
                progress = new ProgressDialog(settingScreen.getMainScreen(), false, "Wait...validating all default settings...");
                progress.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doInBackground(new AsyncJob.BackgroundJob() {
                            @Override
                            public void doInBackground() {
                                UserSettings userSettings = settingScreen.getMainScreen().app.getUserSettings();
                                userSettings.resetDealtSettings();
                                userSettings.updateInDisk();
                                doInMainThread(new AsyncJob.MainThreadJob() {
                                    @Override
                                    public void doInUIThread() {
                                        progress.close();
                                        settingScreen.updateToggleCheckStatus();
                                    }
                                });
                            }
                        });
                    }
                }, 2000);
            }

            @Override
            public void onNo(TextView view) {
                yesNoDialog.close();
            }
        };

        yesNoDialog.setMessage("Do you want to reset all the settings ?");
        yesNoDialog.show();
    }

    private void urlFilter() {
        final UserSettings setting = getUserSettings();
        if (setting.isUrlFilter()) setting.setUrlFilter(false);
        else setting.setUrlFilter(true);
        settingScreen.urlFilterToggle.setChecked(setting.isUrlFilter());
    }

    private void userAgent() {
        new UserAgentEditor(settingScreen).show();
    }

    private UserSettings getUserSettings() {
        return settingScreen.getMainScreen().app.getUserSettings();
    }

    private void downloadViaWfi() {
        final UserSettings setting = getUserSettings();
        if (setting.isOnlyWifi()) setting.setOnlyWifi(false);
        else setting.setOnlyWifi(true);
        settingScreen.downloadViaWifiToggle.setChecked(setting.isOnlyWifi());
    }

    private void pauseAfterError() {
        new PauseAfterErrorEditor(settingScreen.getMainScreen()).show();
    }

    private void progressInterval() {
        new UiProgressIntervalEditor(settingScreen.getMainScreen()).show();
    }

    private void runningDownloads() {
        new RunningDownlaodEditor(settingScreen.getMainScreen()).show();
    }

    private void downloadBufferSize() {
        new BufferSizeEditor(settingScreen.getMainScreen()).show();
    }

    private void autoFileCatalog() {
        final UserSettings userSettings = getUserSettings();
        if (userSettings.isFileCatalog()) userSettings.setFileCatalog(false);
        else userSettings.setFileCatalog(true);

        settingScreen.fileCatalogToggle.setChecked(userSettings.isFileCatalog());
    }

    private void autoDownloadPart() {
        final UserSettings userSettings = getUserSettings();
        if (userSettings.isSmartDownload()) userSettings.setSmartDownload(false);
        else userSettings.setSmartDownload(true);

        settingScreen.smartDownloadToggle.setChecked(userSettings.isSmartDownload());
    }
}
