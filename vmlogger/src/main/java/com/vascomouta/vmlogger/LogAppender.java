package com.vascomouta.vmlogger;

import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.util.ArrayList;
import java.util.Map;

public abstract class LogAppender {

    public static String NAME = "name";
    public static String ENCODER = "encoder";
    public static String FILTERS = "filters";
    public static String CLASSNAME = "class";
    public static String FORMATTERS = "formatters";

    /**
     * The name of the `LogRecorder`, which is constructed automatically based on the `filePath`.
     */
    public String name;

    /**
     * The `LogFormatter`s that will be used to format messages for the `LogEntry`s to be logged.
     */
    public ArrayList<LogFormatter> formatters;

    /**
     * The list of `LogFilter`s to be used for filtering log messages.
     */
    public ArrayList<LogFilter> filters;

    /**
     * queue that should be used for logging actions related to
     * the receiver.
     */
    public DispatchQueue dispatchQueue;

    protected LogAppender() {

    }

    public LogAppender(Map<String,Object> configuration) {

    }

    public abstract void recordFormatterMessage(String message, LogEntry logEntry, DispatchQueue dispatchQueue , boolean sychronousMode);

}
