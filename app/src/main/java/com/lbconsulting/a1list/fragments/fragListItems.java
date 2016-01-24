package com.lbconsulting.a1list.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.adapters.ListItemsArrayAdapter;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A fragment that shows master list of grocery items
 */
public class fragListItems extends Fragment {
    private static final String ARG_LIST_TITLE_UUID = "argListTitleUuid";
    private com.nhaarman.listviewanimations.itemmanipulation.DynamicListView lvListItems;
    private String mListTitleUuid;
    private ListTitle mListTitle;
    private ListAttributes mAttributes;
    private LinearLayout llListItems;
    private ListItemsArrayAdapter mListItemsArrayAdapter;


    public fragListItems() {
        // Required empty public constructor
    }

    public static fragListItems newInstance(String listTitleUuid) {
        fragListItems frag = new fragListItems();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_TITLE_UUID, listTitleUuid);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListTitle = null;
        Bundle args = getArguments();
        if (args.containsKey(ARG_LIST_TITLE_UUID)) {
            mListTitleUuid = args.getString(ARG_LIST_TITLE_UUID);
            refreshListTitle(mListTitleUuid, "onCreate");
        } else {
            MyLog.e("fragListItems", "onCreate: No ListTitle found!");
        }
        EventBus.getDefault().register(this);
        MyLog.i("fragListItems", "onCreate complete: " + mListTitle.getName());
    }

    public void onEvent(MyEvents.updateListUI event) {
        if (event.getListTitleUuid() == null) {
            new LoadListItems(mListTitle).execute();
        } else if (mListTitle.getListTitleUuid().equals(event.getListTitleUuid())) {
            new LoadListItems(mListTitle).execute();
        }
    }
    private void refreshListTitle(String listTitleUuid, String source) {
        if (listTitleUuid != null && !listTitleUuid.equals(MySettings.NOT_AVAILABLE)) {
            mListTitle = ListTitle.getListTitle(listTitleUuid);
            if (mListTitle != null) {
                mAttributes = mListTitle.getAttributes();
                MyLog.i("fragListItems", source + " refreshListTitle " + mListTitle.getName());
            } else {
                MyLog.e("fragListItems", source + ": listTitleUuid = " + listTitleUuid + " Unable to find ListTitle");
            }
        } else {
            MyLog.e("fragListItems", source + ": listTitleUuid in null or NOT_AVAILABLE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("fragListItems", "onCreateView: " + mListTitle.getName());
        View rootView = inflater.inflate(R.layout.frag_list_items, container, false);

        llListItems = (LinearLayout) rootView.findViewById(R.id.llListItems);
        llListItems.setBackground(mAttributes.getBackgroundDrawable());

        lvListItems = (com.nhaarman.listviewanimations.itemmanipulation.DynamicListView) rootView.findViewById(R.id.lvListItems);
        lvListItems.setLongClickable(true);

        // Set up the ListView adapter
        mListItemsArrayAdapter = new ListItemsArrayAdapter(getActivity(), lvListItems, mListTitle);
        lvListItems.setAdapter(mListItemsArrayAdapter);

        // Load list items async
        new LoadListItems(mListTitle).execute();

        // setup dragDrop
        if (!mListTitle.sortListItemsAlphabetically()) {
            lvListItems.enableDragAndDrop();
            lvListItems.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                                       final int position, final long id) {
                            lvListItems.startDragging(position);
                            return true;
                        }
                    }
            );
        } else {
            lvListItems.disableDragAndDrop();
            lvListItems.setOnItemLongClickListener(null);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragListItems", "onActivityCreated: " + mListTitle.getName());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("fragListItems", "onSaveInstanceState: " + mListTitle.getName());
        outState.putString(ARG_LIST_TITLE_UUID, mListTitle.getListTitleUuid());
        /*
        Called to ask the fragment to save its current dynamic state,
        so it can later be reconstructed in a new instance if its process is restarted.
        If a new instance of the fragment later needs to be created, the data you place
        in the Bundle here will be available in the Bundle given to onCreate(Bundle),
        onCreateView(LayoutInflater, ViewGroup, Bundle), and onActivityCreated(Bundle).
        
        Note however: this method may be called at any time before onDestroy(). 
        
        There are many situations where a fragment may be mostly torn down (such as when placed on 
        the back stack with no UI showing), but its state will not be saved until its owning 
        activity actually needs to save its state.
        */
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_LIST_TITLE_UUID)) {
            mListTitleUuid = savedInstanceState.getString(ARG_LIST_TITLE_UUID);
            refreshListTitle(mListTitleUuid, "onViewStateRestored");
        }
        MyLog.i("fragListItems", "onViewStateRestored: " + mListTitle.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragListItems", "onResume: " + mListTitle.getName());
        refreshListTitle(mListTitleUuid, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragListItems", "onPause: " + mListTitle.getName());
        mListTitle.setIsForceViewInflation(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MyLog.i("fragListItems", "onDestroy: " + mListTitle.getName());
    }

    private class LoadListItems extends AsyncTask<Void, Void, Void> {
        private final ListTitle mListTitle;
        private List<ListItem> listItemData;

        public LoadListItems(ListTitle listTitle) {
            mListTitle = listTitle;
        }

        @Override
        protected Void doInBackground(Void... params) {
            listItemData = ListItem.getAllListItems(mListTitle);
            MyLog.i("LoadListItems", "doInBackground; " + mListTitle.getName() + " found " + listItemData.size() + " items.");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mListItemsArrayAdapter.setData(listItemData, mAttributes);
            mListItemsArrayAdapter.setAttributes(mAttributes);

            mListItemsArrayAdapter.notifyDataSetChanged();
            llListItems.setBackground(mAttributes.getBackgroundDrawable());
            super.onPostExecute(aVoid);
        }
    }

}
