//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xtkj.nfcjar.bean;

import java.io.Serializable;
import java.util.List;

public class ReadResultBean implements Serializable {
    private static final long serialVersionUID = -556807072405592956L;
    public int result;
    public String meterNum;
    public String currentTime;
    public float currentPrice;
    public String remainedMoney;
    public String totalBuy;
    public String totalUse;
    public int noUseDay;
    public int noUseSecond;
    public String meterState;
    public String dealWord;
    public int securityCount;
    public List<String> securityRecords;
    public String recentColseRec;
    public int nfcTimes;
    public String nfcBuy;
    public String nfcTotalBuy;
    public List<String> historyList;

    public ReadResultBean(int result) {
        this.result = result;
    }

    public ReadResultBean() {
    }
}
