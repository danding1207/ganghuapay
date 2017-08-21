package com.mqt.ganghuazhifu.bean;

import java.util.List;

public class RecordsList {

	public List<Record> QryResults;
	public int year;
	public int month;
	
	public RecordsList(List<Record> list, int year, int month) {
		super();
		this.QryResults = list;
		this.year = year;
		this.month = month;
	}

	public RecordsList() {
	}
}
