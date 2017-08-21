package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 营业费 查询结果
 * @author yang.lei
 *
 */
@Parcel
public class BusiFeeResult {

	public String CityCode;// = city.CityCode;
	public String ProvinceCode;// = province.CityCode;
	public String PayeeCode;//
	public String EasyNo;//速记号
	public String UserNb;//户号
	public String UserName;//客户名
	public String UserAddr;//地址
	public String BusifeeCount;//营业费科目数
	public String AllBusifee;//营业欠费总额

	public String PRESAVING;//营业预存总额

	public List<BusiFeeRecord> BusifeeCountDetail;//速记号
	public String QueryId;//营业欠费总额


	public BusiFeeResult(String easyNo, String userNb, String userName,
			String userAddr, String busifeeCount, String allBusifee,
			List<BusiFeeRecord> busifeeCountDetail, String queryId, String pRESAVING) {
		super();
		EasyNo = easyNo;
		UserNb = userNb;
		UserName = userName;
		UserAddr = userAddr;
		BusifeeCount = busifeeCount;
		AllBusifee = allBusifee;
		BusifeeCountDetail = busifeeCountDetail;
		QueryId = queryId;
		PRESAVING = pRESAVING;
	}
	public BusiFeeResult() {
		BusifeeCountDetail = new ArrayList<>();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("EasyNo--->" + EasyNo + "\n");
		sb.append("UserNb--->" + UserNb + "\n");
		sb.append("UserName--->" + UserName + "\n");
		sb.append("UserAddr--->" + UserAddr + "\n");
		sb.append("BusifeeCount--->" + BusifeeCount + "\n");
		sb.append("AllBusifee--->" + AllBusifee + "\n");
		sb.append("PRESAVING--->" + PRESAVING + "\n");
		sb.append("QueryId--->" + QueryId + "\n");
		for (BusiFeeRecord busiFeeRecord : BusifeeCountDetail) {
			sb.append(busiFeeRecord.toString());
		}
		return sb.toString();
	}
	
}
