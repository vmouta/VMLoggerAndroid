package com.vascomouta.vmlogger.implementation.appender;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFilter;
import com.vascomouta.vmlogger.LogFormatter;
import com.vascomouta.vmlogger.implementation.BaseLogAppender;
import com.vascomouta.vmlogger.utils.DispatchQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryName;

public class URLLogAppender extends BaseLogAppender {

    public interface URLLogAppenderService {
        @GET
        public Call<ResponseBody> getLog(
                @HeaderMap Map<String, String> headers,
                @Query("log") String options
        );

        @PUT
        public Call<ResponseBody> postLog(
                @HeaderMap Map<String, String> headers,
                @QueryName String options,
                @Body String body
        );
    }
    URLLogAppenderService service;

    public static String SERVERURL = "url";
    public static String HEADERS = "headers";
    public static String METHOD = "method";
    public static String PARAMETER = "parameter";

    public String url;

    public Map<String, String> headers;

    public String method;

    public String parameter;

    public URLLogAppender(){

    }

    /**
     * Attempts to initialize a new `UrlLogAppender` instance to use the
     * given file path and log formatters. This will fail if `filePath` could
     * not be opened for writing.
     * @param url
     * @param method
     * @param parameter
     * @param headers apiKey
     * @param formatters formatters The `LogFormatter`s to use for the recorder.
     */
    public URLLogAppender(String url, String method, String  parameter, HashMap<String, String> headers, ArrayList<LogFormatter> formatters) {
        this("URLLogRecorder[" + url + "]", url, method, parameter, headers, formatters, new ArrayList<LogFilter>());
    }

    /**
     * Attempts to initialize a new `FileLogRecorder` instance to use the
     * given file path and log formatters. This will fail if `filePath` could
     * not be opened for writing.
     * @param name
     * @param url
     * @param method
     * @param parameter
     * @param headers
     * @param formatters formatters The `LogFormatter`s to use for the recorder.
     * @param filters
     */
    public URLLogAppender(String name,String  url, String method,String  parameter, HashMap<String, String> headers, ArrayList<LogFormatter> formatters, ArrayList<LogFilter> filters)
    {
        super(name, formatters, filters);
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.parameter = parameter;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.url)
                .build();
        service = retrofit.create(URLLogAppenderService.class);
    }

    public URLLogAppender(HashMap<String, Object> configuration) {
        super(configuration);

        // URL
        url = (String)configuration.get(URLLogAppender.SERVERURL);

        // Headers
        headers = (HashMap<String, String>) configuration.get(URLLogAppender.HEADERS);

        // Method
        String method = (String) configuration.get(URLLogAppender.METHOD);
        if(method == null){
            this.method = "POST";
        } else {
            this.method = method;
        }

        // Parameters
        parameter = (String) configuration.get(URLLogAppender.PARAMETER);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.url)
                .build();
        service = retrofit.create(URLLogAppenderService.class);
    }

    /**
     * /**
     Called by the `LogReceptacle` to record the specified log message.

     **Note:** This function is only called if one of the `formatters`
     * associated with the receiver returned a non-`nil` string.
     * @param message message The message to record.
     * @param logEntry  entry The `LogEntry` for which `message` was created.
     * @param dispatchQueue currentQueue The GCD queue on which the function is being  executed.
     * @param synchronousMode synchronousMode If `true`, the receiver should record the
     * log entry synchronously. Synchronous mode is used during
     * debugging to help ensure that logs reflect the latest state
     * when debug breakpoints are hit. It is not recommended for production code.
     */
    @Override
    public void recordFormatterMessage(final String message, LogEntry logEntry, DispatchQueue dispatchQueue, boolean synchronousMode) {
        if(this.method == "POST") {
            service.postLog(this.headers, this.parameter, message);
        } else {
            service.getLog(this.headers, message);
        }
    }
}
