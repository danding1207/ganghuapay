package com.mqt.ganghuazhifu.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class MinuteClocker {

	private int minute;
	private int second;
	private OnClockerUpdateListener onClockerUpdateListener;
	private OnClockerFinishListener onClockerFinishListener;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				setup();
				if(onClockerUpdateListener!=null) {
					onClockerUpdateListener.OnClockerUpdate(minute, second);
				}
				break;
			case 2:
				if(onClockerFinishListener!=null) {
					onClockerFinishListener.OnClockerFinish(true);
				}
				break;
			}
		};
	};
	
	public MinuteClocker(Activity activity){
		
	}
	
	protected void setup() {
		if(second>0) {
			second--;
		} else {
			if(minute>0) {
				minute--;
				second = 59;
			} else {
				handler.sendEmptyMessage(2);
				return;
			}
		}
		handler.sendEmptyMessageDelayed(1, 1000);
	}

	public void init(int minute,int second) {
		if(minute==0&&second==0) {
			handler.sendEmptyMessage(2);
			return;
		}
		if((minute>0||second>0)&&second<60) {
			this.minute = minute;
			this.second = second;
			handler.sendEmptyMessage(1);
		} else {
//			Log.i("TAG", "计时初始化异常");
		}
	}
	
	public interface OnClockerUpdateListener{
		void OnClockerUpdate(int minute,int second);
	}
	
	public interface OnClockerFinishListener{
		void OnClockerFinish(boolean flag);
	}

	public OnClockerUpdateListener getOnClockerUpdateListener() {
		return onClockerUpdateListener;
	}

	public void setOnClockerUpdateListener(
			OnClockerUpdateListener onClockerUpdateListener) {
		this.onClockerUpdateListener = onClockerUpdateListener;
	}

	public OnClockerFinishListener getOnClockerFinishListener() {
		return onClockerFinishListener;
	}

	public void setOnClockerFinishListener(
			OnClockerFinishListener onClockerFinishListener) {
		this.onClockerFinishListener = onClockerFinishListener;
	}
	
	class ActionBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
		}

	}
	
}
