package com.mqt.ganghuazhifu.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.baidu.android.pushservice.PushService;
import com.mqt.ganghuazhifu.aidl.IBridgeInterface;


public class GuardService extends Service {

    private static final String TAG = GuardService.class.getSimpleName();
    private MyBinder mBinder;
    private MyServiceConnection mServiceConnection;
    private MyServiceConnection2 mServiceConnection2;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mBinder == null) {
            mBinder = new MyBinder();
        }
        mServiceConnection = new MyServiceConnection();
        mServiceConnection2 = new MyServiceConnection2();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        this.bindService(new Intent(this, GuardService2.class), mServiceConnection, Context.BIND_IMPORTANT);
        this.bindService(new Intent(this, PushService.class), mServiceConnection2, Context.BIND_IMPORTANT);

        return START_STICKY;
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            GuardService.this.startService(new Intent(GuardService.this, GuardService2.class));
            GuardService.this.bindService(new Intent(GuardService.this, GuardService2.class),
                    mServiceConnection, Context.BIND_IMPORTANT);


        }

    }

    class MyServiceConnection2 implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            GuardService.this.startService(new Intent(GuardService.this, PushService.class));
            GuardService.this.bindService(new Intent(GuardService.this, PushService.class),
                    mServiceConnection2, Context.BIND_IMPORTANT);

        }
    }

    class MyBinder extends IBridgeInterface.Stub {

        @Override
        public String getName() throws RemoteException {
            return "GuardService";
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
