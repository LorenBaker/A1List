package com.lbconsulting.a1list.classes;

/**
 * EventBus events.
 */
public class MyEvents {
//    public static class updateUI {
//        public updateUI() {
//        }
//    }

    public static class showListItem{
        String mListItemUuid;
        public showListItem(String listItemUuid){
            mListItemUuid=listItemUuid;
        }

        public String getListItemUuid(){
            return mListItemUuid;
        }
    }

    public static class showProgressBar {
        public showProgressBar() {

        }
    }

    public static class hideProgressBar {
        public hideProgressBar() {

        }
    }

    public static class refreshSectionsPagerAdapter {
        public refreshSectionsPagerAdapter() {

        }
    }

    public static class showEditListTitleDialog {
        String mListTitleUuid;

        public showEditListTitleDialog(String listTitleUuid) {
            mListTitleUuid = listTitleUuid;
        }

        public String getListTitleUuid() {
            return mListTitleUuid;
        }
    }

    public static class showEditListItemDialog {
        String mListItemUuid;

        public showEditListItemDialog(String listItemUuid) {
            mListItemUuid = listItemUuid;
        }

        public String getListItemUuid() {
            return mListItemUuid;
        }
    }

    public static class showEditAttributesNameDialog {
        String mAttributesUuid;

        public showEditAttributesNameDialog(String attributesUuid) {
            mAttributesUuid = attributesUuid;
        }

        public String getAttributesUuid() {
            return mAttributesUuid;
        }
    }


    public static class updateListUIAsync {
        String mListTitleUuid;

        public updateListUIAsync(String listTitleUuid) {
            mListTitleUuid = listTitleUuid;
        }

        public String getListTitleUuid() {
            return mListTitleUuid;
        }

    }

    public static class updateListUI {
        String mListTitleUuid;

        public updateListUI(String listTitleUuid) {
            mListTitleUuid = listTitleUuid;
        }

        public String getListTitleUuid() {
            return mListTitleUuid;
        }

    }


    public static class updateListTitleUI {
        public updateListTitleUI() {
        }
    }

    public static class startA1List {
        private final boolean mRefreshDataFromTheCloud;

        public startA1List(boolean refreshDataFromTheCloud) {
            this.mRefreshDataFromTheCloud = refreshDataFromTheCloud;
        }

        public boolean getRefreshDataFromTheCloud() {
            return mRefreshDataFromTheCloud;
        }
    }

    public static class dismissDialogSelectTheme {
        public dismissDialogSelectTheme() {

        }
    }

    public static class showOkDialog {
        private final String mTitle;
        private final String mMessage;

        public showOkDialog(String title, String message) {
            mTitle = title;
            mMessage = message;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class setLocalAttributesName {
        private final String mName;

        public setLocalAttributesName(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }

    public static class setLocalAttributesStartColor {
        private final int mColor;

        public setLocalAttributesStartColor(int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }
    }

    public static class setLocalAttributesEndColor {
        private final int mColor;

        public setLocalAttributesEndColor(int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }
    }

    public static class setLocalAttributesTextColor {
        private final int mColor;

        public setLocalAttributesTextColor(int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }
    }

    public static class setLocalAttributesTextSize {
        private final int mSelectedTextSize;

        public setLocalAttributesTextSize(int selectedTextSize) {
            mSelectedTextSize = selectedTextSize;
        }

        public float getTextSize() {
            return (float) mSelectedTextSize;
        }
    }

    public static class setLocalAttributesHorizontalPadding {
        private final int mHorizontalPadding;

        public setLocalAttributesHorizontalPadding(int selectedHorizontalPadding) {
            mHorizontalPadding = selectedHorizontalPadding;
        }

        public int getHorizontalPadding() {
            return mHorizontalPadding;
        }
    }

    public static class setLocalAttributesVerticalPadding {
        private final int mVerticalPadding;

        public setLocalAttributesVerticalPadding(int selectedVerticalPadding) {
            mVerticalPadding = selectedVerticalPadding;
        }

        public int getVerticalPadding() {
            return mVerticalPadding;
        }
    }

    public static class setLocalAttributes {
        private final String mAttributeUuid;

        public setLocalAttributes(String attributeUuid) {
            mAttributeUuid = attributeUuid;
        }

        public String getAttributeUuid() {
            return mAttributeUuid;
        }
    }
}


