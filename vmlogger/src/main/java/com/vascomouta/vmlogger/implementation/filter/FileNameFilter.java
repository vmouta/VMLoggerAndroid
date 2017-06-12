package com.vascomouta.vmlogger.implementation.filter;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogFilter;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class FileNameFilter implements LogFilter {


    // Option to toggle the match results
    private  boolean inverse = false;

    // Option to match full path or just the fileName
    private boolean excludePath = true;

    // Internal list of fileNames to match against
    private Set<String> fileNamesToMatch = new HashSet<>();

    FileNameFilter(){}


    /**
     * Initializer to create an inclusion list of fileNames to match against
     * Note: Only log messages from the specified files will be logged, all others will be excluded
     * @param fileNames Set or Array of fileNames to match against.
     * @param excludePathWhenMatching  Whether or not to ignore the path for matches. **Default: true
     * @param inverse
     */
    public FileNameFilter(Set<String> fileNames, boolean excludePathWhenMatching, boolean inverse){
        this.inverse = inverse;
        this.excludePath = excludePathWhenMatching;
        this.fileNamesToMatch = fileNames;
    }


    /**
     *
     * @param fileName Name of the file to match against.
     * @return true: FileName added. false:    FileName already added.
     */
    public boolean add(String fileName){
        String fn;
        if(excludePath){
            String[] components = fileName.split("/");
            fn = components[components.length - 1];
        }else{
            fn = fileName;
        }
        return fileNamesToMatch.add(fn);

    }


    /**
     *  Add a list of fileNames to the list of names to match against.
     * @param fileNames Set or Array of fileNames to match against.
     */
    public void add(Set<String> fileNames){
        if(fileNames != null){
            fileNamesToMatch.addAll(fileNames);
        }
    }

    @Override
    public LogFilter init(HashMap<String, Object> configuration) {
        return null;
    }

    @Override
    public boolean shouldRecordLogEntry(LogEntry logEntry) {
        String file = logEntry.callingFilePath;
        if(excludePath) {
            String[] components = file.split("/");
            file = components[components.length -1];
        }
        boolean matched = fileNamesToMatch.contains(file);
        if(inverse) {
            matched = !matched;
        }
        return matched;
    }

    public void clear(){
        fileNamesToMatch.clear();
    }

    //TODO need to change description
    @Override
    public String toString() {
        return super.toString();
    }
}
