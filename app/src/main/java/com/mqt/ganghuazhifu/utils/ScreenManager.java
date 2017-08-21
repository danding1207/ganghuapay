package com.mqt.ganghuazhifu.utils;

import java.util.Stack;

import com.mqt.ganghuazhifu.BaseActivity;
import com.orhanobut.logger.Logger;

import android.app.Activity;
import android.util.Log;

public class ScreenManager {
	private static Stack<Activity> activityStack;
	private static ScreenManager instance;

	private ScreenManager() {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
	}

	/**
	 * 获取单例对象
	 */
	public static ScreenManager getScreenManager() {
		if (instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}

	/**
	 * 退出栈顶 activity
	 */
	public void popActivity() {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		Activity activity = activityStack.lastElement();
		if (activity != null) {
//			Log.i("ScreenManager", "popActivity--->" + activity.getClass());
			activity.finish();
			activity = null;
		}
	}

	/**
	 * ָ退出指定 activity，只有当该 activity 在栈顶时生效
	 */
	public void popActivity(Activity activity) {
		Logger.t("ScreenManager").i("popActivity--->" + activity.getClass());
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		activity.finish();
		activityStack.remove(activity);
		Logger.t("ScreenManager").i("activityStack.size--->" + activityStack.size());
	}

	/**
	 * 判断指定 activity 是否在栈顶
	 */
	public boolean isCurrentActivity(Class cls) {
		Logger.e("isCurrentActivity()");
		if (!activityStack.isEmpty()) {
			Logger.e("activityStack != null");
			Activity activity = activityStack.lastElement();
			if (activity.getClass().equals(cls)) {
				Logger.e("equals");
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否要强制跳转到登陆页面
	 */
	public boolean isWelcomeActivity() {
		if (activityStack == null) {
			activityStack = new Stack<>();
			return true;
		}
		return false;
	}

	/**
	 * 判断栈内是否存有指定 activity
	 */
	public boolean isContainActivity(Class cls) {
		if (!activityStack.isEmpty()) {
			for (Activity activity : activityStack) {
				if (activity.getClass().equals(cls)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取栈顶 activity 对象
	 */
	public Activity currentActivity() {
		if (!activityStack.isEmpty()) {
			Activity activity = activityStack.lastElement();
			return activity;
		}
		return null;
	}

	/**
	 * ָ添加 activity 到栈顶
	 */
	public void pushActivity(Activity activity) {
		Logger.t("ScreenManager").i("pushActivity--->" + activity.getClass());
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		activityStack.add(activity);
		Logger.t("ScreenManager").i("activityStack.size--->" + activityStack.size());
	}

	/**
	 * 退出所有 activity 直到指定 activity
	 */
	public void popAllActivityExceptOne(Class cls) {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}

	/**
	 * 退出所有 activity 直到指定 activity，但是包括该指定的 activity
	 */
	public void popAllActivityExceptOneInclude(Class cls) {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
//			Log.i("ScreenManager",
//					"popAllActivityExceptOne--->" + activity.getClass());
			popActivity(activity);
			if (activity.getClass().equals(cls)) {
				break;
			}
		}
	}

	/**
	 * 跳过指定activity，退出所有 activity 直到另一指定 activity
	 */
	public void popAllActivityJumpOverOneExceptOne(Class jumpOverOne, Class exceptOne) {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		Logger.t("ScreenManager").e("popAllActivityJumpOverOneExceptOne:jumpOverOne--->" + jumpOverOne.getName());
		Logger.t("ScreenManager").e("popAllActivityJumpOverOneExceptOne:exceptOne--->" + exceptOne.getName());
		Activity activityOne = null;
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(exceptOne)) {
				break;
			}
			if (activity.getClass().equals(jumpOverOne)) {
				activityOne = activity;
				activityStack.remove(activity);
			} else {
				popActivity(activity);
			}
		}
		if (activityOne != null)
			activityStack.add(activityOne);
		Logger.t("ScreenManager").e("popAllActivityJumpOverOne--->activityStack.size:  " + activityStack.size());
	}

	/**
	 * 跳过指定activity，退出所有 activity
	 */
	public void popAllActivityJumpOverOne(Class cls) {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		Logger.t("ScreenManager").e("popAllActivityJumpOverOne--->" + cls.getName());
		Activity activityOne = null;
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			Logger.t("ScreenManager").e("popAllActivityJumpOverOne--->" + activity.getClass());
			if (activity.getClass().equals(cls)) {
				activityOne = activity;
				activityStack.remove(activity);
			} else {
				popActivity(activity);
			}
		}
		if (activityOne != null)
			activityStack.add(activityOne);
		Logger.t("ScreenManager").e("popAllActivityJumpOverOne--->activityStack.size:  " + activityStack.size());
	}

	/**
	 * 退出所有 activity
	 */
	public void popAllActivity() {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			Logger.t("ScreenManager").i("popAllActivity--->" + activity.getClass());
			popActivity(activity);
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

	/**
	 * 退出指定 activity，只有当该 activity 在栈顶时生效
	 */
	public void popActivity(Class cls) {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		if (activityStack.isEmpty()) {
			return;
		}
		Activity activity = activityStack.lastElement();
		if (activity != null) {
			if (activity.getClass().equals(cls)) {
				activity.finish();
				activity = null;
			}
		}
	}

	public void finsh(Class cls) {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
		if (activityStack.isEmpty()) {
			return;
		}
		for (int i = 0; i < activityStack.size(); i++) {
			Activity activity = activityStack.get(i);
			if (activity.getClass().equals(cls)) {
				popActivity(activity);
			}
		}
	}
}
