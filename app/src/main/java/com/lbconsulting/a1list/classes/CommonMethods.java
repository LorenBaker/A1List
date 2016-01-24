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
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.parse.ParseException;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


/**
 * A1List Common methods.
 */
public class CommonMethods {

    public static void uploadDirtyAttributes() {
        long startTime = System.currentTimeMillis();

        List<ListAttributes> dirtyAttributes = ListAttributes.getAllDirtyListAttributes();
        MyLog.i("uploadDirtyListAttributes", "Found " + dirtyAttributes.size() + " dirty Attributes.");
        int count = 0;
        for (ListAttributes item : dirtyAttributes) {
            try {
                item.setAttributesDirty(false);
                item.save();
                count++;
            } catch (ParseException e) {
                item.setAttributesDirty(true);
                MyLog.e("uploadDirtyListAttributes", "Error saving dirty Attribute \"" + item.getName()
                        + "\" : ParseException" + e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        MyLog.i("uploadDirtyListAttributes", "Saved " + count + " Attributes to Parse. Duration = "
                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
    }

    public static  void uploadDirtyListTitles() {
        long startTime = System.currentTimeMillis();

        List<ListTitle> dirtyListTitles = ListTitle.getAllDirtyListTitles();
        MyLog.i("uploadDirtyListTitles", "Found " + dirtyListTitles.size() + " dirty List Titles.");
        int count = 0;
        for (ListTitle item : dirtyListTitles) {
            try {
                item.setListTitleDirty(false);
                item.save();
                count++;
            } catch (ParseException e) {
                item.setListTitleDirty(true);
                MyLog.e("uploadDirtyListTitles", "Error saving dirty List Title \"" + item.getName()
                        + "\" : ParseException" + e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        MyLog.i("uploadDirtyListTitles", "Saved " + count + " List Titles to Parse. Duration = "
                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
    }

    public static  void uploadDirtyListItems() {
        long startTime = System.currentTimeMillis();

        List<ListItem> dirtyListItems = ListItem.getAllDirtyListItems();
        MyLog.i("uploadDirtyListItems", "Found " + dirtyListItems.size() + " dirty List Items.");
        int count = 0;
        for (ListItem item : dirtyListItems) {
            try {
                item.setListItemDirty(false);
                item.save();
                count++;
            } catch (ParseException e) {
                item.setListItemDirty(true);
                MyLog.e("uploadDirtyListItems", "Error saving dirty List Item \"" + item.getName()
                        + "\" : ParseException" + e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        MyLog.i("uploadDirtyListItems", "Saved " + count + " List Items to Parse. Duration = "
                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");

    }

    public static  void deleteMarkedAttributes() {
        long startTime = System.currentTimeMillis();

        List<ListAttributes> attributesMarkedForDeletion = ListAttributes.getAllAttributesMarkedForDeletion();
        MyLog.i("deleteMarkedAttributes", "Found " + attributesMarkedForDeletion.size() + " marked Attributes.");
        int count = 0;
        for (ListAttributes item : attributesMarkedForDeletion) {
            try {
                item.unpin();
                item.delete();
                count++;
            } catch (ParseException e) {
                MyLog.e("deleteMarkedAttributes", "Error deleting Attributes \"" + item.getName()
                        + "\" : ParseException" + e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        MyLog.i("deleteMarkedAttributes", "Deleted " + count + " Attributes from Parse. Duration = "
                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
    }

    public static  void deleteMarkedListTitles() {
        long startTime = System.currentTimeMillis();

        List<ListTitle> listTitlesMarkedForDeletion = ListTitle.getAllListTitlesMarkedForDeletion();
        MyLog.i("deleteMarkedAttributes", "Found " + listTitlesMarkedForDeletion.size() + " marked List Titles.");
        int count = 0;
        for (ListTitle item : listTitlesMarkedForDeletion) {
            try {
                item.unpin();
                item.delete();
                count++;
            } catch (ParseException e) {
                MyLog.e("deleteMarkedAttributes", "Error deleting List Title \"" + item.getName()
                        + "\" : ParseException" + e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        MyLog.i("deleteMarkedAttributes", "Deleted " + count + " List Titles from Parse. Duration = "
                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
    }

    public static  void deleteMarkedListItems() {
        long startTime = System.currentTimeMillis();

        List<ListItem> allListItemsMarkedForDeletion = ListItem.getAllNonFavoriteListItemsMarkedForDeletion();
        MyLog.i("deleteMarkedListItems", "Found " + allListItemsMarkedForDeletion.size() + " marked List Items.");
        int count = 0;
        for (ListItem item : allListItemsMarkedForDeletion) {
            try {
                item.unpin();
                item.delete();
                count++;
            } catch (ParseException e) {
                MyLog.e("deleteMarkedListItems", "Error deleting List Item \"" + item.getName()
                        + "\" : ParseException" + e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        MyLog.i("deleteMarkedListItems", "Deleted " + count + " List Items from Parse. Duration = "
                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.");
    }


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
