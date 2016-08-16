package main.screens.main_screen.setting_screen;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import main.screens.BaseScreen;
import main.screens.main_screen.downloads_screen.BaseIntentChooser;
import main.utilities.UiUtils;
import net.fdm.R;

import java.util.List;

/**
 * <b>FriendInvite:</b> is the class where all user can share about the aladin dm
 * to their friends with facebook, whatsApp, twitter directly.
 *
 * @author Shiba.
 */
public class FriendInvite implements View.OnClickListener {

    private BaseScreen baseScreen;

    private Dialog dialog;
    private TextView showMoreButton;
    private ImageButton facebook, whatsApp, twitter, messenger;

    public FriendInvite(BaseScreen baseScreen) {
        this.baseScreen = baseScreen;
        init();
    }


    public void show() {
        if (dialog != null) dialog.show();
    }

    public void close() {
        if (dialog != null) dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.invite_friends_dialog);
        TextView message = (TextView) dialog.findViewById(R.id.message_edit);
        showMoreButton = (TextView) dialog.findViewById(R.id.show_more_bnt);

        facebook = (ImageButton) dialog.findViewById(R.id.facebook_bnt);
        whatsApp = (ImageButton) dialog.findViewById(R.id.whats_app_bnt);
        twitter = (ImageButton) dialog.findViewById(R.id.twitter_bnt);
        messenger = (ImageButton) dialog.findViewById(R.id.messenger_bnt);

        showMoreButton.setOnClickListener(this);

        facebook.setOnClickListener(this);
        whatsApp.setOnClickListener(this);
        twitter.setOnClickListener(this);
        messenger.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == showMoreButton.getId()) {
            close();
            showMoreButton();
        } else if (view.getId() == facebook.getId()) {
            close();
            facebook();
        } else if (view.getId() == whatsApp.getId()) {
            close();
            whatsApp();
        } else if (view.getId() == twitter.getId()) {
            close();
            twitter();
        } else if (view.getId() == messenger.getId()) {
            close();
            messenger();
        }
    }

    private void messenger() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());

            final List matchingApkInfo = baseScreen.getPackageManager().queryIntentActivities(intent, 0);
            for (Object object : matchingApkInfo) {
                ResolveInfo info = (ResolveInfo) object;

                String packageName = info.activityInfo.packageName;
                if (packageName.toLowerCase().equals("com.facebook.orca")) {
                    intent.setPackage(packageName);
                    baseScreen.startActivity(intent);
                    return;
                }
            }
            baseScreen.showSimpleMessageBox("You do not have latest Messenger installed on your phone.");
        } catch (Exception error) {
            error.printStackTrace();
            baseScreen.showSimpleMessageBox("You do not have latest Messenger installed on your phone.");
        }
    }

    private void twitter() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());

            final List matchingApkInfo = baseScreen.getPackageManager().queryIntentActivities(intent, 0);
            for (Object object : matchingApkInfo) {
                ResolveInfo info = (ResolveInfo) object;

                String packageName = info.activityInfo.packageName;
                if (packageName.toLowerCase().equals("com.twitter.android")) {
                    intent.setPackage(packageName);
                    baseScreen.startActivity(intent);
                    return;
                }
            }
            baseScreen.showSimpleMessageBox("You do not have latest Twitter installed on your phone.");
        } catch (Exception error) {
            error.printStackTrace();
            baseScreen.showSimpleMessageBox("You do not have latest Twitter installed on your phone.");
        }
    }

    private void whatsApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());

            final List matchingApkInfo = baseScreen.getPackageManager().queryIntentActivities(intent, 0);
            for (Object object : matchingApkInfo) {
                ResolveInfo info = (ResolveInfo) object;

                String packageName = info.activityInfo.packageName;
                if (packageName.toLowerCase().equals("com.whatsapp")) {
                    intent.setPackage(packageName);
                    baseScreen.startActivity(intent);
                    return;
                }
            }

            baseScreen.showSimpleMessageBox("You do not have latest WhatsApp installed on your phone.");
        } catch (Exception error) {
            error.printStackTrace();
            baseScreen.showSimpleMessageBox("You do not have latest WhatsApp installed on your phone.");
        }
    }

    private String getShareMessage() {
        return "I just have found a great app, I'm sure you will love it too. Check out : https://goo.gl/isg5Vk";
    }

    private void facebook() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());

            final List matchingApkInfo = baseScreen.getPackageManager().queryIntentActivities(intent, 0);
            for (Object object : matchingApkInfo) {
                ResolveInfo info = (ResolveInfo) object;
                String appName = (String) info.loadLabel(baseScreen.getPackageManager());
                if (appName.toLowerCase().equals("facebook") || appName.toLowerCase().equals("add to facebook")) {
                    intent.setPackage(info.activityInfo.packageName);
                    baseScreen.startActivity(intent);
                    return;
                }
            }
            baseScreen.showSimpleMessageBox("You do not have latest Facebook installed on your phone.");
        } catch (Exception error) {
            error.printStackTrace();
            baseScreen.showSimpleMessageBox("You do not have latest Facebook installed on your phone.");
        }
    }

    private void showMoreButton() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());

        BaseIntentChooser fileShareChooser = new BaseIntentChooser(baseScreen) {
            @Override
            public void onStartActivity(Intent intent, String packageName) {

            }
        };
        fileShareChooser.setIntent(intent);
        fileShareChooser.show();
    }
}
