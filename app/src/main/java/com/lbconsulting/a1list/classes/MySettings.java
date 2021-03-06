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

    public static final String NOT_AVAILABLE = "N/A";
    public static final int END_COLOR_PICKER = 1;
    public static final int START_COLOR_PICKER = 2;
    public static final int TEXT_COLOR_PICKER = 3;
    public static final int TEXT_SIZE_PICKER = 10;
    public static final int HORIZONTAL_PADDING_PICKER = 20;
    public static final int VERTICAL_PADDING_PICKER = 30;


    public static final String ARG_LIST_TITLE_ID = "listTitleID";

    private static final String SETTING_ACTIVE_LIST_TITLE_UUID = "activeListTitleUuid";
    private static final String SETTING_ALPHABETICALLY_SORT_NAVIGATION_MENU = "alphabeticallySortNavigationMenu";
    private static final String SETTING_DEFAULT_ATTRIBUTES_ID = "defaultAttributesID";
    private static final String SETTING_IS_USER_EMAIL_VERIFIED = "isEmailVerified";
    private static final String SETTING_IS_USER_INITIALIZED = "isUserInitialized";
    private static final String SETTING_NEXT_LIST_ATTRIBUTES_ID = "nextListAttributesID";
    private static final String SETTING_NEXT_LIST_ITEM_ID = "nextListItemID";
    private static final String SETTING_NEXT_LIST_TITLE_ID = "nextListTitleID";
    private static final String SETTING_REFRESH_DATA_FROM_THE_CLOUD = "refreshDataFromTheCloud";
    private static final String SETTING_IS_FIRST_TIME_RUN = "isFirstTimeRun";
    private static final String SETTING_LAST_TIME_SYNCED = "lastTimeSynced";
    public static final String SETTING_SYNC_DURATION = "syncDuration";

    private static SharedPreferences mPreferences;

    public static void setContext(Context context) {
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

    //region SETTING_IS_FORCE_VIEW_INFLATION
//    public static boolean isForceViewInflation() {
//        return mPreferences.getBoolean(SETTING_IS_FORCE_VIEW_INFLATION, false);
//    }
//
//    public static void setIsForceViewInflation(boolean isForceViewInflation) {
//        SharedPreferences.Editor editor = mPreferences.edit();
//        editor.putBoolean(SETTING_IS_FORCE_VIEW_INFLATION, isForceViewInflation);
//        editor.apply();
//    }
    //endregion

    //region SETTING_APP_INITIALIZATION_DATE
    private static Calendar getAppInitializationDatePlus7Days() {
        Date createdAt = ParseUser.getCurrentUser().getCreatedAt();
        Calendar appInitializationDatePlus7Days = Calendar.getInstance();
        appInitializationDatePlus7Days.setTimeInMillis(createdAt.getTime());
        appInitializationDatePlus7Days.add(Calendar.DATE, 7);
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

    //region SETTING_REFRESH_DATA_FROM_THE_CLOUD

    public static boolean getRefreshDataFromTheCloud() {
        return mPreferences.getBoolean(SETTING_REFRESH_DATA_FROM_THE_CLOUD, true);
    }

    public static void setRefreshDataFromTheCloud(boolean refreshDataFromTheCloud) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(SETTING_REFRESH_DATA_FROM_THE_CLOUD, refreshDataFromTheCloud);
        editor.apply();
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

    //region SETTING_IS_FIRST_TIME_RUN
    public static boolean isFirstTimeRun() {
        return mPreferences.getBoolean(SETTING_IS_FIRST_TIME_RUN, true);
    }

    public static void setIsFirstTimeRun(boolean isFirstTimeRun) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(SETTING_IS_FIRST_TIME_RUN, isFirstTimeRun);
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

    //region SETTING_LAST_TIME_SYNCED
    private static long getLastTimeSynced() {
        return mPreferences.getLong(SETTING_LAST_TIME_SYNCED, 0);
    }

    public static void setLastTimeSynced(long lastTimeSynced) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(SETTING_LAST_TIME_SYNCED, lastTimeSynced);
        editor.apply();
    }

    public static boolean requiresSyncing() {
        boolean result = false;
        long duration = System.currentTimeMillis() - getLastTimeSynced();
        long requiredDuration = getRequiredSyncDuration();
        if (requiredDuration > -1 && duration > requiredDuration) {
            result = true;
        }
        return result;
    }

    private static long getRequiredSyncDuration() {
        String syncDuration = mPreferences.getString(SETTING_SYNC_DURATION, "0");
        return Long.parseLong(syncDuration);
    }

    public static String getRequiredSyncDurationString() {
        return mPreferences.getString(SETTING_SYNC_DURATION, "0");
    }

    //endregion

    //region ListAttributes, ListTitle and ListItem IDs

    public static long getNextListAttributesID() {
        long nextListAttributesID = mPreferences.getLong(SETTING_NEXT_LIST_ATTRIBUTES_ID, 0);
        setNextListAttributesID(nextListAttributesID + 1);
        return nextListAttributesID;
    }

    public static void setNextListAttributesID(long nextListAttributesID) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(SETTING_NEXT_LIST_ATTRIBUTES_ID, nextListAttributesID);
        editor.apply();
    }

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
