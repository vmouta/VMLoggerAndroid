package com.vascomouta.vmlogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
public interface LogFactory {

    public LogConfiguration createLogger(String identifier, LogConfiguration parent, Map<String, LogAppender> allAppenders, HashMap<String, Object> configuration);

    public LogConfiguration createCleanLogger(String identifier, LogConfiguration parent, boolean synchronousMode);
}
