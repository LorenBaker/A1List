package com.lbconsulting.a1list.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.adapters.ListItemsSampleArrayAdapter;
import com.lbconsulting.a1list.classes.CommonMethods;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.lbconsulting.a1list.database.LocalListAttributes;
import com.lbconsulting.a1list.dialogs.dialogColorPicker;
import com.lbconsulting.a1list.dialogs.dialogEditLocalListAttributesName;
import com.lbconsulting.a1list.dialogs.dialogNumberPicker;
import com.lbconsulting.a1list.dialogs.dialogSelectTheme;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ListThemeActivity extends AppCompatActivity implements View.OnClickListener {
    private static LocalListAttributes mLocalAttributes;
    private ListTitle mListTitle;
    private ListAttributes mOriginalAttributes;
    private LinearLayout llListTheme;
    private LinearLayout llContentListTheme;
    private LinearLayout llCancelNewSave;
    private Button btnAttributesName;
    private CheckBox ckIsDefaultAttributes;
    private RadioButton rbAlphabetical;
    private RadioButton rbManual;
    private Button btnStartColor;
    private Button btnEndColor;
    private Button btnTextSize;
    private Button btnTextStyle;
    private CheckBox ckItemBackgroundTransparent;
    private Button btnHorizontalMargin;
    private Button btnVerticalMargin;
    private ListView lvSampleItems;
    private ListItemsSampleArrayAdapter mSampleArrayAdapter;

    public static LocalListAttributes getLocalAttributes() {
        return mLocalAttributes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("ListThemeActivity", "onCreate");

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_list_theme);
        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle args = getIntent().getExtras();
        String listTitleID = args.getString(MySettings.ARG_LIST_TITLE_ID);
        mListTitle = ListTitle.getListTitle(listTitleID);
        if (mListTitle != null) {
            if (mActionBar != null) {
                String actionBarTitle =
                        String.format(getString(R.string.onCreate_listThemeActivity_actionBar_title),
                                mListTitle.getName());
                mActionBar.setTitle(actionBarTitle);
            }
            mOriginalAttributes = mListTitle.getAttributes();
            if (mOriginalAttributes != null) {
                mLocalAttributes = ListAttributes.createLocalListAttributes(mOriginalAttributes);
            } else {
                String msg = String.format(getString(R.string.onCreate_creating_listThemeActivity_error_message), mListTitle.getName());
                MyLog.e("ListThemeActivity", "onCreate: " + msg);
            }
        } else {
            String msg = "Unable to find ListTitle with uuid = " + listTitleID;
            MyLog.e("ListThemeActivity", "onCreate: " + msg);
        }
        // get activity views
        llListTheme = (LinearLayout) findViewById(R.id.llListTheme);
        llContentListTheme = (LinearLayout) findViewById(R.id.llContentListTheme);
        llCancelNewSave = (LinearLayout) findViewById(R.id.llCancelNewSave);

        btnAttributesName = (Button) findViewById(R.id.btnAttributesName);
        ckIsDefaultAttributes = (CheckBox) findViewById(R.id.ckIsDefaultAttributes);
        rbAlphabetical = (RadioButton) findViewById(R.id.rbAlphabetical);
        rbManual = (RadioButton) findViewById(R.id.rbManual);
        btnStartColor = (Button) findViewById(R.id.btnStartColor);
        btnEndColor = (Button) findViewById(R.id.btnEndColor);
        btnTextSize = (Button) findViewById(R.id.btnTextSize);
        btnTextStyle = (Button) findViewById(R.id.btnTextStyle);
        ckItemBackgroundTransparent = (CheckBox) findViewById(R.id.ckItemBackgroundTransparent);
        btnHorizontalMargin = (Button) findViewById(R.id.btnHorizontalMargin);
        btnVerticalMargin = (Button) findViewById(R.id.btnVerticalMargin);

        lvSampleItems = (ListView) findViewById(R.id.lvSampleItems);

        // set button OnClickListeners
        for (int i = 0; i < llContentListTheme.getChildCount(); i++) {
            View v = llContentListTheme.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setOnClickListener(this);
            } else if (v instanceof RadioGroup) {
                rbAlphabetical.setOnClickListener(this);
                rbManual.setOnClickListener(this);
            }
        }

        for (int i = 0; i < llCancelNewSave.getChildCount(); i++) {
            View v = llCancelNewSave.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setOnClickListener(this);
            }
        }

        int count = 1;
        ArrayList<String> sampleList = new ArrayList<>();
        String sampleName = getString(R.string.onCreate_listThemeActivity_sampleitemName);
        for (int i = 0; i < 5; i++) {
            if (count < 10) {
                sampleList.add(sampleName + "0" + count);
            } else {
                sampleList.add(sampleName + count);
            }
            count++;
        }

        mSampleArrayAdapter = new ListItemsSampleArrayAdapter(this, lvSampleItems,
                mLocalAttributes, mListTitle.getName());
        lvSampleItems.setAdapter(mSampleArrayAdapter);
        mSampleArrayAdapter.setData(sampleList);

        updateUI();
    }

    //region OnEvent
    public void onEvent(MyEvents.setAttributesName event) {
        mLocalAttributes.setName(event.getName());
        btnAttributesName.setText(String.format(getString(R.string.btnAttributesName_text), event.getName()));
    }

    public void onEvent(MyEvents.setAttributesStartColor event) {
        mLocalAttributes.setStartColor(event.getColor());
        mSampleArrayAdapter.setAttributes(mLocalAttributes);
        updateUI();
    }

    public void onEvent(MyEvents.setAttributesEndColor event) {
        mLocalAttributes.setEndColor(event.getColor());
        mSampleArrayAdapter.setAttributes(mLocalAttributes);
        updateUI();
    }

    public void onEvent(MyEvents.setAttributesTextColor event) {
        mLocalAttributes.setTextColor(event.getColor());
        mSampleArrayAdapter.setAttributes(mLocalAttributes);
        updateUI();
    }

    public void onEvent(MyEvents.setAttributesTextSize event) {
        mLocalAttributes.setTextSize(event.getTextSize());
        mSampleArrayAdapter.setAttributes(mLocalAttributes);
        updateUI();
    }

    public void onEvent(MyEvents.setAttributesHorizontalPadding event) {
        mLocalAttributes.setHorizontalPaddingInDp(event.getHorizontalPadding());
        mSampleArrayAdapter.setAttributes(mLocalAttributes);
        updateUI();
    }

    public void onEvent(MyEvents.setAttributesVerticalPadding event) {
        mLocalAttributes.setVerticalPaddingInDp(event.getVerticalPadding());
        mSampleArrayAdapter.setAttributes(mLocalAttributes);
        updateUI();
    }

    public void onEvent(MyEvents.replaceAttributes event) {
        ListAttributes attributes = ListAttributes.getAttributes(event.getAttributeUuid());
        mLocalAttributes = ListAttributes.createLocalListAttributes(attributes);
        mListTitle.setAttributes(attributes);
        ListItem.setNewAttributes(mListTitle, attributes);
        mSampleArrayAdapter.setAttributes(mLocalAttributes);
        updateUI();
    }

    //endregion

    private void updateUI() {
        // set the background drawable
        Resources res = getResources();
        llListTheme.setBackground(mLocalAttributes.getBackgroundDrawable());

        // set views' text color
        for (int i = 0; i < llContentListTheme.getChildCount(); i++) {
            View v = llContentListTheme.getChildAt(i);
            // Note: Switches and CheckBoxes are "Buttons"
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setTextColor(mLocalAttributes.getTextColor());
            } else if (v instanceof RadioGroup) {
                rbAlphabetical.setTextColor(mLocalAttributes.getTextColor());
                rbManual.setTextColor(mLocalAttributes.getTextColor());
            }
        }

        for (int i = 0; i < llCancelNewSave.getChildCount(); i++) {
            View v = llCancelNewSave.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setTextColor(mLocalAttributes.getTextColor());
            }
        }

        // show the attributes values in their respective Buttons

        btnAttributesName.setText(String.format(getString(R.string.btnAttributesName_text),
                mLocalAttributes.getName()));
        ckIsDefaultAttributes.setChecked(mLocalAttributes.isDefaultAttributes());

        if (mListTitle.sortListItemsAlphabetically()) {
            rbAlphabetical.setChecked(true);
        } else {
            rbManual.setChecked(true);
        }

        btnStartColor.setBackgroundColor(mLocalAttributes.getStartColor());
        btnEndColor.setBackgroundColor(mLocalAttributes.getEndColor());

        btnTextSize.setText(res.getString(R.string.btnTextSize_text, mLocalAttributes.getTextSize()));
        if (mLocalAttributes.isBold()) {
            btnTextStyle.setText(R.string.btnTextStyle_text_bold);
        } else {
            btnTextStyle.setText(R.string.btnTextStyle_text_normal);
        }

        ckItemBackgroundTransparent.setChecked(mLocalAttributes.isTransparent());

        btnHorizontalMargin.setText(res.getString(R.string.btnHorizontalMargin_text,
                mLocalAttributes.getHorizontalPaddingInDp()));
        btnVerticalMargin.setText(res.getString(R.string.btnVerticalMargin_text,
                mLocalAttributes.getVerticalPaddingInDp()));


        // set list views' background drawables
        lvSampleItems.setBackground(mLocalAttributes.getBackgroundDrawable());
        mSampleArrayAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("ListThemeActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("ListThemeActivity", "onPause");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyLog.i("ListThemeActivity", "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_list_theme, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_show_themes) {
            FragmentManager fm = getFragmentManager();
            dialogSelectTheme dialog = dialogSelectTheme.newInstance();
            dialog.show(fm, "dialogSelectTheme");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("ListThemeActivity", "onDestroy");
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onClick(View v) {

        FragmentManager fm = getFragmentManager();
        dialogColorPicker colorPickerDialog;
        switch (v.getId()) {

            case R.id.btnAttributesName:
                dialogEditLocalListAttributesName editListAttributesNameDialog
                        = dialogEditLocalListAttributesName.newInstance();
                editListAttributesNameDialog.show(fm, "dialogEditLocalListAttributesName");
                break;

            case R.id.ckIsDefaultAttributes:
                mLocalAttributes.setIsDefaultAttributes(ckIsDefaultAttributes.isChecked());
                break;

            case R.id.rbAlphabetical:
            case R.id.rbManual:
                mListTitle.setSortListItemsAlphabetically(rbAlphabetical.isChecked());
                break;

            case R.id.btnStartColor:
                colorPickerDialog = dialogColorPicker.newInstance(MySettings.START_COLOR_PICKER,
                        mLocalAttributes.getStartColor());
                colorPickerDialog.show(fm, "dialogStartColorPicker");
                break;

            case R.id.btnEndColor:
                colorPickerDialog = dialogColorPicker.newInstance(MySettings.END_COLOR_PICKER,
                        mLocalAttributes.getEndColor());
                colorPickerDialog.show(fm, "dialogEndColorPicker");
                break;

            case R.id.btnTextSize:
                dialogNumberPicker numberPickerDialog = dialogNumberPicker.newInstance(MySettings.TEXT_SIZE_PICKER,
                        Math.round(mLocalAttributes.getTextSize()));
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;

            case R.id.btnTextColor:
                colorPickerDialog = dialogColorPicker.newInstance(MySettings.TEXT_COLOR_PICKER,
                        mLocalAttributes.getTextColor());
                colorPickerDialog.show(fm, "dialogTextColorPicker");
                break;

            case R.id.btnTextStyle:
                mLocalAttributes.toggleTextStyle();
                mSampleArrayAdapter.setAttributes(mLocalAttributes);
                updateUI();
                break;

            case R.id.ckItemBackgroundTransparent:
                mLocalAttributes.setIsTransparent(ckItemBackgroundTransparent.isChecked());
                mSampleArrayAdapter.setAttributes(mLocalAttributes);
                updateUI();
                break;

            case R.id.btnHorizontalMargin:
                numberPickerDialog = dialogNumberPicker.newInstance(MySettings.HORIZONTAL_PADDING_PICKER,
                        mLocalAttributes.getHorizontalPaddingInDp());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;

            case R.id.btnVerticalMargin:
                numberPickerDialog = dialogNumberPicker.newInstance(MySettings.VERTICAL_PADDING_PICKER,
                        mLocalAttributes.getVerticalPaddingInDp());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;

            case R.id.btnCancel:
                mListTitle.setAttributes(mOriginalAttributes);
                ListItem.setNewAttributes(mListTitle, mOriginalAttributes);
                finish();
                break;

            case R.id.btnNewAttributes:
                if (createNewAttributes()) {
                    finish();
                }
                break;

            case R.id.btnSaveAttributes:
                saveAttributes();
                finish();
                break;
        }
    }

    private boolean createNewAttributes() {
        boolean attributesCreated = false;

        if (ListAttributes.isValidAttributesName(mLocalAttributes.getName())) {
            // We have a unique name ... so save the new attributes
            try {
                if (ckIsDefaultAttributes.isChecked()) {
                    // These "new" attributes are now the default attributes ... so
                    // make sure that no other attributes are set as default.
                    ListAttributes.clearAllDefaultAttributes();
                }
                ListAttributes newAttributes = new ListAttributes();
                newAttributes.setLocalUuid();
                newAttributes.setAttributesID();
                newAttributes.setAuthor(ParseUser.getCurrentUser());
                newAttributes = ListAttributes.copyLocalListAttributes(mLocalAttributes, newAttributes);
                newAttributes.pin();
                mListTitle.setAttributes(newAttributes);
                ListItem.setNewAttributes(mListTitle, newAttributes);

                attributesCreated = true;
            } catch (ParseException e) {
                MyLog.e("ListThemeActivity", "createNewAttributes: ParseException: " + e.getMessage());
            }
        } else {
            String title = getString(R.string.failedToCreateNewTheme_title);
            String msg = String.format(getString(R.string.failedToCreateNewTheme_message), mLocalAttributes.getName());
            CommonMethods.showOkDialog(this, title, msg);
        }
        return attributesCreated;
    }

    private void saveAttributes() {
        if (ckIsDefaultAttributes.isChecked()) {
            // These "existing" attributes are now the default attributes ... so
            // make sure that no other attributes are set as default.
            ListAttributes.clearAllDefaultAttributes();
        }
        ListAttributes attributes = mListTitle.getAttributes();
        attributes = ListAttributes.copyLocalListAttributes(mLocalAttributes, attributes);
        attributes.setAttributesDirty(true);
    }
}

