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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListTitle;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * An ArrayAdapter for displaying a ListTitle.
 */
public class ListTitleArrayAdapter extends ArrayAdapter<ListTitle> implements Swappable {

    private final Context mContext;
    private final ListView mListView;

    public ListTitleArrayAdapter(Context context, ListView listView) {
        super(context, 0);
        this.mContext = context;
        this.mListView = listView;
        MyLog.i("ListTitleArrayAdapter", "Initialized");
    }

    public void setData(List<ListTitle> data) {
        if (data == null) {
            MyLog.i("ListTitleArrayAdapter", "setData: data NULL");
        }
        clear();
        if (data != null) {
            addAll(data);
            MyLog.i("ListTitleArrayAdapter", "Loaded " + data.size() + " ListTitle.");
        }
    }

    @Override
    public long getItemId(int position) {
        ListTitle listTitle = getItem(position);
        return listTitle.getListID();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getPosition(ListTitle soughtItem) {
        return getItemPosition(soughtItem.getListTitleUuid());
    }

    private int getItemPosition(String soughtAttributesUuid) {
        int position;
        boolean found = false;

        ListTitle item;
        for (position = 0; position < getCount(); position++) {
            item = getItem(position);
            if (item.getListTitleUuid().equals(soughtAttributesUuid)) {
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
        ListTitleViewHolder holder;

        // Get the data item for this position
        ListTitle listTitle = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_list_title, parent, false);
            holder = new ListTitleViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ListTitleViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        ListAttributes attributes = listTitle.getAttributes();
        if (attributes != null) {
            holder.tvListTitleName.setText(listTitle.getName());
            holder.tvListTitleName.setTextSize(TypedValue.COMPLEX_UNIT_SP, attributes.getTextSize());
            holder.tvListTitleName.setTextColor(attributes.getTextColor());

            int horizontalPadding = attributes.getHorizontalPaddingPx();
            int verticalPadding = attributes.getVerticalPaddingPx();
            holder.tvListTitleName.setPadding(horizontalPadding, verticalPadding,
                    horizontalPadding, verticalPadding);

            if (attributes.isBackgroundTransparent()) {
                holder.tvListTitleName.setBackgroundColor(Color.TRANSPARENT);
                mListView.setDivider(null);
                mListView.setDividerHeight(0);
            } else {
                holder.llRowListTitle.setBackground(attributes.getBackgroundDrawable());
                mListView.setDivider(new ColorDrawable(ContextCompat.getColor(mContext, R.color.greyLight3_50Transparent)));
                mListView.setDividerHeight(1);
            }
        }

        if(listTitle.isListLocked()){
            holder.btnLockList.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lock_black));
        }else{
            holder.btnLockList.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lock_open_black));
        }

        holder.btnLockList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton)v;
                ListTitle selectedListTitle = (ListTitle) v.getTag();
                selectedListTitle.setLockList(!selectedListTitle.isListLocked());
                if(selectedListTitle.isListLocked()){
                    button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lock_black));
                }else{
                    button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_lock_open_black));
                }
            }
        });

        holder.btnEditListTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListTitle selectedListTitle = (ListTitle) v.getTag();
                EventBus.getDefault().post(new MyEvents.showEditListTitleDialog(selectedListTitle.getListTitleUuid()));
            }
        });

        // save the item so it can be retrieved later
        holder.btnEditListTitle.setTag(listTitle);
        holder.btnLockList.setTag(listTitle);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        MyLog.i("ListTitleArrayAdapter", "notifyDataSetChanged");
    }

    @Override
    public void swapItems(int positionOne, int positionTwo) {
        if (MySettings.isAlphabeticallySortNavigationMenu()) {
            return;
        }
        ListTitle itemOne = getItem(positionOne);
        ListTitle itemTwo = getItem(positionTwo);

        long origItemOneSortKey = itemOne.getListTitleManualSortKey();
        long origItemTwoSortKey = itemTwo.getListTitleManualSortKey();

        itemOne.setListTitleManualSortKey(origItemTwoSortKey);
        itemTwo.setListTitleManualSortKey(origItemOneSortKey);

        EventBus.getDefault().post(new MyEvents.updateListTitleUI());
    }

    private class ListTitleViewHolder {
        public final LinearLayout llRowListTitle;
        public final TextView tvListTitleName;
        public final ImageButton btnLockList;
        public final ImageButton btnEditListTitle;

        public ListTitleViewHolder(View base) {
            llRowListTitle = (LinearLayout) base.findViewById(R.id.llRowListTitle);
            tvListTitleName = (TextView) base.findViewById(R.id.tvListTitleName);
            btnLockList = (ImageButton) base.findViewById(R.id.btnLockList);
            btnEditListTitle = (ImageButton) base.findViewById(R.id.btnEditListTitle);
        }
    }
}

