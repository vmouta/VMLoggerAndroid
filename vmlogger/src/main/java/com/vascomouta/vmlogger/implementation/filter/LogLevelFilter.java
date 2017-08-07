package com.vascomouta.vmlogger.implementation.filter;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFilter;
import com.vascomouta.vmlogger.LogLevel;

import java.util.HashMap;

public class LogLevelFilter extends LogFilter {

    public static String Level  ="level";

    /**
     * Returns the `LogSeverity` associated with the receiver.
     */
    public LogLevel severity;

    public LogLevelFilter(){}


    /**
     * Initializes a new `LogSeverityFilter` instance.
     * @param severity severity Specifies the `LogSeverity` that the filter will
     * use to determine whether a given `LogEntry` should be
     * recorded. Only those log entries with a severity equal to
     * or more severe than this value will pass through the filter.
     */
    public LogLevelFilter(LogLevel severity)
    {
        this.severity = severity;
    }

    public LogLevelFilter(HashMap<String, Object> configuration) {
        severity = LogLevel.getLogLevel((String)configuration.get(Level));
    }

    @Override
    public boolean shouldRecordLogEntry(LogEntry logEntry) {
        return logEntry.logLevel == severity;
    }

}
