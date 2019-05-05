package com.vascomouta.vmloggertest;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import android.arch.lifecycle.LifecycleObserver

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogConfiguration;
import com.vascomouta.vmlogger.LogLevel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppLogger extends Log implements Application.ActivityLifecycleCallbacks {

    private static String AppLoggerUI = "TrackUI";
    private static String ForegroundDuration = "ForegroundDuration";
    private static String BackgroundDuration = "BackgroundDuration";
    private static String Terminated = "Terminated";

    private static Date startDate = new Date(System.currentTimeMillis());
    private static Date eventDate = startDate;

    public AppLogger() {
        super();
    }

    protected AppLogger(String identifier, LogLevel assignedLevel, LogConfiguration parent, ArrayList<LogAppender> appenders, boolean synchronousMode, boolean additivity) {
        super(identifier, assignedLevel, parent, appenders, synchronousMode, additivity);
    }

    public AppLogger(String identifier, LogLevel assignedLevel, LogConfiguration parent, ArrayList<LogAppender> appenders, boolean synchronousMode) {
        super(identifier, assignedLevel, parent, appenders, synchronousMode, true);
    }

    public AppLogger(String identifier, LogConfiguration parent, Map<String, LogAppender> allAppenders, HashMap<String, Object> configuration) {
        super(identifier, parent, allAppenders, configuration);
    }

    @Override
    public AppLogger createLogger(String identifier, LogConfiguration parent, Map<String, LogAppender> allAppenders, HashMap<String, Object> configuration) {
        return new AppLogger(identifier, parent, allAppenders, configuration);
    }

    @Override
    public AppLogger createCleanLogger(String identifier, LogConfiguration parent, boolean synchronousMode) {
        return new AppLogger(identifier, null, parent, new ArrayList<LogAppender>(), synchronousMode, true);
    }

    /// Application life cicle

    public static void onPause() {
        AppLogger.v("appMovedToBackground");

        Date end = new Date(System.currentTimeMillis());
        long diffInMs = end.getTime() - startDate.getTime();
        long timeInterval = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        AppLogger.ev(AppLoggerEvent.createEvent(AppLoggerEvent.UI, AppLogger.ForegroundDuration, String.valueOf(timeInterval), null));
        eventDate = new Date(System.currentTimeMillis());
    }

    public static void onResume() {
        AppLogger.v("appMovedToForeground");

        Date end = new Date(System.currentTimeMillis());
        long diffInMs = end.getTime() - eventDate.getTime();
        long timeInterval = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        AppLogger.ev(AppLoggerEvent.createEvent(AppLoggerEvent.UI, AppLogger.BackgroundDuration, String.valueOf(timeInterval), null));
        eventDate = new Date(System.currentTimeMillis());
    }

    public static void onDestroy() {
        AppLogger.v("appTerminate");

        Date end = new Date(System.currentTimeMillis());
        long diffInMs = end.getTime() - eventDate.getTime();
        long timeInterval = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        AppLogger.ev(AppLoggerEvent.createEvent(AppLoggerEvent.UI, AppLogger.Terminated, String.valueOf(timeInterval), null));
    }

    public static void onStop() {
        AppLogger.v("appResignActive");
    }

    public static void onStart() {
        AppLogger.v("appBecomeActive");
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}