package main.screens.main_screen.downloads_screen;

import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import main.app.App;
import main.download_manager.DownloadModel;
import main.download_manager.DownloadSystem;
import main.screens.main_screen.MainScreen;
import main.utilities.UiUtils;
import net.fdm.R;

import java.io.File;

public final class RenameDownloadFile {

    private Dialog dialog;
    private MainScreen mainScreen;
    private DownloadModel downloadModel;

    public RenameDownloadFile(MainScreen mainScreen, DownloadModel downloadModel) {
        this.mainScreen = mainScreen;
        this.downloadModel = downloadModel;
        init();
    }

    public void show() {
        if (dialog != null) dialog.show();
    }

    public void close() {
        if (dialog != null) dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(mainScreen, R.layout.file_name_edit_dialog);

        final TextView title = (TextView) dialog.findViewById(R.id.title);
        final EditText fileName = (EditText) dialog.findViewById(R.id.fileNameEdit);
        final TextView renameButton = (TextView) dialog.findViewById(R.id.okButton);
        final TextView cancelButton = (TextView) dialog.findViewById(R.id.cancelButton);

        title.setText("Rename File Name");
        fileName.setText(downloadModel.fileName);

        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
                rename(fileName.getText().toString().trim());
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }

    private void rename(String newFileName) {
        File file = new File(downloadModel.filePath, downloadModel.fileName);
        file.renameTo(new File(downloadModel.filePath, newFileName));

        downloadModel.deleteFromDisk(DownloadModel.COMPLETE_MODEL);
        downloadModel.fileName = newFileName;
        downloadModel.changeToCompleteModel();

        App app = mainScreen.app;
        DownloadSystem downloadSystem = app.getDownloadSystem();
        downloadSystem.getDownloadUiManager().updateCompleteDownloadList();
    }

}
