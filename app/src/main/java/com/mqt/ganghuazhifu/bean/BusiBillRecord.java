package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

/**
 * 营业费账单
 * @author yang.lei
 *
 */
@Parcel
public class BusiBillRecord {

	public String BusiType;//营业费类别
	public String BusiAmount;//营业费金额
	public String BusiAlreadyPayAmount;//已缴营业费
	public String BusiDate;//欠费日期
	
	public BusiBillRecord(String busiType, String busiAmount, String date) {
		super();
		BusiType = busiType;
		BusiAmount = busiAmount;
		BusiDate = date;
	}

	public BusiBillRecord() {
	}
	
	@Override
	public String toString() {
		return "BusiBillRecord [BusiType=" + BusiType + ", BusiAmount="
				+ BusiAmount + ", BusiDate=" + BusiDate + "]";
	}
	
}
