package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

/**
 * 营业费
 * @author yang.lei
 *
 */
@Parcel
public class BusiFeeRecord {

	public String BusiType;//营业费类别
	public String BusiAmount;//营业费金额

	public BusiFeeRecord(String busiType, String busiAmount) {
		super();
		BusiType = busiType;
		BusiAmount = busiAmount;
	}
	public BusiFeeRecord() {
	}

}
