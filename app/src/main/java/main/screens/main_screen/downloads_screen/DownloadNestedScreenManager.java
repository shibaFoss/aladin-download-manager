package main.screens.main_screen.downloads_screen;

import android.support.v4.view.ViewPager;
import android.view.View;
import main.screens.main_screen.MainScreen;
import net.fdm.R;

public class DownloadNestedScreenManager implements View.OnClickListener {

    public ViewPager viewPager;
    public DownloadNestedScreenAdapter downloadNestedScreenAdapter;
    public DownloadsScreen downloadsScreen;
    public MainScreen mainScreen;

    public int totalNestedScreen = 2;

    public DownloadNestedScreenManager(DownloadsScreen downloadScreen) {
        this.downloadsScreen = downloadScreen;
        this.mainScreen = downloadScreen.getMainScreen();

        this.viewPager = (ViewPager) downloadScreen.downloadScreenLayout.findViewById(R.id.view_pager);
        this.downloadNestedScreenAdapter = new DownloadNestedScreenAdapter(this);

        //configuring the view pager
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(downloadNestedScreenAdapter);
        viewPager.setOnPageChangeListener(downloadNestedScreenAdapter);
    }

    public void setTabButtons(View[] buttons) {
        for (View button : buttons) button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int clickId = view.getId();

        if (clickId == downloadsScreen.downloadingButton.getId()) runningClick();
        else if (clickId == downloadsScreen.downloadedButton.getId()) completeClick();
    }

    private void runningClick() {
        this.viewPager.setCurrentItem(0);
        resetTabButtonColor();
        resetSelectorButtonBg(0);
    }

    private void completeClick() {
        this.viewPager.setCurrentItem(1);
        resetTabButtonColor();
        resetSelectorButtonBg(1);
    }

    public void resetSelectorButtonBg(int currentViewPagerIndex) {
        if (currentViewPagerIndex == 1) {
            downloadsScreen.downloadedButton.setTextColor(downloadsScreen.getMainScreen().getColorFrom(R.color.white));
            downloadsScreen.downloadedButton.setBackgroundColor(downloadsScreen.getMainScreen().getColorFrom(R.color.color_primary));

            downloadsScreen.downloadingButton.setTextColor(downloadsScreen.getMainScreen().getColorFrom(R.color.black_light));
            downloadsScreen.downloadingButton.setBackgroundColor(downloadsScreen.getMainScreen().getColorFrom(R.color.white));

        } else if (currentViewPagerIndex == 0) {
            downloadsScreen.downloadingButton.setTextColor(downloadsScreen.getMainScreen().getColorFrom(R.color.white));
            downloadsScreen.downloadingButton.setBackgroundColor(downloadsScreen.getMainScreen().getColorFrom(R.color.color_primary));

            downloadsScreen.downloadedButton.setTextColor(downloadsScreen.getMainScreen().getColorFrom(R.color.black_light));
            downloadsScreen.downloadedButton.setBackgroundColor(downloadsScreen.getMainScreen().getColorFrom(R.color.white));
        }

    }

    public void resetTabButtonColor() {
        downloadsScreen.downloadingButton.setTextColor(downloadsScreen.getMainScreen().getColorFrom(R.color.black_light));
        downloadsScreen.downloadedButton.setTextColor(downloadsScreen.getMainScreen().getColorFrom(R.color
                .black_light));

        downloadsScreen.downloadingButton.setBackgroundResource(R.drawable.transparent_bg);
        downloadsScreen.downloadedButton.setBackgroundResource(R.drawable.transparent_bg);
    }
}
