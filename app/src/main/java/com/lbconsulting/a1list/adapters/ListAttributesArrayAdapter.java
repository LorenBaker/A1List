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
 * An ArrayAdapter for displaying a ListAttributes.
 */
public class ListAttributesArrayAdapter extends ArrayAdapter<ListAttributes> {

    private final Context mContext;
    private final ListView mListView;

    public ListAttributesArrayAdapter(Context context, ListView listView) {
        super(context, 0);
        this.mContext = context;
        this.mListView = listView;
        MyLog.i("ListAttributesArrayAdapter", "Initialized");
    }

    public void setData(List<ListAttributes> data) {
        if (data == null) {
            MyLog.i("ListAttributesArrayAdapter", "setData: data NULL");
        }
        clear();
        if (data != null) {
            addAll(data);
            MyLog.i("ListAttributesArrayAdapter", "Loaded " + data.size() + " ListAttributes.");
        }
    }

    @Override
    public long getItemId(int position) {
        ListAttributes listAttributes = getItem(position);
        return listAttributes.getAttributesID();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getPosition(ListAttributes soughtItem) {
        return getItemPosition(soughtItem.getLocalUuid());
    }

    private int getItemPosition(String soughtAttributesUuid) {
        int position;
        boolean found = false;

        ListAttributes item;
        for (position = 0; position < getCount(); position++) {
            item = getItem(position);
            if (item.getLocalUuid().equals(soughtAttributesUuid)) {
                found = true;
                break;
            }
        }

        if (!found) {
            position = 0;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListAttributesViewHolder holder;

        // Get the data item for this position
        ListAttributes attributes = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item_name, parent, false);
            holder = new ListAttributesViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ListAttributesViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        if (attributes != null) {
            holder.tvListAttributesName.setText(attributes.getName());
            holder.tvListAttributesName.setTextSize(TypedValue.COMPLEX_UNIT_SP, attributes.getTextSize());
            holder.tvListAttributesName.setTextColor(attributes.getTextColor());

            int horizontalPadding = attributes.getHorizontalPaddingPx();
            int verticalPadding = attributes.getVerticalPaddingPx();
            holder.tvListAttributesName.setPadding(horizontalPadding, verticalPadding,
                    horizontalPadding, verticalPadding);

            if (attributes.isBackgroundTransparent()) {
                holder.tvListAttributesName.setBackgroundColor(Color.TRANSPARENT);
                mListView.setDivider(null);
                mListView.setDividerHeight(0);
            } else {
                holder.tvListAttributesName.setBackground(attributes.getBackgroundDrawable());
                mListView.setDivider(new ColorDrawable(ContextCompat.getColor(mContext, R.color.greyLight3_50Transparent)));
                mListView.setDividerHeight(1);
            }
        }

        // save the item so it can be retrieved later
        holder.tvListAttributesName.setTag(attributes);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        MyLog.i("ListAttributesArrayAdapter", "notifyDataSetChanged");
    }

    private class ListAttributesViewHolder {
        public final TextView tvListAttributesName;

        public ListAttributesViewHolder(View base) {
            tvListAttributesName = (TextView) base.findViewById(R.id.tvItemName);
        }
    }
}

