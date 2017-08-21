//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xtkj.nfcjar;

import android.annotation.TargetApi;
import android.util.Log;
import com.xtkj.nfcjar.IUtil;
import com.xtkj.nfcjar.bean.CheckCardBean;
import com.xtkj.nfcjar.bean.ParamBean;
import com.xtkj.nfcjar.bean.PayResultBean;
import com.xtkj.nfcjar.bean.PriceGroup;
import com.xtkj.nfcjar.bean.ReadResultBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TargetApi(9)
public class DataManager {
    public DataManager() {
    }

    public static byte[] getReadByte(String userNum) {
        byte[] b1 = new byte[]{(byte)104, (byte)48, (byte)58, (byte)10};
        byte[] b2 = IUtil.getBCD(userNum);
        byte[] b3 = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(b2, 0, b3, b1.length, b2.length);
        byte[] cmd = IUtil.mergeArrays(b3);
        return cmd;
    }

    public static byte[] getCheckTimeByte(String userNum, String time) {
        byte[] b1 = new byte[]{(byte)104, (byte)48, (byte)121, (byte)16};
        byte[] b2 = IUtil.getBCD(userNum);
        byte[] b4 = IUtil.getBCD(time);
        byte[] b3 = new byte[b1.length + b2.length + b4.length];
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(b2, 0, b3, b1.length, b2.length);
        System.arraycopy(b4, 0, b3, b1.length + b2.length, b4.length);
        byte[] cmd = IUtil.mergeArrays(b3);
        return cmd;
    }

    public static byte[] getHistoryData(String userNum, int index) {
        byte[] b1 = new byte[]{(byte)104, (byte)48, (byte)80, (byte)11};
        byte[] b2 = IUtil.getBCD(userNum);
        byte[] b3 = new byte[b1.length + b2.length + 1];
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(b2, 0, b3, b1.length, b2.length);
        b3[b3.length - 1] = (byte)(index & 255);
        byte[] cmd = IUtil.mergeArrays(b3);
        return cmd;
    }

    public static byte[] getSetBottomData(String userNum, int botNum) {
        byte[] b1 = new byte[]{(byte)104, (byte)48, (byte)73, (byte)14};
        byte[] b2 = IUtil.getBCD(userNum);
        byte[] bot = IUtil.getNumByte4(botNum);
        byte[] b3 = new byte[b1.length + b2.length + bot.length];
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(b2, 0, b3, b1.length, b2.length);
        System.arraycopy(bot, 0, b3, b1.length + b2.length, bot.length);
        byte[] cmd = IUtil.mergeArrays(b3);
        return cmd;
    }

    public static byte[] getClearData(String userNum, String valiDate, String command, String random) {
        byte[] b1 = new byte[]{(byte)104, (byte)48, (byte)83, (byte)29};
        byte[] b2 = IUtil.getBCD(userNum);
        byte[] b3 = IUtil.getBCD(valiDate);
        byte[] b4 = IUtil.hexToByte(command + random);
        byte[] b5 = new byte[b1.length + b2.length + b3.length + b4.length];
        System.arraycopy(b1, 0, b5, 0, b1.length);
        System.arraycopy(b2, 0, b5, b1.length, b2.length);
        System.arraycopy(b3, 0, b5, b1.length + b2.length, b3.length);
        System.arraycopy(b4, 0, b5, b1.length + b2.length + b3.length, b4.length);
        byte[] cmd = IUtil.mergeArrays(b5);
        return cmd;
    }

