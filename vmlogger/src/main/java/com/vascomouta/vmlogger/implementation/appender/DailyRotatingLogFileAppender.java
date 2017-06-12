package com.vascomouta.vmlogger.implementation.appender;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFormatter;
import com.vascomouta.vmlogger.implementation.BaseLogAppender;
import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

    private Date mostRecentLogTime;
    private FileLogAppender currentFileRecorder;


    private static DateFormat fileNameFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'.log'");
    }

    public DailyRotatingLogFileAppender(int daysToKeep, String directoryPath, ArrayList<LogFormatter> formatters) {
       // super("DailyRotatingLogFileRecorder/directoryPath", formatters);
        this.daysToKeep = daysToKeep;
        this.directoryPath = directoryPath;
        try {
            // try to create the directory that will contain the log files
            String url = new URL(directoryPath).getPath();
            File file = new File(url);
            file.createNewFile();
        }catch (MalformedURLException ex){

        }catch (IOException e){

        }
    }


    @Override
    public LogAppender init(HashMap<String, Object> configuration) {
        // fatalError("init(configuration:) has not been implemented")
        return super.init(configuration);

    }

    /**
     * Returns a string representing the filename that will be used to store logs
     * recorded on the given date.
     * @param date date The `NSDate` for which the log file name is desired.
     * @return The filename.
     */
    public String logFilenameForDate(Date date){
        return fileNameFormatter().format(date);
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
           // date = (cal as NSCalendar).date(byAdding: .day, value: -1, to: date, options: .wrapComponents)!
        }

        do {
            File file = new File(directoryPath);
            for(File filename : file.listFiles()){


              //  let pathsToRemove = filenames
           //             .filter { return !$0.hasPrefix(".") }
           //     .filter { return !filesToKeep.contains($0) }
           //     .map { return (self.directoryPath as NSString).appendingPathComponent($0) }

            }
            File[] pathToRemove = file.listFiles();
            for (File removeFile : pathToRemove) {
                try {
                    removeFile.delete();
                } catch (Exception ex) {
                    Log.printError("Error attempting to delete the unneeded file" +  removeFile.getPath() + ex.getMessage());
                }
            }


        }while (true);

    }



}


