package com.mqt.ganghuazhifu.utils;

public class DateTextUtils {

	public static String DateToString(int date) {
		if(date>9) {
			return String.valueOf(date);
		} else {
			return "0"+date; 
		}
	}
	
}
