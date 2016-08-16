package main.screens.main_screen;

import android.view.View;
import main.custom_views.NonSwappableViewPager;
import net.fdm.R;

public class NestedScreenManager implements View.OnClickListener {

    public NonSwappableViewPager viewPager;
    public ScreenAdapter screenAdapter;
    public MainScreen mainScreen;

    public int totalNestedScreen = 4;

    public NestedScreenManager(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.viewPager = (NonSwappableViewPager) mainScreen.findViewById(R.id.view_pager);
        this.screenAdapter = new ScreenAdapter(mainScreen.getSupportFragmentManager(), this);

        //configuring the view pager
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(screenAdapter);
        viewPager.setOnPageChangeListener(screenAdapter);
    }

    public void setTabButtons(View[] buttons) {
        for (View button : buttons) button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int clickId = view.getId();

        if (clickId == mainScreen.homeTabButton.getId()) homeTabClick();
        else if (clickId == mainScreen.searchTabButton.getId()) searchTabClick();
        else if (clickId == mainScreen.downloadTabButton.getId()) downloadsTabClick();
        else if (clickId == mainScreen.settingsTabButton.getId()) settingTabClick();
    }

    public void homeTabClick() {
        this.viewPager.setCurrentItem(0);
        resetTabButtonColor();
        mainScreen.homeTabButton.setImageResource(R.drawable.home_press);
    }

    public void searchTabClick() {
        this.viewPager.setCurrentItem(1);
        resetTabButtonColor();
        mainScreen.searchTabButton.setImageResource(R.drawable.browser_press);

    }

    public void downloadsTabClick() {
        this.viewPager.setCurrentItem(2);
        resetTabButtonColor();
        mainScreen.downloadTabButton.setImageResource(R.drawable.download_press);
    }

    public void settingTabClick() {
        this.viewPager.setCurrentItem(3);
        resetTabButtonColor();
        mainScreen.settingsTabButton.setImageResource(R.drawable.setting_press);
    }

    /**
     * Reset all the tab button's color.
     */
    private void resetTabButtonColor() {
        mainScreen.homeTabButton.setImageResource(R.drawable.home_unpress);
        mainScreen.searchTabButton.setImageResource(R.drawable.browser_unpress);
        mainScreen.downloadTabButton.setImageResource(R.drawable.download_unpress);
        mainScreen.settingsTabButton.setImageResource(R.drawable.setting_unpress);
    }

}
