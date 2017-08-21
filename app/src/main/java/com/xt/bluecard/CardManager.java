//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import com.decard.cardreader.toad.lib.ByteResult;
import com.decard.cardreader.toad.lib.CardReader;
import com.decard.cardreader.toad.lib.util.HexDump;
import com.xt.bluecard.CardInfo;
import com.xt.bluecard.DataParse;
import com.xt.bluecard.PriceGroup;
import com.xt.bluecard.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint({"SimpleDateFormat"})
public class CardManager {
    private String tag = "TAG";
    public static final int ADPU_EXCHANGE_SUCESS = 0;
    public static final int ADPU_EXCHANGE_TIMEOUT = -1;
    public static final int ADPU_EXCHANGE_OTHER_ERROR = -9;
    public static final int BASE = 85;
    public static final int GET_SERIAL = 86;
    public static final int IP_PORT_ERROR = 87;
    private CardReader cardReader;
    private long timeOut = 3L;
    public static String returnData;
    private OutputStream os;
    private InputStream is;
    private Socket socket;
    private int cardVerson = 1;

    public CardManager(Context mContext) {
        this.cardReader = new CardReader(mContext);
        this.cardReader.init();
    }

    public boolean connectBlE(String address) {
        return this.cardReader.openWithAddress(address);
    }

    public boolean isBlEConnected() {
        return this.cardReader.isOpened();
    }

    public void disConnectBlE() {
        this.cardReader.close();
    }

    public String[] powerOn() {
        String reuslt = "";
        String errorCode = "";
        byte[] cmd = new byte[]{(byte)-1, (byte)112, (byte)2, (byte)0, (byte)2, (byte)0, (byte)2};
        ByteResult recData = new ByteResult();
        ByteResult recSate = new ByteResult();
        long flag = this.cardReader.sendApdu(cmd, recData, recSate, 3L);
        switch((int)flag) {
            case -9:
                reuslt = "0";
                errorCode = "other";
                break;
            case -1:
                reuslt = "0";
                errorCode = "timeout";
                break;
            case 0:
                String resultStrState = recSate.byteArr != null?HexDump.toHexString(recSate.byteArr):"";
                if(resultStrState.equals("9000")) {
                    reuslt = "1";
                } else {
                    reuslt = "0";
                    errorCode = resultStrState;
                }
        }

        return new String[]{reuslt, errorCode};
    }

    public String[] powerOff() {
        String reuslt = "";
        String errorCode = "";
        byte[] cmd = new byte[]{(byte)-1, (byte)112, (byte)3, (byte)0, (byte)2, (byte)0, (byte)2};
        ByteResult recData = new ByteResult();
        ByteResult recSate = new ByteResult();
        long flag = this.cardReader.sendApdu(cmd, recData, recSate, 3L);
        switch((int)flag) {
            case -9:
                reuslt = "0";
                errorCode = "other";
                break;
            case -1:
                reuslt = "0";
                errorCode = "timeout";
                break;
            case 0:
                String resultStrState = recSate.byteArr != null?HexDump.toHexString(recSate.byteArr):"";
                if(resultStrState.equals("9000")) {
                    reuslt = "1";
                } else {
                    reuslt = "0";
                    errorCode = resultStrState;
                }
        }

        return new String[]{reuslt, errorCode};
    }

