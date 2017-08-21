package com.mqt.ganghuazhifu.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * 获取手机基本信息，用于激活功能
 * 
 * @author jodhan
 *
 */
public class PhoneActiveInfo {

	public PhoneActiveInfo(Context context) {
		setPhoneInfo(context);
		getInstallationId(context);
	}

	private String version; // 软件版本号
	private String brand; // 手机制造商
	private String model; // 手机型号
	private String release; // 手机系统版本
	private String sdk; // sdk版本
	private String imei;// imei号码
	private String imsi; // imsi号码
	private String screensize; // 屏幕分辨率
	private String networkoperatorname; // 运营商名称
	private String simserialnumber; // sim卡串号

	private int screenWidth; // 屏幕宽
	private int screenHeight;// 屏幕高

	public static boolean isBackground(Context context, int type) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					if (type == 1) {
						// Log.i("后台", appProcess.processName);
					}
					return true;
				} else {
					if (type == 1) {
						// Log.i("前台", appProcess.processName);
					}
					return false;
				}
			}
		}
		return false;
	}

	public static boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
			// Log.i("后台", context.getPackageName());
			return true;
		}
		// Log.i("后台", context.getPackageName());
		return false;
	}

	public void setPhoneInfo(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e1) {
			version = "NULL";
		}
		try {
			brand = Build.BRAND; // 手机制造商
		} catch (Exception e) {
			brand = "NULL";
		}
		try {
			model = Build.MODEL; // 手机型号
		} catch (Exception e) {
			model = "NULL";
		}
		try {
			release = Build.VERSION.RELEASE; // 手机系统版本
		} catch (Exception e) {
			release = "NULL";
		}
		try {
			sdk = Build.VERSION.SDK;// sdk版本
		} catch (Exception e) {
			sdk = "NULL";
		}
		try {
			imei = tm.getDeviceId();
		} catch (Exception e) {
			imei = "XXXXXXXXXXXXXXXX";
		}

		try {
			imsi = tm.getSubscriberId();// imsi
		} catch (Exception e) {
			imsi = null;
		}
