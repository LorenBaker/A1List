package com.lbconsulting.a1list.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
 * A dialog where the user edits the ListAttributes' text size, horizontal and vertical padding
 */
public class dialogNumberPicker extends DialogFragment {

    private static final String ARG_NUMBER_PICKER_ID = "argNumberPickerID";
    private static final String ARG_STARTING_VALUE = "argStartingValue";

    private NumberPicker npTens;
    private NumberPicker npOnes;

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
            String msg = "Fragment arguments do not contain Number Picker ID = " + ARG_NUMBER_PICKER_ID + "!";
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
                                EventBus.getDefault().post(new MyEvents.setLocalAttributesTextSize(mSelectedValue));
                                break;

                            case MySettings.HORIZONTAL_PADDING_PICKER:
                                EventBus.getDefault().post(new MyEvents.setLocalAttributesHorizontalPadding(mSelectedValue));
                                break;

                            case MySettings.VERTICAL_PADDING_PICKER:
                                EventBus.getDefault().post(new MyEvents.setLocalAttributesVerticalPadding(mSelectedValue));
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
        npTens = (NumberPicker) view.findViewById(R.id.npTens);
        npOnes = (NumberPicker) view.findViewById(R.id.npOnes);
        npTens.setWrapSelectorWheel(true);
        npOnes.setWrapSelectorWheel(true);

        npTens.setMinValue(0);
        npTens.setMaxValue(9);
        npOnes.setMinValue(0);
        npOnes.setMaxValue(9);

        npTens.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mSelectedValue = newVal * 10 + npOnes.getValue();
            }
        });
        npOnes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mSelectedValue = npTens.getValue() * 10 + newVal;
            }
        });

        setPickerValues(mStartingNumberPickerValue);

        String title = "";
        switch (mNumberPickerId) {
            case MySettings.TEXT_SIZE_PICKER:
                title = getActivity().getString(R.string.numberPickerDialog_select_text_size_title);
                break;

            case MySettings.HORIZONTAL_PADDING_PICKER:
                title = getActivity().getString(R.string.numberPickerDialog_select_horizontal_padding_title);
                break;

            case MySettings.VERTICAL_PADDING_PICKER:
                title = getActivity().getString(R.string.numberPickerDialog_select_vertical_padding_title);
                break;
        }

        // build the dialog
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.btnSelect_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mDialog;
    }

    private void setPickerValues(int value) {

        int tens = value / 10;
        int ones = value % 10;

        npTens.setValue(tens);
        npOnes.setValue(ones);

    }

}
