package com.mqt.ganghuazhifu.utils;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author yang.lei
 */
public class NetworkUtil {
    
	/**
	 * 网络类型(全部小写)
	 */
	private static String mNetworkType = "";

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 *            环境对象
	 * @return true 有网络，false 无网络
	 */
	public static boolean isNetWorking(Context context) {
		if(context == null){
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			if (networkInfo.getTypeName().equals("WIFI")) {
				mNetworkType = networkInfo.getTypeName().toLowerCase(Locale.ENGLISH);
			} else {
				if (networkInfo.getExtraInfo() == null) {
					mNetworkType = "";
				} else {
					mNetworkType = networkInfo.getExtraInfo().toLowerCase(Locale.ENGLISH);
				}
			}
			return true;
		} else {
			mNetworkType = "";
			return false;
		}
	}
	public static boolean isNet2G3G(Context context) {
		boolean is2g3g = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();

		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			// NETWORK_TYPE_EVDO_A是电�?G
			// NETWORK_TYPE_EVDO_A是中国电�?G的getNetworkType
			// NETWORK_TYPE_CDMA电信2G是CDMA
			// NETWORK_TYPE_EDGE 移动2G�?
			// China Unicom 1 NETWORK_TYPE_GPRS 联�?�?G
			if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS
					|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA
					|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
				is2g3g = true;
			} else {
				is2g3g = false;
			}
		} else {
			is2g3g = false;
		}
		return is2g3g;
	}
	
	public static void startNetSetting(Context context){
		Intent intent=null;
        //判断手机系统的版�? 即API大于10 就是3.0或以上版�?
        if(android.os.Build.VERSION.SDK_INT>10){
            intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        }else{
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
	}

	/**
	 * 获取网络类型
	 * 
	 * @return 网络类型
	 */
	public static String getNetworkType() {
		if(mNetworkType == null){
			mNetworkType = "";
		}
		return mNetworkType;
	}
	
	public static boolean isWifiNetwork(Context context){
		if(context == null){
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			if (networkInfo.getTypeName().equals("WIFI")) {
				return true;
			}
		}
		return false;
	}
	
}
