package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class BindedBanks {

	public String paytype;// 支付方式
	public String gateid;// 支付银行
	public String mediaid;// 手机号 
	public String cardid;// 卡号
	
	public BindedBanks(String paytype, String gateid, String mediaid, String cardid) {
		super();
		this.paytype = paytype;
		this.gateid = gateid;
		this.mediaid = mediaid;
		this.cardid = cardid;
	}

	public BindedBanks() {
	}
}
