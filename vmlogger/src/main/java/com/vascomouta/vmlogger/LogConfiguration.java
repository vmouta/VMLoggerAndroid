package com.vascomouta.vmlogger;

import java.util.ArrayList;
import java.util.Collection;

public abstract class LogConfiguration {

    public static String APPENDERS = "appenders";
    public static String LEVEL = "level";
    public static String SYNCHRONOUS = "synchronous";
    public static String ADDITIVITY = "additivity";

    public String identifier;

    public boolean additivity;

    public LogLevel assignedLogLevel;

    public LogLevel effectiveLogLevel;

    public ArrayList<LogAppender> appenders;

    public  boolean synchronousMode;

    public  LogConfiguration parent;

    public abstract void addChildren(LogConfiguration childConfiguration, boolean copyGrandChildren);

    public abstract LogConfiguration getChildren(String name);
    public abstract Collection<LogConfiguration> getChildrens();

    public abstract String fullName();

    public abstract void setParent(LogConfiguration parent);

    public abstract String details();
}
