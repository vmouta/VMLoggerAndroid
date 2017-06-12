package com.vascomouta.vmlogger.implementation.formatter;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogLevel;
import com.vascomouta.vmlogger.implementation.BaseLogFormatter;

import java.util.HashMap;
import java.util.Map;


// MARK: - PrePostFixLogFormatter
/// A log formatter that will optionally add a prefix, and/or postfix string to a message
public class PrePostFixLogFormatter extends BaseLogFormatter {

    /// Internal cache of the prefix strings for each log level
    private HashMap<LogLevel, String> prefixStrings = new HashMap<>();

    /// Internal cache of the postfix strings codes for each log level
    private HashMap<LogLevel, String> postfixStrings = new HashMap<>();

    @Override
    public void init(Map<String, Object> configuration) {
        super.init(configuration);
    }


    /**
     * Set the prefix/postfix strings for a specific log level.
     * @param prefix A string to prepend to log messages.
     * @param postfix A string to postpend to log messages.
     * @param level The log level.
     */
    public void apply(String prefix,String postfix, LogLevel level) {
        if(level == null){
            if(prefix == null || postfix == null){
                clearFormatting();
                return;
            }
        }
        for(LogLevel logLevel : LogLevel.getAllLevel()){
            apply(prefix, postfix, logLevel);
        }

       if(prefix != null){
           prefixStrings.put(level, prefix);
       }else{
           prefixStrings.remove(level);
       }

        if(postfix != null){
            postfixStrings.put(level, postfix);
        }else{
            postfixStrings.remove(level);
        }

    }

    /**
     * Clear all previously set colours. (Sets each log level back to default)
     */
    public void clearFormatting() {
        prefixStrings = new HashMap<>();
        postfixStrings = new HashMap<>();
    }

    /**
     * Returns a formatted representation of the given `LogEntry`.
     * @param logEntry entry The `LogEntry` being formatted.
     * @param message
     * @return The formatted representation of `entry`. This particular
     * implementation will never return `nil`.
     */
    @Override
    public String formatLogEntry(LogEntry logEntry, String message) {
        return  "(" + getPrefixString(logEntry.logLevel) + message + getPostfixString(logEntry.logLevel) +  ")";
    }

    private String getPrefixString(LogLevel logLevel){
       return  prefixStrings.get(logLevel) != null ? prefixStrings.get(logLevel) : "";
    }

    private String getPostfixString(LogLevel logLevel){
        return  postfixStrings.get(logLevel) != null ? prefixStrings.get(logLevel) : "";
    }

    //TODO change
    @Override
    public String toString() {
        return super.toString();
    }
}

