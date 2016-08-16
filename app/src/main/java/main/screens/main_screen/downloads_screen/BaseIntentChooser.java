package main.screens.main_screen.downloads_screen;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import main.screens.BaseScreen;
import main.utilities.UiUtils;
import net.fdm.R;

import java.util.List;

import static main.utilities.IntentChooserUtils.getMatchingIntentActivities;

public abstract class BaseIntentChooser {

    public Dialog dialog;
    public BaseScreen baseScreen;
    private List<ResolveInfo> appInfoList;
    private Intent intent;

    public BaseIntentChooser(BaseScreen baseScreen) {
        this.baseScreen = baseScreen;
    }

    public abstract void onStartActivity(Intent intent, String packageName);

    public void show() {
        if (this.appInfoList.size() > 0) {
            dialog.show();
        } else {
            baseScreen.showSimpleMessageBox("No Application can handle the file.");
        }
    }

    public void close() {
        dialog.dismiss();
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
        this.appInfoList = getMatchingIntentActivities(baseScreen, intent);
        this.init();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.intent_chooser_dialog);
        ListView appList = (ListView) dialog.findViewById(R.id.app_name_list);

        appList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return appInfoList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                if (view == null) {
                    view = LayoutInflater.from(baseScreen).inflate(R.layout.intent_chooser_list_row_layout, null);
                }
                TextView appName = (TextView) view.findViewById(R.id.app_name);
                appName.setText(appInfoList.get(position).loadLabel(baseScreen.getPackageManager()));
                ImageView appImg = (ImageView) view.findViewById(R.id.app_img);
                appImg.setImageDrawable(appInfoList.get(position).loadIcon(baseScreen.getPackageManager()));
                return view;
            }
        });

        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ResolveInfo resolveInfo = appInfoList.get(position);
                String packageName = resolveInfo.activityInfo.packageName;
                intent.setPackage(packageName);
                close();
                onStartActivity(intent, packageName);
                baseScreen.startActivity(intent);
            }
        });
    }
}
