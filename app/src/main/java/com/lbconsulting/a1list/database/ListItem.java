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
 * Parse object for an A1List Item.
 */

@ParseClassName("ListItems")
public class ListItem extends ParseObject {

    public static final String AUTHOR = "author";
    public static final String NAME = "name";
    public static final String NAME_LOWERCASE = "nameLowercase";
    public static final String LIST_TITLE = "ListTitle";
    public static final String ATTRIBUTES = "ListAttributes";
    public static final String IS_STRUCK_OUT = "isStruckOut";
    public static final String IS_CHECKED = "isChecked";
    public static final String IS_LIST_ITEM_DIRTY = "isListItemDirty";
    public static final String IS_MARKED_FOR_DELETION = "isMarkedForDeletion";
    public static final String LIST_ITEM_MANUAL_SORT_KEY = "manualSortKey";
    public static final String LOCAL_UUID = "localUuid";
    public static final String LIST_ITEM_ID = "listItemId";

    public ListItem() {
        // A default constructor is required.
    }

    private String getObjectID() {
        return getObjectId();
    }

    public void setItemUuid() {
        UUID uuid = UUID.randomUUID();
        put(LOCAL_UUID, uuid.toString());
        setListItemDirty(true);
    }

    public String getItemUuid() {
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
        setListItemDirty(true);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String itemName) {
        put(NAME, itemName);
        put(NAME_LOWERCASE, itemName.toLowerCase());
        setListItemDirty(true);
    }

    public String getLowercaseName() {
        return getString(NAME_LOWERCASE);
    }

    public ListTitle getListTitle() {
        return (ListTitle) get(LIST_TITLE);
    }

    public void setListTitle(ListTitle listTitle) {
        put(LIST_TITLE, listTitle);
        setListItemDirty(true);
    }

    public ListAttributes getAttributes() {
        return (ListAttributes) get(ATTRIBUTES);
    }

    public void setAttributes(ListAttributes attributes) {
        put(ATTRIBUTES, attributes);
        setListItemDirty(true);
    }

    public boolean isStruckOut() {
        return getBoolean(IS_STRUCK_OUT);
    }

    public void setIsStruckOut(boolean isStruckOut) {
        put(IS_STRUCK_OUT, isStruckOut);
        setListItemDirty(true);
    }

    public void toggleStrikeout() {
        put(IS_STRUCK_OUT, !isStruckOut());
        setListItemDirty(true);
    }

    public boolean isChecked() {
        return getBoolean(IS_CHECKED);
    }

    public void setChecked(boolean isChecked) {
        put(IS_CHECKED, isChecked);
        setListItemDirty(true);
    }

    public boolean isListItemDirty() {
        return getBoolean(IS_LIST_ITEM_DIRTY);
    }

    public void setListItemDirty(boolean isDirty) {
        put(IS_LIST_ITEM_DIRTY, isDirty);
    }

    public boolean isMarkedForDeletion() {
        return getBoolean(IS_MARKED_FOR_DELETION);
    }

    public void setMarkedForDeletion(boolean isMarkedForDeletion) {
        put(IS_MARKED_FOR_DELETION, isMarkedForDeletion);
        setListItemDirty(true);
    }

    public long getListItemManualSortKey() {
        return getLong(LIST_ITEM_MANUAL_SORT_KEY);
    }

    public void setListItemManualSortKey(long manualSortKey) {
        put(LIST_ITEM_MANUAL_SORT_KEY, manualSortKey);
        setListItemDirty(true);
    }

    public long getItemID() {
        return getLong(LIST_ITEM_ID);
    }

    public void setItemID() {
        long itemID = MySettings.getNextListItemID();
        put(LIST_ITEM_ID, itemID);
        setListItemDirty(true);
    }

    public Date getDateModified() {
        return getUpdatedAt();
    }

    public Date getDateCreated() {
        return getCreatedAt();
    }

