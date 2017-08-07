package com.vascomouta.vmlogger.implementation.appender;

import android.util.Log;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.implementation.BaseLogAppender;
import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.util.Map;


public class ConsoleLogAppender extends BaseLogAppender {

    public static String CONSOLE_IDENTIFIER = "console";

    public ConsoleLogAppender() {
        super(ConsoleLogAppender.CONSOLE_IDENTIFIER);
    }

    public ConsoleLogAppender(Map<String, Object> configuration) {
        super(configuration);
    }

    @Override
    public void recordFormatterMessage(String message, LogEntry logEntry, DispatchQueue thread, boolean sychronousMode) {
        //System.out.println(message);
        switch (logEntry.logLevel) {
            case VERBOSE:
                Log.v(this.name, message);
                break;
            case DEBUG:
                Log.d(this.name, message);
                break;
            case WARNING:
                Log.w(this.name, message);
                break;
            case INFO:
                Log.i(this.name, message);
                break;
            case ERROR:
                Log.e(this.name, message);
                break;
            case SEVERE:
                Log.wtf(this.name, message);
                break;
            case EVENT:
                Log.i(this.name, message);
                break;
        }
    }
}
