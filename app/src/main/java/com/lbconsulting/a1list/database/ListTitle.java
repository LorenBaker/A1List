package com.lbconsulting.a1list.database;

import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Parse object for an A1List Table.
 */

@ParseClassName("ListTitles")
public class ListTitle extends ParseObject {

    public static final String AUTHOR = "author";
    public static final String NAME = "name";
    public static final String NAME_LOWERCASE = "nameLowercase";
    public static final String ATTRIBUTES = "ListAttributes";
    public static final String IS_CHECKED = "isChecked";
    public static final String IS_LIST_TITLE_DIRTY = "isListTitleDirty";
    public static final String IS_MARKED_FOR_DELETION = "isMarkedForDeletion";
    public static final String LIST_TITLE_MANUAL_SORT_KEY = "manualSortKey";
    public static final String SORT_LIST_ITEMS_ALPHABETICALLY = "sortListItemsAlphabetically";
    public static final String LOCAL_UUID = "localUuid";
    public static final String LIST_TITLE_ID = "listTitleId";

    public ListTitle() {
        // A default constructor is required.
    }

    private String getObjectID() {
        return getObjectId();
    }

    public void setLocalUuid() {
        UUID uuid = UUID.randomUUID();
        put(LOCAL_UUID, uuid.toString());
        setListTitleDirty(true);
    }

    public String getLocalUuid() {
        String uuidString = getString(LOCAL_UUID);
        if (uuidString == null || uuidString.isEmpty()) {
            uuidString = getObjectID();
        }
        return uuidString;
    }

    public ParseUser getAuthor() {
        return getParseUser(AUTHOR);
    }

    public void setAuthor(ParseUser currentUser) {
        put(AUTHOR, currentUser);
        setListTitleDirty(true);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String tableName) {
        put(NAME, tableName);
        put(NAME_LOWERCASE, tableName.toLowerCase());
        setListTitleDirty(true);
    }

    public String getLowercaseName() {
        return getString(NAME_LOWERCASE);
    }

    public ListAttributes getAttributes() {
        return (ListAttributes) get(ATTRIBUTES);
    }

    public void setAttributes(ListAttributes attributes) {
        put(ATTRIBUTES, attributes);
        setListTitleDirty(true);
    }

    public boolean isChecked() {
        return getBoolean(IS_CHECKED);
    }

    public void setChecked(boolean isChecked) {
        put(IS_CHECKED, isChecked);
        setListTitleDirty(true);
    }

    public boolean isListTitleDirty() {
        return getBoolean(IS_LIST_TITLE_DIRTY);
    }

    public void setListTitleDirty(boolean isDirty) {
        put(IS_LIST_TITLE_DIRTY, isDirty);
    }

    public boolean isMarkedForDeletion() {
        return getBoolean(IS_MARKED_FOR_DELETION);
    }

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

    public void setListID() {
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


    public static ParseQuery<ListTitle> getQuery() {
        return ParseQuery.getQuery(ListTitle.class);
    }

    public Date getDateModified() {
        return getUpdatedAt();
    }

    public Date getDateCreated() {
        return getCreatedAt();
    }

    @Override
    public String toString() {
        return getName();
    }

    public static ListTitle getListTitle(String listTitleID, boolean isUuid) {
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
            ParseQuery<ListTitle> query = getQuery();
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            if (sortAlphabetically) {
                query.orderByAscending(NAME_LOWERCASE);
            } else {
                query.orderByAscending(LIST_TITLE_MANUAL_SORT_KEY);
            }
            query.include(ATTRIBUTES);
            query.fromLocalDatastore();
            allListTitles = query.find();
        } catch (ParseException e) {
            MyLog.e("ListTitle", "getAllListTitles: ParseException: " + e.getMessage());
        }
        return allListTitles;
    }


    public static List<ListTitle> getAllListTitles(ListAttributes listAttributes) {
        List<ListTitle> allListTitles = new ArrayList<>();
        try {
            ParseQuery<ListTitle> query = getQuery();
            query.whereEqualTo(ATTRIBUTES,listAttributes);
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
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
            query.fromLocalDatastore();
            allListTitlesMarkedForDeletion = query.find();
        } catch (ParseException e) {
            MyLog.e("ListTitle", "getAllListTitlesMarkedForDeletion: ParseException: " + e.getMessage());
        }
        return allListTitlesMarkedForDeletion;
    }

    public static int getListTitlesCount() {
        int count = 0;
        try {
            ParseQuery<ListTitle> query = getQuery();
            query.fromLocalDatastore();
            count = query.count();
        } catch (ParseException e) {
            MyLog.e("ListTitle", "getListTitlesCount: ParseException: " + e.getMessage());
        }
        return count;
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

}
