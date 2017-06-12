package com.vascomouta.vmlogger.implementation.formatter;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.implementation.BaseLogFormatter;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class Base64LogFormatter extends BaseLogFormatter {

    private final static String UTF_8 = "UTF-8";

    @Override
    public void init(Map<String, Object> configuration) {
        super.init(configuration);
    }

    @Override
    public String formatLogEntry(LogEntry logEntry, String message) {
        String encodedString;
        try {
            encodedString = java.net.URLEncoder.encode(message, UTF_8).replace("+", "%20");
            if (encodedString != null) {
                return encodedString;
            }
        }catch (UnsupportedEncodingException ex){

        }
        return message;
    }
}
