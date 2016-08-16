package main.screens.main_screen.home_screen;

import android.support.v4.view.ViewPager;
import main.screens.main_screen.MainScreen;
import net.fdm.R;

public class HomeNestedScreenManager {

    public HomeScreen homeScreen;
    public MainScreen mainScreen;
    public ViewPager viewPager;
    public HomeNestedScreenAdapter screenAdapter;
    public int totalNestedScreen = 2;


    public HomeNestedScreenManager(HomeScreen homeScreen) {
        this.homeScreen = homeScreen;
        this.mainScreen = homeScreen.getMainScreen();
        viewPager = (ViewPager) homeScreen.getLayoutView().findViewById(R.id.view_pager);
        screenAdapter = new HomeNestedScreenAdapter(this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(screenAdapter);
        viewPager.setOnPageChangeListener(screenAdapter);
    }


    public void speedDialClick() {
        this.viewPager.setCurrentItem(0);
        homeScreen.speedDial.setTextColor(homeScreen.getResources().getColor(R.color.white));
        homeScreen.bookmarks.setTextColor(homeScreen.getResources().getColor(R.color.color_border_deep));
    }


    public void bookmarkClick() {
        this.viewPager.setCurrentItem(2);
        homeScreen.bookmarks.setTextColor(homeScreen.getResources().getColor(R.color.white));
        homeScreen.speedDial.setTextColor(homeScreen.getResources().getColor(R.color.color_border_deep));
    }


}
