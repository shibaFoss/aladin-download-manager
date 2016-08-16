package main.download_manager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import main.app.App;
import main.key_database.KeyStore;
import main.screens.main_screen.MainScreen;
import main.screens.popup_screen.DownloadPopupScreen;
import net.fdm.R;

/**
 * DownloadService is the main class that rus the whole {@link DownloadSystem} at the background
 * process.
 * The class receives operational task intent of downloadTasks. After it receive the task intent
 * it handover the data to the downloadSystem. The remaining done by the {@link DownloadSystem}.
 */
public class DownloadService extends Service {
    public static final int ADD_NEW_DOWNLOAD = 0, PAUSE_DOWNLOAD = 1,
            DELETE_DOWNLOAD = 2, RESTART_DOWNLOAD = 3;
    private DownloadSystem downloadSystem;

    @Override
    public void onCreate() {
        super.onCreate();
        App application = (App) getApplication();
        downloadSystem = application.getDownloadSystem();
        startDownloadSystem();
        makeServiceForeground();
        injectClipboardMonitor();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        super.onStartCommand(intent, flag, startId);
        makeServiceForeground();

        if (intent == null) return START_NOT_STICKY;

        int REQUEST_CODE = intent.getIntExtra(KeyStore.DOWNLOAD_REQUEST_CODE, -1);

        if (REQUEST_CODE == ADD_NEW_DOWNLOAD) {
            downloadSystem.addOrResumeDownload(DownloadService.this, intent);
        }

        if (REQUEST_CODE == PAUSE_DOWNLOAD) {
            downloadSystem.pauseDownload(DownloadService.this, intent);
        }

        if (REQUEST_CODE == DELETE_DOWNLOAD) {
            downloadSystem.deleteDownload(DownloadService.this, intent);
        }

        if (REQUEST_CODE == RESTART_DOWNLOAD) {
            downloadSystem.restartDownload(DownloadService.this, intent);
        }

        return START_STICKY;
    }

    @Deprecated
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadSystem != null) downloadSystem.stopDownloadSystem();
    }

    private void startDownloadSystem() {
        if (downloadSystem == null) {
            throw new NullPointerException(getString(R.string.download_system_fail_message));
        }

        downloadSystem.startDownloadSystem(this);
    }

    public void makeServiceForeground() {
        Intent intent = new Intent(this, MainScreen.class);
        intent.putExtra(KeyStore.OPEN_DOWNLOAD_FRAGMENT, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        configureNotification(pendingIntent, notificationBuilder);
        startForeground(2016, notificationBuilder.build());
    }

    public void stopForeground() {
        stopForeground(true);
    }

    private void configureNotification(PendingIntent intent, NotificationCompat.Builder builder) {
        int version= Build.VERSION.SDK_INT;
        if (version > 19 ) {
            builder.setSmallIcon(R.drawable.ic_aladin_dm_notification);
        } else {
            builder.setSmallIcon(R.drawable.ic_launcher);
        }

        builder.setContentTitle(getResources().getString(R.string.app_name_short));
        builder.setContentText("Service is running.");
        builder.setContentIntent(intent);
    }

    private void injectClipboardMonitor() {
        ClipboardMonitor clipboardMonitor = new ClipboardMonitor(this, (App) getApplication());
        clipboardMonitor.setOnCopyListener(new ClipboardMonitor.OnCopyListener() {
            @Override
            public void onCopyUrl(String fileName, String fileUrl) {
                startDownloadPopupActivity(fileName, fileUrl);
            }
        });
    }

    private void startDownloadPopupActivity(String fileName, String fileUrl) {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(20);
        Intent intent = new Intent(DownloadService.this, DownloadPopupScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra(KeyStore.FILE_NAME, fileName);
        intent.putExtra(KeyStore.FILE_URL, fileUrl);
        startActivity(intent);
    }

}
