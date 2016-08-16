package main.utilities;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import main.settings.UserSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.graphics.BitmapFactory.decodeFile;

public class ImageCache {

    public static final String CACHE_FILE_PATH = UserSettings.settingPath + "/.Bitmap Caches";


    public static void initialize() {
        makeDirs();
    }


    public synchronized static void saveImageBitmapToDisk(Bitmap bitmap, String imageName) {
        StorageTool.saveBitmapOnDisk(bitmap, CACHE_FILE_PATH, imageName + ".jpg");
    }


    public synchronized static void saveImageDrawableToDisk(Drawable drawable, String imageName) {
        StorageTool.saveBitmapOnDisk(drawableToBitmap(drawable), CACHE_FILE_PATH, imageName + ".jpg");
    }


    public synchronized static Bitmap readImageBitmap(String packageName) {
        File imageFile = new File(CACHE_FILE_PATH, packageName + ".jpg");
        if (imageFile.exists())
            return decodeFile(imageFile.getAbsolutePath());
        else
            return null;
    }


    public synchronized static Bitmap drawableToBitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        final Bitmap[] bitmap = new Bitmap[1];

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        bitmap[0] = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap[0]);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap[0];
    }


    private static void makeDirs() {
        File cacheDirectory = new File(CACHE_FILE_PATH);
        if (!cacheDirectory.exists())
            //noinspection ResultOfMethodCallIgnored
            cacheDirectory.mkdirs();
    }


    public static Bitmap getBitmapFromURL(URL src) {
        try {
            HttpURLConnection connection = (HttpURLConnection) src.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
