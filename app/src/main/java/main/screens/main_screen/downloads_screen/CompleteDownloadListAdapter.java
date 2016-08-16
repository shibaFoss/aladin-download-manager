package main.screens.main_screen.downloads_screen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import main.download_manager.DownloadModel;
import main.screens.main_screen.MainScreen;
import main.utilities.FileCatalog;
import net.fdm.R;

import java.util.ArrayList;

public class CompleteDownloadListAdapter extends BaseAdapter {

    private MainScreen mainScreen;
    private ArrayList<DownloadModel> dataList;

    public CompleteDownloadListAdapter(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.dataList = new ArrayList<>();
    }

    public void setDataList(ArrayList<DownloadModel> dataList) {
        this.dataList = dataList;
    }

    public void notifyDataChange() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (dataList == null) {
            return 0;
        }
        return this.dataList.size();
    }

    @Override
    public Object getItem(int index) {
        if (dataList == null) {
            return null;
        }
        return dataList.get(index);
    }

    @Override
    public long getItemId(int index) {
        if (dataList == null) {
            return 0;
        }
        return dataList.get(index).id;
    }

    @Override
    public View getView(int index, View listItemView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (listItemView == null) {
            LayoutInflater inflater = LayoutInflater.from(mainScreen);
            listItemView = inflater.inflate(R.layout.complete_download_list_row_layout, null);

            //set the white background.
            listItemView.setBackgroundColor(mainScreen.getColor(R.color.white));

            //create a view holder object and tag the object to the
            //list-item-view.
            viewHolder = new ViewHolder();
            viewHolder.fileName = (TextView) listItemView.findViewById(R.id.file_name);

            listItemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) listItemView.getTag();
        }

        String fileName = dataList.get(index).fileName;
        viewHolder.fileName.setText(fileName);

        int fileThumbIconId = FileCatalog.getDownloadDrawableBy(fileName);
        int lockIconId = dataList.get(index).isLock ? R.drawable.ic_lock_small : 0;

        viewHolder.fileName.
                setCompoundDrawablesWithIntrinsicBounds(fileThumbIconId, 0, lockIconId, 0);

        return listItemView;
    }


    public static class ViewHolder {
        public TextView fileName;
    }
}
