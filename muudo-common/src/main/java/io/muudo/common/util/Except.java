package io.muudo.common.util;

import java.io.IOException;

/**
 * Helper class to make common exceptions easier to create with string formats
 */
public class Except {
    /**
     * Creates a new IllegalArgumentException using string format.
     * Short for new IllegalArgumentException(String.format(format, args), null)
     *
     * @param format The string format
     * @param args The args for the format
     * @return a new IllegalArgumentException
     */
    public static IllegalArgumentException newIllegalArgument(String format, Object ... args) {
        return new IllegalArgumentException(String.format(format, args), null);
    }

    /**
     * Creates a new IllegalArgumentException using string format.
     * Short for new IllegalArgumentException(String.format(format, args), t)
     *
     * @param t A throwable exception to wrap.
     * @param format The string format
     * @param args The args for the format
     * @return a new IllegalArgumentException
     */
    public static IllegalArgumentException newIllegalArgument(Throwable t, String format, Object ... args) {
        return new IllegalArgumentException(String.format(format, args), t);
    }

    /**
     * Creates a new IOException using string format.
     * Short for new IOException(String.format(format, args), null)
     *
     * @param format The string format
     * @param args The args for the format
     * @return a new IllegalArgumentException
     */
    public static IOException newIOException(String format, Object ... args) {
        return new IOException(String.format(format, args), null);
    }

    /**
     * Creates a new IOException using string format.
     * Short for new IOException(String.format(format, args), t)
     *
     * @param t A throwable exception to wrap.
     * @param format The string format
     * @param args The args for the format
     * @return a new IllegalArgumentException
     */
    public static IOException newIOException(Throwable t, String format, Object ... args) {
        return new IOException(String.format(format, args), t);
    }

}
