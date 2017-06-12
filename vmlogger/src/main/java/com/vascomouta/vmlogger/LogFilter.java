package com.vascomouta.vmlogger;

import java.util.HashMap;


public interface LogFilter {


    /**
     * Called to determine whether the given `LogEntry` should be recorded.
     * @param logEntry : entry The `LogEntry` to be evaluated by the filter.
     * @return : `true` if `entry` should be recorded, `false` if not.
     */
     boolean shouldRecordLogEntry(LogEntry logEntry);


    /**
     * constructor to be used by introspection
     * @param configuration configuration for the filter
     * @return if configuration is correct a new LogFilter
     */
     LogFilter init(HashMap<String, Object> configuration);

}
