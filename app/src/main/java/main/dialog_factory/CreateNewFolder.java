package main.dialog_factory;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import main.utilities.DeviceTool;
import main.utilities.UiUtils;
import net.fdm.R;

public class CreateNewFolder {

    private Dialog dialog;
    private Activity activity;
    private String parentPath;
    private OnCreateFolder listener;

    public CreateNewFolder(Activity activity, String parentFile, OnCreateFolder createFolderListener) {
        this.activity = activity;
        this.parentPath = parentFile;
        this.listener = createFolderListener;
        initialize();
    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public void close() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void initialize() {
        dialog();
        views();
    }

    private void views() {
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText(activity.getString(R.string.create_new__folder));

        final EditText fileName = (EditText) dialog.findViewById(R.id.fileNameEdit);

        TextView createButton = (TextView) dialog.findViewById(R.id.okButton);
        createButton.setText(activity.getString(R.string.create));

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
                DeviceTool.mkdirs(parentPath + "/" + fileName.getText().toString().trim());
                listener.onCreate();
            }
        });

        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

    }

    private void dialog() {
        dialog = UiUtils.generateNewDialog(activity, R.layout.file_name_edit_dialog);
    }

    public interface OnCreateFolder {
        void onCreate();
    }

}
