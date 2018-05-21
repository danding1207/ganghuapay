package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class PaymentResult {

    public String OrderNb;//订单号
    public String Pmttp;//业务类型
    public String PayeeName;//收款单位名称
    public String PayeeCode;//收款单位Code
    public String UserNb;//缴费户号
    public String UserNm;//缴费户名
    public String OrderSetTime;//订单创建时间
    public String PaymentTime;//付款时间
    public String PayDate;//缴费日期
    public String PayTime;//缴费成功时间
    public String Amount;//交易金额
    public String Mount;//交易气量
    public String Status;//支付状态
    public String PayStatus;//缴费状态
    public String ErrorMsg;//失败原因
    public String NFCFlag;//NFC标识：10不是NFC；11是NFC
    public int NFCICSumCount;//写卡次数
    public String ICcardNo;//IC 卡号 16位 前补8个0
}
