package com.lbconsulting.a1list.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.parse.ParseException;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * This service uploads dirty Parse objects
 */
public class UploadDirtyObjectsService extends Service {

    private static final int INTERVAL_FAST = 30000; // 30 seconds
    private static final int INTERVAL_SLOW = 15 * 60000; // 15 minutes
    private static final int MAX_NUMBER_OF_FAST_SLEEP_INTERVALS = 60;

    private ServiceHandler mServiceHandler;

    private volatile boolean mSeekingNetwork = true;
    private volatile int mInterval;
    private volatile boolean mStoppedSelf;

    private volatile int mNumberOfSleepIntervals;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyLog.i("UploadDirtyObjectsService", "onBind");
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i("UploadDirtyObjectsService", "onCreate");
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStoppedSelf) {
            MyLog.i("UploadDirtyObjectsService", "onDestroy: Stopped self.");
        } else {
            MyLog.i("UploadDirtyObjectsService", "onDestroy: Stopped by the Operating System.");
        }
        mSeekingNetwork = false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.i("UploadDirtyObjectsService", "onStartCommand");
        try {
            mStoppedSelf = false;
            mInterval = INTERVAL_FAST;

            // For each start request, send a message to start a job and deliver the
            // start ID so we know which request we're stopping when we finish the job
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);

        } catch (Exception e) {
            MyLog.e("UploadDirtyObjectsService", "onStartCommand: Exception" + e.getMessage());
        }

        // If we get killed, after returning from here, restart
        return Service.START_STICKY;
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int startID = msg.arg1;
            MyLog.i("UploadDirtyObjectsService", "Started background thread with StartID = " + startID);
            while (mSeekingNetwork) {
                synchronized (this) {
                    while (mSeekingNetwork) {
                        if (isNetworkAvailable()) {
                            mInterval = INTERVAL_FAST;
                            mNumberOfSleepIntervals = 0;
                            uploadDirtyParseObjects();
                        } else {
                            try {
                                mNumberOfSleepIntervals++;
                                if (mNumberOfSleepIntervals > MAX_NUMBER_OF_FAST_SLEEP_INTERVALS) {
                                    // The network has not been available for a long time ... increase the sleep interval
                                    mInterval = INTERVAL_SLOW;
                                }
                                MyLog.i("UploadDirtyObjectsService", mNumberOfSleepIntervals + " SLEEPING " + mInterval / 1000 + " seconds.");
                                Thread.sleep(mInterval);
                            } catch (InterruptedException e) {
                                MyLog.e("UploadDirtyObjectsService", "InterruptedException. " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        private void uploadDirtyParseObjects() {
            mSeekingNetwork = false;
            long startTime = System.currentTimeMillis();
            uploadDirtyAttributes();
            uploadDirtyListTitles();
            uploadDirtyListItems();

            deleteMarkedAttributes();
            deleteMarkedListTitles();
            deleteMarkedListItems();

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            MyLog.d("UploadDirtyObjectsService", "Overall upload elapsed time = "
                    + NumberFormat.getNumberInstance(Locale.US).format(elapsedTime) + " milliseconds.");

            // We're done ... so stop the service
            mStoppedSelf = true;
            stopSelf();

        }

        private void uploadDirtyAttributes() {
            long startTime = System.currentTimeMillis();

            List<ListAttributes> dirtyAttributes = ListAttributes.getAllDirtyListAttributes();
            MyLog.i("uploadDirtyListAttributes", "Found " + dirtyAttributes.size() + " dirty Attributes.");
            int count = 0;
            for (ListAttributes item : dirtyAttributes) {
                try {
                    item.setAttributesDirty(false);
                    item.save();
                    count++;
                } catch (ParseException e) {
                    item.setAttributesDirty(true);
                    MyLog.e("uploadDirtyListAttributes", "Error saving dirty Attribute \"" + item.getName()
                            + "\" : ParseException" + e.getMessage());
                }
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MyLog.i("uploadDirtyListAttributes", "Saved " + count + " Attributes to Parse. Duration = "
                    + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
        }

        private void uploadDirtyListTitles() {
            long startTime = System.currentTimeMillis();

            List<ListTitle> dirtyListTitles = ListTitle.getAllDirtyListTitles();
            MyLog.i("uploadDirtyListTitles", "Found " + dirtyListTitles.size() + " dirty List Titles.");
            int count = 0;
            for (ListTitle item : dirtyListTitles) {
                try {
                    item.setListTitleDirty(false);
                    item.save();
                    count++;
                } catch (ParseException e) {
                    item.setListTitleDirty(true);
                    MyLog.e("uploadDirtyListTitles", "Error saving dirty List Title \"" + item.getName()
                            + "\" : ParseException" + e.getMessage());
                }
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MyLog.i("uploadDirtyListTitles", "Saved " + count + " List Titles to Parse. Duration = "
                    + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
        }

        private void uploadDirtyListItems() {
            long startTime = System.currentTimeMillis();

            List<ListItem> dirtyListItems = ListItem.getAllDirtyListItems();
            MyLog.i("uploadDirtyListItems", "Found " + dirtyListItems.size() + " dirty List Items.");
            int count = 0;
            for (ListItem item : dirtyListItems) {
                try {
                    item.setListItemDirty(false);
                    item.save();
                    count++;
                } catch (ParseException e) {
                    item.setListItemDirty(true);
                    MyLog.e("uploadDirtyListItems", "Error saving dirty List Item \"" + item.getName()
                            + "\" : ParseException" + e.getMessage());
                }
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MyLog.i("uploadDirtyListItems", "Saved " + count + " List Items to Parse. Duration = "
                    + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");

        }

        private void deleteMarkedAttributes() {
            long startTime = System.currentTimeMillis();

            List<ListAttributes> attributesMarkedForDeletion = ListAttributes.getAllAttributesMarkedForDeletion();
            MyLog.i("deleteMarkedAttributes", "Found " + attributesMarkedForDeletion.size() + " marked Attributes.");
            int count = 0;
            for (ListAttributes item : attributesMarkedForDeletion) {
                try {
                    item.unpin();
                    item.delete();
                    count++;
                } catch (ParseException e) {
                    MyLog.e("deleteMarkedAttributes", "Error deleting Attributes \"" + item.getName()
                            + "\" : ParseException" + e.getMessage());
                }
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MyLog.i("deleteMarkedAttributes", "Deleted " + count + " Attributes from Parse. Duration = "
                    + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
        }

        private void deleteMarkedListTitles() {
            long startTime = System.currentTimeMillis();

            List<ListTitle> listTitlesMarkedForDeletion = ListTitle.getAllListTitlesMarkedForDeletion();
            MyLog.i("deleteMarkedAttributes", "Found " + listTitlesMarkedForDeletion.size() + " marked List Titles.");
            int count = 0;
            for (ListTitle item : listTitlesMarkedForDeletion) {
                try {
                    item.unpin();
                    item.delete();
                    count++;
                } catch (ParseException e) {
                    MyLog.e("deleteMarkedAttributes", "Error deleting List Title \"" + item.getName()
                            + "\" : ParseException" + e.getMessage());
                }
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MyLog.i("deleteMarkedAttributes", "Deleted " + count + " List Titles from Parse. Duration = "
                    + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
        }

        private void deleteMarkedListItems() {
            long startTime = System.currentTimeMillis();

            List<ListItem> allListItemsMarkedForDeletion = ListItem.getAllListItemsMarkedForDeletion();
            MyLog.i("deleteMarkedListItems", "Found " + allListItemsMarkedForDeletion.size() + " marked List Items.");
            int count = 0;
            for (ListItem item : allListItemsMarkedForDeletion) {
                try {
                    item.unpin();
                    item.delete();
                    count++;
                } catch (ParseException e) {
                    MyLog.e("deleteMarkedListItems", "Error deleting List Item \"" + item.getName()
                            + "\" : ParseException" + e.getMessage());
                }
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MyLog.i("deleteMarkedListItems", "Deleted " + count + " List Items from Parse. Duration = "
                    + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
        }


        private boolean isNetworkAvailable() {

            boolean networkAvailable = false;
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            if ((ni != null) && (ni.isConnected())) {
                // We have a network connection
                networkAvailable = true;
            }
            if (networkAvailable) {
                MyLog.i("UploadDirtyObjectsService", "Network is available.");
            } else {
                MyLog.i("UploadDirtyObjectsService", "Network NOT available.");
            }

            return networkAvailable;
        }

    }


}