    public CardInfo readCard() {
        String appLen = "00C8";
        String returnLen = "00C8";
        String[] data = this.sendToCard("00A40000023F00");
        Log.e(this.tag, "进入目录（00）==" + data[1]);
        if(!data[0].equals("1")) {
            return new CardInfo("0", data[1]);
        } else {
            CardInfo cardInfo = new CardInfo();
            data = this.sendToCard("00B095003C");
            Log.e(this.tag, "读二进制文件==" + data[1]);
            if(!data[0].equals("1")) {
                return new CardInfo("0", data[1]);
            } else {
                this.parseCommonInfo(cardInfo, data[1]);
                data = this.sendToCard("00A40000023F02");
                if(!data[0].equals("1")) {
                    return new CardInfo("0", data[1]);
                } else {
                    Log.e(this.tag, "进入目录（02）==" + data[1]);
                    data = this.sendToCard("805C000204");
                    if(!data[0].equals("1")) {
                        return new CardInfo("0", data[1]);
                    } else {
                        cardInfo.thisMoney = Utils.getPositiveNum(data[1].substring(0, 8));
                        Log.e(this.tag, "读钱包数据==" + data[1] + "===" + cardInfo.thisMoney);
                        data = this.sendToCard("00B0990001");
                        Log.e(this.tag, data[0] + "===卡中标志信息文件==" + data[1]);
                        if(data[0].equals("1") && !Utils.isNullOrEmpty(data[1])) {
                            this.cardVerson = 2;
                            appLen = "00D3";
                            returnLen = "00E9";
                        }

                        data = this.sendToCard("00B081" + appLen);
                        if(!data[0].equals("1")) {
                            return new CardInfo("0", data[1]);
                        } else {
                            Log.e(this.tag, "应用指令==" + "00B081" + appLen);
                            Log.e(this.tag, "读用户应用信息文件==" + data[1]);
                            this.parseAppliInfo(cardInfo, data[1], appLen);
                            data = this.sendToCard("00B083" + returnLen);
                            Log.e(this.tag, "反馈指令==" + "00B083" + returnLen);
                            Log.e(this.tag, "燃气表回馈信息文件==" + data[1]);
                            this.parseReturnData(cardInfo, data[1], returnLen);
                            cardInfo.result = "1";
                            return cardInfo;
                        }
                    }
                }
            }
        }
    }

    private void parseReturnData(CardInfo cardInfo, String string, String returnLen) {
        try {
            cardInfo.nowTime = string.substring(0, 14);
            cardInfo.nowPrice = Integer.parseInt(string.substring(14, 18), 16);
            cardInfo.nowRemainMoney = Utils.getNegativeNum(string.substring(18, 26));
            cardInfo.toalBuyMoney = Utils.getPositiveNum(string.substring(26, 34));
            cardInfo.toalBuyMoney = Utils.getPositiveNum(string.substring(26, 34));
            cardInfo.toatalUseGas = Utils.getPositiveNum(string.substring(34, 42));
            cardInfo.noUseDayCount = Integer.parseInt(string.substring(42, 44), 16);
            cardInfo.noUseSecondsCount = Integer.parseInt(string.substring(44, 48), 16);
            cardInfo.nowStatus = string.substring(48, 52);
            cardInfo.dealWords = string.substring(52, 54);
            String e = string.substring(54, 246);
            cardInfo.monthUseList = this.getUseList(e);
            cardInfo.securityCounts = Integer.parseInt(string.substring(246, 248), 16);
            cardInfo.securityRecord = this.getRecordList(string.substring(248, 284));
            cardInfo.recentClose = Integer.parseInt(string.substring(284, 286), 16);
            cardInfo.nfcTimes = Integer.parseInt(string.substring(286, 288), 16);
            cardInfo.nfcMoney = Utils.getPositiveNum(string.substring(288, 296));
            cardInfo.nfcTotalMoney = Utils.getPositiveNum(string.substring(296, 304));
            cardInfo.checkSum3 = string.substring(304, 306);
            if(returnLen.equals("00E9")) {
                ArrayList list = new ArrayList();
                String date = string.substring(306, 402);
                int i = 0;

                while(true) {
                    if(i >= date.length()) {
                        cardInfo.historyMonthList = list;
                        cardInfo.addjustDate = string.substring(402, 408);
                        cardInfo.addjustBottom = Utils.getPositiveNum(string.substring(408, 416));
                        cardInfo.payDate = string.substring(416, 422);
                        cardInfo.payBottom = Utils.getPositiveNum(string.substring(422, 430));
                        cardInfo.backupData2 = string.substring(430, 462);
                        cardInfo.extSum2 = string.substring(462, 464);
                        break;
                    }

                    list.add(date.substring(i, i + 4));
                    i += 4;
                }
            }

            returnData = string;
        } catch (NumberFormatException var8) {
            Log.e(this.tag, "解析异常parseReturnData");
            var8.printStackTrace();
        }

    }

