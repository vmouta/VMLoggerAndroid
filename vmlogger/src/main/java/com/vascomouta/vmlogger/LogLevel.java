package com.vascomouta.vmlogger;


import java.util.ArrayList;

public enum LogLevel {

    ALL("All", 0),
    VERBOSE("Verbose", 1),
    DEBUG("Debug", 2),
    INFO("Info", 3),
    WARNING("Warning", 4),
    ERROR("Error", 5),
    SEVERE("Severe", 6),
    EVENT("Event", 7),
    OFF("OFF", 8);

    private String level;
    private int value;

    LogLevel(String level, int value){
        this.level = level;
        this.value = value;
    }


    String getLevel(){
      return level;
    }

    int getValue(){
        return value;
    }

    public String getLogLevel(LogLevel level){
        switch (level){
            case OFF:
                return OFF.getLevel();
            case VERBOSE:
                return VERBOSE.getLevel();
            case DEBUG:
                return DEBUG.getLevel();
            case INFO:
                return INFO.getLevel();
            case WARNING:
                return WARNING.getLevel();
            case ERROR:
                return ERROR.getLevel();
            case SEVERE:
                return SEVERE.getLevel();
            case EVENT:
                return EVENT.getLevel();
        }
        return ALL.getLevel();
    }

    public static int getLogLevelValue(LogLevel level){
        switch (level){
            case OFF:
                return ALL.getValue();
            case VERBOSE:
                return VERBOSE.getValue();
            case DEBUG:
                return DEBUG.getValue();
            case INFO:
                return INFO.getValue();
            case WARNING:
                return WARNING.getValue();
            case ERROR:
                return ERROR.getValue();
            case SEVERE:
                return SEVERE.getValue();
            case EVENT:
                return EVENT.getValue();
        }
        return ALL.getValue();
    }

    public String description() {
        switch (this) {
            case OFF:
                return ALL.getLevel();
            case VERBOSE:
                return VERBOSE.getLevel();
            case DEBUG:
                return DEBUG.getLevel();
            case INFO:
                return INFO.getLevel();
            case WARNING:
                return WARNING.getLevel();
            case ERROR:
                return ERROR.getLevel();
            case SEVERE:
                return SEVERE.getLevel();
            case EVENT:
                return EVENT.getLevel();
        }
        return ALL.getLevel();
    }

    public static LogLevel getLogLevel(String level){
        if(level.equals(OFF.getLevel())){
            return OFF;
        }else if(level.equals(VERBOSE.getLevel())){
            return VERBOSE;
        }else if(level.equals(DEBUG.getLevel())){
            return DEBUG;
        }else if(level.equals(INFO.getLevel())){
            return  INFO;
        }else if(level.equals(WARNING.getLevel())){
            return WARNING;
        }else if(level.equals(ERROR.getLevel())){
            return ERROR;
        }else if(level.equals(SEVERE.getLevel())){
            return SEVERE;
        }else if(level.equals(EVENT.getLevel())){
            return EVENT;
        }
        return ALL;
    }

    public static ArrayList<LogLevel> getAllLevel(){
        ArrayList<LogLevel> logLevels = new ArrayList<>();
        logLevels.add(LogLevel.VERBOSE);
        logLevels.add(LogLevel.DEBUG);
        logLevels.add(LogLevel.INFO);
        logLevels.add(LogLevel.WARNING);
        logLevels.add(LogLevel.ERROR);
        logLevels.add(LogLevel.SEVERE);
        logLevels.add(LogLevel.EVENT);
        return logLevels;
    }
}
