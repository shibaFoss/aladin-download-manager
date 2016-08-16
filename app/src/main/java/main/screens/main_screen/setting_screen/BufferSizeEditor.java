package main.screens.main_screen.setting_screen;

import android.view.View;
import android.widget.RadioButton;
import main.screens.BaseScreen;
import main.settings.UserSettings;
import net.fdm.R;

public class BufferSizeEditor extends BaseDialogBuilder implements View.OnClickListener {

    public RadioButton kb1, kb2, kb4, kb8, kb16;
    private UserSettings userSettings;
    private BaseScreen baseScreen;

    public BufferSizeEditor(BaseScreen baseScreen) {
        super(baseScreen, R.layout.download_buffer_size_dialog);
        this.baseScreen = baseScreen;
        this.userSettings = baseScreen.app.getUserSettings();
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
        int size = baseScreen.app.getUserSettings().getBufferSize() / 1024;

        if (size == 1) kb1.setChecked(true);
        else if (size == 2) kb2.setChecked(true);
        else if (size == 4) kb4.setChecked(true);
        else if (size == 8) kb8.setChecked(true);
        else if (size == 16) kb16.setChecked(true);
    }

    private void views() {
        kb1 = (RadioButton) dialog.findViewById(R.id.kb1);
        kb2 = (RadioButton) dialog.findViewById(R.id.kb2);
        kb4 = (RadioButton) dialog.findViewById(R.id.kb4);
        kb8 = (RadioButton) dialog.findViewById(R.id.kb8);
        kb16 = (RadioButton) dialog.findViewById(R.id.kb16);

        kb1.setOnClickListener(this);
        kb2.setOnClickListener(this);
        kb4.setOnClickListener(this);
        kb8.setOnClickListener(this);
        kb16.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        close();

        if (view.getId() == kb1.getId())
            userSettings.setBufferSize(1024);

        else if (view.getId() == kb2.getId())
            userSettings.setBufferSize(1024 * 2);

        else if (view.getId() == kb4.getId())
            userSettings.setBufferSize(1024 * 4);

        else if (view.getId() == kb8.getId())
            userSettings.setBufferSize(1024 * 8);

        else if (view.getId() == kb16.getId())
            userSettings.setBufferSize(1024 * 16);
    }

}
