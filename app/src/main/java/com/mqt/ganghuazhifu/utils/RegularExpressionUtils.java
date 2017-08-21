package com.mqt.ganghuazhifu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.orhanobut.logger.Logger;

public class RegularExpressionUtils {

	public static boolean regularExpression_Eng_num_char(
			String regularString) {
		Pattern pattern = Pattern.compile("^[0-9a-zA-Z`~!@#$%^&*()_|/+={}:;<>?\\.\\,\\\"\'\\-\\[\\]]*$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(regularString);
//		Log.i("HSGHADYAFF", matcher.matches() + "");
		return matcher.matches();
	}

	public static boolean regularExpression_Eng(
			String regularString) {
		Logger.i("regularString-->"+"<"+regularString+">");
		Pattern pattern = Pattern.compile("^[a-zA-Z]*$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(regularString);
		return matcher.matches();
	}

	public static boolean regularExpression_Chinese(String regularString) {
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5]*$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(regularString);
		return matcher.matches();
	}

	public static boolean regularExpression (String regularString) {
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa50-9a-zA-Z`~!@#$%^&*()_|+={}:;?\\.\\,\\-\\[\\]]*$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(regularString);
		return matcher.matches();
	}

	public static boolean regularExpression_IdCard(String regularString) {
		Pattern pattern = Pattern.compile("^[0-9x]*$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(regularString);
//		Log.i("HSGHADYAFF", matcher.matches() + "");
		return matcher.matches();
	}
	
}
