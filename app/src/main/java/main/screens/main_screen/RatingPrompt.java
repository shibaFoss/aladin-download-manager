package main.screens.main_screen;

import android.app.Dialog;
import android.view.View;
import android.widget.TextView;
import main.dialog_factory.YesNoDialog;
import main.key_database.KeyStore;
import main.screens.main_screen.feedback_screen.FeedbackScreen;
import main.utilities.UiUtils;
import net.fdm.R;
import remember_lib.Remember;

public class RatingPrompt implements View.OnClickListener {

    private MainScreen mainScreen;
    private Dialog dialog;
    private TextView happy;
    private TextView notHappy;
    private boolean isExitCommand = false;

    public RatingPrompt(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        init();
    }

    public void onClick(View view) {
        if (view.getId() == happy.getId()) {
            happyClick();
        } else if (view.getId() == notHappy.getId()) {
            notHappyClick();
        }
    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public void close() {
        if (dialog != null)
            dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(mainScreen, R.layout.rate_us_dialog);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        happy = (TextView) dialog.findViewById(R.id.happy);
        notHappy = (TextView) dialog.findViewById(R.id.not_happy);

        happy.setOnClickListener(this);
        notHappy.setOnClickListener(this);
    }

    private void happyClick() {
        close();
        final YesNoDialog confirmDialog = new YesNoDialog(mainScreen);
        confirmDialog.clickListener = new YesNoDialog.ClickListener() {
            @Override
            public void onYes(TextView view) {
                confirmDialog.close();
                mainScreen.gotoPlayStore();
                Remember.putBoolean(KeyStore.IS_RATED, true);

                if (isExitCommand)
                    mainScreen.finish();
            }

            @Override
            public void onNo(TextView view) {
                confirmDialog.close();

                if (isExitCommand)
                    mainScreen.finish();
            }
        };

        confirmDialog.setMessage(mainScreen.getString(R.string.how_about_a_rating));
        confirmDialog.yesButton.setText(mainScreen.getString(R.string.ok_sure));
        confirmDialog.noButton.setText(mainScreen.getString(R.string.no_thanks));
        confirmDialog.show();
    }

    private void notHappyClick() {
        close();

        close();
        final YesNoDialog confirmDialog = new YesNoDialog(mainScreen);
        confirmDialog.clickListener = new YesNoDialog.ClickListener() {
            @Override
            public void onYes(TextView view) {
                confirmDialog.close();
                mainScreen.startActivity(FeedbackScreen.class);
                Remember.putBoolean(KeyStore.IS_RATED, true);

                if (isExitCommand)
                    mainScreen.finish();
            }

            @Override
            public void onNo(TextView view) {
                confirmDialog.close();

                if (isExitCommand)
                    mainScreen.finish();
            }
        };

        confirmDialog.setMessage(mainScreen.getString(R.string.would_you_mind_for_feedback));
        confirmDialog.yesButton.setText(mainScreen.getString(R.string.ok_sure));
        confirmDialog.noButton.setText(mainScreen.getString(R.string.no_thanks));
        confirmDialog.show();
    }

    public RatingPrompt setForExit() {
        isExitCommand = true;
        return this;
    }
}
