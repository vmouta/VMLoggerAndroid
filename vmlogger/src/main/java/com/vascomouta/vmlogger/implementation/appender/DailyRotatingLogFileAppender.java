package com.vascomouta.vmlogger.implementation.appender;

import android.os.Environment;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFilter;
import com.vascomouta.vmlogger.LogFormatter;
import com.vascomouta.vmlogger.implementation.BaseLogAppender;
import com.vascomouta.vmlogger.implementation.formatter.DefaultLogFormatter;
import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A `LogRecorder` implementation that maintains a set of daily rotating log
 *files, kept for a user-specified number of days.
 **Important:**
 * The `DailyRotatingLogFileRecorder` is expected to have full
 * control over the `directoryPath` with which it was instantiated. Any file not
 * explicitly known to be an active log file may be removed during the pruning
 * process. Therefore, be careful not to store anything in the `directoryPath`
 * that you wouldn't mind being deleted when pruning occurs.
 */
public class DailyRotatingLogFileAppender extends BaseLogAppender {

    public static String FILENAMEDATEFORMAT = "filenamedateformat";
    public static String DAYSTOKEEP = "daystokeep";
    public static String DIRECTORYTOPATH = "directorypath";

    public static String DEFAULTFILENAMEDATEFORMAT = "yyyy-MM-dd'.log'";
    public static String DEFAULTDAYSTOKEEP= "30";
    public static String DEFAULTDIRECTORYPATH = "/logs";

    /**
     * The number of days for which the receiver will retain log files
     * before they're eligible for pruning.
     */
    public int daysToKeep;

    /**
     * The filesystem path to a directory where the log files will be
     * stored.
     */
    public String directoryPath;
    public String directoryURLPath;

    private String filenameDateFormatter;
    private DateFormat getFileNameFormatter() {
        return new SimpleDateFormat(filenameDateFormatter);
    }

    private Date mostRecentLogTime;
    private FileLogAppender currentFileRecorder;

    public DailyRotatingLogFileAppender(int daysToKeep, String directoryPath) {
        this(daysToKeep, directoryPath, new ArrayList<LogFormatter>(Arrays.asList(new DefaultLogFormatter())));
    }

    public DailyRotatingLogFileAppender(int daysToKeep, String directoryPath, ArrayList<LogFormatter> formatters) {
        this(daysToKeep, directoryPath, formatters, new ArrayList<LogFilter>());
    }

    public DailyRotatingLogFileAppender(int daysToKeep, String directoryPath, ArrayList<LogFormatter> formatters, ArrayList<LogFilter> filters) {
        this("DailyRotatingLogFileRecorder["+directoryPath+"]", DEFAULTFILENAMEDATEFORMAT, daysToKeep, directoryPath, formatters, filters);
    }

    public DailyRotatingLogFileAppender(String name, String dateformat, int daysToKeep, String directoryPath, ArrayList<LogFormatter> formatters, ArrayList<LogFilter> filters) {
        super(name, formatters, filters);
        this.filenameDateFormatter = dateformat;
        this.daysToKeep = daysToKeep;
        this.directoryPath = directoryPath;
        this.directoryURLPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + this.directoryPath;
        File dir = new File(this.directoryURLPath);
        dir.mkdirs();
    }

    public DailyRotatingLogFileAppender(Map<String, Object> configuration) {
        super(configuration);
        String fileNameDateFormat = (String) configuration.get(FILENAMEDATEFORMAT);
        if(fileNameDateFormat != null) {
            this.filenameDateFormatter = fileNameDateFormat;
        } else {
            this.filenameDateFormatter = DEFAULTFILENAMEDATEFORMAT;
        }
        String daysToKeep = (String) configuration.get(DAYSTOKEEP);
        if(daysToKeep != null) {
            this.daysToKeep = Integer.getInteger(daysToKeep);
        } else {
            this.daysToKeep = Integer.getInteger(DEFAULTDAYSTOKEEP);
        }
        String directoryPath = (String) configuration.get(DIRECTORYTOPATH);
        if(directoryPath != null) {
            this.directoryPath = directoryPath;
        } else {
            this.directoryPath = DEFAULTDIRECTORYPATH;
        }
        this.directoryURLPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + this.directoryPath;
        File dir = new File(this.directoryURLPath);
        dir.mkdirs();
    }

    /**
     * Returns a string representing the filename that will be used to store logs
     * recorded on the given date.
     * @param date date The `NSDate` for which the log file name is desired.
     * @return The filename.
     */
    public String logFilenameForDate(Date date){
        return getFileNameFormatter().format(date);
    }

    private FileLogAppender fileLogRecordedForDate(Date date, String directoryPath, ArrayList<LogFormatter> formatters){
            String fileName = logFilenameForDate(date);
            String filePath = directoryPath.concat(fileName);
        return new FileLogAppender(filePath, formatters);
    }

    private FileLogAppender fileLogRecorderForDate(Date date) {
        return fileLogRecordedForDate(date, directoryPath,  formatters);
    }

    private boolean isDate(Date firstDate, Date secondDate){
        String firstDateStr = logFilenameForDate(firstDate);
        String secondDateStr = logFilenameForDate(secondDate);
        return firstDateStr.equals(secondDateStr);
    }


    /**
     * Called by the `LogReceptacle` to record the specified log message.
     **Note:** This function is only called if one of the `formatters`
     * associated with the receiver returned a non-`nil` string.
     * @param message message The message to record.
     * @param logEntry entry The `LogEntry` for which `message` was created.
     * @param dispatchQueue currentQueue The GCD queue on which the function is being executed.
     * @param synchronousMode  If `true`, the receiver should record the
     *                         log entry synchronously. Synchronous mode is used during
     *                         debugging to help ensure that logs reflect the latest state
     *                         when debug breakpoints are hit. It is not recommended for production code.
     */
    @Override
    public void recordFormatterMessage(String message, LogEntry logEntry, DispatchQueue dispatchQueue, boolean synchronousMode) {

        if(mostRecentLogTime == null || !isDate(logEntry.timestamp, mostRecentLogTime)){
            prune();
            currentFileRecorder = fileLogRecorderForDate(logEntry.timestamp);
        }
        mostRecentLogTime = logEntry.timestamp;
        currentFileRecorder.recordFormatterMessage(message, logEntry, dispatchQueue, synchronousMode);
    }

    /**
     *
     */
    public void  prune()
    {
        // figure out what files we'd want to keep, then nuke everything else
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        Set<String> filesToKeep = new HashSet<>();
        for (int i = 0; i < daysToKeep; i++) {
            String fileName = logFilenameForDate(date);
            filesToKeep.add(fileName);
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);
            date = cal.getTime();
        }

        File file = new File(directoryURLPath);
        for(File filename : file.listFiles()){
            if(filesToKeep.contains(filename.getName()) == false) {
                try {
                    filename.delete();
                } catch (Exception ex) {
                    Log.e("Error attempting to delete the unneeded file" +  filename.getAbsolutePath() + ":" + ex.getMessage());
                }
            }
        }
    }
}


