package com.lbconsulting.a1list.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.adapters.ListAttributesArrayAdapter;
import com.lbconsulting.a1list.adapters.ListTitleArrayAdapter;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
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
    private ListAttributesArrayAdapter mListAttributesArrayAdapter;

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (mDataType) {

                    case MANAGE_LISTS:

                        break;

                    case MANAGE_THEMES:

                        break;

                }
                Snackbar.make(view, "action add item", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (mDataType == MANAGE_THEMES) {
            fab.setVisibility(View.GONE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle args = getIntent().getExtras();
            mDataType = args.getInt(ARG_DATA_TYPE);
        } else {
            mDataType = savedInstanceState.getInt(ARG_DATA_TYPE);
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
                                updateListTitleUI();
                            }
                        }
                );

                if (!MySettings.isAlphabeticallySortNavigationMenu()) {
                    lvItems.enableDragAndDrop();
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
                }

                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView tv = (TextView) view.findViewById(R.id.tvItemName);
                        Toast.makeText(ManageListsAndThemesActivity.this, "Selected List: "
                                + tv.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case MANAGE_THEMES:
                getSupportActionBar().setTitle("Manage Themes");
                mListAttributesArrayAdapter = new ListAttributesArrayAdapter(this, lvItems);
                lvItems.setAdapter(mListAttributesArrayAdapter);
                lvItems.enableSwipeToDismiss(
                        new OnDismissCallback() {
                            @Override
                            public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {

                                int position = reverseSortedPositions[0];
                                ListAttributes item = mListAttributesArrayAdapter.getItem(position);
                                deleteListAttributes(item);
                                updateListTitleUI();
                            }
                        }
                );

                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView tv = (TextView) view.findViewById(R.id.tvItemName);
                        Toast.makeText(ManageListsAndThemesActivity.this, "Selected Theme: " + tv.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

        }

        updateListTitleUI();
    }

    private static void showOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set dialog title and message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnOK = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                btnOK.setTextSize(18);
            }
        });

        // show it
        alertDialog.show();
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
            String title = "Unable to Delete Theme";
            String msg = "Theme \"" + listAttributes.getName() +
                    "\" is the last Theme in the datastore. There must be a minimum of one Theme in the datastore.";
            showOkDialog(this, title, msg);
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
    }

    public void onEvent(MyEvents.updateListTitleUI event) {
        updateListTitleUI();
    }

    private void updateListTitleUI() {
        switch (mDataType) {

            case MANAGE_LISTS:
                List<ListTitle> allListTitles = ListTitle.getAllListTitles(MySettings.isAlphabeticallySortNavigationMenu());
                mListTitleArrayAdapter.setData(allListTitles);
                mListTitleArrayAdapter.notifyDataSetChanged();
                MyLog.i("ManageListsAndThemesActivity", "updateListTitleUI with " + allListTitles.size() + " ListTitles.");
                break;

            case MANAGE_THEMES:
                List<ListAttributes> allAttributes = ListAttributes.getAllListAttributes();
                mListAttributesArrayAdapter.setData(allAttributes);
                mListAttributesArrayAdapter.notifyDataSetChanged();
                MyLog.i("ManageListsAndThemesActivity", "updateListTitleUI with " + allAttributes.size() + " ListAttributes.");
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
        menu.findItem(R.id.action_settings).setVisible(mDataType == MANAGE_LISTS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "action_settings selected.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("ManageListsAndThemesActivity", "onDestroy");
        EventBus.getDefault().unregister(this);
    }
}
