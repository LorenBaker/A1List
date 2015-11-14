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
import com.lbconsulting.a1list.activities.ListThemeActivity;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.LocalListAttributes;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user edits the ListAttributes' name
 */
public class dialogEditListAttributesName extends DialogFragment {

//    private static final String ARG_LIST_ATTRIBUTES_UUID = "argListAttributesID";

    private EditText txtListAttributesName;
    private LocalListAttributes mAttributes;
    //    private String mName;
    private AlertDialog mEditListAttributesNameDialog;


    public dialogEditListAttributesName() {
        // Empty constructor required for DialogFragment
    }


    public static dialogEditListAttributesName newInstance() {
        MyLog.i("dialogEditListAttributesName", "newInstance");
        return new dialogEditListAttributesName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogEditListAttributesName", "onCreate");
        mAttributes = ListThemeActivity.getLocalAttributes();
        if (mAttributes == null) {
            String okDialogTitle = "Error Getting Attributes";
            String msg = "Attributes is null!";
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
                        if (ListAttributes.isValidAttributesName(attributesProposedName)) {
                            EventBus.getDefault().post(new MyEvents.setAttributesName(attributesProposedName));
                            dismiss();
                        } else {
                            txtListAttributesName.setText(mAttributes.getName());
                            String title = "Invalid Theme Name";
                            String msg = "Theme \"" + attributesProposedName
                                    + "\" already exists.\n\nPlease enter a unique theme name.";
                            ListThemeActivity.showOkDialog(getActivity(),title,msg);
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
