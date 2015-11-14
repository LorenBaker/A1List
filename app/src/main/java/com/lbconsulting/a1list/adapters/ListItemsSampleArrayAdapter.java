package com.lbconsulting.a1list.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListAttributes;

import java.util.List;

/**
 * An ArrayAdapter for displaying a sample list of items.
 */
public class ListItemsSampleArrayAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private final ListView mListView;
    private final String mListName;
    private ListAttributes mAttributes;
//    private ListTitle mListTitle;

    public ListItemsSampleArrayAdapter(Context context, ListView listView, ListAttributes attributes, String listName) {
        super(context, 0);
        this.mContext = context;
        this.mListView = listView;
//        this.mListTitle = listTitle;
        this.mListName = listName;
        this.mAttributes = attributes;
        MyLog.i("ListItemsSampleArrayAdapter", "Initialized for List: " + mListName);
    }

    public void setData(List<String> data) {
        if (data == null) {
            MyLog.i("ListItemsSampleArrayAdapter", "setData: data NULL");
        }
        clear();
        if (data != null) {
            addAll(data);
            MyLog.i("ListItemsSampleArrayAdapter", "Loaded " + data.size() + " items for " + mListName);
        }
    }

    public void setAttributes(ListAttributes attributes) {
        mAttributes = attributes;
    }

//    @Override
//    public long getItemId(int position) {
//        // TODO: Add item ids to data tables
//        ListItem listItem = getItem(position);
//        return listItem.getItemID();
//    }

//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }

//    @Override
//    public int getPosition(ListItem soughtItem) {
//        return getItemPosition(soughtItem.getItemUuid());
//    }

//    private int getItemPosition(String soughtItemUuid) {
//        int position;
//        boolean found = false;
//
//        ListItem item;
//        for (position = 0; position < getCount(); position++) {
//            item = getItem(position);
//            if (item.getItemUuid().equals(soughtItemUuid)) {
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
        // Get the data item for this position
        String item = getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_list_item_name, parent, false);
        TextView tvListItemName = (TextView) convertView.findViewById(R.id.tvListItemName);

        if (mAttributes != null) {
            tvListItemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, mAttributes.getTextSize());
            tvListItemName.setTextColor(mAttributes.getTextColor());

            int horizontalPadding = mAttributes.getHorizontalPaddingPx();
            int verticalPadding = mAttributes.getVerticalPaddingPx();
            tvListItemName.setPadding(horizontalPadding, verticalPadding,
                    horizontalPadding, verticalPadding);

            if (mAttributes.isBackgroundTransparent()) {
                tvListItemName.setBackgroundColor(Color.TRANSPARENT);
                mListView.setDivider(null);
                mListView.setDividerHeight(0);
            } else {
                tvListItemName.setBackground(mAttributes.getBackgroundDrawable());
                mListView.setDivider(new ColorDrawable(ContextCompat.getColor(mContext, R.color.greyLight3_50Transparent)));
                mListView.setDividerHeight(1);
            }
        }
        tvListItemName.setText(item);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        MyLog.i("ListItemsSampleArrayAdapter", "notifyDataSetChanged for List: " + mListName);
    }

}

