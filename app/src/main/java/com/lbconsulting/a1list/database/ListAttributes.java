package com.lbconsulting.a1list.database;

import android.graphics.drawable.GradientDrawable;

import com.lbconsulting.a1list.classes.CommonMethods;
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
 * Parse object for an A1List Attributes.
 */

@ParseClassName("ListAttributes")
public class ListAttributes extends ParseObject {

    public static final String NAME_LOWERCASE = "nameLowercase";
    private static final String AUTHOR = "author";
    private static final String END_COLOR = "endColor"; // int
    private static final String HORIZONTAL_PADDING_DP = "horizontalPaddingInDp"; //float dp. Need to convert to float px
    private static final String IS_ATTRIBUTES_DIRTY = "isAttributesDirty";
    private static final String IS_BOLD = "isBold";
    private static final String IS_CHECKED = "isChecked";
    private static final String IS_DEFAULT_ATTRIBUTES = "isDefaultAttributes";
    private static final String IS_MARKED_FOR_DELETION = "isMarkedForDeletion";
    private static final String IS_TRANSPARENT = "isTransparent";
    private static final String LOCAL_UUID = "localUuid";
    private static final String NAME = "name";
    private static final String START_COLOR = "startColor"; // int
    private static final String TEXT_COLOR = "textColor"; // int
    private static final String TEXT_SIZE = "textSize"; //float
    private static final String VERTICAL_PADDING_DP = "verticalPaddingInDp"; //float dp. Need to convert to float px
    private static final String LIST_ATTRIBUTES_ID = "listAttributesID";

    public ListAttributes() {
        // A default constructor is required.
    }

    public static void clearAllDefaultAttributes() {
        List<ListAttributes> allDefaultAttributes = new ArrayList<>();
        try {
            ParseQuery<ListAttributes> query = getQuery();
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.whereEqualTo(IS_DEFAULT_ATTRIBUTES, true);
            query.fromLocalDatastore();
            allDefaultAttributes = query.find();
        } catch (ParseException e) {
            MyLog.e("ListAttributes", "getAllListAttributes: ParseException: " + e.getMessage());
        }
        for (ListAttributes attributes : allDefaultAttributes) {
            attributes.setDefaultAttributes(false);
        }
    }

    public static ParseQuery<ListAttributes> getQuery() {
        return ParseQuery.getQuery(ListAttributes.class);
    }

    public static ListAttributes getAttributes(String attributeID) {
        boolean isUuid = attributeID.contains("-");
        ListAttributes attributes = null;
        try {
            ParseQuery<ListAttributes> query = getQuery();
            if (isUuid) {
                query.whereEqualTo(LOCAL_UUID, attributeID);
            } else {
                query.whereEqualTo("objectId", attributeID);
            }
            query.fromLocalDatastore();
            attributes = query.getFirst();
        } catch (ParseException e) {
            MyLog.i("ListAttributes", "getAttributes: ParseException: " + e.getMessage());
        }
        return attributes;
    }

