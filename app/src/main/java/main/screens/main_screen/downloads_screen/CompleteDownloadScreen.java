package main.screens.main_screen.downloads_screen;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import main.download_manager.DownloadModel;
import main.download_manager.DownloadUiManager;
import main.screens.main_screen.BaseNestedScreen;
import net.fdm.R;

public class CompleteDownloadScreen extends BaseNestedScreen {

    private ListView downloadedFileList;
    public CompleteDownloadListAdapter completeDownloadListAdapter;

    @Override
    protected int getScreenLayout() {
        return R.layout.complete_download_screen;
    }

    @Override
    protected void onAfterLayoutLoad(View layoutView, Bundle savedInstanceState) {
        downloadedFileList = (ListView) layoutView.findViewById(R.id.list_view);
        completeDownloadListAdapter = new CompleteDownloadListAdapter(getMainScreen());
        downloadedFileList.setAdapter(completeDownloadListAdapter);

        DownloadUiManager downloadUiManager = getMainScreen().app.getDownloadSystem().getDownloadUiManager();
        downloadUiManager.injectCompleteDownloadListAdapter(completeDownloadListAdapter);

        //set up the click events..
        onDownloadTaskClick();
    }

    @Override
    protected void onViewCreating(View view) {
        //do nothing
    }

    private void onDownloadTaskClick() {
        if (this.downloadedFileList == null) {
            throw new NullPointerException(getClass().getName() + " CompleteDownloadTaskListView is null.");
        }

        onDownloadListClick();
    }

    private void onDownloadListClick() {
        downloadedFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                DownloadModel model = (DownloadModel) downloadedFileList.getAdapter().getItem(position);
                CompleteDownloadOptions completeDownloadOptions = new CompleteDownloadOptions(CompleteDownloadScreen.this);
                completeDownloadOptions.show(model);
            }
        });
    }


}