package main.screens.about_us_screen;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import net.fdm.R;

import main.screens.BaseScreen;

/**
 * The screen is responsible for showing the about details of the app and the project.
 */
public class AboutUsScreen extends BaseScreen {

    @Override
    public int getLayout() {
        return R.layout.screen_about_us;
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


    public void onBackButtonClick(View view) {
        exit();
    }

    public void onProjectWebsiteClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/shibaFoss/aladin-download-manager"));
        startActivity(intent);
    }

}



