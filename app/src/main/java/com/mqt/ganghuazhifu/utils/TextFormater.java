package com.mqt.ganghuazhifu.utils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.Log;

public class TextFormater {
	
	/**
	 * 文件大小 long整形转换为字符串
	 * @param size
	 * @return
	 */
	public static String dataSizeFormat(long size) {
		DecimalFormat formater = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "B";
		} else if (size < (1 << 20)) // 左移20位，相当于1024 * 1024
		{
			float kSize = size >> 10; // 右移10位，相当于除以1024
			Log.i("SB", "kSize---" + kSize);
			return formater.format(kSize) + "KB";
		} else if (size < (1 << 30)) // 左移30位，相当于1024 * 1024 * 1024
		{
			float mSize = size >> 20; // 右移20位，相当于除以1024再除以1024
			Log.i("SB", "mSize---" + mSize);
			return formater.format(mSize) + "MB";
		} else if (size < (1 << 40)) {
			float gSize = size >> 30;
			return formater.format(gSize) + "GB";
		} else {
			return "size : error";
		}
	}

	/**
	 * 文件大小 字符串（单位KB）转换为long整形
	 * @param kSize
	 * @return
	 */
	public static String getSizeFromKB(long kSize) {
		return dataSizeFormat(kSize << 10);
	}

	/**
	 * @return 返回指定笔和指定字符串的长度
	 */
	public static float getFontlength(Paint paint, String str) {
		return paint.measureText(str);
	}

	/**
	 * @return 返回指定笔的文字高度
	 */
	public static float getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.descent - fm.ascent;
	}

	/**
	 * @return 返回指定笔离文字顶部的基准距离
	 */
	public static float getFontLeading(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.leading - fm.ascent;
	}

	/**
	 * 去除字符串中 空格 等符号
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

}
