package com.lbconsulting.a1list.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.adapters.FavoritesArrayAdapter;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user selects favorite items to add to the list
 */
public class dialogSelectFavorites extends DialogFragment {

    private static final String ARG_LIST_TITLE_UUID = "listTitleUuid";
    private ListTitle mListTitle;
    private FavoritesArrayAdapter mFavoritesArrayAdapter;

    private AlertDialog mDialog;

    public dialogSelectFavorites() {
        // Empty constructor required for DialogFragment
    }

    public static dialogSelectFavorites newInstance(String listTitleUuid) {
        MyLog.i("dialogSelectFavorites", "newInstance");
        dialogSelectFavorites dialogFragment = new dialogSelectFavorites();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_TITLE_UUID, listTitleUuid);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args.containsKey(ARG_LIST_TITLE_UUID)) {
            String listTitleUuid = args.getString(ARG_LIST_TITLE_UUID);
            mListTitle = ListTitle.getListTitle(listTitleUuid);
        } else {
            MyLog.e("dialogSelectFavorites", "onCreate: Failed to find ListTitle!");
        }
        if (mListTitle != null) {
            MyLog.i("dialogSelectFavorites", "onCreate: Favorites for: " + mListTitle.getName());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogSelectFavorites", "onActivityCreated");
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button applyButton = mDialog.getButton(Dialog.BUTTON_POSITIVE);
                applyButton.setTextSize(17);
                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Apply
                        mFavoritesArrayAdapter.selectCheckedItems();
                        EventBus.getDefault().post(new MyEvents.updateListUI());
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
        MyLog.i("dialogSelectFavorites", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_favorites, null, false);

        // find the dialog's views
        ListView lvFavorites = (ListView) view.findViewById(R.id.lvFavorites);

        lvFavorites.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.whiteSmoke));
        List<ListItem> favorites = ListItem.getFavorites(mListTitle);
        mFavoritesArrayAdapter = new FavoritesArrayAdapter(getActivity(), favorites);
        lvFavorites.setAdapter(mFavoritesArrayAdapter);
//        lvFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CheckBox checkBox = (CheckBox) view;
//                ListItem clickedItem = (ListItem) view.getTag();
//                if(clickedItem!=null){
//                    clickedItem.setChecked(checkBox.isChecked());
//                }
//            }
//        });


//        lvAttributes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ListAttributes selectedAttributes = mFavoritesArrayAdapter.getItem(position);
//                EventBus.getDefault().post(new MyEvents.replaceAttributes(selectedAttributes.getLocalUuid()));
//                dismiss();
//            }
//        });

        // build the dialog
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Select Items For \"" + mListTitle.getName() + "\"")
                .setView(view)
                .setPositiveButton(R.string.btnApply_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mDialog;
    }

}
