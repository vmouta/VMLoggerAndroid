package com.vascomouta.vmlogger.implementation;

import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFormatter;
import com.vascomouta.vmlogger.LogLevel;
import com.vascomouta.vmlogger.utils.ObjectType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class BaseLogFormatter extends LogFormatter {

    private DateFormat dateFormate;
    private int severityTagLength;
    private int identityTagLength;

    protected BaseLogFormatter(){
        super();
        this.dateFormate = timeStampFormatter();
    }

    public BaseLogFormatter(DateFormat dateFormatter, int severityTagLenght, int identityTagLenght){
        super();
        this.dateFormate = dateFormatter;
        this.severityTagLength = severityTagLenght;
        this.identityTagLength = identityTagLenght;
    }

    public BaseLogFormatter(Map<String,Object> configuration) {
        super(configuration);
    }

    private static DateFormat timeStampFormatter(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz");
    }

    /**
     Returns a formatted representation of the given `LogEntry`.

     :param:         entry The `LogEntry` being formatted.

     :returns:       The formatted representation of `entry`. This particular
     implementation will never return `nil`.
     */
    public String formatLogEntry( LogEntry logEntry, String message){
        // precondition(false, "Must override this");
        return null;
    }

    /**
     Returns a string representation for a calling file and line.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations of a `LogEntry`'s `callingFilePath` and
     `callingFileLine` properties.

     :param:     filePath The full file path of the calling file.

     :param:     line The line number within the calling file.

     :returns:   The string representation of `filePath` and `line`.
     */
    public static String stringRepresentationForCallingFile(String filePath, int line) {
        String file = filePath != null ? filePath : "(unknown)";
        return file + ":" + line;
    }

    /**
     Returns a string representation for a calling file and line.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations of a `LogEntry`'s `callingFilePath` and
     `callingFileLine` properties.

     :param:     filePath The full file path of the calling file.

     :param:     line The line number within the calling file.

     :returns:   The string representation of `filePath` and `line`.
     */
    public static String stringRepresentationForFile(String filePath){
        return filePath != null ? filePath : "(unknown)";
    }


    /**
     Returns a string representation of an arbitrary optional value.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations of `LogEntry` payloads.

     :param:     entry The `LogEntry` whose payload is desired in string form.

     :returns:   The string representation of `entry`'s payload.
     */
    public static String stringRepresentationForPayload(LogEntry entry) {
        switch (entry.payload){
            case MESSAGE:
                return entry.message;
            case VALUE:
                return stringRepresentationForValuePayload(entry.value);
        }
        return  "-"; // Used for trace since System out doesn't print empty line
    }


    /**
     Returns a string representation of an arbitrary optional value.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations of `LogEntry` instances containing `.Value` payloads.

     :param:     value The value for which a string representation is desired.

     :returns:   If value is `nil`, the string "`(nil)`" is returned; otherwise,
     the return value of `stringRepresentationForValue(Any)` is
     returned.
     */
    public static String stringRepresentationForValuePayload(@Nullable Object value){
            if(value != null){
               return stringRepresentationForValue(value);
            }
                return "(null)";
    }

    //TODO
    public static String stringRepresentationForExec()
    {
        //closure()
        return "(Executed)";
    }

    public static String stringRepresentationForMDC() {
        boolean isUiThread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? Looper.getMainLooper().isCurrentThread()
                : Thread.currentThread() == Looper.getMainLooper().getThread();
        if(isUiThread) {
             return  "[main] ";
        }else {
            String threadName =  Thread.currentThread().getName();
            if(!threadName.equals("")) {
                return "[" + threadName + "] ";
            }else {
                return "[" + String.format("%p", Thread.currentThread() + "] ");
            }
        }
    }

    /**
     Returns a string representation of an arbitrary value.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations of `LogEntry` instances containing `.Value` payloads.

     :param:     value The value for which a string representation is desired.

     :returns:   A string representation of `value`.
     */
    public static String stringRepresentationForValue(@NonNull Object value){
        String type = ObjectType.getType(value);
        String desc= value.toString();
        if(value.toString() instanceof  Object){
            desc = "(no description)";
        }
       return "<" + type + " : " + desc + " >";
    }

    /**
     Returns a string representation of a given `LogSeverity` value.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations for representing the `severity` value of
     `LogEntry` instances.

     :param:     severity The `LogSeverity` for which a string representation
     is desired.

     :returns:   A string representation of the `severity` value.
     */
    private  static String stringRepresentation(String string, int length, boolean right) {
        if(length > 0) {
            String str = string;
            if(str.length() < length) {
                while(str.length() < length) {
                    if(right) {
                        str = str + " ";
                    } else {
                        str = " " + str;
                    }
                }
            } else {
                int index = str.indexOf(0, length);
                str = string.substring(index);
            }
            return str;
        }
        return string;
    }

    /**
     Returns a string representation of a given `LogSeverity` value.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations for representing the `severity` value of
     `LogEntry` instances.

     :param:     severity The `LogSeverity` for which a string representation
     is desired.

     :returns:   A string representation of the `severity` value.
     */
    public String stringRepresentationOfSeverity(LogLevel severity) {
        return BaseLogFormatter.stringRepresentation(severity.description(), severityTagLength, false);
    }

    /**
     Returns a string representation of a given `LogSeverity` value.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations for representing the `severity` value of
     `LogEntry` instances.

     :param:     severity The `LogSeverity` for which a string representation
     is desired.

     :returns:   A string representation of the `severity` value.
     */
    public String stringRepresentationOfIdentity(String identity) {
        return BaseLogFormatter.stringRepresentation(identity, severityTagLength, false);
    }


    /**
     Returns a string representation of an `` timestamp.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations of a `LogEntry`'s `timestamp` property.

     :param:     timestamp The timestamp.

     :returns:   The string representation of `timestamp`.
     */
    public String stringRepresentationOfTimestamp(Date timestamp) {
        if(dateFormate == null){
            dateFormate = timeStampFormatter();
        }
        return dateFormate.format(timestamp);
    }

    /**
     Returns a string representation of a thread identifier.

     This implementation is used by the `DefaultLogFormatter` for creating
     string representations of a `LogEntry`'s `callingThreadID` property.

     :param:     threadID The thread identifier.

     :returns:   The string representation of `threadID`.
     */
    public  static String stringRepresentationOfThreadID(long threadID){
        return String.format("%08X", threadID);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
