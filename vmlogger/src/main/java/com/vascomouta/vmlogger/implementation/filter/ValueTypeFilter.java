package com.vascomouta.vmlogger.implementation.filter;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFilter;
import com.vascomouta.vmlogger.utils.ObjectType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A `LogFilter` implementation that filters out any `LogEntry` with a
 *`LogSeverity` less than a specified value.
 */
public class ValueTypeFilter extends LogFilter {

    public static String Types = "types";

    /**
     * Returns the `LogSeverity` associated with the receiver.
     */
    public ArrayList<String> type;

    public ValueTypeFilter(){

    }

    public ValueTypeFilter(HashMap<String, Object> configuration) {
        type = (ArrayList<String>) configuration.get(Types);
    }

    /**
     * Initializes a new `LogSeverityFilter` instance.
     * @param types severity Specifies the `LogSeverity` that the filter will
     * use to determine whether a given `LogEntry` should be
     * recorded. Only those log entries with a severity equal to
     * or more severe than this value will pass through the filter.
     */
    public ValueTypeFilter(ArrayList<String> types){
        this.type = types;
    }

    @Override
    public boolean shouldRecordLogEntry(LogEntry logEntry) {
        switch (logEntry.payload){
            case VALUE:
                if(logEntry.value != null){
                    String objectType = ObjectType.getType(logEntry.value);
                    return type.contains(objectType);
                }
            default:
                return false;
        }
    }
}
