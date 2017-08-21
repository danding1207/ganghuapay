package com.mqt.ganghuazhifu.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.mqt.ganghuazhifu.App;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;


/**
 * 进程
 * 
 * @author yang.lei
 * 
 */

public class UnCeHandler implements UncaughtExceptionHandler {

	public static final String TAG = "UnCeHandler";
	private App application;
	private static UnCeHandler myCrashHandler;
	private Context context;

	private UnCeHandler() {
		
	}

	public static synchronized UnCeHandler getInstance() {
		if (myCrashHandler == null) {
			myCrashHandler = new UnCeHandler();
		}
		return myCrashHandler;
	}

	public void init(Context context) {
		this.context = context;
	}


	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				ToastUtil.Companion.toastError("很抱歉,程序出现异常,即将退出.");
				Looper.loop();
			}
		}.start();
		return true;
	}

    /**
     * 程序崩溃  异常捕获 保存到sd卡
     */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		StringBuilder sb = new StringBuilder();
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(),
					0);
			sb.append("程序的版本号为" + packinfo.versionName);
			sb.append("\n");

			Field[] fields = Build.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				String name = fields[i].getName();
				sb.append(name + " = ");
				String value = fields[i].get(null).toString();
				sb.append(value);
				sb.append("\n");
			}
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			String result = writer.toString();
			sb.append(result);
		    if(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
		    	File mi = new File(Environment.getExternalStorageDirectory()+File.separator+"Android"
		    			+File.separator+"data"
		    					+File.separator+"com.mqt.ganghuazhifu"
		    					+File.separator+"file");
		    	if(!mi.exists()) {
		    		mi.mkdir();
		    	}
		    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
		    	String date = dateFormat.format(System.currentTimeMillis());
		    	
		    	File file=new File(Environment.getExternalStorageDirectory()+File.separator+"Android"
		    			+File.separator+"data"
		    					+File.separator+"com.mqt.ganghuazhifu"
		    					+File.separator+"file"
		    					+File.separator+"error_log:"+date+".txt");
		    	if(!file.exists()) {
		    		file.createNewFile();
		    	}
		    	FileWriter fw=new FileWriter(file);
		    	fw.write(sb.toString());
		    	fw.flush();
		    	fw.close();
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ScreenManager.getScreenManager().popAllActivity();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
