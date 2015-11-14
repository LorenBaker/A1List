package com.lbconsulting.a1list.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * An async task that downloads app data from Parse and initiates an
 * update of the  ListUI
 */
public class DownloadDataAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final int QUERY_LIMIT_ATTRIBUTES = 50;
    private static final int QUERY_LIMIT_LIST_TITLES = 100;
    private static final int QUERY_LIMIT_LIST_ITEMS = 2000;
    private static final int NOTIFICATION_DOWNLOAD_ID = 33;

    private List<ListAttributes> mAttributes;
    private List<ListTitle> mListTitles;
    private List<ListItem> mListItems;

    private final Context mContext;

    public DownloadDataAsyncTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        MyLog.i("DownloadDataAsyncTask", "onPreExecute");
        showDownLoadNotification();
    }

    @Override
    protected Void doInBackground(Void... params) {
        MyLog.i("DownloadDataAsyncTask", "doInBackground");

        downloadAttributes();
        downloadListTitles();
        downloadListItems();
        updateLocalDataStore();

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        MyLog.i("DownloadDataAsyncTask", "onPostExecute");
        cancelDownLoadNotification();
        EventBus.getDefault().post(new MyEvents.updateListUI());
        EventBus.getDefault().post(new MyEvents.startA1List(false));
        super.onPostExecute(aVoid);
    }

    private void updateLocalDataStore() {
        unpinOldData();
        pinNewData();
        setNextIdsAndSortKeys();
    }

    private void setNextIdsAndSortKeys() {
        long id = 0;
        for (ListItem item : mListItems) {
            if (item.getItemID() > id) {
                id = item.getItemID();
            }
        }
        id++;
        MySettings.setNextListItemID(id);

        id=0;
        for (ListTitle item : mListTitles) {
            if (item.getListID() > id) {
                id = item.getListID();
            }
        }
        id++;
        MySettings.setNextListTitleID(id);
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
                MyLog.i("DownloadDataAsyncTask", "pinNewData: " + mAttributes.size() + " Attributes.");
            }

            if (mListTitles != null && mListTitles.size() > 0) {
                ParseObject.pinAll(mListTitles);
                MyLog.i("DownloadDataAsyncTask", "pinNewData: " + mListTitles.size() + " ListTitles.");
            }

            if (mListItems != null && mListItems.size() > 0) {
                ParseObject.pinAll(mListItems);
                MyLog.i("DownloadDataAsyncTask", "pinNewData: " + mListItems.size() + " ListItems.");
            }

        } catch (ParseException e) {
            MyLog.e("DownloadDataAsyncTask", "pinNewData: ParseException: " + e.getMessage());
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
            MyLog.i("DownloadDataAsyncTask", "downloaded " + mListItems.size() + " List Items from Parse.");

        } catch (ParseException e) {
            MyLog.e("DownloadDataAsyncTask", "downloadListItems: ParseException: " + e.getMessage());
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
            MyLog.i("DownloadDataAsyncTask", "downloaded " + mListTitles.size() + " List Titles from Parse.");

        } catch (ParseException e) {
            MyLog.e("DownloadDataAsyncTask", "downloadListTitles: ParseException: " + e.getMessage());
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
            MyLog.i("DownloadDataAsyncTask", "downloaded " + mAttributes.size() + " Attributes from Parse.");

        } catch (ParseException e) {
            MyLog.e("DownloadDataAsyncTask", "downloadAttributes: ParseException: " + e.getMessage());
        }
    }

    private void showDownLoadNotification() {

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0, new Intent(mContext,
                DownloadDataAsyncTask.class), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationBuilder.setContentTitle(mContext.getString(R.string.notification_downloading_title));
        notificationBuilder.setContentText(mContext.getString(R.string.notification_downloading_text));
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        notificationBuilder.setTicker(mContext.getString(R.string.notification_download_ticker));
        notificationManager
                .notify(NOTIFICATION_DOWNLOAD_ID, notificationBuilder.build());

//        mDownloadNotificationShowing = true;
    }

    private void cancelDownLoadNotification() {

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0, new Intent(mContext,
                DownloadDataAsyncTask.class), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationBuilder.setContentTitle(mContext.getString(R.string.notification_downloading_title));
        notificationBuilder.setContentText(mContext.getString(R.string.notification_downloading_text));
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        notificationBuilder.setTicker(mContext.getString(R.string.notification_download_ticker));
        notificationManager.cancelAll();
//        mDownloadNotificationShowing = false;
    }
}
