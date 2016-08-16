package main.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class DeviceTool {

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }
        char first = string.charAt(0);
        if (Character.isUpperCase(first)) {
            return string;
        } else {
            return Character.toUpperCase(first) + string.substring(1);
        }
    }

    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void mkdirs(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                boolean result = file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String humanReadableSizeOf(long size) {
        DecimalFormat df = new DecimalFormat("##.##");

        if (size / (1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            return "" + df.format(tmpSize) + "Mb";
        } else if (size / 1024 > 0) {
            return "" + df.format((size / (1024))) + "Kb";
        } else
            return "" + df.format(size) + "B";
    }

    public static String humanReadableSizeOf(double size) {
        DecimalFormat df = new DecimalFormat("##.##");

        if (size / (1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            return "" + df.format(tmpSize) + "Mb";
        } else if (size / 1024 > 0) {
            return "" + df.format((size / (1024))) + "Kb";
        } else
            return "" + df.format(size) + "Kb";
    }

    public static String getMimeType(String uri) {
        String mimeType = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri);

        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mimeType = mime.getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }

    public static void openFile(File file, Activity activity) {
        String mimeType = getMimeType(Uri.fromFile(file).toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), mimeType);
        activity.startActivity(Intent.createChooser(intent, "Open with"));
    }

    public static void move(File oldDownloadFile, File newDownloadFile) throws IOException {
        FileInputStream inputStream = new FileInputStream(oldDownloadFile);
        FileOutputStream outputStream = new FileOutputStream(newDownloadFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, length);
        inputStream.close();
        outputStream.close();
    }

    public static void copy(File firstFile, File secondFile) throws IOException {
        FileInputStream inputStream = new FileInputStream(firstFile);
        FileOutputStream outputStream = new FileOutputStream(secondFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, length);
        inputStream.close();
        outputStream.close();
    }

    public static String getServiceProvider(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }
}

