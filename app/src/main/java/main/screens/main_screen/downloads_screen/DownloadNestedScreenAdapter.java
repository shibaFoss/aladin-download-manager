package main.screens.main_screen.downloads_screen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class DownloadNestedScreenAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private DownloadNestedScreenManager downloadNestedScreenManager;
    private Fragment[] totalScreens;

    public DownloadNestedScreenAdapter(DownloadNestedScreenManager downloadNestedScreenManager) {
        super(downloadNestedScreenManager.downloadsScreen.getChildFragmentManager());
        this.downloadNestedScreenManager = downloadNestedScreenManager;
        initNestedScreens();
    }

    private void initNestedScreens() {
        this.totalScreens = new Fragment[]{new RunningDownloadScreen(), new CompleteDownloadScreen()};
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
    public void onPageSelected(int viewPagerItemIndex) {
       downloadNestedScreenManager.resetSelectorButtonBg(viewPagerItemIndex);
    }

    @Override
    public void onPageScrollStateChanged(int screenNumber) {
        System.gc();
    }

    @Override
    public int getCount() {
        return downloadNestedScreenManager.totalNestedScreen;
    }
}
