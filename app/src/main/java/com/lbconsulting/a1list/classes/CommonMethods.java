package com.lbconsulting.a1list.classes;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

import com.lbconsulting.a1list.activities.App;
import com.lbconsulting.a1list.database.ListAttributes;

import java.util.List;


/**
 * A1List Common methods.
 */
public class CommonMethods {

    public static boolean stringsIdentical(String itemName1, String itemName2) {
        return itemName1 == null & itemName2 == null || !(itemName1 == null || itemName2 == null) && itemName1.equals(itemName2);
    }

//    public static ListAttributes getDefaultAttributes() {
//        ListAttributes defaultAttributes = null;
//
//        List<ListAttributes> allAttributes = ListAttributes.getAllListAttributes();
//        if (allAttributes.size() > 0) {
//            int defaultAttributesID = MySettings.getDefaultAttributesID();
//            if (defaultAttributesID >= allAttributes.size()) {
//                defaultAttributesID = 0;
//                MySettings.setDefaultAttributesID(1);
//            }
//            defaultAttributes = allAttributes.get(defaultAttributesID);
//        }
//
//        return defaultAttributes;
//    }

    public static boolean isNetworkAvailable() {
        Context context = App.getContext();
        boolean networkAvailable = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if ((ni != null) && (ni.isConnected())) {
            // We have a network connection
            networkAvailable = true;
        }
        if (networkAvailable) {
            MyLog.i("CommonMethods", "Network is available.");
        } else {
            MyLog.i("CommonMethods", "Network NOT available.");
        }

        return networkAvailable;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(int dp) {
        float floatDp = (float) dp;
        Resources resources = App.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = floatDp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px) {
        Resources resources = App.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
}
