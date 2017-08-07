package com.vascomouta.vmloggertest;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogConfiguration;
import com.vascomouta.vmlogger.LogLevel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppLogger extends Log {

    private static String AppLoggerUI = "TrackUI";
    private static String ForgroundDuration = "ForgroundDuration";
    private static String BackgroundDuration = "BackgroundDuration";
    private static String Teriminated = "Terminated";

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

    public void onPause() {
        AppLogger.v("appMovedToBackground");

        Date end = new Date(System.currentTimeMillis());
        long diffInMs = end.getTime() - startDate.getTime();
        long timeInterval = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        AppLogger.e(AppLoggerEvent.createEvent(AppLoggerEvent.UI, ForgroundDuration, String.valueOf(timeInterval), null));
        eventDate = new Date(System.currentTimeMillis());
    }

    public void onResume() {
        AppLogger.v("appMovedToForeground");

        Date end = new Date(System.currentTimeMillis());
        long diffInMs = end.getTime() - eventDate.getTime();
        long timeInterval = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        AppLogger.e(AppLoggerEvent.createEvent(AppLoggerEvent.UI, BackgroundDuration, String.valueOf(timeInterval), null));
        eventDate = new Date(System.currentTimeMillis());
    }

    public void onDestroy() {
        AppLogger.v("appTerminate");

        Date end = new Date(System.currentTimeMillis());
        long diffInMs = end.getTime() - eventDate.getTime();
        long timeInterval = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        AppLogger.e(AppLoggerEvent.createEvent(AppLoggerEvent.UI, Teriminated, String.valueOf(timeInterval), null));
    }

    public void onStop() {
        AppLogger.v("appResignActive");
    }

    public void onStart() {
        AppLogger.v("appBecomeActive");
    }
}