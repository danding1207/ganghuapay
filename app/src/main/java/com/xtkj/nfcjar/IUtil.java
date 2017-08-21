//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xtkj.nfcjar;

import android.annotation.SuppressLint;
import com.xtkj.nfcjar.bean.PriceGroup;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressLint({"DefaultLocale", "SimpleDateFormat"})
public class IUtil {
    public IUtil() {
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0 || str.toLowerCase().equals("null");
    }

    public static String getString(String str) {
        return str == null?"":str;
    }

    public static String byte2HexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if(src != null && src.length > 0) {
            for(int i = 0; i < src.length; ++i) {
                int v = src[i] & 255;
                String hv = Integer.toHexString(v);
                if(hv.length() == 1) {
                    hv = "0" + hv;
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
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

    public static String getReverse(String str) {
        StringBuffer buffer = new StringBuffer();
        int len = str.length();

        for(int i = len; i >= 2; i -= 2) {
            buffer.append(str.substring(i - 2, i));
        }

        return buffer.toString();
    }

    public static int HexToBCD(int data) {
        int temp = data / 10 * 16 + data % 10;
        return temp;
    }

    public static byte[] getBCD(String str) {
        int len = str.length();
        byte[] dest = new byte[len / 2];
        if(len % 2 != 0) {
            return dest;
        } else {
            for(int i = 0; i < dest.length; ++i) {
                dest[i] = (byte)HexToBCD(Integer.parseInt(str.substring(i * 2, i * 2 + 2)));
            }

            return dest;
        }
    }

    public static byte getSum(byte[] bs) {
        int sum = 0;
        byte[] var5 = bs;
        int var4 = bs.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            byte b = var5[var3];
            sum += b;
        }

        return (byte)(sum & 255);
    }

    public static byte[] mergeArrays(byte[] bs) {
        byte[] b3 = new byte[bs.length + 2];
        System.arraycopy(bs, 0, b3, 0, bs.length);
        b3[b3.length - 2] = getSum(b3);
        b3[b3.length - 1] = 22;
        byte[] cmd = new byte[5 + b3.length];
        cmd[0] = 32;
        cmd[1] = 48;
        cmd[2] = 0;
        cmd[3] = 0;
        cmd[4] = (byte)b3.length;

        for(int i = 0; i < b3.length; ++i) {
            cmd[5 + i] = b3[i];
        }

        return cmd;
    }

    public static byte[] getNumByte4(int num) {
        byte[] bot = new byte[]{(byte)(num & 255), (byte)(num >> 8 & 255), (byte)(num >> 16 & 255), (byte)(num >> 24 & 255)};
        return bot;
    }

    public static byte[] getNumByte3(int num) {
        byte[] bot = new byte[]{(byte)(num >> 16 & 255), (byte)(num >> 8 & 255), (byte)(num & 255)};
        return bot;
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

    public static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        return format.format(new Date());
    }

    public static String getNowDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
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

    public static boolean checkLegal(String time, String pattern) {
        try {
            SimpleDateFormat e = new SimpleDateFormat(pattern);
            Date date = e.parse(time);
            String newTime = e.format(date);
            return newTime.equals(time);
        } catch (ParseException var5) {
            var5.printStackTrace();
            return false;
        }
    }

    public static String getHex(int num, double res) {
        String result = "";
        byte[] bs = new byte[1];
        int in = (int)res;
        switch(num) {
        case 2:
            result = "0000";
            bs = new byte[]{(byte)(in >> 8 & 255), (byte)(in & 255)};
            result = byte2HexString(bs);
            break;
        case 3:
            result = "000000";
            bs = new byte[]{(byte)(in >> 16 & 255), (byte)(in >> 8 & 255), (byte)(in & 255)};
            result = byte2HexString(bs);
            break;
        case 4:
            result = "00000000";
            bs = new byte[]{(byte)(in >> 24 & 255), (byte)(in >> 16 & 255), (byte)(in >> 8 & 255), (byte)(in & 255)};
            result = byte2HexString(bs);
        }

        return result;
    }

    public static String getPriceGroupString(PriceGroup priceGroup) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getHex(2, (double)priceGroup.price1));
        buffer.append(getHex(3, (double)priceGroup.divideCount1));
        buffer.append(getHex(2, (double)priceGroup.price2));
        buffer.append(getHex(3, (double)priceGroup.divideCount2));
        buffer.append(getHex(2, (double)priceGroup.price3));
        buffer.append(getHex(3, (double)priceGroup.divideCount3));
        buffer.append(getHex(2, (double)priceGroup.price4));
        buffer.append(getHex(3, (double)priceGroup.divideCount4));
        buffer.append(getHex(2, (double)priceGroup.price5));
        return buffer.toString();
    }

    public static String getListString(List<String> list) {
        StringBuffer buffer = new StringBuffer();
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            String string = (String)var3.next();
            buffer.append(string);
        }

        return buffer.toString();
    }
}
