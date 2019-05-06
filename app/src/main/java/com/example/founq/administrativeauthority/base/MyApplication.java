package com.example.founq.administrativeauthority.base;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    static Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

}
