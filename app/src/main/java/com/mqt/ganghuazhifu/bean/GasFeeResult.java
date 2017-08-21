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
    public String UserName;// 客户名
    public String UserAddr;// 地址
    public String HasBusifee;// 营业费标记 0：无营业欠费 ；1：有营业费欠费
    public String AllGasfee;// 应收总额
    public String FeeCount;// 账期数目
    public String DueMonth;// 账期
    public List<GasFeeRecord> FeeCountDetail;
    public String QuerySeq;//查询序列号

    public String NFCSecurAlert;//安检提示信息
    public float NFCLimitGasFee;//NFC限购金额
    public float NFCNotWriteGas;//NFC未写卡金额
    public int NFCICSumCount;//NFCIC卡购气次数

    public String ICcardNo;//IC卡号
    public float UserCanAmount;//用户本次可购气量
    public float PriceId;//用户气价

    public String QueryId;//IC卡号


    public GasFeeResult(String provinceCode, String cityCode, String easyNo,
                        String userNb, String userName, String userAddr, String hasBusifee,
                        String allGasfee, String feeCount, List<GasFeeRecord> feeCountDetail, String querySeq,
                        String nFCSecurAlert, float nFCLimitGasFee, float nFCNotWriteGas,
                        String iCcardNo, float userCanAmount, float priceId,
                        String nFCFlag, String queryId
    ) {

        super();
        ProvinceCode = provinceCode;
        CityCode = cityCode;
        EasyNo = easyNo;
        UserNb = userNb;
        UserName = userName;
        UserAddr = userAddr;
        HasBusifee = hasBusifee;
        AllGasfee = allGasfee;
        FeeCount = feeCount;
        FeeCountDetail = feeCountDetail;
        QuerySeq = querySeq;
        NFCSecurAlert = nFCSecurAlert;
        NFCLimitGasFee = nFCLimitGasFee;
        NFCNotWriteGas = nFCNotWriteGas;
        NFCFlag = nFCFlag;
        ICcardNo = iCcardNo;
        UserCanAmount = userCanAmount;
        PriceId = priceId;
        QueryId = queryId;
    }

    public GasFeeResult() {
        FeeCountDetail = new ArrayList<>();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ProvinceCode--->" + ProvinceCode + "/n");
        sb.append("CityCode--->" + CityCode + "/n");
        sb.append("EasyNo--->" + EasyNo + "/n");
        sb.append("UserNb--->" + UserNb + "/n");
        sb.append("UserName--->" + UserName + "/n");
        sb.append("UserAddr--->" + UserAddr + "/n");
        sb.append("HasBusifee--->" + HasBusifee + "/n");
        sb.append("AllGasfee--->" + AllGasfee + "/n");
        sb.append("FeeCount--->" + FeeCount + "/n");
        sb.append("QuerySeq--->" + QuerySeq + "/n");
        sb.append("NFCSecurAlert--->" + NFCSecurAlert + "/n");
        sb.append("NFCLimitGasFee--->" + NFCLimitGasFee + "/n");
        sb.append("NFCNotWriteGas--->" + NFCNotWriteGas + "/n");
        sb.append("NFCFlag--->" + NFCFlag + "/n");
        sb.append("ICcardNo--->" + ICcardNo + "/n");
        sb.append("UserCanAmount--->" + UserCanAmount + "/n");
        sb.append("PriceId--->" + PriceId + "/n");
        sb.append("QueryId--->" + QueryId + "/n");
        for (GasFeeRecord gasFeeRecord : FeeCountDetail) {
            if (gasFeeRecord != null)
                sb.append(gasFeeRecord.toString());
        }
        return sb.toString();
    }

}
