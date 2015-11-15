package com.lbconsulting.a1list.database;

import android.graphics.drawable.GradientDrawable;

import com.lbconsulting.a1list.classes.CommonMethods;
import com.lbconsulting.a1list.classes.MyEvents;
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

import de.greenrobot.event.EventBus;

/**
 * Parse object for an A1List Attributes.
 */

@ParseClassName("ListAttributes")
public class ListAttributes extends ParseObject {

    public static final String AUTHOR = "author";
    public static final String END_COLOR = "endColor"; // int
    public static final String HORIZONTAL_PADDING_DP = "horizontalPaddingInDp"; //float dp. Need to convert to float px
    public static final String IS_ATTRIBUTES_DIRTY = "isAttributesDirty";
    public static final String IS_BOLD = "isBold";
    public static final String IS_CHECKED = "isChecked";
    public static final String IS_DEFAULT_ATTRIBUTES = "isDefaultAttributes";
    public static final String IS_MARKED_FOR_DELETION = "isMarkedForDeletion";
    public static final String IS_TRANSPARENT = "isTransparent";
    public static final String LOCAL_UUID = "localUuid";
    public static final String NAME = "name";
    public static final String NAME_LOWERCASE = "nameLowercase";
    public static final String START_COLOR = "startColor"; // int
    public static final String TEXT_COLOR = "textColor"; // int
    public static final String TEXT_SIZE = "textSize"; //float
    public static final String VERTICAL_PADDING_DP = "verticalPaddingInDp"; //float dp. Need to convert to float px


    public ListAttributes() {
        // A default constructor is required.
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

    public ParseUser getAuthor() {
        return getParseUser(AUTHOR);
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

    public String getLowercaseName() {
        return getString(NAME_LOWERCASE);
    }


    public boolean isDefaultAttributes() {
        return getBoolean(IS_DEFAULT_ATTRIBUTES);
    }

    public void setDefaultAttributes(boolean isDefaultAttributes) {
        put(IS_DEFAULT_ATTRIBUTES, isDefaultAttributes);
        setAttributesDirty(true);
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

    public boolean isChecked() {
        return getBoolean(IS_CHECKED);
    }

    public void setChecked(boolean isChecked) {
        put(IS_CHECKED, isChecked);
        setAttributesDirty(true);
    }

    public boolean isAttributesDirty() {
        return getBoolean(IS_ATTRIBUTES_DIRTY);
    }

    public void setAttributesDirty(boolean isDirty) {
        put(IS_ATTRIBUTES_DIRTY, isDirty);
    }

    public boolean isMarkedForDeletion() {
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

    public int getHorizontalPaddingDp() {
        return getInt(HORIZONTAL_PADDING_DP);
    }

    public void setHorizontalPaddingDp(int paddingInDp) {
        put(HORIZONTAL_PADDING_DP, paddingInDp);
        setAttributesDirty(true);
    }

    public int getVerticalPaddingPx() {
        return CommonMethods.convertDpToPixel(getInt(VERTICAL_PADDING_DP));
    }

    public int getVerticalPaddingDp() {
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

    public Date getDateModified() {
        return getUpdatedAt();
    }

    public Date getDateCreated() {
        return getCreatedAt();
    }

    public static ParseQuery<ListAttributes> getQuery() {
        return ParseQuery.getQuery(ListAttributes.class);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static ListAttributes getAttributes(String attributeID, boolean isUuid) {
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

//    public static boolean isValidAttributesName(String attributesProposedName, ListAttributes sourceAttributes) {
//
//        boolean isValidName = false;
//        String title = "Invalid Theme Name";
//
//        if (attributesProposedName.isEmpty()) {
//            String msg = "Theme name cannot be empty!";
//            EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));
//
//        } else {
//            String existingAttributesID = getExistingAttributesID(attributesProposedName);
//            if (existingAttributesID != null) {
//                // Found uuid for the proposed attributes name ...
//                // check if the uuid is the same a the provided mAttributes' uuid
//                if (existingAttributesID.equals(sourceAttributes.getLocalUuid())) {
//                    isValidName = true;
//                } else {
//                    String msg = "Theme name already exists!";
//                    EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));
//                }
//            } else {
//                isValidName = true;
//            }
//        }
//
//        return isValidName;
//    }

    public static boolean isValidAttributesName(String attributesProposedName) {

        boolean isValidName = false;
        if (!attributesProposedName.isEmpty()) {
            String existingAttributesID = getExistingAttributesID(attributesProposedName);
            if (existingAttributesID == null) {
                isValidName = true;
            }
        }

        return isValidName;
    }

    private static String getExistingAttributesID(String proposedAttributesName) {
        // The ListItem exist if its lowercase name is in the datastore, AND
        // it is not marked for deletion

        String existingAttributesID = null;

        try {
            ParseQuery<ListAttributes> query = getQuery();
            query.whereEqualTo(NAME_LOWERCASE, proposedAttributesName.trim().toLowerCase());
            query.whereEqualTo(IS_MARKED_FOR_DELETION, false);
            query.fromLocalDatastore();
            ListAttributes attributes = query.getFirst();
            if (attributes != null) {
                existingAttributesID = attributes.getLocalUuid();
            }

        } catch (ParseException e) {

            if (e.getCode() != 101) {  // 101 = ObjectNotFound
                MyLog.e("ListItem", "itemExists: ParseException: " + e.getMessage());
            }
        }

        return existingAttributesID;
    }

//    public static  ListAttributes copyLocalListAttributes(ListAttributes sourceAttributes, ListAttributes targetAttributes) {
//
//        targetAttributes.setAuthor(ParseUser.getCurrentUser());
//        targetAttributes.setEndColor(sourceAttributes.getEndColor());
//        targetAttributes.setHorizontalPaddingDp(sourceAttributes.getHorizontalPaddingDp());
//        targetAttributes.setAttributesDirty(sourceAttributes.isAttributesDirty());
//        targetAttributes.setBold(sourceAttributes.isBold());
//        targetAttributes.setChecked(sourceAttributes.isChecked());
//        targetAttributes.setDefaultAttributes(sourceAttributes.isDefaultAttributes());
//        targetAttributes.setMarkedForDeletion(sourceAttributes.isMarkedForDeletion());
//        targetAttributes.setBackgroundTransparent(sourceAttributes.isBackgroundTransparent());
//        targetAttributes.setName(sourceAttributes.getName());
//        targetAttributes.setStartColor(sourceAttributes.getStartColor());
//        targetAttributes.setTextColor(sourceAttributes.getTextColor());
//        targetAttributes.setTextSize(sourceAttributes.getTextSize());
//        targetAttributes.setVerticalPaddingDp(sourceAttributes.getVerticalPaddingDp());
//
//        return targetAttributes;
//    }

    public static LocalListAttributes createLocalListAttributes(ListAttributes sourceAttributes) {
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


}
