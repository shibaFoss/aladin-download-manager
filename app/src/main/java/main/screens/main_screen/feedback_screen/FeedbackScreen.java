package main.screens.main_screen.feedback_screen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import com.parse.ParseObject;
import main.dialog_factory.YesNoDialog;
import main.parse.ParseServer;
import main.screens.BaseScreen;
import main.utilities.DeviceTool;
import net.fdm.R;

import java.util.HashMap;
import java.util.Map;

public class FeedbackScreen extends BaseScreen {
    private boolean intentForRequestFeature = false;
    private EditText name, email, feedbackMessage;
    private RadioButton advise, needHelp;

    @Override
    public int getLayout() {
        return R.layout.feedback_screen;
    }

    @Override
    public void onLayoutLoad() {
        Intent intent = getIntent();
        if (intent.getBooleanExtra("request_feature", false)) {
            intentForRequestFeature = true;
        }

        init();
    }

    @Override
    public void onAfterLayoutLoad() {

    }

    @Override
    public void onPauseScreen() {

    }

    @Override
    public void onResumeScreen() {

    }

    @Override
    public void onExitScreen() {
        confirmExit();
    }

    @Override
    public void onClearMemory() {

    }

    @Override
    public void onScreenOptionChange(Configuration configuration) {

    }

    private void init() {
        TextView title = (TextView) findViewById(R.id.tool_bar_title);
        name = (EditText) findViewById(R.id.name_edit);
        email = (EditText) findViewById(R.id.email_edit);
        feedbackMessage = (EditText) findViewById(R.id.feedback_edit);
        feedbackMessage.setSingleLine(false);

        advise = (RadioButton) findViewById(R.id.advise);
        advise = (RadioButton) findViewById(R.id.advise);
        needHelp = (RadioButton) findViewById(R.id.help);

        if (intentForRequestFeature) {
            title.setText("Request new feature");
            advise.setChecked(true);
            feedbackMessage.setHint("Tell us about your new idea.");
        }
    }

    public void onSendClick(View view) {
        if (!validateUserInformation()) return; //user did not write his/her information right.
        sendFeedback();
        vibrator.vibrate(20);
        showSimpleHtmlMessageBox(getFeedbackReplyMessage());

        //clear the inputs
        name.setText("");
        email.setText("");
        feedbackMessage.setText("");
    }

    private void confirmExit() {
        if (feedbackMessage.getText().toString().length() > 0) {
            final YesNoDialog confirmDialog = new YesNoDialog(this);
            confirmDialog.setMessage(getString(R.string.are_you_sure_to_exit_without_send_feedback));
            confirmDialog.clickListener = new YesNoDialog.ClickListener() {
                @Override
                public void onYes(TextView view) {
                    finish();
                }

                @Override
                public void onNo(TextView view) {
                    confirmDialog.close();
                }
            };
            confirmDialog.show();
        } else {
            getInputMethodManager().hideSoftInputFromWindow(email.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            getInputMethodManager().hideSoftInputFromWindow(name.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            getInputMethodManager().hideSoftInputFromWindow(feedbackMessage.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            exit();
        }
    }

    public InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    private boolean validateUserInformation() {
        String user_name = name.getText().toString();
        String user_email = email.getText().toString();
        String feedBack = feedbackMessage.getText().toString();

        if (user_name.equals("") || user_name.length() < 2) {
            showSimpleMessageBox(getString(R.string.enter_your_valid_name));
            return false;
        } else if (user_email.equals("") || user_email.length() < 2 || !user_email.contains("@")) {
            showSimpleMessageBox(getString(R.string.enter_your_valid_email));
            return false;
        } else if (feedBack.length() < 2) {
            showSimpleMessageBox(getString(R.string.enter_your_feedback));
            return false;
        }

        return true;
    }

    private void sendFeedback() {
        try {
            ParseObject parseObject = new ParseObject("Feedback");
            parseObject.put("name", name.getText().toString());
            parseObject.put("email", email.getText().toString());
            parseObject.put("feedback", feedbackMessage.getText().toString());
            parseObject.put("device_info", getDeviceInfo());
            parseObject.saveInBackground();

            Map<String, String> params = new HashMap<>();
            params.put("text",
                    "From : " + name.getText().toString() + "\n" +
                            "Email : " + email.getText().toString() + "\n" +
                            "Device Model : " + getDeviceInfo() + "\n" +
                            "Advise : " + advise.isChecked() + "\n" +
                            "Need Help : " + needHelp.isChecked() + "\n" +
                            "Feedback : " + feedbackMessage.getText().toString());

            params.put("subject",
                    "Feedback of " + name.getText().toString() + " " + getString(R.string.app_name_short) +
                            " version:" + getPackageManager().getPackageInfo(getPackageName(),
                            PackageManager.SIGNATURE_MATCH).versionName);

            params.put("originator", "aladin.support@softc.com");
            params.put("target", "shiba.spj@hotmail.com");
            ParseServer.sendFeedbackMail(params);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private String getFeedbackReplyMessage() {
        return getString(R.string.thank_you) + name.getText().toString() +
                "</b>." + getString(R.string.we_will_review_your_feedback) +
                getString(R.string.for_quick_help_email_us);
    }

    private String getDeviceInfo() {
        return "Device name : " + DeviceTool.getDeviceName();
    }

    public void onBackClick(View view) {
        confirmExit();
    }
}
