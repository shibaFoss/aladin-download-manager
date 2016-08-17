package main.ad_system;

import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import libs.async_job.AsyncJob;
import main.screens.BaseScreen;

import static libs.async_job.AsyncJob.doInBackground;


public class AdSystem {

    private static final String adUnitId = "ca-app-pub-6209892632321204/6411104977";
    private AdView adView;


    public AdSystem(BaseScreen baseScreen, final RelativeLayout adViewLayoutHolder) {
        this.adView = new AdView(baseScreen);
        this.adView.setAdSize(AdSize.SMART_BANNER);

        doInBackground(new AsyncJob.BackgroundJob() {
            @Override
            public void doInBackground() {
                loadAds(adUnitId, adView, adViewLayoutHolder);
            }
        });
    }

    private void loadAds(final String adUnitId,
                         final AdView adView, final RelativeLayout adViewContainerLayout) {
        AsyncJob.doInMainThread(new AsyncJob.MainThreadJob() {
            @Override
            public void doInUIThread() {
                adView.setAdUnitId(adUnitId);
                adViewContainerLayout.addView(adView, 0);

                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }
        });
    }

    public void loadAds(boolean resumeAd) {
        if (adView != null) {
            if (resumeAd) {
                adView.resume();
            } else {
                adView.pause();
            }
        }
    }

}
