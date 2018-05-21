package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * 气费 查询结果
 *
 * @author yang.lei
 */
@Parcel
public class GasFeeResult {

    public String PayeeCode;// 收款单位编码
    public String ProvinceCode;// 省编码
    public String CityCode;// 市编码
    public String EasyNo;// 速记号
    public String UserNb;// 户号
    public String NFCFlag;// NFC标识
    public String MeterType;// 金额表类型
    public String SecurAlert;// NFC表安检提醒
    public float LimitGasfee;// 限购金额
    public String NfcSumcount;// 购气次数
    public String IcCardno;// IC卡号
    public String UserName;// 客户名
    public String UserAddr;// 地址
    public String HasBusifee;// 营业费标记 0：无营业欠费 ；1：有营业费欠费
    public String AllGasfee;// 应收总额
    public String FeeCount;// 账期数目
    public String DueMonth;// 账期
    public List<GasFeeRecord> FeeCountDetail;
    public String QuerySeq;//查询序列号

    public float NFCNotWriteGas;//NFC未写卡金额

    public float UserCanAmount;//用户本次可购气量
    public float PriceId;//用户气价

    public String QueryId;//查询号




    public GasFeeResult() {
        FeeCountDetail = new ArrayList<>();
    }

    public GasFeeResult(String payeeCode, String provinceCode, String cityCode, String easyNo,
                        String userNb, String NFCFlag, String meterType, String securAlert,
                        float limitGasfee, String nfcSumcount, String icCardno, String userName,
                        String userAddr, String hasBusifee, String allGasfee, String feeCount,
                        String dueMonth, List<GasFeeRecord> feeCountDetail, String querySeq,
                        float NFCNotWriteGas, float userCanAmount, float priceId,
                        String queryId) {
        PayeeCode = payeeCode;
        ProvinceCode = provinceCode;
        CityCode = cityCode;
        EasyNo = easyNo;
        UserNb = userNb;
        this.NFCFlag = NFCFlag;
        MeterType = meterType;
        SecurAlert = securAlert;
        LimitGasfee = limitGasfee;
        NfcSumcount = nfcSumcount;
        IcCardno = icCardno;
        UserName = userName;
        UserAddr = userAddr;
        HasBusifee = hasBusifee;
        AllGasfee = allGasfee;
        FeeCount = feeCount;
        DueMonth = dueMonth;
        FeeCountDetail = feeCountDetail;
        QuerySeq = querySeq;
        this.NFCNotWriteGas = NFCNotWriteGas;
        UserCanAmount = userCanAmount;
        PriceId = priceId;
        QueryId = queryId;
    }


    @Override
    public String toString() {
        return "GasFeeResult{" +
                "PayeeCode='" + PayeeCode + '\'' +
                ", ProvinceCode='" + ProvinceCode + '\'' +
                ", CityCode='" + CityCode + '\'' +
                ", EasyNo='" + EasyNo + '\'' +
                ", UserNb='" + UserNb + '\'' +
                ", NFCFlag='" + NFCFlag + '\'' +
                ", MeterType='" + MeterType + '\'' +
                ", SecurAlert='" + SecurAlert + '\'' +
                ", LimitGasfee='" + LimitGasfee + '\'' +
                ", NfcSumcount='" + NfcSumcount + '\'' +
                ", IcCardno='" + IcCardno + '\'' +
                ", UserName='" + UserName + '\'' +
                ", UserAddr='" + UserAddr + '\'' +
                ", HasBusifee='" + HasBusifee + '\'' +
                ", AllGasfee='" + AllGasfee + '\'' +
                ", FeeCount='" + FeeCount + '\'' +
                ", DueMonth='" + DueMonth + '\'' +
                ", FeeCountDetail=" + FeeCountDetail +
                ", QuerySeq='" + QuerySeq + '\'' +
                ", NFCNotWriteGas='" + NFCNotWriteGas + '\'' +
                ", UserCanAmount=" + UserCanAmount +
                ", PriceId=" + PriceId +
                ", QueryId='" + QueryId + '\'' +
                '}';
    }
}
