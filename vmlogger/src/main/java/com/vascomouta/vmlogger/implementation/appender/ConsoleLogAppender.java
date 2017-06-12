package com.vascomouta.vmlogger.implementation.appender;


import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.implementation.BaseLogAppender;
import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.util.HashMap;


public class ConsoleLogAppender extends BaseLogAppender {

    public static String CONSOLE_IDENTIFIER = "console";


    public ConsoleLogAppender() {
       super(ConsoleLogAppender.CONSOLE_IDENTIFIER);
    }

    @Override
    public LogAppender init(HashMap<String, Object> configuration) {
        return super.init(configuration);
    }

    @Override
    public void recordFormatterMessage(String message, LogEntry logEntry, DispatchQueue thread, boolean sychronousMode) {
        System.out.println(message);
    }

}
