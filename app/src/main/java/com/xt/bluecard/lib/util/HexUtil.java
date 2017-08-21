//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard.lib.util;

public class HexUtil {
    private static final char[] _hexcodes = "0123456789ABCDEF".toCharArray();
    private static final int[] _shifts = new int[]{60, 56, 52, 48, 44, 40, 36, 32, 28, 24, 20, 16, 12, 8, 4, 0};

    public HexUtil() {
    }

    public static String toHex(long value, int digits) {
        StringBuffer result = new StringBuffer(digits);

        for(int j = 0; j < digits; ++j) {
            result.append(_hexcodes[(int)(value >> _shifts[j + (16 - digits)] & 15L)]);
        }

        return result.toString();
    }
}
