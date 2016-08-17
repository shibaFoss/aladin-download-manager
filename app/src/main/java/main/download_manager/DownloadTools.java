package main.download_manager;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import libs.async_job.AsyncJob;

public final class DownloadTools {

    private static DecimalFormat decimalFormat = new DecimalFormat("##.##");

    public static String getFormattedPercentage(DownloadModel model) {
        return decimalFormat.format(model.downloadPercentage);
    }

    public static String getFormatted(double input) {
        return decimalFormat.format(input);
    }

    public static DownloadPart getDownloadPart(DownloadTask downloadTask) {
        return new DownloadPart(downloadTask) {

            @Override
            public File getOriginalFile() {
                return downloadTask.getDestinationFile();
            }

            @Override
            public String getFileUrl() {
                return downloadTask.model.fileUrl;
            }
        };
    }

    public static int getFileSize(final URL url) {
        final int[] resultFileSize = new int[2];
        AsyncJob.doInBackground(new AsyncJob.BackgroundJob() {
            @Override
            public void doInBackground() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("HEAD");
                    conn.getInputStream();
                    resultFileSize[0] = conn.getContentLength();
                } catch (IOException error) {
                    error.printStackTrace();
                    resultFileSize[0] = -1;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        });
        return resultFileSize[0];
    }


    public static int getSmartNumberOfDownloadParts(long totalFileLength) {
        int numberOfParts;

        long _1MB = 1000000;
        long _5MB = 1000000 * 5;
        long _10MB = 1000000 * 10;
        long _50MB = 1000000 * 50;
        long _100MB = 1000000 * 100;

        if (totalFileLength < _1MB) numberOfParts = 1;
        else if (totalFileLength < _5MB) numberOfParts = 2;
        else if (totalFileLength < _10MB) numberOfParts = 3;
        else if (totalFileLength < _50MB) numberOfParts = 9;
        else if (totalFileLength < _100MB) numberOfParts = 12;
        else numberOfParts = 12;

        return numberOfParts;
    }

    public static long getChunkSize(boolean isResumeSupport, long totalFileLength, int numberOfParts) {
        if (isResumeSupport) {
            return (totalFileLength / numberOfParts);
        } else {
            return totalFileLength;
        }
    }

    public static long getAvailableStorageSpace(File directory) {
        try {
            if (!directory.isDirectory()) {
                throw new Exception("The directory is not valid directory.");
            }
            return directory.getFreeSpace();
        } catch (Exception error) {
            error.printStackTrace();
            return 0;
        }
    }

    public static String calculateTime(long millis) {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        if (hour == 0)
            return minute + ":" + second + "";
        if (minute == 0)
            return second + ":";
        else
            return hour + ":" + minute + ":" + second + "";
    }
}
