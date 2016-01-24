package com.lbconsulting.a1list.database;

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
 * Parse object for an A1List Item.
 */

@ParseClassName("ListItems")
public class ListItem extends ParseObject {

    public static final String NAME_LOWERCASE = "nameLowercase";
    private static final String AUTHOR = "author";
    private static final String NAME = "name";
    private static final String LIST_TITLE = "ListTitle";
    private static final String ATTRIBUTES = "ListAttributes";
    private static final String IS_STRUCK_OUT = "isStruckOut";
    private static final String IS_CHECKED = "isChecked";
    private static final String IS_FAVORITE = "isFavorite";
    private static final String IS_LIST_ITEM_DIRTY = "isListItemDirty";
    private static final String IS_MARKED_FOR_DELETION = "isMarkedForDeletion";
    private static final String LIST_ITEM_MANUAL_SORT_KEY = "manualSortKey";
    private static final String LOCAL_UUID = "localUuid";
    private static final String LIST_ITEM_ID = "listItemId";

    public ListItem() {
        // A default constructor is required.
    }

    public static void newInstance(String newItemName, ListTitle listTitle) {
        try {
            ListItem newItem = new ListItem();
            newItem.setItemUuid();
            newItem.setItemID();
            newItem.setName(newItemName);
            newItem.setListTitle(listTitle);
            newItem.setAttributes(listTitle.getAttributes());
            newItem.setAuthor(ParseUser.getCurrentUser());
            newItem.setChecked(false);
            newItem.setIsFavorite(false);
            newItem.setMarkedForDeletion(false);
            newItem.setIsStruckOut(false);
            newItem.setListItemManualSortKey(newItem.getItemID());
            newItem.pin();
        } catch (ParseException e) {
            MyLog.e("ListItem", "newInstance: ParseException: " + e.getMessage());
        }
    }

    public static ListItem getListItem(String listItemID) {
        boolean isUuid = listItemID.contains("-");
        ListItem listItem = null;
        try {
            ParseQuery<ListItem> query = getQuery();
            if (isUuid) {
                query.whereEqualTo(LOCAL_UUID, listItemID);
            } else {
                query.whereEqualTo("objectId", listItemID);
            }
            query.include(ATTRIBUTES);
            query.include(LIST_TITLE);
            query.fromLocalDatastore();
            listItem = query.getFirst();
        } catch (ParseException e) {
            MyLog.e("ListItem", "getListItem: ParseException: " + e.getMessage());
        }
        return listItem;
    }
    public static void updateListItemAttributes(ListTitle listTitle) {
        List<ListItem> allItems = getAllListItems(listTitle);
        for (ListItem item : allItems) {
            item.setAttributes(listTitle.getAttributes());
        }
    }

    public static ParseQuery<ListItem> getQuery() {
        return ParseQuery.getQuery(ListItem.class);
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
                MyLog.i("ListItem", "getAllListItems: " + listTitle.getName() + "; found " +listItems.size() + " items.");
            } catch (ParseException e) {
                MyLog.e("ListItem", "getAllListItems: ParseException" + e.getMessage());
            }
        }
        return listItems;
    }

    public static List<ListItem> getFavorites(ListTitle listTitle) {
        List<ListItem> listItems = new ArrayList<>();
        if (listTitle != null) {
            try {
                ParseQuery<ListItem> query = getQuery();
                query.whereEqualTo(LIST_TITLE, listTitle);
                query.whereEqualTo(IS_FAVORITE, true);
                query.orderByAscending(NAME_LOWERCASE);
                query.fromLocalDatastore();
                listItems = query.find();
            } catch (ParseException e) {
                MyLog.e("ListItem", "getFavorites: ParseException" + e.getMessage());
            }
        }
        return listItems;
    }

    public static List<ListItem> getAllListItems(ListAttributes listAttributes) {
        List<ListItem> listItems = new ArrayList<>();
        if (listAttributes != null) {
            try {
                ParseQuery<ListItem> query = getQuery();
                query.whereEqualTo(ATTRIBUTES, listAttributes);
                query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
                query.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_LIST_ITEMS);
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
            query.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_LIST_ITEMS);
            query.fromLocalDatastore();
            listDirtyItems = query.find();
        } catch (ParseException e) {
            MyLog.e("ListItem", "getAllDirtyListItems: ParseException" + e.getMessage());
        }

        return listDirtyItems;
    }

    public static List<ListItem> getAllNonFavoriteListItemsMarkedForDeletion() {

        List<ListItem> listItemsMarkedForDeletion = new ArrayList<>();
        try {
            ParseQuery<ListItem> query = getQuery();
            query.whereEqualTo(IS_MARKED_FOR_DELETION, true);
            query.whereEqualTo(IS_FAVORITE, false);
            query.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_LIST_ITEMS);
            query.fromLocalDatastore();
            listItemsMarkedForDeletion = query.find();
        } catch (ParseException e) {
            MyLog.e("ListItem", "getAllNonFavoriteListItemsMarkedForDeletion: ParseException" + e.getMessage());
        }

        return listItemsMarkedForDeletion;
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

    public static boolean itemExists(ListTitle listTitle, String proposedItemName) {
        // The ListItem exist if
        // its lowercase name is in the datastore of the provided list
        // AND it is not marked for deletion

        boolean result = false;
        try {
            ParseQuery<ListItem> query = getQuery();
            query.whereEqualTo(NAME_LOWERCASE, proposedItemName.trim().toLowerCase());
            query.whereEqualTo(LIST_TITLE, listTitle);
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

    public boolean isFavorite() {
        return getBoolean(IS_FAVORITE);
    }

    public void setIsFavorite(boolean isFavorite) {
        put(IS_FAVORITE, isFavorite);
        setListItemDirty(true);
    }

    public void toggleFavorite() {
        put(IS_FAVORITE, !isFavorite());
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

    @Override
    public String toString() {
        return getName();
    }

    public static boolean itemExists(String proposedItemName) {
        // The Item exist if its lowercase name is in the datastore, AND
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

    public static boolean getIsSameObject(ListItem listItem, String proposedListName) {
        boolean result = false;
        try {
            ParseQuery<ListItem> query = getQuery();
            query.whereEqualTo(NAME_LOWERCASE, proposedListName.trim().toLowerCase());
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.fromLocalDatastore();
            ListItem existingListItem = query.getFirst();
            if (existingListItem.getItemUuid().equals(listItem.getItemUuid())) {
                result = true;
            }

        } catch (ParseException e) {
            if (e.getCode() != 101) {  // 101 = ObjectNotFound
                MyLog.e("ListItem", "getIsSameObject: ParseException: " + e.getMessage());
            }
        }

        return result;
    }
}
