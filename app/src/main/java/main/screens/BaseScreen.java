package main.screens;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import net.fdm.R;

import main.ad_system.AdUnitId;
import main.app.App;
import main.dialog_factory.MessageBox;
import main.download_manager.DownloadService;

/**
 * The class is the base of all screen(Activity) classes used in this app.
 */
public abstract class BaseScreen extends AppCompatActivity {

    public Vibrator vibrator;
    public App app;
    public InterstitialAd fullscreenAd;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            app = (App) getApplication();
            if (getLayout() != -1)
                setContentView(getLayout());
        } catch (Exception error) {
            error.printStackTrace();
            vibrator.vibrate(20);
            toast(getString(R.string.something_went_wrong));
        }
    }

    public abstract int getLayout();

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        onAfterLayoutLoad();
    }

    public abstract void onAfterLayoutLoad();

    public void onBackPressed() {
        onExitScreen();
    }

    public abstract void onExitScreen();

    public void onPause() {
        super.onPause();
        onPauseScreen();
    }

    public abstract void onPauseScreen();

    public void onResume() {
        super.onResume();
        app.getUserSettings();
        startDownloadServiceSystem();
        onResumeScreen();
    }

    public void startDownloadServiceSystem() {
        startService(new Intent(this, DownloadService.class));
    }

    public abstract void onResumeScreen();

    public void exit() {
        finish();
        overridePendingTransition(R.anim.left_to_right_anim, R.anim.right_to_left_anim);
    }

    public void showFullscreenAd(boolean shouldLoadMoreAd) {
        if (fullscreenAd != null) {
            fullscreenAd.show();
            if (shouldLoadMoreAd) {
                loadNewFullScreenAd();
            }
        }
    }

    public void loadNewFullScreenAd() {
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdUnitId.deviceId).build();
        if (fullscreenAd == null) {
            fullscreenAd = new InterstitialAd(this);
            fullscreenAd.setAdUnitId(AdUnitId.fullScreenAdId);
            fullscreenAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    fullscreenAd.loadAd(adRequest);
                }
            });
        }

        fullscreenAd.loadAd(adRequest);
    }

    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException error) {
            error.printStackTrace();
            return null;
        }
    }

    /**
     * The function open the google play store app with the app's own package name.
     */
    public void gotoPlayStore() {
        String url = "market://details?id=" + getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public Drawable getDrawableImage(int resId) {
        return getResources().getDrawable(resId);
    }

    public void startActivity(Class activityClass) {
        startActivity(new Intent(this, activityClass));
        overridePendingTransition(R.anim.screen_enter_anim, R.anim.screen_out_anim);
    }

    public void showSimpleMessageBox(String message) {
        MessageBox messageBox = MessageBox.getInstance(this);
        messageBox.setMessage(message);
        messageBox.setOkButtonText(getString(R.string.close));
        messageBox.show();
    }

    public void showSimpleMessageBox(String message, MessageBox.ClickListener clickListener) {
        MessageBox messageBox = MessageBox.getInstance(this);
        messageBox.setMessage(message);
        messageBox.clickListener = clickListener;
        messageBox.setOkButtonText(getString(R.string.close));
        messageBox.show();
    }

    public void showSimpleHtmlMessageBox(String message) {
        MessageBox messageBox = MessageBox.getInstance(this);
        messageBox.setHtmlMessage(Html.fromHtml(message));
        messageBox.setOkButtonText(getString(R.string.close));
        messageBox.show();
    }

    public int getColorFrom(int resColorId) {
        return getResources().getColor(resColorId);
    }

}
