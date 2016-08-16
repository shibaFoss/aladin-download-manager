package main.screens.main_screen.home_screen;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import main.app.App;
import main.data_holder.Bookmark;
import main.data_holder.BookmarkLoader;
import main.screens.main_screen.BaseNestedScreen;
import main.screens.main_screen.MainScreen;
import main.screens.main_screen.search_screen.SearchScreen;
import net.fdm.R;

public class BookmarkScreen  extends BaseNestedScreen {

    public BookmarkListAdapter bookmarkListAdapter;
    public ListView bookmarkList;
    public MainScreen mainScreen;
    public App app;
    public View layoutView;

    @Override
    protected int getScreenLayout() {
        return R.layout.bookmark_screen;
    }

    @Override
    protected void onAfterLayoutLoad(View layoutView, Bundle savedInstanceState) {
        this.layoutView = layoutView;
        this.init(layoutView);
    }

    @Override
    protected void onViewCreating(View view) {

    }

    public void notifyBookmarkDataChange() {
        this.bookmarkListAdapter.notifyDataSetChanged();
    }

    private void init(View layoutView) {
        this.mainScreen = getMainScreen();
        this.app = getMainScreen().app;
        this.mainScreen.globalObjectStore.bookmarkScreen = this;

        this.bookmarkList = (ListView) layoutView.findViewById(R.id.list_view);
        this.bookmarkListAdapter = new BookmarkListAdapter(this);

        this.bookmarkList.setAdapter(this.bookmarkListAdapter);
        initBookmarkClickEvent();
    }

    private void initBookmarkClickEvent() {
        this.bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                BookmarkLoader bookmarkLoader = app.getBookmarkLoader();
                Bookmark bookmark = bookmarkLoader.getBookmarksList().get(position);
                String url = bookmark.url;

                if (url != null) {
                    SearchScreen searchScreen = mainScreen.globalObjectStore.searchScreen;
                    if (searchScreen != null) {
                        searchScreen.webEngine.loadUrl(url);
                        mainScreen.nestedScreenManager.searchTabClick();
                    }
                }
            }
        });

        this.bookmarkList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                openBookmarkOption(position);
                return true;
            }
        });
    }


    private void openBookmarkOption(int position) {
        new BookmarkOption(this).setBookmarkModel(
                app.getBookmarkLoader().getBookmarksList().get(position)).show();
    }
}
