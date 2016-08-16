package main.screens.main_screen.setting_screen;

import android.view.View;
import android.widget.RadioButton;
import main.screens.BaseScreen;
import main.settings.UserSettings;
import net.fdm.R;

/**
 * The class helps user to change the progress time interval
 * of every downlaod tasks.
 *
 * @author Shiba Prasad Jana
 */
public class UiProgressIntervalEditor
        extends BaseDialogBuilder implements View.OnClickListener {

    public RadioButton _1sec, _2sec, _3sec;
    private UserSettings userSettings;
    private BaseScreen baseScreen;

    public UiProgressIntervalEditor(BaseScreen baseScreen) {
        super(baseScreen, R.layout.download_progress_timer_dialog);

        this.baseScreen = baseScreen;
        this.userSettings = this.baseScreen.app.getUserSettings();

        init();
        update();
    }

    /**
     * Show the dialog to user.
     */
    public void show() {
        dialog.show();
    }

    /**
     * Close the dialog.
     */
    public void close() {
        dialog.dismiss();
    }

    /**
     * Update information status on views depending on the new user setting's change.
     */
    private void update() {
        int intervalTime = baseScreen.app.getUserSettings().getProgressTimer();

        if (intervalTime == 1) _1sec.setChecked(true);
        else if (intervalTime == 2) _2sec.setChecked(true);
        else if (intervalTime == 3) _3sec.setChecked(true);
    }

    /**
     * Init the dialog and the views.
     */
    private void init() {
        _1sec = (RadioButton) dialog.findViewById(R.id._1);
        _2sec = (RadioButton) dialog.findViewById(R.id._2);
        _3sec = (RadioButton) dialog.findViewById(R.id._3);

        _1sec.setOnClickListener(this);
        _2sec.setOnClickListener(this);
        _3sec.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        close();
        if (view.getId() == _1sec.getId()) userSettings.setProgressTimer(1);
        else if (view.getId() == _2sec.getId()) userSettings.setProgressTimer(2);
        else if (view.getId() == _3sec.getId()) userSettings.setProgressTimer(3);
    }

}
