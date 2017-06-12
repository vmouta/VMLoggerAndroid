package com.vascomouta.vmlogger.utils;

import java.util.Collection;
import java.util.Map;

public class ObjectType {

    public static String getType(Object object){
        if(object == null){
            return "Class";
        }else if(object.getClass().isArray()){
            return "Array";
        }else if(Collection.class.isAssignableFrom(object.getClass())){
            return "Collection";
        }else if(Map.class.isAssignableFrom(object.getClass())){
            return "Map";
        }
        return "Class";

    }


}
