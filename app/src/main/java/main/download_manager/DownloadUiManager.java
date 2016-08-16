package main.download_manager;

import android.app.NotificationManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import async_job.AsyncJob.MainThreadJob;
import main.app.App;
import main.dialog_factory.MessageBox;
import main.screens.main_screen.MainScreen;
import main.screens.main_screen.downloads_screen.CompleteDownloadListAdapter;
import main.screens.main_screen.downloads_screen.CompleteDownloadScreen;
import main.screens.main_screen.downloads_screen.DownloadRefreshLinker;
import main.screens.main_screen.downloads_screen.RunningDownloadScreen;
import main.utilities.DeviceTool;
import net.fdm.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static async_job.AsyncJob.doInMainThread;
import static main.utilities.FileCatalog.getDownloadDrawableBy;

/**
 * This class adds, removes & updates the layout views of all of the incomplete/running
 * and completed download tasks.
 * <br>
 * {@link RunningDownloadScreen} and the {@link CompleteDownloadScreen} injects their layout container
 * via {@link #injectDownloadRunningScreen(RunningDownloadScreen)}
 * and {@link #injectCompleteDownloadListAdapter(CompleteDownloadListAdapter)}
 * <br/>
 * <p/>
 * You can get a instance of this class from {@link DownloadSystem#getDownloadUiManager()}
 *
 * @author shibaprasad
 * @version 1.4
 * @since april 2015
 */
public final class DownloadUiManager implements View.OnClickListener {

    private App app;
    private NotificationManager notificationManager;
    private DownloadSystem downloadSystem;
    private ArrayList<LinearLayout> incompleteDownloadLayouts;

    private CompleteDownloadListAdapter completeDownloadListAdapter;
    private RunningDownloadScreen runningDownloadScreen;
    private LinearLayout layoutContainerOfIncompleteTasks;

    //The decimal formatter is used for formatting the percentage of download tasks.
    private DecimalFormat decimalFormat = new DecimalFormat("##.##");

    /**
     * Don't create new instance of this class. Use
     * {@link DownloadSystem#getDownloadUiManager()} method.
     *
     * @param downloadSystem this class uses the DownloadSystem to
     */
    public DownloadUiManager(DownloadSystem downloadSystem) {
        this.downloadSystem = downloadSystem;
        this.app = downloadSystem.getApp();
        this.notificationManager = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
        this.initialize();
    }

    /**
     * Inject the reference of {@link CompleteDownloadListAdapter} to this class. So that we can use it for
     * updating its list of the complete download tasks.
     *
     * @param listAdapter the completeDownloadListAdapter.
     */
    public void injectCompleteDownloadListAdapter(CompleteDownloadListAdapter listAdapter) {
        this.completeDownloadListAdapter = listAdapter;
        this.completeDownloadListAdapter.setDataList(downloadSystem.getTotalCompleteDownloadModels());
        updateCompleteDownloadList();
    }

    /**
     * Inject the layoutContainer that will holds the incomplete download layouts.
     *
     * @param downloadListContainer the running fragment
     */
    public void injectInCompleteDownloadLayoutContainer(LinearLayout downloadListContainer) {
        this.layoutContainerOfIncompleteTasks = downloadListContainer;
    }

    /**
     * Same as {@link #addIncompleteDownloadLayout(LinearLayout, DownloadModel)}
     * But this method create a new layout before calling its brother method.
     *
     * @param downloadModel the downloadModel will be used for getting the download progress information.
     */
    public void addIncompleteDownloadLayout(DownloadModel downloadModel) {
        LinearLayout downloadView = generateDownloadLayout(LayoutInflater.from(app));
        addIncompleteDownloadLayout(downloadView, downloadModel);
    }


    /**
     * RunningFragment runs on main thread that is responsible for showing views to user. We need a reference
     * of this class so that we can use it for doing ui related work from this background thread.
     *
     * @param runningDownloadScreen reference of running fragment.
     */
    public void injectDownloadRunningScreen(RunningDownloadScreen runningDownloadScreen) {
        this.runningDownloadScreen = runningDownloadScreen;
    }

    /**
     * Add a new incomplete download layout to the {@link #incompleteDownloadLayouts}.
     *
     * @param downloadLayout layout that will be used for showing the download progress.
     * @param downloadModel  the downloadModel will be used for getting the download information.
     */
    public void addIncompleteDownloadLayout(LinearLayout downloadLayout, DownloadModel downloadModel) {
        if (incompleteDownloadLayouts != null)
            if (downloadLayout != null) {
                this.configureDownloadLayout(downloadLayout, downloadModel);
                this.incompleteDownloadLayouts.add(downloadLayout);
                this.notifyDownloadDataChange();
            }
    }

    /**
     * Remove a incomplete download layout from the {@link #incompleteDownloadLayouts}
     *
     * @param index the index of the removal download layout.
     */
    public void removeIncompleteDownloadLayout(int index) {
        if (this.incompleteDownloadLayouts != null) {
            final LinearLayout downloadLayout = this.incompleteDownloadLayouts.remove(index);

            if (downloadLayout != null) {
                if (this.layoutContainerOfIncompleteTasks != null) {
                    doInMainThread(new MainThreadJob() {
                        @Override
                        public void doInUIThread() {
                            layoutContainerOfIncompleteTasks.removeView(downloadLayout);
                            notifyDownloadDataChange();
                        }
                    });
                }
            }
        }
    }

