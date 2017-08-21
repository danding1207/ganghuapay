package com.mqt.ganghuazhifu.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class UnitConversionUtils {

	public static final int SCREEN_EDGE_SCALE = 20;

	/**
	 * dp转换为px
	 * 
	 * @param context
	 * @param dpValue
	 *            dp值
	 * @return 转换后px值
	 */
	public static int dipTopx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px转换为dp
	 * 
	 * @param context
	 * @param pxValue
	 *            px值
	 * @return 转换后dp值
	 */
	public static int pxTodip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    } 

	/**
	 * 将px值转换为sp值，保证文字大小不变  
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return          
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变         
	 * 
	 * @param spValue
	 *                     
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return          
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 获取当前屏幕高度
	 * 
	 * @param context
	 * @return 当前屏幕高度
	 */
	public static int getScreenHigh(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int high = dm.heightPixels;
		return high;
	}

	/**
	 * 获取当前屏幕宽度
	 * 
	 * @param context
	 * @return 当前屏幕宽度
	 */
	public static int getScreenWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		return width;
	}

}
