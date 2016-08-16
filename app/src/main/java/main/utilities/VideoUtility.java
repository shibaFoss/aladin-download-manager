package main.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;

import java.util.HashMap;

public class VideoUtility {

    public static Bitmap retrieveVideoFrameFrom(Context context, String videoUrl) throws Exception {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoUrl, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoUrl);

            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null)
                mediaMetadataRetriever.release();
        }
        return bitmap;
    }
}
