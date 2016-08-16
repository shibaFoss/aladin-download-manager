package main.screens.main_screen.setting_screen;

import android.view.View;
import android.widget.RadioButton;
import main.screens.BaseScreen;
import main.settings.UserSettings;
import net.fdm.R;

public class RunningDownlaodEditor extends BaseDialogBuilder implements View.OnClickListener {

    public RadioButton _1, _2, _3;
    private UserSettings userSettings;
    private BaseScreen baseScreen;

    public RunningDownlaodEditor(BaseScreen baseScreen) {
        super(baseScreen, R.layout.running_download_dialog);
        this.baseScreen = baseScreen;
        this.userSettings = this.baseScreen.app.getUserSettings();
        views();
        update();
    }

    public void show() {
        try {
            dialog.show();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void close() {
        try {
            dialog.dismiss();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void update() {
        int taskNumber = baseScreen.app.getUserSettings().getMaxRunningTask();

        if (taskNumber == 1)
            _1.setChecked(true);

        else if (taskNumber == 2)
            _2.setChecked(true);

        else if (taskNumber == 3)
            _3.setChecked(true);
    }

    private void views() {
        _1 = (RadioButton) dialog.findViewById(R.id._1);
        _2 = (RadioButton) dialog.findViewById(R.id._2);
        _3 = (RadioButton) dialog.findViewById(R.id._3);

        _1.setOnClickListener(this);
        _2.setOnClickListener(this);
        _3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        close();
        if (view.getId() == _1.getId()) userSettings.setMaxRunningTask(1);
        else if (view.getId() == _2.getId()) userSettings.setMaxRunningTask(2);
        else if (view.getId() == _3.getId()) userSettings.setMaxRunningTask(3);
    }

}
