package com.lbconsulting.a1list.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

/**
 * Helper methods for Application Settings
 */
public class MySettings {

    private static Context mContext;
    private static SharedPreferences mPreferences;
    public static final String NOT_AVAILABLE = "N/A";

    public static final int END_COLOR_PICKER = 1;
    public static final int START_COLOR_PICKER = 2;
    public static final int TEXT_COLOR_PICKER = 3;

    public static final int TEXT_SIZE_PICKER = 10;
    public static final int HORIZONTAL_PADDING_PICKER = 20;
    public static final int VERTICAL_PADDING_PICKER = 30;

    public static final String ARG_LIST_TITLE_ID = "listTitleID";

    public static final String SETTING_ACTIVE_LIST_TITLE_UUID = "activeListTitleUuid";
    public static final String SETTING_ALPHABETICALLY_SORT_NAVIGATION_MENU = "alphabeticallySortNavigationMenu";
    public static final String SETTING_IS_USER_INITIALIZED = "isUserInitialized";
    public static final String SETTING_IS_USER_EMAIL_VERIFIED = "isEmailVerified";
    public static final String SETTING_DEFAULT_ATTRIBUTES_ID = "defaultAttributesID";
    public static final String SETTING_NEXT_LIST_ITEM_ID = "nextListItemID";
    public static final String SETTING_NEXT_LIST_TITLE_ID = "nextListTitleID";


    public static void setContext(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //region SETTING_ACTIVE_LIST_TITLE_UUID
    public static String getActiveListTitleUuid() {
        return mPreferences.getString(SETTING_ACTIVE_LIST_TITLE_UUID, NOT_AVAILABLE);
    }

    public static void setActiveListTitleUuid(String activeListTitleUuid) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SETTING_ACTIVE_LIST_TITLE_UUID, activeListTitleUuid);
        editor.apply();
    }
    //endregion

    //region SETTING_ALPHABETICALLY_SORT_NAVIGATION_MENU
    public static boolean isAlphabeticallySortNavigationMenu() {
        return mPreferences.getBoolean(SETTING_ALPHABETICALLY_SORT_NAVIGATION_MENU, true);
    }

    public static void setAlphabeticallySortNavigationMenu(boolean isAlphabetical) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(SETTING_ALPHABETICALLY_SORT_NAVIGATION_MENU, isAlphabetical);
        editor.apply();
    }
    //endregion

    //region SETTING_APP_INITIALIZATION_DATE
    public static Calendar getAppInitializationDatePlus7Days() {
        Date createdAt = ParseUser.getCurrentUser().getCreatedAt();
        Calendar appInitializationDatePlus7Days = Calendar.getInstance();
        appInitializationDatePlus7Days.setTimeInMillis(createdAt.getTime());
        appInitializationDatePlus7Days.add(Calendar.DATE, 7);
//        appInitializationDatePlus7Days.add(Calendar.MINUTE, 1);
        return appInitializationDatePlus7Days;
    }


    public static boolean isAppInitializationDateGreaterThan7Days() {
        boolean result = true;
        Calendar currentDate = Calendar.getInstance();
        Calendar initializationDatePlus7Days = getAppInitializationDatePlus7Days();
        int compare = currentDate.compareTo(initializationDatePlus7Days);

        if (compare < 0) {
            result = false;
        }
        return result;
    }
    //endregion

    //region SETTING_IS_USER_INITIALIZED
    public static boolean isUserInitialized() {
        return mPreferences.getBoolean(SETTING_IS_USER_INITIALIZED, false);
    }

    public static void setIsUserInitialized(boolean isUserInitialized) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(SETTING_IS_USER_INITIALIZED, isUserInitialized);
        editor.apply();
    }
    //endregion

    //region SETTING_IS_USER_EMAIL_VERIFIED
    public static boolean isUserEmailVerified() {
        return mPreferences.getBoolean(SETTING_IS_USER_EMAIL_VERIFIED, false);
    }

    public static void setIsUserEmailVerified(boolean isUserEmailVerified) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(SETTING_IS_USER_EMAIL_VERIFIED, isUserEmailVerified);
        editor.apply();
    }
    //endregion


    //region SETTING_DEFAULT_ATTRIBUTES_ID
    public static int getDefaultAttributesID() {
        int defaultAttributesID = mPreferences.getInt(SETTING_DEFAULT_ATTRIBUTES_ID, 0);
        int nextDefaultAttributesID = defaultAttributesID + 1;
        setDefaultAttributesID(nextDefaultAttributesID);
        return defaultAttributesID;
    }

    public static void setDefaultAttributesID(int defaultAttributesID) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(SETTING_DEFAULT_ATTRIBUTES_ID, defaultAttributesID);
        editor.apply();
    }

    //endregion

    //region ListTitle and ListItem IDs
    public static long getNextListItemID() {
        long nextListItemID = mPreferences.getLong(SETTING_NEXT_LIST_ITEM_ID, 0);
        setNextListItemID(nextListItemID + 1);
        return nextListItemID;
    }

    public static void setNextListItemID(long nextListItemID) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(SETTING_NEXT_LIST_ITEM_ID, nextListItemID);
        editor.apply();
    }

    public static long getNextListTitleID() {
        long nextListTitleID = mPreferences.getLong(SETTING_NEXT_LIST_TITLE_ID, 0);
        setNextListTitleID(nextListTitleID + 1);
        return nextListTitleID;
    }

    public static void setNextListTitleID(long nextListTitleID) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(SETTING_NEXT_LIST_TITLE_ID, nextListTitleID);
        editor.apply();
    }
    //endregion
}
