package com.vascomouta.vmlogger;

import android.content.Context;
import android.os.Environment;

import com.vascomouta.vmlogger.constant.LogAppenderConstant;
import com.vascomouta.vmlogger.constant.LogConfigConstant;
import com.vascomouta.vmlogger.implementation.RootLogConfiguration;
import com.vascomouta.vmlogger.implementation.appender.ConsoleLogAppender;
import com.vascomouta.vmlogger.utils.XMLParser;
import com.vascomouta.vmlogger.webservice.ConnectivityController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Log extends RootLogConfiguration {

    private static String LoggerInfoFile = "VMLogger-Info";
    private static String LoggerConfig  = "LOGGER_CONFIG";
    private static String LoggerAppenders = "LOGGER_APPENDERS";
    private static String LoggerLevel = "LOGGER_LEVEL";
    private static String LoggerSynchronous = "LOGGER_SYNCHRONOUS";
    private static String Appenders = "APPENDERS";

    private static RootLogConfiguration mLogInstance;
    private static HashMap<String, LogAppender> mAppenders;

    private static LogChannel mEventChannel;
    private static LogChannel mSevereChannel;
    private static LogChannel mErrorChannel;
    private static LogChannel mWarningChannel;
    private static LogChannel mInfoChannel;
    private static LogChannel mDebugChannel;
    private static LogChannel mVerboseChannel;


    public static RootLogConfiguration getInstance(){
        if(mLogInstance == null){
           start(new RootLogConfiguration(), new LogReceptacle());
        }
        return mLogInstance;
    }

    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    public static HashMap<Object, Object> enableFromFile(Context context, String fileName){
        boolean isNetworkAvailable = ConnectivityController.isNetworkAvailable(context);

        if(fileName == null){
            fileName = Log.LoggerInfoFile;
        }
        File directory = Environment.getExternalStorageDirectory();
        if(directory != null){
            String filePath = directory.getPath().concat(fileName);
            File file = new File(filePath);
            if(filePath != null && file.exists()){
               HashMap<Object, Object> configuration = readConfigurationFromFile(filePath);
                if(configuration != null){
                    return configuration;
                }
            }
        }

        return enableFromMainBundleFile(context);
    }


    /**
     *
     * @param context
     * @return
     */
    public static HashMap<Object, Object> enableFromMainBundleFile(Context context){
        HashMap<Object, Object> config = XMLParser.readConfigurationFromXml(context);
       if(config != null) {
            enable(config);
           return config;
       }
        if (BuildConfig.DEBUG) {
            enable(LogLevel.DEBUG, false);
        } else {
            enable(LogLevel.VERBOSE, false);
        }
        printError("Log configuration file not found: " + "fileName");

        return config;

    }

    /**
     *
     * @param filePath
     * @return
     */
    private static HashMap<Object, Object> readConfigurationFromFile(String filePath){
        try {
            HashMap<Object, Object> map = new HashMap<>();
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String line = "";
            while ((line = in.readLine()) != null) {
                String parts[] = line.split("\t");
                map.put(parts[0], parts[1]);
            }
            in.close();
            return map;
        }catch (FileNotFoundException ex){
            printError("Configurations file not exist");
        }catch (IOException e){
            printError("Error on read data from file");
        }
        return null;
    }




    //TODO check code on custom configurations
    private static void enable(HashMap<Object, Object> values){
        LogLevel rootLevel;
        if(BuildConfig.DEBUG){
            rootLevel = LogLevel.DEBUG;
        }else{
            rootLevel = LogLevel.INFO;
        }
        boolean rootSynchronous = false;
        HashMap<String, LogAppender> appenders = new HashMap<>();
        ArrayList<LogAppender> rootAppenders = new ArrayList<>();
        HashMap<String, HashMap<String, Object>> rootChildren = new HashMap<>();
        ArrayList<HashMap<String, Object>> appenderConfig = (ArrayList<HashMap<String,Object>>) values.get(Appenders);
        for(HashMap<String, Object> appender : appenderConfig) {
            String className = (String) appender.get(LogAppenderConstant.Class);
            try {
                Class<?> c = Class.forName(className);
                Constructor<?> cons = c.getConstructors()[0];
                LogAppender logAppender = (LogAppender) cons.newInstance();
                if (logAppender != null) {
                    LogAppender appender1 = logAppender.init(appender);
                    appenders.put(appender1.name, appender1);
                }
            } catch (Exception ex) {
              android.util.Log.v("Error","Error on add appenders" +  ex.getMessage());
            }
        }

        //Root Appenders
        ArrayList<String> rootAppenderConfig = (ArrayList<String>) values.get(LoggerAppenders);
        if (rootAppenderConfig != null) {
            for (String rootAppender : rootAppenderConfig) {
                if (appenders.get(rootAppender) != null) {
                    rootAppenders.add(appenders.get(rootAppender));
                }
            }
        }else if(appenders.get(ConsoleLogAppender.CONSOLE_IDENTIFIER) != null){
            rootAppenders.add(appenders.get(ConsoleLogAppender.CONSOLE_IDENTIFIER));
        }

         if(values.containsKey(LoggerLevel)) {
             String rootLogLevel = (String)values.get(LoggerLevel);
             rootLevel =  LogLevel.getLogLevel(rootLogLevel);
         }
         if(values.containsKey(LoggerSynchronous)) {
            rootSynchronous = (boolean) values.get(LoggerSynchronous);
         }
         if(values.containsKey(LoggerConfig)) {
             rootChildren = (HashMap<String, HashMap<String, Object>>) values.get(LoggerConfig);
         }
        RootLogConfiguration root = new RootLogConfiguration(rootLevel, rootAppenders, rootSynchronous);
        for (Object o : rootChildren.entrySet()) {
            Map.Entry child = (Map.Entry) o;
            HashMap<String, Object> configuration = (HashMap<String, Object>) child.getValue();
            if (configuration != null) {
                LogConfiguration currentChild = root.getChildren((String) child.getKey(), getInstance());
                if (currentChild != null && currentChild.parent != null) {
                    LogConfiguration newChild = new Log(currentChild.identifier, currentChild.parent, appenders, configuration);
                    if (newChild != null) {
                        currentChild.parent.addChildren(newChild, true);
                    }
                } else {
                    printWarning("Trying to configure ROOT logger in logger children configuration. Reserved word, children ignored");
                }
            } else {
                printError("Log configuration for (logName) is not valid. HashMap<String, Object> is required");
            }
        }
        enable(root, rootLevel);
      //  printVerbose("Log Configuration:\n" + values.toString());

    }

    /**
     *
     * @param assignedLevel
     * @param sychrounousMode
     */
    public static void enable(LogLevel assignedLevel,  boolean sychrounousMode){
        ArrayList<LogAppender> appenders = new ArrayList<>();
        appenders.add(new ConsoleLogAppender());
        RootLogConfiguration root = new RootLogConfiguration(RootLogConfiguration.ROOT_IDENTIFIER, assignedLevel, null, appenders, sychrounousMode, false);
        enable(root, LogLevel.VERBOSE);
    }

    private static void enable(RootLogConfiguration root, LogLevel minimumSeverity){
        Log.start(root,  new LogReceptacle(),   minimumSeverity);
    }

    private  static void  start(RootLogConfiguration root, LogReceptacle logReceptacle ) {
        start(root, logReceptacle, LogLevel.VERBOSE);
    }

    private static void start(RootLogConfiguration root, LogReceptacle logReceptacle, LogLevel minimumSeverity) {
        HashMap<String, LogAppender> appenders = new HashMap<>();
        start(root, appenders,createLogChannelWithSeverity(LogLevel.EVENT, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.SEVERE, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.ERROR, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.WARNING, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.INFO, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.DEBUG, logReceptacle, minimumSeverity),
                createLogChannelWithSeverity(LogLevel.VERBOSE, logReceptacle, minimumSeverity));
    }


    private static void start(RootLogConfiguration root, HashMap<String, LogAppender> appenders, LogChannel eventChannel, LogChannel severeChannel,
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



    private static LogChannel createLogChannelWithSeverity(LogLevel severity,LogReceptacle receptacle,LogLevel minimumSeverity) {
        if (severity.getValue() >= minimumSeverity.getValue()) {
                return new LogChannel(severity, receptacle);
        }
        return null;
    }

    protected Log(){

    }

    public Log(String identifier, LogConfiguration parent, Map<String, LogAppender> allAppenders, HashMap<String, Object> configuration) {
        boolean additivity = false;
        LogLevel logLevel = LogLevel.VERBOSE;
        ArrayList<LogAppender> appenders = new ArrayList<>();
        boolean synchronous = false;

        /// Log level
        String level = (String)configuration.get(LogConfigConstant.Level);
        if(level != null){
            logLevel = LogLevel.getLogLevel(level);
        }

        if(configuration.get(LogConfigConstant.Additivity) != null){
            additivity = Boolean.valueOf((String)configuration.get(LogConfigConstant.Additivity));
        }

        String logSynchronous = (String)configuration.get(LogConfigConstant.Synchronous);
        if(logSynchronous != null){
            synchronous = Boolean.valueOf(logSynchronous);
        }

        ArrayList<String> config = (ArrayList<String>) configuration.get(LogConfigConstant.Appenders);
        if(config != null){
            for(String appenderName : config){
                LogAppender appender = allAppenders.get(appenderName);
                if(appender != null){
                    appenders.add(appender);
                }
            }
        }

        new Log(identifier, logLevel, null, appenders,synchronous, additivity);
    }


    public  Log(String identifier, LogLevel assignedLevel, LogConfiguration parent , ArrayList<LogAppender> logAppender,
                     boolean synchronousMode, boolean additivity) {
        super(identifier, assignedLevel, parent, logAppender, synchronousMode, additivity);

    }

    public Log init(String identifier, LogLevel assignedLevel, LogConfiguration parent , ArrayList<LogAppender> logAppender,
                    boolean synchronousMode){
        return  new Log(identifier, assignedLevel, parent, logAppender, synchronousMode, true);
    }

    public void verbose(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this, LogLevel.VERBOSE, message,stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void verbose(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.VERBOSE,stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void verbose(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.VERBOSE, value,stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public static void printVerbose(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(mLogInstance, LogLevel.VERBOSE, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printVerbose(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(mLogInstance, LogLevel.VERBOSE, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printVerbose(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(mLogInstance, LogLevel.VERBOSE,value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void debug(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this,LogLevel.DEBUG, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void debug(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.DEBUG, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void debug(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.DEBUG,value,stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public static void printDebug(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(mLogInstance, LogLevel.DEBUG, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printDebug(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(mLogInstance, LogLevel.DEBUG, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printDebug(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(mLogInstance, LogLevel.DEBUG, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void info(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this, LogLevel.INFO,message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void info(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.INFO, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void info(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.INFO, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public static void printInfo(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(mLogInstance, LogLevel.INFO, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printInfo(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(mLogInstance, LogLevel.INFO, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printInfo(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(mLogInstance, LogLevel.INFO, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
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

    public static void printWarning(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(mLogInstance, LogLevel.WARNING, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printWarning(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(mLogInstance, LogLevel.WARNING, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printWarning(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(mLogInstance, LogLevel.WARNING, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }
    public void error(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this, LogLevel.ERROR, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void error(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.ERROR, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void error(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.ERROR, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public static void printError(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(mLogInstance, LogLevel.ERROR, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printError(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(mLogInstance, LogLevel.ERROR, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printError(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(mLogInstance, LogLevel.ERROR, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void severe(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(this, LogLevel.SEVERE, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void severe(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(this, LogLevel.SEVERE, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void severe(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.SEVERE, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public static void printSevere(String message){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        message(mLogInstance, LogLevel.SEVERE, message, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printSevere( ){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        trace(mLogInstance, LogLevel.SEVERE, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public static void printSevere(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(mLogInstance, LogLevel.SEVERE, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    public void event(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(this, LogLevel.EVENT,value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    public void printEvent(Object value){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        value(mLogInstance,LogLevel.SEVERE, value, stackTraceElement.getFileName(), stackTraceElement.getMethodName(),  stackTraceElement.getLineNumber());
    }

    private static void trace(LogConfiguration logger, LogLevel severity , String fileName, String methodName, int lineNumber){
        channelForSeverity(severity).trace(logger,fileName, methodName,lineNumber);
    }


    private static void message(LogConfiguration logger, LogLevel severity , String message, String fileName, String methodName, int lineNumber) {
        channelForSeverity(severity).message(logger,message, fileName, methodName, lineNumber);
    }


    private static void value(LogConfiguration logger, LogLevel severity, Object value, String fileName, String methodName, int lineNumber) {
        channelForSeverity(severity).value(logger, value, fileName, methodName, lineNumber);
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

    public Log getLogger(String identifier){
         return (Log)getInstance().getChildren(identifier, this);
    }

}