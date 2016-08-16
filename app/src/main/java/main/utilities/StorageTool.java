package main.utilities;


import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class StorageTool {

    public static boolean saveBitmapOnDisk(Bitmap imageBitmap, String filePath, String fileName) {
        File parentFile = new File(filePath);
        if (!parentFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parentFile.mkdirs();
        }

        File imageFile = new File(filePath, fileName);
        if (imageFile.exists()) //noinspection ResultOfMethodCallIgnored
            imageFile.delete();

        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(imageFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (Exception error) {
            error.printStackTrace();
        }
        return imageFile.exists();
    }
}
