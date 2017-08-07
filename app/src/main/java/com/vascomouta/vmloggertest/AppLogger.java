package com.vascomouta.vmloggertest;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogConfiguration;
import com.vascomouta.vmlogger.LogLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AppLogger extends Log {

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
}