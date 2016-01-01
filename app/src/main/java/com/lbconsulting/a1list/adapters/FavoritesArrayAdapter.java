package com.lbconsulting.a1list.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListItem;

import java.util.List;

/**
 * An ArrayAdapter for displaying a ListAttributes.
 */
public class FavoritesArrayAdapter extends ArrayAdapter<ListItem> {

    private final Context mContext;

    public FavoritesArrayAdapter(Context context, List<ListItem> data) {
        super(context, 0);
        this.mContext = context;
        setData(data);
        MyLog.i("FavoritesArrayAdapter", "Initialized");
    }

    private void setData(List<ListItem> data) {
        if (data == null) {
            MyLog.i("FavoritesArrayAdapter", "setData: data NULL");
        }
        clear();
        if (data != null) {
            addAll(data);
            MyLog.i("FavoritesArrayAdapter", "Loaded " + data.size() + " ListItems.");
        }
    }

//    @Override
//    public long getItemId(int position) {
//        ListAttributes listAttributes = getItem(position);
//        return listAttributes.getAttributesID();
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//
//    @Override
//    public int getPosition(ListAttributes soughtItem) {
//        return getItemPosition(soughtItem.getLocalUuid());
//    }

//    private int getItemPosition(String soughtAttributesUuid) {
//        int position;
//        boolean found = false;
//
//        ListAttributes item;
//        for (position = 0; position < getCount(); position++) {
//            item = getItem(position);
//            if (item.getLocalUuid().equals(soughtAttributesUuid)) {
//                found = true;
//                break;
//            }
//        }
//
//        if (!found) {
//            position = 0;
//        }
//        return position;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FavoritesViewHolder holder;

        // Get the data item for this position
        ListItem item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_favorites, parent, false);
            holder = new FavoritesViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (FavoritesViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        if (item != null) {
            holder.ckItemName.setText(item.getName());
            boolean isInList = !item.isMarkedForDeletion();
            holder.ckItemName.setChecked(isInList);
            item.setChecked(isInList);
        }

        // save the item so it can be retrieved later
        holder.ckItemName.setTag(item);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        MyLog.i("FavoritesArrayAdapter", "notifyDataSetChanged");
    }

    public void selectCheckedItems() {
        ListItem item;
        for (int i = 0; i < getCount(); i++) {
            item = getItem(i);
            item.setMarkedForDeletion(!item.isChecked());
        }
    }

    private class FavoritesViewHolder {
        public final CheckBox ckItemName;

        public FavoritesViewHolder(View base) {
            ckItemName = (CheckBox) base.findViewById(R.id.ckItemName);
            ckItemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    ListItem clickedItem = (ListItem) v.getTag();
                    if (clickedItem != null) {
                        clickedItem.setChecked(checkBox.isChecked());
                    }
                }
            });
        }
    }
}