    public static List<ListAttributes> getAllListAttributes() {
        List<ListAttributes> allAttributes = new ArrayList<>();
        try {
            ParseQuery<ListAttributes> query = getQuery();
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.orderByAscending(NAME_LOWERCASE);
            query.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_ATTRIBUTES);
            query.fromLocalDatastore();
            allAttributes = query.find();
        } catch (ParseException e) {
            MyLog.e("ListAttributes", "getAllListAttributes: ParseException: " + e.getMessage());
        }
        return allAttributes;
    }

    public static ListAttributes getDefaultAttributes() {
        ListAttributes defaultAttributes = null;
        try {
            ParseQuery<ListAttributes> query = getQuery();
            query.whereEqualTo(IS_DEFAULT_ATTRIBUTES, true);
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.fromLocalDatastore();
            defaultAttributes = query.getFirst();
        } catch (ParseException e) {
            MyLog.i("ListAttributes", "getDefaultAttributes: ParseException: " + e.getMessage());
        }
        if (defaultAttributes == null) {
            List<ListAttributes> allAttributes = getAllListAttributes();
            if (allAttributes.size() > 0) {
                int defaultAttributesID = MySettings.getDefaultAttributesID();
                if (defaultAttributesID >= allAttributes.size()) {
                    defaultAttributesID = 0;
                    MySettings.setDefaultAttributesID(1);
                }
                defaultAttributes = allAttributes.get(defaultAttributesID);
            }
        }
        return defaultAttributes;
    }

    public static List<ListAttributes> getAllDirtyListAttributes() {
        List<ListAttributes> allDirtyAttributes = new ArrayList<>();
        try {
            ParseQuery<ListAttributes> query = getQuery();
            query.whereEqualTo(IS_ATTRIBUTES_DIRTY, true);
            query.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_ATTRIBUTES);
            query.fromLocalDatastore();
            allDirtyAttributes = query.find();
        } catch (ParseException e) {
            MyLog.e("ListAttributes", "getAllDirtyListAttributes: ParseException: " + e.getMessage());
        }
        return allDirtyAttributes;
    }

    public static List<ListAttributes> getAllAttributesMarkedForDeletion() {
        List<ListAttributes> allAttributesMarkedForDeletion = new ArrayList<>();
        try {
            ParseQuery<ListAttributes> query = getQuery();
            query.whereEqualTo(IS_MARKED_FOR_DELETION, true);
            query.setLimit(UpAndDownloadDataAsyncTask.QUERY_LIMIT_ATTRIBUTES);
            query.fromLocalDatastore();
            allAttributesMarkedForDeletion = query.find();
        } catch (ParseException e) {
            MyLog.e("ListAttributes", "getAllAttributesMarkedForDeletion: ParseException: " + e.getMessage());
        }
        return allAttributesMarkedForDeletion;
    }

    public static void unPinAll() {
        try {
            List<ListAttributes> attributes = getAllListAttributes();
            if (attributes != null && attributes.size() > 0) {
                ParseObject.unpinAll(attributes);
            }
        } catch (ParseException e) {
            MyLog.e("ListAttributes", "unPinAll: ParseException: " + e.getMessage());
        }
    }

    public static boolean isValidAttributesName(String attributesProposedName) {

        boolean isValidName = false;
        if (!attributesProposedName.isEmpty()) {
            ListAttributes existingAttributes = getExistingAttributes(attributesProposedName);
            if (existingAttributes == null) {
                isValidName = true;
            }
        }
        return isValidName;
    }

    public static boolean isValidAttributesName(ListAttributes originalAttributes, String attributesProposedName) {

        boolean isValidName = false;
        if (!attributesProposedName.isEmpty()) {
            ListAttributes existingAttributes = getExistingAttributes(attributesProposedName);
            if (existingAttributes == null) {
                isValidName = true;
            } else {
                // found existing attributes ... now see if it is the same object
                if (existingAttributes.getLocalUuid().equals(originalAttributes.getLocalUuid())) {
                    // both the original and existing attributes are the same Parse Object
                    isValidName = true;
                }
            }
        }
        return isValidName;
    }

    private static ListAttributes getExistingAttributes(String proposedAttributesName) {
        // The ListItem exist if its lowercase name is in the datastore, AND
        // it is not marked for deletion

        ListAttributes attributes = null;

        try {
            ParseQuery<ListAttributes> query = getQuery();
            query.whereEqualTo(NAME_LOWERCASE, proposedAttributesName.trim().toLowerCase());
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.fromLocalDatastore();
            attributes = query.getFirst();

        } catch (ParseException e) {

            if (e.getCode() != 101) {  // 101 = ObjectNotFound
                MyLog.e("ListItem", "itemExists: ParseException: " + e.getMessage());
            }
        }
        return attributes;
    }

    public static LocalListAttributes createLocalAttributes(ListAttributes sourceAttributes) {
        LocalListAttributes targetAttributes = new LocalListAttributes();

        targetAttributes.setEndColor(sourceAttributes.getEndColor());
        targetAttributes.setHorizontalPaddingInDp(sourceAttributes.getHorizontalPaddingDp());
        targetAttributes.setIsAttributesDirty(sourceAttributes.isAttributesDirty());
        targetAttributes.setIsBold(sourceAttributes.isBold());
        targetAttributes.setIsChecked(sourceAttributes.isChecked());
        targetAttributes.setIsDefaultAttributes(sourceAttributes.isDefaultAttributes());
        targetAttributes.setIsMarkedForDeletion(sourceAttributes.isMarkedForDeletion());
        targetAttributes.setIsTransparent(sourceAttributes.isBackgroundTransparent());
        targetAttributes.setName(sourceAttributes.getName());
        targetAttributes.setStartColor(sourceAttributes.getStartColor());
        targetAttributes.setTextColor(sourceAttributes.getTextColor());
        targetAttributes.setTextSize(sourceAttributes.getTextSize());
        targetAttributes.setVerticalPaddingInDp(sourceAttributes.getVerticalPaddingDp());

        return targetAttributes;
    }

    public static ListAttributes copyLocalListAttributes(LocalListAttributes sourceAttributes, ListAttributes targetAttributes) {

        targetAttributes.setEndColor(sourceAttributes.getEndColor());
        targetAttributes.setHorizontalPaddingDp(sourceAttributes.getHorizontalPaddingInDp());
        targetAttributes.setAttributesDirty(sourceAttributes.isAttributesDirty());
        targetAttributes.setBold(sourceAttributes.isBold());
        targetAttributes.setChecked(sourceAttributes.isChecked());
        targetAttributes.setDefaultAttributes(sourceAttributes.isDefaultAttributes());
        targetAttributes.setMarkedForDeletion(sourceAttributes.isMarkedForDeletion());
        targetAttributes.setBackgroundTransparent(sourceAttributes.isTransparent());
        targetAttributes.setName(sourceAttributes.getName());
        targetAttributes.setStartColor(sourceAttributes.getStartColor());
        targetAttributes.setTextColor(sourceAttributes.getTextColor());
        targetAttributes.setTextSize(sourceAttributes.getTextSize());
        targetAttributes.setVerticalPaddingDp(sourceAttributes.getVerticalPaddingInDp());

        return targetAttributes;
    }

    private String getObjectID() {
        return getObjectId();
    }

    public void setLocalUuid() {
        UUID uuid = UUID.randomUUID();
        put(LOCAL_UUID, uuid.toString());
        setAttributesDirty(true);
    }

    public String getLocalUuid() {
        String uuidString = getString(LOCAL_UUID);
        if (uuidString == null || uuidString.isEmpty()) {
            uuidString = getObjectID();
        }
        return uuidString;
    }

    public long getAttributesID() {
        return getLong(LIST_ATTRIBUTES_ID);
    }

    public void setAttributesID() {
        long attributesID = MySettings.getNextListAttributesID();
        put(LIST_ATTRIBUTES_ID, attributesID);
        setAttributesDirty(true);
    }

    public void setAuthor(ParseUser currentUser) {
        put(AUTHOR, currentUser);
        setAttributesDirty(true);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String attributeName) {
        put(NAME, attributeName);
        put(NAME_LOWERCASE, attributeName.toLowerCase());
        setAttributesDirty(true);
    }

    private boolean isDefaultAttributes() {
        return getBoolean(IS_DEFAULT_ATTRIBUTES);
    }

    public void setDefaultAttributes(boolean isDefaultAttributes) {
        put(IS_DEFAULT_ATTRIBUTES, isDefaultAttributes);
        setAttributesDirty(true);
    }

    private boolean isChecked() {
        return getBoolean(IS_CHECKED);
    }

    public void setChecked(boolean isChecked) {
        put(IS_CHECKED, isChecked);
        setAttributesDirty(true);
    }

    private boolean isAttributesDirty() {
        return getBoolean(IS_ATTRIBUTES_DIRTY);
    }

    public void setAttributesDirty(boolean isDirty) {
        put(IS_ATTRIBUTES_DIRTY, isDirty);
    }

    private boolean isMarkedForDeletion() {
        return getBoolean(IS_MARKED_FOR_DELETION);
    }

    public void setMarkedForDeletion(boolean isMarkedForDeletion) {
        put(IS_MARKED_FOR_DELETION, isMarkedForDeletion);
        setAttributesDirty(true);
    }

    public float getTextSize() {
        double fontSize = getDouble(TEXT_SIZE);
        return (float) fontSize;
    }

    public void setTextSize(float fontSize) {
        double size = (double) fontSize;
        put(TEXT_SIZE, size);
        setAttributesDirty(true);
    }

    public int getStartColor() {
        return getInt(START_COLOR);
    }

    public void setStartColor(int startColor) {
        put(START_COLOR, startColor);
        setAttributesDirty(true);
    }

    public int getEndColor() {
        return getInt(END_COLOR);
    }

    public void setEndColor(int endColor) {
        put(END_COLOR, endColor);
        setAttributesDirty(true);
    }

    public GradientDrawable getBackgroundDrawable() {
        int colors[] = {getStartColor(), getEndColor()};
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
    }

    public int getTextColor() {
        return getInt(TEXT_COLOR);
    }

    public void setTextColor(int fontColor) {
        put(TEXT_COLOR, fontColor);
        setAttributesDirty(true);
    }

    public boolean isBold() {
        return getBoolean(IS_BOLD);
    }

    public void setBold(boolean isBold) {
        put(IS_BOLD, isBold);
        setAttributesDirty(true);
    }

    public int getHorizontalPaddingPx() {
        return CommonMethods.convertDpToPixel(getInt(HORIZONTAL_PADDING_DP));
    }

    private int getHorizontalPaddingDp() {
        return getInt(HORIZONTAL_PADDING_DP);
    }

    public void setHorizontalPaddingDp(int paddingInDp) {
        put(HORIZONTAL_PADDING_DP, paddingInDp);
        setAttributesDirty(true);
    }

    public int getVerticalPaddingPx() {
        return CommonMethods.convertDpToPixel(getInt(VERTICAL_PADDING_DP));
    }

    private int getVerticalPaddingDp() {
        return getInt(VERTICAL_PADDING_DP);
    }

    public void setVerticalPaddingDp(int paddingInDp) {
        put(VERTICAL_PADDING_DP, paddingInDp);
        setAttributesDirty(true);
    }

    public boolean isBackgroundTransparent() {
        return getBoolean(IS_TRANSPARENT);
    }

    public void setBackgroundTransparent(boolean isTransparent) {
        put(IS_TRANSPARENT, isTransparent);
        setAttributesDirty(true);
    }

    @Override
    public String toString() {
        return getName();
    }
}
