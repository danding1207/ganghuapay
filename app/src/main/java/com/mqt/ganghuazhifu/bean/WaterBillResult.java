package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;
import java.util.ArrayList;
import java.util.List;

/**
 * 水费订单 查询结果
 *
 * @author bo.sun
 *
 */
@Parcel
public class WaterBillResult {

	public String UserNb;// 户号
	public String UserName;// 客户名
	public String UserAddr;// 地址
	public String METER_BOOK;//册簿号
	public String BELONG_Station;//归属站点
	public String AdvPay_Balance;//预存余额
	public String FeeCount;//账期数目
	public List<WaterBillRecord> WaterBillFeeCountDetail;

	public WaterBillResult(
							String userNb,
							String userName, 
							String userAddr, 
							String meter_book, 
							String belong_Station, 
							String advPay_balance, 
							String feeCount,
							List<WaterBillRecord> waterBillFeeCountDetail						
						) {
		
		super();
		UserNb = userNb;
		UserName = userName;
		UserAddr = userAddr;
		METER_BOOK = meter_book;
		BELONG_Station = belong_Station;
		AdvPay_Balance = advPay_balance;
		FeeCount = feeCount;
		WaterBillFeeCountDetail = waterBillFeeCountDetail;		
	}

	public WaterBillResult() {
		WaterBillFeeCountDetail = new ArrayList<WaterBillRecord>();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("UserNb--->" + UserNb + "/n");
		sb.append("UserName--->" + UserName + "/n");
		sb.append("UserAddr--->" + UserAddr + "/n");
		sb.append("METER_BOOK--->" + METER_BOOK + "/n");
		sb.append("BELONG_Station--->" + BELONG_Station + "/n");
		sb.append("AdvPay_Balance--->" + AdvPay_Balance + "/n");
		sb.append("FeeCount--->" + FeeCount + "/n");
		for (WaterBillRecord waterBillRecord : WaterBillFeeCountDetail) {
			sb.append(waterBillRecord.toString());
		}
		return sb.toString();
	}

}