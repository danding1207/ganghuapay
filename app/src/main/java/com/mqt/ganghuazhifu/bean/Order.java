package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class Order {

	public String OrderNb;// 订单号

	public String OrderSetTime;// 订单创建时间

	public String ProductName;// 商品名称

	public String OrderAmount;// 订单金额

	public String usernb;// 订单户号
	
	public Order() {
	}

	@Override
	public String toString() {
		return "Order [OrderNb=" + OrderNb + ", OrderSetTime=" + OrderSetTime + ", ProductName=" + ProductName
				+ ", OrderAmount=" + OrderAmount
				+ ", usernb=" + usernb+ "]";
	}

}
