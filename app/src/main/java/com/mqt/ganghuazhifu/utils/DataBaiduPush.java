package com.mqt.ganghuazhifu.utils;

import android.R.bool;

import com.mqt.ganghuazhifu.BaseActivity;
import com.mqt.ganghuazhifu.R.string;

/**
 * 百度云推送，绑定数据
 * 
 * @author bo.sun
 * @date 2015-07-20
 */
public class DataBaiduPush {
	private static String errorCode = null ;
	private static String appid = "";
	private static String userId = "";
	private static String channelId = "";
	private static String title = null;
	private static String description = null;
	private static BaseActivity topActivity = null;
	private static String pushStatus = null;
	private static String pmttp = null;
	private static boolean isActive = true;
	private static int GOW = 0;
	private static String pmttpType = null;
	private static int haveUnit = 0;
	
	public static String getPushStatus(){
		return pushStatus;
	}
	
	public static void setPushStatus(String a){
		DataBaiduPush.pushStatus = a;
	}	
	
	public static String getErrorCode(){
		return errorCode;
	}
	
	public static void setErrorCode(String a){
		DataBaiduPush.errorCode = a;
	}
	
	public static String getAppId(){
		return appid;
	}
	
	public static void setAppId(String a){
		DataBaiduPush.appid = a;
	}
	
	public static String getUserId(){
		return userId;
	}
	
	public static void setUserId(String a){
		DataBaiduPush.userId = a;
	}
	
	public static String getChannelId(){
		return channelId;
	}
	
	public static void setChannelId(String a){
		DataBaiduPush.channelId = a;
	} 	 	
	
	public static String getTitle(){
		return title;
	}
	
	public static void setTitle(String a){
		DataBaiduPush.title = a;
	}

	public static String getDescription(){
		return description;
	}
	
	public static void setDescription(String a){
		DataBaiduPush.description = a;
	}
	
	public static BaseActivity getTopActivity(){
		return topActivity;
	}
	
	public static void setTopActivity(BaseActivity a){
		DataBaiduPush.topActivity = a;
	}
	
	public static String getPmttp(){
		return pmttp;
	}
	
	public static void setPmttp(String a){
		DataBaiduPush.pmttp = a;
	}
	
	public static boolean getIsActive(){
		return isActive;
	}
	
	public static void setIsActive(boolean a){
		DataBaiduPush.isActive = a;
	}
	
	public static int getGOW(){
		return GOW;
	}
	
	public static void setGOW(int a){
		DataBaiduPush.GOW = a;
	}
	
	public static String getPmttpType(){
		return pmttpType;
	}
	
	public static void setPmttpType(String a){
		DataBaiduPush.pmttpType = a;
	}
	
	public static int getHaveUnit(){
		return haveUnit;
	}
	
	public static void setHaveUnit(int a){
		DataBaiduPush.haveUnit = a;
	}
}

