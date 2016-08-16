package main.screens.main_screen.home_screen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import net.fdm.R;

public class HomeNestedScreenAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    HomeNestedScreenManager nestedScreenManager;
    Fragment[] totalScreens;


    public HomeNestedScreenAdapter(HomeNestedScreenManager nestedScreenManager) {
        super(nestedScreenManager.homeScreen.getChildFragmentManager());
        this.nestedScreenManager = nestedScreenManager;
        initNestedScreens();
    }


    private void initNestedScreens() {
        this.totalScreens = new Fragment[]{new SpeedDialScreen(), new BookmarkScreen()};
    }


    @Override
    public Fragment getItem(int screenNumber) {
        if (totalScreens[screenNumber] == null) initNestedScreens();
        return totalScreens[screenNumber];
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }


    @Override
    public void onPageSelected(int screenNumber) {
        if (screenNumber == 0) {
            nestedScreenManager.homeScreen.speedDial.setTextColor(
                    nestedScreenManager.homeScreen.getResources().getColor(R.color.white));
            nestedScreenManager.homeScreen.bookmarks.setTextColor(
                    nestedScreenManager.homeScreen.getResources().getColor(R.color.color_border_deep));

        } else if (screenNumber == 1) {
            nestedScreenManager.homeScreen.speedDial.setTextColor(
                    nestedScreenManager.homeScreen.getResources().getColor(R.color.color_border_deep));
            nestedScreenManager.homeScreen.bookmarks.setTextColor(
                    nestedScreenManager.homeScreen.getResources().getColor(R.color.color_border_deep));
        } else {
            nestedScreenManager.homeScreen.bookmarks.setTextColor(
                    nestedScreenManager.homeScreen.getResources().getColor(R.color.white));
            nestedScreenManager.homeScreen.speedDial.setTextColor(
                    nestedScreenManager.homeScreen.getResources().getColor(R.color.color_border_deep));
        }
    }


    @Override
    public void onPageScrollStateChanged(int i) {

    }


    @Override
    public int getCount() {
        return nestedScreenManager.totalNestedScreen;
    }
}
