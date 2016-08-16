package main.dialog_factory;

import android.app.Activity;
import android.app.Dialog;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import main.utilities.UiUtils;
import net.fdm.R;

public final class MessageBox implements View.OnClickListener {

    public ClickListener clickListener;
    private Activity activity;

    private Dialog dialog;
    private TextView message;
    private TextView okBnt;

    public MessageBox(Activity activity) {
        this.activity = activity;
        initialize();
    }

    public static MessageBox getInstance(Activity activity) {
        return new MessageBox(activity);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setHtmlMessage(Spanned message) {
        this.message.setText(message);
        this.message.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setOkButtonText(String text) {
        if (okBnt != null)
            okBnt.setText(text);
    }

    public void setTitle(String title) {
        dialog.findViewById(R.id.titleContainerLayout).setVisibility(View.VISIBLE);
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setText(title);
    }

    public void show() {
        try {
            dialog.show();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void close() {
        if (dialog != null)
            if (dialog.isShowing()) dialog.dismiss();
    }

    private void initialize() {
        dialog = UiUtils.generateNewDialog(activity, R.layout.message_box_dialog);
        message = (TextView) dialog.findViewById(R.id.message_edit);
        okBnt = (TextView) dialog.findViewById(R.id.ok);

        registerClickEvent(new View[]{okBnt});
    }


    private void registerClickEvent(View[] views) {
        for (View view : views)
            view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == okBnt) {
            dialog.dismiss();
            if (clickListener != null)
                clickListener.onClick(view);
        }
    }

    public interface ClickListener {
        void onClick(View view);
    }

}
