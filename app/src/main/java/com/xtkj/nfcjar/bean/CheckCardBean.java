//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xtkj.nfcjar.bean;

import com.xtkj.nfcjar.bean.PriceGroup;
import java.io.Serializable;
import java.util.List;

public class CheckCardBean implements Serializable {
    private static final long serialVersionUID = -6075476871201187921L;
    public int result;
    public int secretVerson;
    public String userNum;
    public String cardBuyDate;
    public String cardValidDate;
    public String userType;
    public int cardBuyTimes;
    public int blabFun;
    public int serialHours;
    public int warnLockFun;
    public int longNotUseLocFkFun;
    public int notUseDay1;
    public int notUseDay2;
    public int overFun;
    public int overCount;
    public int overTimeFun;
    public int overTime;
    public int limitBuyFun;
    public String limitUp;
    public int lowWarnMoney;
    public int warn1Fun;
    public int warn1Value;
    public int warn2Fun;
    public int warn2Value;
    public int zeroClose;
    public int securityFun;
    public int securityMonth;
    public int scrapFun;
    public int scrapYear;
    public int versonFlag;
    public String cardFlag;
    public int recordDay;
    public PriceGroup curPriceG1;
    public PriceGroup curPriceG2;
    public PriceGroup newPriceG1;
    public PriceGroup newPriceG2;
    public String newPriceStartTime;
    public List<String> priceStartRepeat;
    public String currentTime;
    public int currentPrice;
    public String remainedMoney;
    public String totalBuy;
    public String totalUse;
    public int noUseDay;
    public int noUseSecond;
    public String meterState;
    public String dealWord;
    public int securityCount;
    public List<String> securityRecords;
    public int recentColseRec;
    public int nfcTimes;
    public String nfcBuy;
    public String nfcTotalBuy;
    public List<String> historyGasList;
    public List<String> historyMonthList;
    public String adjustDate;
    public String adjustBottomNum;
    public String payDate;
    public String payBottomNum;
    public List<String> newPriceRepeat;
    public String valuationWay;

    public CheckCardBean() {
    }

    public CheckCardBean(int result) {
        this.result = result;
    }
}
