package com.mqt.ganghuazhifu.bean;


import org.parceler.Parcel;

/**
 * 水费
 * @author bo.sun
 *
 */
@Parcel
public class WaterFeeRecord {

	public String FeeId;//水费记录号
	public String FeeMonth;//欠费账期
	public String WaterNb;//用水量
	public String LastBal;//上期结余
	public String Amount;//账单金额
	public String CurrentBal;//本期结余
	public String LateAmount;//违约金
	public WaterFeeRecord(String feeId, String feeMonth, String waterNb, 
			String lastBal,String amount,String currentBal,String lateAmount) {
		super();
		FeeId = feeId;
		FeeMonth = feeMonth;
		WaterNb = waterNb;
		LastBal = lastBal;
		Amount = amount;
		CurrentBal = currentBal;
		LateAmount = lateAmount;
		
	}
	public WaterFeeRecord() {
	}
	
	@Override
	public String toString() {
		return "WaterFeeRecord [FeeId=" + FeeId + ", FeeMonth="
				+ FeeMonth + ", WaterNb=" + WaterNb + ", LastBal="
				+ LastBal + ", Amount="
				+ Amount + ", CurrentBal="
				+ CurrentBal + ", LateAmount=" 
				+ LateAmount + "]";
	}
    
}

