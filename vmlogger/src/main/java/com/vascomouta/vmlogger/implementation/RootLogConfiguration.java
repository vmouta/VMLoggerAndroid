package com.vascomouta.vmlogger.implementation;

import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogConfiguration;
import com.vascomouta.vmlogger.LogLevel;
import com.vascomouta.vmlogger.implementation.appender.ConsoleLogAppender;

import java.util.ArrayList;


public class RootLogConfiguration extends BaseLogConfiguration {


    public static String ROOT_IDENTIFIER = "root";
    public static String DOT = ".";

    public RootLogConfiguration() {
        super(RootLogConfiguration.ROOT_IDENTIFIER, LogLevel.INFO, null, new ArrayList<LogAppender>(), false);
    }

    public RootLogConfiguration(LogLevel assignedLevel, ArrayList<LogAppender> appenders, boolean synchronousMode) {
        super(RootLogConfiguration.ROOT_IDENTIFIER, assignedLevel, null, appenders, synchronousMode);
    }

    public RootLogConfiguration(String identifier, LogLevel assignedLevel, LogConfiguration parent, ArrayList<LogAppender> appenders, boolean synchronousMode, boolean additivity) {
        super(identifier, assignedLevel, parent, appenders, synchronousMode);
    }

    public RootLogConfiguration init(String identifier, LogLevel assignedLevel, LogConfiguration parent , ArrayList<LogAppender> logAppender,
                    boolean synchronousMode){
        return  new RootLogConfiguration(identifier, assignedLevel, parent, logAppender, synchronousMode, true);
    }

    private boolean isRootLogger() {
        // only the root logger has a null parent
        return parent == null;
    }


    private ArrayList<LogAppender> getLogAppender() {
        ArrayList<LogAppender> logAppenders = new ArrayList<>();
        logAppenders.add(new ConsoleLogAppender());
        return logAppenders;
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

    public LogConfiguration getChildren(String identifier, BaseLogConfiguration type) {
        String name = identifier;
        LogConfiguration parent = this;
        while (true) {
            if (parent.getChildren(name) != null) {
                //return parent.getChildren(name);
                RootLogConfiguration child = (RootLogConfiguration) parent.getChildren(name);
                return type.init(child.identifier, child.assignedLogLevel, child.parent, child.appenders, child.synchronousMode);
               // return type;
            } else {
                String tree = null;
                if(name.contains(RootLogConfiguration.DOT)) {
                    tree = name.substring(name.indexOf(RootLogConfiguration.DOT) + 1, name.length());
                    name = name.substring(0, name.indexOf(RootLogConfiguration.DOT));
                }

                if(parent.getChildren(name) != null){
                    parent = parent.getChildren(name);
                    name = tree != null ? tree : name;
                    continue;
                }

               LogConfiguration child = type.init(name, null, parent, new ArrayList<LogAppender>(), synchronousMode);
                parent.addChildren(child, false);
                if(tree == null){
                    return child;
                }
                parent = child;
                name = tree;
        }

    }

}

}
