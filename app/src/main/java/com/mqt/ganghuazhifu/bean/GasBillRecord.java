package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

/**
 * 气费账单
 * @author yang.lei
 *
 */
@Parcel
public class GasBillRecord {

	public String FeeMonth;//欠费账期
	public String LastReading;//上期止码
	public String CurrentReading;//本期止码
	public String GasNb;//气量(立方数)
	public String CurrentTotalAmount;//当期欠费总额
	public String PayAmount;//已缴气费
	public String Remark;//备注
	public String GasPrice;//气费单价
	
	public GasBillRecord() {
	}
	
	@Override
	public String toString() {
		return "GasBillRecord [FeeMonth=" + FeeMonth + ", LastReading="
				+ LastReading + ", CurrentReading=" + CurrentReading
				+ ", GasNb=" + GasNb + ", CurrentTotalAmount="
				+ CurrentTotalAmount + ", PayAmount=" + PayAmount + ", Remark="
				+ Remark + ", GasPrice=" + GasPrice + "]";
	}
	
}
