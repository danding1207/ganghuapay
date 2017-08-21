//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard.lib.util;

public class ProtocolFormat {
    public static final byte[] Protocol_CCID_TYPE = new byte[]{(byte)111};
    public static final int Protocol_DataLength = 4;
    public static final byte[] Protocol_Rfu = new byte[1];
    public static final byte[] Protocol_Serial = new byte[1];
    public static final byte[] Protocol_Wait = new byte[1];
    public static final byte[] Protocol_Subpackage = new byte[2];
    public static final int headLength = 10;
    private static final String TAG = "ProtocolFormat";
    public static final int Case_Error = -1;
    public static final int Case_Wait = 1;
    public static final int Case_Subpackage_First = 2;
    public static final int Case_Subpackage_End = 3;
    public static final int Case_Subpackage_Middle = 4;
    public static final int Case_Single = 0;
    public static final byte[] Protocol_Subpackage_Cmd_Surplus = new byte[]{(byte)111, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)2, (byte)0, (byte)0, (byte)16};

    public ProtocolFormat() {
    }

    public static byte[] pakaging(byte[] command) {
        try {
            int e = command.length;
            byte[] dataLength = new byte[]{(byte)(e & 255), (byte)(e >> 8 & 255), (byte)(e >> 16 & 255), (byte)(e >> 24 & 255)};
            byte[] head = new byte[]{Protocol_CCID_TYPE[0], dataLength[0], dataLength[1], dataLength[2], dataLength[3], Protocol_Rfu[0], Protocol_Serial[0], Protocol_Wait[0], Protocol_Subpackage[0], Protocol_Subpackage[1]};
            int totalLength = 10 + e;
            byte[] totalArray = new byte[totalLength];
            System.arraycopy(head, 0, totalArray, 0, head.length);
            System.arraycopy(command, 0, totalArray, head.length, e);
            return totalArray;
        } catch (Exception var6) {
            LogUtil.e("ProtocolFormat", "pakaing data error");
            var6.printStackTrace();
            return null;
        }
    }

    public static byte[] unpakaging(byte[] result) {
        try {
            if(result == null) {
                LogUtil.e("ProtocolFormat", "No data received");
                return null;
            } else {
                int e = result.length - 10;
                if(e < 0) {
                    LogUtil.e("ProtocolFormat", "is not CCID data, result length is" + result.length);
                    return null;
                } else {
                    byte[] responseDataArray = new byte[e];
                    System.arraycopy(result, 10, responseDataArray, 0, e);
                    return responseDataArray;
                }
            }
        } catch (Exception var3) {
            var3.printStackTrace();
            LogUtil.e("ProtocolFormat", "unpakaing data error");
            return null;
        }
    }

    public static int checkWaitTime(byte[] data) {
        return data != null && data.length >= 10?(data.length == 10 && data[7] == -128?Math.abs(data[8] & 255):-1):-1;
    }

    public static int checkPacketdata(byte[] data) {
        if(data != null && data.length >= 10) {
            if(data[9] == 0) {
                LogUtil.d("checkPacketdata", "Case_Single");
                return 0;
            } else if(data[9] == 1) {
                LogUtil.d("checkPacketdata", "Case_Subpackage_First");
                return 2;
            } else if(data[9] == 2) {
                LogUtil.d("checkPacketdata", "Case_Subpackage_End");
                return 3;
            } else if(data[9] == 3) {
                LogUtil.d("checkPacketdata", "Case_Subpackage_Middle");
                return 4;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}
