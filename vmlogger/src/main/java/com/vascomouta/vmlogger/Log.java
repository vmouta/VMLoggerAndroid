package com.vascomouta.vmlogger;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.RawRes;
import android.support.coreui.BuildConfig;

import com.vascomouta.vmlogger.implementation.RootLogConfiguration;
import com.vascomouta.vmlogger.implementation.appender.ConsoleLogAppender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.lang.StringBuffer;

import xmlwise.Plist;

public class Log extends RootLogConfiguration implements LogFactory {

    private static String LOGGERINFOFILE = "VMLogger-Info";
    private static String LOGGERCONFIG  = "LOGGER_CONFIG";
    private static String LOGGERAPPENDERS = "LOGGER_APPENDERS";
    private static String LOGGERLEVEL = "LOGGER_LEVEL";
    private static String LOGGERSYNCHRONOUS = "LOGGER_SYNCHRONOUS";
    private static String APPENDERS = "APPENDERS";

    private static Log mLogInstance;
    private static Map<String, LogAppender> mAppenders;

    private static LogChannel mEventChannel;
    private static LogChannel mSevereChannel;
    private static LogChannel mErrorChannel;
    private static LogChannel mWarningChannel;
    private static LogChannel mInfoChannel;
    private static LogChannel mDebugChannel;
    private static LogChannel mVerboseChannel;

    public static Log getInstance(){
        if(mLogInstance == null){
           start(new Log(), new LogReceptacle());
        }
        return mLogInstance;
    }

    /**
     *
     * @param context
     * @return
     */
    public static Map<String, Object> configureFromFile(Context context) {
        return Log.configureFromFile(context, Log.LOGGERINFOFILE);
    }

    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Map<String, Object> configureFromFile(Context context, String fileName){
        Map<String, Object> configuration = readConfigurationFromFile(context, fileName);
        configure(configuration);
        return configuration;
    }

    /**
     *
     * @param context
     * @return
     */
    public static Map<String, Object> configureFromAssets(Context context){
        return configureFromAssets(context, Log.LOGGERINFOFILE);
    }

    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Map<String, Object> configureFromAssets(Context context, String fileName){
        Map<String, Object> properties = readConfigurationFromAssets(context, fileName);
        if(properties != null) {
            configure(properties);
        } else if (BuildConfig.DEBUG) {
            configure(LogLevel.DEBUG, false);
        } else {
            configure(LogLevel.VERBOSE, false);
        }
        return properties;
    }

    /**
     *
     * @param context
     * @return
     */
    public static Map<String, Object> readConfigurationFromResource(Context context, @RawRes int id) {
        Map<String, Object> properties = null;
        try {
            InputStream inputStream = context.getResources().openRawResource(id);
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                properties = Plist.fromXml(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                br.close();
            }
        }catch (FileNotFoundException ex) {
            e("Configurations file not exist");
        }catch (IOException e) {
            e("Error on read data from file");
        }
        return properties;
    }

    /**
     *
     * @param context
     * @return
     */
    public static Map<String, Object> readConfigurationFromAssets(Context context, String fileName){
        Map<String, Object> properties = null;
        try {
            InputStream is = context.getAssets().open("VMLogger-Info.plist");
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                properties = Plist.fromXml(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                br.close();
            }
        }catch (FileNotFoundException ex) {
            e("Configurations file not exist");
        }catch (IOException e) {
            e("Error on read data from file");
        }
        return properties;
    }

    /**
     *
     * @param fileName
     * @return
     */
    private static Map<String, Object> readConfigurationFromExternalFile( String fileName){
        return readConfigurationFromFile(new File(Environment.getExternalStorageDirectory(), fileName));
    }

    /**
     *
     * @param fileName
     * @return
     */
    private static Map<String, Object> readConfigurationFromFile(Context context, String fileName){
        return readConfigurationFromFile(new File(context.getFilesDir(), fileName));
    }

