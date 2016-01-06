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
import com.lbconsulting.a1list.database.ListAttributes;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * An ArrayAdapter for displaying a Theme names.
 */
public class ThemeNameArrayAdapter extends ArrayAdapter<ListAttributes> {

    private final Context mContext;
    private final ListView mListView;

    public ThemeNameArrayAdapter(Context context, ListView listView) {
        super(context, 0);
        this.mContext = context;
        this.mListView = listView;
        MyLog.i("ThemeNameArrayAdapter", "Initialized");
    }

    public void setData(List<ListAttributes> data) {
        if (data == null) {
            MyLog.i("ThemeNameArrayAdapter", "setData: data NULL");
        }
        clear();
        if (data != null) {
            addAll(data);
            MyLog.i("ThemeNameArrayAdapter", "Loaded " + data.size() + " ListAttributes.");
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_themes, parent, false);
            holder = new ListAttributesViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ListAttributesViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        if (attributes != null) {
            holder.tvThemeName.setText(attributes.getName());
            holder.tvThemeName.setTextSize(TypedValue.COMPLEX_UNIT_SP, attributes.getTextSize());
            holder.tvThemeName.setTextColor(attributes.getTextColor());

            int horizontalPadding = attributes.getHorizontalPaddingPx();
            int verticalPadding = attributes.getVerticalPaddingPx();
            holder.tvThemeName.setPadding(horizontalPadding, verticalPadding,
                    horizontalPadding, verticalPadding);

            if (attributes.isBackgroundTransparent()) {
                holder.llRowThemeName.setBackgroundColor(Color.TRANSPARENT);
                mListView.setDivider(null);
                mListView.setDividerHeight(0);
            } else {
                holder.llRowThemeName.setBackground(attributes.getBackgroundDrawable());
                mListView.setDivider(new ColorDrawable(ContextCompat.getColor(mContext, R.color.greyLight3_50Transparent)));
                mListView.setDividerHeight(1);
            }
        }

        holder.tvThemeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListAttributes listAttributes = (ListAttributes) v.getTag();
                showEditThemeNameDialog(listAttributes.getLocalUuid());
            }
        });

        holder.btnEditThemeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListAttributes listAttributes = (ListAttributes) v.getTag();
                showEditThemeNameDialog(listAttributes.getLocalUuid());
            }
        });

        // save the item so it can be retrieved later
        holder.tvThemeName.setTag(attributes);
        holder.btnEditThemeName.setTag(attributes);

        // Return the completed view to render on screen
        return convertView;
    }

    private void showEditThemeNameDialog(String attributesUuid) {
        EventBus.getDefault().post(new MyEvents.showEditAttributesNameDialog(attributesUuid));
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        MyLog.i("ThemeNameArrayAdapter", "notifyDataSetChanged");
    }

    private class ListAttributesViewHolder {
        public final LinearLayout llRowThemeName;
        public final TextView tvThemeName;
        public final ImageButton btnEditThemeName;

        public ListAttributesViewHolder(View base) {
            llRowThemeName = (LinearLayout) base.findViewById(R.id.llRowThemeName);
            tvThemeName = (TextView) base.findViewById(R.id.tvThemeName);
            btnEditThemeName = (ImageButton) base.findViewById(R.id.btnEditThemeName);
        }
    }
}

