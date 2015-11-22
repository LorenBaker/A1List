package com.lbconsulting.a1list.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.CommonMethods;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestDataActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyLog.i("TestDataActivity", "onCreate");
        setContentView(R.layout.activity_test_data);

        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button btnUploadAttributes = (Button) findViewById(R.id.btnUploadAttributes);
        Button btnRetrieveAttributes = (Button) findViewById(R.id.btnRetrieveAttributes);
        Button btnUploadListTitles = (Button) findViewById(R.id.btnUploadListTitles);
        Button btnRetrieveListTitles = (Button) findViewById(R.id.btnRetrieveListTitles);
        Button btnUploadListItems = (Button) findViewById(R.id.btnUploadListItems);
        Button btnRetrieveListItems = (Button) findViewById(R.id.btnRetrieveListItems);

        btnUploadAttributes.setOnClickListener(this);
        btnRetrieveAttributes.setOnClickListener(this);
        btnUploadListTitles.setOnClickListener(this);
        btnRetrieveListTitles.setOnClickListener(this);
        btnUploadListItems.setOnClickListener(this);
        btnRetrieveListItems.setOnClickListener(this);

    }

    private static void showOkDialog(Context context, String title, String message) {
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnUploadAttributes:
                uploadTestAttributes();
                break;

            case R.id.btnRetrieveAttributes:
                retrieveAttributes();
                break;

            case R.id.btnUploadListTitles:
                uploadTestListTitles();
                break;

            case R.id.btnRetrieveListTitles:
                retrieveListTitles();
                break;

            case R.id.btnUploadListItems:
                uploadTestListItems();
                break;

            case R.id.btnRetrieveListItems:
                retrieveListItems();
                break;
        }
    }


    private void uploadTestAttributes() {
        if (!CommonMethods.isNetworkAvailable()) {
            showOkDialog(this, "Network Not Available", "Unable to upload attributes");
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<ListAttributes> listAttributes = new ArrayList<>();

        ListAttributes attributes;

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Orange");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#ff6c52"));
        attributes.setEndColor(Color.parseColor("#e0341e"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Sandrift");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#cbb59d"));
        attributes.setEndColor(Color.parseColor("#92806c"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Breaker Bay");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#6d8b93"));
        attributes.setEndColor(Color.parseColor("#31535c"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Shakespeare");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#73c5d3"));
        attributes.setEndColor(Color.parseColor("#308d9e"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Seagull");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#94dcea"));
        attributes.setEndColor(Color.parseColor("#4ea0ab"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.black));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Beige");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#fefefe"));
        attributes.setEndColor(Color.parseColor("#d3d8c2"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.black));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Arsenic");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#545c67"));
        attributes.setEndColor(Color.parseColor("#1d242c"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.black));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Acapulco");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#8dbab3"));
        attributes.setEndColor(Color.parseColor("#58857e"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Medium Wood");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#bfaa75"));
        attributes.setEndColor(Color.parseColor("#8a7246"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Sorbus");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#f0725b"));
        attributes.setEndColor(Color.parseColor("#bc3c21"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Paprika");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#994552"));
        attributes.setEndColor(Color.parseColor("#5f0c16"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Whiskey");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#e9ac6d"));
        attributes.setEndColor(Color.parseColor("#ad7940"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Pale Brown");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#ac956c"));
        attributes.setEndColor(Color.parseColor("#705c39"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Dark Khaki");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#ced285"));
        attributes.setEndColor(Color.parseColor("#9b9f55"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.white));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        attributes = new ListAttributes();
        attributes.setAuthor(ParseUser.getCurrentUser());
        attributes.setAttributesID();
        attributes.setName("Lemon Chiffon");
        attributes.setDefaultAttributes(false);
        attributes.setChecked(false);
        attributes.setStartColor(Color.parseColor("#fdfcdd"));
        attributes.setEndColor(Color.parseColor("#e3e2ac"));
        attributes.setTextColor(ContextCompat.getColor(this, R.color.black));
        attributes.setTextSize(17);
        attributes.setBold(false);
        attributes.setHorizontalPaddingDp(15);
        attributes.setVerticalPaddingDp(15);
        attributes.setBackgroundTransparent(false);
        attributes.setLocalUuid();
        attributes.setAttributesDirty(false);
        attributes.setMarkedForDeletion(false);
        listAttributes.add(attributes);

        int count = 0;
        for (ListAttributes item : listAttributes) {
            try {
                item.save();
                count++;
            } catch (ParseException e) {
                MyLog.e("uploadTestAttributes", "Error saving attributes \"" + item.getName()
                        + "\" : ParseException" + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String resultMessage = "Saved " + count + " Attributes to Parse. Duration = "
                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.";
        MyLog.i("uploadTestAttributes", resultMessage);
        showOkDialog(this, "Saved Attributes", resultMessage);
    }

    private void retrieveAttributes() {
        List<ListAttributes> allAttributes = new ArrayList<>();
        ParseQuery<ListAttributes> query = ListAttributes.getQuery();
        query.orderByAscending(ListAttributes.NAME_LOWERCASE);

        query.findInBackground(new FindCallback<ListAttributes>() {
            public void done(final List<ListAttributes> attributesList, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(attributesList, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            // old attributes deleted ...
                            // now save the new ones to the local data store
                            ParseObject.pinAllInBackground(attributesList);
                            showOkDialog(mContext, "Pinned Attributes", "Pinned " + attributesList.size() + " attributes.");
                        }
                    });

                } else {
                    MyLog.e("TestDataActivity", "retrieveAttributes: ParseException: " + e.getMessage());
                }
            }
        });
    }

    private void uploadTestListTitles() {
        long startTime = System.currentTimeMillis();
        String[] listTitleNames = getResources().getStringArray(R.array.list_title_names);
        ArrayList<ListTitle> listTitles = new ArrayList<>();
        List<ListAttributes> attributes = ListAttributes.getAllListAttributes();
        int attributesIndex = 0;
        int count = 1;
        for (String listTitleName : listTitleNames) {
            ListTitle listTitle = new ListTitle();
            listTitle.setAuthor(ParseUser.getCurrentUser());
            listTitle.setName(listTitleName);
            ListAttributes att = attributes.get(attributesIndex);
            MyLog.i("uploadTestListTitles", "Setting attribute \"" + att.getName()
                    + "\" to ListTitle \"" + listTitleName + "\". AttributesLocalUuid = " + att.getLocalUuid());
            listTitle.setAttributes(att);
            listTitle.setChecked(false);
            listTitle.setListTitleManualSortKey(count);
            listTitle.setSortListItemsAlphabetically(true);
            listTitles.add(listTitle);
            attributesIndex++;
            if (attributesIndex >= attributes.size()) {
                attributesIndex = 0;
            }
            count++;
        }

        count = 0;
        for (ListTitle listTitle : listTitles) {
            final String titleName = listTitle.getName();
            try {
                listTitle.save();
                count++;
            } catch (ParseException e) {
                MyLog.e("uploadTestListTitles", "Error saving ListTitle \"" + listTitle.getName()
                        + "\" : ParseException" + e.getMessage());
            }

        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String resultMessage = "Saved " + count + " ListTitles to Parse. Duration = "
                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.";
        MyLog.i("uploadTestListTitles", resultMessage);
        showOkDialog(this, "Saved ListTitles", resultMessage);
    }

    private void retrieveListTitles() {
        ParseQuery<ListTitle> query = ListTitle.getQuery();
        query.orderByAscending(ListAttributes.NAME_LOWERCASE);

        query.findInBackground(new FindCallback<ListTitle>() {
            public void done(final List<ListTitle> titlesList, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(titlesList, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            // old ListTitles deleted ...
                            // now save the new ones to the local data store
                            ParseObject.pinAllInBackground(titlesList);
                            showOkDialog(mContext, "Pinned ListTitles", "Pinned " + titlesList.size() + " ListTitles.");
                        }
                    });

                } else {
                    MyLog.e("TestDataActivity", "retrieveListTitles: ParseException: " + e.getMessage());
                }
            }
        });

    }

    private void uploadTestListItems() {
        long startTime = System.currentTimeMillis();
        String[] list1ItemNames = getResources().getStringArray(R.array.list_1_items);
        String[] list2ItemNames = getResources().getStringArray(R.array.list_2_items);
        String[] list3ItemNames = getResources().getStringArray(R.array.list_3_items);
        String[] list4ItemNames = getResources().getStringArray(R.array.list_4_items);

        ArrayList<String[]> itemNames = new ArrayList<>();
        itemNames.add(list1ItemNames);
        itemNames.add(list2ItemNames);
        itemNames.add(list3ItemNames);
        itemNames.add(list4ItemNames);

        ArrayList<ListItem> listItems = new ArrayList<>();
        List<ListTitle> listTitles = ListTitle.getAllListTitles(true);

        int listTitleIndex = 0;
        int count = 1;
        for (ListTitle listTitle : listTitles) {

            for (String listItemName : itemNames.get(listTitleIndex)) {
                ListItem listItem = new ListItem();
                listItem.setAuthor(ParseUser.getCurrentUser());
                listItem.setName(listItemName);
                listItem.setListTitle(listTitle);
                MyLog.i("uploadTestListItems", "Adding ListItem \"" + listItemName
                        + "\" to \"" + listTitle.getName() + "\". ListTitleLocalUuid = " + listTitle.getLocalUuid());

                ListAttributes att = listTitle.getAttributes();
                MyLog.i("uploadTestListItems", "Setting attribute \"" + att.getName()
                        + "\" to ListItem \"" + listItemName + "\". AttributesLocalUuid = " + att.getLocalUuid());
                listItem.setAttributes(att);
                listItem.setIsStruckOut(false);
                listItem.setChecked(false);
                listItem.setListItemManualSortKey(count);
                listItems.add(listItem);

                count++;
            }

            listTitleIndex++;
        }

        count = 0;
        for (ListItem item : listItems) {
            try {
                item.save();
                count++;
            } catch (ParseException e) {
                MyLog.e("uploadTestListItems", "Error saving ListItem \"" + item.getName()
                        + "\" : ParseException" + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String resultMessage = "Saved " + count + " ListItems to Parse. Duration = "
                + NumberFormat.getNumberInstance(Locale.US).format(duration) + " milliseconds.";
        MyLog.i("uploadTestListTitles", resultMessage);
        showOkDialog(this, "Saved ListItems", resultMessage);

    }

    private void retrieveListItems() {
        ParseQuery<ListItem> query = ListItem.getQuery();
        query.orderByAscending(ListAttributes.NAME_LOWERCASE);

        query.findInBackground(new FindCallback<ListItem>() {
            public void done(final List<ListItem> items, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(items, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            // old ListTitles deleted ...
                            // now save the new ones to the local data store
                            ParseObject.pinAllInBackground(items);
                            showOkDialog(mContext, "Pinned ListItems", "Pinned " + items.size() + " ListItems.");
                        }
                    });

                } else {
                    MyLog.e("TestDataActivity", "retrieveListItems: ParseException: " + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("TestDataActivity", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("TestDataActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("TestDataActivity", "onResume");
    }
}

