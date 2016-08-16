package main.dialog_factory;

import android.app.Activity;
import android.app.Dialog;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;
import main.screens.BaseScreen;
import main.utilities.UiUtils;
import net.fdm.R;

/**
 * The class is used to show users any yes-no related message dialog.
 */
public final class YesNoDialog {

    public ClickListener clickListener;
    public Dialog dialog;

    public TextView message, middleButton, yesButton, noButton;
    private Activity activity;
    private boolean dialogCloseListener;

    /**
     * A static function that returns a complete intense of a {@link YesNoDialog} class.
     */
    public static YesNoDialog getYesNoDialog(BaseScreen baseScreen, final OnYesClick onYesClick) {
        final YesNoDialog yesNoDialog = new YesNoDialog(baseScreen);
        yesNoDialog.clickListener = new ClickListener() {
            @Override
            public void onYes(TextView view) {
                yesNoDialog.close();
                onYesClick.onYesClick(yesNoDialog);
            }

            @Override
            public void onNo(TextView view) {
                yesNoDialog.close();
            }
        };
        return yesNoDialog;
    }

    public YesNoDialog(Activity activity) {
        this.activity = activity;
        init();
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setMessage(Spanned message) {
        this.message.setText(message);
    }

    public void setButtonNames(String yesButton, String noButton) {
        this.yesButton.setText(yesButton);
        this.noButton.setText(noButton);
    }

    public void visibleMiddleButton(String buttonName) {
        this.middleButton.setText(buttonName);
        this.middleButton.setVisibility(View.VISIBLE);
    }

    public void setMiddleButtonClickListener(View.OnClickListener clickListener) {
        this.middleButton.setOnClickListener(clickListener);
    }

    public void show() {
        dialog.show();
    }

    public void close() {
        dialog.dismiss();
    }

    public void setDialogCloseListener(boolean dialogCloseListener) {
        this.dialogCloseListener = dialogCloseListener;
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(activity, R.layout.yes_no_layout_dialog);

        yesButton = (TextView) dialog.findViewById(R.id.yes_bnt);
        middleButton = (TextView) dialog.findViewById(R.id.middle_bnt);
        noButton = (TextView) dialog.findViewById(R.id.cancel_bnt);
        message = (TextView) dialog.findViewById(R.id.message_edit);

        prepareClickEvent();
    }

    private void prepareClickEvent() {
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    if (dialogCloseListener) close();
                    clickListener.onYes((TextView) view);
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    if (dialogCloseListener) close();
                    clickListener.onNo((TextView) view);
                }
            }
        });
    }

    public interface ClickListener {
        void onYes(TextView view);

        void onNo(TextView view);
    }

    public interface OnYesClick {
        void onYesClick(YesNoDialog dialog);
    }
}
