package dsb.sunny.utils;

import kotlin.text.Regex;

public class SunnyUtils {

    public static Regex CAPTAIN_NAME_REGEX = new Regex("\\[(.+)\\].*");

    public static Regex RELATIVE_TIME_REGEX = new Regex("(?<Quantity>\\d+)(?<Identifier>[wdhms])");

    public static Regex TIME_REGEX = new Regex("^\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}$");

    public static String singularReplace(int i, String singular, String pluralSuffix) {
        if (i == 1) {
            return singular;
        }
        return i + " " + pluralSuffix;
    }
}
