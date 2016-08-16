package main.screens.main_screen.setting_screen;

import android.widget.SeekBar;
import android.widget.TextView;
import main.screens.BaseScreen;
import net.fdm.R;

public class DownloadPartEditor extends BaseDialogBuilder {

    private int numberOfParts = 1;
    private TextView preview;
    private SeekBar seekBar;
    private String preLoadedText;
    private BaseScreen baseScreen;

    public DownloadPartEditor(BaseScreen baseScreen) {
        super(baseScreen, R.layout.download_part_dialog);
        this.baseScreen = baseScreen;
        this.numberOfParts = baseScreen.app.getUserSettings().getDownloadPart();
        this.preLoadedText = activity.getString(R.string.download_parts);

        views();
        update();
    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public void close() {
        if (dialog != null)
            dialog.dismiss();
    }

    private void update() {
        seekBar.setProgress(baseScreen.app.getUserSettings().getDownloadPart() - 1);
        preview.setText(preLoadedText + " " + baseScreen.app.getUserSettings().getDownloadPart());
    }

    private void views() {
        preview = (TextView) dialog.findViewById(R.id.download_part_preview);

        seekBar = (SeekBar) dialog.findViewById(R.id.download_parts);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int index, boolean b) {
                numberOfParts = index + 1;
                preview.setText(preLoadedText + " " + numberOfParts);
                baseScreen.app.getUserSettings().setDownloadPart(numberOfParts);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

}