    public List<String> getUseList(String str) {
        ArrayList list = new ArrayList();

        for(int i = 0; i < str.length(); i += 8) {
            list.add(Utils.getPositiveNum(str.substring(i, i + 8)));
        }

        return list;
    }

    public List<String> getRecordList(String str) {
        ArrayList list = new ArrayList();

        for(int i = 0; i < str.length(); i += 12) {
            list.add(str.substring(i, i + 12));
        }

        return list;
    }

    private void parseAppliInfo(CardInfo cardInfo, String string, String len) {
        try {
            cardInfo.cardType = DataParse.getCardType(string.substring(0, 2));
            cardInfo.userFlag = Integer.parseInt(string.substring(2, 4), 16);
            cardInfo.paramModifyFlag = Integer.parseInt(string.substring(4, 6), 16);
            cardInfo.keyVerson = Integer.parseInt(string.substring(6, 8), 16);
            cardInfo.buyTime = string.substring(8, 14);
            cardInfo.validTime = string.substring(14, 20);
            cardInfo.userCode = string.substring(20, 40);
            cardInfo.userType = string.substring(40, 42);
            cardInfo.buyTimes = Integer.parseInt(string.substring(42, 44), 16);
            cardInfo.leakFunction = Integer.parseInt(string.substring(44, 46), 16);
            cardInfo.continuousHours = Integer.parseInt(string.substring(46, 48), 16);
            cardInfo.wanrAutoLock = Integer.parseInt(string.substring(48, 50), 16);
            cardInfo.noUseAutoLock = Integer.parseInt(string.substring(50, 52), 16);
            cardInfo.lockDay1 = Integer.parseInt(string.substring(52, 54), 16);
            cardInfo.lockDay2 = Integer.parseInt(string.substring(54, 56), 16);
            cardInfo.overflowFun = Integer.parseInt(string.substring(56, 58), 16);
            cardInfo.overflowCount = Integer.parseInt(string.substring(58, 62), 16);
            cardInfo.overflowTimeStart = Integer.parseInt(string.substring(62, 64), 16);
            cardInfo.overflowTime = Integer.parseInt(string.substring(64, 66), 16);
            cardInfo.limitBuy = Integer.parseInt(string.substring(66, 68), 16);
            cardInfo.limitMoney = Utils.getPositiveNum(string.substring(68, 76));
            cardInfo.lowWarnMoney = Integer.parseInt(string.substring(76, 80), 16);
            cardInfo.autoWarnStart1 = Integer.parseInt(string.substring(80, 82), 16);
            cardInfo.warnValue1 = Integer.parseInt(string.substring(82, 84), 16);
            cardInfo.autoWarnStart2 = Integer.parseInt(string.substring(84, 86), 16);
            cardInfo.warnValue2 = Integer.parseInt(string.substring(86, 88), 16);
            cardInfo.zeroClose = Integer.parseInt(string.substring(88, 90), 16);
            cardInfo.securityCheckStart = Integer.parseInt(string.substring(90, 92), 16);
            cardInfo.securityMonth = Integer.parseInt(string.substring(92, 94), 16);
            cardInfo.scrapStart = Integer.parseInt(string.substring(94, 96), 16);
            cardInfo.scrapYear = Integer.parseInt(string.substring(96, 98), 16);
            cardInfo.versonFlag = Integer.parseInt(string.substring(98, 100), 16);
            cardInfo.userFlag2 = Integer.parseInt(string.substring(100, 102), 16);
            cardInfo.recordDate = Integer.parseInt(string.substring(102, 104));
            String e = string.substring(104, 148);
            String pStr2 = string.substring(148, 192);
            String pStr3 = string.substring(192, 236);
            String pStr4 = string.substring(236, 280);
            cardInfo.priceGroupC1 = this.getPriceG(e);
            cardInfo.priceGroupC2 = this.getPriceG(pStr2);
            cardInfo.priceGroupN1 = this.getPriceG(pStr3);
            cardInfo.priceGroupN2 = this.getPriceG(pStr4);
            cardInfo.newPriceStart = string.substring(280, 288);
            ArrayList priceList = new ArrayList();
            String pl = string.substring(288, 336);

            for(int list = 0; list < pl.length(); list += 4) {
                priceList.add(pl.substring(list, list + 4));
            }

            cardInfo.priceStartCycling = priceList;
            cardInfo.checkSum2 = string.substring(336, 338);
            if(len.equals("00D3")) {
                ArrayList list1 = new ArrayList();
                String price = string.substring(338, 386);

                for(int i = 0; i < price.length(); i += 4) {
                    list1.add(price.substring(i, i + 4));
                }

                cardInfo.newPriceStartRepeat = list1;
                cardInfo.valWay = Integer.parseInt(string.substring(386, 388), 16);
                cardInfo.backupData = string.substring(388, 420);
                cardInfo.extSum = string.substring(420, 422);
            }
        } catch (NumberFormatException var13) {
            Log.e(this.tag, "解析异常CardNanager");
            var13.printStackTrace();
        }

    }

