//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard;

import android.util.Log;
import com.xt.bluecard.CardInfo;
import com.xt.bluecard.CardManager;
import com.xt.bluecard.PriceGroup;
import com.xt.bluecard.Utils;
import java.util.ArrayList;
import java.util.List;

public class DataParse {
    private static String tag = "TAG";

    public DataParse() {
    }

    public static void parseReturnData(CardInfo cardInfo, String string, String returnLen) {
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
            cardInfo.monthUseList = getUseList(e);
            cardInfo.securityCounts = Integer.parseInt(string.substring(246, 248), 16);
            cardInfo.securityRecord = getRecordList(string.substring(248, 284));
            cardInfo.recentClose = Integer.parseInt(string.substring(284, 286), 16);
            cardInfo.nfcTimes = Integer.parseInt(string.substring(286, 288), 16);
            cardInfo.nfcMoney = Utils.getPositiveNum(string.substring(288, 296));
            cardInfo.nfcTotalMoney = Utils.getPositiveNum(string.substring(296, 304));
            cardInfo.checkSum3 = string.substring(304, 306);
            if(returnLen.equals("00E8")) {
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

            CardManager.returnData = string;
        } catch (NumberFormatException var7) {
            Log.e(tag, "解析异常CardNanager==267行");
            var7.printStackTrace();
        }

    }

    private static List<String> getUseList(String str) {
        ArrayList list = new ArrayList();

        for(int i = 0; i < str.length(); i += 8) {
            list.add(Utils.getPositiveNum(str.substring(i, i + 8)));
        }

        return list;
    }

    private static List<String> getRecordList(String str) {
        ArrayList list = new ArrayList();

        for(int i = 0; i < str.length(); i += 12) {
            list.add(str.substring(i, i + 12));
        }

        return list;
    }

    public static void parseAppliInfo(CardInfo cardInfo, String string, String len) {
        try {
            cardInfo.cardType = getCardType(string.substring(0, 2));
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
            cardInfo.priceGroupC1 = getPriceG(e);
            cardInfo.priceGroupC2 = getPriceG(pStr2);
            cardInfo.priceGroupN1 = getPriceG(pStr3);
            cardInfo.priceGroupN2 = getPriceG(pStr4);
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
        } catch (NumberFormatException var12) {
            Log.e(tag, "解析异常147行");
            var12.printStackTrace();
        }

    }

    private static PriceGroup getPriceG(String str) {
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

    public static void parseCommonInfo(CardInfo cardInfo, String string) {
        try {
            cardInfo.companyCode = string.substring(0, 4);
            cardInfo.cityCode = string.substring(4, 8);
            cardInfo.openTime = string.substring(8, 22);
            cardInfo.userName = string.substring(22, 38);
            cardInfo.userID = string.substring(38, 86);
            cardInfo.checkSum1 = string.substring(86, 88);
        } catch (Exception var3) {
            Log.e(tag, "解析异常CardNanager==344行");
            var3.printStackTrace();
        }

    }

    public static String getCardType(String str) {
        return str.equals("50")?"用户卡":(str.equals("51")?"检查卡":(str.equals("52")?"生产数据设置卡":(str.equals("53")?"密钥修改卡":(str.equals("54")?"清零卡":(str.equals("55")?"换表卡":(str.equals("56")?"校时卡":(str.equals("57")?"应急卡":(str.equals("58")?"参数设置卡":(str.equals("59")?"安检卡":(str.equalsIgnoreCase("5A")?"历史卡":"未知类型"))))))))));
    }
}
