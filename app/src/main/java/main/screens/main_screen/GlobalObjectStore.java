package main.screens.main_screen;

import main.screens.main_screen.home_screen.BookmarkScreen;
import main.screens.main_screen.home_screen.SpeedDialScreen;
import main.screens.main_screen.search_screen.SearchScreen;

@SuppressWarnings("FieldCanBeLocal")
public class GlobalObjectStore {

    private MainScreen mainScreen;

    public SpeedDialScreen speedDialScreen;
    public SearchScreen searchScreen;
    public BookmarkScreen bookmarkScreen;

    public GlobalObjectStore(MainScreen screen) {
        this.mainScreen = screen;
    }


}
