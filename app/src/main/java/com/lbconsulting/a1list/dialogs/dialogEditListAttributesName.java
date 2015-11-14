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
import com.lbconsulting.a1list.database.ListAttributes;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user edits the ListAttributes' name
 */
public class dialogEditListAttributesName extends DialogFragment {

    private static final String ARG_LIST_ATTRIBUTES_UUID = "argListAttributesID";

    private EditText txtListAttributesName;
    private ListAttributes mAttributes;
    //    private String mName;
    private AlertDialog mEditListAttributesNameDialog;


    public dialogEditListAttributesName() {
        // Empty constructor required for DialogFragment
    }


    public static dialogEditListAttributesName newInstance(String attributesUuid) {
        MyLog.i("dialogEditListAttributesName", "newInstance");
        dialogEditListAttributesName fragment = new dialogEditListAttributesName();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_ATTRIBUTES_UUID, attributesUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogEditListAttributesName", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_LIST_ATTRIBUTES_UUID)) {
            String attributesUuid = args.getString(ARG_LIST_ATTRIBUTES_UUID);
            mAttributes = ListAttributes.getAttributes(attributesUuid, true);
            if (mAttributes == null) {
                String okDialogTitle = "Error Getting Attributes";
                String msg = "Attributes is null!";
                EventBus.getDefault().post(new MyEvents.showOkDialog(okDialogTitle, msg));
                MyLog.e("dialogEditListAttributesName", "onCreate: " + msg);
            }
        } else {
            String okDialogTitle = "Error Getting Attributes";
            String msg = "Fragment arguments do not contain " + ARG_LIST_ATTRIBUTES_UUID + "!";
            EventBus.getDefault().post(new MyEvents.showOkDialog(okDialogTitle, msg));
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
                        if (isValidAttributesName(attributesProposedName)) {
                            EventBus.getDefault().post(new MyEvents.setAttributesName(attributesProposedName));
                            dismiss();
                        } else {
                            txtListAttributesName.setText(mAttributes.getName());
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

    private boolean isValidAttributesName(String attributesProposedName) {

        boolean isValidName = false;
        String title = "Invalid Theme Name";

        if (attributesProposedName.isEmpty()) {
            String msg = "Theme name cannot be empty!";
            EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));

        } else {
            String existingAttributesID = ListAttributes.getExistingAttributesID(attributesProposedName);
            if (existingAttributesID != null) {
                // Found uuid for the proposed attributes name ...
                // check if the uuid is the same a the provided mAttributes' uuid
                if (existingAttributesID.equals(mAttributes.getLocalUuid())) {
                    isValidName = true;
                } else {
                    String msg = "Theme name already exists!";
                    EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));
                }
            } else {
                isValidName = true;
            }
        }

        return isValidName;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogEditListAttributesName", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_single_edit_text, null, false);

        // find the dialog's views
        txtListAttributesName = (EditText) view.findViewById(R.id.txtName);
        txtListAttributesName.setHint("Theme Name");
        txtListAttributesName.setText(mAttributes.getName());

        // build the dialog
        mEditListAttributesNameDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Edit Theme Name")
                .setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        return mEditListAttributesNameDialog;
    }

}
