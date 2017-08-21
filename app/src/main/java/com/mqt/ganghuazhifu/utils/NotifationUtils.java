package com.mqt.ganghuazhifu.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import java.util.List;

public class NotifationUtils {

	public boolean isEmpty(String s) {
		if (null == s)
			return true;
		if (s.length() == 0)
			return true;
		else
			return false;
	}

	public void showToast(String toast, Context context) {
		// if (!isAppOnForeground(context)) return;
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// Looper.prepare();
		// Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
		// Looper.loop();
		// }
		// }).start();
	}

	public boolean isAppOnForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// Returns a list of application processes that are running on the
		// device
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

		if (appProcesses == null) {
			return false;
		}

		for (RunningAppProcessInfo info : appProcesses) {
			// importance:
			// The relative importance level that the system places
			// on this process.
			// May be one of IMPORTANCE_FOREGROUND, IMPORTANCE_VISIBLE,
			// IMPORTANCE_SERVICE, IMPORTANCE_BACKGROUND, or IMPORTANCE_EMPTY.
			// These constants are numbered so that "more important" values are
			// always smaller than "less important" values.
			// processName:
			// The name of the process that this object is associated with.
			if (info.processName == context.getPackageName()
					&& info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	public <T> String join(String cement, List<T> array) {
		StringBuilder builder = new StringBuilder();

		if (array == null || array.size() == 0) {
			return null;
		}

		for (T t : array) {
			builder.append(t).append(cement);
		}

		builder.delete(builder.length() - cement.length(), builder.length());

		return builder.toString();
	}

	public boolean isNetworkEnabled(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	public String getImei(Context context, String imei) {
		// String imei = imei;
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();
		return imei;
	}

	public String getAppKey(Context context) {
		Bundle metaData = null;
		String appKey = null;
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				appKey = metaData.getString("YUNBA_APPKEY");
				if (null == appKey || appKey.length() != 24) {
					appKey = "Error";
				}
			}
		} catch (NameNotFoundException e) {

		}
		return appKey;
	}

}
