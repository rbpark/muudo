package io.muudo.common.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ExceptTest {
    @Test
    public void testNewIllegalArgument() {
        // Test regular exception
        IllegalArgumentException except =
                Except.newIllegalArgument("My name is %s %s. I am %d years old.", "Richard", "Park", 1);
        assertEquals("My name is Richard Park. I am 1 years old.", except.getMessage());

        // Test Throwable wrapped exception.
        IOException e = new IOException("EEK");
        IllegalArgumentException except2 =
                Except.newIllegalArgument(e, "My name is %s %s. I am %d years old.", "Richard", "Park", 2);
        assertEquals("My name is Richard Park. I am 2 years old.", except2.getMessage());
        assertEquals(e, except2.getCause());

    }

    @Test
    public void testNewIOException() {
        // Test regular exception
        IOException except =
                Except.newIOException("My name is %s %s. I am %d years old.", "Richard", "Park", 1);
        assertEquals("My name is Richard Park. I am 1 years old.", except.getMessage());

        // Test Throwable wrapped exception.
        IOException e = new IOException("EEK");
        IOException except2 =
                Except.newIOException(e, "My name is %s %s. I am %d years old.", "Richard", "Park", 2);
        assertEquals("My name is Richard Park. I am 2 years old.", except2.getMessage());
        assertEquals(e, except2.getCause());
    }

}
