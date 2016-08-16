package main.screens.main_screen.setting_screen;

import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import main.settings.UserSettings;
import main.utilities.UiUtils;
import net.fdm.R;

/**
 * UserAgent is a dialog class that gives you user to change the default
 * user agent string for all download tasks.
 */
public class UserAgentEditor {

    private Dialog dialog;
    private SettingScreen settingScreen;

    public UserAgentEditor(SettingScreen settingScreen) {
        this.settingScreen = settingScreen;
        init();
    }

    public void show() {
        dialog.show();
    }

    public void close() {
        dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(settingScreen.getMainScreen(), R.layout.user_agent_dialog);
        final EditText userAgentString;
        TextView saveButton;
        TextView cancelButton;

        cancelButton = (TextView) dialog.findViewById(R.id.cancelButton);
        saveButton = (TextView) dialog.findViewById(R.id.okButton);
        userAgentString = (EditText) dialog.findViewById(R.id.fileNameEdit);

        userAgentString.setText(getUserSettings().getUserAgent() + "");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
                String agentString = userAgentString.getText().toString();
                if (agentString.equals("")) {
                    settingScreen.getMainScreen()
                            .showSimpleMessageBox("No User-Agent found. Future downloads will use default User-Agent of Android.");
                }
                getUserSettings().setUserAgent(agentString);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }

    private UserSettings getUserSettings() {
        return settingScreen.getMainScreen().app.getUserSettings();
    }

}
