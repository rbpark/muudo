package io.muudo.common.io;

import org.apache.commons.lang3.tuple.Pair;

import java.util.UnknownFormatConversionException;

public class EncodingUtils {

    public static byte[] encodeIntegerToByteArray(int i) {
        byte[] array = new byte[4];
        array[0] = (byte)((i >> 24)& 0xFF);
        array[1] = (byte)((i >> 16)& 0xFF);
        array[2] = (byte)((i >> 8)& 0xFF);
        array[3] = (byte)(i& 0xFF);

        return array;
    }

    public static int decodeIntegerFromByteArray(byte[] b) {
        int i = b[0] << 24;
        i |= ((b[1]&0xFF) << 16);
        i |= ((b[2]&0xFF) << 8);
        i |= (b[3]&0xFF);

        return i;
    }

    /**
     * Encoding of variable integers (signed) that mimics http://lucene.apache.org/core/3_5_0/fileformats.html#VInt
     *
     * @param value The value to encode. Assumes positive number
     * @param buffer The byte array to add to. It must be at least offset + 5 in length.
     * @param offset The offset into the array.
     * @return The new offset position.
     */
    public static int encodeVariableUnsignedInt(int value, byte[] buffer, int offset) {
        // Unwound logic because it's faster than looping,
        // especially if we expect the values to be small. (or else why variable encode?)

        if (value < 128) {
            buffer[offset++] = (byte)value;
        }
        else if (value < 16384) {
            buffer[offset++] = (byte)((value >> 7) | 0x80);
            buffer[offset++] = (byte)(value & 0x7F);
        }
        else if (value < 2097152) {
            buffer[offset++] = (byte)(((value >> 14) & 0x7F) | 0x80);
            buffer[offset++] = (byte)(((value >> 7) & 0x7F) | 0x80);
            buffer[offset++] = (byte)(value & 0x7F);
        }
        else if (value < 268435456) {
            buffer[offset++] = (byte)(((value >> 21) & 0x7F) | 0x80);
            buffer[offset++] = (byte)(((value >> 14) & 0x7F) | 0x80);
            buffer[offset++] = (byte)(((value >> 7) & 0x7F) | 0x80);
            buffer[offset++] = (byte)(value & 0x7F);
        }
        else {
            buffer[offset++] = (byte)(((value >> 28) & 0x7F) | 0x80);
            buffer[offset++] = (byte)(((value >> 21) & 0x7F) | 0x80);
            buffer[offset++] = (byte)(((value >> 14) & 0x7F) | 0x80);
            buffer[offset++] = (byte)(((value >> 7) & 0x7F) | 0x80);
            buffer[offset++] = (byte)(value & 0x7F);
        }

        return offset;
    }

    /**
     * Decodes the variable signed int from the byte buffer given the offset.
     * It will throw an exception if the encoding is greater than 5 bytes.
     *
     * @param buffer The byte array containing the offset
     * @param offset The offset itself
     * @return The decoded value and the offset.
     */
    public static ValueAndOffset decodeVariableUnsignedInt(byte[] buffer, int offset) {
        int value = 0;

        // The following is the logic that is applied but unwound below. Looks like a lot of
        // copy and paste, but unwinding is faster and safer since we can only do this 4 times max.
        // I've left this in to show the logic.
        // do {
        //    value <<= 7;
        //    value |= buffer[offset] & 0x7F;
        // } while((buffer[offset++] & 0x80) != 0);

        // Value is < 128. 1 byte
        value |= buffer[offset] & 0x7F;
        if ((buffer[offset++] & 0x80) == 0) {
            return ValueAndOffset.of(value, offset);
        }

        // Value is < 16384. 2 bytes.
        value <<= 7;
        value |= buffer[offset] & 0x7F;
        if ((buffer[offset++] & 0x80) == 0) {
            return ValueAndOffset.of(value, offset);
        }

        // Value is < 2097152. 3 bytes.
        value <<= 7;
        value |= buffer[offset] & 0x7F;
        if ((buffer[offset++] & 0x80) == 0) {
            return ValueAndOffset.of(value, offset);
        }

        // Value is < 268435456. 4 bytes.
        value <<= 7;
        value |= buffer[offset] & 0x7F;
        if ((buffer[offset++] & 0x80) == 0) {
            return ValueAndOffset.of(value, offset);
        }

        // Value is less than max. 5 bytes.
        value <<= 7;
        value |= buffer[offset] & 0x7F;
        if ((buffer[offset++] & 0x80) == 0) {
            return ValueAndOffset.of(value, offset);
        }

        throw new UnknownFormatConversionException("Final byte of variable int is not correct.");
    }

    /**
     * Class to return value and offset.
     */
    public static class ValueAndOffset {
        private final int value;
        private final int offset;

        private ValueAndOffset(int value, int offset) {
            this.value = value;
            this.offset = offset;
        }

        public int getValue() {
            return value;
        }

        public int getOffset() {
            return offset;
        }

        public static ValueAndOffset of(int value, int offset) {
            return new ValueAndOffset(value, offset);
        }
    }
}
