package com.lbconsulting.a1list.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.activities.MainActivity;
import com.lbconsulting.a1list.adapters.ListItemsArrayAdapter;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A fragment that shows master list of grocery items
 */
public class fragListItems extends Fragment implements View.OnClickListener {
    private static final int LIST_ITEMS_QUERY_LIMIT = 500;
    private static final String ARG_LIST_TITLE_UUID = "argListTitleUuid";

    //    private ListView lvListItems;
    private com.nhaarman.listviewanimations.itemmanipulation.DynamicListView lvListItems;
    private ListTitle mListTitle;
    private String mListTitleName = "Unknown";

    private ListItemsArrayAdapter mListItemsArrayAdapter;
//    private ParseQueryAdapter<ListItem> mListItemsAdapter;
//    private ParseQueryAdapter.QueryFactory<ListItem> mQueryFactory;


    public static fragListItems newInstance(String listTitleID) {
        MyLog.i("fragListItems", "newInstance: ListTitleID = " + listTitleID);
        fragListItems frag = new fragListItems();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_TITLE_UUID, listTitleID);
        frag.setArguments(args);
        return frag;
    }

    public fragListItems() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        mListTitle = null;
        Bundle args = getArguments();
        if (args.containsKey(ARG_LIST_TITLE_UUID)) {
            String listTitleUuid = args.getString(ARG_LIST_TITLE_UUID);
            if (listTitleUuid != MySettings.NOT_AVAILABLE) {
                mListTitle = ListTitle.getListTitle(listTitleUuid, true);
            }
            if (mListTitle != null) {
                mListTitleName = mListTitle.getName();
                MyLog.i("fragListItems", "onCreate: " + mListTitleName);
            } else {
                MyLog.e("fragListItems", "onCreate: ListTitle is Null! uuid = " + listTitleUuid);
            }
        } else {

            MyLog.e("fragListItems", "onCreate: No ListTitle found!");
        }

        MainActivity.setActiveListTitle(mListTitle);

//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("fragListItems", "onCreateView: " + mListTitleName);
        View rootView = inflater.inflate(R.layout.frag_list_items, container, false);

        lvListItems = (com.nhaarman.listviewanimations.itemmanipulation.DynamicListView) rootView.findViewById(R.id.lvListItems);
//        lvListItems.setItemsCanFocus(true);

        // Set up the ListView adapter
        mListItemsArrayAdapter = new ListItemsArrayAdapter(getActivity(), lvListItems, mListTitle);
        lvListItems.setAdapter(mListItemsArrayAdapter);

        lvListItems.enableSwipeToDismiss(
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {

                        int position = reverseSortedPositions[0];
                        ListItem item = mListItemsArrayAdapter.getItem(position);
                        item.setMarkedForDeletion(true);
                        updateListUI();
                    }
                }
        );

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

        lvListItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.tvListItemName);
                mListItemsArrayAdapter.toggleStrikeout(position, tv);
            }
        });

        return rootView;
    }

//    private void setupListViewAdapter() {
//        mListItemsArrayAdapter = new ListItemsArrayAdapter(getActivity(), lvListItems,mListTitleName);
////        mListItemsAdapter.setObjectsPerPage(LIST_ITEMS_QUERY_LIMIT);
//        lvListItems.setAdapter(mListItemsArrayAdapter);
//
//        mListItemsAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ListItem>() {
//            public void onLoading() {
//                // Trigger "loading" UI progress bar
//            }
//
//            @Override
//            public void onLoaded(List<ListItem> list, Exception e) {
//                // Execute any post-loading logic, hide "loading" progress bar
//                MyLog.i("fragListItems", "onLoaded: " +mListTitleName + ". Loaded " + list.size() + " Items.");
////                if (mMoveToItemID != null && !mMoveToItemID.isEmpty()) {
////                    int position = 0;
////                    ClipData.Item item;
////                    for (int i = 0; i < mListItemsAdapter.getListItemsCount(); i++) {
////                        item = mListItemsAdapter.getItem(i);
////                        if (item.getItemID().equals(mMoveToItemID)) {
////                            position = i;
////                            break;
////                        }
////                    }
////
////                    lvListItems.setSelection(position);
////                }
//            }
//        });
//    }


    public void onEvent(MyEvents.updateListUI event) {
        updateListUI();
    }

//    public void onEvent(MyEvents.updateListUI event) {
//        updateListUI();
//    }

    private void updateListUI() {
        List<ListItem> listItems = ListItem.getAllListItems(mListTitle);
        MyLog.i("fragListItems", "updateListUI List " + mListTitleName + " with " + listItems.size() + " items.");
        mListItemsArrayAdapter.setData(listItems);
        mListItemsArrayAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragListItems", "onActivityCreated: " + mListTitleName);
        String title = "";
        int startColor = -1;
        int endColor = -1;
        if (mListTitle != null) {
            title = mListTitleName;
            MySettings.setActiveListTitleUuid(mListTitle.getLocalUuid());
            startColor = mListTitle.getAttributes().getStartColor();
            endColor = mListTitle.getAttributes().getEndColor();
        }
        EventBus.getDefault().post(new MyEvents.setActionBarTitle(title));
        EventBus.getDefault().post(new MyEvents.setFragmentContainerBackground(startColor, endColor));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("fragListItems", "onSaveInstanceState: " + mListTitleName);
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
        MyLog.i("fragListItems", "onViewStateRestored: " + mListTitleName);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragListItems", "onResume: " + mListTitleName);
        updateListUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragListItems", "onPause: " + mListTitleName);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("fragListItems", "onDestroy: " + mListTitleName);
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        MyLog.i("fragListItems", "onDetach: " + mListTitleName);
    }


    //region Options Menu
/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.frag_master_list, menu);
        MyLog.i("fragListItems", "onCreateOptionsMenu: frag_master_list");

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MyLog.i("fragListItems", "onPrepareOptionsMenu: frag_master_list");
        MenuItem showFavorites = menu.findItem(R.id.action_show_favorites);
        MenuItem showAllItems = menu.findItem(R.id.action_show_all_items);
        if (MySettings.showFavorites()) {
            showFavorites.setVisible(false);
            showAllItems.setVisible(true);
        } else {
            showFavorites.setVisible(true);
            showAllItems.setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // Shown in all fragment views.
        if (id == R.id.action_show_favorites) {
            MySettings.setShowFavorites(true);
            updateListUI(null);
            getActivity().invalidateOptionsMenu();
            return true;

        } else if (id == R.id.action_show_all_items) {
            MySettings.setShowFavorites(false);
            updateListUI(null);
            getActivity().invalidateOptionsMenu();
            return true;

        } else if (id == R.id.action_select_all_items) {
            Item.selectAllItems(getActivity());
            updateListUI(null);
            return true;

        } else if (id == R.id.action_select_all_favorites) {
            Item.selectAllFavorites(getActivity());
            updateListUI(null);
            return true;

        } else if (id == R.id.action_deselect_all_items) {
            Item.deselectAllItems(getActivity());
            updateListUI(null);
            return true;

        } else if (id == R.id.action_show_master_list_sort_dialog) {
            showMasterListSortDialog();
            return true;

        } else
            // Not implemented here
            return false;
    }


    private void showMasterListSortDialog() {
        FragmentManager fm = getFragmentManager();
        dialogMasterListSorting dialog = dialogMasterListSorting.newInstance();
        dialog.show(fm, "dialogMasterListSorting");
    }*/
    //endregion


    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//
//            case R.id.btnClearEditText:
//                txtItemName.setText("");
//                break;
//        }
    }
}
