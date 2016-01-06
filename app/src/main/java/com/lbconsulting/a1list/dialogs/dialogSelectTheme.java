package com.lbconsulting.a1list.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.adapters.ListAttributesArrayAdapter;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListAttributes;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user edits the ListAttributes' text size, horizontal and vertical padding
 */
public class dialogSelectTheme extends DialogFragment {

    private AlertDialog mDialog;

    public dialogSelectTheme() {
        // Empty constructor required for DialogFragment
    }

    public static dialogSelectTheme newInstance() {
        MyLog.i("dialogSelectTheme", "newInstance");
        return new dialogSelectTheme();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogSelectTheme", "onCreate");
        EventBus.getDefault().register(this);
    }

    public void onEvent(MyEvents.dismissDialogSelectTheme event) {
        dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogSelectTheme", "onActivityCreated");
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

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


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogSelectTheme", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_themes, null, false);

        // find the dialog's views
        DynamicListView lvAttributes = (DynamicListView) view.findViewById(R.id.lvAttributes);

        lvAttributes.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.whiteSmoke));
        ListAttributesArrayAdapter mAttributesArrayAdapter = new ListAttributesArrayAdapter(getActivity(), lvAttributes, false);
        lvAttributes.setAdapter(mAttributesArrayAdapter);
        List<ListAttributes> attributesList = ListAttributes.getAllListAttributes();
        mAttributesArrayAdapter.setData(attributesList);
        mAttributesArrayAdapter.notifyDataSetChanged();

        // build the dialog
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialogSelectTheme_title)
                .setView(view)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mDialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("dialogSelectTheme", "onDestroy");
        EventBus.getDefault().unregister(this);
    }
}
