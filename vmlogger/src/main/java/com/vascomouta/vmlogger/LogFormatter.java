package com.vascomouta.vmlogger;

import java.util.Map;


public interface LogFormatter {


    /**
     * Called to create a string representation of the passed-in `LogEntry`.
     * @param entry The `LogEntry` to attempt to convert into a string.
     * @param message
     * @return A `String` representation of `entry`, or `null` if the
              receiver could not format the `LogEntry`.
     */
    String formatLogEntry(LogEntry entry, String message);



    /**
     * constructor to be used by introspection
     * @param configuration configuration for the formatter
     *  returns: if configuration is correct a new LogFormatter
     */
    void init(Map<String, Object> configuration);

}
