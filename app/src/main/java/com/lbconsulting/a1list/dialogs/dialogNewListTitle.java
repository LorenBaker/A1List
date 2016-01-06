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
import com.lbconsulting.a1list.database.ListTitle;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user creates a new ListTitle
 */
public class dialogNewListTitle extends DialogFragment {

    public static final int SOURCE_FROM_MAIN_ACTIVITY = 1;
    public static final int SOURCE_FROM_MANAGE_LISTS_ACTIVITY = 2;
    private static final String ARG_SOURCE = "argSource";

    private EditText txtListTitleName;
    private TextInputLayout txtListTitleName_input_layout;

    private AlertDialog mCreateListTitleDialog;
    private int mSource;

    public dialogNewListTitle() {
        // Empty constructor required for DialogFragment
    }


    public static dialogNewListTitle newInstance(int source) {
        MyLog.i("dialogNewListTitle", "newInstance");
        dialogNewListTitle frag = new dialogNewListTitle();
        Bundle args = new Bundle();
        args.putInt(ARG_SOURCE, source);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogNewListTitle", "onCreate");

        Bundle args = getArguments();
        if (args.containsKey(ARG_SOURCE)) {
            mSource = args.getInt(ARG_SOURCE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogNewListTitle", "onActivityCreated");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mCreateListTitleDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = mCreateListTitleDialog.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setTextSize(17);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (addNewList(txtListTitleName.getText().toString().trim())) {
                            switch (mSource) {
                                case SOURCE_FROM_MAIN_ACTIVITY:
                                    EventBus.getDefault().post(new MyEvents.startA1List(false));
                                    break;

                                case SOURCE_FROM_MANAGE_LISTS_ACTIVITY:
                                    EventBus.getDefault().post(new MyEvents.updateListTitleUI());
                                    break;
                            }

                            dismiss();
                        }
                    }
                });

                Button negativeButton = mCreateListTitleDialog.getButton(Dialog.BUTTON_NEGATIVE);
                negativeButton.setTextSize(17);
                negativeButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Cancel
                        EventBus.getDefault().post(new MyEvents.startA1List(false));
                        dismiss();
                    }
                });

                Button neutralButton = mCreateListTitleDialog.getButton(Dialog.BUTTON_NEUTRAL);
                neutralButton.setTextSize(17);
                neutralButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (addNewList(txtListTitleName.getText().toString().trim())) {
                            txtListTitleName.setText("");
                        }
                    }
                });
            }
        });
    }

    private boolean addNewList(String newListName) {
        boolean result = false;
        if (newListName.isEmpty()) {
            String errorMsg = getActivity().getString(R.string.newListName_isEmpty_error);
            txtListTitleName_input_layout.setError(errorMsg);

        } else if (ListTitle.listExists(newListName)) {
            String errorMsg = String.format(getActivity()
                    .getString(R.string.newListName_listExists_error), newListName);
            txtListTitleName_input_layout.setError(errorMsg);

        } else {
            // ok to create list
            ListTitle.newInstance(newListName);
            result = true;
        }
        return result;
    }




    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogNewListTitle", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_single_edit_text, null, false);

        // find the dialog's views
        txtListTitleName = (EditText) view.findViewById(R.id.txtName);
        txtListTitleName_input_layout = (TextInputLayout) view.findViewById(R.id.txtName_input_layout);
        txtListTitleName_input_layout.setHint(getActivity().getString(R.string.txtListTitleName_hint));
        txtListTitleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtListTitleName_input_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // build the dialog
        mCreateListTitleDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.createListTitleDialog_title)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .setNeutralButton(R.string.btnSaveNew_title, null)
                .create();

        return mCreateListTitleDialog;
    }

}
