package com.vascomouta.vmlogger.webservice;


import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class Response {

    private int responseCode;
    private String responseString;
    private Map<String, List<String>> headerParams;

    public Response(int responseCode, String responseString) {
        super();
        this.responseCode = responseCode;
        this.responseString = responseString;
    }
    public Response(int responseCode, String responseString, Map<String, List<String>> headerParams) {
        super();
        this.responseCode = responseCode;
        this.responseString = responseString;
        this.headerParams = headerParams;
    }


}
