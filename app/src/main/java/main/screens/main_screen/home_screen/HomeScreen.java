package main.screens.main_screen.home_screen;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import main.screens.main_screen.BaseNestedScreen;
import net.fdm.R;

public class HomeScreen extends BaseNestedScreen implements View.OnClickListener {

    public TextView speedDial, bookmarks;
    public View layoutView;
    public HomeNestedScreenManager nestedScreenManager;


    public View getLayoutView() {
        return layoutView;
    }


    public void setLayoutView(View layoutView) {
        this.layoutView = layoutView;
    }


    @Override
    protected int getScreenLayout() {
        return R.layout.home_screen;
    }


    @Override
    protected void onAfterLayoutLoad(View layoutView, Bundle savedInstanceState) {
        init(layoutView);
        initNestedScreenManager();
    }


    @Override
    protected void onViewCreating(View view) {

    }


    private void initNestedScreenManager() {
        this.nestedScreenManager = new HomeNestedScreenManager(this);
    }


    private void init(View layoutView) {
        setLayoutView(layoutView);
        speedDial = (TextView) layoutView.findViewById(R.id.speed_dial);
        bookmarks = (TextView) layoutView.findViewById(R.id.bookmarks);
        speedDial.setOnClickListener(this);
        bookmarks.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == speedDial.getId()) {
            speedDial();
        } else if (view.getId() == bookmarks.getId()) {
            bookmarks();
        }
    }

    private void bookmarks() {
        if (nestedScreenManager != null) {
            nestedScreenManager.bookmarkClick();
        }
    }


    private void speedDial() {
        if (nestedScreenManager != null) {
            nestedScreenManager.speedDialClick();
        }
    }
}
