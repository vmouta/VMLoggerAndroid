package com.vascomouta.vmlogger.implementation.appender;

import android.os.Environment;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFilter;
import com.vascomouta.vmlogger.LogFormatter;
import com.vascomouta.vmlogger.constant.FileLogAppenderConstant;
import com.vascomouta.vmlogger.implementation.BaseLogAppender;
import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A `LogRecorder` implementation that stores log messages in a file.
 **Note:**
 * This implementation provides no mechanism for log file rotation
 * or log pruning. It is the responsibility of the developer to keep the log
 * file at a reasonable size. Use `DailyRotatingLogFileRecorder` instead if you'd
 */
public class FileLogAppender extends BaseLogAppender {

    /** The path of the file to which log messages will be written. */
    public String  filePath;

    private File file;
    private String newlineCharset;

     public FileLogAppender(){

     }

    /**
     * Attempts to initialize a new `FileLogRecorder` instance to use the
     * given file path and log formatters. This will fail if `filePath` could
     *not be opened for writing.
     * @param filePath filePath The path of the file to be written. The containing
     * directory must exist and be writable by the process. If the
     * file does not yet exist, it will be created; if it does exist,
     * new log messages will be appended to the end.
     * @param formatters formatters The `LogFormatter`s to use for the recorder.
     */
    public FileLogAppender(String filePath, ArrayList<LogFormatter> formatters){
        new FileLogAppender(filePath, filePath, formatters, new ArrayList<LogFilter>());
    }

    public FileLogAppender(String name, String filePath, ArrayList<LogFormatter> formatters, ArrayList<LogFilter> filters){
        super(name, formatters, filters);
        File directory  = Environment.getExternalStorageDirectory();
        String nsSt = directory.getPath();
        String fileNamePath =  nsSt.concat("/" + filePath);
        File file = new File(fileNamePath);
        if(file != null) {
            this.filePath = fileNamePath;
            this.file = file;
            this.newlineCharset = "/n";
        }
    }


    @Override
    public LogAppender init(HashMap<String, Object> configuration) {
        String filePath = (String) configuration.get(FileLogAppenderConstant.FileName);
        if(filePath == null){
            return null;
        }
        LogAppender config = super.init(configuration);
        if(config == null){
            return null;
        }
        return new FileLogAppender(config.name, filePath, config.formatters, config.filters);
    }

    @Override
    public void recordFormatterMessage(String message, LogEntry logEntry, DispatchQueue dispatchQueue, boolean sychronousMode) {
        try {
            if(!file.exists()){
               file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(message + "\n");
            bufferedWriter.close();
        }catch (IOException ex){
            Log.printError("Error on write logs on file" + ex.getMessage());
        }
    }
}