    public static byte[] getEmergencyData(String userNum, String warn2, String valiDate, String desCmd, String random) {
        byte[] b1 = new byte[]{(byte)104, (byte)48, (byte)85, (byte)30};
        byte[] b2 = IUtil.getBCD(userNum);
        byte[] b3 = IUtil.getBCD(valiDate);
        byte[] b4 = IUtil.hexToByte(desCmd + random);
        byte[] b5 = new byte[b1.length + b2.length + b3.length + b4.length + 1];
        System.arraycopy(b1, 0, b5, 0, b1.length);
        System.arraycopy(b2, 0, b5, b1.length, b2.length);
        b5[b1.length + b2.length] = (byte)(Integer.parseInt(warn2) & 255);
        System.arraycopy(b3, 0, b5, b1.length + b2.length + 1, b3.length);
        System.arraycopy(b4, 0, b5, b1.length + b2.length + b3.length + 1, b4.length);
        byte[] cmd = IUtil.mergeArrays(b5);
        return cmd;
    }

    public static byte[] getSecurityData(String userNum, String securityNum, String valiDate, String desCmd, String random) {
        byte[] b1 = new byte[]{(byte)104, (byte)48, (byte)86, (byte)32};
        byte[] b2 = IUtil.getBCD(userNum);
        byte[] b3 = IUtil.getNumByte3(Integer.parseInt(securityNum));
        byte[] b4 = IUtil.getBCD(valiDate);
        byte[] b5 = IUtil.hexToByte(desCmd + random);
        byte[] b6 = new byte[b1.length + b2.length + b3.length + b4.length + b5.length];
        System.arraycopy(b1, 0, b6, 0, b1.length);
        System.arraycopy(b2, 0, b6, b1.length, b2.length);
        System.arraycopy(b3, 0, b6, b1.length + b2.length, b3.length);
        System.arraycopy(b4, 0, b6, b1.length + b2.length + b3.length, b4.length);
        System.arraycopy(b5, 0, b6, b1.length + b2.length + b3.length + b4.length, b5.length);
        byte[] cmd = IUtil.mergeArrays(b6);
        return cmd;
    }

    public static byte[] getCheckData(String userNum, int num, String valiDate, String desCmd, String random) {
        byte[] b1 = null;
        if(num == 1) {
            b1 = new byte[]{(byte)104, (byte)48, (byte)81, (byte)29};
        } else if(num == 2) {
            b1 = new byte[]{(byte)104, (byte)48, (byte)82, (byte)29};
        }

        byte[] b2 = IUtil.getBCD(userNum);
        byte[] b4 = IUtil.getBCD(valiDate);
        byte[] b5 = IUtil.hexToByte(desCmd + random);
        byte[] b6 = new byte[b1.length + b2.length + b4.length + b5.length];
        System.arraycopy(b1, 0, b6, 0, b1.length);
        System.arraycopy(b2, 0, b6, b1.length, b2.length);
        System.arraycopy(b4, 0, b6, b1.length + b2.length, b4.length);
        System.arraycopy(b5, 0, b6, b1.length + b2.length + b4.length, b5.length);
        byte[] cmd = IUtil.mergeArrays(b6);
        return cmd;
    }

    public static byte[] getParamData(String userNum, ParamBean paramBean, String valiDate, String desCmd, String random) {
        String params = "";
        if(paramBean != null) {
            params = getParamByBean(paramBean).substring(0, 342);
        } else {
            for(int b1 = 0; b1 < 171; ++b1) {
                params = params + "00";
            }
        }

        byte[] var14 = new byte[]{(byte)104, (byte)48, (byte)84, (byte)-55};
        byte[] b2 = IUtil.getBCD(userNum);
        byte[] b3 = IUtil.hexToByte(params);
        byte b4 = IUtil.getSum(b3);
        byte[] b5 = IUtil.getBCD(valiDate);
        byte[] b6 = IUtil.hexToByte(desCmd + random);
        byte[] b7 = new byte[var14.length + b2.length + b3.length + b5.length + b6.length + 1];
        System.arraycopy(var14, 0, b7, 0, var14.length);
        System.arraycopy(b2, 0, b7, var14.length, b2.length);
        System.arraycopy(b3, 0, b7, var14.length + b2.length, b3.length);
        b7[var14.length + b2.length + b3.length] = b4;
        System.arraycopy(b5, 0, b7, var14.length + b2.length + b3.length + 1, b5.length);
        System.arraycopy(b6, 0, b7, var14.length + b2.length + b3.length + b5.length + 1, b6.length);
        byte[] cmd = IUtil.mergeArrays(b7);
        return cmd;
    }

