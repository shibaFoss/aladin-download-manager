package main.data_holder;

import main.app.App;
import main.settings.UserSettings;
import main.utilities.LogHelper;

import java.io.File;
import java.util.ArrayList;

import static main.utilities.FileUtility.makeDirectory;

/**
 * BookmarkLoader is the main class from where you can get and write/save your
 * bookmark-list/speed-dial-list.<br>
 * The class saved and read automatically from the sdcard by the app itself, so you can get ony one
 * intense of the object from the {@link App#getBookmarkLoader()} method.
 *
 * @author Shiba.
 */
public class BookmarkLoader extends BaseWritableObject {

    private static LogHelper logHelper = new LogHelper(BookmarkLoader.class);

    public static final String BOOKMARKS_CONFIG_FILE = "bookmarks.cofig";
    public static final String BOOKMARK_FOLDER_PATH = UserSettings.settingPath + "/.bookmarkList";


    private ArrayList<SpeedDialModel> speedDialModels = new ArrayList<>();
    private ArrayList<Bookmark> bookmarks = new ArrayList<>();

    /**
     * The public constructor of the BookmarkLoader class.
     */
    private BookmarkLoader() {
        File bookmarkFolder = new File(BOOKMARK_FOLDER_PATH);
        if (!bookmarkFolder.exists()) {
            if (!makeDirectory(bookmarkFolder)) {
                logHelper.e("The bookmark folder can not be created due to some unexpected errors.");
                return;
            }
        }

        installDefaultSpeedDials();
        updateInDisk();
    }

    /**
     * Install the default speed-dial web sites to the speed-dial data base.
     */
    private void installDefaultSpeedDials() {
        getSpeedDialModelList().add(new SpeedDialModel("Google", "http://google.com"));
        getSpeedDialModelList().add(new SpeedDialModel("Yahoo", "http://yahoo.com"));
        getSpeedDialModelList().add(new SpeedDialModel("Duck Duck", "http://duckduckgo.com"));
        getSpeedDialModelList().add(new SpeedDialModel("Duck Duck GO", "http://bing.com"));
        getSpeedDialModelList().add(new SpeedDialModel("Ask.com", "http://ask.com"));

        getSpeedDialModelList().add(new SpeedDialModel("Mp4MobileMovies", "http://mp4mobilemovies.net"));

        getSpeedDialModelList().add(new SpeedDialModel("BossMobi", "http://www.bossmobi.com"));
        getSpeedDialModelList().add(new SpeedDialModel("Mp3Skull", "https://mp3skull.la/mp3/free_music.html"));

        getBookmarksList().add(new Bookmark("Google", "http://google.com"));
        getBookmarksList().add(new Bookmark("Yahoo", "http://yahoo.com"));
        getBookmarksList().add(new Bookmark("Duck Duck", "http://duckduckgo.com"));
        getBookmarksList().add(new Bookmark("Duck Duck GO", "http://bing.com"));
        getBookmarksList().add(new Bookmark("Ask.com", "http://ask.com"));

        getBookmarksList().add(new Bookmark("Mp4MobileMovies", "http://mp4mobilemovies.net"));

        getBookmarksList().add(new Bookmark("BossMobi", "http://www.bossmobi.com"));
        getBookmarksList().add(new Bookmark("Mp3Skull", "https://mp3skull.la/mp3/free_music.html"));
    }

    /**
     * Read the {@link BookmarkLoader} class from the sd card.
     */
    public static BookmarkLoader readFromDisk() {
        File bookmarkFile = new File(BOOKMARK_FOLDER_PATH, BOOKMARKS_CONFIG_FILE);
        BookmarkLoader bookmarkLoaderObject = null;
        //we try to read the object from the file first.
        if (bookmarkFile.exists())
            bookmarkLoaderObject = (BookmarkLoader) readObject(bookmarkFile);

        //if we did get the success of reading out the object from the sdcard.
        //we will create a new empty default bookmarkLoader object.
        if (bookmarkLoaderObject == null)
            bookmarkLoaderObject = new BookmarkLoader();

        return bookmarkLoaderObject;
    }

    /**
     * Get the list of total speed-dial objects.
     */
    public ArrayList<SpeedDialModel> getSpeedDialModelList() {
        return speedDialModels;
    }

    /**
     * Replace the speed-dial-model list with a new list.
     */
    public void replaceSpeedDialModelList(ArrayList<SpeedDialModel> speedDialModels) {
        this.speedDialModels = speedDialModels;
        this.updateInDisk();
    }

    /**
     * Returns the list of total bookmark objects.
     */
    public ArrayList<Bookmark> getBookmarksList() {
        return bookmarks;
    }

    /**
     * Replace the bookmark list with a new list.
     */
    public void replaceBookmarksList(ArrayList<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
        this.updateInDisk();
    }


    /**
     * Update and save the bookmark-loader object in the disk memory(sd-card).
     */
    public void updateInDisk() {
        writeObject(this, BOOKMARK_FOLDER_PATH, BOOKMARKS_CONFIG_FILE);
    }

}
