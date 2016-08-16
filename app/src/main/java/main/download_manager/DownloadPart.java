package main.download_manager;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;
import async_job.AsyncJob;
import main.app.App;
import main.utilities.LogHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import static async_job.AsyncJob.*;
import static main.utilities.NetworkUtils.isNetworkAvailable;
import static main.utilities.NetworkUtils.isWifiEnabled;

/**
 * DownloadPart is the actual class that do the download process via internet.
 * It read the bytes with a range it has been given by the DownloadTask.
 */
public abstract class DownloadPart {

    private static final String TAG = "DownloadPart";
    private static final int MAX_HTTP_READING_TIMEOUT = 1000 * 30;
    public int partNumber;
    public long downloadedByte;
    public int downloadPercentage;
    //The Field is used to know about the current status of the download.
    public int downloadStatus;
    public boolean isDownloadCanceled;
    public boolean isTerminated;
    public boolean isNetworkConnectionBroke;
    public boolean isOnlyWifiSettingEnabled;
    public Exception downloadError;
    public DownloadTask downloadTask;
    public DownloadStatusListener downloadStatusListener;
    private LogHelper log = new LogHelper(getClass());
    private DownloadPart intenseOfThisClass = DownloadPart.this;
    private long startPoint, chunkSize;
    private String fileUrl;
    private File destinationFile;
    private Vibrator vibrator;

    private App app;

