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
import com.lbconsulting.a1list.database.ListAttributes;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user edits the ListAttributes' name
 */
public class dialogEditListAttributesName extends DialogFragment {

    private static final String ARG_ATTRIBUTES_ID = "argAttributesID";

    private EditText txtListAttributesName;
    private TextInputLayout txtListAttributesName_input_layout;
    private ListAttributes mAttributes;
    private AlertDialog mEditListAttributesNameDialog;


    public dialogEditListAttributesName() {
        // Empty constructor required for DialogFragment
    }


    public static dialogEditListAttributesName newInstance(String attributesID) {
        MyLog.i("dialogEditListAttributesName", "newInstance");
        dialogEditListAttributesName frag = new dialogEditListAttributesName();
        Bundle args = new Bundle();
        args.putString(ARG_ATTRIBUTES_ID, attributesID);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogEditListAttributesName", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_ATTRIBUTES_ID)) {
            String attributesID = args.getString(ARG_ATTRIBUTES_ID);
            mAttributes = ListAttributes.getAttributes(attributesID);
        }
        if (mAttributes == null) {
            String msg = "Attributes is null!";
            MyLog.e("dialogEditListAttributesName", "onCreate: " + msg);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogEditListAttributesName", "onActivityCreated");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditListAttributesNameDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button saveButton = mEditListAttributesNameDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(17);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        String attributesProposedName = txtListAttributesName.getText().toString().trim();

                        if (attributesProposedName.isEmpty()) {
                            String errorMsg = getActivity().getString(R.string.attributesProposedName_isEmpty_error);
                            txtListAttributesName_input_layout.setError(errorMsg);

                        } else if (!ListAttributes.isValidAttributesName(mAttributes, attributesProposedName)) {
                            txtListAttributesName.setText(mAttributes.getName());
                            String errorMsg = String.format(getActivity()
                                            .getString(R.string.attributesProposedName_invalidName_error),
                                    attributesProposedName);
                            txtListAttributesName_input_layout.setError(errorMsg);
                        } else {
                            mAttributes.setName(attributesProposedName);
                            EventBus.getDefault().post(new MyEvents.updateListTitleUI());
                            dismiss();
                        }
                    }
                });

                Button cancelButton = mEditListAttributesNameDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(17);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        dismiss();
                    }
                });

            }
        });
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogEditListAttributesName", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_single_edit_text, null, false);

        // find the dialog's views
        txtListAttributesName = (EditText) view.findViewById(R.id.txtName);
        txtListAttributesName.setText(mAttributes.getName());
        txtListAttributesName_input_layout = (TextInputLayout) view.findViewById(R.id.txtName_input_layout);
        txtListAttributesName_input_layout.setHint(getActivity().getString(R.string.txtListAttributesName_hint));
        txtListAttributesName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtListAttributesName_input_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // build the dialog
        mEditListAttributesNameDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.editListAttributesNameDialog_title)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mEditListAttributesNameDialog;
    }

}