    public PriceGroup getPriceG(String str) {
        PriceGroup priceGroup = new PriceGroup();
        byte step = 2;
        priceGroup.price1 = Integer.parseInt(str.substring(0 * step, 2 * step), 16);
        priceGroup.divideCount1 = Integer.parseInt(str.substring(2 * step, 5 * step), 16);
        priceGroup.price2 = Integer.parseInt(str.substring(5 * step, 7 * step), 16);
        priceGroup.divideCount2 = Integer.parseInt(str.substring(7 * step, 10 * step), 16);
        priceGroup.price3 = Integer.parseInt(str.substring(10 * step, 12 * step), 16);
        priceGroup.divideCount3 = Integer.parseInt(str.substring(12 * step, 15 * step), 16);
        priceGroup.price4 = Integer.parseInt(str.substring(15 * step, 17 * step), 16);
        priceGroup.divideCount4 = Integer.parseInt(str.substring(17 * step, 20 * step), 16);
        priceGroup.price5 = Integer.parseInt(str.substring(20 * step, 22 * step), 16);
        return priceGroup;
    }

    private void parseCommonInfo(CardInfo cardInfo, String string) {
        try {
            cardInfo.companyCode = string.substring(0, 4);
            cardInfo.cityCode = string.substring(4, 8);
            cardInfo.openTime = string.substring(8, 22);
            cardInfo.userName = string.substring(22, 38);
            cardInfo.userID = string.substring(38, 86);
            cardInfo.checkSum1 = string.substring(86, 88);
        } catch (Exception var4) {
            Log.e(this.tag, "解析异常parseCommonInfo");
            var4.printStackTrace();
        }

    }

    private String[] sendToCard(String s) {
        String[] str = null;
        byte[] cmd = HexDump.hexStringToByteArray(s);
        ByteResult recData = new ByteResult();
        ByteResult recSate = new ByteResult();
        long flag = this.cardReader.sendApdu(cmd, recData, recSate, this.timeOut);
        switch((int)flag) {
            case -9:
                str = new String[]{"-9", "oterError"};
                break;
            case -1:
                str = new String[]{"-1", "timeout"};
                break;
            case 0:
                String resultStrData = recData.byteArr != null?HexDump.toHexString(recData.byteArr):"";
                String resultStrState = recSate.byteArr != null?HexDump.toHexString(recSate.byteArr):"";
                if(resultStrState.equals("9000")) {
                    str = new String[]{"1", resultStrData};
                } else {
                    str = new String[]{"0", resultStrState};
                }
        }

        return str;
    }

    private boolean isEquals0() {
        int length = returnData.length();
        String str = "";

        for(int i = 0; i < length; ++i) {
            str = str + "0";
        }

        return returnData.equals(str);
    }

