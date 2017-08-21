package com.xt.bluecard.lib.util;

public class DataTypeHelper {
    public DataTypeHelper() {
    }

    public static class BigEndian {
        public BigEndian() {
        }

        public static byte[] intToBytes(int value) {
            byte[] src = new byte[]{(byte) (value >> 24 & 255), (byte) (value >> 16 & 255), (byte) (value >> 8 & 255), (byte) (value & 255)};
            return src;
        }

        public static int bytesToInt(byte[] src, int offset) {
            int value = (src[offset] & 255) << 24 | (src[offset + 1] & 255) << 16 | (src[offset + 2] & 255) << 8 | src[offset + 3] & 255;
            return value;
        }

        public static byte[] shortToBytes(short value) {
            byte[] targets = new byte[2];

            for (int i = 0; i < 2; ++i) {
                targets[1] = (byte) (value & 255);
                targets[0] = (byte) (value >> 8 & 255);
            }

            return targets;
        }

        public static short bytesToShort(byte[] src) {
            boolean s = false;
            short s0 = (short) (src[0] & 255);
            short s1 = (short) (src[1] & 255);
            s0 = (short) (s0 << 8);
            s1 = (short) (s0 | s1);
            return s1;
        }
    }

    public static class LittleEndian {
        public LittleEndian() {
        }

        public static byte[] intToBytes(int value) {
            byte[] src = new byte[]{(byte) (value & 255), (byte) (value >> 8 & 255), (byte) (value >> 16 & 255), (byte) (value >> 24 & 255)};
            return src;
        }

        public static int bytesToInt(byte[] src, int offset) {
            int value = src[offset] & 255 | (src[offset + 1] & 255) << 8 | (src[offset + 2] & 255) << 16 | (src[offset + 3] & 255) << 24;
            return value;
        }
    }
}