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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

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
import com.lbconsulting.a1list.fragments.fragListItems;
import com.lbconsulting.a1list.services.DownloadDataAsyncTask;
import com.lbconsulting.a1list.services.UploadDirtyObjectsService;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
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

    private RelativeLayout mFragmentContainer;

    private static ListTitle mActiveListTitle;

    public static void setActiveListTitle(ListTitle listTitle) {
        mActiveListTitle = listTitle;
    }

    private Toolbar mToolbar;
    private String mActiveFragmentTag;
    private Menu mNavigationMenu;
    private List<ListTitle> mListTitles;

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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActiveListTitle != null) {
                    showNewListItemDialog(mActiveListTitle);
                }
            }
        });

        mFragmentContainer = (RelativeLayout) findViewById(R.id.rlActivityMain);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mNavigationMenu = navigationView.getMenu();


    }


    //region OnEvent
    public void onEvent(MyEvents.setActionBarTitle event) {
        if (mToolbar != null) {
            mToolbar.setTitle(event.getTitle());
        }
    }

    public void onEvent(MyEvents.showOkDialog event) {
        showOkDialog(this, event.getTitle(), event.getMessage());
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
        startA1List(false);
    }

    public void onEvent(MyEvents.startA1List event){
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
    protected void onStart() {
        super.onStart();
        MyLog.i("MainActivity", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MyLog.i("MainActivity", "onRestart");
    }

    @Override
    protected void onResume() {
        // A1List sync strategy:
        //  onResume() data is download from Parse and then the UI is updated
        //  onPause() any dirty data is uploaded to Parse
        //  The client always wins ... data in the cloud is overwritten by the client's data
        super.onResume();



        if (MySettings.isUserEmailVerified()) {
            MyLog.i("MainActivity", "onResume: User email verified -- startA1List.");
            startA1List(true);

        } else if (getIsUserEmailVerified()) {
            MyLog.i("MainActivity", "onResume: User email has become verified -- startA1List.");
            MySettings.setIsUserInitialized(true);
            startA1List(true);

        } else if (MySettings.isUserInitialized()) {

            if (ParseUser.getCurrentUser().isNew()) {
                MyLog.i("MainActivity", "onResume: User initialized and is a new user -- startA1List.");
                startA1List(true);
            } else{
                checkInitializationDate();
            }

        } else if (ParseUser.getCurrentUser().isNew()) {
            MyLog.i("MainActivity", "onResume: New User -- initializeNewUser");
            initializeNewUser();

        }else if(ParseUser.getCurrentUser().isAuthenticated()){
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
            startA1List(true);
        }
    }

    private void startA1List(boolean refreshDataFromTheCloud) {
        MyLog.i("MainActivity", "startA1List");
        setupNavigationMenu();

        String activeListTitleUuid = MySettings.getActiveListTitleUuid();
        if (activeListTitleUuid.equals(MySettings.NOT_AVAILABLE)) {
            mActiveListTitle = null;
            // show default gradient

            ListAttributes defaultAttributes = ListAttributes.getDefaultAttributes();
            if (defaultAttributes != null) {
                mFragmentContainer.setBackground(defaultAttributes.getBackgroundDrawable());
            }

            mToolbar.setTitle("No List Selected");

        } else {
            mActiveListTitle = ListTitle.getListTitle(activeListTitleUuid, true);
        }

        showList(activeListTitleUuid);

        if (refreshDataFromTheCloud) {
            downloadDataFromParse();
        }

        // Note: setupNavigationMenu() retrieves mListTitles
//        if (mListTitles != null && mListTitles.size() == 0) {
//            showCreateNewListDialog();
//        }
    }


    private void downloadDataFromParse() {
        if (CommonMethods.isNetworkAvailable()) {
            new DownloadDataAsyncTask(this).execute();
        }
    }
//
//    private void showCreateNewListDialog() {
//        String title = "Create A New List";
//        String newListTitle = getResources().getString(R.string.action_newList_title);
//        String msg = "Please select \"" + newListTitle + "\" from the upper right dropdown menu.";
//        showOkDialog(this, title, msg);
//    }

    private void requestEmailBeVerified() {
        String title = "Please Confirm Email Address";
        String requestEmailBeVerificationMsg = "Please see the email from no-reply@lbconsulting.a1list.com to confirm your email address. "
                + "Please confirm it as soon as practical.\n\nIf your email address is not confirmed within seven days of A1List's installation A1List will stop working.";
        showOkDialog(this, title, requestEmailBeVerificationMsg);
    }

    private void terminateApp() {
        String title = "Confirm Email Address";
        String terminationMsg = "It's been more than seven days since A1List was installed and your email address is not confirmed. Please see the email from no-reply@lbconsulting.a1list.com to confirm your email address.\n\n"
                + "After you have confirmed your email address, please restart A1List.";

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
        // TODO: figure out what to do if the network is not available 
        boolean isEmailVerified = false;
        try {
            ParseUser user = ParseUser.getCurrentUser().fetch();
            isEmailVerified = user.getBoolean("emailVerified");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isEmailVerified;
    }

    private void initializeNewUser() {
        // initializeNewUser on Parse
        // TODO: show progress bar
        final HashMap<String, Object> params = new HashMap<String, Object>();
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
//                                        MySettings.setAppInitializationDate(endTime);
                                        long duration = endTime - startTime;
                                        ParseUser user = ParseUser.getCurrentUser();
                                        String successMsg = user.getUsername() + " successfully initialized.\n\n"
                                                + numberOfAttributes + " list color themes have been created in your cloud account.\n\n"
                                                + "Shortly you'll receive an email from no-reply@lbconsulting.a1list.com asking you to confirm your email address. "
                                                + "Please confirm it as soon as practical.";

                                        String successLog = "New user \"" + user.getUsername() + "\" successfully initialized. "
                                                + numberOfAttributes + " Attributes created in Parse cloud. Duration = "
                                                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.";
                                        MyLog.i("MainActivity", successLog);
                                        String title = "Welcome to A1List";
                                        showOkDialog(context, title, successMsg);
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
                    String title = "Initialize New User Error";
                    showOkDialog(MainActivity.this, title, e.getMessage());
                }
            }
        });

    }


    private void showList(String listTitleID) {
        FragmentManager fm = getFragmentManager();
        if (listTitleID.equals(MySettings.NOT_AVAILABLE)) {
            Fragment activeFragment = fm.findFragmentByTag(mActiveFragmentTag);
            if (activeFragment != null) {
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .remove(activeFragment)
                        .commit();
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
        //  The client always wins ... data in the cloud is overwritten by the client's data
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
    protected void onStop() {
        super.onStop();
        MyLog.i("MainActivity", "onStop");
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

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_deleteStrikeouts) {
            deleteStrikeoutItems();
            return true;

        } else if (id == R.id.action_newList) {
            showNewListDialog();
            return true;

        } else if (id == R.id.action_listSorting) {
            showListSortingDialog();
//            Toast.makeText(this, "action_listSorting selected.", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_editListTheme) {
            Intent intent = new Intent(this, ListThemeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(MySettings.ARG_LIST_TITLE_ID, mActiveListTitle.getLocalUuid()); //Your id
            intent.putExtras(bundle); //Put your id to your next Intent
            startActivity(intent);
//            Toast.makeText(this, "action_editListTheme selected.", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_showLists) {
            Toast.makeText(this, "action_showLists selected.", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_refresh) {
            MyLog.i("MainActivity", "onOptionsItemSelected: action_refresh");
            downloadDataFromParse();
            return true;

        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "action_settings selected.", Toast.LENGTH_SHORT).show();
            return true;

            // TODO: Remove action_test_data menu item
        } else if (id == R.id.action_test_data) {
            Toast.makeText(this, "action_test_data selected.", Toast.LENGTH_SHORT).show();
//            Intent testDataIntent = new Intent(this, TestDataActivity.class);
//            this.startActivity(testDataIntent);
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showListSortingDialog() {
        FragmentManager fm = getFragmentManager();
        dialogListItemSorting dialog = dialogListItemSorting.newInstance(mActiveListTitle.getLocalUuid());
        dialog.show(fm, "dialogListItemSorting");
    }

    private void deleteStrikeoutItems() {
        List<ListItem> strikeoutItems = ListItem.getStrikeoutItems(mActiveListTitle);
        if (strikeoutItems.size() > 0) {
            for (ListItem item : strikeoutItems) {
                item.setMarkedForDeletion(true);
            }
            EventBus.getDefault().post(new MyEvents.updateListUI());
        }
    }

    private void showNewListDialog() {
        FragmentManager fm = getFragmentManager();
        dialogNewListTitle dialog = dialogNewListTitle.newInstance();
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

