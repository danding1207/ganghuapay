//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard.lib.util;

public class HexDump {
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public HexDump() {
    }

    public static String dumpHexString(byte[] array) {
        return dumpHexString(array, 0, array.length);
    }

    public static String dumpHexString(byte[] array, int offset, int length) {
        StringBuilder result = new StringBuilder();
        byte[] line = new byte[16];
        int lineIndex = 0;
        result.append("\n0x");
        result.append(toHexString(offset));

        int count;
        int i;
        for(count = offset; count < offset + length; ++count) {
            if(lineIndex == 16) {
                result.append(" ");
                i = 0;

                while(true) {
                    if(i >= 16) {
                        result.append("\n0x");
                        result.append(toHexString(count));
                        lineIndex = 0;
                        break;
                    }

                    if(line[i] > 32 && line[i] < 126) {
                        result.append(new String(line, i, 1));
                    } else {
                        result.append(".");
                    }

                    ++i;
                }
            }

            byte var8 = array[count];
            result.append(" ");
            result.append(HEX_DIGITS[var8 >>> 4 & 15]);
            result.append(HEX_DIGITS[var8 & 15]);
            line[lineIndex++] = var8;
        }

        if(lineIndex != 16) {
            count = (16 - lineIndex) * 3;
            ++count;

            for(i = 0; i < count; ++i) {
                result.append(" ");
            }

            for(i = 0; i < lineIndex; ++i) {
                if(line[i] > 32 && line[i] < 126) {
                    result.append(new String(line, i, 1));
                } else {
                    result.append(".");
                }
            }
        }

        return result.toString();
    }

    public static String toHexString(byte b) {
        return toHexString(toByteArray(b));
    }

    public static String toHexString(byte[] array) {
        return toHexString(array, 0, array.length);
    }

    public static String toHexString(byte[] array, int offset, int length) {
        char[] buf = new char[length * 2];
        int bufIndex = 0;

        for(int i = offset; i < offset + length; ++i) {
            byte b = array[i];
            buf[bufIndex++] = HEX_DIGITS[b >>> 4 & 15];
            buf[bufIndex++] = HEX_DIGITS[b & 15];
        }

        return new String(buf);
    }

    public static String toHexString(int i) {
        return toHexString(toByteArray(i));
    }

    public static byte[] toByteArray(byte b) {
        byte[] array = new byte[]{b};
        return array;
    }

    public static byte[] toByteArray(int i) {
        byte[] array = new byte[]{(byte)(i >> 24 & 255), (byte)(i >> 16 & 255), (byte)(i >> 8 & 255), (byte)(i & 255)};
        return array;
    }

    private static int toByte(char c) {
        if(c >= 48 && c <= 57) {
            return c - 48;
        } else if(c >= 65 && c <= 70) {
            return c - 65 + 10;
        } else if(c >= 97 && c <= 102) {
            return c - 97 + 10;
        } else {
            throw new RuntimeException("Invalid hex char \'" + c + "\'");
        }
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] buffer = new byte[length / 2];

        for(int i = 0; i < length; i += 2) {
            buffer[i / 2] = (byte)(toByte(hexString.charAt(i)) << 4 | toByte(hexString.charAt(i + 1)));
        }

        return buffer;
    }

    public static String toAmountString(float value) {
        return String.format("%.2f", new Object[]{Float.valueOf(value)});
    }

    public static int CRCB(byte[] Indata) {
        int y = '\uffff';

        for(int i = 0; i < Indata.length; ++i) {
            int x = Indata[i];

            for(int j = 0; j < 8; ++j) {
                char yy;
                if(((y ^ x) & 1) != 0) {
                    yy = '萈';
                } else {
                    yy = 0;
                }

                x >>= 1;
                y >>= 1;
                y ^= yy;
            }
        }

        return ~y & '\uffff';
    }

    public static byte[] xorByteArray(byte[] source) throws Exception {
        byte[] tmp = new byte[source.length];
        System.arraycopy(source, 0, tmp, 0, source.length);

        for(int i = 0; i < tmp.length - 1; ++i) {
            tmp[i + 1] ^= tmp[i];
        }

        return new byte[]{tmp[tmp.length - 1]};
    }
}
