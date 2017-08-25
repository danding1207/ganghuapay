package com.mqt.ganghuazhifu.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.utils.UnitConversionUtils;

public class PaymentMethodDialog extends Dialog implements View.OnClickListener {

    private Window window = null;
    private PaymentMethodDialog myDialog;

    private ImageView iv_cancle;
    private LinearLayout ll_weixin, ll_yinhang;
    private Activity activity;
    private OnPaymentMethodSelectedListener listener;


    public PaymentMethodDialog(Activity context, OnPaymentMethodSelectedListener listener) {
        super(context, R.style.dialog_floating);
        myDialog = this;
        this.listener = listener;
        this.activity = context;
        View views = LayoutInflater.from(context)
                .inflate(R.layout.payment_method_dialog, null);
        iv_cancle = (ImageView) views.findViewById(R.id.iv_cancle);
        ll_weixin = (LinearLayout) views.findViewById(R.id.ll_weixin);
        ll_yinhang = (LinearLayout) views.findViewById(R.id.ll_yinhang);

        ll_weixin.setOnClickListener(this);
        ll_yinhang.setOnClickListener(this);
        iv_cancle.setOnClickListener(this);
        this.setContentView(views);
        windowDeploy(context);
    }

    public PaymentMethodDialog(Activity context) {
        this(context, null);
    }

    // 设置窗口显示
    public void windowDeploy(Context context) {
        window = getWindow(); // 得到对话框
        window.setWindowAnimations(R.style.dialogWindowAnim); // 设置窗口弹出动画
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 根据x，y坐标设置窗口需要显示的位置
        wl.width = dm.widthPixels;
        wl.height = UnitConversionUtils.dipTopx(context, 300);
        wl.gravity = Gravity.BOTTOM; // 设置重力
        window.setAttributes(wl);
    }

    public interface OnPaymentMethodSelectedListener {
        void OnPaymentMethodSelected(int position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancle:
                dismiss();
                break;
            case R.id.ll_weixin:
                if (listener != null) {
                    listener.OnPaymentMethodSelected(0);
                }
                dismiss();
                break;
            case R.id.ll_yinhang:
                if (listener != null) {
                    listener.OnPaymentMethodSelected(1);
                }
                dismiss();
                break;
        }
    }

    public void showDialog() {
        if (!myDialog.isShowing()) {
            myDialog.show();
        }
    }

}
