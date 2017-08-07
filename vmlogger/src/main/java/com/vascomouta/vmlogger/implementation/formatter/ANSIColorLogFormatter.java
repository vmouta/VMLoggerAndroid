package com.vascomouta.vmlogger.implementation.formatter;

import android.graphics.Color;

import com.vascomouta.vmlogger.LogEntry;
import com.vascomouta.vmlogger.LogLevel;
import com.vascomouta.vmlogger.implementation.BaseLogFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Asma on 05/06/17.
 */

public class ANSIColorLogFormatter extends BaseLogFormatter {

    /// ANSI Escape code
    public static String escape = "\\u{001b}[";

    /// ANSI Reset colours code
    public static String reset = escape+"m";

    public enum ANSIColor{
        BLACK("30", "40", "Black"),
        RED("31", "41", "Red"),
        GREEN("32", "42", "Green"),
        YELLOW("33", "43", "Yellow"),
        BLUE("34", "44", "Blue"),
        MAGENTA("35", "45", "Magenta"),
        CYAN("36", "46", "Cyan"),
        LIGHTGREY("37", "47", "LightGrey"),
        DARKGREY("90", "100", "DarkGrey"),
        LIGHTRED("91", "101", "LightRed"),
        LIGHTGREEN("92","102", "LightGreen"),
        LIGHTYELLOW("93", "103", "LightYellow"),
        LIGHTBLUE("94", "104", "LightBlue"),
        LIGHTMAGENTA("95", "105", "LightMagenta"),
        LIGHTCYAN("96", "106", "LightCyan"),
        WHITE("97", "107", "White"),
        DEFAULT("39", "49", "Default");

        public String foreground;
        public String background;

        ANSIColor(String foreground, String background, String description){
            this.foreground = foreground;
            this.background = background;
        }

        ANSIColor(int r, int g, int b, String description, String background){
            this("38;2;"+Math.min(Math.max(0, r), 255)+";"+Math.min(Math.max(0, g), 255)+";"+Math.min(Math.max(0, b), 255), background, description);
        }

        ANSIColor(String foreground, String description, int r, int g, int b){
            this(foreground, "48;2;"+Math.min(Math.max(0, r), 255)+";"+Math.min(Math.max(0, g), 255)+";"+Math.min(Math.max(0, b), 255), description);
        }

        ANSIColor(int r, int g, int b, String description, int br, int bg, int bb){
            this("38;2;"+Math.min(Math.max(0, r), 255)+";"+Math.min(Math.max(0, g), 255)+";"+Math.min(Math.max(0, b), 255),
                    "48;2;"+Math.min(Math.max(0, r), 255)+";"+Math.min(Math.max(0, g), 255)+";"+Math.min(Math.max(0, b), 255), description);
        }

        ANSIColor(int foregroundIndex, int backgroundIndex, String description){
            this("38;5;"+Math.min(Math.max(0, foregroundIndex), 255), "48;5;"+Math.min(Math.max(0, backgroundIndex), 255), description);
        }

        public String getForeground(){
            return foreground;
        }

        public String getBackground(){
            return background;
        }

    }


    public enum ANSIOptions{
         BOLD("1", "Bold"),
         FAINT("2", "Faint"),
         ITALIC("3", "Italic"),
         UNDERLINE("4", "Underline"),
         BLINK("5", "Blink"),
         BLINKFAST("6", "BlinkFast"),
         STRIKETHROUGH("9", "StrikeThrough");

        public String value;
        public  String label;

        ANSIOptions(String value, String label){
            this.value = value;
            this.label = label;
        }
    }

    /// Internal cache of the ANSI codes for each log level
    private HashMap<LogLevel, String> formatStrings = new HashMap<>();

    /// Internal cache of the description for each log level
    private HashMap<LogLevel, String> descriptionStrings = new HashMap<>();

    public ANSIColorLogFormatter(){
        super();
        resetFormatting();
    }

    ANSIColorLogFormatter(Map<String, Object> configuration) {
        this();
    }

    public void colorize(LogLevel level, ANSIColor foregroundColor,ANSIColor backgroundColor, ArrayList<ANSIOptions> options) {
        StringBuilder codes = new StringBuilder(String.valueOf(foregroundColor.getForeground()) +String.valueOf(backgroundColor.getBackground()));
        String description = foregroundColor +  "on" +  backgroundColor;

        for (ANSIOptions option : options) {
            codes.append(option.value);
            description += option;
        }
        //TODO add color code
        formatStrings.put(level, ANSIColorLogFormatter.escape + "" );
        descriptionStrings.put(level, description);
    }

    public void colorize(LogLevel level , String custom) {
        if (custom.startsWith(String.valueOf(ANSIColorLogFormatter.escape))) {
            formatStrings.put(level, custom);
           // descriptionStrings.put(level, "Custom: \(custom.substring(from: custom.index(custom.startIndex, offsetBy: 2)))");
        } else {
            formatStrings.put(level, ANSIColorLogFormatter.escape + custom);
            descriptionStrings.put(level, "Custom: " + custom);
        }
    }

    private String formatString(LogLevel level) {
        return (formatStrings.get(level) != null) ? formatStrings.get(level) : String.valueOf(ANSIColorLogFormatter.reset);
    }

    public void resetFormatting() {
        ArrayList<ANSIOptions> options = new ArrayList<>();
        options.add(ANSIOptions.BOLD);
        colorize(LogLevel.VERBOSE, ANSIColor.WHITE, ANSIColor.DEFAULT, options);
        colorize(LogLevel.DEBUG, ANSIColor.BLACK, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.INFO, ANSIColor.BLUE, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.WARNING, ANSIColor.YELLOW, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.ERROR, ANSIColor.RED, ANSIColor.DEFAULT,options);
        colorize(LogLevel.SEVERE, ANSIColor.WHITE, ANSIColor.RED, new ArrayList<ANSIOptions>());
        colorize(LogLevel.EVENT, ANSIColor.DEFAULT, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.OFF, ANSIColor.DEFAULT, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
    }


    public void clearFormatting() {
        colorize(LogLevel.VERBOSE, ANSIColor.DEFAULT, ANSIColor.DEFAULT , new ArrayList<ANSIOptions>());
        colorize(LogLevel.DEBUG, ANSIColor.DEFAULT, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.INFO, ANSIColor.DEFAULT, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.WARNING, ANSIColor.DEFAULT, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.ERROR, ANSIColor.DEFAULT, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.SEVERE, ANSIColor.DEFAULT, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.EVENT, ANSIColor.DEFAULT, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>());
        colorize(LogLevel.OFF,ANSIColor.DEFAULT, ANSIColor.DEFAULT, new ArrayList<ANSIOptions>() );
    }

    @Override
    public String formatLogEntry(LogEntry logEntry, String message) {
        return formatString(logEntry.logLevel) + message + ANSIColorLogFormatter.reset;
    }
}
