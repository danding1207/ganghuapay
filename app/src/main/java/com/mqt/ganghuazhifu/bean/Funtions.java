package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class Funtions {

	public String name;
	public String tip;
	public String status;
	public int leftResID;
	public int rightResID;//1:有;2:无;3:待定;

	public Funtions(String name, String tip, String status, int leftResID,
			int rightResID) {
		super();
		this.name = name;
		this.tip = tip;
		this.status = status;
		this.leftResID = leftResID;
		this.rightResID = rightResID;
	}

	public Funtions() {
	}
}