    public static byte[] getPayData(String EncodeString, byte flag, ParamBean paramBean, String desCmd) {
        String value = "";
        String param = "";
        if(paramBean != null) {
            param = getParamByBean(paramBean).substring(0, 342);
        } else {
            for(int p2 = 0; p2 < 170; ++p2) {
                param = param + "00";
            }

            param = param + "11";
        }

        String var13 = IUtil.byte2HexString(new byte[]{IUtil.getSum(IUtil.hexToByte(param))});
        value = param + var13 + desCmd.substring(0, 16) + desCmd.substring(desCmd.length() - 16);
        byte[] b1 = new byte[]{(byte)104, (byte)48, (byte)25, (byte)24};
        byte[] b2 = IUtil.hexToByte(EncodeString);
        byte[] b6 = IUtil.hexToByte(value);
        byte[] b7 = new byte[b1.length + 24 + b6.length + 1];
        System.arraycopy(b1, 0, b7, 0, b1.length);
        System.arraycopy(b2, 0, b7, b1.length, 24);
        b7[b1.length + 24] = flag;
        System.arraycopy(b6, 0, b7, b1.length + 25, b6.length);
        byte[] cmd = IUtil.mergeArrays(b7);
        return cmd;
    }

    public static byte[] getPayDataWithoutP(String EncodeString, byte flag) {
        byte[] b1 = new byte[]{(byte)104, (byte)48, (byte)25, (byte)24};
        byte[] b2 = IUtil.hexToByte(EncodeString);
        byte[] b7 = new byte[b1.length + 24 + 1];
        System.arraycopy(b1, 0, b7, 0, b1.length);
        System.arraycopy(b2, 0, b7, b1.length, 24);
        b7[b1.length + 24] = flag;
        byte[] cmd = IUtil.mergeArrays(b7);
        return cmd;
    }

    public static ReadResultBean parseReadResult(String readResult) {
        try {
            ReadResultBean e = new ReadResultBean();
            ArrayList list = new ArrayList();
            ArrayList records = new ArrayList();
            String useData = readResult.substring(10, 334);
            byte step = 20;
            e.meterNum = useData.substring(0, step);
            e.currentTime = useData.substring(0 + step, 14 + step);
            e.currentPrice = Integer.parseInt(useData.substring(14 + step, 18 + step), 16);
            e.remainedMoney = IUtil.getNegativeNum(useData.substring(18 + step, 26 + step));
            e.totalBuy = IUtil.getPositiveNum(useData.substring(26 + step, 34 + step));
            e.totalUse = IUtil.getPositiveNum(useData.substring(34 + step, 42 + step));
            e.noUseDay = Integer.parseInt(useData.substring(42 + step, 44 + step), 16);
            e.noUseSecond = Integer.parseInt(useData.substring(44 + step, 48 + step), 16);
            e.meterState = useData.substring(48 + step, 52 + step);
            e.dealWord = useData.substring(52 + step, 54 + step);

            BigDecimal totalBuy = new BigDecimal(e.totalBuy);
            BigDecimal remainedMoney = new BigDecimal(e.remainedMoney);
            BigDecimal currentPrice = new BigDecimal(e.currentPrice);
            BigDecimal dd = new BigDecimal(100);
            e.remainedMoney = remainedMoney.divide(dd).setScale(2,BigDecimal.ROUND_HALF_UP)+"";
            e.totalBuy = totalBuy.divide(dd).setScale(2,BigDecimal.ROUND_HALF_UP)+"";
            e.currentPrice = currentPrice.divide(dd).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();

            String history = useData.substring(54 + step, 246 + step);

            for(int checkRecord = 0; checkRecord < history.length(); checkRecord += 8) {
                String i = IUtil.getPositiveNum(history.substring(checkRecord, checkRecord + 8));
                list.add(i);
            }

            e.historyList = list;
            e.securityCount = Integer.parseInt(useData.substring(246 + step, 248 + step), 16);
            String checkRecord1 = useData.substring(248 + step, 284 + step);

            for(int i1 = 0; i1 < checkRecord1.length(); i1 += 12) {
                String re = checkRecord1.substring(i1, i1 + 12);
                records.add(re);
            }

            e.securityRecords = records;
            e.recentColseRec = useData.substring(284 + step, 286 + step);
            e.nfcTimes = Integer.parseInt(useData.substring(286 + step, 288 + step), 16);
            e.nfcBuy = IUtil.getPositiveNum(useData.substring(288 + step, 296 + step));
            e.nfcTotalBuy = IUtil.getPositiveNum(useData.substring(296 + step));
            e.result = 1;
            return e;
        } catch (NumberFormatException var10) {
            Log.e("TAG", "解析出现异常========");
            var10.printStackTrace();
            return new ReadResultBean(0);
        }
    }