    protected boolean openSocket(String ip, int port) {
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), 5000);
            this.os = this.socket.getOutputStream();
            this.is = this.socket.getInputStream();
            return true;
        } catch (UnknownHostException var4) {
            return false;
        } catch (IOException var5) {
            return false;
        }
    }

    public boolean isEnable(CardInfo info) {
        return Double.parseDouble(info.thisMoney) != 0.0D?false:!this.isEquals0();
    }

    public String getQuan(String cardID, String quanRes, int money, String time) {
        String quanMac = "-1";
        String sendQuan = this.sendQuan(cardID, quanRes, money, time);
        if(!Utils.isNullOrEmpty(sendQuan)) {
            Log.e(this.tag, "加密圈存结果==" + sendQuan);
            quanMac = Utils.getMapValue(sendQuan, "MAC");
        }

        return quanMac;
    }

    private String getLoadData(String cardID, String quanRes, int money, String timeGet) {
        StringBuilder builder = new StringBuilder();
        builder.append("[Request],");
        builder.append("Command=GetLoadMac,");
        builder.append("UserCardNo=00000000" + cardID + ",");
        builder.append("InitializeLoad=" + quanRes + ",");
        builder.append("LoadMoney=" + Utils.getHexMoney(money) + ",");
        builder.append("LoadDayTime=" + timeGet);
        Log.e("TAG", "getLoadData====" + builder.toString());
        return builder.toString();
    }

    private String getDesData(String times, String random4, String cardMsg) {
        StringBuilder builder = new StringBuilder();
        builder.append("[Request],");
        builder.append("Command=GetDesMac,");
        String msg = this.getNewMsg(times, cardMsg);
        builder.append("UserData=" + msg + ",");
        builder.append("Randm=" + random4);
        return builder.toString();
    }

    private String getNewMsg(String times, String cardMsg) {
        String str = "";
        byte byt;
        String string;
        String oldTime;
        String oldValidTime;
        long time;
        String newTime;
        String newValidTime;
        byte[] bs;
        int sum;
        byte hexString;
        int var15;
        int var16;
        byte[] var17;
        String var18;
        if(this.cardVerson == 1) {
            byt = (byte)(Integer.parseInt(times) & 255);
            string = Utils.byte2HexString(new byte[]{byt});
            oldTime = cardMsg.substring(8, 14);
            oldValidTime = cardMsg.substring(14, 20);
            time = Utils.getDayGap(oldTime, oldValidTime);
            newTime = Utils.getNowDate();
            newValidTime = Utils.getNewValidTime(newTime, time);
            str = cardMsg.substring(0, 8) + newTime + newValidTime + cardMsg.subSequence(20, 42) + string + cardMsg.substring(44, 336);
            bs = Utils.hexToByte(str);
            sum = 0;
            var17 = bs;
            var16 = bs.length;

            for(var15 = 0; var15 < var16; ++var15) {
                hexString = var17[var15];
                sum += hexString;
            }

            var18 = Utils.byte2HexString(new byte[]{(byte)(sum & 255)});
            str = str + var18;
        } else {
            byt = (byte)(Integer.parseInt(times) & 255);
            string = Utils.byte2HexString(new byte[]{byt});
            oldTime = cardMsg.substring(8, 14);
            oldValidTime = cardMsg.substring(14, 20);
            time = Utils.getDayGap(oldTime, oldValidTime);
            newTime = Utils.getNowDate();
            newValidTime = Utils.getNewValidTime(newTime, time);
            str = cardMsg.substring(0, 8) + newTime + newValidTime + cardMsg.subSequence(20, 42) + string + cardMsg.substring(44, 336);
            bs = Utils.hexToByte(str);
            sum = 0;
            var17 = bs;
            var16 = bs.length;

            for(var15 = 0; var15 < var16; ++var15) {
                hexString = var17[var15];
                sum += hexString;
            }

            var18 = Utils.byte2HexString(new byte[]{(byte)(sum & 255)});
            str = str + var18 + cardMsg.substring(338);
        }

        Log.e("TAG", "需加密的数据修改后=========" + str);
        return str;
    }

    private String getAuData(String cardID, String random8) {
        StringBuilder builder = new StringBuilder();
        builder.append("[Request],");
        builder.append("Command=GetOuAuth,");
        builder.append("UserCardNo=00000000" + cardID + ",");
        builder.append("Randm=" + random8.substring(0, 16) + ",");
        builder.append("CmdInfo=2705");
        return builder.toString();
    }

    private String getLoginData() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Request],");
        builder.append("Command=Login,");
        builder.append("UserName=admin,");
        builder.append("Password=admin");
        return builder.toString();
    }

    private byte[] getDataLen(String str) {
        int length = str.length();
        byte[] len = new byte[]{(byte)(length >>> 0), (byte)(length >>> 8), (byte)(length >>> 16), (byte)(length >>> 24)};
        return len;
    }

    private byte[] getPLength(String str) {
        int dLen = str.length() + 4;
        byte[] pLen = new byte[]{(byte)(dLen >>> 0), (byte)(dLen >>> 8), (byte)(dLen >>> 16), (byte)(dLen >>> 24)};
        return pLen;
    }

    private boolean login() {
        try {
            String e = this.getLoginData();
            byte[] dataLen = this.getDataLen(e);
            byte[] pLen = this.getPLength(e);
            this.os.write(pLen);
            this.os.write(dataLen);
            this.os.write(e.getBytes("UTF-8"));
            this.os.flush();
            byte[] buffer = new byte[1024];
            int read = this.is.read(buffer);
            if(read > 8) {
                byte[] bs = Arrays.copyOfRange(buffer, 8, read);
                String loginResult = new String(bs);
                Log.e(this.tag, "登录结果==" + loginResult);
                if(!Utils.isNullOrEmpty(loginResult) && Utils.getMapValue(loginResult, "Code").equals("0")) {
                    return true;
                }
            }
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        return false;
    }

    private String getAuthString(String cardID, String random8) {
        try {
            String e = this.getAuData(cardID, random8);
            byte[] auDataLen = this.getDataLen(e);
            byte[] auDLen = this.getPLength(e);
            this.os.write(auDLen);
            this.os.write(auDataLen);
            this.os.write(e.getBytes("UTF-8"));
            this.os.flush();
            byte[] b = new byte[1024];
            int r = this.is.read(b);
            byte[] cs = Arrays.copyOfRange(b, 8, r);
            String authResult = new String(cs);
            return authResult;
        } catch (Exception var10) {
            var10.printStackTrace();
            return "";
        }
    }

    protected String sendQuan(String cardID, String quanRes, int money, String time) {
        try {
            String e = this.getLoadData(cardID, quanRes, money, time);
            byte[] loadPLength = this.getPLength(e);
            byte[] loadDLen = this.getDataLen(e);
            this.os.write(loadPLength);
            this.os.write(loadDLen);
            this.os.write(e.getBytes("UTF-8"));
            this.os.flush();
            byte[] bt = new byte[1024];
            int r7 = this.is.read(bt);
            byte[] ca = Arrays.copyOfRange(bt, 8, r7);
            String loadRes = new String(ca);
            return loadRes;
        } catch (Exception var12) {
            var12.printStackTrace();
            return "";
        }
    }

    public String[] getSerialNum() {
        String[] res = this.sendToCard("FF70060100");
        return !res[0].equals("1")?new String[]{"0", res[1]}:new String[]{"1", Utils.getReverse(res[1])};
    }

    public boolean enter3F02() {
        String[] result = this.sendToCard("00A40000023F02");
        return result[0].equals("1");
    }

    public String[] getRandom8Hex() {
        String[] res = this.sendToCard("0084000008");
        return !res[0].equals("1")?new String[]{"0", res[1]}:new String[]{"1", res[1]};
    }

    public String sendToAuth(String ip, int port, String seria, String random8) {
        boolean b = this.openSocket(ip, port);
        if(!b) {
            return "-1";
        } else {
            boolean login = this.login();
            if(!login) {
                return "-2";
            } else {
                String authResult = this.getAuthString(seria, random8);
                Log.e(this.tag, "认证结果==" + authResult);
                if(!Utils.isNullOrEmpty(authResult) && Utils.getMapValue(authResult, "Code").equals("0")) {
                    String value = Utils.getMapValue(authResult, "DesRandm");
                    return value;
                } else {
                    return "-3";
                }
            }
        }
    }

    public String[] transOutAuth(String authData) {
        String[] res = this.sendToCard("0082000408" + authData);
        return !res[0].equals("1")?new String[]{"0", res[1]}:new String[]{"1"};
    }

    public String[] getRandom4Hex() {
        String[] res = this.sendToCard("0084000004");
        return !res[0].equals("1")?new String[]{"0", res[1]}:new String[]{"1", res[1] + "00000000"};
    }

    public String[] getCardMsg() {
        String cmd = "00C8";
        if(this.cardVerson == 2) {
            cmd = "00D3";
        }

        Log.e(this.tag, "获取卡的应用信息文件=========" + cmd);
        String[] res = this.sendToCard("00B081" + cmd);
        return !res[0].equals("1")?new String[]{"0", res[1]}:(this.cardVerson == 1?new String[]{"1", res[1].substring(0, 338)}:(this.cardVerson == 2?new String[]{"1", res[1].substring(0, 422)}:new String[]{"1", res[1].substring(0, 338)}));
    }

    private String getDesString(String times, String random4, String cardMsg) {
        try {
            String e = this.getDesData(times, random4, cardMsg);
            byte[] macPLength = this.getPLength(e);
            byte[] macDLen = this.getDataLen(e);
            this.os.write(macPLength);
            this.os.write(macDLen);
            this.os.write(e.getBytes("UTF-8"));
            this.os.flush();
            byte[] bu = new byte[1024];
            int r5 = this.is.read(bu);
            byte[] cf = Arrays.copyOfRange(bu, 8, r5);
            String macRes = new String(cf);
            return macRes;
        } catch (Exception var11) {
            var11.printStackTrace();
            return "";
        }
    }

    public String[] getDesResult(String times, String random4, String cardMsg) {
        String[] result = null;
        String macRes = this.getDesString(times, random4, cardMsg);
        Log.e(this.tag, "加密结果==" + macRes);
        if(!Utils.isNullOrEmpty(macRes)) {
            String enStr = Utils.getMapValue(macRes, "DesData");
            String mac = Utils.getMapValue(macRes, "MAC");
            result = new String[]{enStr, mac};
        } else {
            result = null;
        }

        return result;
    }

    public String[] sendDesData(String desData, String mac) {
        String len = String.format("%02X", new Object[]{Integer.valueOf(desData.length() / 2 + 4)});
        String write = "04D68100" + len + desData + mac;
        String[] result = this.sendToCard(write);
        return !result[0].equals("1")?new String[]{"0", result[1]}:new String[]{"1", result[1]};
    }

    public String[] sendMoney(int money) {
        String quan = "805000020B02" + Utils.getHexMoney(money) + "112233445566";
        Log.e(this.tag, "quan==" + quan);
        String[] result = this.sendToCard(quan);
        return !result[0].equals("1")?new String[]{"0", result[1]}:new String[]{"1", result[1]};
    }

    public String[] sendQuanMac(String time, String mac) {
        int le = (time.length() + mac.length()) / 2;
        String cmd = "80520000" + String.format("%02X", new Object[]{Integer.valueOf(le)}) + time + mac;
        String[] result = this.sendToCard(cmd);
        return !result[0].equals("1")?new String[]{"0", result[1]}:new String[]{"1", result[1]};
    }

    public boolean selectFile() {
        String[] result = this.sendToCard("00A40000020003");
        return result[0].equals("1");
    }

    public boolean writeToZero() {
        String cmd = "00C8";
        if(this.cardVerson == 2) {
            cmd = "00E9";
        }

        Log.e(this.tag, "cmd==写0字节长度==" + cmd);
        byte[] ze = new byte[Integer.parseInt(cmd, 16)];
        String[] result = this.sendToCard("00D600" + cmd + HexDump.toHexString(ze));
        return result[0].equals("1");
    }
}
