package main.screens.main_screen.downloads_screen;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import main.download_manager.DownloadTaskEditor;
import main.screens.main_screen.BaseNestedScreen;
import net.fdm.R;

import static main.utilities.Font.titleFont;

/**
 * DownloadScreen the screen that actually holds two different screens one {@link RunningDownloadScreen}
 * two {@link CompleteDownloadScreen}
 */
public class DownloadsScreen extends BaseNestedScreen implements View.OnClickListener {

    public View downloadScreenLayout;
    public TextView title, downloadingButton, downloadedButton;
    public ImageButton addDownloadButton;

    @Override
    protected int getScreenLayout() {
        return R.layout.downloads_screen;
    }

    @Override
    protected void onViewCreating(View view) {
        //nothing to do here.
    }

    @Override
    protected void onAfterLayoutLoad(View layoutView, Bundle savedInstanceState) {
        this.downloadScreenLayout = layoutView;
        initViews(this.downloadScreenLayout);
        initNestedScreenManager();
    }


    public void onClick(View view) {
        if (view.getId() == addDownloadButton.getId()) {
            addNewDownload();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initViews(View downloadScreenLayout) {
        title = (TextView) downloadScreenLayout.findViewById(R.id.title);
        downloadingButton = (TextView) downloadScreenLayout.findViewById(R.id.downloading_bnt);
        downloadedButton = (TextView) downloadScreenLayout.findViewById(R.id.downloaded_bnt);

        addDownloadButton = (ImageButton) downloadScreenLayout.findViewById(R.id.add_download);
        addDownloadButton.setOnClickListener(this);
    }

    private void initNestedScreenManager() {
        DownloadNestedScreenManager manager = new DownloadNestedScreenManager(this);
        manager.setTabButtons(new View[]{downloadingButton, downloadedButton});
    }

    private void addNewDownload() {
        DownloadTaskEditor downloadTaskEditor = new DownloadTaskEditor(getMainScreen());
        downloadTaskEditor.setFileUrl("");
        downloadTaskEditor.setFileName("");
        downloadTaskEditor.show();
    }
}
