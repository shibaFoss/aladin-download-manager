package main.screens.music_screen;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import main.screens.BaseScreen;
import main.screens.music_screen.AudioManager.MusicPreparedListener;
import main.screens.music_screen.AudioManager.MusicProgressListener;
import net.fdm.R;

import java.io.File;

import static android.graphics.BitmapFactory.decodeByteArray;
import static main.utilities.TimeUtility.calculateTime;
import static main.utilities.ViewUtility.circleCropBitmap;

/**
 * The screen gives user the a UI where they can play any audio file.
 */
public class MusicPlayerScreen extends BaseScreen
        implements OnClickListener, MusicProgressListener, MusicPreparedListener, SeekBar.OnSeekBarChangeListener {

    public ImageButton rewindButton, playButton, forwardButton, optionButton;
    public TextView songName, songArtist, currentTime, totalTime;
    public ImageView albumImage, albumImageBig;
    public SeekBar seekBar;

    @Override
    public int getLayout() {
        return R.layout.music_screen;
    }

    @Override
    public void onLayoutLoad() {
        init();
    }

    @Override
    public void onAfterLayoutLoad() {
        try {
            //get the intended file path.
            String audioFilePath = getIntendedAudioFilePath();
            if (audioFilePath != null) {

                //if the requested audio file is not exist, so then we need to terminate the process.
                if (!new File(audioFilePath).exists()) return;

                AudioManager audioManager = app.getAudioManager();
                AudioInfo audioInfo = generateAudioInfo(audioFilePath);

                //request to play the audio.
                audioManager.play(audioInfo);
            }
        } catch (Exception error) {
            error.printStackTrace();
            showSimpleMessageBox(getString(R.string.file_cant_be_played));
        }
    }

    //Generate a AudioInfo object with all the information of the
    //audio file.
    private AudioInfo generateAudioInfo(String audioFilePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(audioFilePath);

        //get the information of the audio file.
        String name = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        byte[] coverPicByte = retriever.getEmbeddedPicture();

        //make a audio info object, so that we can send this for execute various
        //operation later.
        AudioInfo audioInfo = new AudioInfo();
        audioInfo.songName = name;
        audioInfo.songArtist = artist;
        audioInfo.filePath = audioFilePath;
        audioInfo.coverImage = coverPicByte;
        audioInfo.audioDuration = Integer.parseInt(duration);

        if (audioInfo.songName.length() < 1) {
            audioInfo.songName = new File(audioFilePath).getName();
            audioInfo.songArtist = "Unknown";
        }

        return audioInfo;
    }

    @Override
    public void onPauseScreen() {
        //todo : nothing to do here.
    }

    @Override
    public void onResumeScreen() {
        try {
            //start the background music_player_service
            startService(new Intent(this, MusicPlayerService.class));

            //set up the listener of the audio manager.
            AudioManager audioManager = app.getAudioManager();
            audioManager.setMusicPreparedListener(this);
            audioManager.setMusicProgressListener(this);

            //update the current status of a running audio file.
            onMusicPrepared(audioManager);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    @Override
    public void onExitScreen() {
        finish();
    }

    @Override
    public void onClearMemory() {
        //todo : nothing to do here.
    }

    @Override
    public void onScreenOptionChange(Configuration configuration) {
        //todo : nothing to do here.
    }

    /**
     * This method is called when the media player finishes its buffer loading.
     */
    @Override
    public void onMusicPrepared(AudioManager audioManager) {
        AudioInfo audioInfo = audioManager.getAudioInfo();
        if (audioInfo != null) {
            songName.setText(audioInfo.songName);
            songArtist.setText(audioInfo.songArtist);
            totalTime.setText(calculateTime(audioInfo.audioDuration));

            Bitmap coverPic = decodeByteArray(audioInfo.coverImage, 0, audioInfo.coverImage.length);
            Bitmap cropAlbumCover = circleCropBitmap(coverPic);

            albumImageBig.setImageBitmap(coverPic);

            if (cropAlbumCover != null) {
                albumImage.setImageBitmap(cropAlbumCover);
            } else {
                albumImage.setImageBitmap(coverPic);
            }

            seekBar.setMax(audioInfo.audioDuration);
        }
    }

    /**
     * This method is called when the media player updates its progress status.
     * the progress interval time is 1 sec.
     */
    @Override
    public void onProgressUpdate(AudioManager audioManager) {
        MediaPlayer mediaPlayer = audioManager.getMediaPlayer();
        if (mediaPlayer != null) {
            AudioInfo audioInfo = audioManager.getAudioInfo();
            if (audioInfo != null) {
                int currentProgress = mediaPlayer.getCurrentPosition();
                currentTime.setText((calculateTime(currentProgress)));
                seekBar.setProgress(currentProgress);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == playButton.getId()) {
            toggleAudioPlay();
        }
    }

    private void init() {
        rewindButton = (ImageButton) findViewById(R.id.buttonPrevious);
        forwardButton = (ImageButton) findViewById(R.id.buttonNext);
        playButton = (ImageButton) findViewById(R.id.buttonControl);
        optionButton = (ImageButton) findViewById(R.id.optionButton);

        songName = (TextView) findViewById(R.id.text_singer);
        songArtist = (TextView) findViewById(R.id.text_publisher);

        currentTime = (TextView) findViewById(R.id.current_time);
        totalTime = (TextView) findViewById(R.id.total_time);

        albumImage = (ImageView) findViewById(R.id.music_album_thumb);
        albumImageBig = (ImageView) findViewById(R.id.music_player_thumb);
        seekBar = (SeekBar) findViewById(R.id.music_progress);

        optionButton.setOnClickListener(this);
        rewindButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        forwardButton.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(this);
    }

    private String getIntendedAudioFilePath() {
        try {
            Intent intent = getIntent();
            Uri dataUri = intent.getData();
            String musicFilePath = Uri.decode(dataUri.toString().substring(6));
            File musicFile = new File(musicFilePath);
            return musicFile.getAbsolutePath();
        } catch (Exception error) {
            error.printStackTrace();
            return null;
        }
    }

    private void toggleAudioPlay() {
        AudioManager audioManager = app.getAudioManager();
        MediaPlayer mediaPlayer = audioManager.getMediaPlayer();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                playButton.setImageDrawable(getDrawableImage(R.drawable.icon_play));
            } else {
                mediaPlayer.seekTo(seekBar.getProgress());
                mediaPlayer.reset();
                playButton.setImageDrawable(getDrawableImage(R.drawable.icon_pause));
            }
        }
    }

    int seekPosition = 0;

    @Override
    public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
        seekPosition = position;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //nothing to do.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        AudioManager audioManager = app.getAudioManager();
        MediaPlayer mediaPlayer = audioManager.getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(seekPosition);
            seekPosition = 0;
        }
    }
}
