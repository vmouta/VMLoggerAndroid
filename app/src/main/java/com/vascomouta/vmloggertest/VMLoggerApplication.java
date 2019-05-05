package com.vascomouta.vmloggertest;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.vascomouta.vmlogger.Log;


public class VMLoggerApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static VMLoggerApplication mInstance;
    public static VMLoggerApplication getInstance() {
        if(mInstance == null) {
            mInstance = new VMLoggerApplication();
        }
        return mInstance;
    }

    public Log applogger;

    public VMLoggerApplication() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        //AppLogger.configure(LogLevel.VERBOSE);
        AppLogger.configureFromAssets(getApplicationContext());
        applogger = AppLogger.getLogger(VMLoggerApplication.class.getCanonicalName());
        applogger.verbose();
        AppLogger.v("Verbose");
        applogger.debug();
        AppLogger.d("Debug");
        applogger.info();
        AppLogger.i("Info");
        applogger.warning();
        AppLogger.w("Warning");
        applogger.error();
        AppLogger.e("Error");
        applogger.severe();
        AppLogger.s("Severe");
    }

    /* From Application.ActivityLifecycleCallbacks */

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        applogger.debug();
    }

    @Override
    public void onActivityStarted(Activity activity) {
        AppLogger.onStart();
        applogger.debug();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        AppLogger.onResume();
        applogger.debug();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        AppLogger.onPause();
        applogger.debug();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        applogger.debug();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        applogger.debug();
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        AppLogger.onDestroy();
        applogger.debug();
    }
}
