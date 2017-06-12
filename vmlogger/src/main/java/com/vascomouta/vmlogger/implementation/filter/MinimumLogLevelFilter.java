package com.vascomouta.vmlogger.implementation.filter;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogLevel;


public class MinimumLogLevelFilter extends LogLevelFilter{

    public MinimumLogLevelFilter(){}

    public MinimumLogLevelFilter(LogLevel logLevel){
        super(logLevel);
    }

    /**
     * Called to determine whether the given `LogEntry` should be recorded.
     * @param logEntry The `LogEntry` to be evaluated by the filter.
     * @return `true` if `entry.severity` is as or more severe than the
    receiver's `severity` property; `false` otherwise.
     */
    @Override
    public boolean shouldRecordLogEntry(LogEntry logEntry) {
        return LogLevel.getLogLevelValue(logEntry.logLevel) >= LogLevel.getLogLevelValue(severity);

    }
}
