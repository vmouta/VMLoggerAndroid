package com.vascomouta.vmlogger.implementation.filter;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFilter;
import com.vascomouta.vmlogger.LogLevel;
import com.vascomouta.vmlogger.constant.LogLevelFilterConstant;

import java.util.HashMap;


public class LogLevelFilter implements LogFilter {


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


    @Override
    public LogLevelFilter init(HashMap<String, Object> configuration) {
        String level = (String)configuration.get(LogLevelFilterConstant.Level);
        if(level != null){
           return new LogLevelFilter(LogLevel.getLogLevel(level));
        }
            return null;
    }

    @Override
    public boolean shouldRecordLogEntry(LogEntry logEntry) {
        return logEntry.logLevel == severity;
    }

}
