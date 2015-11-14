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
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListTitle;
import com.parse.ParseException;
import com.parse.ParseUser;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user creates a new ListTitle
 */
public class dialogNewListTitle extends DialogFragment {

    private EditText txtListName;
    private AlertDialog mNewListTitleDialog;

    public dialogNewListTitle() {
        // Empty constructor required for DialogFragment
    }


    public static dialogNewListTitle newInstance() {
        MyLog.i("dialogNewListTitle", "newInstance");
        return new dialogNewListTitle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogNewListTitle", "onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogNewListTitle", "onActivityCreated");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mNewListTitleDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = mNewListTitleDialog.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setTextSize(17);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        addNewList(txtListName.getText().toString().trim());
                        EventBus.getDefault().post(new MyEvents.startA1List(false));
                        dismiss();
                    }
                });

                Button negativeButton = mNewListTitleDialog.getButton(Dialog.BUTTON_NEGATIVE);
                negativeButton.setTextSize(17);
                negativeButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Cancel
                        EventBus.getDefault().post(new MyEvents.startA1List(false));
                        dismiss();
                    }
                });

                Button neutralButton = mNewListTitleDialog.getButton(Dialog.BUTTON_NEUTRAL);
                neutralButton.setTextSize(17);
                neutralButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        addNewList(txtListName.getText().toString().trim());
                        txtListName.setText("");
                    }
                });
            }
        });
    }

    private void addNewList(String newListName) {
        String title = "List Name Not Valid";
        if (newListName.isEmpty()) {
            String msg = "The list's name cannot be empty!\n\nPlease try again.";
            EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));

        } else if (ListTitle.listExists(newListName)) {
            String msg = "List \"" + newListName + "\" already exists.\nPlease try again.";
            EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));

        } else {
            // ok to create list
            createNewList(newListName);
        }
    }

    private void createNewList(String newListName) {
        ListTitle newListTitle = new ListTitle();
        try {
            newListTitle.setName(newListName);
            ListAttributes defaultAttributes = ListAttributes.getDefaultAttributes();
            newListTitle.setAttributes(defaultAttributes);
            newListTitle.setLocalUuid();
            newListTitle.setListID();
            newListTitle.setAuthor(ParseUser.getCurrentUser());
            newListTitle.setChecked(false);
            newListTitle.setListTitleDirty(true);
            newListTitle.setMarkedForDeletion(false);
            newListTitle.setSortListItemsAlphabetically(true);
            MySettings.setActiveListTitleUuid(newListTitle.getLocalUuid());
            newListTitle.setListTitleManualSortKey(newListTitle.getListID());
            newListTitle.pin();

        } catch (ParseException e) {
            MyLog.e("MainActivity", "createNewList; newListTitle.pin(): ParseException: " + e.getMessage());
        }
    }

//    private long getNextListTitleSortKey() {
//        long sortKey = 0;
//        List<ListTitle> listTitles = ListTitle.getAllListTitles(false);
//        if (listTitles.size() > 0) {
//            sortKey = listTitles.get(listTitles.size() - 1).getListTitleManualSortKey();
//            for (ListTitle listTitle : listTitles) {
//                if (listTitle.getListTitleManualSortKey() > sortKey) {
//                    sortKey = listTitle.getListTitleManualSortKey();
//                }
//            }
//        }
//        sortKey++;
//        return sortKey;
//    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogNewListTitle", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_single_edit_text, null, false);

        // find the dialog's views
        txtListName = (EditText) view.findViewById(R.id.txtName);
        txtListName.setHint("List Name");

        // build the dialog
        mNewListTitleDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Create New List")
                .setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Save/New", null)
                .create();

        return mNewListTitleDialog;
    }

}
