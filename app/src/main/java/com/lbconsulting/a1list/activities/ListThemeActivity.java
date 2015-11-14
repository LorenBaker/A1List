package com.lbconsulting.a1list.activities;

import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lbconsulting.a1list.R;
import com.lbconsulting.a1list.adapters.ListItemsSampleArrayAdapter;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListTitle;
import com.lbconsulting.a1list.dialogs.dialogColorPicker;
import com.lbconsulting.a1list.dialogs.dialogEditListAttributesName;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ListThemeActivity extends AppCompatActivity implements View.OnClickListener {
    private ActionBar mActionBar;
    private ListTitle mListTitle;
    private ListAttributes mTempAttributes;
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
    private Button btnTextColor;
    private Button btnTextStyle;
    private CheckBox ckItemBackgroundTransparent;
    private Button btnHorizontalMargin;
    private Button btnVerticalMargin;
    private Button btnCancel;
    private Button btnNewAttributes;
    private Button btnSaveAttributes;


    private ListView lvSampleItems;
    private ListItemsSampleArrayAdapter mSampleArrayAdapter;
    private DynamicListView lvAttributes;


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

        // TODO: Figure out why app status bar background is white without this code ... change min api back to 16
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle args = getIntent().getExtras();
        String listTitleID = args.getString(MySettings.ARG_LIST_TITLE_ID);
        mListTitle = ListTitle.getListTitle(listTitleID, true);
        String title = "Error creating ListThemeActivity";
        if (mListTitle != null) {
            if (mActionBar != null) {
                mActionBar.setTitle(mListTitle.getName() + " Theme");
            }
            mOriginalAttributes = mListTitle.getAttributes();
            if (mOriginalAttributes != null) {
                mTempAttributes = ListAttributes.cloneListAttributes(mOriginalAttributes);
                mTempAttributes.pinInBackground();
            } else {
                String msg = "List \"" + mListTitle.getName() + "\" does not contain attributes!";
                EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));
            }
        } else {
            String msg = "Unable to find ListTitle with uuid = " + listTitleID;
            EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));
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
        btnTextColor = (Button) findViewById(R.id.btnTextColor);
        btnTextStyle = (Button) findViewById(R.id.btnTextStyle);
        ckItemBackgroundTransparent = (CheckBox) findViewById(R.id.ckItemBackgroundTransparent);
        btnHorizontalMargin = (Button) findViewById(R.id.btnHorizontalMargin);
        btnVerticalMargin = (Button) findViewById(R.id.btnVerticalMargin);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnNewAttributes = (Button) findViewById(R.id.btnNewAttributes);
        btnSaveAttributes = (Button) findViewById(R.id.btnSaveAttributes);

        lvSampleItems = (ListView) findViewById(R.id.lvSampleItems);
        lvAttributes = (DynamicListView) findViewById(R.id.lvAttributes);

        // set button OnClickListeners
        for (int i = 0; i < llContentListTheme.getChildCount(); i++) {
            View v = llContentListTheme.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setOnClickListener(this);
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
        String sampleName = "Sample Item ";
        for (int i = 0; i < 10; i++) {
            if (count < 10) {
                sampleList.add(sampleName + "0" + count);
            } else {
                sampleList.add(sampleName + count);
            }
            count++;
        }

        mSampleArrayAdapter = new ListItemsSampleArrayAdapter(this, lvSampleItems,
                mListTitle.getAttributes(), mListTitle.getName());
        lvSampleItems.setAdapter(mSampleArrayAdapter);
        mSampleArrayAdapter.setData(sampleList);

        upDateUI();
    }

    public void onEvent(MyEvents.setAttributesName event) {
        mTempAttributes.setName(event.getName());
        btnAttributesName.setText("Theme Name: " + event.getName());
    }

    public void onEvent(MyEvents.setAttributesStartColor event) {
        mTempAttributes.setStartColor(event.getColor());
        mSampleArrayAdapter.setAttributes(mTempAttributes);
        upDateUI();
    }

    public void onEvent(MyEvents.setAttributesEndColor event) {
        mTempAttributes.setEndColor(event.getColor());
        mSampleArrayAdapter.setAttributes(mTempAttributes);
        upDateUI();
    }

    public void onEvent(MyEvents.setAttributesTextColor event) {
        mTempAttributes.setTextColor(event.getColor());
        mSampleArrayAdapter.setAttributes(mTempAttributes);
        upDateUI();
    }

    private void upDateUI() {
        // set the background drawable
        Resources res = getResources();
        llListTheme.setBackground(mTempAttributes.getBackgroundDrawable());

        // set views' text color
        for (int i = 0; i < llContentListTheme.getChildCount(); i++) {
            View v = llContentListTheme.getChildAt(i);
            // Note: Switches and CheckBoxes are "Buttons"
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setTextColor(mTempAttributes.getTextColor());
            } else if (v instanceof RadioGroup) {
                rbAlphabetical.setTextColor(mTempAttributes.getTextColor());
                rbManual.setTextColor(mTempAttributes.getTextColor());
            }
        }

        for (int i = 0; i < llCancelNewSave.getChildCount(); i++) {
            View v = llCancelNewSave.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setTextColor(mTempAttributes.getTextColor());
            }
        }

        // show the attributes values in their respective Buttons

        btnAttributesName.setText("Theme Name: " + mTempAttributes.getName());
        ckIsDefaultAttributes.setChecked(mTempAttributes.isDefaultAttributes());

        if (mListTitle.sortListItemsAlphabetically()) {
            rbAlphabetical.setChecked(true);
        } else {
            rbManual.setChecked(true);
        }

        btnStartColor.setBackgroundColor(mTempAttributes.getStartColor());
        btnEndColor.setBackgroundColor(mTempAttributes.getEndColor());

        btnTextSize.setText(res.getString(R.string.btnTextSize_text, mTempAttributes.getTextSize()));
        if (mTempAttributes.isBold()) {
            btnTextStyle.setText(R.string.btnTextStyle_text_bold);
        } else {
            btnTextStyle.setText(R.string.btnTextStyle_text_normal);
        }

        ckItemBackgroundTransparent.setChecked(mTempAttributes.isBackgroundTransparent());

        btnHorizontalMargin.setText(res.getString(R.string.btnHorizontalMargin_text, mTempAttributes.getHorizontalPaddingDp()));
        btnVerticalMargin.setText(res.getString(R.string.btnVerticalMargin_text, mTempAttributes.getVerticalPaddingDp()));


        // set list views' background drawables
        lvSampleItems.setBackground(mTempAttributes.getBackgroundDrawable());
        mSampleArrayAdapter.notifyDataSetChanged();
        lvAttributes.setBackgroundColor(ContextCompat.getColor(this, R.color.whiteSmoke));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                dialogEditListAttributesName editListAttributesNameDialog
                        = dialogEditListAttributesName.newInstance(mTempAttributes.getLocalUuid());
                editListAttributesNameDialog.show(fm, "dialogEditListAttributesName");
                break;

            case R.id.btnStartColor:
                colorPickerDialog = dialogColorPicker.newInstance(MySettings.START_COLOR_PICKER,
                        mTempAttributes.getStartColor());
                colorPickerDialog.show(fm, "dialogStartColorPicker");
                break;

            case R.id.btnEndColor:
                colorPickerDialog = dialogColorPicker.newInstance(MySettings.END_COLOR_PICKER,
                        mTempAttributes.getEndColor());
                colorPickerDialog.show(fm, "dialogEndColorPicker");
                break;

            case R.id.btnTextSize:
                Toast.makeText(this, "btnTextSize clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnTextColor:
                colorPickerDialog = dialogColorPicker.newInstance(MySettings.TEXT_COLOR_PICKER,
                        mTempAttributes.getTextColor());
                colorPickerDialog.show(fm, "dialogTextColorPicker");
                break;

            case R.id.btnTextStyle:
                Toast.makeText(this, "btnTextStyle clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnHorizontalMargin:
                Toast.makeText(this, "btnHorizontalMargin clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnVerticalMargin:
                Toast.makeText(this, "btnVerticalMargin clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCancel:
//                Toast.makeText(this, "btnCancel clicked", Toast.LENGTH_SHORT).show();
                mTempAttributes.unpinInBackground();
                finish();
                break;

            case R.id.btnNewAttributes:
//                Toast.makeText(this, "btnNewAttributes clicked", Toast.LENGTH_SHORT).show();
                createNewAttributes();
                finish();
                break;

            case R.id.btnSaveAttributes:
                saveAttributes();
                finish();
                break;

        }
    }

    private void createNewAttributes() {
        String existingAttributesID = ListAttributes.getExistingAttributesID(mTempAttributes.getName());
        if (existingAttributesID != null) {
            String title = "Unable To Create New Theme";
            String msg = "Theme name \"" + mTempAttributes.getName() + "\" exists. Please rename the Theme.";
            EventBus.getDefault().post(new MyEvents.showOkDialog(title, msg));
        } else {
            // We have a unique name ... so save the new attributes
            mTempAttributes.setAttributesDirty(true);
            mListTitle.setAttributes(mTempAttributes);
        }
    }

    private void saveAttributes() {
        mOriginalAttributes = ListAttributes.copyListAttributes(mTempAttributes, mOriginalAttributes);
        mOriginalAttributes.setAttributesDirty(true);
        mTempAttributes.unpinInBackground();
    }
}