    public DownloadPart(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
        this.app = this.downloadTask.getApp();
        this.downloadStatusListener = downloadTask;
        this.vibrator = (Vibrator) downloadTask.getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Get the original file that will be used for downloading.
     *
     * @return download destination file.
     */
    public abstract File getOriginalFile();

    /**
     * Get the download file ur.
     *
     * @return the Url string.
     */
    public abstract String getFileUrl();

    /**
     * Initialize the class with necessary information.
     *
     * @param partNumber the part number of the class
     * @param startPoint starting point of http reading range
     * @param chunkSize  the ending point of http reading range.
     * @throws Exception the error while initializing.
     */
    public void initialize(int partNumber, long startPoint, long chunkSize) throws Exception {
        log.d("Download Part #" + partNumber + " initializing...");

        this.partNumber = partNumber;
        this.startPoint = startPoint;
        this.chunkSize = chunkSize;

        this.fileUrl = getFileUrl();
        this.destinationFile = getOriginalFile();

        initializeDownloadedByte(partNumber, chunkSize);
    }

    public void setToDefault(DownloadPart downloadPart) {
        downloadPart.isNetworkConnectionBroke = false;
        downloadPart.isTerminated = false;
        downloadPart.isOnlyWifiSettingEnabled = false;
        downloadPart.isDownloadCanceled = false;
    }

    /**
     * Start the download.
     */
    public void startDownload() {
        doInBackground(new BackgroundJob() {
            @Override
            public void doInBackground() {
                try {
                    setToDefault(DownloadPart.this);
                    tryToDownload();
                } catch (Exception error) {
                    error.printStackTrace();
                    cancelDownload();
                }
            }
        });
    }

    /**
     * Cancel the download manually.
     */
    public void cancelDownload() {
        this.isDownloadCanceled = true;
        this.downloadStatus = DownloadStatus.CANCELED;
    }

    /**
     * The function checks for all the necessary staff and the settings.
     * If all are correctly configured then it starts the actual download.
     */
    private void tryToDownload() {
        if (downloadedByte >= chunkSize) {
            updateDownloadStatus(DownloadStatus.COMPLETE);
            downloadStatusListener.onComplete(intenseOfThisClass);
        } else {
            //start the download..
            if (!isDownloadCanceled) {
                download();
            }
        }
    }

    /**
     * The Function gets the downloaded byte history from the download model.
     */
    private void initializeDownloadedByte(int partNumber, long chunkSize) {
        DownloadModel downloadModel = downloadTask.model;

        if (downloadModel.downloadPartTotalByteWrite != null) {
            downloadedByte = downloadModel.downloadPartTotalByteWrite[partNumber];
            downloadPercentage = (int) ((downloadedByte * 100) / chunkSize);
        }
    }

    /**
     * This function do the actual download.
     */
    private void download() {
        HttpURLConnection urlConnection;
        InputStream inputStream;
        URL url;

        try {
            validateDownloadSettings();

            url = new URL(this.fileUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            //make the range depending on the downloadedByte's length.
            String range = generateConnectionRange();

            //configured the url connection.
            configureConnection(urlConnection, range);

            //connect
            urlConnection.connect();

            //update status to PENDING
            updateDownloadStatus(DownloadStatus.PENDING);

            //get the input stream.
            inputStream = urlConnection.getInputStream();

            RandomAccessFile randomAccessFile = new RandomAccessFile(destinationFile, "rw");
            randomAccessFile.seek(startPoint + downloadedByte);

            int bufferSize = app.getUserSettings().getBufferSize();
            log.d("Buffer Size : " + (bufferSize / 1024));

            byte buffer[] = new byte[bufferSize];
            long lastTimeByteCount = downloadedByte;
            int byteCount;

            while (!isDownloadCanceled && (byteCount = inputStream.read(buffer)) != -1) {
                downloadedByte += byteCount;

                if (downloadedByte <= chunkSize) {
                    randomAccessFile.write(buffer, 0, byteCount);

                } else if (downloadedByte > chunkSize) {
                    int remainingByteToFinish = (int) (chunkSize - lastTimeByteCount);
                    randomAccessFile.write(buffer, 0, remainingByteToFinish);
                    break;
                }

                lastTimeByteCount = downloadedByte;
                downloadPercentage = (int) ((downloadedByte * 100) / chunkSize);

                if (isTerminated) break;
            }

            inputStream.close();
            randomAccessFile.close();

            //disconnect the url connection.
            urlConnection.disconnect();

            if (!isDownloadCanceled) {
                updateDownloadStatus(DownloadStatus.COMPLETE);
                this.downloadStatusListener.onComplete(this);

            } else if (isTerminated) {
                throw new Exception("The download task has been terminated automatically.");

            } else {
                throw new Exception("The download task has been canceled manually by user.");
            }

        } catch (Exception error) {
            error.printStackTrace();
            downloadError = error;
            updateDownloadStatus(DownloadStatus.CANCELED);
            this.downloadStatusListener.onTerminate(this);
        }
    }

    public void validateDownloadSettings() {
        doInBackground(new BackgroundJob() {
            @Override
            public void doInBackground() {
                if (!isNetworkAvailable(downloadTask.getContext())) {
                    isNetworkConnectionBroke = true;
                    isTerminated = true;
                }

                if (app.getUserSettings().isOnlyWifi()) {
                    AsyncJob.doInMainThread(new MainThreadJob() {
                        @Override
                        public void doInUIThread() {
                            isOnlyWifiSettingEnabled = true;
                            terminateDownloadForOnlyViaWifiSettings();
                        }
                    });
                }
            }
        });
    }

    private void terminateDownloadForOnlyViaWifiSettings() {
        if (!isWifiEnabled(downloadTask.getContext())) {
            vibrator.vibrate(20);
            Toast.makeText(downloadTask.getContext(), "Only via Wifi is turned on.", Toast.LENGTH_SHORT).show();
            downloadTask.cancelDownload();
        }
    }

    private void configureConnection(HttpURLConnection connection, String range) throws IOException {
        connection.setRequestProperty("Range", range);
        connection.setReadTimeout(MAX_HTTP_READING_TIMEOUT);
        connection.setConnectTimeout(MAX_HTTP_READING_TIMEOUT);
    }

    private String generateConnectionRange() {
        return "bytes=" + (startPoint + downloadedByte) + "-";
    }

    private void updateDownloadStatus(int statusCode) {
        this.downloadStatus = statusCode;
    }

    public interface DownloadStatusListener {

        void onTerminate(DownloadPart downloadPart);

        void onComplete(DownloadPart downloadPart);
    }
}
