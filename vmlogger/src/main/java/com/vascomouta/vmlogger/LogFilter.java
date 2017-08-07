package com.vascomouta.vmlogger;

import java.util.Map;

public abstract class LogFilter {

    public  static String CLASSNAME = "class";

    protected LogFilter() {

    }

    /**
     * constructor to be used by introspection
     * @param configuration configuration for the filter
     * @return if configuration is correct a new LogFilter
     */
    public LogFilter(Map<String,Object> configuration) {

    }

    /**
     * Called to determine whether the given `LogEntry` should be recorded.
     * @param logEntry : entry The `LogEntry` to be evaluated by the filter.
     * @return : `true` if `entry` should be recorded, `false` if not.
     */
    abstract public boolean shouldRecordLogEntry(LogEntry logEntry);
}
