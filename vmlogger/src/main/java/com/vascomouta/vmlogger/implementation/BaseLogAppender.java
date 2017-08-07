package com.vascomouta.vmlogger.implementation;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFilter;
import com.vascomouta.vmlogger.LogFormatter;
import com.vascomouta.vmlogger.implementation.formatter.DefaultLogFormatter;
import com.vascomouta.vmlogger.implementation.formatter.PatternLogFormatter;
import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map;


public class BaseLogAppender extends LogAppender {

    protected BaseLogAppender(){

    }

    protected BaseLogAppender(String name){
        this();
        this.name = name;
        this.dispatchQueue = new DispatchQueue(this.name);
    }

    /**
     * Initialize a new `LogRecorderBase` instance to use the given parameters.
     * @param name The name of the log recorder, which must be unique.
     * @param formatters formatters The `LogFormatter`s to use for the recorder.
     */
    public BaseLogAppender(String name, ArrayList<LogFormatter> formatters, ArrayList<LogFilter> filters){
        this(name);
        this.formatters = formatters;
        this.filters = filters;
    }

    public BaseLogAppender(Map<String,Object> configuration) {
        super(configuration);

        // Appender Name
        name = (String)configuration.get(LogAppender.NAME);
        this.dispatchQueue = new DispatchQueue(this.name);

        // Appender Encoder
        formatters = new ArrayList<LogFormatter>();
        Map<String, Object> encodersConfig = (Map<String, Object>) configuration.get(LogAppender.ENCODER);
        if (encodersConfig != null) {
            ArrayList<String> patternConfig = (ArrayList<String>) encodersConfig.get(PatternLogFormatter.PATTERN);
            if (patternConfig != null) {
                for (String pattern : patternConfig) {
                    if (pattern.isEmpty()) {
                        formatters.add(new PatternLogFormatter());
                    } else {
                        formatters.add(new PatternLogFormatter(pattern));
                    }
                }
            }

            ArrayList<Map<String, Object>> customFormatterConfig = (ArrayList<Map<String, Object>>) encodersConfig.get(LogAppender.FORMATTERS);
            if (customFormatterConfig != null) {
                for (Map<String, Object> formatterConfig : customFormatterConfig) {
                    String className = (String) formatterConfig.get(LogFormatter.CLASSNAME);
                    if (className != null) {
                        try {
                            Class<?> c = Class.forName(className);
                            Constructor<?> cons = c.getConstructor(Map.class);
                            LogFormatter formatter = (LogFormatter) cons.newInstance(formatterConfig);
                            if (formatter != null) {
                                formatters.add(formatter);
                            }
                        } catch (Exception ex) {
                            Log.e("Error on get formatter name from custom configurations" + ex.getMessage());
                        }
                    }
                }
            }
        } else {
            formatters.add(new DefaultLogFormatter());
        }

        //Appender filter
        filters = new ArrayList<LogFilter>();
        ArrayList<Map<String, Object>> filtersConfig = (ArrayList<Map<String, Object>>) configuration.get(LogAppender.FILTERS);
        if (filtersConfig != null) {
            for (Map<String, Object> filterConfig : filtersConfig) {
                String filterClassName = (String) filterConfig.get(LogFilter.CLASSNAME);
                if (filterClassName != null) {
                    try {
                        Class<?> c = Class.forName(filterClassName);
                        Constructor<?> cons = c.getConstructor(Map.class);
                        LogFilter filter = (LogFilter) cons.newInstance(filterConfig);
                        if (filter != null) {
                            filters.add(filter);
                        }
                    } catch (Exception ex) {
                        Log.e("Error on get filter name from custom configurations" + ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     This implementation does nothing. Subclasses must override this function
     to provide actual log recording functionality.

     **Note:** This function is only called if one of the `formatters`
     associated with the receiver returned a non-`nil` string.

     :param:     message The message to record.

     :param:     entry The `LogEntry` for which `message` was created.

     :param:     currentQueue The GCD queue on which the function is being
     executed.

     :param:     synchronousMode If `true`, the receiver should record the
     log entry synchronously. Synchronous mode is used during
     debugging to help ensure that logs reflect the latest state
     when debug breakpoints are hit. It is not recommended for
     production code.
     */
    @Override
    public void recordFormatterMessage(String message, LogEntry logEntry, DispatchQueue dispatchQueue, boolean synchronousMode) {
        //TODO
        //precondition(false, "Must override this")
    }
}
