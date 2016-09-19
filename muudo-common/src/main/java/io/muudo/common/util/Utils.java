package io.muudo.common.util;

import java.io.File;

public final class Utils {
    /**
     * Sprintf static function so we can use more common C/C++ style commands.
     * Shorthand for String.format(String s, Object ... arg)
     *
     * @param format String format
     * @param arg Arguments for the string format
     * @return
     */
    public static String sprintf(String format, Object ... arg) {
        return String.format(format, arg);
    }

    /**
     * Prints formated string to standard out.
     * Shorthand for System.out.print(String.format(s, val));
     *
     * @param format String format
     * @param arg Arguments for the string format
     */
    public static void printf(String format, Object ... arg) {
        System.out.print(sprintf(format, arg));
    }

    /**
     * Prints formated string to standard out.
     * Shorthand for System.out.print(String.format(s, val));
     *
     * @param format String format
     */
    public static void printf(Object format) {
        System.out.print(format.toString());
    }

    /**
     * Returns true if and only if value >= lower and value <= upper. If lower is greater than upper, then this will
     * always return false;
     *
     * @param value The value to compare.
     * @param lower The lower bound inclusive.
     * @param upper The upper bound inclused.
     * @return true if value is between [lower, upper]
     */
    public static boolean between(int value, int lower, int upper) {
        return value >= lower && value <= upper;
    }

    /**
     * Harsh validator for is between. Will throw an IllegalArgumentException instead of return true or false.
     * This will throw an exception if value is less than lower or value is greater than upper.
     * It will also throw an exception if min > max.
     *
     * @param name The variable name. It is used the the exception error message.
     * @param value The value to compare.
     * @param lower The lower bound inclusive.
     * @param upper The upper bound inclusive.
     */
    public static void validateBetween(String name, int value, int lower, int upper) {
        if (lower > upper) {
            throw Except.newIllegalArgument("Value %s max of %d is less than min %d", name, upper, lower);
        }

        if (!between(value, lower, upper)) {
            throw Except.newIllegalArgument("Value %s is %d. Must be between %d and %d", name, value, lower, upper);
        }
    }

    /**
     * Validates that a file exists and returns the file. If not, it throws an exception.
     *
     * @param name The short name for what the file represents.
     * @param path The path for the file.
     */
    public static File validateFileExists(String name, String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw Except.newIllegalArgument("%s file does not exist on %s", name, file.getAbsolutePath());
        }

        return file;
    }

}
