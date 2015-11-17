package com.lbconsulting.a1list.activities;

import android.app.Application;
import android.content.Context;


import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.lbconsulting.a1list.database.ListAttributes;
import com.lbconsulting.a1list.database.ListItem;
import com.lbconsulting.a1list.database.ListTitle;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;


public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i("App", "onCreate");

        mContext = this;

        // TODO: Enable crash reporting and other analytics
        // Initialize Crash Reporting.
        //ParseCrashReporting.enable(this);

        // Add your initialization code here
        ParseObject.registerSubclass(ListAttributes.class);
        ParseObject.registerSubclass(ListItem.class);
        ParseObject.registerSubclass(ListTitle.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "jPKE0gmjhsw5MKmEG94bPEd2bqmGv4ZsFuNEip1O", "plC4MhFr2rAgxyMsHeWppgnl4H0eRCwKaY05gnqE");

        //user's data is only accessible by the user unless explicit permission is given
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
        MyLog.i("App", "onCreate: Parse initialized");

        MySettings.setContext(mContext);
    }

    public static Context getContext(){
        return mContext;
    }

}
