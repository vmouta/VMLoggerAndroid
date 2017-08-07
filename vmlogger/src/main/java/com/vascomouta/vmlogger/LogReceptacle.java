package com.vascomouta.vmlogger;

import com.vascomouta.vmlogger.implementation.BaseLogFormatter;
import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.util.ArrayList;

public class LogReceptacle {

    private DispatchQueue mQueue;
    private DispatchQueue getQueue(){
        if(mQueue == null){
            mQueue = new DispatchQueue("LogBackReceptacle.acceptQueue");
        }
        return mQueue;
    }

    public  void log(final LogEntry logEntry) {
        final boolean synchronous = logEntry.logger.synchronousMode;
        dispatcherForQueue(getQueue(), synchronous, new Runnable() {
            @Override
            public void run() {
                LogConfiguration logger = logEntry.logger;
                LogConfiguration config;
                do {
                    config = logger;
                    if ((logEntry.logLevel.getValue() >= config.effectiveLogLevel.getValue()) || (config.effectiveLogLevel.getValue() == LogLevel.OFF.getValue()
                            && !config.identifier.equals(logEntry.logger.identifier))) {
                        if (config.appenders != null) {
                            for (final LogAppender appender : config.appenders) {
                                if (logEntry(logEntry, appender.filters)) {
                                    dispatcherForQueue(appender.dispatchQueue, synchronous, new Runnable() {
                                        @Override
                                        public void run() {
                                            String formatted = BaseLogFormatter.stringRepresentationForPayload(logEntry);
                                            String formattedMessage = formatted;
                                            if (appender.formatters != null) {
                                                for (LogFormatter formatter : appender.formatters) {
                                                    formattedMessage = formatter.formatLogEntry(logEntry, formatted);
                                                }
                                            }
                                            appender.recordFormatterMessage(formattedMessage, logEntry, appender.dispatchQueue, synchronous);
                                        }
                                    });
                                }
                            }
                        }
                        logger = config.parent;
                    } else if (!config.identifier.equals(logEntry.logger.identifier)) {
                        logger = config.parent;
                    } else {
                        logger = null;
                    }

                } while (config.additivity && logger != null);
            }
        });

    }

    private boolean logEntry(LogEntry entry, ArrayList<LogFilter> passesFilters ) {
        if(passesFilters == null){
            return true;
        }
        for(LogFilter filter : passesFilters) {
            if (!filter.shouldRecordLogEntry(entry)) {
                return false;
            }
       }return true;
    }

    private  void dispatcherForQueue(DispatchQueue dispatchQueue,  boolean  synchronous , Runnable thread) {
        if (synchronous) {
            dispatchQueue.sync(thread);
        } else {
            dispatchQueue.async(thread);
        }
    }
}
