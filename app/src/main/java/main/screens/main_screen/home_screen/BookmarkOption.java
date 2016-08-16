package main.screens.main_screen.home_screen;

import android.app.Dialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import main.app.App;
import main.data_holder.Bookmark;
import main.data_holder.BookmarkLoader;
import main.screens.BaseScreen;
import main.screens.main_screen.GlobalObjectStore;
import main.screens.main_screen.MainScreen;
import main.screens.main_screen.search_screen.AddBookmarkDialog;
import main.utilities.UiUtils;
import net.fdm.R;

public class BookmarkOption implements View.OnClickListener {

    public BaseScreen baseScreen;
    public Dialog dialog;
    public TextView title, renameBnt, deleteBnt;
    public BookmarkScreen bookmarkScreen;
    public Bookmark bookmark;

    public BookmarkOption(BookmarkScreen bookmarkScreen) {
        this.bookmarkScreen = bookmarkScreen;
        this.baseScreen = bookmarkScreen.getMainScreen();
        this.init();
    }

    public void show() {
        dialog.show();
    }

    public void close() {
        dialog.dismiss();
    }

    public BookmarkOption setBookmarkModel(Bookmark model) {
        this.bookmark = model;
        return this;
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.speed_dial_n_bookmark_option_dialog);

        title = (TextView) dialog.findViewById(R.id.title);
        title.setText("Bookmark Option");

        renameBnt = (TextView) dialog.findViewById(R.id.edit_bnt);
        deleteBnt = (TextView) dialog.findViewById(R.id.delete_bnt);

        renameBnt.setOnClickListener(this);
        deleteBnt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == renameBnt.getId()) {
            renameBnt();
        } else if (view.getId() == deleteBnt.getId()) {
            deleteBnt();
        }
    }

    private void deleteBnt() {
        close();
        MainScreen mainScreen = bookmarkScreen.getMainScreen();
        App app = mainScreen.app;
        BookmarkLoader bookmarkLoader = app.getBookmarkLoader();
        bookmarkLoader.getBookmarksList().remove(bookmark);
        bookmarkLoader.updateInDisk();

        mainScreen.vibrator.vibrate(20);
        mainScreen.toast("Deleted");
        bookmarkScreen.notifyBookmarkDataChange();
    }

    private void renameBnt() {
        close();
        AddBookmarkDialog bookmarkDialog = new AddBookmarkDialog(bookmarkScreen.getMainScreen());
        bookmarkDialog.setDefaultValue(bookmark.url, bookmark.name);
        bookmarkDialog.openForBookmarkEditing(bookmark);

        GlobalObjectStore globalObjectStore = bookmarkScreen.getMainScreen().globalObjectStore;
        ImageButton favoriteButton = globalObjectStore.searchScreen.favoriteButton;
        bookmarkDialog.setFavoriteButton(favoriteButton);

        bookmarkDialog.show();
    }


}
