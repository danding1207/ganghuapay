package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * 水费欠费查询结果
 *
 * @author bo.sun
 */
@Parcel
public class WaterFeeResult {

    public String UserNb;// 户号
    public String UserName;// 客户名
    public String UserAddr;// 地址
    public String AB;//预付款余额（水务）

    public String AllWaterFee;//应收总额
    public String TSH;//托收合同号
    public String FEE_YJSF;//应缴水费
    public String FEE_YJZNJ;//应缴滞纳金违约金
    public String FEE_MON;//欠费记录账务年月区间

    public String FeeCount;// 账期数目

    public String PayeeCode;// 收款单位编码
    public String ProvinceCode;// 省编码
    public String CityCode;// 市编码

    public String QueryId;// 市编码

    public List<WaterFeeRecord> WaterFeeCountDetail;


    public WaterFeeResult(String provinceCode, String cityCode,
                          String userNb, String userName, String userAddr, String feeCount,
                          String ab, List<WaterFeeRecord> waterfeeCountDetail,
                          String allWaterFee, String tsh, String fee_yjsf, String fee_yjznj, String fee_mon, String queryId
    ) {

        super();
        ProvinceCode = provinceCode;
        CityCode = cityCode;
        UserNb = userNb;
        UserName = userName;
        UserAddr = userAddr;
        FeeCount = feeCount;
        AB = ab;
        WaterFeeCountDetail = waterfeeCountDetail;
        AllWaterFee = allWaterFee;
        TSH = tsh;
        FEE_YJSF = fee_yjsf;
        FEE_YJZNJ = fee_yjznj;
        FEE_MON = fee_mon;
        QueryId = queryId;
    }

    public WaterFeeResult() {
        WaterFeeCountDetail = new ArrayList<WaterFeeRecord>();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ProvinceCode--->" + ProvinceCode + "/n");
        sb.append("CityCode--->" + CityCode + "/n");
        sb.append("UserNb--->" + UserNb + "/n");
        sb.append("UserName--->" + UserName + "/n");
        sb.append("UserAddr--->" + UserAddr + "/n");
        sb.append("FeeCount--->" + FeeCount + "/n");
        sb.append("AB--->" + AB + "/n");
        sb.append("AllWaterFee--->" + AllWaterFee + "/n");
        sb.append("TSH--->" + TSH + "/n");
        sb.append("FEE_YJSF--->" + FEE_YJSF + "/n");
        sb.append("FEE_YJZNJ--->" + FEE_YJZNJ + "/n");
        sb.append("FEE_MON--->" + FEE_MON + "/n");
        sb.append("QueryId--->" + QueryId + "/n");
        for (WaterFeeRecord waterFeeRecord : WaterFeeCountDetail) {
            sb.append(waterFeeRecord.toString());
        }
        return sb.toString();
    }

}

