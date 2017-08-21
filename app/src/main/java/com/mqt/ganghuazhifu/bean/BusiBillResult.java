package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;
import java.util.ArrayList;
import java.util.List;

/**
 * 营业费账单 查询结果
 * @author yang.lei
 *
 */
@Parcel
public class BusiBillResult {

	public String EasyNo;//速记号
	public String UserNb;//户号
	public String UserName;//客户名
	public String UserAddr;//地址
	public String BusifeeCount;//营业费科目数
	public List<BusiBillRecord> BusifeeCountDetail;//速记号
	
	public BusiBillResult(String easyNo, String userNb, String userName,
			String userAddr, String busifeeCount,
			List<BusiBillRecord> busifeeCountDetail) {
		super();
		EasyNo = easyNo;
		UserNb = userNb;
		UserName = userName;
		UserAddr = userAddr;
		BusifeeCount = busifeeCount;
		BusifeeCountDetail = busifeeCountDetail;
	}

	public BusiBillResult() {
		BusifeeCountDetail = new ArrayList<BusiBillRecord>();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("EasyNo--->" + EasyNo + "\n");
		sb.append("UserNb--->" + UserNb + "\n");
		sb.append("UserName--->" + UserName + "\n");
		sb.append("UserAddr--->" + UserAddr + "\n");
		sb.append("BusifeeCount--->" + BusifeeCount + "\n");
		for (BusiBillRecord busiBillRecord : BusifeeCountDetail) {
			sb.append(busiBillRecord.toString());
		}
		return sb.toString();
	};
}