    /**
     *
     * @param filePath
     * @return
     */
    private static Map<String, Object> readConfigurationFromFile(File filePath){
        Map<String, Object> configuration = null;
        try {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(filePath));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                configuration = Plist.fromXml(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                br.close();
            }
        }catch (FileNotFoundException ex){
            e("Configurations file not exist");
        }catch (IOException e){
            e("Error on read data from file");
        }
        return configuration;
    }

    private static void configure(Map<String, Object> values){

        HashMap<String, LogAppender> appenders = new HashMap<>();
        ArrayList<HashMap<String, Object>> appenderConfig = (ArrayList<HashMap<String,Object>>) values.get(APPENDERS);
        for(HashMap<String, Object> appender : appenderConfig) {
            String className = (String) appender.get(LogAppender.CLASSNAME);
            try {
                Class<?> c = Class.forName(className);
                Constructor<?> cons = c.getConstructor(Map.class);
                LogAppender logAppender = (LogAppender) cons.newInstance(appender);
                if (logAppender != null) {
                    appenders.put(logAppender.name, logAppender);
                }
            } catch (Exception ex) {
              android.util.Log.e("Error", "Error on add appenders\n" +  ex.getMessage());
            }
        }

        //Root Appenders
        ArrayList<LogAppender> rootAppenders = new ArrayList<>();
        ArrayList<String> rootAppenderConfig = (ArrayList<String>) values.get(LOGGERAPPENDERS);
        if (rootAppenderConfig != null) {
            for (String rootAppender : rootAppenderConfig) {
                if (appenders.get(rootAppender) != null) {
                    rootAppenders.add(appenders.get(rootAppender));
                }
            }
        }else if(appenders.get(ConsoleLogAppender.CONSOLE_IDENTIFIER) != null){
            rootAppenders.add(appenders.get(ConsoleLogAppender.CONSOLE_IDENTIFIER));
        }

        /// Root Log Level
        LogLevel rootLevel;
        if(values.containsKey(LOGGERLEVEL)) {
             String rootLogLevel = (String)values.get(LOGGERLEVEL);
             rootLevel =  LogLevel.getLogLevel(rootLogLevel);
        } else if(BuildConfig.DEBUG){
            rootLevel = LogLevel.DEBUG;
        } else {
            rootLevel = LogLevel.INFO;
        }

        // Root synchronous mode
        boolean rootSynchronous = false;
        if(values.containsKey(LOGGERSYNCHRONOUS)) {
            rootSynchronous = (boolean) values.get(LOGGERSYNCHRONOUS);
        }

        Log root = new Log(rootLevel, rootAppenders, rootSynchronous);
        // Children Configuration
        HashMap<String, HashMap<String, Object>> rootChildren = new HashMap<>();
        if(values.containsKey(LOGGERCONFIG)) {
             rootChildren = (HashMap<String, HashMap<String, Object>>) values.get(LOGGERCONFIG);
        }
        for (Object o : rootChildren.entrySet()) {
            Map.Entry child = (Map.Entry) o;
            HashMap<String, Object> configuration = (HashMap<String, Object>) child.getValue();
            if (configuration != null) {
                LogConfiguration currentChild = root.getChildren((String) child.getKey());
                if (currentChild != null && currentChild.parent != null) {
                    LogConfiguration newChild = root.createLogger(currentChild.identifier, currentChild.parent, appenders, configuration);
                    if (newChild != null) {
                        currentChild.parent.addChildren(newChild, true);
                    }
                } else {
                    w("Trying to configure ROOT logger in logger children configuration. Reserved word, children ignored");
                }
            } else {
                e("Log configuration for (logName) is not valid. HashMap<String, Object> is required");
            }
        }
        configure(root, rootLevel);
        v("Log Configuration:\n" + values.toString());
    }

    /**
     *
     * @param assignedLevel
     */
    public static void configure(LogLevel assignedLevel) {
        configure(assignedLevel, true);
    }

    /**
     *
     * @param assignedLevel
     * @param sychrounousMode
     */
    public static void configure(LogLevel assignedLevel,  boolean sychrounousMode) {
        ArrayList<LogAppender> appenders = new ArrayList<LogAppender>(Arrays.asList(new ConsoleLogAppender()));
        Log root = new Log(assignedLevel, appenders, sychrounousMode);
        configure(root, LogLevel.VERBOSE);
    }

    private static void configure(Log root, LogLevel minimumSeverity){
        Log.start(root,  new LogReceptacle(),   minimumSeverity);
    }

    protected static void  start(Log root, LogReceptacle logReceptacle ) {
        start(root, logReceptacle, LogLevel.VERBOSE);
    }

    protected static void start(Log root, LogReceptacle logReceptacle, LogLevel minimumSeverity) {
        HashMap<String, LogAppender> appenders = new HashMap<>();
        start(root, appenders,createLogChannelWithSeverity(LogLevel.EVENT, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.SEVERE, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.ERROR, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.WARNING, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.INFO, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.DEBUG, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.VERBOSE, logReceptacle, minimumSeverity));
    }

    protected static void start(Log root, HashMap<String, LogAppender> appenders, LogChannel eventChannel, LogChannel severeChannel,
                       LogChannel errorChannel, LogChannel warningChannel, LogChannel infoChannel, LogChannel debugChannel, LogChannel verboseChannel){
        mLogInstance = root;
        mVerboseChannel = verboseChannel;
        mDebugChannel = debugChannel;
        mInfoChannel =  infoChannel;
        mWarningChannel = warningChannel;
        mErrorChannel = errorChannel;
        mSevereChannel = severeChannel;
        mEventChannel = eventChannel;
        mAppenders = appenders;
    }

    protected Log() {
        super();
    }

    protected Log(LogLevel assignedLevel, ArrayList<LogAppender> appenders, boolean synchronousMode) {
        super(assignedLevel, appenders, synchronousMode);
    }

    protected Log(String identifier, LogLevel assignedLevel, LogConfiguration parent, ArrayList<LogAppender> appenders, boolean synchronousMode, boolean additivity) {
        super(identifier, assignedLevel, parent, appenders, synchronousMode, additivity);
    }

    public Log(String identifier, LogConfiguration parent, Map<String, LogAppender> allAppenders, HashMap<String, Object> configuration) {
        this.identifier = identifier;
        this.parent = parent;

        /// Log level
        String level = (String)configuration.get(LogConfiguration.LEVEL);
        if(level != null){
            this.assignedLogLevel = LogLevel.getLogLevel(level);
        } else {
            this.assignedLogLevel = null;
        }

        /* Additivity */
        if(configuration.get(LogConfiguration.ADDITIVITY) != null){
            this.additivity = Boolean.valueOf((String)configuration.get(LogConfiguration.ADDITIVITY));
        } else {
            this.additivity = false;
        }

        /* Synchronous */
        String logSynchronous = (String)configuration.get(LogConfiguration.SYNCHRONOUS);
        if(logSynchronous != null){
            this.synchronousMode = Boolean.valueOf(logSynchronous);
        } else {
            this.synchronousMode = false;
        }

        /* Appenders */
        this.appenders = new ArrayList<LogAppender>();
        ArrayList<String> config = (ArrayList<String>) configuration.get(LogConfiguration.APPENDERS);
        if(config != null) {
            for (String appenderName : config) {
                LogAppender appender = allAppenders.get(appenderName);
                if (appender != null) {
                    appenders.add(appender);
                }
            }
        }

        this.effectiveLogLevel = (this.assignedLogLevel != null ? this.assignedLogLevel : (parent != null ? parent.effectiveLogLevel : LogLevel.INFO) );
    }

    public static Log getLogger(String identifier){
        return (Log)getInstance().getChildren(identifier, getInstance());
    }

    // Convenience logging methods
    // * Verbose

    public static void v(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(getInstance(), LogLevel.VERBOSE, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void v(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(getInstance(), LogLevel.VERBOSE, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void v(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(getInstance(), LogLevel.VERBOSE,value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void verbose(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.VERBOSE,stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void verbose(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this, LogLevel.VERBOSE, message,stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void verbose(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.VERBOSE, value,stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    // * Debug

    public static void d(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(getInstance(), LogLevel.DEBUG, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void d(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(getInstance(), LogLevel.DEBUG, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void d(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(getInstance(), LogLevel.DEBUG, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void debug(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.DEBUG, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void debug(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this,LogLevel.DEBUG, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void debug(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.DEBUG,value,stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    // * Info

    public static void i(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(getInstance(), LogLevel.INFO, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void i(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(getInstance(), LogLevel.INFO, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void i(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(getInstance(), LogLevel.INFO, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void info(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.INFO, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void info(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this, LogLevel.INFO,message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void info(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.INFO, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    // * Warning

    public static void w(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(getInstance(), LogLevel.WARNING, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void w(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(getInstance(), LogLevel.WARNING, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void w(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(getInstance(), LogLevel.WARNING, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void warning(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this, LogLevel.WARNING, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void warning(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.WARNING, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void warning(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.WARNING,value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    // * Error

    public static void e(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(getInstance(), LogLevel.ERROR, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void e(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(getInstance(), LogLevel.ERROR, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void e(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(getInstance(), LogLevel.ERROR, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void error(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.ERROR, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void error(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this, LogLevel.ERROR, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void error(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.ERROR, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    // * Severe

    public static void s( ){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(getInstance(), LogLevel.SEVERE, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void s(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(getInstance(), LogLevel.SEVERE, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void s(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(getInstance(), LogLevel.SEVERE, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void severe(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.SEVERE, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void severe(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this, LogLevel.SEVERE, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void severe(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.SEVERE, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    // * Event

    public static void logEvent(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(getInstance(),LogLevel.SEVERE, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void event(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.EVENT,value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    private static void trace(LogConfiguration logger, LogLevel severity , String fileName, String methodName, int lineNumber){
        LogChannel channel = channelForSeverity(severity);
        if(channel != null) channel.trace(logger,fileName, methodName,lineNumber);
    }

    private static void message(LogConfiguration logger, LogLevel severity , String message, String fileName, String methodName, int lineNumber) {
        LogChannel channel = channelForSeverity(severity);
        if(channel != null) channel.message(logger,message, fileName, methodName, lineNumber);
    }

    private static void value(LogConfiguration logger, LogLevel severity, Object value, String fileName, String methodName, int lineNumber) {
        LogChannel channel = channelForSeverity(severity);
        if(channel != null) channel.value(logger, value, fileName, methodName, lineNumber);
    }

    private static LogChannel createLogChannelWithSeverity(LogLevel severity,LogReceptacle receptacle,LogLevel minimumSeverity) {
        if (severity.getValue() >= minimumSeverity.getValue()) {
            return new LogChannel(severity, receptacle);
        }
        return null;
    }

    private static LogChannel channelForSeverity(LogLevel severity) {
        switch (severity) {
            case DEBUG:
                return mDebugChannel;
            case INFO:
                return mInfoChannel;
            case WARNING:
                return mWarningChannel;
            case ERROR:
                return mErrorChannel;
            case SEVERE:
                return mSevereChannel;
            case EVENT:
                return mEventChannel;
        }
        return mVerboseChannel;
    }

    public static void dumpLog() {
        dumpLog(getInstance(), LogLevel.INFO);
    }

    public static void dumpLog(LogConfiguration log) {
        dumpLog(log, LogLevel.INFO);
    }

    public static void dumpLog(LogConfiguration log, LogLevel severity) {
        StringBuffer description = new StringBuffer("assigned: ");

        if( log.assignedLogLevel != null) {
            description.append(log.assignedLogLevel.getLevel().charAt(0));
        } else {
            description.append("-");
        }
        description.append(" | effective: " + log.effectiveLogLevel.getLevel().charAt(0));
        description.append(" | synchronousMode: " + log.synchronousMode);
        description.append(" | additivity: " + log.additivity);
        description.append(" | appenders: " + log.appenders);
        description.append(" | name: " + log.fullName());
        description.append(" | classType: "+ log.getClass().getName());
        switch(severity) {
            case VERBOSE:
                v(description.toString());
                break;
            case DEBUG:
                d(description.toString());
                break;
            case INFO:
                i(description.toString());
                break;
            case WARNING:
                w(description.toString());
                break;
            case ERROR:
                e(description.toString());
                break;
            case SEVERE:
                s(description.toString());
                break;
            case EVENT:
                logEvent(description.toString());
                break;
            default:
                break;
        }
        for(LogConfiguration child : log.getChildrens()) {
            dumpLog(child, severity);
        }
    }

    /* From LogFactory */

    public Log createLogger(String identifier, LogConfiguration parent, Map<String, LogAppender> allAppenders, HashMap<String, Object> configuration) {
        return new Log(identifier, parent, allAppenders, configuration);
    }

    public Log createCleanLogger(String identifier, LogConfiguration parent, boolean synchronousMode) {
        return new Log(identifier, null, parent, new ArrayList<LogAppender>(), synchronousMode, true);
    }
}
