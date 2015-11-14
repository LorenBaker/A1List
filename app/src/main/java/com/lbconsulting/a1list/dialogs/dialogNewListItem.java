package com.lbconsulting.a1list.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.parse.ParseException;
import com.parse.ParseUser;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user creates a new ListItem
 */
public class dialogNewListItem extends DialogFragment {

    private static final String ARG_LIST_UUID = "argListUuid";

    private EditText txtItemName;

    private ListTitle mListTitle;
    private AlertDialog mNewListItemDialog;

    public dialogNewListItem() {
        // Empty constructor required for DialogFragment
    }


    public static dialogNewListItem newInstance(String listUuid) {
        MyLog.i("dialogNewListItem", "newInstance");
        dialogNewListItem fragment = new dialogNewListItem();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_UUID, listUuid);
//        args.putString(ARG_ITEM_UUID, itemUuid);
//        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogNewListItem", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_LIST_UUID)) {
            String listUuid = args.getString((ARG_LIST_UUID));
            mListTitle = ListTitle.getListTitle(listUuid, true);
            if (mListTitle == null) {
                String okDialogTitle = "Error Creating New Item";
                String msg = "ListTitle with uuid = \"" + listUuid + "\" does not exist!";
                EventBus.getDefault().post(new MyEvents.showOkDialog(okDialogTitle, msg));
                MyLog.e("dialogNewListItem", "onCreate: " + msg);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogNewListItem", "onActivityCreated");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mNewListItemDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveButton = mNewListItemDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(17);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        addNewItem(txtItemName.getText().toString().trim());
                        EventBus.getDefault().post(new MyEvents.updateListUI());
                        dismiss();
                    }
                });

                Button cancelButton = mNewListItemDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(17);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        EventBus.getDefault().post(new MyEvents.updateListUI());
                        dismiss();
                    }
                });

                Button addNewButton = mNewListItemDialog.getButton(Dialog.BUTTON_NEUTRAL);
                addNewButton.setTextSize(17);
                addNewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        addNewItem(txtItemName.getText().toString().trim());
                        txtItemName.setText("");
                    }
                });
            }
        });
    }

    private void addNewItem(String newItemName) {
        String title = "Item Name Not Valid";
        if (newItemName.isEmpty()) {
            String msg = "The item's name cannot be empty!\n\nPlease try again.";
            EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));

        } else if (ListItem.itemExists(newItemName)) {
            String msg = "Item \"" + newItemName + " already exists.\nPlease try again.";
            EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));

        } else {
            // ok to create item
            createNewItem(newItemName);
        }
    }

    private void createNewItem(String newItemName) {
        try {
            ListItem newItem = new ListItem();
            newItem.setItemUuid();
            newItem.setItemID();
            newItem.setName(newItemName);
            newItem.setListTitle(mListTitle);
            newItem.setAttributes(mListTitle.getAttributes());
            newItem.setAuthor(ParseUser.getCurrentUser());
            newItem.setChecked(false);
//            newItem.setListItemDirty(true);
            newItem.setMarkedForDeletion(false);
            newItem.setIsStruckOut(false);
            newItem.setListItemManualSortKey(newItem.getItemID());
            newItem.pin();
        } catch (ParseException e) {
            MyLog.e("dialogNewListItem", "createNewItem: ParseException: " + e.getMessage());
        }
    }

//    private long getNextListItemSortKey(ListTitle listTitle) {
//        long sortKey = 0;
//        List<ListItem> listItems = ListItem.getAllListItems(listTitle);
//
//        for (ListItem listItem : listItems) {
//            if (listItem.getListItemManualSortKey() > sortKey) {
//                sortKey = listItem.getListItemManualSortKey();
//            }
//        }
//
//        sortKey++;
//        return sortKey;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogNewListItem", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_single_edit_text, null, false);

        // find the dialog's views
        txtItemName = (EditText) view.findViewById(R.id.txtName);
        txtItemName.setHint("Item Name");

        // build the dialog
        mNewListItemDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Create New Item")
                .setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Save/New", null)
                .create();

        return mNewListItemDialog;
    }

}