    public static PayResultBean parsePayResult(String addResult) {
        PayResultBean bean = new PayResultBean();

        try {
            String e = addResult.substring(30, 32);
            bean.payTimes = Integer.parseInt(e, 16);
            String remain = addResult.substring(32, 40);
            String builder = IUtil.getReverse(remain);
            bean.remainMoney = IUtil.getNegativeNum(builder.toString());
            String buy = addResult.substring(40, 48);
            String builder1 = IUtil.getReverse(buy);
            bean.totalPay = IUtil.getPositiveNum(builder1.toString());

            BigDecimal totalPay = new BigDecimal(bean.totalPay);
            BigDecimal remainMoney = new BigDecimal(bean.remainMoney);
            BigDecimal dd = new BigDecimal(100);
            bean.remainMoney = remainMoney.divide(dd).setScale(2,BigDecimal.ROUND_HALF_UP)+"";
            bean.totalPay = totalPay.divide(dd).setScale(2,BigDecimal.ROUND_HALF_UP)+"";

            String useCount = addResult.substring(48, 56);
            String builder2 = IUtil.getReverse(useCount);
            bean.totalUse = IUtil.getPositiveNum(builder2.toString());
            bean.meterState = addResult.substring(56, 60);
            bean.historyDosList = getHistoryCount(addResult.substring(62, 254));
            bean.historyMonthList = getHistoryMonth(addResult.substring(254, 350));
            bean.adjustDate = addResult.substring(350, 356);
            bean.adjustBottomNum = IUtil.getPositiveNum(addResult.substring(356, 364));
            bean.payDate = addResult.substring(364, 370);
            bean.payBottomNum = IUtil.getPositiveNum(addResult.substring(370, 378));
            bean.valWay = Integer.parseInt(addResult.substring(378, 380), 16);
            bean.result = 1;
        } catch (NumberFormatException var9) {
            bean.result = 0;
            Log.e("TAG", "解析出现异常========");
            var9.printStackTrace();
        }

        return bean;
    }

    public static PayResultBean parsePayResultShort(String addResult) {
        PayResultBean bean = new PayResultBean();

        try {
            String e = addResult.substring(30, 32);
            bean.payTimes = Integer.parseInt(e, 16);
            String remain = addResult.substring(32, 40);
            String builder = IUtil.getReverse(remain);
            bean.remainMoney = IUtil.getNegativeNum(builder.toString());
            String buy = addResult.substring(40, 48);
            String builder1 = IUtil.getReverse(buy);
            bean.totalPay = IUtil.getPositiveNum(builder1.toString());

            BigDecimal totalPay = new BigDecimal(bean.totalPay);
            BigDecimal remainMoney = new BigDecimal(bean.remainMoney);
            BigDecimal dd = new BigDecimal(100);
            bean.remainMoney = remainMoney.divide(dd).setScale(2,BigDecimal.ROUND_HALF_UP)+"";
            bean.totalPay = totalPay.divide(dd).setScale(2,BigDecimal.ROUND_HALF_UP)+"";

            String useCount = addResult.substring(48, 56);
            String builder2 = IUtil.getReverse(useCount);
            bean.totalUse = IUtil.getPositiveNum(builder2.toString());
            bean.meterState = addResult.substring(56, 60);
            bean.result = 1;
        } catch (NumberFormatException var9) {
            bean.result = 0;
            Log.e("TAG", "解析出现异常========");
            var9.printStackTrace();
        }

        return bean;
    }

