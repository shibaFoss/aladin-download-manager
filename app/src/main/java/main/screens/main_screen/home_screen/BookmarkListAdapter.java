package main.screens.main_screen.home_screen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import main.data_holder.Bookmark;
import net.fdm.R;

import java.util.ArrayList;

public class BookmarkListAdapter extends BaseAdapter {

    public BookmarkScreen bookmarkScreen;
    public ArrayList<Bookmark> bookmarks = new ArrayList<>();

    public BookmarkListAdapter(BookmarkScreen bookmarkScreen) {
        this.bookmarkScreen = bookmarkScreen;
        this.bookmarks = this.bookmarkScreen.app.getBookmarkLoader().getBookmarksList();
    }

    public ArrayList<Bookmark> getBookmarks() {
        return this.bookmarks;
    }

    @Override
    public int getCount() {
        return this.bookmarks.size();
    }

    @Override
    public Object getItem(int position) {
        return bookmarks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(bookmarkScreen.mainScreen).inflate(R.layout.bookmark_list_row_layout, null);
            viewHolder = new ViewHolder();

            viewHolder.position = position;
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Bookmark bookmark = bookmarks.get(position);
        viewHolder.title.setText(bookmark.name);

        return convertView;
    }

    public static class ViewHolder {
        public int position;
        public TextView title;
    }

}
