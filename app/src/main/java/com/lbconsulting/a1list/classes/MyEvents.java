package com.lbconsulting.a1list.classes;

/**
 * EventBus events.
 */
public class MyEvents {
    public static class updateUI {
        public updateUI() {
        }
    }

    public static class updateListUI {
        public updateListUI() {
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

    public static class setActionBarTitle {
        private final String mTitle;

        public setActionBarTitle(String title) {
            this.mTitle = title;
        }

        public String getTitle() {
            return mTitle;
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

    public static class setFragmentContainerBackground {
        private final int mStartColor;
        private final int mEndColor;

        public setFragmentContainerBackground(int startColor, int endColor) {
            mStartColor = startColor;
            mEndColor = endColor;
        }

        public int getStartColor() {
            return mStartColor;
        }

        public int getEndColor() {
            return mEndColor;
        }
    }

    public static class setAttributesName {
        private final String mName;

        public setAttributesName(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }

    public static class setAttributesStartColor {
        private final int mColor;

        public setAttributesStartColor(int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }
    }

    public static class setAttributesEndColor {
        private final int mColor;

        public setAttributesEndColor(int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }
    }

    public static class setAttributesTextColor {
        private final int mColor;

        public setAttributesTextColor(int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }
    }

    public static class setAttributesTextSize {
        private final int mSelectedTextSize;

        public setAttributesTextSize(int selectedTextSize) {
            mSelectedTextSize = selectedTextSize;
        }

        public float getTextSize() {
            return (float) mSelectedTextSize;
        }
    }

    public static class setAttributesHorizontalPadding {
        private final int mHorizontalPadding;

        public setAttributesHorizontalPadding(int selectedHorizontalPadding) {
            mHorizontalPadding = selectedHorizontalPadding;
        }

        public int getHorizontalPadding() {
            return mHorizontalPadding;
        }
    }

    public static class setAttributesVerticalPadding {
        private final int mVerticalPadding;

        public setAttributesVerticalPadding(int selectedVerticalPadding) {
            mVerticalPadding = selectedVerticalPadding;
        }

        public int getVerticalPadding() {
            return mVerticalPadding;
        }
    }

    public static class replaceAttributes {
        private final String mAttributeUuid;

        public replaceAttributes(String attributeUuid) {
            mAttributeUuid = attributeUuid;
        }

        public String getAttributeUuid() {
            return mAttributeUuid;
        }
    }
}


