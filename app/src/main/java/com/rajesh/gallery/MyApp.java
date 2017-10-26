package com.rajesh.gallery;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * desc
 *
 * @author zhufeng on 2017/1/10.
 */
public class MyApp extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        Fresco.initialize(this);
    }

    public static Context getAppContext() {
        return mContext;
    }
}
