package com.lbconsulting.a1list.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.CommonMethods;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.lbconsulting.a1list.dialogs.dialogListItemSorting;
import com.lbconsulting.a1list.dialogs.dialogNewListItem;
import com.lbconsulting.a1list.dialogs.dialogNewListTitle;
import com.lbconsulting.a1list.dialogs.dialogSelectFavorites;
import com.lbconsulting.a1list.fragments.fragListItems;
import com.lbconsulting.a1list.services.DownloadDataAsyncTask;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static CoordinatorLayout mSnackBarView;
    private static ListTitle mActiveListTitle;
    private static Toolbar mToolbar;
    private boolean mRefreshDataFromTheCloud;
    private RelativeLayout mFragmentContainer;
    private String mActiveFragmentTag;
    private Menu mNavigationMenu;
    private List<ListTitle> mListTitles;

    //region Static Methods
    public static void setActiveListTitle(ListTitle listTitle) {
        mActiveListTitle = listTitle;
    }

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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActiveListTitle != null) {
                    showNewListItemDialog(mActiveListTitle);
                } else {
                    MainActivity.showCreateNewListSnackBar();
                }
            }
        });

        mFragmentContainer = (RelativeLayout) findViewById(R.id.rlActivityMain);
        mSnackBarView = (CoordinatorLayout) findViewById(R.id.snackbarPosition);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mNavigationMenu = navigationView.getMenu();

        mRefreshDataFromTheCloud = MySettings.getRefreshDataFromTheCloud();

    }

    //region OnEvent
    public void onEvent(MyEvents.setActionBarTitle event) {
        if (mToolbar != null) {
            mToolbar.setTitle(event.getTitle());
        }
    }

    public void onEvent(MyEvents.showOkDialog event) {
        CommonMethods.showOkDialog(this, event.getTitle(), event.getMessage());
    }

    public void onEvent(MyEvents.setFragmentContainerBackground event) {
        setFragmentContainerBackground(event.getStartColor(), event.getEndColor());
    }

    private void setFragmentContainerBackground(int startColor, int endColor) {
        int colors[] = {startColor, endColor};
        GradientDrawable backgroundDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        mFragmentContainer.setBackground(backgroundDrawable);
    }

    public void onEvent(MyEvents.updateUI event) {
        updateUI();
    }

    private void updateUI() {
        MyLog.i("MainActivity", "updateUI");
        startA1List(mRefreshDataFromTheCloud);
    }

    public void onEvent(MyEvents.startA1List event) {
        startA1List(event.getRefreshDataFromTheCloud());
    }

    //endregion

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
        setupNavigationMenu();

        String activeListTitleUuid = MySettings.getActiveListTitleUuid();
        if (activeListTitleUuid.equals(MySettings.NOT_AVAILABLE)) {
            // get all ListTitles
            List<ListTitle> allLists = ListTitle.getAllListTitles(MySettings.isAlphabeticallySortNavigationMenu());

            if (allLists.size() > 0) {
                // Since there are ListTitles in the datastore ... select the first ListTitle
                mActiveListTitle = allLists.get(0);
                activeListTitleUuid = mActiveListTitle.getLocalUuid();
            } else {
                mActiveListTitle = null;
                // show default gradient
                ListAttributes defaultAttributes = ListAttributes.getDefaultAttributes();
                if (defaultAttributes != null) {
                    mFragmentContainer.setBackground(defaultAttributes.getBackgroundDrawable());
                }
                if (!MySettings.isFirstTimeRun()) {
                    showCreateNewListSnackBar();
                }
            }
        } else {
            mActiveListTitle = ListTitle.getListTitle(activeListTitleUuid);
        }

        showList(activeListTitleUuid);

        if (refreshDataFromTheCloud) {
            downloadDataFromParse();
        }

        MySettings.setRefreshDataFromTheCloud(true);
        MySettings.setIsFirstTimeRun(false);
    }

    private void downloadDataFromParse() {
        if (CommonMethods.isNetworkAvailable()) {
            new DownloadDataAsyncTask(this).execute();
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

    private void showList(String listTitleID) {
        FragmentManager fm = getFragmentManager();

        if (listTitleID.equals(MySettings.NOT_AVAILABLE)) {
            if (mActiveFragmentTag != null && !mActiveFragmentTag.isEmpty()) {
                Fragment activeFragment = fm.findFragmentByTag(mActiveFragmentTag);
                if (activeFragment != null) {
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .remove(activeFragment)
                            .commit();
                }
            }
        } else {
            mActiveFragmentTag = "frag_" + listTitleID;
            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.rlActivityMain,
                            fragListItems.newInstance(listTitleID), mActiveFragmentTag)
                    .commit();
        }
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
        setupNavigationMenu();
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
            bundle.putString(MySettings.ARG_LIST_TITLE_ID, mActiveListTitle.getLocalUuid());
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
            downloadDataFromParse();
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
        FragmentManager fm = getFragmentManager();
        dialogListItemSorting dialog = dialogListItemSorting.newInstance(mActiveListTitle.getLocalUuid());
        dialog.show(fm, "dialogListItemSorting");
    }

    private void deleteStrikeoutItems() {
        List<ListItem> strikeoutItems = ListItem.getStrikeoutItems(mActiveListTitle);
        if (strikeoutItems.size() > 0) {
            for (ListItem item : strikeoutItems) {
                item.setMarkedForDeletion(true);
                item.setIsStruckOut(false);
            }
            EventBus.getDefault().post(new MyEvents.updateListUI());
        }
    }

    private void showFavoriteItems() {
        FragmentManager fm = getFragmentManager();
        dialogSelectFavorites dialog = dialogSelectFavorites.newInstance(mActiveListTitle.getLocalUuid());
        dialog.show(fm, "dialogSelectFavorites");
    }

    private void showNewListDialog() {
        FragmentManager fm = getFragmentManager();
        dialogNewListTitle dialog = dialogNewListTitle.newInstance(dialogNewListTitle.SOURCE_FROM_MAIN_ACTIVITY);
        dialog.show(fm, "dialogNewListTitle");
    }

    private void showNewListItemDialog(ListTitle listTitle) {
        if (listTitle != null) {
            FragmentManager fm = getFragmentManager();
            dialogNewListItem dialog = dialogNewListItem.newInstance(listTitle.getLocalUuid());
            dialog.show(fm, "dialogNewListItem");
        }
    }

    private void setupNavigationMenu() {

        mListTitles = ListTitle.getAllListTitles(MySettings.isAlphabeticallySortNavigationMenu());
        MyLog.i("MainActivity", "setupNavigationMenu with " + mListTitles.size() + " ListTitles.");
        mNavigationMenu.clear();
        if (mListTitles != null) {
            int itemID = 0;
            for (ListTitle listTitle : mListTitles) {
                mNavigationMenu.add(Menu.NONE, itemID, Menu.NONE, listTitle.getName());
                itemID++;
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int itemId = item.getItemId();
        ListTitle listTitle = mListTitles.get(itemId);
        showList(listTitle.getLocalUuid());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

