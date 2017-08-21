package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * 气费账单 查询结果
 * @author yang.lei
 *
 */
@Parcel
public class GasBillResult {

	public String PayeeCode ;// 收款单位编码
	public String ProvinceCode;// 省编码
	public String CityCode;// 市编码
	public String HasBusifee;//速记号
	public String EasyNo;//速记号
	public String UserNb;//户号
	public String UserName;//客户名
	public String UserAddr;//地址
	public String FeeCount;//账期数目
	public String GasPrice;//气费单价
	public ArrayList<GasBillRecord> FeeCountDetail;
	public GasBillResult(String hasBusifee,String easyNo, String userNb, String userName, 
			String gasPrice, String userAddr, String feeCount,
						 ArrayList<GasBillRecord> feeCountDetail) {
		super();
		HasBusifee = hasBusifee;
		EasyNo = easyNo;
		UserNb = userNb;
		UserName = userName;
		GasPrice = gasPrice;
		UserAddr = userAddr;
		FeeCount = feeCount;
		FeeCountDetail = feeCountDetail;
	}
	
	public GasBillResult() {
		FeeCountDetail = new ArrayList<GasBillRecord>();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProvinceCode--->" + ProvinceCode + "\n");
		sb.append("CityCode--->" + CityCode + "\n");
		sb.append("EasyNo--->" + EasyNo + "\n");
		sb.append("UserNb--->" + UserNb + "\n");
		sb.append("UserName--->" + UserName + "\n");
		sb.append("GasPrice--->" + GasPrice + "\n");
		sb.append("UserAddr--->" + UserAddr + "\n");
		sb.append("HasBusifee--->" + HasBusifee + "\n");
		sb.append("FeeCount--->" + FeeCount + "\n");
		for (GasBillRecord gasBillRecord : FeeCountDetail) {
			sb.append(gasBillRecord.toString());
		}
		return sb.toString();
	}
	
}
