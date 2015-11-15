package com.lbconsulting.a1list.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user edits the ListAttributes' name
 */
public class dialogNumberPicker extends DialogFragment {

    private static final String ARG_NUMBER_PICKER_ID = "argNumberPickerID";
    private static final String ARG_STARTING_VALUE = "argStartingValue";

    private NumberPicker mNumberPicker;

    private AlertDialog mDialog;
    private int mNumberPickerId;
    private int mStartingNumberPickerValue;
    private int mSelectedValue;


    public dialogNumberPicker() {
        // Empty constructor required for DialogFragment
    }


    public static dialogNumberPicker newInstance(int numberPickerID, int startingValue) {
        MyLog.i("dialogNumberPicker", "newInstance");
        dialogNumberPicker fragment = new dialogNumberPicker();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMBER_PICKER_ID, numberPickerID);
        args.putInt(ARG_STARTING_VALUE, startingValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogNumberPicker", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_NUMBER_PICKER_ID)) {
            mNumberPickerId = args.getInt(ARG_NUMBER_PICKER_ID);
            mStartingNumberPickerValue = args.getInt(ARG_STARTING_VALUE);
        } else {
            String okDialogTitle = "Error Getting Number Picker";
            String msg = "Fragment arguments do not contain Number Picker ID = " + ARG_NUMBER_PICKER_ID + "!";
            EventBus.getDefault().post(new MyEvents.showOkDialog(okDialogTitle, msg));
            MyLog.e("dialogNumberPicker", "onCreate: " + msg);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogNumberPicker", "onActivityCreated");
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button saveButton = mDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(17);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        switch (mNumberPickerId) {
                            case MySettings.TEXT_SIZE_PICKER:
                                EventBus.getDefault().post(new MyEvents.setAttributesTextSize(mSelectedValue));
                                break;

                            case MySettings.HORIZONTAL_PADDING_PICKER:
                                EventBus.getDefault().post(new MyEvents.setAttributesHorizontalPadding(mSelectedValue));
                                break;

                            case MySettings.VERTICAL_PADDING_PICKER:
                                EventBus.getDefault().post(new MyEvents.setAttributesVerticalPadding(mSelectedValue));
                                break;
                        }
                        dismiss();
                    }
                });

                Button cancelButton = mDialog.getButton(Dialog.BUTTON_NEGATIVE);
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
        MyLog.i("dialogNumberPicker", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_picker, null, false);

        // find the dialog's views
        mNumberPicker = (NumberPicker) view.findViewById(R.id.npNumberPicker);
        mNumberPicker.setWrapSelectorWheel(true);
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mSelectedValue = newVal;
            }
        });

        String title = "";
        int minNumberPickerValue = 1;
        int maxNumberPickerValue = 10;
        switch (mNumberPickerId) {
            case MySettings.TEXT_SIZE_PICKER:
                title = "Select Text Size";
                maxNumberPickerValue = 25;
                break;

            case MySettings.HORIZONTAL_PADDING_PICKER:
                title = "Select Horizontal Padding";
                maxNumberPickerValue = 25;
                break;

            case MySettings.VERTICAL_PADDING_PICKER:
                title = "Select Vertical Padding";
                maxNumberPickerValue = 25;
                break;
        }

        mNumberPicker.setMinValue(minNumberPickerValue);
        mNumberPicker.setMaxValue(maxNumberPickerValue);
        mNumberPicker.setValue(mStartingNumberPickerValue);


        // build the dialog
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view)
                .setPositiveButton("Select", null)
                .setNegativeButton("Cancel", null)
                .create();

        return mDialog;
    }

}
