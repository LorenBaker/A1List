package com.lbconsulting.a1list.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user creates a new ListItem
 */
public class dialogListTitleSorting extends DialogFragment {

    private RadioButton rbAlphabetical;
    private AlertDialog mListTitleSortingDialog;

    public dialogListTitleSorting() {
        // Empty constructor required for DialogFragment
    }

    public static dialogListTitleSorting newInstance() {
        MyLog.i("dialogListTitleSorting", "newInstance");
        return new dialogListTitleSorting();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogListTitleSorting", "onCreate");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogListTitleSorting", "onActivityCreated");

        mListTitleSortingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button okButton = mListTitleSortingDialog.getButton(Dialog.BUTTON_POSITIVE);
                okButton.setTextSize(17);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (rbAlphabetical.isChecked()) {
                            MySettings.setAlphabeticallySortNavigationMenu(true);
                        } else {
                            MySettings.setAlphabeticallySortNavigationMenu(false);
                        }
                        EventBus.getDefault().post(new MyEvents.updateListTitleUI());
                        dismiss();
                    }
                });

                Button cancelButton = mListTitleSortingDialog.getButton(Dialog.BUTTON_NEGATIVE);
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
        MyLog.i("dialogListTitleSorting", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_list_sorting, null, false);

        // find the dialog's views
        rbAlphabetical = (RadioButton) view.findViewById(R.id.rbAlphabetical);
        RadioButton rbManual = (RadioButton) view.findViewById(R.id.rbManual);

        if (MySettings.isAlphabeticallySortNavigationMenu()) {
            rbAlphabetical.setChecked(true);
        } else {
            rbManual.setChecked(true);
        }

        // build the dialog
        mListTitleSortingDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.listTitleSortingDialog_title)
                .setView(view)
                .setPositiveButton(R.string.btnOk_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mListTitleSortingDialog;
    }

}
