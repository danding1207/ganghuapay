package com.mqt.ganghuazhifu.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.baidu.android.pushservice.PushService;
import com.mqt.ganghuazhifu.aidl.IBridgeInterface;


public class GuardService2 extends Service {

    private static final String TAG = GuardService2.class.getSimpleName();
    private GuardService2.MyBinder mBinder;
    private GuardService2.MyServiceConnection mServiceConnection;

    @Override
    public void onCreate() {
        super.onCreate();


        if (mBinder == null) {
            mBinder = new GuardService2.MyBinder();
        }
        mServiceConnection = new GuardService2.MyServiceConnection();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.bindService(new Intent(this, GuardService.class), mServiceConnection, Context.BIND_IMPORTANT);

        return START_STICKY;
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            GuardService2.this.startService(new Intent(GuardService2.this, PushService.class));
            GuardService2.this.startService(new Intent(GuardService2.this, GuardService.class));
            GuardService2.this.bindService(new Intent(GuardService2.this, GuardService.class),
                    mServiceConnection, Context.BIND_IMPORTANT);


        }

    }

    class MyBinder extends IBridgeInterface.Stub {

        @Override
        public String getName() throws RemoteException {
            return "GuardService2";
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
