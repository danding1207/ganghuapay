package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

/**
 * 气费 查询结果
 *
 * @author yang.lei
 */
@Parcel
public class IcChangeResult {

    public String Amount;// 气量 对应 金额
    public String ErrorCode;//
    public String ErrorDetail;//
    public String PayeeCode;//
    public String UserNb;// 户号

    public IcChangeResult(String amount, String errorCode, String errorDetail,
                          String userNb, String payeeCode) {
        super();
        Amount = amount;
        ErrorCode = errorCode;
        ErrorDetail = errorDetail;
        UserNb = userNb;
        PayeeCode = payeeCode;
    }

    public IcChangeResult() {
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Amount--->" + Amount + "/n");
        sb.append("ErrorCode--->" + ErrorCode + "/n");
        sb.append("ErrorDetail--->" + ErrorDetail + "/n");
        sb.append("UserNb--->" + UserNb + "/n");
        sb.append("PayeeCode--->" + PayeeCode + "/n");
        return sb.toString();
    }

}
