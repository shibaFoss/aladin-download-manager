package main.screens.music_screen;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import main.app.App;

import java.io.IOException;
import java.io.Serializable;

/**
 * AudioManager : A Manager class that provides all sorts of music playing functions to play audio
 * files.
 */
public class AudioManager implements
        Serializable, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {


    public interface MusicPreparedListener {
        void onMusicPrepared(AudioManager audioManager);
    }

    public interface MusicProgressListener {
        void onProgressUpdate(AudioManager audioManager);
    }

    public interface MusicCompleteListener {
        void onMusicComplete(AudioManager audioManager);
    }

    private App app;
    private MediaPlayer mediaPlayer;
    private AudioInfo audioInfo;
    private CountDownTimer runningTimer;
    private MusicPlayerService musicPlayerService;

    private MusicCompleteListener musicCompleteListener;
    private MusicProgressListener musicProgressListener;
    private MusicPreparedListener musicPreparedListener;

    //public constructor
    public AudioManager(App app) {
        this.app = app;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public AudioInfo getAudioInfo() {
        return audioInfo;
    }

    public App getApp() {
        return app;
    }

    public MusicPreparedListener getMusicPreparedListener() {
        return musicPreparedListener;
    }

    public void setMusicPreparedListener(MusicPreparedListener musicPreparedListener) {
        this.musicPreparedListener = musicPreparedListener;
    }

    public MusicProgressListener getMusicProgressListener() {
        return musicProgressListener;
    }

    public void setMusicProgressListener(MusicProgressListener musicProgressListener) {
        this.musicProgressListener = musicProgressListener;
    }

    public MusicCompleteListener getMusicCompleteListener() {
        return musicCompleteListener;
    }

    public void setMusicCompleteListener(MusicCompleteListener musicCompleteListener) {
        this.musicCompleteListener = musicCompleteListener;
    }

    public MusicPlayerService getMusicPlayerService() {
        return musicPlayerService;
    }

    public void setMusicPlayerService(MusicPlayerService musicPlayerService) {
        this.musicPlayerService = musicPlayerService;
    }

    /**
     * Prepare the media player.
     * call this method before calling {@link #play(AudioInfo)}
     */
    private void prepareMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            try {
                mediaPlayer.reset();
            } catch (IllegalStateException error) {
                error.printStackTrace();
            }
            mediaPlayer = new MediaPlayer();
        }

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    //Play a audio file.
    public void play(AudioInfo audioInfo) throws IOException {
        prepareMediaPlayer();
        this.audioInfo = audioInfo;
        mediaPlayer.setDataSource(audioInfo.filePath);
        mediaPlayer.prepare();
    }

    //The method get a callback after the media player finishes
    //it's buffer loading.
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //create a count_down_timer
        runningTimer = new CountDownTimer(audioInfo.audioDuration, 1000) {
            @Override
            public void onTick(long l) {
                try {
                    if (getMediaPlayer() != null && getMediaPlayer().isPlaying()) {
                        if (getMusicProgressListener() != null)
                            getMusicProgressListener().onProgressUpdate(AudioManager.this);
                    } else {
                        stopRunningTimer();
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    stopRunningTimer();
                }
            }

            @Override
            public void onFinish() {
                stopRunningTimer();
                if (getMusicProgressListener() != null)
                    getMusicProgressListener().onProgressUpdate(AudioManager.this);
            }
        };

        //start playing the sound.
        mediaPlayer.start();
        runningTimer.start();

        //oh now show the notification with song detail.
        if (musicPlayerService != null)
            musicPlayerService.startForeground(audioInfo.songName, audioInfo.songArtist);
    }

    private void stopRunningTimer() {
        if (runningTimer != null) {
            runningTimer.cancel();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (getMusicCompleteListener() != null)
            getMusicCompleteListener().onMusicComplete(this);

        if (runningTimer != null) runningTimer.cancel();

        mediaPlayer.stop();
        mediaPlayer.release();
        audioInfo = null;
    }

}
