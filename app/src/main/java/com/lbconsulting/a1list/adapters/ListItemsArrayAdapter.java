package com.lbconsulting.a1list.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * An ArrayAdapter for displaying a ListItems.
 */
public class ListItemsArrayAdapter extends ArrayAdapter<ListItem> implements Swappable{

    private final Context mContext;
    private final ListView mListView;
    private final String mListName;
    private ListAttributes mAttributes;
    private ListTitle mListTitle;

    public ListItemsArrayAdapter(Context context, ListView listView, ListTitle listTitle) {
        super(context, 0);
        this.mContext = context;
        this.mListView = listView;
        this.mListTitle = listTitle;
        this.mListName = listTitle.getName();
        MyLog.i("ListItemsArrayAdapter", "Initialized for List: " + mListName);
    }

    public void setData(List<ListItem> data) {
        if(data== null){
            MyLog.i("ListItemsArrayAdapter", "setData: data NULL");
        }
        clear();
        if (data != null) {
            addAll(data);
            MyLog.i("ListItemsArrayAdapter", "Loaded " + data.size() + " items for " + mListName);
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO: Add item ids to data tables
        ListItem listItem = getItem(position);
        return  listItem.getItemID();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getPosition(ListItem soughtItem) {
        return getItemPosition(soughtItem.getItemUuid());
    }

    private int getItemPosition(String soughtItemUuid) {
        int position;
        boolean found = false;

        ListItem item;
        for (position = 0; position < getCount(); position++) {
            item = getItem(position);
            if (item.getItemUuid().equals(soughtItemUuid)) {
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
        ListItemViewHolder holder;

        // Get the data item for this position
        ListItem item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_list_item_name, parent, false);
            holder = new ListItemViewHolder(convertView);

            mAttributes = item.getAttributes();
            if (mAttributes != null) {
                holder.tvListItemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, mAttributes.getTextSize());
                holder.tvListItemName.setTextColor(mAttributes.getTextColor());

                int horizontalPadding = mAttributes.getHorizontalPaddingPx();
                int verticalPadding = mAttributes.getVerticalPaddingPx();
                holder.tvListItemName.setPadding(horizontalPadding, verticalPadding,
                        horizontalPadding, verticalPadding);

                if (mAttributes.isBackgroundTransparent()) {
                    holder.tvListItemName.setBackgroundColor(Color.TRANSPARENT);
                    mListView.setDivider(null);
                    mListView.setDividerHeight(0);
                } else {
                    holder.tvListItemName.setBackground(mAttributes.getBackgroundDrawable());
                    mListView.setDivider(new ColorDrawable(ContextCompat.getColor(mContext, R.color.greyLight3_50Transparent)));
                    mListView.setDividerHeight(1);
                }
            }
            convertView.setTag(holder);
        } else {
            holder = (ListItemViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        holder.tvListItemName.setText(item.getName());
        if (item.isStruckOut()) {
            setStrikeOut(holder.tvListItemName);
        } else {
            setNoStrikeOut(holder.tvListItemName);
        }

        // save the item so it can be retrieved later
        holder.tvListItemName.setTag(item);

/*        holder.tvListItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                final ListItem clickedItem = (ListItem) v.getTag();

                // toggle the item's struckOut attribute
                clickedItem.toggleStrikeout();
                clickedItem.setListItemDirty(true);

                // set the TextView's attributes to either strikeout or normal as appropriate
                if (clickedItem.isStruckOut()) {
                    setStrikeOut(tv);
                } else {
                    setNoStrikeOut(tv);
                }
            }
        });*/

        // Return the completed view to render on screen
        return convertView;
    }

    public void toggleStrikeout(int position, TextView tv){
        final ListItem clickedItem = getItem(position);
        // toggle the item's struckOut attribute
        clickedItem.toggleStrikeout();
//        clickedItem.setListItemDirty(true);

        // set the TextView's attributes to either strikeout or normal as appropriate
        if (clickedItem.isStruckOut()) {
            setStrikeOut(tv);
        } else {
            setNoStrikeOut(tv);
        }
    }

    private void setStrikeOut(TextView tv) {
        if (mAttributes.isBold()) {
            tv.setTypeface(null, Typeface.BOLD_ITALIC);
        } else {
            tv.setTypeface(null, Typeface.ITALIC);
        }
        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void setNoStrikeOut(TextView tv) {
        if (mAttributes.isBold()) {
            tv.setTypeface(null, Typeface.BOLD);
        } else {
            tv.setTypeface(null, Typeface.NORMAL);
        }
        tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        MyLog.i("ListItemsArrayAdapter", "notifyDataSetChanged for List: " + mListName);
    }



    @Override
    public void swapItems(int positionOne, int positionTwo) {
        if(mListTitle.sortListItemsAlphabetically()){
            return;
        }
        ListItem itemOne = getItem(positionOne);
        ListItem itemTwo = getItem(positionTwo);

        long origItemOneSortKey = itemOne.getListItemManualSortKey();
        long origItemTwoSortKey = itemTwo.getListItemManualSortKey();

        itemOne.setListItemManualSortKey(origItemTwoSortKey);
        itemTwo.setListItemManualSortKey(origItemOneSortKey);

//        itemOne.setListItemDirty(true);
//        itemTwo.setListItemDirty(true);

        EventBus.getDefault().post(new MyEvents.updateListUI());


    }

    private class ListItemViewHolder {
        public final TextView tvListItemName;

        public ListItemViewHolder(View base) {
            tvListItemName = (TextView) base.findViewById(R.id.tvListItemName);
        }
    }
}

