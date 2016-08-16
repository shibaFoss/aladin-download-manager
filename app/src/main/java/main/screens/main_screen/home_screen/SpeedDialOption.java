package main.screens.main_screen.home_screen;

import android.app.Dialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import main.app.App;
import main.data_holder.BookmarkLoader;
import main.data_holder.SpeedDialModel;
import main.screens.BaseScreen;
import main.screens.main_screen.GlobalObjectStore;
import main.screens.main_screen.MainScreen;
import main.screens.main_screen.search_screen.AddBookmarkDialog;
import main.utilities.UiUtils;
import net.fdm.R;

public class SpeedDialOption implements View.OnClickListener {

    public BaseScreen baseScreen;
    public Dialog dialog;
    public TextView title, renameBnt, deleteBnt;
    public SpeedDialScreen speedDialScreen;
    public SpeedDialModel speedDialModel;

    public SpeedDialOption(SpeedDialScreen speedDialScreen) {
        this.speedDialScreen = speedDialScreen;
        this.baseScreen = speedDialScreen.getMainScreen();
        this.init();
    }

    public void show() {
        dialog.show();
    }

    public void close() {
        dialog.dismiss();
    }

    private void init() {
        dialog = UiUtils.generateNewDialog(baseScreen, R.layout.speed_dial_n_bookmark_option_dialog);

        title = (TextView) dialog.findViewById(R.id.title);
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
        MainScreen mainScreen = speedDialScreen.getMainScreen();
        App app = mainScreen.app;
        BookmarkLoader bookmarkLoader = app.getBookmarkLoader();
        bookmarkLoader.getSpeedDialModelList().remove(speedDialModel);
        bookmarkLoader.updateInDisk();

        mainScreen.vibrator.vibrate(20);
        mainScreen.toast("Deleted");
        speedDialScreen.notifySpeedDialDataChange();

        /*for (SpeedDialModel model : bookmarkLoader.getSpeedDialModelList()) {
            if (model.url.equals(this.bookmark.url)) {

            }
        }*/
    }

    private void renameBnt() {
        close();
        AddBookmarkDialog bookmarkDialog = new AddBookmarkDialog(speedDialScreen.getMainScreen());
        bookmarkDialog.setDefaultValue(speedDialModel.url, speedDialModel.name);
        bookmarkDialog.openForSpeedDialEditing(speedDialModel);

        GlobalObjectStore globalObjectStore = speedDialScreen.getMainScreen().globalObjectStore;
        ImageButton favoriteButton = globalObjectStore.searchScreen.favoriteButton;
        bookmarkDialog.setFavoriteButton(favoriteButton);

        bookmarkDialog.show();
    }

    public SpeedDialOption setSpeedDialModel(SpeedDialModel model) {
        this.speedDialModel = model;
        return this;
    }
}