    /**
     * Update the complete download task list.
     */
    public void updateCompleteDownloadList() {
        if (this.completeDownloadListAdapter != null) {
            doInMainThread(new MainThreadJob() {
                @Override
                public void doInUIThread() {
                    completeDownloadListAdapter.notifyDataChange();
                }
            });
        }
    }

    /**
     * This method updates the {@link #layoutContainerOfIncompleteTasks}
     * with {@link #incompleteDownloadLayouts} from the scratch.
     * <p/>
     * This method is very important when something goes wrong or the array data has been change
     * and the container did not get the change to update itself with new array items.
     */
    public void notifyDownloadDataChange() {
        if (layoutContainerOfIncompleteTasks != null) {
            doInMainThread(new MainThreadJob() {
                @Override
                public void doInUIThread() {
                    layoutContainerOfIncompleteTasks.removeAllViews();
                    addAllIncompleteDownloadViewsToDownloadContainer();
                }
            });
        }
    }

    /**
     * This method gets a callback when a downloadLayout is clicked by a user.
     *
     * @param downloadLayout the downloadLayout which got the clicked.
     */
    @Override
    public void onClick(View downloadLayout) {
        if (downloadLayout.getId() == R.id.download_view) {
            clickEventOf(downloadLayout);
        }
    }

    private void clickEventOf(View downloadLayout) {
        LinearLayout downloadView = (LinearLayout) downloadLayout.getParent();

        //Get the layout id of the  downloadLayout.
        int index = (downloadView != null) ? downloadView.getId() : -1;

        //Check if the running fragment is alive or not.
        if (this.runningDownloadScreen != null) {
            if (index != -1) {
                DownloadModel downloadModel = downloadSystem.getTotalIncompleteDownloadModels().get(index);
                if (downloadModel != null) {
                    runningDownloadScreen.onClickDownloadItemView(downloadLayout, downloadModel);
                }
            }
        }
    }

    //Update the downloadLayout wth the given downloadModel.
    public void updateDownloadProgressWith(DownloadModel downloadModel) {
        //the download view container from the running download screen is not alive,
        // so we need to terminate further execution of this function.
        if (layoutContainerOfIncompleteTasks == null) return;

        if (incompleteDownloadLayouts.size() > 0) {
            if (downloadModel != null) {
                int downloadTaskPosition = downloadSystem.getTotalIncompleteDownloadModels().indexOf(downloadModel);

                //index must be greater than -1
                if (downloadTaskPosition > -1) {

                    LinearLayout downloadLayout = incompleteDownloadLayouts.get(downloadTaskPosition);
                    //The downloadLayout of the specific downloadModel is alive,
                    //now we can update it with new updated information.
                    if (downloadLayout != null) configureDownloadLayout(downloadLayout, downloadModel);
                }
            }
        }
    }

    /**
     * This is the main method that initialize all the necessary things to start this class.
     */
    private void initialize() {
        this.incompleteDownloadLayouts = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(app);
        this.initializeIncompleteDownloadTaskViews(inflater);
        this.updateCompleteDownloadList();
    }

    /**
     * Initialize and generate the downloadLayouts from the incomplete download models of {@link DownloadSystem}.
     *
     * @param inflater the inflater that inflate the download view from the xml layout.
     */
    private void initializeIncompleteDownloadTaskViews(LayoutInflater inflater) {
        for (DownloadModel downloadModel : downloadSystem.getTotalIncompleteDownloadModels()) {
            LinearLayout downloadView = generateDownloadLayout(inflater);
            addIncompleteDownloadLayout(downloadView, downloadModel);
        }
    }

    /**
     * Generate a fresh inflated view object from the layout.
     *
     * @param inflater the inflater reference object that will be needed for inflating the xml layout.
     * @return the inflated linear Layout object. (Download View)
     */
    private LinearLayout generateDownloadLayout(LayoutInflater inflater) {
        LinearLayout downloadView = (LinearLayout) inflater.inflate(R.layout.running_download_list_row, null);
        downloadView.findViewById(R.id.download_view).setOnClickListener(this);
        return downloadView;
    }


    private void configureDownloadLayout(LinearLayout downloadView, DownloadModel downloadModel) {
        TextView title;
        TextView downloadInfo;
        ProgressBar[] progressBars;

        title = (TextView) downloadView.findViewById(R.id.file_name);
        downloadInfo = (TextView) downloadView.findViewById(R.id.download_info);
        progressBars = new ProgressBar[12];

        initDownloadProgress(downloadView, progressBars);

        title.setText(downloadModel.fileName);
        downloadInfo.setText(generateDownloadInfo(downloadModel) + " " + downloadModel.extraText);

        int statusIcon = R.drawable.ic_resume;
        int formatIcon = getDownloadDrawableBy(downloadModel.fileName);

        if (downloadModel.isRunning) {
            statusIcon = R.drawable.ic_pause;
        } else {
            if (notificationManager != null) notificationManager.cancel(downloadModel.id);
        }
        title.setCompoundDrawablesWithIntrinsicBounds(statusIcon, 0, formatIcon, 0);

        if (downloadModel.partProgressPercentage != null) {
            for (int index = 0; index < downloadModel.partProgressPercentage.length; index++) {
                int percentage = downloadModel.partProgressPercentage[index];

                if (progressBars[index].getVisibility() != View.VISIBLE)
                    progressBars[index].setVisibility(View.VISIBLE);

                progressBars[index].setProgress(percentage);
            }
        }
    }

