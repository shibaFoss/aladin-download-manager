package main.screens.main_screen.downloads_screen;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import main.app.App;
import main.download_manager.DownloadModel;
import main.download_manager.DownloadSystem;
import main.download_manager.DownloadUiManager;
import main.screens.main_screen.BaseNestedScreen;
import main.screens.main_screen.MainScreen;
import net.fdm.R;

public class RunningDownloadScreen extends BaseNestedScreen {

    public MainScreen mainScreen;
    private RunningDownloadOption runningDownloadOption;

    @Override
    protected int getScreenLayout() {
        return R.layout.running_download_screen;
    }

    @Override
    protected void onViewCreating(View view) {
        //nothing to do here.
    }

    @Override
    protected void onAfterLayoutLoad(View runningDownloadScreenLayout, Bundle savedInstanceState) {
        this.mainScreen = getMainScreen();
        this.runningDownloadOption = new RunningDownloadOption(this.getMainScreen());


        //the download list item view container.
        LinearLayout downloadsListContainer;
        downloadsListContainer = (LinearLayout) runningDownloadScreenLayout
                .findViewById(R.id.download_list_container_linear_layout);
        downloadsListContainer.removeAllViews();

        //inject the download list item container view to the download-ui-manager.
        App app = (App) getActivity().getApplication();
        DownloadSystem downloadSystem = app.getDownloadSystem();
        downloadSystem.getDownloadUiManager().injectInCompleteDownloadLayoutContainer(downloadsListContainer);
        downloadSystem.getDownloadUiManager().injectDownloadRunningScreen(this);
        downloadSystem.getDownloadUiManager().notifyDownloadDataChange();

    }


    /**
     * The method will be called by the {@link DownloadUiManager} when a user clicks on a
     * download list item view.
     *
     * @param downloadView  the root layout view of the download view.
     * @param downloadModel the download-model object of that download-task.
     */
    public void onClickDownloadItemView(View downloadView, DownloadModel downloadModel) {
        if (this.runningDownloadOption != null) {
            runningDownloadOption.showOptionsFor(downloadModel);
        }
    }

}