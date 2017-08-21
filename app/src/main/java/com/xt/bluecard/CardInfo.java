//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard;

import com.xt.bluecard.PriceGroup;
import java.io.Serializable;
import java.util.List;

public class CardInfo implements Serializable {
    private static final long serialVersionUID = -5275086880290544023L;
    public String result;
    public String errorCode;
    public String companyCode;
    public String cityCode;
    public String openTime;
    public String userName;
    public String userID;
    public String checkSum1;
    public String cardType;
    public int userFlag;
    public int paramModifyFlag;
    public int keyVerson;
    public String buyTime;
    public String validTime;
    public String userCode;
    public String userType;
    public int buyTimes;
    public int leakFunction;
    public int continuousHours;
    public int wanrAutoLock;
    public int noUseAutoLock;
    public int lockDay1;
    public int lockDay2;
    public int overflowFun;
    public int overflowCount;
    public int overflowTimeStart;
    public int overflowTime;
    public int limitBuy;
    public String limitMoney;
    public int lowWarnMoney;
    public int autoWarnStart1;
    public int warnValue1;
    public int autoWarnStart2;
    public int warnValue2;
    public int zeroClose;
    public int securityCheckStart;
    public int securityMonth;
    public int scrapStart;
    public int scrapYear;
    public int versonFlag;
    public int userFlag2;
    public int recordDate;
    public PriceGroup priceGroupC1;
    public PriceGroup priceGroupC2;
    public PriceGroup priceGroupN1;
    public PriceGroup priceGroupN2;
    public String newPriceStart;
    public List<String> priceStartCycling;
    public String checkSum2;
    public List<String> newPriceStartRepeat;
    public int valWay;
    public String backupData;
    public String extSum;
    public String thisMoney;
    public String nowTime;
    public int nowPrice;
    public String nowRemainMoney;
    public String toalBuyMoney;
    public String toatalUseGas;
    public int noUseDayCount;
    public int noUseSecondsCount;
    public String nowStatus;
    public String dealWords;
    public List<String> monthUseList;
    public int securityCounts;
    public List<String> securityRecord;
    public int recentClose;
    public int nfcTimes;
    public String nfcMoney;
    public String nfcTotalMoney;
    public String checkSum3;
    public List<String> historyMonthList;
    public String addjustDate;
    public String addjustBottom;
    public String payDate;
    public String payBottom;
    public String backupData2;
    public String extSum2;

    public CardInfo() {
    }

    public CardInfo(String result, String errorCode) {
        this.result = result;
        this.errorCode = errorCode;
    }
}
