package main.screens.about_us_screen;


import android.content.res.Configuration;
import android.view.View;

import net.fdm.R;

import main.screens.BaseScreen;

public class AboutAppScreen extends BaseScreen {

    @Override
    public int getLayout() {
        return R.layout.screen_about_us;
    }

    @Override
    public void onLayoutLoad() {

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
        exit();
    }

    @Override
    public void onClearMemory() {
        freeMemory();
    }

    @Override
    public void onScreenOptionChange(Configuration configuration) {

    }


    public void onBackButtonClick(View view) {
        exit();
    }

    private void freeMemory() {
        unbindDrawables(findViewById(R.id.main_layout));
        System.gc();
    }
}



