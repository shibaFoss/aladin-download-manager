package main.screens.main_screen.home_screen;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import main.app.App;
import main.data_holder.SpeedDialModel;
import main.screens.main_screen.BaseNestedScreen;
import main.screens.main_screen.home_screen.SpeedDialGridAdapter.ViewHolder;
import main.screens.main_screen.search_screen.SearchScreen;
import net.fdm.R;

import java.util.ArrayList;

/**
 * <b>SpeedDialScreen:</b> A nested fragment screen where the speed dial bookmarks
 * are shown to user.<br>
 */
public class SpeedDialScreen extends BaseNestedScreen {

    public App app;
    public View layoutView;
    public GridView gridView;
    public SpeedDialGridAdapter gridAdapter;

    @Override
    protected int getScreenLayout() {
        return R.layout.speed_dail_screen;
    }

    @Override
    protected void onAfterLayoutLoad(View layoutView, Bundle savedInstanceState) {
        this.layoutView = layoutView;
        this.app = getMainScreen().app;
        this.init(layoutView);

        //save this screen to the global object store.
        getMainScreen().globalObjectStore.speedDialScreen = this;
    }

    @Override
    protected void onViewCreating(View view) {

    }

    public View getLayoutView() {
        return layoutView;
    }

    public void notifySpeedDialDataChange() {
        if (this.gridAdapter != null)
            this.gridAdapter.notifyDataSetChanged();
    }

    private void init(View layoutView) {
        this.gridView = (GridView) layoutView.findViewById(R.id.grid_view);
        this.gridAdapter = new SpeedDialGridAdapter(this);

        //set the grid adapter to the grid view.
        this.gridView.setAdapter(gridAdapter);
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (viewHolder != null) {
                    loadSpeedDial(viewHolder);
                }
            }
        });

        this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (viewHolder != null) {
                    openSpeedDialOption(viewHolder, position);
                }
                return true;
            }
        });
    }

    private void openSpeedDialOption(ViewHolder viewHolder, int position) {
        if (viewHolder != null) {
            SpeedDialModel model = getMainScreen().app.getBookmarkLoader().getSpeedDialModelList().get(position);
            new SpeedDialOption(this).setSpeedDialModel(model).show();
        }
    }

    /**
     * Load the speed dial url to the web_view of the search_screen.
     */
    public void loadSpeedDial(ViewHolder viewHolder) {
        if (viewHolder != null) {
            SpeedDialModel model = viewHolder.speedDialModel;
            String url = model.url;

            if (url != null) {
                SearchScreen searchScreen = getMainScreen().globalObjectStore.searchScreen;

                if (searchScreen != null) {
                    searchScreen.webEngine.loadUrl(url);
                    getMainScreen().nestedScreenManager.searchTabClick();
                }
            }
        }
    }

    /**
     * Return the speed_dial array list.
     */
    public ArrayList<SpeedDialModel> getSpeedDialList() {
        return app.getBookmarkLoader().getSpeedDialModelList();
    }
}
