package com.vascomouta.vmlogger.utils;


import java.util.regex.MatchResult;

public class OffsetBasedMatchResult {

    private MatchResult matchResult;
    private String input ;

    public OffsetBasedMatchResult(MatchResult matchResult, String input) {
        this.matchResult = matchResult;
        this.input = input;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}


