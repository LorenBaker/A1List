package com.lbconsulting.a1list.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.CommonMethods;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * An intent services that downloads and then pins all user data from Parse
 */
public class DownloadDataIntentService extends IntentService {

    private static final int FAST_INTERVAL = 30000; // 30 seconds
    private static final int SLOW_INTERVAL = 15 * 60000; // 15 minutes
    private static final int MAX_NUMBER_OF_FAST_SLEEP_INTERVALS = 60;
    private static final int QUERY_LIMIT_ATTRIBUTES = 50;
    private static final int QUERY_LIMIT_LIST_TITLES = 50;
    private static final int QUERY_LIMIT_LIST_ITEMS = 1000;
    private static final int NOTIFICATION_DOWNLOAD_ID = 33;

    private int mNumberOfSleepIntervals;
    private boolean mDownloadNotificationShowing;

    private List<ListAttributes> mAttributes;
    private List<ListTitle> mListTitles;
    private List<ListItem> mListItems;


    public DownloadDataIntentService() {
        super("DownloadDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MyLog.i("DownloadDataIntentService", "onHandleIntent");
        // see this site for conflict resolution
        //http://emertechie.com/syncing-data-with-parse-conflict-management/
        boolean seekingNetwork = true;
        mDownloadNotificationShowing = false;

        int INTERVAL = FAST_INTERVAL;
        long startTime = 0;
        while (seekingNetwork) {
            if (CommonMethods.isNetworkAvailable()) {
                startTime = System.currentTimeMillis();
                showDownLoadNotification();
                seekingNetwork = false;
                downloadAttributes();
                downloadListTitles();
                downloadListItems();
                updateLocalDataStore();
                // TODO: Figure out how to updateUI
//                EventBus.getDefault().post(new MyEvents.updateUI());
                MyLog.i("DownloadDataIntentService", "updateListUI");
                EventBus.getDefault().post(new MyEvents.updateListUI());
                cancelDownLoadNotification();
            } else {
                try {
                    mNumberOfSleepIntervals++;
                    if (mNumberOfSleepIntervals > MAX_NUMBER_OF_FAST_SLEEP_INTERVALS) {
                        // The network has not been available for a long time ... increase the sleep interval
                        INTERVAL = SLOW_INTERVAL;
                    }
                    MyLog.i("DownloadDataIntentService", mNumberOfSleepIntervals + " SLEEPING " + INTERVAL / 1000 + " seconds.");
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    MyLog.e("DownloadDataIntentService", "InterruptedException. " + e.getMessage());
                }
            }
        }

        // Finished downloading and updating data from Parse
        long endTime = System.currentTimeMillis();
        long elapsedTimeMills = endTime - startTime;
        double elapsedTimeSeconds = elapsedTimeMills / 1000.0;
        String msg = String.format("Sync elapsed time =  %.2f", elapsedTimeSeconds) + " seconds... DONE";
        MyLog.i("DownloadDataIntentService", msg);

    }

    private void updateLocalDataStore() {
        unpinOldData();
        pinNewData();
    }

    private void unpinOldData() {
        ListAttributes.unPinAll();
        ListTitle.unPinAll();
        ListItem.unPinAll();
    }

    private void pinNewData() {
        try {
            if (mAttributes != null && mAttributes.size() > 0) {
                ParseObject.pinAll(mAttributes);
                MyLog.i("DownloadDataIntentService", "pinNewData: " + mAttributes.size() + " Attributes.");
            }

            if (mListTitles != null && mListTitles.size() > 0) {
                ParseObject.pinAll(mListTitles);
                MyLog.i("DownloadDataIntentService",  "pinNewData: " + mListTitles.size() + " ListTitles.");
            }

            if (mListItems != null && mListItems.size() > 0) {
                ParseObject.pinAll(mListItems);
                MyLog.i("DownloadDataIntentService", "pinNewData: " + mListItems.size() + " ListItems.");
            }

        } catch (ParseException e) {
            MyLog.e("DownloadDataIntentService", "pinNewData: ParseException: " + e.getMessage());
        }
    }

    private void downloadListItems() {
        mListItems = null;
        ParseQuery<ListItem> query = ListItem.getQuery();
        query.orderByAscending(ListItem.NAME_LOWERCASE);
        query.setLimit(QUERY_LIMIT_LIST_ITEMS);
        // Query for new results from the network.
        try {
            mListItems = query.find();
            MyLog.i("DownloadDataIntentService", "downloaded " + mListItems.size() + " List Items from Parse.");

        } catch (ParseException e) {
            MyLog.e("DownloadDataIntentService", "downloadListItems: ParseException: " + e.getMessage());
        }
    }

    private void downloadListTitles() {
        mListTitles = null;
        ParseQuery<ListTitle> query = ListTitle.getQuery();
        query.orderByAscending(ListTitle.NAME_LOWERCASE);
        query.setLimit(QUERY_LIMIT_LIST_TITLES);
        // Query for new results from the network.
        try {
            mListTitles = query.find();
            MyLog.i("DownloadDataIntentService", "downloaded " + mListTitles.size() + " List Titles from Parse.");

        } catch (ParseException e) {
            MyLog.e("DownloadDataIntentService", "downloadListTitles: ParseException: " + e.getMessage());
        }
    }

    private void downloadAttributes() {
        mAttributes = null;
        ParseQuery<ListAttributes> query = ListAttributes.getQuery();
        query.orderByAscending(ListAttributes.NAME_LOWERCASE);
        query.setLimit(QUERY_LIMIT_ATTRIBUTES);
        // Query for new results from the network.
        try {
            mAttributes = query.find();
            for (ListAttributes attributes : mAttributes) {

            }
            MyLog.i("DownloadDataIntentService", "downloaded " + mAttributes.size() + " Attributes from Parse.");

        } catch (ParseException e) {
            MyLog.e("DownloadDataIntentService", "downloadAttributes: ParseException: " + e.getMessage());
        }
    }


    private void showDownLoadNotification() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this,
                DownloadDataIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationBuilder.setContentTitle(getString(R.string.notification_downloading_title));
        notificationBuilder.setContentText(getString(R.string.notification_downloading_text));
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        notificationBuilder.setTicker(getString(R.string.notification_download_ticker));
        notificationManager
                .notify(NOTIFICATION_DOWNLOAD_ID, notificationBuilder.build());

        mDownloadNotificationShowing = true;
    }

    private void cancelDownLoadNotification() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this,
                DownloadDataIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationBuilder.setContentTitle(getString(R.string.notification_downloading_title));
        notificationBuilder.setContentText(getString(R.string.notification_downloading_text));
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        notificationBuilder.setTicker(getString(R.string.notification_download_ticker));
        notificationManager.cancelAll();
        mDownloadNotificationShowing = false;
    }


    @Override
    public void onDestroy() {
        if (mDownloadNotificationShowing) {
            cancelDownLoadNotification();
        }
        MyLog.i("DownloadDataIntentService", "onDestroy: mDownloadNotificationShowing = " + mDownloadNotificationShowing);
        super.onDestroy();
    }
}
