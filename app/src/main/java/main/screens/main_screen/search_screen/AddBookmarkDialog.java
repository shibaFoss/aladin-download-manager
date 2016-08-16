package main.screens.main_screen.search_screen;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import main.data_holder.Bookmark;
import main.data_holder.BookmarkLoader;
import main.screens.main_screen.GlobalObjectStore;
import main.screens.main_screen.MainScreen;
import main.screens.main_screen.home_screen.BookmarkScreen;
import main.data_holder.SpeedDialModel;
import main.utilities.UiUtils;
import net.fdm.R;

import java.util.ArrayList;

/**
 * <b>AddBookmarkDialog:</b> Dialog shower class.<br>
 * <b>Responsibility:</b> <br>
 * 1. Adding a new bookmark to the {@link BookmarkLoader#getBookmarksList()}. <br>
 * 2. Adding new speed dial to the {@link BookmarkLoader#getSpeedDialModelList()}.
 * 3. Edit existing speed-dial or bookmark models.
 */
public class AddBookmarkDialog implements View.OnClickListener {

    public MainScreen mainScreen;
    public Dialog dialog;
    public EditText nameEditText, urlEditText;
    public CheckBox addToSpeedDial;
    public TextView title, cancelBnt, saveBnt;
    public ImageButton favoriteButton;

    public boolean isOpenForEditing = false;
    public SpeedDialModel speedDialModel = null;
    public Bookmark bookmarkModel = null;

    public AddBookmarkDialog(MainScreen screen) {
        this.mainScreen = screen;
        this.init();
    }

    /**
     * Set the default values to the edit_text views.
     *
     * @param url  the default value of urlEditText
     * @param name the default value of nameEditText
     */
    public void setDefaultValue(String url, String name) {
        urlEditText.setText(url);
        nameEditText.setText(name);
    }

    public void setFavoriteButton(ImageButton button) {
        this.favoriteButton = button;
    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public void close() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void openForSpeedDialEditing(SpeedDialModel model) {
        this.isOpenForEditing = true;
        this.speedDialModel = model;
        addToSpeedDial.setVisibility(View.GONE);
    }

    public void openForBookmarkEditing(Bookmark bookmark) {
        this.isOpenForEditing = true;
        this.bookmarkModel = bookmark;
        addToSpeedDial.setVisibility(View.GONE);
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(mainScreen, R.layout.add_bookmark_dialog);

        title = (TextView) dialog.findViewById(R.id.title);
        addToSpeedDial = (CheckBox) dialog.findViewById(R.id.add_speed_dial);

        nameEditText = (EditText) dialog.findViewById(R.id.message_edit);
        urlEditText = (EditText) dialog.findViewById(R.id.file_url_edit);

        cancelBnt = (TextView) dialog.findViewById(R.id.cancel_bnt);
        saveBnt = (TextView) dialog.findViewById(R.id.save_bnt);


        saveBnt.setOnClickListener(this);
        cancelBnt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == cancelBnt.getId()) {
            cancelBnt();

        } else if (view.getId() == saveBnt.getId()) {
            saveBnt();
        }
    }

    private void saveBnt() {
        String name = nameEditText.getText().toString();
        String url = urlEditText.getText().toString();

        if (name.length() > 0) {
            close();

            if (isOpenForEditing)
                edit(name, url);
            else
                save(name, url);

        } else {
            mainScreen.vibrator.vibrate(20);
            mainScreen.showSimpleMessageBox("Please give the bookmark a valid name.");
        }
    }

    private void edit(String name, String url) {
        if (speedDialModel != null) {
            speedDialModel.url = url;
            speedDialModel.name = name;
            mainScreen.app.getBookmarkLoader().updateInDisk();

            mainScreen.vibrator.vibrate(20);
            mainScreen.toast("Saved");
            mainScreen.globalObjectStore.speedDialScreen.notifySpeedDialDataChange();

        } else if (bookmarkModel != null) {
            bookmarkModel.url = url;
            bookmarkModel.name = name;
            mainScreen.app.getBookmarkLoader().updateInDisk();

            mainScreen.vibrator.vibrate(20);
            mainScreen.toast("Saved");
            mainScreen.globalObjectStore.bookmarkScreen.notifyBookmarkDataChange();
        }
    }

    private void save(String name, String url) {
        saveBookmark(name, url);
        if (addToSpeedDial.isChecked()) {
            addToSpeedDial(name, url);
        }
        updateFavoriteButtonIcon();
        mainScreen.vibrator.vibrate(20);
        mainScreen.toast("Saved");
    }

    private void saveBookmark(String name, String url) {
        BookmarkLoader bookmarkLoader = mainScreen.app.getBookmarkLoader();
        ArrayList<Bookmark> bookmarks = bookmarkLoader.getBookmarksList();

        Bookmark bookmark = new Bookmark();
        bookmark.name = name;
        bookmark.url = url;

        bookmarks.add(0, bookmark);
        bookmarkLoader.updateInDisk();

        //update the bookmark screen with new data change.
        BookmarkScreen bookmarkScreen = mainScreen.globalObjectStore.bookmarkScreen;
        if (bookmarkScreen != null) {
            bookmarkScreen.notifyBookmarkDataChange();
        }

    }

    private void addToSpeedDial(String name, String url) {
        BookmarkLoader bookmarkLoader = mainScreen.app.getBookmarkLoader();
        ArrayList<SpeedDialModel> speedDialModels = bookmarkLoader.getSpeedDialModelList();

        SpeedDialModel speedDial = new SpeedDialModel();
        speedDial.name = name;
        speedDial.url = url;

        speedDialModels.add(0, speedDial);
        bookmarkLoader.updateInDisk();

        //update the speed_dial_screen with new data change.
        GlobalObjectStore globalObjectStore = mainScreen.globalObjectStore;
        if (globalObjectStore.speedDialScreen != null)
            globalObjectStore.speedDialScreen.notifySpeedDialDataChange();
    }

    private void updateFavoriteButtonIcon() {
        Drawable favoriteButtonPressIcon = mainScreen.getDrawableImage(R.drawable.ic_love_bookmark_press);
        favoriteButton.setImageDrawable(favoriteButtonPressIcon);
    }

    private void cancelBnt() {
        close();
    }

}
