package main.screens.main_screen;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import net.fdm.R;

import main.ad_system.AdUnitId;
import main.dialog_factory.YesNoDialog;
import main.key_database.KeyStore;
import main.screens.BaseScreen;
import main.screens.main_screen.search_screen.SearchScreen;

public class MainScreen extends BaseScreen {

    public ImageButton homeTabButton, searchTabButton, downloadTabButton, settingsTabButton;
    public NestedScreenManager nestedScreenManager;
    public GlobalObjectStore globalObjectStore;
    public boolean isExitFullScreenAdRequested = false;

    @Override
    public int getLayout() {
        return R.layout.main_screen;
    }

    @Override
    public void onAfterLayoutLoad() {
        this.initViews();
        this.initNestedScreenManager();
        this.initGlobalObjectStore();
        checkScreenIntent();
    }

    @Override
    public void onPauseScreen() {

    }

    @Override
    public void onResumeScreen() {
        if (app.getGlobalClasses() != null) {
            app.getGlobalClasses().mainScreen = this;
        }

        loadNewFullScreenAd();
    }

    @Override
    public void onExitScreen() {
        app.getUserTracker().promptForAppRating(this);
    }

    @Override
    public void loadNewFullScreenAd() {
        final AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdUnitId.deviceId).build();
        if (fullscreenAd == null) {
            fullscreenAd = new InterstitialAd(this);
            fullscreenAd.setAdUnitId(AdUnitId.fullScreenAdId);
            fullscreenAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    fullscreenAd.loadAd(adRequest);
                    if (isExitFullScreenAdRequested) {
                        promptUserForExit();
                    }
                }
            });
        }
        fullscreenAd.loadAd(adRequest);
    }

    public void promptUserForExit() {
        final YesNoDialog yesNoDialog = new YesNoDialog(this);
        yesNoDialog.setMessage("Are you sure to exit Aladin DM?");
        yesNoDialog.setButtonNames("Minimize", "No");

        yesNoDialog.clickListener = new YesNoDialog.ClickListener() {
            @Override
            public void onYes(TextView view) {
                yesNoDialog.close();
                finish();
            }

            @Override
            public void onNo(TextView view) {
                yesNoDialog.close();
            }
        };

        yesNoDialog.show();
    }

    private void initViews() {
        this.homeTabButton = (ImageButton) findViewById(R.id.home);
        this.searchTabButton = (ImageButton) findViewById(R.id.search);
        this.downloadTabButton = (ImageButton) findViewById(R.id.downloads);
        this.settingsTabButton = (ImageButton) findViewById(R.id.settings);
    }

    private void initNestedScreenManager() {
        this.nestedScreenManager = new NestedScreenManager(this);
        this.nestedScreenManager.setTabButtons(new View[]{homeTabButton, searchTabButton, downloadTabButton, settingsTabButton});
    }

    private void initGlobalObjectStore() {
        this.globalObjectStore = new GlobalObjectStore(this);
    }

    /**
     * Check the screen/activity intent, if found any action is attached to the intent
     * then it will open the {@link SearchScreen}.
     */
    private void checkScreenIntent() {
        String webUrl = getIntendedUrl();
        if (webUrl != null) {
            nestedScreenManager.searchTabClick();
        }
    }

    /**
     * Returns the intended web url that has been fired by the other app.
     */
    public String getIntendedUrl() {
        Intent intent = getIntent();
        String action = intent.getAction();

        if (intent.getBooleanExtra(KeyStore.OPEN_DOWNLOAD_FRAGMENT, false)) {
            nestedScreenManager.downloadsTabClick();
        } else {
            if (action != null && action.equals(Intent.ACTION_SEND)) {
                return intent.getStringExtra(Intent.EXTRA_TEXT);
            } else if (action != null && action.equals(Intent.ACTION_VIEW)) {
                return intent.getDataString();
            }
        }

        return null;
    }


}