    public static CheckCardBean parseCheckRes(String data1, String data2) {
        try {
            CheckCardBean e = new CheckCardBean(1);
            byte step = 2;
            e.userNum = data1.substring(0, 10 * step);
            data1 = data1.substring(10 * step);
            data2 = data2.substring(10 * step);
            e.secretVerson = Integer.parseInt(data1.substring(0, 1 * step));
            e.cardBuyDate = data1.substring(1 * step, 4 * step);
            e.cardValidDate = data1.substring(4 * step, 7 * step);
            e.userType = data1.substring(17 * step, 18 * step);
            e.cardBuyTimes = Integer.parseInt(data1.substring(18 * step, 19 * step), 16);
            e.blabFun = Integer.parseInt(data1.substring(19 * step, 20 * step), 16);
            e.serialHours = Integer.parseInt(data1.substring(20 * step, 21 * step), 16);
            e.warnLockFun = Integer.parseInt(data1.substring(21 * step, 22 * step), 16);
            e.longNotUseLocFkFun = Integer.parseInt(data1.substring(22 * step, 23 * step), 16);
            e.notUseDay1 = Integer.parseInt(data1.substring(23 * step, 24 * step), 16);
            e.notUseDay2 = Integer.parseInt(data1.substring(24 * step, 25 * step), 16);
            e.overFun = Integer.parseInt(data1.substring(25 * step, 26 * step), 16);
            e.overCount = Integer.parseInt(data1.substring(26 * step, 28 * step), 16);
            e.overTimeFun = Integer.parseInt(data1.substring(28 * step, 29 * step), 16);
            e.overTime = Integer.parseInt(data1.substring(29 * step, 30 * step), 16);
            e.limitBuyFun = Integer.parseInt(data1.substring(30 * step, 31 * step), 16);
            e.limitUp = IUtil.getPositiveNum(data1.substring(31 * step, 35 * step));
            e.lowWarnMoney = Integer.parseInt(data1.substring(35 * step, 37 * step), 16);
            e.warn1Fun = Integer.parseInt(data1.substring(37 * step, 38 * step), 16);
            e.warn1Value = Integer.parseInt(data1.substring(38 * step, 39 * step), 16);
            e.warn2Fun = Integer.parseInt(data1.substring(39 * step, 40 * step), 16);
            e.warn2Value = Integer.parseInt(data1.substring(40 * step, 41 * step), 16);
            e.zeroClose = Integer.parseInt(data1.substring(41 * step, 42 * step), 16);
            e.securityFun = Integer.parseInt(data1.substring(42 * step, 43 * step), 16);
            e.securityMonth = Integer.parseInt(data1.substring(43 * step, 44 * step), 16);
            e.scrapFun = Integer.parseInt(data1.substring(44 * step, 45 * step), 16);
            e.scrapYear = Integer.parseInt(data1.substring(45 * step, 46 * step), 16);
            e.versonFlag = Integer.parseInt(data1.substring(46 * step, 47 * step), 16);
            e.cardFlag = data1.substring(47 * step, 48 * step);
            e.recordDay = Integer.parseInt(data1.substring(48 * step, 49 * step));
            e.curPriceG1 = getPriceG(data1.substring(49 * step, 71 * step));
            e.curPriceG2 = getPriceG(data1.substring(71 * step, 93 * step));
            e.newPriceG1 = getPriceG(data1.substring(93 * step, 115 * step));
            e.newPriceG2 = getPriceG(data1.substring(115 * step, 137 * step));
            e.newPriceStartTime = data1.substring(137 * step, 141 * step);
            e.priceStartRepeat = getPriceRepeat(data1.substring(141 * step, 165 * step));
            e.newPriceRepeat = getPriceRepeat(data1.substring(165 * step, 189 * step));
            e.valuationWay = data1.substring(189 * step, 190 * step);
            e.currentTime = data2.substring(0, 7 * step);
            e.currentPrice = Integer.parseInt(data2.substring(7 * step, 9 * step), 16);
            e.remainedMoney = IUtil.getNegativeNum(data2.substring(9 * step, 13 * step));
            e.totalBuy = IUtil.getPositiveNum(data2.substring(13 * step, 17 * step));
            e.totalUse = IUtil.getPositiveNum(data2.substring(17 * step, 21 * step));
            e.noUseDay = Integer.parseInt(data2.substring(21 * step, 22 * step));
            e.noUseSecond = Integer.parseInt(data2.substring(22 * step, 24 * step), 16);
            e.meterState = data2.substring(24 * step, 26 * step);
            e.dealWord = data2.substring(26 * step, 27 * step);
            e.historyGasList = getHistoryCount(data2.substring(27 * step, 123 * step));
            e.securityCount = Integer.parseInt(data2.substring(123 * step, 124 * step), 16);
            e.securityRecords = getCheckRecords(data2.substring(124 * step, 142 * step));
            e.recentColseRec = Integer.parseInt(data2.substring(142 * step, 143 * step), 16);
            e.nfcTimes = Integer.parseInt(data2.substring(143 * step, 144 * step), 16);
            e.nfcBuy = IUtil.getPositiveNum(data2.substring(144 * step, 148 * step));
            e.nfcTotalBuy = IUtil.getPositiveNum(data2.substring(148 * step, 152 * step));
            e.historyMonthList = getHistoryMonth(data2.substring(152 * step, 200 * step));
            e.adjustDate = data2.substring(200 * step, 203 * step);
            e.adjustBottomNum = IUtil.getPositiveNum(data2.substring(203 * step, 207 * step));
            e.payDate = data2.substring(207 * step, 210 * step);
            e.payBottomNum = IUtil.getPositiveNum(data2.substring(210 * step, 214 * step));
            return e;
        } catch (Exception var4) {
            Log.e("TAG", "解析出现异常========");
            var4.printStackTrace();
            return new CheckCardBean(0);
        }
    }

