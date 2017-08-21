//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard;

import android.annotation.SuppressLint;

import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class Utils {
    public Utils() {
    }

    public static String byte2HexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        byte[] var5 = data;
        int var4 = data.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            byte b = var5[var3];
            String hexStr = Integer.toHexString(b & 255);
            if(hexStr.length() == 1) {
                hexStr = "0" + hexStr;
            }

            sb.append(hexStr);
        }

        return sb.toString();
    }

    public static byte[] hexToByte(String hexStr) {
        if(hexStr.length() < 1) {
            return null;
        } else {
            byte[] result = new byte[hexStr.length() / 2];

            for(int i = 0; i < hexStr.length() / 2; ++i) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte)(high * 16 + low);
            }

            return result;
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0 || str.toLowerCase().equals("null");
    }

    public static String getString(String str) {
        return str == null?"":str;
    }

    public static long getDayGap(String s1, String s2) {
        try {
            SimpleDateFormat e = new SimpleDateFormat("yyMMdd");
            Date d1 = e.parse(s1);
            Date d2 = e.parse(s2);
            long time = d2.getTime() - d1.getTime();
            return time;
        } catch (ParseException var7) {
            var7.printStackTrace();
            return 0L;
        }
    }

    public static String getNowDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        return dateFormat.format(new Date());
    }

    public static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }

    public static String getNewValidTime(String s1, long time) {
        try {
            SimpleDateFormat e = new SimpleDateFormat("yyMMdd");
            Date d1 = e.parse(s1);
            long newTime = d1.getTime() + time;
            Date date = new Date(newTime);
            return e.format(date);
        } catch (ParseException var8) {
            var8.printStackTrace();
            return "";
        }
    }

    public static String getHexMoney(int money) {
        StringBuffer buffer = new StringBuffer();
        String string = Integer.toHexString(money);
        int length = string.length();
        if(length < 8) {
            for(int i = 0; i < 8 - length; ++i) {
                buffer.append("0");
            }
        }

        buffer.append(string);
        return buffer.toString();
    }

    public static String getMapValue(String s, String key) {
        HashMap map = new HashMap();
        if(s.contains(",")) {
            String[] split = s.split(",");
            String[] var7 = split;
            int var6 = split.length;

            for(int var5 = 0; var5 < var6; ++var5) {
                String string = var7[var5];
                if(string.contains("=")) {
                    String[] va = string.split("=");
                    map.put(va[0], va[1]);
                } else {
                    map.put(string, "");
                }
            }
        }

        return getString((String)map.get(key));
    }

    public static String getNegativeNum(String s) {
        int a = Integer.parseInt(s.substring(0, 2), 16);
        int b = Integer.parseInt(s.substring(2, 4), 16);
        int c = Integer.parseInt(s.substring(4, 6), 16);
        int d = Integer.parseInt(s.substring(6), 16);
        int e = a << 24;
        int f = b << 16;
        int g = c << 8;
        return (new DecimalFormat("#")).format((long)(e + f + g + d));
    }

    public static String getPositiveNum(String s) {
        return (new DecimalFormat("#")).format(Long.parseLong(s, 16));
    }



    public static String getReverse(String str) {
        StringBuffer buffer = new StringBuffer();
        Logger.t("CardManager").e(str);
        if (str.length() > 8) {
            str = str.substring(str.length() - 8, str.length());
        }
        Logger.t("CardManager").e(str);
        for (int i = str.length(); i >= 2; i -= 2) {
            buffer.append(str.substring(i - 2, i));
        }
        Logger.t("CardManager").e(buffer.toString());
        return buffer.toString();
    }

    public static String createRanHex8() {
        byte[] b = new byte[8];
        Random random = new Random();

        for(int string = 0; string < 8; ++string) {
            int nextInt = random.nextInt(256);
            b[string] = (byte)(nextInt & 255);
        }

        String var4 = byte2HexString(b);
        return var4;
    }

    public static String getSerial(String str) {
        return str.length() < 8?"":getReverse(str.substring(str.length() - 8));
    }
}
