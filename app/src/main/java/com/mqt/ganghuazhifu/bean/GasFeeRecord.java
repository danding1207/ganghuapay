package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

import java.util.List;

/**
 * 气费
 * 
 * @author yang.lei
 *
 */
@Parcel
public class GasFeeRecord {

	public String FeeMonth;// 2014年8月 (欠费账期)
	public String UserPresave;// 上月余额 0元 (上期结余)
	public String GasNb;// 使用：12m2燃气 气量(立方数)
	public String CurrentGasTotalAmount;// 气费金额：80.00元 当期气费总额
	public String CurrentLateAmount;// 违约金滞纳金：20.00元
	public String CurrentOtherAmount;// 其他费用：0元
	public String CurrentTotalAmount;// 欠费总额：50.00元 当期欠费总额
	public String Remark;// 备注
	public String CurrentDeadLines;// 当前账期截止缴费日期
	public String isChecked;// 缴费标志 true|false
	public List<String> Details;

	public GasFeeRecord(String feeMonth, String userPresave, String gasNb, String currentGasTotalAmount,
			String currentLateAmount, String currentOtherAmount, String currentTotalAmount,
			String currentDeadLines, String remark, String ischecked, List<String> details) {
		super();
		FeeMonth = feeMonth;
		UserPresave = userPresave;
		GasNb = gasNb;
		CurrentGasTotalAmount = currentGasTotalAmount;
		CurrentLateAmount = currentLateAmount;
		CurrentOtherAmount = currentOtherAmount;
		CurrentTotalAmount = currentTotalAmount;
		Remark = remark;
		CurrentDeadLines = currentDeadLines;
		isChecked = ischecked;
		Details = details;
	}

	public GasFeeRecord() {
	}

	@Override
	public String toString() {
		return "GasFeeRecord [FeeMonth=" + FeeMonth + ", UserPresave=" + UserPresave
				+ ", GasNb=" + GasNb + ", CurrentGasTotalAmount=" + CurrentGasTotalAmount + ", CurrentLateAmount="
				+ CurrentLateAmount + ", CurrentOtherAmount=" + CurrentOtherAmount + ", CurrentTotalAmount="
				+ CurrentTotalAmount + ", Remark=" + Remark + ", CurrentDeadLines=" + CurrentDeadLines
				+ ", isChecked=" + isChecked + "]";
	}

}
