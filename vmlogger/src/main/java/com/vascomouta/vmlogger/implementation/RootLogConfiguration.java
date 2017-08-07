package com.vascomouta.vmlogger.implementation;

import android.util.ArrayMap;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogConfiguration;
import com.vascomouta.vmlogger.LogLevel;
import com.vascomouta.vmlogger.LogFactory;
import com.vascomouta.vmlogger.implementation.appender.ConsoleLogAppender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class RootLogConfiguration extends BaseLogConfiguration {

    public static String ROOT_IDENTIFIER = "root";
    public static String DOT = ".";

    public RootLogConfiguration() {
        this(RootLogConfiguration.ROOT_IDENTIFIER, LogLevel.INFO, null, new ArrayList<LogAppender>(Arrays.asList(new ConsoleLogAppender())), false, false);
    }

    public RootLogConfiguration(LogLevel assignedLevel, ArrayList<LogAppender> appenders, boolean synchronousMode) {
        this(RootLogConfiguration.ROOT_IDENTIFIER, assignedLevel, null, appenders, synchronousMode, false);
    }

    public RootLogConfiguration(String identifier, LogLevel assignedLevel, LogConfiguration parent, ArrayList<LogAppender> appenders, boolean synchronousMode) {
        this(identifier, assignedLevel, parent, appenders, synchronousMode, true);
    }

    public RootLogConfiguration(String identifier, LogLevel assignedLevel, LogConfiguration parent, ArrayList<LogAppender> appenders, boolean synchronousMode, boolean additivity) {
        super(identifier, assignedLevel, parent, appenders, synchronousMode, additivity);
    }

    private boolean isRootLogger() {
        // only the root logger has a null parent
        return parent == null;
    }

    public LogConfiguration getChildren(String identifier, LogFactory logFactory) {
        String name = identifier;
        LogConfiguration parent = this;
        while (true) {
            Object child = parent.getChildren(name);
            if (child != null && child instanceof LogConfiguration) {
                return (LogConfiguration)child;
            } else {
                String tree = null;
                int range = name.indexOf(RootLogConfiguration.DOT);
                if(range != -1) {
                    tree = name.substring(range + 1, name.length());
                    name = name.substring(0, range);
                    child = parent.getChildren(name);
                    if(child != null){
                        parent = (LogConfiguration) child;
                        name = tree;
                        continue;
                    }
                }

                LogConfiguration childConf = logFactory.createCleanLogger(name, parent, synchronousMode);
                parent.addChildren(childConf, false);
                if(tree == null){
                    return childConf;
                }
                parent = childConf;
                name = tree;
            }
        }
    }

    public String fullName() {
        String name;
        if (parent != null && !parent.identifier.equals(RootLogConfiguration.ROOT_IDENTIFIER)) {
            LogConfiguration parent = this.parent;
            name = parent.fullName() + RootLogConfiguration.DOT + this.identifier;
        } else {
            name = this.identifier;
        }
        return name;
    }
}
