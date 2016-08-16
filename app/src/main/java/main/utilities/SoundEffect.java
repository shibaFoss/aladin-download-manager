package main.utilities;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundEffect {

    public static void playSound(Context context, int resId) {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
            mediaPlayer.start();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
