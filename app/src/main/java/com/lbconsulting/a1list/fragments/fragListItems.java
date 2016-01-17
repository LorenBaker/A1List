package com.lbconsulting.a1list.fragments;

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
    private String mListTitleName = "Unknown";
    private ListAttributes mAttributes;

    private LinearLayout llListItems;

    private ListItemsArrayAdapter mListItemsArrayAdapter;

    public fragListItems() {
        // Required empty public constructor
    }

    public static fragListItems newInstance(String listTitleID) {
        MyLog.i("fragListItems", "newInstance: ListTitleID = " + listTitleID);
        fragListItems frag = new fragListItems();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_TITLE_UUID, listTitleID);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        mListTitle = null;
        Bundle args = getArguments();
        if (args.containsKey(ARG_LIST_TITLE_UUID)) {
            mListTitleUuid = args.getString(ARG_LIST_TITLE_UUID);
            refreshListTitle(mListTitleUuid);
        } else {

            MyLog.e("fragListItems", "onCreate: No ListTitle found!");
        }
    }

    private void refreshListTitle(String listTitleUuid) {
        if (listTitleUuid != null && !listTitleUuid.equals(MySettings.NOT_AVAILABLE)) {
            mListTitle = ListTitle.getListTitle(listTitleUuid);
        }
        if (mListTitle != null) {
            mListTitleName = mListTitle.getName();
//            String attributesUuid = mListTitle.getAttributes().getLocalUuid();
//            mAttributes = ListAttributes.getAttributes(attributesUuid);
            mAttributes = mListTitle.getAttributes();
            MyLog.i("fragListItems", "refreshListTitle: " + mListTitleName);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("fragListItems", "onCreateView: " + mListTitleName);
        View rootView = inflater.inflate(R.layout.frag_list_items, container, false);

        llListItems = (LinearLayout) rootView.findViewById(R.id.llListItems);
        llListItems.setBackground(mAttributes.getBackgroundDrawable());

        lvListItems = (com.nhaarman.listviewanimations.itemmanipulation.DynamicListView) rootView.findViewById(R.id.lvListItems);
        lvListItems.setLongClickable(true);

        // Set up the ListView adapter
        mListItemsArrayAdapter = new ListItemsArrayAdapter(getActivity(), lvListItems, mListTitle);
        lvListItems.setAdapter(mListItemsArrayAdapter);

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

    public void onEvent(MyEvents.updateListUI event) {
        if (event.getListTitleUuid() == null) {
            updateListUI();
        } else if (mListTitleUuid.equals(event.getListTitleUuid())) {
            updateListUI();
        }
    }

    private void updateListUI() {
        mAttributes = ListAttributes.getAttributes(mListTitle.getAttributes().getLocalUuid());
        List<ListItem> listItems = ListItem.getAllListItems(mListTitle);
        MyLog.i("fragListItems", "updateListUI List " + mListTitleName + " with " + listItems.size() + " items.");
        mListItemsArrayAdapter.setData(listItems, mAttributes);
        mListItemsArrayAdapter.notifyDataSetChanged();
        llListItems.setBackground(mAttributes.getBackgroundDrawable());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragListItems", "onActivityCreated: " + mListTitleName);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("fragListItems", "onSaveInstanceState: " + mListTitleName);
        outState.putString(ARG_LIST_TITLE_UUID, mListTitle.getLocalUuid());
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
        updateListUI();
//        EventBus.getDefault().post(new MyEvents.refreshSectionsPagerAdapter());
//        if(savedInstanceState!=null && savedInstanceState.containsKey(ARG_LIST_TITLE_UUID)){
//            mListTitleUuid = savedInstanceState.getString(ARG_LIST_TITLE_UUID);
//            if (mListTitleUuid != null && !mListTitleUuid.equals(MySettings.NOT_AVAILABLE)) {
//                mListTitle = ListTitle.getListTitle(mListTitleUuid);
//            }
//            if (mListTitle != null) {
//                mListTitleName = mListTitle.getName();
//                mAttributes = mListTitle.getAttributes();
//            }
//            llListItems.setBackground(mAttributes.getBackgroundDrawable());
        MyLog.i("fragListItems", "onViewStateRestored: " + mListTitleName);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragListItems", "onResume: " + mListTitleName);
        refreshListTitle(mListTitleUuid);
        updateListUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragListItems", "onPause: " + mListTitleName);
        mListTitle.setIsForceViewInflation(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("fragListItems", "onDestroy: " + mListTitleName);
        EventBus.getDefault().unregister(this);
    }
}
