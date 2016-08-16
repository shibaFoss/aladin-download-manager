package main.screens.main_screen.home_screen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import main.data_holder.SpeedDialModel;
import net.fdm.R;

/**
 * The GridView adapter of speed dial grid view.
 */
public class SpeedDialGridAdapter extends BaseAdapter {

    SpeedDialScreen speedDialScreen;


    public SpeedDialGridAdapter(SpeedDialScreen speedDialScreen) {
        this.speedDialScreen = speedDialScreen;
    }


    @Override
    public int getCount() {
        return speedDialScreen.getSpeedDialList().size();
    }


    @Deprecated
    @Override
    public Object getItem(int position) {
        return speedDialScreen.getSpeedDialList().get(position);
    }


    @Deprecated
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(
                    speedDialScreen.getMainScreen()).inflate(R.layout.speed_dial_grid_layout_row, null);

            viewHolder = new ViewHolder(position);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            setRandomColor(convertView, position);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        SpeedDialModel speedDialModel = speedDialScreen.getSpeedDialList().get(position);
        viewHolder.speedDialModel = speedDialModel;
        viewHolder.title.setText(speedDialModel.name);

        return convertView;
    }


    private int getRandomPatternNumber(int position, int max) {
        if (position > max) {
            int index = position - max;
            while (index > max) {
                index = (index - max);
            }
            return index;
        }

        return position;
    }


    private void setRandomColor(View convertView, int position) {
        int index = getRandomPatternNumber(position, 8);

        if (index == 0) {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_0));
        } else if (index == 1) {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_1));
        } else if (index == 2) {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_2));
        } else if (index == 3) {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_3));
        } else if (index == 4) {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_4));
        } else if (index == 5) {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_5));
        } else if (index == 6) {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_6));
        } else if (index == 7) {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_7));
        } else if (index == 8) {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_8));
        } else {
            convertView.findViewById(R.id.root_layout)
                    .setBackgroundColor(speedDialScreen.getResources().getColor(R.color.color_0));
        }
    }


    static class ViewHolder {
        int position;
        SpeedDialModel speedDialModel;
        TextView title;


        public ViewHolder(int position) {
            this.position = position;
        }
    }

}
