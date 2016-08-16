package main.screens.main_screen.downloads_screen;

import android.app.Activity;
import android.app.Dialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import main.download_manager.DownloadModel;
import main.utilities.DeviceTool;
import main.utilities.UiUtils;
import net.fdm.R;

import static main.download_manager.DownloadTools.calculateTime;

@SuppressWarnings("FieldCanBeLocal")
public class DownloadDetailViewer {

    private Dialog dialog;
    private Activity activity;

    private TextView title, downloadId, fileName, filePath, fileUrl, fileSize, downloadParts, totalTime;

    private DownloadModel downloadModel;

    public DownloadDetailViewer(Activity activity, DownloadModel downloadModel) {
        this.activity = activity;
        this.downloadModel = downloadModel;
        initialize();
        updateInfo();
    }

    public void show() {
        if (dialog != null) dialog.show();
    }

    public void close() {
        if (dialog != null) dialog.dismiss();
    }

    public void updateInfo() {
        downloadId.setText(Html.fromHtml("<b>Download ID : </b>" + downloadModel.id));
        fileName.setText(Html.fromHtml("<b>File Name : </b>" + downloadModel.fileName));
        filePath.setText(Html.fromHtml("<b>File Path : </b>" + downloadModel.filePath));
        fileUrl.setText(Html.fromHtml("<b>File Url : </b><a href=" + downloadModel.fileUrl + ">" + downloadModel.fileUrl + "</a>"));
        fileUrl.setMovementMethod(LinkMovementMethod.getInstance());
        fileSize.setText(Html.fromHtml("<b>File Size : </b>" + DeviceTool.humanReadableSizeOf(downloadModel.totalFileLength)));
        downloadParts.setText(Html.fromHtml("<b>Download Parts : </b>" + downloadModel.numberOfPart));
        totalTime.setText(Html.fromHtml("<b>Total Downloaded Time : </b>" + calculateTime(downloadModel.totalTime)));
    }

    private void initialize() {
        dialog = UiUtils.generateNewDialog(activity, R.layout.download_detail_viewer_dialog);

        title = (TextView) dialog.findViewById(R.id.title);
        downloadId = (TextView) dialog.findViewById(R.id.id);
        fileName = (TextView) dialog.findViewById(R.id.file_name);
        filePath = (TextView) dialog.findViewById(R.id.filePath);
        fileSize = (TextView) dialog.findViewById(R.id.fileSize);
        fileUrl = (TextView) dialog.findViewById(R.id.fileUrl);
        downloadParts = (TextView) dialog.findViewById(R.id.totalDownloadParts);
        totalTime = (TextView) dialog.findViewById(R.id.totalTime);

        TextView closeButton = (TextView) dialog.findViewById(R.id.ok);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }

}
