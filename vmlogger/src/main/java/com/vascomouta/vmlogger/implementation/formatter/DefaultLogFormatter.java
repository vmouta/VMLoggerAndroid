package com.vascomouta.vmlogger.implementation.formatter;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.implementation.BaseLogFormatter;

import java.util.Map;


public class DefaultLogFormatter extends BaseLogFormatter {


    private boolean showThreadID = false;
    private boolean showLogIdentifier  = false;
    private boolean showLogLevel = true;
    private boolean showDate = true;
    private boolean showMessage = false;

    private boolean showThreadName = false;
    private boolean showFunctionName = true;
    private boolean showFileName = true;
    private boolean showLineNumber = true;

    public DefaultLogFormatter(){

    }

    /**
     * Initializes the DefaultLogFormatter using the given settings.
     * @param showThreadID
     * @param showLogIdentifier
     * @param showLogLevel
     * @param showDate
     * @param showMessage
     * @param showThreadName
     * @param showFunctionName
     * @param showFileName
     * @param showLineNumber
     */
    public DefaultLogFormatter(boolean showThreadID, boolean showLogIdentifier, boolean showLogLevel,
                               boolean showDate, boolean showMessage, boolean showThreadName,
                               boolean showFunctionName, boolean showFileName, boolean showLineNumber) {
        this.showThreadID = showThreadID;
        this.showLogIdentifier = showLogIdentifier;
        this.showLogLevel = showLogLevel;
        this.showDate = showDate;
        this.showMessage = showMessage;
        this.showThreadName = showThreadName;
        this.showFunctionName = showFunctionName;
        this.showFileName = showFileName;
        this.showLineNumber = showLineNumber;
    }

    @Override
    public String formatLogEntry(LogEntry entry, String message) {
        String extendedDetails = "";
        if(showDate) {
            String timestamp = stringRepresentationOfTimestamp(entry.timestamp);
            extendedDetails += timestamp;
        }

        if(showLogLevel) {
            String severity = stringRepresentationOfSeverity(entry.logLevel);
            extendedDetails += " "  + severity;
        }

        if(showLogIdentifier) {
            extendedDetails += "[" +  entry.logger.fullName() +"] ";
        }

        if(showThreadName) {
            extendedDetails += BaseLogFormatter.stringRepresentationForMDC();
        }

        if (showFileName){
            String fileName= entry.callingFilePath != null ? entry.callingFilePath : "(unknown)";
            String caller = "[" + fileName + (showLineNumber ? ": " + entry.callingFileLine : " ") + "] ";
            extendedDetails += caller;
        } else if (showLineNumber) {
            extendedDetails += "[" + entry.callingFileLine + "] ";
        }

        if(showFunctionName) {
            extendedDetails += entry.callingFunction + " ";
        }

        if (showThreadID) {
            extendedDetails += BaseLogFormatter.stringRepresentationOfThreadID(entry.callingThreadID);
        }
        return extendedDetails + "> " + message;
    }
}
