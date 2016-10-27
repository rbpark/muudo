package io.muudo.common.io;

import org.junit.Test;

import static org.junit.Assert.*;

public class EncodingUtilsTest {
    @Test
    public void testIntegerByteArrayEncoding() {
        byte[] expectedBuffer = {0x12, (byte)0xA4, 0x56, (byte)0xB8};
        int expectedInt = 0x12A456B8;

        byte[] buf = EncodingUtils.encodeIntegerToByteArray(expectedInt);
        assertArrayEquals(expectedBuffer, buf);

        int a = EncodingUtils.decodeIntegerFromByteArray(buf);
        assertEquals(expectedInt, a);
    }

    @Test
    public void testVariableIntegerEncoding() {
        byte[] buffer = new byte[6];

        // Test 1 byte
        testVariableSerde(0, buffer, 0, 1);
        testVariableSerde(1, buffer, 0, 1);
        testVariableSerde(127, buffer, 0, 1);
        // Test offset
        testVariableSerde(0, buffer, 5, 1);
        testVariableSerde(1, buffer, 5, 1);
        testVariableSerde(127, buffer, 5, 1);

        // Test 2 Byte
        testVariableSerde(128, buffer, 0, 2);
        testVariableSerde(256, buffer, 0, 2);
        testVariableSerde(16383, buffer, 0, 2);
        testVariableSerde(128, buffer, 4, 2);
        testVariableSerde(256, buffer, 4, 2);
        testVariableSerde(16383, buffer, 4, 2);

        // Test 3 Byte
        testVariableSerde(16384, buffer, 0, 3);
        testVariableSerde(16385, buffer, 0, 3);
        testVariableSerde(65535, buffer, 0, 3);
        testVariableSerde(65536, buffer, 0, 3);
        testVariableSerde(2097151, buffer, 0, 3);
        testVariableSerde(16384, buffer, 3, 3);
        testVariableSerde(16385, buffer, 3, 3);
        testVariableSerde(65535, buffer, 3, 3);
        testVariableSerde(65536, buffer, 3, 3);
        testVariableSerde(2097151, buffer, 3, 3);

        // Test 4 bytes
        testVariableSerde(2097152, buffer, 0, 4);
        testVariableSerde(2097153, buffer, 0, 4);
        testVariableSerde(16097153, buffer, 0, 4);
        testVariableSerde(268435455, buffer, 0, 4);
        testVariableSerde(2097152, buffer, 2, 4);
        testVariableSerde(2097153, buffer, 2, 4);
        testVariableSerde(16097153, buffer, 2, 4);
        testVariableSerde(268435455, buffer, 2, 4);

        // Test 5 bytes
        testVariableSerde(268435456, buffer, 0, 5);
        testVariableSerde(268435457, buffer, 0, 5);
        testVariableSerde(1000000000, buffer, 0, 5);
        testVariableSerde(2000000000, buffer, 0, 5);
        testVariableSerde(268435456, buffer, 1, 5);
        testVariableSerde(268435457, buffer, 1, 5);
        testVariableSerde(1000000000, buffer, 1, 5);
        testVariableSerde(2000000000, buffer, 1, 5);
    }

    public static void testVariableSerde(int value, byte[] buffer, int offset, int expectedSize) {
        int newOffset = EncodingUtils.encodeVariableUnsignedInt(value, buffer, offset);
        assertEquals(offset + expectedSize, newOffset);

        EncodingUtils.ValueAndOffset val = EncodingUtils.decodeVariableUnsignedInt(buffer, offset);
        assertEquals(offset + expectedSize, val.getOffset());
        assertEquals(value, val.getValue());
    }
}
