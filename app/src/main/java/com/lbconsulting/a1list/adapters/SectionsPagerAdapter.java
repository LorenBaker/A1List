package com.lbconsulting.a1list.adapters;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListTitle;
import com.lbconsulting.a1list.fragments.fragListItems;

import java.util.List;

/**
 * A FragmentPagerAdapter that displays fragListItems.
 */
//public class SectionsPagerAdapter extends FragmentPagerAdapter {
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private List<ListTitle> mAllLists = null;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        // get all ListTitles
        mAllLists = ListTitle.getAllListTitles(MySettings.isAlphabeticallySortNavigationMenu());
        if (mAllLists == null) {
            MyLog.e("SectionsPagerAdapter", "Unable to initialize SectionsPagerAdapter. mAllList is null!");
        }
    }

    @Override
    public fragListItems getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        fragListItems frag = null;
        ListTitle listTitle = mAllLists.get(position);
        if (listTitle != null) {
            String listTitleUuid = listTitle.getListTitleUuid();
            MyLog.i("SectionsPagerAdapter", "getItem: position = " + position);
            frag = fragListItems.newInstance(listTitleUuid);
        }
        return frag;
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
////        return super.getItemPosition(object);
//    }

    @Override
    public int getCount() {
        return mAllLists.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String listTitleName = null;
        ListTitle listTitle = mAllLists.get(position);
        if (listTitle != null) {
            listTitleName = listTitle.getName();
        }
        return listTitleName;
    }

    public ListTitle getListTitle(int position) {
        ListTitle listTitle = null;
        if (mAllLists.size() > 0 && position < mAllLists.size()) {
            listTitle = mAllLists.get(position);
        }
        return listTitle;
    }

    public int getPosition(String listTitleUuid) {
        int position = 0;
        boolean found = false;
        for (ListTitle listTitle : mAllLists) {
            if (listTitle.getListTitleUuid().equals(listTitleUuid)) {
                found = true;
                break;
            }
            position++;
        }

        if (!found) {
            position = 0;
        }
        return position;
    }

}

