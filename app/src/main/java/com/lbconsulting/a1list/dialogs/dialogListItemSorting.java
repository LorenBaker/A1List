package com.lbconsulting.a1list.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.parse.ParseException;
import com.parse.ParseUser;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user creates a new ListItem
 */
public class dialogListItemSorting extends DialogFragment {

    private static final String ARG_LIST_UUID = "argListUuid";

    private RadioButton rbAlphabetical;
    private RadioButton rbManual;

    private ListTitle mListTitle;
    private AlertDialog mListItemSortingDialog;

    public dialogListItemSorting() {
        // Empty constructor required for DialogFragment
    }


    public static dialogListItemSorting newInstance(String listUuid) {
        MyLog.i("dialogListItemSorting", "newInstance");
        dialogListItemSorting fragment = new dialogListItemSorting();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_UUID, listUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogListItemSorting", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_LIST_UUID)) {
            String listUuid = args.getString((ARG_LIST_UUID));
            mListTitle = ListTitle.getListTitle(listUuid, true);
            if (mListTitle == null) {
                String okDialogTitle = "List Item Sorting Error";
                String msg = "ListTitle with uuid = \"" + listUuid + "\" does not exist!";
                EventBus.getDefault().post(new MyEvents.showOkDialog(okDialogTitle, msg));
                MyLog.e("dialogListItemSorting", "onCreate: " + msg);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogListItemSorting", "onActivityCreated");

        mListItemSortingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button okButton = mListItemSortingDialog.getButton(Dialog.BUTTON_POSITIVE);
                okButton.setTextSize(17);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if(rbAlphabetical.isChecked()){
                            mListTitle.setSortListItemsAlphabetically(true);
                        }else{
                            mListTitle.setSortListItemsAlphabetically(false);
                        }
                        mListTitle.setListTitleDirty(true);
                        EventBus.getDefault().post(new MyEvents.updateListUI());
                        dismiss();
                    }
                });

                Button cancelButton = mListItemSortingDialog.getButton(Dialog.BUTTON_NEGATIVE);
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
        MyLog.i("dialogListItemSorting", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_list_sorting, null, false);

        // find the dialog's views
        rbAlphabetical = (RadioButton) view.findViewById(R.id.rbAlphabetical);
        rbManual = (RadioButton) view.findViewById(R.id.rbManual);

        if(mListTitle.sortListItemsAlphabetically()){
            rbAlphabetical.setChecked(true);
        }else{
            rbManual.setChecked(true);
        }

        // build the dialog
        mListItemSortingDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mListTitle.getName() + " Sorting")
                .setView(view)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create();

        return mListItemSortingDialog;
    }

}
