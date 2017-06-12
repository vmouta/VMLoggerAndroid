package com.vascomouta.vmloggertest;

import com.vascomouta.vmlogger.Log;
import com.vascomouta.vmlogger.LogConfiguration;


public class AppLogger extends Log {



    public Log getLogger(String identifier){
        return super.getLogger(identifier);
    }


}
