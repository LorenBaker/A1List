package com.lbconsulting.a1list.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user creates a new ListItem
 */
public class dialogNewListItem extends DialogFragment {

    private static final String ARG_LIST_UUID = "argListUuid";

    private EditText txtItemName;
    private TextInputLayout txtName_input_layout;

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
            mListTitle = ListTitle.getListTitle(listUuid);
            if (mListTitle == null) {
                String msg = "ListTitle with uuid = \"" + listUuid + "\" does not exist!";
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
                        if (addNewItem(txtItemName.getText().toString().trim())) {
                            EventBus.getDefault().post(new MyEvents.updateListUIAsync(mListTitle.getListTitleUuid()));
                            dismiss();
                        }
                    }
                });

                Button cancelButton = mNewListItemDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(17);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        EventBus.getDefault().post(new MyEvents.updateListUIAsync(mListTitle.getListTitleUuid()));
                        dismiss();
                    }
                });

                Button addNewButton = mNewListItemDialog.getButton(Dialog.BUTTON_NEUTRAL);
                addNewButton.setTextSize(17);
                addNewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (addNewItem(txtItemName.getText().toString().trim())) {
                            txtItemName.setText("");
                        }
                    }
                });
            }
        });
    }

    private boolean addNewItem(String newItemName) {
        boolean result = false;
        if (newItemName.isEmpty()) {
            String errorMsg = getActivity().getString(R.string.newItemName_isEmpty_error);
            txtName_input_layout.setError(errorMsg);

        } else if (ListItem.itemExists(mListTitle, newItemName)) {
            String errorMsg = String.format(getActivity()
                    .getString(R.string.newItemName_itemExists_error), newItemName);
            txtName_input_layout.setError(errorMsg);

        } else {
            // ok to create item
            ListItem.newInstance(newItemName, mListTitle);
            result = true;
        }
        return result;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogNewListItem", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_single_edit_text, null, false);

        // find the dialog's views
        txtItemName = (EditText) view.findViewById(R.id.txtName);
        txtName_input_layout = (TextInputLayout) view.findViewById(R.id.txtName_input_layout);
        txtName_input_layout.setHint(getActivity().getString(R.string.txtItemName_hint));
        txtItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtName_input_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // build the dialog
        mNewListItemDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.newListItemDialog_title)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .setNeutralButton(R.string.btnSaveNew_title, null)
                .create();

        return mNewListItemDialog;
    }

}
