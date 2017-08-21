package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

/**
 * 水费
 * @author bo.sun
 *
 */
@Parcel
public class WaterBillRecord {

	public String FeeMonth;//欠费账期
	public String Index_Now;//本期示度
	public String WATER_NUM;//水量
	public String AMOUNT;//账单金额
	public String AMOUNT_YSF;//用水费
	public String AMOUNT_DZF;//代征费用
	public String AMOUNT_WYJ;//违约金
	public String DATE_Pay;//缴费日期
	public String AMOUNT_Pay;//实缴金额
	public String AMOUNT_SQY;//上期余额
	public String AMOUNT_YE;//本期余额
	
	public WaterBillRecord(String feemonth, String index_now, String water_num, String amount, 
						  String amount_ysf, String amount_dzf, String amount_wyj, String date_pay, 
						  String amount_pay, String amount_sqy, String amount_ye) {
			super();
			FeeMonth = feemonth;
			Index_Now = index_now;
			WATER_NUM = water_num;
			AMOUNT = amount;
			AMOUNT_YSF = amount_ysf;
			AMOUNT_DZF = amount_dzf;
			AMOUNT_WYJ = amount_wyj;
			DATE_Pay = date_pay;
			AMOUNT_Pay = amount_pay;
			AMOUNT_SQY = amount_sqy;
			AMOUNT_YE = amount_ye;
		
	}
	public WaterBillRecord() {
	}
	
	@Override
	public String toString() {
		return "WaterRecord ["
				+ "FeeMonth=" + FeeMonth 
				+ ", Index_Now="+ Index_Now 
				+ ", WATER_NUM=" + WATER_NUM 
				+ ", AMOUNT="+ AMOUNT 
				+ ", AMOUNT_YSF="+ AMOUNT_YSF 
				+ ", AMOUNT_DZF="+ AMOUNT_DZF 
				+ ", AMOUNT_WYJ=" + AMOUNT_WYJ 
				+ ", DATE_Pay=" + DATE_Pay 
				+ ", AMOUNT_Pay=" + AMOUNT_Pay 
				+ ", AMOUNT_SQY=" + AMOUNT_SQY 
				+ ", AMOUNT_YE=" + AMOUNT_YE 
				+ "]";
	}
    
}
