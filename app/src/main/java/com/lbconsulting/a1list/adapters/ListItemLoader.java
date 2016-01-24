package com.lbconsulting.a1list.adapters;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;

import java.util.List;

import de.greenrobot.event.EventBus;

// Source: http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html

public class ListItemLoader extends AsyncTaskLoader<List<ListItem>> {

    // We hold a reference to the Loader’s data here.
    private List<ListItem> mData;
    private ListTitle mListTitle;
    private boolean mObserverRegistered;

    /****************************************************/
    /**
     * (1) A task that performs the asynchronous load
     **/
    public ListItemLoader(Context context, ListTitle listTitle) {

        // Loaders may be used across multiple Activities (assuming they aren't
        // bound to the LoaderManager), so NEVER hold a reference to the context
        // directly. Doing so will cause you to leak an entire Activity's context.
        // The superclass constructor will store a reference to the Application
        // Context instead, and can be retrieved with a call to getContext().
        super(context);
        mListTitle = listTitle;
        mObserverRegistered = false;
        MyLog.i("ListItemLoader", "Initialized: " + mListTitle.getName());
    }

/********************************************************/
/** (2) Deliver the results to the registered listener **/

    /****************************************************/

    @Override
    public List<ListItem> loadInBackground() {
        // This method is called on a background thread and should generate a
        // new set of data to be delivered back to the client.
        List<ListItem> data = ListItem.getAllListItems(mListTitle);
        MyLog.i("ListItemLoader", "loadInBackground complete: " + mListTitle.getName() + "; found " + data.size() + " items." );
        return data;
    }

/*********************************************************/
/** (3) Implement the Loader’s state-dependent behavior **/

    /********************************************************/

    @Override
    public void deliverResult(List<ListItem> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<ListItem> oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    /*********************************************************/

    @Override
    protected void onStartLoading() {
//        MyLog.i("ListItemLoader", "onStartLoading: " + mListTitleName);
        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        // Begin monitoring the underlying data source.
        registerObserver();

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
//        MyLog.i("ListItemLoader", "onStopLoading: " + mListTitleName);
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
//        MyLog.i("ListItemLoader", "onReset: " + mListTitleName);
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
        unregisterObserver();
    }

    @Override
    public void onCanceled(List<ListItem> data) {
//        MyLog.i("ListItemLoader", "onCanceled: " + mListTitleName);
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

/*********************************************************************/
    /**
     * (4) Observer which receives notifications when the data changes
     **/

    private void releaseResources(List<ListItem> data) {
//        MyLog.i("ListItemLoader", "releaseResources: " + mListTitleName + "; " + data.size() + " items.");
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
        data=null;
    }

    /**
     * Called at the appropriate time for registering observers on the content
     * being loaded.
     * <p/>
     * NOTE: This method may be called multiple times without a call to
     * unregisterObserver, so your implementation should be idempotent
     */
    protected void registerObserver() {
//        MyLog.i("ListItemLoader", "registerObserver: " + mListTitleName);
        if (!mObserverRegistered) {
            EventBus.getDefault().register(this);
            mObserverRegistered = true;
        }
    }

    /**
     * Called at the appropriate time for unregistering observers on the content
     * being loaded.
     */
    protected void unregisterObserver() {
//        MyLog.i("ListItemLoader", "unregisterObserver: " + mListTitleName);
        EventBus.getDefault().unregister(this);
        mObserverRegistered = false;
    }

    public void onEvent(MyEvents.updateListUI event) {
        if (event.getListTitleUuid() == null) {
            onContentChanged();
        } else if (mListTitle.getListTitleUuid().equals(event.getListTitleUuid())) {
            onContentChanged();
        }
    }
}
