package com.vascomouta.vmlogger.implementation;

import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogConfiguration;
import com.vascomouta.vmlogger.LogLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class BaseLogConfiguration extends LogConfiguration {

    private HashMap<String, LogConfiguration> childMap = new HashMap<>();

    public BaseLogConfiguration(String identifier, LogConfiguration parent , ArrayList<LogAppender> appenders, boolean synchronousMode, boolean additivity) {
        this(identifier, null, parent, appenders, synchronousMode);
    }


    public BaseLogConfiguration(String identifier, LogLevel assignedLevel,LogConfiguration parent, ArrayList<LogAppender> appenders,
                                boolean synchronousMode)
    {
        this.identifier = identifier;
        this.additivity = true;
        this.assignedLogLevel = assignedLevel;
        this.appenders = appenders;
        this.synchronousMode = synchronousMode;
        this.parent = parent;
        this.effectiveLogLevel = parent != null ? parent.effectiveLogLevel : LogLevel.VERBOSE;
    }

    public BaseLogConfiguration init(String identifier, LogLevel assignedLevel, LogConfiguration parent , ArrayList<LogAppender> logAppender,
                                     boolean synchronousMode){
        return  new BaseLogConfiguration(identifier, assignedLevel, parent, logAppender, synchronousMode);
    }

    public void setChildren(){
        children = new ArrayList<>();
        Iterator it = childMap.entrySet().iterator();
        while (it.hasNext()){
            Set childEntrySet = childMap.entrySet();
            for (Object aChildEntrySet : childEntrySet) {
                Map.Entry childMe = (Map.Entry) aChildEntrySet;
                children.add((LogConfiguration) childMe.getValue());
            }
        }
    }


    @Override
    public void addChildren(LogConfiguration childConfiguration, boolean copyGrandChildren) {
        childConfiguration.setParent(this);
        childMap.put(childConfiguration.identifier, childConfiguration);
        LogConfiguration oldConfiguration = childMap.get(childConfiguration.identifier);
        if(oldConfiguration != null && copyGrandChildren && oldConfiguration.children != null){
             for(LogConfiguration grandChildren : oldConfiguration.children){
                childConfiguration.addChildren(grandChildren, copyGrandChildren);
             }
        }
    }

    @Override
    public LogConfiguration getChildren(String name) {
        return childMap.get(name);
    }


    public  void setParent(LogConfiguration parent) {
        this.parent = parent;
    }

    @Override
    public String fullName() {
        return "Log";
    }

    @Override
    public String details() {
        String details = "\n";
        LogLevel assigned = assignedLogLevel;
        if(assigned != null) {
            details = details + assigned.description() + " - " + effectiveLogLevel.description() + "-"
                    + fullName();
        }else{
            details = details + "null - " + effectiveLogLevel.description() + " - " + fullName();
        }
       /* for (_, child) in self.childrenDic {
            details += child.details()
        }*/
        return details;
    }
}
