package com.lbconsulting.a1list.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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

    private ListAttributesArrayAdapter mAttributesArrayAdapter;

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


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogSelectTheme", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_themes, null, false);

        // find the dialog's views
        DynamicListView lvAttributes = (DynamicListView) view.findViewById(R.id.lvAttributes);

        lvAttributes.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.whiteSmoke));
        mAttributesArrayAdapter = new ListAttributesArrayAdapter(getActivity(), lvAttributes);
        lvAttributes.setAdapter(mAttributesArrayAdapter);
        List<ListAttributes> attributesList = ListAttributes.getAllListAttributes();
        mAttributesArrayAdapter.setData(attributesList);
        mAttributesArrayAdapter.notifyDataSetChanged();

        lvAttributes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAttributes selectedAttributes = mAttributesArrayAdapter.getItem(position);
                EventBus.getDefault().post(new MyEvents.replaceAttributes(selectedAttributes.getLocalUuid()));
                dismiss();
            }
        });

        // build the dialog
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialogSelectTheme_title)
                .setView(view)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mDialog;
    }

}
