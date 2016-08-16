package main.screens.main_screen.search_screen;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.parse.ParseObject;
import main.screens.main_screen.MainScreen;
import main.utilities.UiUtils;
import net.fdm.R;

public class TrendingRequestDialog implements OnClickListener {

    public MainScreen mainScreen;
    public Dialog dialog;
    public EditText webAddress, webTitle, messageBox;
    public TextView title, cancelBnt, sendMessageBnt;

    public TrendingRequestDialog(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.init();
    }

    public TrendingRequestDialog setWebAddress(String webAddress) {
        this.webAddress.setText(webAddress);
        return this;
    }

    public TrendingRequestDialog setWebTitle(String title) {
        webTitle.setText(title);
        return this;
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

    private void init() {
        dialog = UiUtils.generateNewDialog(mainScreen, R.layout.trending_request_dialog);

        title = (TextView) dialog.findViewById(R.id.title);

        webAddress = (EditText) dialog.findViewById(R.id.web_address_edit);
        webTitle = (EditText) dialog.findViewById(R.id.web_title);
        messageBox = (EditText) dialog.findViewById(R.id.message_edit);
        this.webAddress.setEnabled(false);

        cancelBnt = (TextView) dialog.findViewById(R.id.cancel_bnt);
        sendMessageBnt = (TextView) dialog.findViewById(R.id.save_bnt);

        cancelBnt.setOnClickListener(this);
        sendMessageBnt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == cancelBnt.getId()) {
            cancelBnt();
        } else if (view.getId() == sendMessageBnt.getId()) {
            sendMessageBnt();
        }
    }

    private void sendMessageBnt() {
        close();
        ParseObject parseObject = new ParseObject("TrendingRequest");
        parseObject.put("Name", webTitle.getText().toString());
        parseObject.put("Address", webAddress.getText().toString());
        parseObject.put("Message", messageBox.getText().toString());
        parseObject.saveInBackground();
        mainScreen.showSimpleHtmlMessageBox(
                "Thank you for sending us the request. " +
                        "We will shortly review this link and " +
                        "will publish to the <b>Trending</b> zone.");
    }

    private void cancelBnt() {
        close();
    }

}
