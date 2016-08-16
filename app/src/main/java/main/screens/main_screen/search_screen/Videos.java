package main.screens.main_screen.search_screen;

import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import main.screens.main_screen.MainScreen;
import main.utilities.UiUtils;
import net.fdm.R;

public class Videos {

    private SearchScreen searchScreen;
    private WebEngine webEngine;
    private String[] videoUrls;
    private Dialog dialog;

    public Videos(SearchScreen searchScreen, String[] urls) {
        this.searchScreen = searchScreen;
        this.webEngine = searchScreen.webEngine;
        this.videoUrls = urls;
        this.init();
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void close() {
        if (dialog != null)
            dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(getMainScreen(), R.layout.video_list_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

        TextView title = (TextView) dialog.findViewById(R.id.title);
        ListView videoListView = (ListView) dialog.findViewById(R.id.video_list);
        BaseAdapter listAdapter = getVideoListAdapter();

        videoListView.setAdapter(listAdapter);

        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    String videoUrl = videoUrls[position];
                    String videoName = URLUtil.guessFileName(videoUrl, null, null);
                    webEngine.downloadListener.showDownloadOption(videoUrl, videoName);
                } catch (Exception error) {
                    error.printStackTrace();
                    getMainScreen().showSimpleMessageBox("Something goes wrong. Try again later.");
                }
            }
        });
    }

    private BaseAdapter getVideoListAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return videoUrls.length;
            }

            @Override
            public Object getItem(int position) {
                return videoUrls[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View view, ViewGroup viewGroup) {
                if (view == null) {
                    view = View.inflate(getMainScreen(), R.layout.video_list_row, null);
                    TextView name = (TextView) view.findViewById(R.id.video_name);
                    name.setText(videoUrls[position]);
                }
                return view;
            }
        };
    }

    private MainScreen getMainScreen() {
        return searchScreen.getMainScreen();
    }
}
