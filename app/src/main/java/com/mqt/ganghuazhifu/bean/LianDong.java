package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class LianDong {

	public String orderid;// 商户唯一订单号
	public String tradeno;// U付订单号
	public String merdate;// 订单日期
	public double amount;// 付款金额
	public String amttype;// 付款币种
	public String paytype;// 支付方式
	public String gateid;// 支付银行
	public String mediaid;// 手机号
	public String cardid;// 卡号
	public String identitytype;// 证件类型
	public String identitycode;// 证件号
	public String cardholder;// 持卡人姓名
	public String validdate;// 信用卡有效期
	public String cvv2;// 信用卡CVN2/CVV2
	public String flag;// 签约标识 10：未签约;11：已签约

	public LianDong() {
	}

	@Override
	public String toString() {
		return "LianDong [orderid=" + orderid + ", tradeno=" + tradeno + ", merdate=" + merdate + ", amount="
				+ amount + ", amttype=" + amttype + ", paytype=" + paytype + ", gateid=" + gateid + ", mediaid="
				+ mediaid + ", cardid=" + cardid + ", identitytype=" + identitytype + ", identitycode="
				+ identitycode + ", cardholder=" + cardholder + ", validdate=" + validdate + ", cvv2=" + cvv2
				+ ", flag=" + flag + "]";
	}

}
