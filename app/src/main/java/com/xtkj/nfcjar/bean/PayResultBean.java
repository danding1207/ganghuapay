package com.xtkj.nfcjar.bean;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class PayResultBean {
    public int result;
    public int payTimes;
    public String remainMoney;
    public String totalPay;
    public String totalUse;
    public String meterState;
    public int valWay;
    public List<String> historyMonthList;
    public List<String> historyDosList;
    public String adjustDate;
    public String adjustBottomNum;
    public String payDate;
    public String payBottomNum;

    public PayResultBean() {
    }

    public PayResultBean(int res) {
        this.result = res;
    }

    public String toString() {
        return "PayResultBean [result=" + this.result + ", payTimes=" + this.payTimes + ", remainMoney=" + this.remainMoney + ", totalPay=" + this.totalPay + ", totalUse=" + this.totalUse + ", meterState=" + this.meterState + ", valWay=" + this.valWay + ", historyMonthList=" + this.historyMonthList + ", historyDosList=" + this.historyDosList + ", adjustDate=" + this.adjustDate + ", adjustBottomNum=" + this.adjustBottomNum + ", payDate=" + this.payDate + ", payBottomNum=" + this.payBottomNum + "]";
    }
}
