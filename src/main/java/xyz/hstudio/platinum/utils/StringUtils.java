package xyz.hstudio.platinum.utils;

import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+.*[0-9]*");

    public static boolean isNumber(final String string) {
        return NUMBER_PATTERN.matcher(string).matches();
    }
}