package com.lbconsulting.a1list.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.adapters.SectionsPagerAdapter;
import com.lbconsulting.a1list.classes.CommonMethods;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.lbconsulting.a1list.dialogs.dialogEditListItem;
import com.lbconsulting.a1list.dialogs.dialogListItemSorting;
import com.lbconsulting.a1list.dialogs.dialogNewListItem;
import com.lbconsulting.a1list.dialogs.dialogNewListTitle;
import com.lbconsulting.a1list.dialogs.dialogSelectFavorites;
import com.lbconsulting.a1list.services.UpAndDownloadDataAsyncTask;
import com.lbconsulting.a1list.services.UploadDirtyObjectsService;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    private static CoordinatorLayout mSnackBarView;
    private static ListTitle mActiveListTitle;
    private static Toolbar mToolbar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ProgressBar mProgressBar;
    private TabLayout mTabLayout;
    private boolean mRefreshDataFromTheCloud;

    //region Static Methods

    private static void showSnackBar(String message) {
        Snackbar
                .make(mSnackBarView, message, Snackbar.LENGTH_LONG)
                .show();
    }

    private static void showCreateNewListSnackBar() {
        mToolbar.setTitle(R.string.toolbar_create_list_title);
        String msg = App.getContext().getResources().getString(R.string.snackbar_create_list_message);
        showSnackBar(msg);
    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MainActivity", "onCreate");

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        mSnackBarView = (CoordinatorLayout) findViewById(R.id.main_content);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mProgressBar =(ProgressBar)findViewById(R.id.progressBar);

        // Create the adapter that will return a fragListItems fragment
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MyLog.i("MainActivity", "onPageSelected: position = " + position);
                updateActiveListTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActiveListTitle != null) {
                    showNewListItemDialog(mActiveListTitle);
                    mActiveListTitle.setIsForceViewInflation(false);
                } else {
                    MainActivity.showCreateNewListSnackBar();
                }
            }
        });

    }

    private void showProgressBar(){
        mViewPager.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        mViewPager.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void updateActiveListTitle(int position) {
        mActiveListTitle = mSectionsPagerAdapter.getListTitle(position);
        if (mActiveListTitle != null) {
            MySettings.setActiveListTitleUuid(mActiveListTitle.getListTitleUuid());
            setToolBarTitle(mActiveListTitle.getName());
            EventBus.getDefault().post(new MyEvents.updateListUI(mActiveListTitle.getListTitleUuid()));
            MyLog.i("MainActivity", "updateActiveListTitle: position = " + position + ": " + mActiveListTitle.getName());
        }else{
            MyLog.e("MainActivity", "updateActiveListTitle: Unable to find ListTitle at position = " + position);
        }
    }

    private void setToolBarTitle(String title) {
        mToolbar.setTitle(title);
    }

    //region OnEvent

    public void onEvent(MyEvents.refreshSectionsPagerAdapter event) {
        MyLog.i("MainActivity", "refreshSectionsPagerAdapter");
        mSectionsPagerAdapter.notifyDataSetChanged();
        if(mActiveListTitle!=null) {
            EventBus.getDefault().post(new MyEvents.updateListUI(mActiveListTitle.getListTitleUuid()));
        }
    }

    public void onEvent(MyEvents.showEditListItemDialog event) {
        FragmentManager fm = getSupportFragmentManager();
        dialogEditListItem dialog = dialogEditListItem.newInstance(event.getListItemUuid());
        dialog.show(fm, "dialogEditListItem");
    }

    public void onEvent(MyEvents.showOkDialog event) {
        CommonMethods.showOkDialog(this, event.getTitle(), event.getMessage());
    }

    public void onEvent(MyEvents.startA1List event) {
        startA1List(event.getRefreshDataFromTheCloud());
    }

    public void onEvent(MyEvents.showProgressBar event) {
        showProgressBar();
    }

    public void onEvent(MyEvents.hideProgressBar event) {
        hideProgressBar();
    }

    //endregion


    @Override
    protected void onResume() {
        // A1List sync strategy:
        //  onResume() data is download from Parse and then the UI is updated
        //  onPause() any dirty data is uploaded to Parse
        //  Conflicts: the client always wins ... data in the cloud is overwritten by the client's data
        super.onResume();

        if (MySettings.isUserEmailVerified() || !CommonMethods.isNetworkAvailable()) {
            MyLog.i("MainActivity", "onResume: User email verified -- startA1List.");
            startA1List(mRefreshDataFromTheCloud);

        } else if (getIsUserEmailVerified()) {
            MyLog.i("MainActivity", "onResume: User email has become verified -- startA1List.");
            if (CommonMethods.isNetworkAvailable()) {
                MySettings.setIsUserEmailVerified(true);
                MySettings.setIsUserInitialized(true);
            }
            startA1List(mRefreshDataFromTheCloud);

        } else if (MySettings.isUserInitialized()) {
            if (ParseUser.getCurrentUser().isNew()) {
                MyLog.i("MainActivity", "onResume: User initialized and is a new user -- startA1List.");
                startA1List(mRefreshDataFromTheCloud);
            } else {
                checkInitializationDate();
            }

        } else if (ParseUser.getCurrentUser().isNew()) {
            MyLog.i("MainActivity", "onResume: New User -- initializeNewUser");
            initializeNewUser();

        } else if (ParseUser.getCurrentUser().isAuthenticated()) {
            MySettings.setIsUserInitialized(true);
            checkInitializationDate();

        } else {
            MyLog.e("MainActivity", "onResume: Unknown startup configuration!");
        }

    }

    private void checkInitializationDate() {
        if (MySettings.isAppInitializationDateGreaterThan7Days()) {
            MyLog.i("MainActivity", "onResume: User initialized and initialization date greater than 7 days -- terminateApp");
            terminateApp();
        } else {
            MyLog.i("MainActivity", "onResume: User initialized but within 7 day grace period -- requestEmailBeVerified -- startA1List");
            requestEmailBeVerified();
            startA1List(mRefreshDataFromTheCloud);
        }
    }

    private void startA1List(boolean refreshDataFromTheCloud) {
        MyLog.i("MainActivity", "startA1List");

        String activeListTitleUuid = MySettings.getActiveListTitleUuid();
        if (activeListTitleUuid.equals(MySettings.NOT_AVAILABLE)) {
            if (mSectionsPagerAdapter.getCount() > 0) {
                mViewPager.setCurrentItem(0);
            }
        } else {
            int position = mSectionsPagerAdapter.getPosition(activeListTitleUuid);
            int currentPosition = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(position);
            if (position == currentPosition) {
                // Since position and currentPosition are equal
                // mViewPager's OnPageChangeListener won't fire
                // but the activeListTitle still needs to be updated... so update it.
                updateActiveListTitle(position);
            }
        }

        if (refreshDataFromTheCloud) {
            upAndDownloadDataFromParse();
        }

        MySettings.setRefreshDataFromTheCloud(true);
        MySettings.setIsFirstTimeRun(false);
    }

    private void upAndDownloadDataFromParse() {
        if (CommonMethods.isNetworkAvailable()) {
            new UpAndDownloadDataAsyncTask(this).execute();
        }
    }

    private void requestEmailBeVerified() {
        CommonMethods.showOkDialog(this, getString(R.string.requestEmailBeVerified_title),
                getString(R.string.requestEmailBeVerified_message));
    }

    private void terminateApp() {
        String title = getString(R.string.terminateApp_title);
        String terminationMsg = getString(R.string.terminateApp_message);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set dialog title and message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(terminationMsg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
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

    private boolean getIsUserEmailVerified() {
        boolean isEmailVerified = false;
        if (CommonMethods.isNetworkAvailable()) {
            try {
                ParseUser user = ParseUser.getCurrentUser().fetch();
                isEmailVerified = user.getBoolean("emailVerified");
            } catch (ParseException e) {
                MyLog.e("MainActivity", "getIsUserEmailVerified: ParseException: " + e.getMessage());
            }
        } else {
            // if the network is not available ... assume that the email has been verified.
            isEmailVerified = true;
        }

        return isEmailVerified;
    }

    private void initializeNewUser() {
        // initializeNewUser on Parse
        final HashMap<String, Object> params = new HashMap<>();
        final long startTime = System.currentTimeMillis();
        final Context context = this;
        ParseCloud.callFunctionInBackground("initializeNewUser", params, new FunctionCallback<Integer>() {
            @Override
            public void done(final Integer numberOfAttributes, ParseException e) {
                if (e == null) {
                    // Success. New user initialized in Parse cloud
                    MySettings.setIsUserInitialized(true);
                    // Retrieve attributes and pin to local data store
                    ParseQuery<ListAttributes> query = ListAttributes.getQuery();
                    query.orderByAscending(ListAttributes.NAME_LOWERCASE);

                    query.findInBackground(new FindCallback<ListAttributes>() {
                        public void done(final List<ListAttributes> attributesList, ParseException e) {
                            if (e == null) {
                                // Success. retrieved attributes from the cloud
                                // Pin attributes to the local data store
                                ParseObject.unpinAllInBackground(attributesList, new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        // Success. Old attributes deleted ...
                                        // now save the new ones to the local data store
                                        ParseObject.pinAllInBackground(attributesList);
                                        MyLog.i("MainActivity", "retrieveAttributes: Pinned" + attributesList.size() + "attributes.");
                                        long endTime = System.currentTimeMillis();
                                        long duration = endTime - startTime;
                                        ParseUser user = ParseUser.getCurrentUser();
                                        String successMsg = user.get("name") + getString(R.string.initializeNewUser_success_message);

                                        String successLog = "New user \"" + user.getUsername() + "\" successfully initialized. "
                                                + numberOfAttributes + " Attributes created in Parse cloud. Duration = "
                                                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.";
                                        MyLog.i("MainActivity", successLog);
                                        String title = String.format(getString(R.string.initializeNewUser_success_title), user.get("name"));
                                        CommonMethods.showOkDialog(context, title, successMsg);
                                    }
                                });

                            } else {
                                MyLog.e("MainActivity", "retrieveAttributes: ParseException: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    // Failed to initialize new user.
                    MyLog.e("MainActivity", "initializeNewUser Failed to fully initialize new user. " + e.getMessage());
                    String title = getString(R.string.initializeNewUser_error_title);
                    CommonMethods.showOkDialog(MainActivity.this, title, e.getMessage());
                }
            }
        });

    }

    @Override
    protected void onPause() {
        // A1List sync strategy:
        //  onResume() data is download from Parse and then the UI is updated
        //  onPause() any dirty data is uploaded to Parse
        //  Conflicts: the client always wins ... data in the cloud is overwritten by the client's data
        super.onPause();
        MyLog.i("MainActivity", "onPause");
        uploadDirtyObjects();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyLog.i("MainActivity", "onStart");
        mRefreshDataFromTheCloud = MySettings.getRefreshDataFromTheCloud();
    }


    private void uploadDirtyObjects() {
        MyLog.i("MainActivity", "uploadDirtyObjects");
        Intent uploadDirtyObjectsIntent = new Intent(this, UploadDirtyObjectsService.class);
        startService(uploadDirtyObjectsIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("MainActivity", "onDestroy");
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("MainActivity", "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MyLog.i("MainActivity", "onRestoreInstanceState");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyLog.i("MainActivity", "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_deleteStrikeouts) {
            deleteStrikeoutItems();
            return true;

        } else if (id == R.id.action_showFavorites) {
            showFavoriteItems();
            return true;

        } else if (id == R.id.action_newList) {
            showNewListDialog();
            return true;

        } else if (id == R.id.action_listSorting) {
            showListItemSortingDialog();
            return true;

        } else if (id == R.id.action_editListTheme) {
            Intent intent = new Intent(this, ListThemeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(MySettings.ARG_LIST_TITLE_ID, mActiveListTitle.getListTitleUuid());
            intent.putExtras(bundle);
            startActivity(intent);
            return true;

        } else if (id == R.id.action_manageLists) {
            Intent intent = new Intent(this, ManageListsAndThemesActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(ManageListsAndThemesActivity.ARG_DATA_TYPE, ManageListsAndThemesActivity.MANAGE_LISTS);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;

        } else if (id == R.id.action_manageThemes) {
            Intent intent = new Intent(this, ManageListsAndThemesActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(ManageListsAndThemesActivity.ARG_DATA_TYPE, ManageListsAndThemesActivity.MANAGE_THEMES);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;

        } else if (id == R.id.action_refresh) {
            upAndDownloadDataFromParse();
            return true;

        } else if (id == R.id.action_logoff) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
                        startActivity(intent);
                    } else {
                        String title = "Failed of Log Out";
                        String msg = e.getMessage();
                        CommonMethods.showOkDialog(MainActivity.this, title, msg);
                        MyLog.e("MainActivity", "action_logoff: " + msg);
                    }
                }
            });
            return true;

            // TODO: Remove action_test_data menu item
        }
//        else if (id == R.id.action_test_data) {
//
//            Intent intent = new Intent(this, TestDataActivity.class);
//            startActivity(intent);
//
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    private void showListItemSortingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        dialogListItemSorting dialog = dialogListItemSorting.newInstance(mActiveListTitle.getListTitleUuid());
        dialog.show(fm, "dialogListItemSorting");
    }

    private void deleteStrikeoutItems() {
        List<ListItem> strikeoutItems = ListItem.getStrikeoutItems(mActiveListTitle);
        if (strikeoutItems.size() > 0) {
            for (ListItem item : strikeoutItems) {
                item.setMarkedForDeletion(true);
                item.setIsStruckOut(false);
            }
            EventBus.getDefault().post(new MyEvents.updateListUI(mActiveListTitle.getListTitleUuid()));
        }
    }

    private void showFavoriteItems() {
        FragmentManager fm = getSupportFragmentManager();
        dialogSelectFavorites dialog = dialogSelectFavorites.newInstance(mActiveListTitle.getListTitleUuid());
        dialog.show(fm, "dialogSelectFavorites");
    }

    private void showNewListDialog() {
        FragmentManager fm = getSupportFragmentManager();
        dialogNewListTitle dialog = dialogNewListTitle.newInstance(dialogNewListTitle.SOURCE_FROM_MAIN_ACTIVITY);
        dialog.show(fm, "dialogNewListTitle");
    }

    private void showNewListItemDialog(ListTitle listTitle) {
        if (listTitle != null) {
            FragmentManager fm = getSupportFragmentManager();
            dialogNewListItem dialog = dialogNewListItem.newInstance(listTitle.getListTitleUuid());
            dialog.show(fm, "dialogNewListItem");
        }
    }

}

