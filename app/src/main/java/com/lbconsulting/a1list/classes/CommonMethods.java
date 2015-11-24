package com.lbconsulting.a1list.classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.widget.Button;

import com.lbconsulting.a1list.activities.App;


/**
 * A1List Common methods.
 */
public class CommonMethods {

    public static void showOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set dialog title and message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnOK = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                btnOK.setTextSize(18);
            }
        });

        // show it
        alertDialog.show();
    }

    public static boolean isNetworkAvailable() {
        boolean networkAvailable = false;
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

/*    *//**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     *//*
    public static float convertPixelsToDp(float px) {
        Resources resources = App.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }*/
}