    /**
     * This is a simple method that initialize all the progressBar View fom the DownloadView layout.
     *
     * @param downloadView the download view layout
     * @param progressBars the array of progress bar objects.
     */
    private void initDownloadProgress(LinearLayout downloadView, ProgressBar[] progressBars) {
        if (progressBars == null) return;
        progressBars[0] = (ProgressBar) downloadView.findViewById(R.id.progressBar0);
        progressBars[1] = (ProgressBar) downloadView.findViewById(R.id.progressBar1);
        progressBars[2] = (ProgressBar) downloadView.findViewById(R.id.progressBar2);
        progressBars[3] = (ProgressBar) downloadView.findViewById(R.id.progressBar3);
        progressBars[4] = (ProgressBar) downloadView.findViewById(R.id.progressBar4);
        progressBars[5] = (ProgressBar) downloadView.findViewById(R.id.progressBar5);
        progressBars[6] = (ProgressBar) downloadView.findViewById(R.id.progressBar6);
        progressBars[7] = (ProgressBar) downloadView.findViewById(R.id.progressBar7);
        progressBars[8] = (ProgressBar) downloadView.findViewById(R.id.progressBar8);
        progressBars[9] = (ProgressBar) downloadView.findViewById(R.id.progressBar9);
        progressBars[10] = (ProgressBar) downloadView.findViewById(R.id.progressBar10);
        progressBars[11] = (ProgressBar) downloadView.findViewById(R.id.progressBar11);
    }

    /**
     * Generate the download info string from the model.
     *
     * @param downloadModel the download model.
     * @return the generating string.
     */
    private String generateDownloadInfo(DownloadModel downloadModel) {
        return DeviceTool.humanReadableSizeOf(downloadModel.totalByteWroteToDisk) + "/" +
                DeviceTool.humanReadableSizeOf(downloadModel.totalFileLength) + "    " +
                //set the remaining and total downloaded time.
                (downloadModel.isRunning && !downloadModel.isWaitingForNetwork ? decimalFormat.format(downloadModel.downloadPercentage) + "%   " +
                        downloadModel.networkSpeed + "   " + downloadModel.remainingTime : " ");
    }

    /**
     * Add all the download views of incomplete task to the download container linear layout from
     * runningDownloadScreen.
     */
    private void addAllIncompleteDownloadViewsToDownloadContainer() {
        if (incompleteDownloadLayouts.size() > 0) {
            for (int index = 0; index < incompleteDownloadLayouts.size(); index++) {
                //get the download view from the array and set the layout id by the array index of the view item.
                LinearLayout downloadView = incompleteDownloadLayouts.get(index);
                downloadView.setId(index);

                //sometime after updating the view container layout the new parent layout refuses to add the
                //download view due to the previous parent layout view. so we need to remove the download view from
                //the parent class.
                ViewGroup parent = (ViewGroup) downloadView.getParent();
                if (parent != null) {
                    parent.removeView(downloadView);
                }

                layoutContainerOfIncompleteTasks.addView(downloadView);
            }
        }
    }

    public void openReplaceLinkAdvise(final DownloadModel model) {
        if (model != null) {
            if (runningDownloadScreen != null) {
                doInMainThread(new MainThreadJob() {
                    @Override
                    public void doInUIThread() {
                        final MainScreen mainScreen = runningDownloadScreen.mainScreen;
                        mainScreen.vibrator.vibrate(30);
                        MessageBox messageBox = MessageBox.getInstance(mainScreen);
                        messageBox.setOkButtonText("Replace File Link");
                        messageBox.setMessage(mainScreen.getString(R.string.outdated_link_dialog_message));
                        messageBox.clickListener = new MessageBox.ClickListener() {
                            @Override
                            public void onClick(View view) {
                                DownloadRefreshLinker downloadRefreshLinker = new DownloadRefreshLinker(mainScreen);
                                downloadRefreshLinker.registerOnSaveListener(new DownloadRefreshLinker.OnSaveListener() {

                                    @Override
                                    public void onSave(DownloadRefreshLinker downloadRefreshLinker, String refreshedLink) {
                                        downloadRefreshLinker.close();
                                        model.fileUrl = refreshedLink;
                                        model.updateDataInDisk();
                                    }
                                });

                                downloadRefreshLinker.setDownloadModel(model);
                                downloadRefreshLinker.show();
                            }
                        };

                        messageBox.show();
                    }
                });
            }
        }
    }
}
