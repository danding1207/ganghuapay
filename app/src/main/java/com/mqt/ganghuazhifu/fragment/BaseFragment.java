package com.mqt.ganghuazhifu.fragment;

import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.utils.PhoneActiveInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseFragment extends Fragment implements OnClickListener {

	private String mPageName = this.getClass().getSimpleName();
	public PhoneActiveInfo phoneInfo;


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		phoneInfo = new PhoneActiveInfo(getActivity());
	}

	/**
	 * 给控件设置监事件
	 * @param resId
	 * @param parent
	 */
	public View setViewClick(View parent,int resId) {
		View view = parent.findViewById(resId);
		if (view != null) {
			view.setOnClickListener(this);
		}
		return view;
	}
	
	/**
	 * 控件的点击事件
	 * @param v
	 */
	public abstract void OnViewClick(View v);
	
	@Override
	public void onClick(View v) {
		OnViewClick(v);
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		// 第一个参数为启动时动画效果，第二个参数为退出时动画效果
		getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	public void finish() {
		// 第一个参数为启动时动画效果，第二个参数为退出时动画效果
		getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}
