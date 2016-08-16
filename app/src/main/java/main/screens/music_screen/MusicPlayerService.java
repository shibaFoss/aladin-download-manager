package main.screens.music_screen;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import main.app.App;
import net.fdm.R;

@SuppressWarnings("FieldCanBeLocal")
public class MusicPlayerService extends Service implements AudioManager.MusicCompleteListener {

    private NotificationManager notificationmanager;
    private App application;
    private AudioManager audioManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        application = (App) getApplication();
        notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        audioManager = application.getAudioManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        super.onStartCommand(intent, flag, startId);
        audioManager.setMusicCompleteListener(this);
        audioManager.setMusicPlayerService(this);
        return START_STICKY;
    }

    public void startForeground(String songName, String songArtist) {
        Intent intent = new Intent(this, MusicPlayerScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setContentTitle(songName)
                .setContentText(songArtist);
        builder.setSmallIcon(R.drawable.ic_play_white);

        startForeground(1992, builder.build());
    }

    @Override
    public void onMusicComplete(AudioManager audioManager) {
        stopForeground(true);
    }
}
