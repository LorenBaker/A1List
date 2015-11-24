package com.lbconsulting.a1list.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.color_picker.GradientView;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user edits the ListAttributes' name
 */
public class dialogColorPicker extends DialogFragment {

    private static final String ARG_COLOR_PICKER_ID = "argColorPickerID";
    private static final String ARG_STARTING_COLOR = "argStartingColor";

    private TextView mTextView;
    private Drawable mIcon;

    private AlertDialog mDialog;
    private int mColorPickerId;
    private int mStartingColor;
    private int mSelectedColor;


    public dialogColorPicker() {
        // Empty constructor required for DialogFragment
    }


    public static dialogColorPicker newInstance(int colorPickerID, int startingColor) {
        MyLog.i("dialogColorPicker", "newInstance");
        dialogColorPicker fragment = new dialogColorPicker();
        Bundle args = new Bundle();
        args.putInt(ARG_COLOR_PICKER_ID, colorPickerID);
        args.putInt(ARG_STARTING_COLOR, startingColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogColorPicker", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_COLOR_PICKER_ID)) {
            mColorPickerId = args.getInt(ARG_COLOR_PICKER_ID);
            mStartingColor = args.getInt(ARG_STARTING_COLOR);
        } else {
            String msg = "Fragment arguments do not contain Color Picker ID = " + ARG_COLOR_PICKER_ID + "!";
            MyLog.e("dialogColorPicker", "onCreate: " + msg);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogColorPicker", "onActivityCreated");
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button saveButton = mDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(17);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        switch (mColorPickerId) {
                            case MySettings.TEXT_COLOR_PICKER:
                                EventBus.getDefault().post(new MyEvents.setAttributesTextColor(mSelectedColor));
                                break;

                            case MySettings.START_COLOR_PICKER:
                                EventBus.getDefault().post(new MyEvents.setAttributesStartColor(mSelectedColor));
                                break;

                            case MySettings.END_COLOR_PICKER:
                                EventBus.getDefault().post(new MyEvents.setAttributesEndColor(mSelectedColor));
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
        MyLog.i("dialogColorPicker", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_color_picker, null, false);

        // find the dialog's views
        mIcon = ContextCompat.getDrawable(getActivity(), R.drawable.andy_the_robot);
        mTextView = (TextView) view.findViewById(R.id.color);
        mTextView.setCompoundDrawablesWithIntrinsicBounds(mIcon, null, null, null);
        GradientView mTop = (GradientView) view.findViewById(R.id.top);
        GradientView mBottom = (GradientView) view.findViewById(R.id.bottom);
        mTop.setBrightnessGradientView(mBottom);
        mBottom.setOnColorChangedListener(new GradientView.OnColorChangedListener() {
            @Override
            public void onColorChanged(GradientView view, int color) {
                mSelectedColor = color;
                mTextView.setTextColor(color);
                mTextView.setText(String.format(getActivity().getString(R.string.onColorChanged),
                        Integer.toHexString(color)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mIcon.setTint(color);
                }
            }
        });

        mTop.setColor(mStartingColor);

        String title = "";
        switch (mColorPickerId) {
            case MySettings.TEXT_COLOR_PICKER:
                title = getActivity().getString(R.string.colorPicker_textColor_title);
                break;

            case MySettings.START_COLOR_PICKER:
                title = getActivity().getString(R.string.colorPicker_startColor_title);
                break;

            case MySettings.END_COLOR_PICKER:
                title = getActivity().getString(R.string.colorPicker_endColor_title);
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

}
