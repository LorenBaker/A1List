package com.lbconsulting.a1list.database;

import com.lbconsulting.a1list.activities.App;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.services.UpAndDownloadDataAsyncTask;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Parse object for an A1List Table.
 */

@ParseClassName("ListTitles")
public class ListTitle extends ParseObject {

    public static final String NAME_LOWERCASE = "nameLowercase";
    private static final String AUTHOR = "author";
    private static final String NAME = "name";
    private static final String ATTRIBUTES = "ListAttributes";
    private static final String IS_CHECKED = "isChecked";
    private static final String IS_FORCE_VIEW_INFLATION = "isForceViewInflation";
    private static final String IS_LIST_TITLE_DIRTY = "isListTitleDirty";
    private static final String IS_MARKED_FOR_DELETION = "isMarkedForDeletion";
    private static final String LIST_TITLE_MANUAL_SORT_KEY = "manualSortKey";
    private static final String SORT_LIST_ITEMS_ALPHABETICALLY = "sortListItemsAlphabetically";
    private static final String LOCAL_UUID = "localUuid";
    private static final String LIST_TITLE_ID = "listTitleId";
    private static final String LIST_LOCK_STRING = "listLockString";
    private static final String LIST_NOT_LOCK = "listNotLocked";


    public ListTitle() {
        // A default constructor is required.
    }

    public static ParseQuery<ListTitle> getQuery() {
        return ParseQuery.getQuery(ListTitle.class);
    }

    public static void newInstance(String newListName) {
        ListTitle newListTitle = new ListTitle();
        try {
            newListTitle.setName(newListName);
            ListAttributes defaultAttributes = ListAttributes.getDefaultAttributes();
            newListTitle.setAttributes(defaultAttributes);
            newListTitle.setLocalUuid();
            newListTitle.setListID();
            newListTitle.setAuthor(ParseUser.getCurrentUser());
            newListTitle.setChecked(false);
            newListTitle.setListTitleDirty(true);
            newListTitle.setMarkedForDeletion(false);
            newListTitle.setSortListItemsAlphabetically(true);
            MySettings.setActiveListTitleUuid(newListTitle.getListTitleUuid());
            newListTitle.setListTitleManualSortKey(newListTitle.getListID());
            newListTitle.setListLockString(LIST_NOT_LOCK);
            newListTitle.pin();

        } catch (ParseException e) {
            MyLog.e("MainActivity", "createNewList; newListTitle.pin(): ParseException: " + e.getMessage());
        }
    }

    public static ListTitle getListTitle(String listTitleID) {
        boolean isUuid = listTitleID.contains("-");
        ListTitle listTitle = null;
        try {
            ParseQuery<ListTitle> query = getQuery();
            if (isUuid) {
                query.whereEqualTo(LOCAL_UUID, listTitleID);
            } else {
                query.whereEqualTo("objectId", listTitleID);
            }
            query.include(ATTRIBUTES);
            query.fromLocalDatastore();
            listTitle = query.getFirst();
        } catch (ParseException e) {
            MyLog.e("ListTitle", "getListTitle: ParseException: " + e.getMessage());
        }
        return listTitle;
    }

