package com.lbconsulting.a1list.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.lbconsulting.a1list.classes.CommonMethods;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.LocalListAttributes;

import java.util.List;

/**
 * An ArrayAdapter for displaying a sample list of items.
 */
public class ListItemsSampleArrayAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private final ListView mListView;
    private final String mListName;
    private LocalListAttributes mAttributes;
//    private ListTitle mListTitle;

    public ListItemsSampleArrayAdapter(Context context, ListView listView, LocalListAttributes attributes, String listName) {
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

    public void setAttributes(LocalListAttributes attributes) {
        mAttributes = attributes;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemViewHolder holder;

        // Get the data item for this position
        String item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_list_item_name, parent, false);
            holder = new ListItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ListItemViewHolder) convertView.getTag();
        }

        if (mAttributes != null) {
            holder.tvListItemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, mAttributes.getTextSize());
            holder.tvListItemName.setTextColor(mAttributes.getTextColor());

            int horizontalPadding = CommonMethods.convertDpToPixel(mAttributes.getHorizontalPaddingInDp());
            int verticalPadding = CommonMethods.convertDpToPixel(mAttributes.getVerticalPaddingInDp());
            holder.tvListItemName.setPadding(horizontalPadding, verticalPadding,
                    horizontalPadding, verticalPadding);

            if (mAttributes.isTransparent()) {
                holder.tvListItemName.setBackgroundColor(Color.TRANSPARENT);
                mListView.setDivider(null);
                mListView.setDividerHeight(0);
            } else {
                holder.tvListItemName.setBackground(mAttributes.getBackgroundDrawable());
                mListView.setDivider(new ColorDrawable(ContextCompat.getColor(mContext, R.color.greyLight3_50Transparent)));
                mListView.setDividerHeight(1);
            }

            if (mAttributes.isBold()) {
                holder.tvListItemName.setTypeface(null, Typeface.BOLD);
            }else{
                holder.tvListItemName.setTypeface(null, Typeface.NORMAL);
            }
        }
        holder.tvListItemName.setText(item);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        MyLog.i("ListItemsSampleArrayAdapter", "notifyDataSetChanged for List: " + mListName);
    }

    private class ListItemViewHolder {
        public final TextView tvListItemName;

        public ListItemViewHolder(View base) {
            tvListItemName = (TextView) base.findViewById(R.id.tvListItemName);
        }
    }
}

