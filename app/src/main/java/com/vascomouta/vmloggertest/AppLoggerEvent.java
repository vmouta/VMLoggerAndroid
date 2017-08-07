package com.vascomouta.vmloggertest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
class AppLoggerEvent {

    public static String GROUP = "Group";
    public static String TYPE = "Type";
    public static String PARAMS = "Params";

    public static String ERROR = "Error";

    public static String EVENT = "Event";
    public static String LOG = "Log";
    public static String UI = "UI";
    public static String UI_SCREENDISPLAY = "Display";
    public static String UI_SCREENDISPLAYDURATION = "DisplayDuration";

    public static String NAME = "name";
    public static String LABEL = "label";
    public static String VALUE = "value";

    public static String CLASS = "class";

    protected Map<String, Object> requestValues = new HashMap<String, Object>();

    public static AppLoggerEvent createViewEvent(String name) {
        return createEvent(AppLoggerEvent.UI, UI_SCREENDISPLAY, name, null);
    }

    public static AppLoggerEvent createViewEvent(String name, Map<String, Object> params)  {
        AppLoggerEvent appEvent = createEvent(AppLoggerEvent.UI, UI_SCREENDISPLAY, params);
        appEvent.set(name, AppLoggerEvent.LABEL);
        return appEvent;
    }

    public static AppLoggerEvent createViewDurationEvent(String name, Double duration) {
        return createEvent(AppLoggerEvent.UI, UI_SCREENDISPLAYDURATION, name, String.valueOf(duration));
    }

    public static AppLoggerEvent createViewDurationEvent(String name, Double duration,  Map<String, Object> params) {
        AppLoggerEvent appEvent = createEvent(AppLoggerEvent.UI, UI_SCREENDISPLAYDURATION, params);
        appEvent.set(name, AppLoggerEvent.LABEL);
        appEvent.set(String.valueOf(duration), AppLoggerEvent.VALUE);
        return appEvent;
    }

    /**
     Returns a GAIDictionaryBuilder object with parameters specific to an event hit.
     :
     - parameter category: <#category description#>
     - parameter action:   <#action description#>
     - parameter label:    <#label description#>
     - parameter value:    <#value description#>
     */
    public static AppLoggerEvent createEvent(String category, String action, String label, String value) {
        return new AppLoggerEvent(category, action, label, value);
    }

    public static AppLoggerEvent createEvent(String group, String type, Map<String, Object> params) {
        return new AppLoggerEvent(group, type, params);
    }

    AppLoggerEvent() {
        requestValues.put(PARAMS, new HashMap<String, Object>());
    }

    AppLoggerEvent(String group, String type, Map<String, Object> params) {
        requestValues.put(PARAMS, new HashMap<String, Object>());
        requestValues.put(AppLoggerEvent.GROUP, group);
        requestValues.put(AppLoggerEvent.TYPE, type);
        setAllParams(params);
    }

    AppLoggerEvent(String category, String action, String label, String value) {
        requestValues.put(PARAMS, new HashMap<String, Object>());
        requestValues.put(AppLoggerEvent.GROUP, category);
        requestValues.put(AppLoggerEvent.TYPE, action);
        if(label != null) {
            set(label, AppLoggerEvent.LABEL);
        }

        if(value != null) {
            set(value, AppLoggerEvent.VALUE);
        }
    }

    public boolean set(String value, String key) {
        Object paramsDic = requestValues.get(AppLoggerEvent.PARAMS);
        if(paramsDic != null) {
            ((Map<String, Object>) paramsDic).put(key, value);
            setAllParams(((Map<String, Object>) paramsDic));
            return true;
        }
        return false;
    }

    /*!
     * Copies all the name-value pairs from params into this object, ignoring any
     * keys that are not NSString and any values that are neither NSString or
     * NSNull.
     */
    public void setAll(Map<String, Object> values) {
        requestValues = values;
    }

    /*!
     * Copies all the name-value pairs from params into this object, ignoring any
     * keys that are not NSString and any values that are neither NSString or
     * NSNull.
     */
    public void setAllParams(Map<String, Object> values) {
        requestValues.put(AppLoggerEvent.PARAMS, values);
    }

    /*!
     * Returns the value for the input parameter paramName, or nil if paramName
     * is not present.
     */
    public String get(String paramName) {
        Object value = requestValues.get(paramName);
        if(value != null && value instanceof String) {
            return (String)value;
        } else {
            Object params = requestValues.get(AppLoggerEvent.PARAMS);
            if (params != null) {
                value = requestValues.get(paramName);
                if(value != null && value instanceof String) {
                    return (String)value;
                }
            }
        }
        return null;
    }

    /*!
     * Return an NSMutableDictionary object with all the parameters set in this
     */
    public Map<String, Object> build() {
        return requestValues;
    }

    public String customDescription() {
        Gson gson = new GsonBuilder().create();
        String gsonString = gson.toJson(this.build());
        if (gsonString != null) {
            try {
                final byte[] gsonBytes = gsonString.getBytes("UTF8");
                return new String(gsonBytes, "UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "wrong json format";
    }

    @Override
    public String toString() {
        return customDescription();
    }
}
