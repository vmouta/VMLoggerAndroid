package com.vascomouta.vmlogger.implementation;

import com.vascomouta.vmlogger.LogAppender;
import com.vascomouta.vmlogger.LogConfiguration;
import com.vascomouta.vmlogger.LogLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;

public abstract class BaseLogConfiguration extends LogConfiguration {

    private HashMap<String, LogConfiguration> childMap = new HashMap<>();

    public BaseLogConfiguration(String identifier, LogLevel assignedLevel, LogConfiguration parent, ArrayList<LogAppender> appenders, boolean synchronousMode) {
        this(identifier, assignedLevel, parent, appenders, synchronousMode, true);
    }

    public BaseLogConfiguration(String identifier, LogConfiguration parent, ArrayList<LogAppender> appenders, boolean synchronousMode, boolean additivity) {
        this(identifier, null, parent, appenders, synchronousMode, additivity);
    }

    public BaseLogConfiguration(String identifier, LogLevel assignedLevel, LogConfiguration parent, ArrayList<LogAppender> appenders, boolean synchronousMode, boolean additivity) {
        this.identifier = identifier;
        this.additivity = additivity;
        this.assignedLogLevel = assignedLevel;
        this.appenders = appenders;
        this.synchronousMode = synchronousMode;
        this.parent = parent;
        this.effectiveLogLevel = (assignedLevel != null ? assignedLevel : (parent != null ? parent.effectiveLogLevel : LogLevel.INFO) );
    }

    @Override
    public void addChildren(LogConfiguration childConfiguration, boolean copyGrandChildren) {
        childConfiguration.setParent(this);
        childMap.put(childConfiguration.identifier, childConfiguration);
        LogConfiguration oldConfiguration = childMap.get(childConfiguration.identifier);
        if(oldConfiguration != null && copyGrandChildren && oldConfiguration.getChildrens() != null){
             for(LogConfiguration grandChildren : oldConfiguration.getChildrens()){
                childConfiguration.addChildren(grandChildren, copyGrandChildren);
             }
        }
    }

    @Override
    public LogConfiguration getChildren(String name) {
        return childMap.get(name);
    }

    @Override
    public Collection<LogConfiguration> getChildrens() { return childMap.values(); }

    @Override
    public  void setParent(LogConfiguration parent) {
        this.parent = parent;
    }

    @Override
    public String details() {
        StringBuffer details = new StringBuffer("\n");
        LogLevel assigned = assignedLogLevel;
        if(assigned != null) {
            details.append(assigned.description());
        }else{
            details.append("null");
        }
        details.append(" - ");
        details.append(effectiveLogLevel.description());
        details.append("-");
        details.append(fullName());

        for(Map.Entry<String, LogConfiguration> child : this.childMap.entrySet()) {
            details.append(child.getValue().details());
        }
        return details.toString();
    }
}
