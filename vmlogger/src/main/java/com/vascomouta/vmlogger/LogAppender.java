package com.vascomouta.vmlogger;

import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class LogAppender {


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

    public abstract void recordFormatterMessage(String message, LogEntry logEntry, DispatchQueue dispatchQueue , boolean sychronousMode);

    public abstract LogAppender init(HashMap<String,Object> configuration);


}
