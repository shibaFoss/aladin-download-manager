package main.screens.main_screen.setting_screen;

import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import main.screens.BaseScreen;
import main.utilities.UiUtils;
import net.fdm.R;

/**
 * Change the maximum download errors before any downlaod get paused itself.
 */
public class PauseAfterErrorEditor {

    private Dialog dialog;
    private BaseScreen baseScreen;


    public PauseAfterErrorEditor(BaseScreen baseScreen) {
        this.baseScreen = baseScreen;
        init();
    }


    public void show() {
        dialog.show();
    }


    public void close() {
        dialog.dismiss();
    }


    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.pause_after_error_dialog);

        final EditText numberEditor;
        final TextView saveButton;

        numberEditor = (EditText) dialog.findViewById(R.id.number_edit);
        numberEditor.setText(baseScreen.app.getUserSettings().getMaxErrors() + "");

        saveButton = (TextView) dialog.findViewById(R.id.ok_bnt);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
                int maxErrorNumbers = Integer.parseInt("0" + numberEditor.getText().toString());
                baseScreen.app.getUserSettings().setMaxErrors(maxErrorNumbers);
            }
        });


        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel_bnt);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }

}
