package main.screens;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import main.ad_system.AdUnitId;
import main.app.App;
import main.dialog_factory.MessageBox;
import main.download_manager.DownloadService;
import net.fdm.R;

import static android.content.pm.PackageManager.SIGNATURE_MATCH;

/**
 * <b>BaseScreen:</b> is the base class for all the other screens/activities.
 * The basic functions of every activity are written here.
 *
 * @author Shiba.
 */
public abstract class BaseScreen extends AppCompatActivity {

    public Vibrator vibrator;
    public App app;
    public InterstitialAd fullscreenAd;


    /**
     * Remove the drawable from all of the children views of the given view. That's useful for free up
     * lots of memory.
     */
    public static void unbindDrawables(View view) {
        try {
            if (view.getBackground() != null)
                view.getBackground().setCallback(null);

            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(null);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    unbindDrawables(viewGroup.getChildAt(i));

                if (!(view instanceof AdapterView))
                    viewGroup.removeAllViews();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public abstract int getLayout();


    public abstract void onLayoutLoad();


    public abstract void onAfterLayoutLoad();


    public abstract void onPauseScreen();


    public abstract void onResumeScreen();


    public abstract void onExitScreen();


    public abstract void onClearMemory();


    public abstract void onScreenOptionChange(Configuration configuration);


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            app = (App) getApplication();

            if (getLayout() != -1) {
                setContentView(getLayout());
            }
            onLayoutLoad();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        onAfterLayoutLoad();
    }


    public void onPause() {
        super.onPause();
        onPauseScreen();
    }


    public void onResume() {
        super.onResume();
        app.getUserSettings();
        startDownloadServiceSystem();
        onResumeScreen();
    }


    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        onScreenOptionChange(configuration);
    }


    public void onBackPressed() {
        onExitScreen();
    }


    public void exit() {
        finish();
        overridePendingTransition(R.anim.left_to_right_anim, R.anim.right_to_left_anim);
    }


    public void onDestroy() {
        super.onDestroy();
        onClearMemory();
    }


    public void loadNewFullScreenAd() {
        final AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdUnitId.deviceId).build();
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


    /**
     * Show the fullscreen advertisement to the user.
     *
     * @param shouldLoadMoreAd true if new ad need be to loaded in background, false otherwise.
     */
    public void showFullscreenAd(boolean shouldLoadMoreAd) {
        if (fullscreenAd != null) {
            fullscreenAd.show();
            if (shouldLoadMoreAd) {
                loadNewFullScreenAd();
            }
        }
    }


    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), SIGNATURE_MATCH).versionName;
        } catch (PackageManager.NameNotFoundException error) {
            error.printStackTrace();
            return null;
        }
    }


    public int getVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), SIGNATURE_MATCH).versionCode;
        } catch (PackageManager.NameNotFoundException error) {
            error.printStackTrace();
            return -1;
        }
    }


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


    public void startDownloadServiceSystem() {
        startService(new Intent(this, DownloadService.class));
    }


    public int getColorFrom(int resColorId) {
        return getResources().getColor(resColorId);
    }


    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
