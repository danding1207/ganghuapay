//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard.lib;

import android.content.Context;

import com.xt.bluecard.lib.util.LogUtil;

public class CardReader implements ICardReader, IConnectState {
    private BLEManage mBLEManage;
    public static final int ADPU_EXCHANGE_SUCESS = 0;
    public static final int ADPU_EXCHANGE_TIMEOUT = -1;
    public static final int ADPU_EXCHANGE_OTHER_ERROR = -9;

    public CardReader(Context context) {
        this.mBLEManage = new BLEManage(context);
    }

    public boolean init() {
        return this.mBLEManage.init();
    }

    public boolean openWithAddress(String address) {
        return this.mBLEManage.connectTo(address);
    }

    public void close() {
        this.mBLEManage.disConnect();
    }

    public boolean isOpened() {
        return this.mBLEManage.getState() == 2;
    }

    public String getBatteryLevel(long timeout) {
        return this.mBLEManage.getBatteryLevel(timeout * 1000L);
    }

    public long sendApdu(byte[] arrApdu, ByteResult byteData, ByteResult byteState, long timeout) {
        byte code = -9;
        if(this.mBLEManage != null && arrApdu != null) {
            try {
                byte[] e = new byte[1024];
                int len = this.mBLEManage.transCommand(arrApdu, arrApdu.length, e, timeout * 1000L);
                if(len > 0) {
                    byte[] r_result = new byte[len];
                    System.arraycopy(e, 0, r_result, 0, len);
                    if(len >= 2) {
                        byteState.byteArr = new byte[2];
                        System.arraycopy(r_result, len - 2, byteState.byteArr, 0, 2);
                        byteData.byteArr = new byte[len - 2];
                        System.arraycopy(r_result, 0, byteData.byteArr, 0, len - 2);
                    }

                    code = 0;
                } else {
                    code = -1;
                }
            } catch (Exception var10) {
                LogUtil.e("SendApdu", var10.getMessage());
            }
        }

        return (long)code;
    }
}