//		try {
//			context.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
//			screenWidth = dm.widthPixels;
//			screenHeight = dm.heightPixels;
//			screensize = Integer.toString(screenWidth) + "*" + Integer.toString(screenHeight); // 屏幕分配率
//		} catch (Exception e) {
//			screensize = "NULL";
//		}
		try {
			networkoperatorname = tm.getNetworkOperatorName();
		} catch (Exception e) {
			networkoperatorname = "NULL";
		}
		try {
			simserialnumber = tm.getSimSerialNumber();
		} catch (Exception e) {
			simserialnumber = "NULL";
		}

	}

	private String getVersionName() throws Exception {

		return version;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t|" + "软件版本号：" + version + "\n\t");
		sb.append("|" + "手机制造商：" + brand + "\n\t");
		sb.append("|" + "手机型号：" + model + "\n\t");
		sb.append("|" + "手机系统版本：" + release + "\n\t");
		sb.append("|" + "sdk版本：" + sdk + "\n\t");
		sb.append("|" + "imei：" + imei + "\n\t");
		sb.append("|" + "imsi：" + imsi + "\n\t");
		sb.append("|" + "屏幕分辨率：" + screensize + "\n\t");
		sb.append("|" + "运营商：" + networkoperatorname + "\n\t");
		sb.append("|" + "ICCID(SIM串号)：" + simserialnumber + "\n\t");
		return sb.toString();
	}

	public static String getSystemProperty(String propName) {
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			Logger.e("Unable to read sysprop " + propName, ex);
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Logger.e("Exception while closing InputStream", e);
				}
			}
		}
		return line;
	}

	/*********************** 获取手机SIM卡信息的一些方法 ************************/

	/**
	 * 获取NetWorkIP
	 * 
	 */
	private String getIP() {
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress ip = ips.nextElement();
					if (!ip.isLoopbackAddress()) {
						return ip.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "没有检测到IP地址";
	}

	public String getAppVersion() {
		return version;
	}

	public void setAppVersion(String version) {
		this.version = version;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	// 获取CPU最大频率（单位KHZ）
	// "/system/bin/cat" 命令行
	// "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
	private String getMaxCpuFreq() {
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return result.trim();
	}

	// 获取CPU最小频率（单位KHZ）
	private String getMinCpuFreq() {
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return result.trim();
	}

	// 获取CPU名字
	private String getCpuName() {
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+", 2);
			for (int i = 0; i < array.length; i++) {
			}
			return array[1];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 内存：/proc/meminfo
	private String getTotalMemory() {
		String str1 = "/proc/meminfo";
		String str2 = "";
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			while ((str2 = localBufferedReader.readLine()) != null) {
				// Log.i("test", "---" + str2);
			}
		} catch (IOException e) {

		} finally {
			return str2;
		}
	}

	// ROM大小
	private long[] getRomMemroy() {
		long[] romInfo = new long[2];
		// Total rom memory
		romInfo[0] = getTotalInternalMemorySize();
		// Available rom memory
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		romInfo[1] = blockSize * availableBlocks;
		getVersion();
		return romInfo;
	}

	private long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	// 系统的版本信息
	private String[] getVersion() {
		String[] version = { "null", "null", "null", "null" };
		String str1 = "/proc/version";
		String str2;
		String[] arrayOfString;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			version[0] = arrayOfString[2];
			// KernelVersion
			localBufferedReader.close();
		} catch (IOException e) {

		}
		version[1] = Build.VERSION.RELEASE;
		// firmware version
		version[2] = Build.MODEL;
		// model
		version[3] = Build.DISPLAY;
		// system version
		return version;
	}

	// sdcard大小
	private long[] getSDCardMemory() {
		long[] sdCardInfo = new long[2];
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long bSize = sf.getBlockSize();
			long bCount = sf.getBlockCount();
			long availBlocks = sf.getAvailableBlocks();
			sdCardInfo[0] = bSize * bCount;
			// 总大小
			sdCardInfo[1] = bSize * availBlocks;
			// 可用大小
		}
		return sdCardInfo;
	}

	// 获取MAC地址
	// private String getLocalMacAddress() {
	// WifiManager wifi = (WifiManager) context
	// .getSystemService(Context.WIFI_SERVICE);
	// WifiInfo info = wifi.getConnectionInfo();
	// return info.getMacAddress();
	// }

	/**
	 * 获取CPU序列号
	 * 
	 * @return CPU序列号(16位) 读取失败为"0000000000000000"
	 */
	private static String getCPUSerial() {
		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			// 读取CPU信息
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			// 查找CPU序列号
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					// 查找到序列号所在行
					if (str.indexOf("Serial") > -1) {
						// 提取序列号
						strCPU = str.substring(str.indexOf(":") + 1, str.length());
						// 去空格
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					// 文件结尾
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return cpuAddress;
	}

	// 获得开机时间
	private String getTimes() {
		long ut = SystemClock.elapsedRealtime() / 1000;
		if (ut == 0) {
			ut = 1;
		}
		int m = (int) ((ut / 60) % 60);
		int h = (int) ((ut / 3600));
		return h + "时" + m + "分";
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getSdk() {
		return sdk;
	}

	public void setSdk(String sdk) {
		this.sdk = sdk;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getScreensize() {
		return screensize;
	}

	public void setScreensize(String screensize) {
		this.screensize = screensize;
	}

	public String getNetworkoperatorname() {
		return networkoperatorname;
	}

	public void setNetworkoperatorname(String networkoperatorname) {
		this.networkoperatorname = networkoperatorname;
	}

	public String getSimserialnumber() {
		return simserialnumber;
	}

	public void setSimserialnumber(String simserialnumber) {
		this.simserialnumber = simserialnumber;
	}


	private  String sID = null;
	private  final String INSTALLATION = "INSTALLATION";

	public String getsID() {
		return sID;
	}

	private synchronized String getInstallationId(Context context) {
		if (sID == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation);
				sID = readInstallationFile(installation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sID;
	}

	private String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private void writeInstallationFile(File installation) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}


}