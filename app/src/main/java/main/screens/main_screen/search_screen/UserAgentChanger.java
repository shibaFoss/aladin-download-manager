package main.screens.main_screen.search_screen;

import android.app.Dialog;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import main.utilities.UiUtils;
import net.fdm.R;

public class UserAgentChanger {

    private Dialog dialog;
    private SearchScreen searchScreen;

    public UserAgentChanger(SearchScreen searchScreen) {
        this.searchScreen = searchScreen;
        init();
    }

    public String getUserAgentString(WebView webView) {
        return webView.getSettings().getUserAgentString();
    }

    public void show() {
        dialog.show();
    }

    public void close() {
        dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(searchScreen.getMainScreen(), R.layout.user_agent_dialog);
        final EditText userAgentString;
        TextView saveButton;
        TextView cancelButton;
        TextView defaultButton;
        TextView settingHint;

        cancelButton = (TextView) dialog.findViewById(R.id.cancelButton);
        defaultButton = (TextView) dialog.findViewById(R.id.middle_bnt);
        saveButton = (TextView) dialog.findViewById(R.id.okButton);
        userAgentString = (EditText) dialog.findViewById(R.id.fileNameEdit);
        settingHint = (TextView) dialog.findViewById(R.id.previewTitle);

        defaultButton.setText("Default");
        defaultButton.setVisibility(View.VISIBLE);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String defaultUserAgentSettings = new WebView(
                        searchScreen.getMainScreen()).getSettings().getUserAgentString();
                userAgentString.setText(defaultUserAgentSettings);
            }
        });

        ((TextView) dialog.findViewById(R.id.dis)).setText("Set the default user-agent of the browser.");
        userAgentString.setText(getUserAgentString(searchScreen.webView));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredAgentString = userAgentString.getText().toString();
                if (enteredAgentString.length() > 2) {
                    searchScreen.getMainScreen().app.getUserSettings().setWebUserAgent(enteredAgentString);
                    searchScreen.webView.getSettings().setUserAgentString(enteredAgentString);
                    searchScreen.webView.reload();
                    close();
                } else {
                    searchScreen.getMainScreen().vibrator.vibrate(20);
                    searchScreen.getMainScreen().showSimpleMessageBox("Enter a valid user agent");
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }
}
