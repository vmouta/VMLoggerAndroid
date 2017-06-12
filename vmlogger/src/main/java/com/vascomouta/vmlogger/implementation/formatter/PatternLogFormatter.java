package com.vascomouta.vmlogger.implementation.formatter;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.constant.PatternLogFormatterConstants;
import com.vascomouta.vmlogger.implementation.BaseLogFormatter;
import com.vascomouta.vmlogger.utils.OffsetBasedMatchResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternLogFormatter  extends BaseLogFormatter{

    public static  String defaultLogFormat = "%.30d [%thread] %-7p %-20.-20c - %m";

    public static String lengthPattern = "([-]?\\d{1,2}[.][-]?\\d{1,2}|[.][-]?\\d{1,2}|[-]?\\d{1,2})";

    public static String MDC = "%" + lengthPattern + "?" + "(X)";
    public static String identifier = "%" + lengthPattern + "?" + "(logger|lo|c)";
    public static String level = "%" + lengthPattern + "?" + "(level|le|p)";
    public static String date = "%" + lengthPattern + "?" + "(date|d)";
    public static String message = "%" + lengthPattern + "?" + "(message|msg|m)";

    public static String thread = "%" + lengthPattern + "?" + "(thread|t)";

    public static String caller = "%" + lengthPattern + "?" + "(Caller)";
    public static String function = "%" + lengthPattern + "?" + "(M|Method)";
    public static String file = "%" + lengthPattern + "?" + "(F|file)";
    public static String line = "%" + lengthPattern + "?" + "(L|line)";
    public static String lineSeparator = "%n";

    public static String grouping = "%" + lengthPattern + "[(].{1,}[)]";

    private static String[] patterns = {MDC,identifier,level,date,message,thread,caller,function,file,line,lineSeparator};
    //private static let patterns: [String] = [date]

    String pattern;

    /**
     * Patterns
     * c -> Logger Pattern
     * C -> CLass Name Pattern
     * d -> DatePattern
     * F -> FileLocation
     * l -> FullLocation
     * L -> lineLocation
     * m -> Message
     * n -> LineSeparator
     * M -> MethodLocation
     * p -> LevelPattern
     * r -> RelativeTime Pattern
     * t -> Thread Pattern
     * x -> NDC  Pattern
     * X -> Properties Pattern
     * sn -> SequenceNumber
     * throwable -> ThrowableInformation
     * i -> IntegerPattern
     */


    public PatternLogFormatter(){

    }

    /**
     *Initializes the DefaultLogFormatter using the given settings.
     * @param pattern
     */
    public PatternLogFormatter(String pattern){
        this.pattern = pattern;
    }

    @Override
    public void init(Map<String, Object> configuration) {
        super.init(configuration);
        String pattern = (String)configuration.get(PatternLogFormatterConstants.Pattern);
                if(pattern == null){
                    return;
                }
        new PatternLogFormatter(pattern);
    }

    /**
     *
     * @param logEntry
     * @param message
     * @return
     */
    @Override
    public String formatLogEntry(LogEntry logEntry, String message) {
        super.formatLogEntry(logEntry, message);
        String resultString = pattern;
        Pattern regex = Pattern.compile(PatternLogFormatter.grouping);
        if(regex != null){
            ArrayList<MatchResult> allMatches = getAllMatches(regex, resultString);
            if(allMatches.size() > 0){
                for(MatchResult match : allMatches) {
                    String content = resultString.substring(match.start(), match.end());
                    String[] range = content.split("\\(");
                    int replacementRange = content.indexOf(range[0], content.length() - 1);
                    String subPattern = content.substring(replacementRange);
                    subPattern = patternReplacement(logEntry, message, subPattern);
                    subPattern = formatSpecifiers(content, subPattern);
                    resultString = resultString.replace(content, subPattern);
                }
            }
        }
        return  patternReplacement(logEntry, message, resultString);

    }


    /**
     *
     * @param pattern
     * @param regexString
     * @return
     */
    private ArrayList<MatchResult> getAllMatches(Pattern pattern, String regexString){
        Matcher matcher =  pattern.matcher(regexString);
        ArrayList<MatchResult> allMatches = new ArrayList<>();
        while (matcher.find()){
            MatchResult matchResult = matcher.toMatchResult();
            allMatches.add(matchResult);
        }
        return allMatches;
    }


    /**
     *
     * @param p
     * @param input
     * @return
     */
    private Iterable<OffsetBasedMatchResult> allMatches(final Pattern p, final CharSequence input) {
        return new Iterable<OffsetBasedMatchResult>() {
            public Iterator<OffsetBasedMatchResult> iterator() {
                return new Iterator<OffsetBasedMatchResult>() {
                    // Use a matcher internally.
                    final Matcher matcher = p.matcher(input);
                    // Keep a match around that supports any interleaving of hasNext/next calls.
                    MatchResult pending;

                    public boolean hasNext() {
                        // Lazily fill pending, and avoid calling find() multiple times if the
                        // clients call hasNext() repeatedly before sampling via next().
                        if (pending == null && matcher.find()) {
                            pending = matcher.toMatchResult();
                        }
                        return pending != null;
                    }

                    public OffsetBasedMatchResult next() {
                        // Fill pending if necessary (as when clients call next() without
                        // checking hasNext()), throw if not possible.
                        if (!hasNext()) { throw new NoSuchElementException(); }
                        // Consume pending so next call to hasNext() does a find().
                        MatchResult next = pending;
                        pending = null;
                        return new OffsetBasedMatchResult(next, p.pattern());
                    }

                    /** Required to satisfy the interface, but unsupported. */
                    public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }


    /**
     *
     * @param expression
     * @param replacement
     * @return
     */
    public String formatSpecifiers(String expression, String replacement) {
        String newReplacement = replacement;
        Pattern regex = Pattern.compile(PatternLogFormatter.lengthPattern);
        if (regex != null) {
            ArrayList<MatchResult> matches = getAllMatches(regex, expression);
            if (matches.size() > 0) {
                int min = 0;
                int max = 0;
                String specifier = expression.substring(matches.get(0).start(), matches.get(0).end());
                String[] values = specifier.split("\\.");
                if (values.length == 1) {
                    if (specifier.contains(".")) {
                        max = Integer.valueOf(values[0]);
                    } else {
                        min = Integer.valueOf(values[0]);
                    }
                } else if (values.length == 2) {
                    min = (values[0].length() != 0) ? Integer.valueOf(values[0]) : 0;
                    max = (values[1].length() != 0) ? Integer.valueOf(values[1]) : 0;
                }
                int minLength = min;
                if (minLength != 0 && newReplacement.length() < Math.abs(minLength)) {
                    int diff = Math.abs(minLength) - newReplacement.length();
                    for (int i = 1; i <= diff; i++) {
                        newReplacement = (minLength < 0) ? newReplacement += " " : newReplacement.concat(" ");
                    }
                }
                int maxLength = max;
                if (maxLength != 0 && newReplacement.length() > max) {
                    if (maxLength < 0) {
                        newReplacement = newReplacement.substring(0, Math.abs(maxLength));
                    } else {
                        newReplacement = newReplacement.substring(newReplacement.length() - maxLength -1, newReplacement.length());
                    }
                }
            }

        }
        return newReplacement;
    }


    /**
     *
     * @param entry
     * @param message
     * @param pattern
     * @return
     */
    public String patternReplacement(LogEntry entry, String message, String pattern){
        int offset = 0;
        TreeMap<Integer, OffsetBasedMatchResult> orderMatches = new TreeMap<>();
        String details = pattern;
        for(String pat : PatternLogFormatter.patterns) {
            Pattern regex = Pattern.compile(pat);
            if(regex != null){
                for(OffsetBasedMatchResult matchResult : allMatches(regex, details)){
                    orderMatches.put(matchResult.getMatchResult().start(), matchResult);
                }
            }
        }
        for(TreeMap.Entry<Integer, OffsetBasedMatchResult> match : orderMatches.entrySet()){
                String patternExpression = match.getValue().getInput();
                int[] range = {match.getValue().getMatchResult().start() + offset, match.getValue().getMatchResult().end() + offset};
                String replacement = "";
                if(patternExpression.equals(PatternLogFormatter.MDC)){
                    replacement = BaseLogFormatter.stringRepresentationForMDC();
                }else if(patternExpression.equals(PatternLogFormatter.identifier)){
                    replacement = stringRepresentationOfIdentity(entry.logger.identifier);
                }else if(patternExpression.equals(PatternLogFormatter.level)){
                    replacement = stringRepresentationOfSeverity(entry.logLevel);
                }else if(patternExpression.equals(PatternLogFormatter.date)){
                    replacement = stringRepresentationOfTimestamp(entry.timestamp);
                }else if(patternExpression.equals(PatternLogFormatter.message)){
                    replacement = message;
                }else if(patternExpression.equals(PatternLogFormatter.thread)){
                    int threadID = 0;
                    //TODO
                    // pthread_threadid_np(nil, &threadID)
                    replacement = String.valueOf(threadID);
                }else if(patternExpression.equals(PatternLogFormatter.caller)){
                    replacement = String.valueOf(entry.callingThreadID);
                }else if(patternExpression.equals(PatternLogFormatter.function)){
                    replacement = entry.callingFunction;
                }else if(patternExpression.equals(PatternLogFormatter.file)){
                    replacement = BaseLogFormatter.stringRepresentationForFile(entry.callingFilePath);
                }else if(patternExpression.equals(PatternLogFormatter.line)){
                    replacement = String.valueOf(entry.callingFileLine);
                }else if(patternExpression.equals(PatternLogFormatter.lineSeparator)){
                    replacement = "\n";
                }

            String expression = details.substring(range[0], range[1]);
            replacement = formatSpecifiers(expression, replacement);
            details = details.replace(expression, replacement);
            offset += (replacement.length() - (range[1] - range[0]));
        }
        return details;
    }



    public static String getCaller(LogEntry entry){
        String caller = "";
        caller +=  entry.callingFunction ;
        caller += "(" + entry.callingFileLine + ":";
        caller +=  entry.callingFileLine + ")";
        return caller;
    }




}