    public static PriceGroup getPriceG(String curPriceG1) {
        PriceGroup group = new PriceGroup();
        byte step = 2;
        group.price1 = Integer.parseInt(curPriceG1.substring(0 * step, 2 * step), 16);
        group.divideCount1 = Integer.parseInt(curPriceG1.substring(2 * step, 5 * step), 16);
        group.price2 = Integer.parseInt(curPriceG1.substring(5 * step, 7 * step), 16);
        group.divideCount2 = Integer.parseInt(curPriceG1.substring(7 * step, 10 * step), 16);
        group.price3 = Integer.parseInt(curPriceG1.substring(10 * step, 12 * step), 16);
        group.divideCount3 = Integer.parseInt(curPriceG1.substring(12 * step, 15 * step), 16);
        group.price4 = Integer.parseInt(curPriceG1.substring(15 * step, 17 * step), 16);
        group.divideCount4 = Integer.parseInt(curPriceG1.substring(17 * step, 20 * step), 16);
        group.price5 = Integer.parseInt(curPriceG1.substring(20 * step, 22 * step), 16);
        return group;
    }

    public static List<String> getPriceRepeat(String str) {
        ArrayList list = new ArrayList();

        for(int i = 0; i < str.length(); i += 4) {
            list.add(str.substring(i, i + 4));
        }

        return list;
    }

