package main.screens.main_screen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import main.screens.main_screen.downloads_screen.DownloadsScreen;
import main.screens.main_screen.home_screen.HomeScreen;
import main.screens.main_screen.search_screen.SearchScreen;
import main.screens.main_screen.setting_screen.SettingScreen;

/**
 * ScreenAdapter is the adapter class of the HomeNestedScreenManager's all nested screens.
 */
public class ScreenAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private NestedScreenManager nestedScreenManager;
    private Fragment[] totalScreens;

    public ScreenAdapter(FragmentManager fragmentManager, NestedScreenManager nestedScreenManager) {
        super(fragmentManager);
        this.nestedScreenManager = nestedScreenManager;
        initNestedScreens();
    }

    @Override
    public Fragment getItem(int screenNumber) {
        if (totalScreens[screenNumber] == null) {
            initNestedScreens();
        }

        return totalScreens[screenNumber];
    }

    private void initNestedScreens() {
        this.totalScreens = new Fragment[]{
                new HomeScreen(), new SearchScreen(), new DownloadsScreen(), new SettingScreen()};
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int screenNumber) {
    }

    @Override
    public void onPageScrollStateChanged(int screenNumber) {
        System.gc();
    }

    @Override
    public int getCount() {
        return nestedScreenManager.totalNestedScreen;
    }
}
