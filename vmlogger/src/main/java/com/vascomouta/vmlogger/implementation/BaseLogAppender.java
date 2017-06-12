package com.vascomouta.vmlogger.implementation;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFilter;
import com.vascomouta.vmlogger.LogFormatter;
import com.vascomouta.vmlogger.constant.LogAppenderConstant;
import com.vascomouta.vmlogger.constant.LogFilterConstant;
import com.vascomouta.vmlogger.constant.LogFormatterConstant;
import com.vascomouta.vmlogger.constant.PatternLogFormatterConstants;
import com.vascomouta.vmlogger.implementation.formatter.DefaultLogFormatter;
import com.vascomouta.vmlogger.implementation.formatter.PatternLogFormatter;
import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;


public  class BaseLogAppender extends LogAppender {


    protected BaseLogAppender(){

    }

    protected BaseLogAppender(String name){
      this.name = name;

    }

    /**
     * Initialize a new `LogRecorderBase` instance to use the given parameters.
     * @param name The name of the log recorder, which must be unique.
     * @param formatters formatters The `LogFormatter`s to use for the recorder.
     */
    public BaseLogAppender(String name, ArrayList<LogFormatter> formatters, ArrayList<LogFilter> filters){
        this.name = name;
        this.formatters = formatters;
        this.dispatchQueue = new DispatchQueue();
        this.filters = filters;
    }


    public BaseLogAppender(String name, ArrayList<LogFormatter> formatters, DispatchQueue dispatchQueue, ArrayList<LogFilter> filters) {
        this.name = name;
        this.formatters = formatters;
        this.dispatchQueue = new DispatchQueue();
        this.filters = new ArrayList<>();
    }

    @Override
    public LogAppender init(HashMap<String, Object> configuration) {
        LogAppender config = configuration(configuration);
        if(config != null){
            this.name = config.name;
            this.formatters = config.formatters;
            this.dispatchQueue = new DispatchQueue();
            this.filters = config.filters;
            return this;
        }
        return null;
    }


    /**
     *
     * @param configuration
     * @return
     */
    public LogAppender configuration(HashMap<String, Object> configuration) {
       String name = (String)configuration.get(LogAppenderConstant.Name);
        if(name != null) {
            BaseLogAppender returnConfig = new BaseLogAppender();
            returnConfig.name = name;
            returnConfig.formatters = new ArrayList<>();

            HashMap<String, Object> encodersConfig = (HashMap<String, Object>) configuration.get(LogAppenderConstant.Encoder);
            if (encodersConfig != null) {
                ArrayList<String> patternConfig = (ArrayList<String>) encodersConfig.get(PatternLogFormatterConstants.Pattern);
                if (patternConfig != null) {
                    for (String pattern : patternConfig) {
                        if (pattern.isEmpty()) {
                            returnConfig.formatters.add(new PatternLogFormatter());
                        } else {
                            returnConfig.formatters.add(new PatternLogFormatter(pattern));
                        }
                    }
                }

                ArrayList<HashMap<String, Object>> customFormatterConfig = (ArrayList<HashMap<String, Object>>) encodersConfig.get(LogAppenderConstant.Formatters);
                if (customFormatterConfig != null) {
                    for (HashMap<String, Object> formatterConfig : customFormatterConfig) {
                        String className = (String) formatterConfig.get(LogFormatterConstant.ClassName);
                        if (className != null) {
                            try {
                                Class<?> c = Class.forName(className);
                                Constructor<?> cons = c.getConstructors()[0];
                                LogFormatter formatter = (LogFormatter) cons.newInstance();
                                if (formatter != null) {
                                    formatter.init(formatterConfig);
                                    returnConfig.formatters.add(formatter);
                                }
                            } catch (Exception ex) {
                                Log.printError("Error on get formatter name from custom configurations" + ex.getMessage());
                            }
                        }
                    }
                }
            } else {
                returnConfig.formatters.add(new DefaultLogFormatter());
            }

            returnConfig.filters = new ArrayList<>();
            //Appender filter
            ArrayList<HashMap<String, Object>> filtersConfig = (ArrayList<HashMap<String, Object>>) configuration.get(LogAppenderConstant.Filters);
            if (filtersConfig != null) {
                for (HashMap<String, Object> filterConfig : filtersConfig) {
                    String filterClassName = (String) filterConfig.get(LogFilterConstant.className);
                    if (filterClassName != null) {
                        try {
                            Class<?> c = Class.forName(filterClassName);
                            Constructor<?> cons = c.getConstructors()[0];
                            LogFilter filter = (LogFilter) cons.newInstance();
                            if (filter != null) {
                                 filter.init(filterConfig);
                                returnConfig.filters.add(filter);
                            }
                        } catch (Exception ex) {
                            Log.printError("Error on get filter name from custom configurations" + ex.getMessage());
                        }
                    }
                }
            }

            return returnConfig;
        }
        return null;
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