    public static List<String> getHistoryCount(String str) {
        ArrayList list = new ArrayList();

        for(int i = 0; i < str.length(); i += 8) {
            String use = IUtil.getPositiveNum(str.substring(i, i + 8));
            list.add(use);
        }

        return list;
    }

    public static List<String> getCheckRecords(String str) {
        ArrayList list = new ArrayList();

        for(int i = 0; i < str.length(); i += 12) {
            list.add(str.substring(i, i + 12));
        }

        return list;
    }

    public static List<String> getHistoryMonth(String str) {
        ArrayList list = new ArrayList();

        for(int i = 0; i < str.length(); i += 4) {
            list.add(str.substring(i, i + 4));
        }

        return list;
    }

    public static String getParamByBean(ParamBean bean) {
        StringBuffer param = new StringBuffer();
        param.append(bean.blabFun == 1?"01":"00");
        param.append(IUtil.byte2HexString(new byte[]{(byte)(bean.serialHours & 255)}));
        param.append(bean.warnLockFun == 1?"01":"00");
        param.append(bean.longNotUseLocFkFun == 1?"01":"00");
        param.append(IUtil.byte2HexString(new byte[]{(byte)(bean.notUseDay1 & 255)}));
        param.append(IUtil.byte2HexString(new byte[]{(byte)(bean.notUseDay2 & 255)}));
        param.append(bean.overFun == 1?"01":"00");
        param.append(IUtil.getHex(2, bean.overCount));
        param.append(bean.overTimeFun == 1?"01":"00");
        param.append(IUtil.byte2HexString(new byte[]{(byte)(bean.overTime & 255)}));
        param.append(bean.limitBuyFun == 1?"01":"00");
        param.append(IUtil.getHex(4, (double)bean.limitUp));
        param.append(IUtil.getHex(2, (double)bean.lowWarnMoney));
        param.append(bean.warn1Fun == 1?"01":"00");
        param.append(IUtil.byte2HexString(new byte[]{(byte)(bean.warn1Value & 255)}));
        param.append(bean.warn2Fun == 1?"01":"00");
        param.append(IUtil.byte2HexString(new byte[]{(byte)(bean.warn2Value & 255)}));
        param.append(bean.zeroClose == 1?"01":"00");
        param.append(bean.securityFun == 1?"01":"00");
        param.append(IUtil.byte2HexString(new byte[]{(byte)(bean.securityMonth & 255)}));
        param.append(bean.scrapFun == 1?"01":"00");
        param.append(IUtil.byte2HexString(new byte[]{(byte)(bean.scrapYear & 255)}));
        param.append(IUtil.byte2HexString(new byte[]{(byte)(bean.versonFlag & 255)}));
        param.append(bean.cardFlag);
        if(bean.recordDay < 10) {
            param.append(IUtil.byte2HexString(IUtil.getBCD("0" + bean.recordDay)));
        } else {
            param.append(IUtil.byte2HexString(IUtil.getBCD("" + bean.recordDay)));
        }

        param.append(IUtil.getPriceGroupString(bean.curPriceG1));
        param.append(IUtil.getPriceGroupString(bean.curPriceG2));
        param.append(IUtil.getPriceGroupString(bean.newPriceG1));
        param.append(IUtil.getPriceGroupString(bean.newPriceG2));
        param.append(IUtil.byte2HexString(IUtil.getBCD(bean.newPriceStartTime)));
        param.append(IUtil.getListString(bean.priceStartRepeat));
        param.append(IUtil.getListString(bean.newPriceRepeat));
        if(bean.valuationWay == 0) {
            param.append("55");
        } else if(bean.valuationWay == 1) {
            param.append("11");
        } else {
            String hexString = IUtil.byte2HexString(new byte[]{(byte)(bean.valuationWay & 255)});
            if(hexString.equals("11") || hexString.equals("55")) {
                hexString = "00";
            }

            param.append(hexString);
        }

        param.append(IUtil.byte2HexString(IUtil.getBCD(bean.valiDate)));
        return param.toString();
    }
}
