package com.mqt.ganghuazhifu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.mqt.ganghuazhifu.BuildConfig;
import com.mqt.ganghuazhifu.R;

public class ShuiyinView extends FrameLayout {

    private int visibility = View.GONE;

    public ShuiyinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ShuiyinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ShuiyinView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        if (BuildConfig.DEBUG) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.shuiyin_layout, this);
        this.setVisibility(visibility);
    }

}