    public static List<ListTitle> getAllListTitles(boolean sortAlphabetically) {
        List<ListTitle> allListTitles = new ArrayList<>();
        try {
            ParseQuery<ListTitle> orQuery1 = getQuery();
            orQuery1.whereEqualTo(LIST_LOCK_STRING, LIST_NOT_LOCK);

            ParseQuery<ListTitle> orQuery2 = getQuery();
            orQuery2.whereEqualTo(LIST_LOCK_STRING, getDeviceId());

            List<ParseQuery<ListTitle>> orQueries = new ArrayList<>();
            orQueries.add(orQuery1);
            orQueries.add(orQuery2);

            ParseQuery<ListTitle> mainQuery = ParseQuery.or(orQueries);
            mainQuery.whereEqualTo(IS_MARKED_FOR_DELETION, false);

            if (sortAlphabetically) {
                mainQuery.orderByAscending(NAME_LOWERCASE);
            } else {
                mainQuery.orderByAscending(LIST_TITLE_MANUAL_SORT_KEY);
            }
            mainQuery.include(ATTRIBUTES);
            mainQuery.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_LIST_TITLES);
            mainQuery.fromLocalDatastore();
            allListTitles = mainQuery.find();
        } catch (ParseException e) {
            MyLog.e("ListTitle", "getAllListTitles: ParseException: " + e.getMessage());
        }
        return allListTitles;
    }

    public static List<ListTitle> getAllListTitles(ListAttributes listAttributes) {
        List<ListTitle> allListTitles = new ArrayList<>();
        try {
            ParseQuery<ListTitle> query = getQuery();
            query.whereEqualTo(ATTRIBUTES, listAttributes);
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_LIST_TITLES);
            query.fromLocalDatastore();
            allListTitles = query.find();
        } catch (ParseException e) {
            MyLog.e("ListTitle", "getAllListTitles: ParseException: " + e.getMessage());
        }
        return allListTitles;
    }

    public static List<ListTitle> getAllDirtyListTitles() {
        List<ListTitle> allDirtyListTitles = new ArrayList<>();
        try {
            ParseQuery<ListTitle> query = getQuery();
            query.whereEqualTo(IS_LIST_TITLE_DIRTY, true);
            query.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_LIST_TITLES);
            query.fromLocalDatastore();
            allDirtyListTitles = query.find();
        } catch (ParseException e) {
            MyLog.e("ListTitle", "getAllDirtyListTitles: ParseException: " + e.getMessage());
        }
        return allDirtyListTitles;
    }

    public static List<ListTitle> getAllListTitlesMarkedForDeletion() {
        List<ListTitle> allListTitlesMarkedForDeletion = new ArrayList<>();
        try {
            ParseQuery<ListTitle> query = getQuery();
            query.whereEqualTo(IS_MARKED_FOR_DELETION, true);
            query.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_LIST_TITLES);
            query.fromLocalDatastore();
            allListTitlesMarkedForDeletion = query.find();
        } catch (ParseException e) {
            MyLog.e("ListTitle", "getAllListTitlesMarkedForDeletion: ParseException: " + e.getMessage());
        }
        return allListTitlesMarkedForDeletion;
    }

    public static void unPinAll() {
        try {
            List<ListTitle> allListTitles = getAllListTitles(true);
            if (allListTitles != null && allListTitles.size() > 0) {
                ParseObject.unpinAll(allListTitles);
            }
        } catch (ParseException e) {
            MyLog.e("ListTitle", "unPinAll: ParseException: " + e.getMessage());
        }
    }

    public static boolean listExists(String proposedListName) {
        // The List exist if its lowercase name is in the datastore, AND
        // it is not marked for deletion

        boolean result = false;
        try {
            ParseQuery<ListTitle> query = getQuery();
            query.whereEqualTo(NAME_LOWERCASE, proposedListName.trim().toLowerCase());
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.fromLocalDatastore();
            ListTitle listTitle = query.getFirst();
            if (listTitle != null) {
                result = true;
            }

        } catch (ParseException e) {
            if (e.getCode() != 101) {  // 101 = ObjectNotFound
                MyLog.e("ListTitle", "listExists: ParseException: " + e.getMessage());
            }
        }

        return result;
    }

    public static boolean getIsSameObject(ListTitle listTitle, String proposedListName) {
        boolean result = false;
        try {
            ParseQuery<ListTitle> query = getQuery();
            query.whereEqualTo(NAME_LOWERCASE, proposedListName.trim().toLowerCase());
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.fromLocalDatastore();
            ListTitle existingListTitle = query.getFirst();
            if (existingListTitle.getListTitleUuid().equals(listTitle.getListTitleUuid())) {
                result = true;
            }

        } catch (ParseException e) {
            if (e.getCode() != 101) {  // 101 = ObjectNotFound
                MyLog.e("ListTitle", "getIsSameObject: ParseException: " + e.getMessage());
            }
        }

        return result;
    }

    private static String getDeviceId() {
        String androidDeviceId = "NoAndroidId";
        try {
            androidDeviceId = android.provider.Settings.Secure.getString(App.getContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            if (androidDeviceId == null) {
                androidDeviceId = "NoAndroidId";
            }
        } catch (Exception e) {
            MyLog.e("ListTitle", "getDeviceId: Exception: " + e.getMessage());
        }

        return androidDeviceId;
    }

    private String getObjectID() {
        return getObjectId();
    }

    private void setLocalUuid() {
        UUID uuid = UUID.randomUUID();
        put(LOCAL_UUID, uuid.toString());
        setListTitleDirty(true);
    }

    public String getListTitleUuid() {
        String uuidString = getString(LOCAL_UUID);
        if (uuidString == null || uuidString.isEmpty()) {
            uuidString = getObjectID();
        }
        return uuidString;
    }

    public void setAuthor(ParseUser currentUser) {
        put(AUTHOR, currentUser);
        setListTitleDirty(true);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String listName) {
        put(NAME, listName);
        put(NAME_LOWERCASE, listName.toLowerCase());
        setListTitleDirty(true);
    }

    public ListAttributes getAttributes() {
        return (ListAttributes) get(ATTRIBUTES);
    }

    public void setAttributes(ListAttributes attributes) {
        put(ATTRIBUTES, attributes);
        setListTitleDirty(true);
    }

// --Commented out by Inspection START (1/3/2016 8:32 AM):
//    public boolean isChecked() {
//        return getBoolean(IS_CHECKED);
//    }
// --Commented out by Inspection STOP (1/3/2016 8:32 AM)

    public void setChecked(boolean isChecked) {
        put(IS_CHECKED, isChecked);
        setListTitleDirty(true);
    }

    public boolean isForceViewInflation() {
        return getBoolean(IS_FORCE_VIEW_INFLATION);
    }

    public void setIsForceViewInflation(boolean isForceViewInflation) {
        put(IS_FORCE_VIEW_INFLATION, isForceViewInflation);
//        setListTitleDirty(true);
    }

// --Commented out by Inspection START (1/3/2016 8:32 AM):
//    public boolean isListTitleDirty() {
//        return getBoolean(IS_LIST_TITLE_DIRTY);
//    }
// --Commented out by Inspection STOP (1/3/2016 8:32 AM)

    public void setListTitleDirty(boolean isDirty) {
        put(IS_LIST_TITLE_DIRTY, isDirty);
    }

// --Commented out by Inspection START (1/3/2016 8:32 AM):
//    public boolean isMarkedForDeletion() {
//        return getBoolean(IS_MARKED_FOR_DELETION);
//    }
// --Commented out by Inspection STOP (1/3/2016 8:32 AM)

    public void setMarkedForDeletion(boolean isMarkedForDeletion) {
        put(IS_MARKED_FOR_DELETION, isMarkedForDeletion);
        setListTitleDirty(true);
    }

    public long getListTitleManualSortKey() {
        return getLong(LIST_TITLE_MANUAL_SORT_KEY);
    }

    public void setListTitleManualSortKey(long manualSortKey) {
        put(LIST_TITLE_MANUAL_SORT_KEY, manualSortKey);
        setListTitleDirty(true);
    }

    public long getListID() {
        return getLong(LIST_TITLE_ID);
    }

    private void setListID() {
        long titleID = MySettings.getNextListTitleID();
        put(LIST_TITLE_ID, titleID);
        setListTitleDirty(true);
    }

    public boolean sortListItemsAlphabetically() {
        return getBoolean(SORT_LIST_ITEMS_ALPHABETICALLY);
    }

    public void setSortListItemsAlphabetically(boolean sortAlphabetically) {
        put(SORT_LIST_ITEMS_ALPHABETICALLY, sortAlphabetically);
        setListTitleDirty(true);
    }

    private String getListLockString() {
        return getString(LIST_LOCK_STRING);
    }

    private void setListLockString(String listLockString) {
        put(LIST_LOCK_STRING, listLockString);
        setListTitleDirty(true);
    }

    public boolean isListLocked() {
        return !getListLockString().equals(LIST_NOT_LOCK);
    }

    public void setLockList(boolean lockList) {
        if (lockList) {
            setListLockString(getDeviceId());
        } else {
            setListLockString(LIST_NOT_LOCK);
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
