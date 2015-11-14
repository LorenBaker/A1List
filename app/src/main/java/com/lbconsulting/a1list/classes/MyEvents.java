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


    public static class startA1List {
        private boolean mRefreshDataFromTheCloud;

        public startA1List(boolean refreshDataFromTheCloud) {
            this.mRefreshDataFromTheCloud = refreshDataFromTheCloud;
        }

        public boolean getRefreshDataFromTheCloud() {
            return mRefreshDataFromTheCloud;
        }
    }

    public static class setActionBarTitle {
        private String mTitle;

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
        private int mStartColor;
        private int mEndColor;

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
        private String mName;

        public setAttributesName(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }

    public static class setAttributesStartColor {
        private int mColor;

        public setAttributesStartColor(int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }
    }

    public static class setAttributesEndColor {
        private int mColor;

        public setAttributesEndColor(int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }
    }

    public static class setAttributesTextColor {
        private int mColor;

        public setAttributesTextColor(int color) {
            mColor = color;
        }

        public int getColor() {
            return mColor;
        }
    }
}


