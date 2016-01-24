package com.lbconsulting.a1list.activities;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.adapters.ListTitleArrayAdapter;
import com.lbconsulting.a1list.adapters.ThemeNameArrayAdapter;
import com.lbconsulting.a1list.classes.CommonMethods;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.lbconsulting.a1list.dialogs.dialogEditListAttributesName;
import com.lbconsulting.a1list.dialogs.dialogEditListTitle;
import com.lbconsulting.a1list.dialogs.dialogListTitleSorting;
import com.lbconsulting.a1list.dialogs.dialogNewListTitle;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.util.List;

import de.greenrobot.event.EventBus;

public class ManageListsAndThemesActivity extends AppCompatActivity {

    public static final int MANAGE_LISTS = 100;
    public static final int MANAGE_THEMES = 200;
    public static final String ARG_DATA_TYPE = "argDataType";

    private int mDataType;
    private DynamicListView lvItems;
    private ListTitleArrayAdapter mListTitleArrayAdapter;
    private ThemeNameArrayAdapter mThemeNameArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.i("ManageListsAndThemesActivity", "onCreate");
        super.onCreate(savedInstanceState);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_manage_lists_and_themes);
        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MySettings.setRefreshDataFromTheCloud(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (mDataType) {

                    case MANAGE_LISTS:
                        showNewListDialog();
                        break;

                    case MANAGE_THEMES:
                        // do nothing ... should never reach here!
                        break;

                }
                Snackbar.make(view, "action add item", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle args = getIntent().getExtras();
            mDataType = args.getInt(ARG_DATA_TYPE);
        } else {
            mDataType = savedInstanceState.getInt(ARG_DATA_TYPE);
        }

        if (mDataType == MANAGE_THEMES) {
            fab.setVisibility(View.GONE);
        }

        lvItems = (DynamicListView) findViewById(R.id.lvItems);

        switch (mDataType) {

            case MANAGE_LISTS:
                getSupportActionBar().setTitle("Manage Lists");
                mListTitleArrayAdapter = new ListTitleArrayAdapter(this, lvItems);
                lvItems.setAdapter(mListTitleArrayAdapter);
                lvItems.enableSwipeToDismiss(
                        new OnDismissCallback() {
                            @Override
                            public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                                int position = reverseSortedPositions[0];
                                ListTitle item = mListTitleArrayAdapter.getItem(position);
                                deleteListTitle(item);
                                updateUI();
                            }
                        }
                );

                lvItems.enableDragAndDrop();

//                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        TextView tv = (TextView) view.findViewById(R.id.tvItemName);
//                        ListTitle listTitle = (ListTitle) tv.getTag();
//                        FragmentManager fm = getSupportFragmentManager();
//                        dialogEditListTitle dialog = dialogEditListTitle.newInstance(listTitle.getListTitleUuid());
//                        dialog.show(fm, "dialogEditListTitle");
//                    }
//                });

                break;

            case MANAGE_THEMES:
                getSupportActionBar().setTitle("Swipe to Delete Theme");
                mThemeNameArrayAdapter = new ThemeNameArrayAdapter(this, lvItems);
                lvItems.setAdapter(mThemeNameArrayAdapter);
                lvItems.enableSwipeToDismiss(
                        new OnDismissCallback() {
                            @Override
                            public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {

                                int position = reverseSortedPositions[0];
                                ListAttributes item = mThemeNameArrayAdapter.getItem(position);
                                deleteListAttributes(item);
                                updateUI();
                            }
                        }
                );

//                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        TextView tv = (TextView) view.findViewById(R.id.tvItemName);
//                        ListAttributes attributes = (ListAttributes) tv.getTag();
//                        FragmentManager fm = getSupportFragmentManager();
//                        dialogEditListAttributesName dialog = dialogEditListAttributesName.newInstance(attributes.getListTitleUuid());
//                        dialog.show(fm, "dialogEditListAttributesName");
//                    }
//                });
                break;

        }

        updateUI();
    }

    public void onEvent(MyEvents.showEditListTitleDialog event) {
        FragmentManager fm = getSupportFragmentManager();
        dialogEditListTitle dialog = dialogEditListTitle.newInstance(event.getListTitleUuid());
        dialog.show(fm, "dialogEditListTitle");
    }



    public void onEvent(MyEvents.showEditAttributesNameDialog event) {
        FragmentManager fm = getSupportFragmentManager();
        dialogEditListAttributesName dialog = dialogEditListAttributesName.newInstance(event.getAttributesUuid());
        dialog.show(fm, "dialogEditListAttributesName");
    }

    private void showNewListDialog() {
        FragmentManager fm = getSupportFragmentManager();
        dialogNewListTitle dialog = dialogNewListTitle.newInstance(dialogNewListTitle.SOURCE_FROM_MANAGE_LISTS_ACTIVITY);
        dialog.show(fm, "dialogNewListTitle");
    }

    private void deleteListAttributes(ListAttributes listAttributes) {
        listAttributes.setMarkedForDeletion(true);
        List<ListAttributes> listAttributesList = ListAttributes.getAllListAttributes();
        if (listAttributesList.size() > 0) {
            ListAttributes defaultAttributes = ListAttributes.getDefaultAttributes();

            // get all items and lists that have the attributes being deleted
            List<ListItem> listItems = ListItem.getAllListItems(listAttributes);
            List<ListTitle> listTitles = ListTitle.getAllListTitles(listAttributes);

            // replace the deleted attributes with the default attributes
            for (ListItem item : listItems) {
                item.setAttributes(defaultAttributes);
            }

            for (ListTitle item : listTitles) {
                item.setAttributes(defaultAttributes);
            }
        } else {
            listAttributes.setMarkedForDeletion(false);
            String title = getString(R.string.unableToDeleteListAttributes_title);
            String msg = String.format(getString(R.string.unableToDeleteListAttributes_message),
                    listAttributes.getName());
            CommonMethods.showOkDialog(this, title, msg);
        }

    }

    private void deleteListTitle(ListTitle listTitle) {
        // mark all items in the ListTitle for deletion
        List<ListItem> items = ListItem.getAllListItems(listTitle);
        for (ListItem item : items) {
            item.setMarkedForDeletion(true);
        }
        // mark the ListTitle for deletion
        listTitle.setMarkedForDeletion(true);


        if (MySettings.getActiveListTitleUuid().equals(listTitle.getListTitleUuid())) {
            // The active ListTitle is being deleted ... so reset the active ListTitle ID
            MySettings.setActiveListTitleUuid(MySettings.NOT_AVAILABLE);
        }
    }

    public void onEvent(MyEvents.updateListTitleUI event) {
        updateUI();
    }

    private void updateUI() {
        switch (mDataType) {

            case MANAGE_LISTS:
                if (!MySettings.isAlphabeticallySortNavigationMenu()) {
                    lvItems.setOnItemLongClickListener(
                            new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                                               final int position, final long id) {

                                    lvItems.startDragging(position);
                                    return true;
                                }
                            }
                    );
                } else {
                    lvItems.setOnItemLongClickListener(null);
                }

                List<ListTitle> allListTitles = ListTitle.getAllListTitles(MySettings.isAlphabeticallySortNavigationMenu());
                mListTitleArrayAdapter.setData(allListTitles);
                mListTitleArrayAdapter.notifyDataSetChanged();
                MyLog.i("ManageListsAndThemesActivity", "updateUI with " + allListTitles.size() + " ListTitles.");
                break;

            case MANAGE_THEMES:
                List<ListAttributes> allAttributes = ListAttributes.getAllListAttributes();
                mThemeNameArrayAdapter.setData(allAttributes);
                mThemeNameArrayAdapter.notifyDataSetChanged();
                MyLog.i("ManageListsAndThemesActivity", "updateUI with " + allAttributes.size() + " ListAttributes.");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("ManageListsAndThemesActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("ManageListsAndThemesActivity", "onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("ManageListsAndThemesActivity", "onSaveInstanceState");
        outState.putInt(ARG_DATA_TYPE, mDataType);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MyLog.i("ManageListsAndThemesActivity", "onRestoreInstanceState");
        mDataType = savedInstanceState.getInt(ARG_DATA_TYPE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_manage_lists_and_themes, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_listTitleSorting).setVisible(mDataType == MANAGE_LISTS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_listTitleSorting) {
            showListTitleSortingDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showListTitleSortingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        dialogListTitleSorting dialog = dialogListTitleSorting.newInstance();
        dialog.show(fm, "dialogListTitleSorting");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("ManageListsAndThemesActivity", "onDestroy");
        EventBus.getDefault().unregister(this);
    }
}
