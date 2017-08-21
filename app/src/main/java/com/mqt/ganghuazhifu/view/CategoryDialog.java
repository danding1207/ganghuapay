package com.mqt.ganghuazhifu.view;

import com.mqt.ganghuazhifu.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class CategoryDialog extends Dialog implements View.OnClickListener {

	public static final int CAMERATYPE = 0012;
	public static final int PHOTOTYPE = 0013;

    private Window window = null;
    private Button item_popupwindows_camera, item_popupwindows_photo, item_popupwindows_cancel;
    private CategoryDialog myDialog;
    private Activity activity;
    private OnCategorySelectedListener onCategorySelectedListener;

    public CategoryDialog(Activity context, OnCategorySelectedListener onCategorySelectedListener) {
        super(context, R.style.dialog_floating);
        myDialog = this;
        this.onCategorySelectedListener = onCategorySelectedListener;
        this.activity = context;
        View views = LayoutInflater.from(context)
                .inflate(R.layout.item_popupwindows, null);

        item_popupwindows_camera = (Button) views.findViewById(R.id.item_popupwindows_camera);
        item_popupwindows_photo = (Button) views.findViewById(R.id.item_popupwindows_photo);
        item_popupwindows_cancel = (Button) views.findViewById(R.id.item_popupwindows_cancel);

        item_popupwindows_camera.setOnClickListener(this);
        item_popupwindows_photo.setOnClickListener(this);
        item_popupwindows_cancel.setOnClickListener(this);

        this.setContentView(views);
        windowDeploy(context);
    }

    public CategoryDialog(Activity context) {
        this(context, null);
    }

    //设置窗口显示
    public void windowDeploy(Context context) {
        window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams wl = window.getAttributes();
        //根据x，y坐标设置窗口需要显示的位置
        wl.width = dm.widthPixels;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.BOTTOM; //设置重力
        window.setAttributes(wl);
    }

    public void showDialog() {
        if (!myDialog.isShowing()) {
            myDialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_popupwindows_camera:
                if (onCategorySelectedListener != null) {
                    onCategorySelectedListener.OnCategorySelected(CAMERATYPE);
                }
                dismiss();
                break;
            case R.id.item_popupwindows_photo:
                if (onCategorySelectedListener != null) {
                    onCategorySelectedListener.OnCategorySelected(PHOTOTYPE);
                }
                dismiss();
                break;
            case R.id.item_popupwindows_cancel:
                dismiss();
                break;
        }
    }

    public interface OnCategorySelectedListener {
        void OnCategorySelected(int type);
    }

}