    public static ParseQuery<ListItem> getQuery() {
        return ParseQuery.getQuery(ListItem.class);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static ListItem getListItem(String listItemID, boolean isUuid) {
        ListItem listItem = null;
        try {
            ParseQuery<ListItem> query = getQuery();
            if (isUuid) {
                query.whereEqualTo(LOCAL_UUID, listItemID);
            } else {
                query.whereEqualTo("objectId", listItemID);
            }
            query.include(ATTRIBUTES);
            query.fromLocalDatastore();
            listItem = query.getFirst();
        } catch (ParseException e) {
            MyLog.e("ListTitle", "getListTitle: ParseException: " + e.getMessage());
        }
        return listItem;
    }

    public static List<ListItem> getAllListItems(ListTitle listTitle) {

        List<ListItem> listItems = new ArrayList<>();
        if (listTitle != null) {
            boolean sortAlphabetically = listTitle.sortListItemsAlphabetically();
            try {
                ParseQuery<ListItem> query = getQuery();
                query.whereEqualTo(LIST_TITLE, listTitle);
                query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
                query.include(ATTRIBUTES);
                if (sortAlphabetically) {
                    query.orderByAscending(NAME_LOWERCASE);
                } else {
                    query.orderByAscending(LIST_ITEM_MANUAL_SORT_KEY);
                }
                query.fromLocalDatastore();
                listItems = query.find();
            } catch (ParseException e) {
                MyLog.e("ListItem", "getAllListItems: ParseException" + e.getMessage());
            }
        }
        return listItems;
    }

    public static List<ListItem> getStrikeoutItems(ListTitle listTitle) {
        List<ListItem> listItems = new ArrayList<>();
        if (listTitle != null) {
            try {
                ParseQuery<ListItem> query = getQuery();
                query.whereEqualTo(LIST_TITLE, listTitle);
                query.whereEqualTo(IS_STRUCK_OUT, true);
                query.fromLocalDatastore();
                listItems = query.find();
            } catch (ParseException e) {
                MyLog.e("ListItem", "getStrikeoutItems: ParseException" + e.getMessage());
            }
        }
        return listItems;
    }

    public static List<ListItem> getAllDirtyListItems() {

        List<ListItem> listDirtyItems = new ArrayList<>();
        try {
            ParseQuery<ListItem> query = getQuery();
            query.whereEqualTo(IS_LIST_ITEM_DIRTY, true);
            query.fromLocalDatastore();
            listDirtyItems = query.find();
        } catch (ParseException e) {
            MyLog.e("ListItem", "getAllDirtyListItems: ParseException" + e.getMessage());
        }

        return listDirtyItems;
    }

    public static List<ListItem> getAllListItemsMarkedForDeletion() {

        List<ListItem> listItemsMarkedForDeletion = new ArrayList<>();
        try {
            ParseQuery<ListItem> query = getQuery();
            query.whereEqualTo(IS_MARKED_FOR_DELETION, true);
            query.fromLocalDatastore();
            listItemsMarkedForDeletion = query.find();
        } catch (ParseException e) {
            MyLog.e("ListItem", "getAllListItemsMarkedForDeletion: ParseException" + e.getMessage());
        }

        return listItemsMarkedForDeletion;
    }

    public static int getListItemsCount(ListTitle listTitle) {
        int count = 0;
        if (listTitle != null) {
            try {
                ParseQuery<ListItem> query = getQuery();
                query.whereEqualTo(LIST_TITLE, listTitle);
                query.fromLocalDatastore();
                count = query.count();
            } catch (ParseException e) {
                MyLog.e("ListItem", "getListItemsCount: ParseException: " + e.getMessage());
            }
        }
        return count;
    }

    public static void unPinAll() {
        try {
            ParseQuery<ListItem> query = getQuery();
            query.fromLocalDatastore();
            List<ListItem> allListItems = query.find();
            if (allListItems != null && allListItems.size() > 0) {
                ParseObject.unpinAll(allListItems);
            }
        } catch (ParseException e) {
            MyLog.e("ListItem", "unPinAll: ParseException: " + e.getMessage());
        }
    }

    public static boolean itemExists(String proposedItemName) {
        // The ListItem exist if its lowercase name is in the datastore, AND
        // it is not marked for deletion

        boolean result = false;
        try {
            ParseQuery<ListItem> query = getQuery();
            query.whereEqualTo(NAME_LOWERCASE, proposedItemName.trim().toLowerCase());
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.fromLocalDatastore();
            ListItem listItem = query.getFirst();
            if (listItem != null) {
                result = true;
            }

        } catch (ParseException e) {
            if (e.getCode() != 101) {  // 101 = ObjectNotFound
                MyLog.e("ListItem", "itemExists: ParseException: " + e.getMessage());
            }
        }

        return result;
    }


}
