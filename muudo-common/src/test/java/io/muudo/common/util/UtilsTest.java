package io.muudo.common.util;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class UtilsTest {
    @Test
    public void testSprintf() {
        String test = Utils.sprintf("my name is %s. I have %d quarters, so %.2f dollars", "Richard", 7, 1.75f);
        assertEquals("my name is Richard. I have 7 quarters, so 1.75 dollars", test);
    }

    @Test
    public void testPrintf() throws Exception {
        //*** Warning: Overridding System.out to a ByteArray
        PrintStream oldOs = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, "utf-8"));
        //***

        Utils.printf("my name is %s. I have %d quarters, so %.2f dollars", "Richard", 7, 1.75f);
        String output = outContent.toString("utf-8");

        //*** Resetting System.out.
        outContent.close();
        System.setOut(oldOs);
        //***

        assertEquals("my name is Richard. I have 7 quarters, so 1.75 dollars", output);
     }

    @Test
    public void testBetween() {
        assertTrue(Utils.between(1, 0, 2));
        assertFalse(Utils.between(-1, 0, 2));
        assertTrue(Utils.between(5, 5, 5));
        assertFalse(Utils.between(5, 7, 6));
        assertTrue(Utils.between(-6, -7, -5));
    }

    @Test
    public void testValidateBetween() {
        // Test regular passing
        try {
            Utils.validateBetween("TestParam", 1, 0, 2);
        }
        catch (Throwable t) {
            fail(t.getMessage());
        }

        // Test regular fail.
        try {
            Utils.validateBetween("TestParam", -1, 0, 2);
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("TestParam"));
            assertTrue(e.getMessage().contains("-1"));
            assertTrue(e.getMessage().contains("0"));
            assertTrue(e.getMessage().contains("2"));
        }
        catch (Throwable t) {
            fail();
        }

        // Test inclusive pass.
        try {
            Utils.validateBetween("TestParam", -1, -1, -1);
        }
        catch (Throwable t) {
            fail(t.getMessage());
        }

        // Test bad lower/upper fail.
        try {
            Utils.validateBetween("TestParam", 600, 800, 500);
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("TestParam"));
            assertTrue(e.getMessage().contains("800"));
            assertTrue(e.getMessage().contains("500"));
        }
        catch (Throwable t) {
            fail();
        }
    }

    @Test
    public void testValidateFileExists() {
        // This should exist so should pass just fine.
        File file = Utils.validateFileExists("MyDummyFile", "src/test/files/MyFile.dummy");
        assertNotNull(file);

        // Should throw an error since it does not exists
        try {
            File notFile = Utils.validateFileExists("DoesNotExist", "this/file/does/not/exist.txt");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("DoesNotExist"));
            assertTrue(e.getMessage().contains("DoesNotExist"));
        }
    }

}
