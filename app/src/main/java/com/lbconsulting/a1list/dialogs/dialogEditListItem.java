package com.lbconsulting.a1list.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user creates a new ListItem
 */
public class dialogEditListItem extends DialogFragment {

    private static final String ARG_LIST_ITEM_ID = "argListItemID";

    private EditText txtListItemName;
    private TextInputLayout txtListItemName_input_layout;

    private AlertDialog mEditListItemDialog;
    private ListItem mListItem;

    public dialogEditListItem() {
        // Empty constructor required for DialogFragment
    }

    public static dialogEditListItem newInstance(String listItemID) {
        MyLog.i("dialogEditListItem", "newInstance");
        dialogEditListItem frag = new dialogEditListItem();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_ITEM_ID, listItemID);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogEditListItem", "onCreate");

        Bundle args = getArguments();
        if (args.containsKey(ARG_LIST_ITEM_ID)) {
            String listItemID = args.getString(ARG_LIST_ITEM_ID);
            mListItem = ListItem.getListItem(listItemID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogEditListItem", "onActivityCreated");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditListItemDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = mEditListItemDialog.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setTextSize(17);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (reviseItemName(txtListItemName.getText().toString().trim())) {
                            EventBus.getDefault().post(new MyEvents.updateListUI(mListItem.getListTitle().getListTitleUuid()));
                            dismiss();
                        }
                    }
                });

                Button negativeButton = mEditListItemDialog.getButton(Dialog.BUTTON_NEGATIVE);
                negativeButton.setTextSize(17);
                negativeButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Cancel
                        dismiss();
                    }
                });

            }
        });
    }

    private boolean reviseItemName(String newItemName) {
        boolean result = false;
        if (newItemName.isEmpty()) {
            String errorMsg = getActivity().getString(R.string.reviseItemName_isEmpty_error);
            txtListItemName_input_layout.setError(errorMsg);

        } else if (ListItem.itemExists(newItemName)) {
            boolean isSameObject = ListItem.getIsSameObject(mListItem, newItemName);
            if (isSameObject) {
                mListItem.setName(newItemName);
                result = true;
            } else {
                String errorMsg = String.format(getActivity()
                        .getString(R.string.reviseItemName_listExists_error), newItemName);
                txtListItemName_input_layout.setError(errorMsg);
            }

        } else {
            // ok to revise ItemName
            mListItem.setName(newItemName);
            result = true;
        }
        return result;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogEditListItem", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_single_edit_text, null, false);

        // find the dialog's views
        txtListItemName = (EditText) view.findViewById(R.id.txtName);
        txtListItemName.setText(mListItem.getName());
        txtListItemName_input_layout = (TextInputLayout) view.findViewById(R.id.txtName_input_layout);
        txtListItemName_input_layout.setHint(getActivity().getString(R.string.txtItemName_hint));
        txtListItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtListItemName_input_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // build the dialog
        mEditListItemDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.editListItemDialog_title)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mEditListItemDialog;
    }

}
