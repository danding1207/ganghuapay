package com.mqt.ganghuazhifu.view;

/**
 * Created by msc on 2016/10/13.
 */

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.mqt.ganghuazhifu.App;
import com.mqt.ganghuazhifu.R;

public class MyWindow {

    private Context mContext;
    private WindowManager mwinWindowManager;
    private View mView;

    public MyWindow(Context context) {
        mContext = context.getApplicationContext();
        //初始化View
        mView = initView(mContext);
    }

    public void showMyWindow() {
        Log.e("TAG", "showMyWindow: ");
        mwinWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //类型是TYPE_TOAST，像一个普通的Android Toast一样。这样就不需要申请悬浮窗权限了。
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
        //初始化后不首先获得窗口焦点。不妨碍设备上其他部件的点击、触摸事件。
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;
        //点击back键，关闭window
        mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        hideMyWindow();
                        return true;
                    default:
                        return false;
                }
            }
        });
        mwinWindowManager.addView(mView, params);
    }

    private View initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_window, null);
        return view;
    }

    public void hideMyWindow() {
        if (mView != null) {
            mwinWindowManager.removeView(mView);
        }
    }
}