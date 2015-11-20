package com.lbconsulting.a1list.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
public class dialogEditListTitle extends DialogFragment {

    private static final String ARG_LIST_TITLE_ID = "argListTitleID";

    private EditText txtListTitleName;
    private TextInputLayout txtListTitleName_input_layout;

    private AlertDialog mNewListTitleDialog;
    private ListTitle mListTitle;

    public dialogEditListTitle() {
        // Empty constructor required for DialogFragment
    }


    public static dialogEditListTitle newInstance(String listTitleID) {
        MyLog.i("dialogEditListTitle", "newInstance");
        dialogEditListTitle frag = new dialogEditListTitle();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_TITLE_ID, listTitleID);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogEditListTitle", "onCreate");

        Bundle args = getArguments();
        if (args.containsKey(ARG_LIST_TITLE_ID)) {
            String listTitleID = args.getString(ARG_LIST_TITLE_ID);
            mListTitle = ListTitle.getListTitle(listTitleID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogEditListTitle", "onActivityCreated");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mNewListTitleDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = mNewListTitleDialog.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setTextSize(17);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (reviseListName(txtListTitleName.getText().toString().trim())) {
                            EventBus.getDefault().post(new MyEvents.updateListTitleUI());
                            dismiss();
                        }
                    }
                });

                Button negativeButton = mNewListTitleDialog.getButton(Dialog.BUTTON_NEGATIVE);
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

    private boolean reviseListName(String newListName) {
        boolean result = false;
        if (newListName.isEmpty()) {
            String errorMsg = "The List's name cannot be empty.\nPlease enter a unique List name.";
            txtListTitleName_input_layout.setError(errorMsg);

        } else if (ListTitle.listExists(newListName)) {
            boolean isSameObject = ListTitle.getIsSameObject(mListTitle, newListName);
            if(isSameObject){
                mListTitle.setName(newListName);
                result = true;
            }else {
                String errorMsg = "List \"" + newListName
                        + "\" already exists.\nPlease enter a unique List name.";
                txtListTitleName_input_layout.setError(errorMsg);
            }

        } else {
            // ok to create list
            mListTitle.setName(newListName);
            result = true;
        }
        return result;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogEditListTitle", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_single_edit_text, null, false);

        // find the dialog's views
        txtListTitleName = (EditText) view.findViewById(R.id.txtName);
        txtListTitleName.setText(mListTitle.getName());
        txtListTitleName_input_layout = (TextInputLayout) view.findViewById(R.id.txtName_input_layout);
        txtListTitleName_input_layout.setHint("List Name");
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
        mNewListTitleDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Edit List Name")
                .setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        return mNewListTitleDialog;
    }

}
