package main.screens.main_screen.setting_screen;

import android.app.Activity;
import android.app.Dialog;
import main.utilities.UiUtils;

public abstract class BaseDialogBuilder {

    protected Dialog dialog;
    protected Activity activity;

    public BaseDialogBuilder(Activity activity, int layoutId) {
        this.activity = activity;
        init(layoutId);
    }

    private void init(int layoutId) {
        dialog = UiUtils.generateNewDialog(activity, layoutId);
    }
}
