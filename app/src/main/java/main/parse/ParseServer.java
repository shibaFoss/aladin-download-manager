package main.parse;

import android.content.Context;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.Map;

import libs.async_job.AsyncJob.BackgroundJob;
import libs.remember_lib.Remember;
import main.app.App;
import main.download_manager.DownloadModel;
import main.download_manager.DownloadTools;
import main.utilities.DeviceTool;
import main.utilities.DeviceUuidFactory;
import main.utilities.LogHelper;
import main.utilities.UserEmailFetcher;

import static libs.async_job.AsyncJob.doInBackground;

public class ParseServer {

    public static void initialize(App app) {
        Parse.enableLocalDatastore(app);
        Parse.initialize(app, "D7ictnZcoMIfkmlSxMUGICIbqJNy3eJJ0HXF6y61", "krPqGnFPs4a27inm67PlvcriE48iJuXU8NspHqvC");
    }

    public static void sendFeedbackMail(final Map<String, String> param) {
        doInBackground(new BackgroundJob() {
            @Override
            public void doInBackground() {
                try {
                    ParseCloud.callFunctionInBackground("feedbackMail", param, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object response, ParseException e) {
                            Log.e("cloud code example", "response: " + response);
                        }
                    });
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });
    }

    public static void trackDownloadInfo(Context context, DownloadModel downloadModel) {
        try {

            String email[] = UserEmailFetcher.getEmail(context);

            ParseObject parseObject = new ParseObject("DownloadTracking");
            parseObject.put("Email", (email != null ? email[2] : ""));
            parseObject.put("DeviceId", new DeviceUuidFactory(context).getDeviceUuid());
            parseObject.put("DeviceName", DeviceTool.getDeviceName());
            parseObject.put("NetworkProvider", DeviceTool.getServiceProvider(context));

            parseObject.put("FileName", downloadModel.fileName);
            parseObject.put("Url", downloadModel.fileUrl);
            parseObject.put("Size", DeviceTool.humanReadableSizeOf(downloadModel.totalFileLength));
            parseObject.put("Time", DownloadTools.calculateTime(downloadModel.totalTime));
            parseObject.put("Speed", downloadModel.networkSpeed);
            parseObject.put("Interval", downloadModel.uiProgressInterval);
            parseObject.put("BufferSize", downloadModel.bufferSize);
            parseObject.put("Parts", downloadModel.numberOfPart);

            parseObject.saveInBackground();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static void firstOpenTrack(Context context) {
        try {
            String[] email = UserEmailFetcher.getEmail(context);
            if (email != null) {
                String type = email[0];
                String accountName = email[1];
                String accountString = email[2];

                ParseObject parseUser = new ParseObject("ParseUser");
                parseUser.put("Type", type);
                parseUser.put("AccountName", accountName);
                parseUser.put("AccountToString", accountString);
                parseUser.saveInBackground();
                Remember.putBoolean("FIRST_VISIT", false);
            }
        } catch (Exception error) {
            error.printStackTrace();
            LogHelper.e(ParseServer.class, error.getMessage());
        }
    }
}
