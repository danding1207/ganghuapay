package com.mqt.ganghuazhifu.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.mqt.ganghuazhifu.event.RunningInfoEvent;
import com.orhanobut.logger.Logger;


public class RunningInfoService extends Service {

	private final IBinder binder = new MyBinder();

	@Override
	public IBinder onBind(Intent intent) {
		Logger.i("RunningInfoService--->onBind");
		return binder;
	}

	public class MyBinder extends Binder {
		public RunningInfoService getService() {
			return RunningInfoService.this;
		}
	}

	@Override
	public void onCreate() {
		Logger.i("RunningInfoService--->onCreate");
		RxBus.get().register(this);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Logger.i("RunningInfoService--->onDestroy");
		stopForeground(true);
		RxBus.get().unregister(this);
		super.onDestroy();
	}

	@Subscribe(
			thread = EventThread.MAIN_THREAD
	)
	public void onRunningInfoEvent(RunningInfoEvent event) {
//		Logger.d("onRunningInfoEvent");
//		Logger.t("LockPattern").i("AppCount--->"+App.Companion.getAppCount());
//		if (!App.Companion.isRunningForeground()) {
//            Logger.e("onRunningInfoEvent--->Background");
//            Logger.e("onRunningInfoEvent--->isFront:" + Features.isFront);
//			if (Features.isFront) {
//				Features.isFront = false;
//			}
//		} else {
//            Logger.e("onRunningInfoEvent--->Foreground");
//            Logger.e("onRunningInfoEvent--->isFront:" + Features.isFront);
//			User user = EncryptedPreferencesUtils.getUser();
//			if (user != null && !TextUtils.isEmpty(user.getGesturePwd()) && !user.getGesturePwd().equals("9DD4E461268C8034F5C8564E155C67A6") && !Features.isFront && !ScreenManager.getScreenManager().isContainActivity(ReLockPatternActivity.class)&& ScreenManager.getScreenManager().isContainActivity(MainActivity.class)) {
//				Intent intent2 = new Intent(this, ReLockPatternActivity.class);
//				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				this.startActivity(intent2);
//			}
//			Features.isFront = true;
//		}
	}

}
