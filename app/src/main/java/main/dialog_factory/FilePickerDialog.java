package main.dialog_factory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.fdm.R;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import libs.async_job.AsyncJob;
import main.app.App;
import main.utilities.LogHelper;
import main.utilities.UiUtils;

/**
 * FilePickerDialog class shows a directory chooser dialog to user. Basically it just lists all folders of the user's
 * device sdcard and give the user the ability to select any folder where he/she want to store his/her all downloads files.
 *
 * @author shibaprasad
 */
public final class FilePickerDialog implements View.OnClickListener {

    private static LogHelper LOG = new LogHelper(FilePickerDialog.class);

    public Dialog folderPickerDialog;
    public OnSelectListener onPathSelectListener;
    public ListView listView;
    public TextView title, cancelButton, newButton, selectButton, upButton;
    private Activity activity;
    private LinkedList<File> directoryList = new LinkedList<>();

    public FilePickerDialog(Activity activity) {
        this.activity = activity;
        this.init();

        App app = (App) activity.getApplication();
        loadDefaultDownloadPath(app.getUserSettings().getDownloadPath());
    }

    private void init() {
        initDialog();
        initViews();
        setOnClickEventListener(new View[]{cancelButton, selectButton, upButton, newButton});
    }

    private void loadDefaultDownloadPath(String downloadPath) {
        try {
            loadChildDirectoriesFrom(new File(downloadPath));
        } catch (Exception error) {
            error.printStackTrace();
            showMessage("Can not open the path.");
        }
    }

    private void initDialog() {
        folderPickerDialog = UiUtils.generateNewDialog(activity, R.layout.folder_picker_layout_dialog);
    }

    private void initViews() {
        title = (TextView) folderPickerDialog.findViewById(R.id.title);
        initListView();
        initButtonViews();

    }

    private void setOnClickEventListener(View[] buttons) {
        for (View view : buttons) {
            view.setOnClickListener(this);
        }
    }

    private void loadChildDirectoriesFrom(final File file) throws Exception {
        AsyncJob.doInBackground(new AsyncJob.BackgroundJob() {
            @Override
            public void doInBackground() {
                File[] childFolders = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                });

                if (childFolders != null) {
                    directoryList = new LinkedList<>(Arrays.asList(childFolders));
                    Collections.sort(directoryList);
                }

                AsyncJob.doInMainThread(new AsyncJob.MainThreadJob() {
                    @Override
                    public void doInUIThread() {
                        title.setText(file.getPath());
                        BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void showMessage(String message) {
        MessageBox messageBox = new MessageBox(activity);
        messageBox.setMessage(message);
        messageBox.show();
    }

    private void initListView() {
        listView = (ListView) folderPickerDialog.findViewById(R.id.file_list);
        listView.setAdapter(getCustomListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int directoryIndex, long id) {
                loadSelectedDirectoryFromList(directoryIndex);
            }
        });
    }

    private void initButtonViews() {
        newButton = (TextView) folderPickerDialog.findViewById(R.id.new_file);
        selectButton = (TextView) folderPickerDialog.findViewById(R.id.ok);
        cancelButton = (TextView) folderPickerDialog.findViewById(R.id.cancel_bnt);
        upButton = (TextView) folderPickerDialog.findViewById(R.id.upButton);
    }

    private BaseAdapter getCustomListAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return getDirectoryList().size();
            }

            @Override
            public Object getItem(int index) {
                return getDirectoryList().get(index);
            }

            @Override
            public long getItemId(int index) {
                return index;
            }

            @SuppressLint("ViewHolder")
            @Override
            public View getView(int index, View view, ViewGroup viewGroup) {
                view = LayoutInflater.from(activity).inflate(R.layout.folder_list_row_layout, null);
                TextView title = ((TextView) view.findViewById(R.id.title));

                String fileName = getDirectoryList().get(index).getName();
                title.setText(fileName);
                return view;
            }
        };
    }

    private void loadSelectedDirectoryFromList(int directoryIndex) {
        try {
            File file = directoryList.get(directoryIndex);
            if (file.canExecute()) {
                loadChildDirectoriesFrom(file);
            } else {
                throw new Exception("");
            }
        } catch (Exception error) {
            error.printStackTrace();
            showMessage("Can not open this folder. May be it is protected by the file system.");
        }
    }

    private List<File> getDirectoryList() {
        return directoryList;
    }

    public FilePickerDialog(Activity activity, String loadDirectoryPath) {
        this.activity = activity;
        this.init();
        loadDefaultDownloadPath(loadDirectoryPath);
    }

    public void show() {
        try {
            if (folderPickerDialog != null) {
                folderPickerDialog.show();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == cancelButton.getId()) {
            close();

        } else if (view.getId() == selectButton.getId()) {
            selectCurrentTitleDirectory();

        } else if (view.getId() == upButton.getId()) {
            goUpFromCurrentTitleDirectory();

        } else if (view.getId() == newButton.getId()) {
            createNewFolder();

        }
    }

    public void close() {
        if (folderPickerDialog != null)
            folderPickerDialog.dismiss();
    }

    private void selectCurrentTitleDirectory() {
        if (isCurrentTitleDirWritable()) {
            close();
            if (onPathSelectListener != null)
                onPathSelectListener.onSelect(this, getTitlePath());
        } else {
            showMessage("Can not select the folder. " +
                    "The Folder is protected from write permission by the file system.");
        }
    }

    private void goUpFromCurrentTitleDirectory() {
        try {
            File file = new File(getTitlePath()).getParentFile();
            if (file.canExecute())
                loadChildDirectoriesFrom(file);
            else
                throw new Exception("");
        } catch (Exception error) {
            error.printStackTrace();
            showMessage("Can not go back. May be this is the root directory of the file system.");
        }
    }

    private void createNewFolder() {
        new CreateNewFolder(activity, getTitlePath(), new CreateNewFolder.OnCreateFolder() {
            @Override
            public void onCreate() {
                try {
                    loadChildDirectoriesFrom(new File(getTitlePath()));
                } catch (Exception error) {
                    error.printStackTrace();
                    LOG.e(error.getMessage());
                    close();
                    showMessage("Something gone wrong, We will fix it very soon.");
                }
            }
        }).show();
    }

    private boolean isCurrentTitleDirWritable() {
        return new File(getTitlePath()).canWrite();
    }

    private String getTitlePath() {
        return title.getText().toString();
    }

    public interface OnSelectListener {
        void onSelect(FilePickerDialog filePickerDialog, String path);
    }

}

