package main.screens.main_screen.home_screen;

import android.content.ClipData;
import android.content.Intent;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import main.data_holder.SpeedDialModel;

import java.util.ArrayList;

public abstract class BaseSpeedDialAdapter extends BaseAdapter {

    public abstract ArrayList<SpeedDialModel> getDataList();

    public abstract void onViewClick(int position, BaseSpeedDialViewHolder viewHolder);

    public abstract void onUpdateDataList(BaseSpeedDialAdapter baseSpeedDialAdapter);

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    @Override
    public int getCount() {
        return getDataList().size();
    }

    public void setUpDragNDrop(final int position, final View view, final BaseSpeedDialViewHolder viewHolder) {
        viewHolder.position = position;

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent data = new Intent();
                data.putExtra("position", viewHolder.position);
                view.startDrag(ClipData.newIntent("dataList", data), new View.DragShadowBuilder(view), view, 0);
                return true;
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewClick(position, viewHolder);
            }
        });

        view.setOnDragListener(new View.OnDragListener() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean onDrag(View view, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        int dragPosition = event.getClipData().getItemAt(0).getIntent().getIntExtra("position", -1);
                        if (dragPosition < 0) return false;

                        SpeedDialModel model = getDataList().get(position);

                        getDataList().remove(dragPosition);
                        getDataList().add(viewHolder.position, model);
                        onUpdateDataList(BaseSpeedDialAdapter.this);
                        BaseSpeedDialAdapter.this.notifyDataSetChanged();
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        GridView parent = (GridView) view.getParent();
                        int position = viewHolder.position;
                        if (position > parent.getLastVisiblePosition() - parent.getNumColumns()) {
                            parent.smoothScrollByOffset(1);
                        }
                        if (position < parent.getFirstVisiblePosition() + parent.getNumColumns()) {
                            parent.smoothScrollByOffset(-1);
                        }
                        break;

                    default:
                        return false;
                }

                return true;
            }
        });

    }
}