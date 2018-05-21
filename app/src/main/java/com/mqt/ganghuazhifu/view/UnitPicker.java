package com.mqt.ganghuazhifu.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.bean.Unit;
import com.mqt.ganghuazhifu.http.CusFormBody;
import com.mqt.ganghuazhifu.http.HttpRequest;
import com.mqt.ganghuazhifu.http.HttpRequestParams;
import com.mqt.ganghuazhifu.http.HttpURLS;
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener;
import com.mqt.ganghuazhifu.utils.DataBaiduPush;
import com.mqt.ganghuazhifu.utils.ToastUtil;
import com.mqt.ganghuazhifu.view.ScrollerNumberPicker.OnSelectListener;
import com.mqt.ganghuazhifu.view.ScrollerNumberPicker.OnSingleTouchListener;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 城市Picker
 * 
 * @author yang.lei
 */
public class UnitPicker extends LinearLayout {
	/** 滑动控件 */
	private ScrollerNumberPicker unitPicker;
	/** 选择监听 */
	private OnSelectingListener onSelectingListener;
	/** 选择监听 */
	private OnSelectedListener onSelectedListener;
	/** 初始化完成监听 */
	private OnInitdataedListener onInitdataedListener;
	/** 刷新界面 */
	private static final int REFRESH_VIEW = 0x001;
	/** 获取单位 */
	private static final int GETUNIT = 0x002;
	/** 重置单位 */
	private static final int SETUNIT = 0x003;
	/** 清空界面 */
	private static final int CLEAR_VIEW = 0x004;
	/** 临时城市 */
	private int tempUnitIndex = 0;
	private Activity context;
	private ArrayList<Unit> unit;
	private CardView button_comfirg;
	private String pmttp;

	public UnitPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setActivity(Activity context) {
		this.context = context;
	}

	public UnitPicker(Context context) {
		super(context);
	}

	// 获取城市信息
	public void initView(String cityCode, String pmttp) {
		this.pmttp = pmttp;
		initUnit(cityCode);
	}

	// 重置缴费单位
	public void resetView(String cityCode, String pmttp) {
		this.pmttp = pmttp;
		resetUnit(cityCode);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.unit_picker, this);
		// 获取控件引用
		unitPicker = (ScrollerNumberPicker) findViewById(R.id.unit);
		button_comfirg = (CardView) findViewById(R.id.button_comfirg);
		button_comfirg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onSelectedListener != null) {
					if (DataBaiduPush.getHaveUnit() == 1 && unit != null) {
						onSelectedListener.selected(unit.get(tempUnitIndex));
					} else {
						onSelectedListener.selected(null);
					}
				}
			}
		});
		unitPicker.setOnSingleTouchListener(new OnSingleTouchListener() {
			@Override
			public void onSingleTouch() {
				if (onSelectedListener != null) {
					if (DataBaiduPush.getHaveUnit() == 1 && unit != null) {
						onSelectedListener.selected(unit.get(tempUnitIndex));
					} else {
						onSelectedListener.selected(null);
					}
				}
			}
		});
		unitPicker.setUnit(unit);
		unitPicker.setDefault(0);
		unitPicker.setOnSelectListener(new OnSelectListener() {
			@Override
			public void endSelect(int id, String text) {
				if (text.equals("") || text == null)
					return;
				if (tempUnitIndex != id) {
					String selectDay = unitPicker.getSelectedText();
					if (selectDay == null || selectDay.equals(""))
						return;
					int lastDay = Integer.valueOf(unitPicker.getListSize());
					if (id > lastDay) {
						unitPicker.setDefault(lastDay - 1);
					}
				}
				tempUnitIndex = id;
				Message message = new Message();
				message.what = REFRESH_VIEW;
				handler.sendMessage(message);
			}

			@Override
			public void selecting(int id, String text) {
			}
		});
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_VIEW:
				if (onSelectingListener != null)
					onSelectingListener.selecting(true);
				break;
			case GETUNIT:
				unitPicker.setUnit(unit);
				unitPicker.setDefault(0);
				if (onInitdataedListener != null && unit != null && unit.size() > 0) {
					onInitdataedListener.Initdataed(unit.get(0));
				}
				break;
			case SETUNIT:
				unitPicker.setUnit(unit);
				unitPicker.setDefault(0);
				// if(onInitdataedListener!=null&&unit!=null&&unit.size()>0) {
				// onInitdataedListener.Initdataed(unit.get(0));
				// }
				break;
			case CLEAR_VIEW:
				unit = new ArrayList<Unit>();
				unitPicker.setUnit(unit);
				onInitdataedListener.Initdataed(null);
				invalidate();
				break;
			}
		}

	};

	public void setOnSelectingListener(OnSelectingListener onSelectingListener) {
		this.onSelectingListener = onSelectingListener;
	}

	public interface OnSelectingListener {

		public void selecting(boolean selected);
	}

	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}

	public interface OnSelectedListener {
		public void selected(Unit unit);
	}

	public void setOnInitdataedListener(OnInitdataedListener onInitdataedListener) {
		this.onInitdataedListener = onInitdataedListener;
	}

	public interface OnInitdataedListener {
		public void Initdataed(Unit unit);
	}

	private void initUnit(String code) {
		DataBaiduPush.setHaveUnit(0);
		CusFormBody body = HttpRequestParams.INSTANCE.getParamsForPayeesQuery(code, pmttp);
		HttpRequest.Companion.getInstance().httpPost((Activity) getContext(), HttpURLS.INSTANCE.getPayeesQuery(), true, "PayeesQuery", body,
				new OnHttpRequestListener() {
					@Override
					public void OnCompleted(Boolean isError, JSONObject response, int type, IOException error) {
						if(isError) {
							if (onInitdataedListener != null && unit != null && unit.size() > 0) {
								onInitdataedListener.Initdataed(null);
							}
							ToastUtil.Companion.toastError("获取缴费单位失败，请重新选择城市！");
						} else {
							JSONObject ResponseHead = response.getJSONObject("ResponseHead");
							String ProcessCode = ResponseHead.getString("ProcessCode");
							String ProcessDes = ResponseHead.getString("ProcessDes");
							// 判断返回是jsonObject还是jsonArray
							switch (type) {
								case 0:
									// unit = new ArrayList<Unit>();
									handler.sendEmptyMessage(CLEAR_VIEW);
									// onInitdataedListener.Initdataed(null);
									DataBaiduPush.setHaveUnit(0);
									ToastUtil.Companion.toastInfo("该城市暂未开通服务");
									unitPicker.refresh();
									break;
								case 1:
									DataBaiduPush.setHaveUnit(1);
									String ResponseFields = response.getString("ResponseFields");
									if (ResponseFields != null) {
										unit = new ArrayList<>();
										if (ProcessCode.equals("0000")) {
											unit = (ArrayList<Unit>) JSONObject.parseArray(ResponseFields, Unit.class);
										}
										handler.sendEmptyMessage(GETUNIT);
									} else {
										if (onInitdataedListener != null && unit != null && unit.size() > 0) {
											onInitdataedListener.Initdataed(null);
										}
										ToastUtil.Companion.toastInfo("该城市暂未开通服务");
									}
									break;
								case 2:
									DataBaiduPush.setHaveUnit(1);
									JSONObject ResponseFields1 = response.getJSONObject("ResponseFields");
									if (ResponseFields1 != null) {
										String PayeesDetail = ResponseFields1.getString("PayeesDetail");
										unit = new ArrayList<>();
										if (ProcessCode.equals("0000")) {
											unit.add(JSONObject.parseObject(PayeesDetail, Unit.class));
										}
										handler.sendEmptyMessage(GETUNIT);
									} else {
										if (onInitdataedListener != null && unit != null && unit.size() > 0) {
											onInitdataedListener.Initdataed(null);
										}
										ToastUtil.Companion.toastInfo("该城市暂未开通服务");
									}
									break;
							}
						}
					}
				});
	}

	/**
	 * 重置缴费单位
	 */
	private void resetUnit(String code) {
		DataBaiduPush.setHaveUnit(0);
		tempUnitIndex = 0;
		CusFormBody body = HttpRequestParams.INSTANCE.getParamsForPayeesQuery(code, pmttp);
		HttpRequest.Companion.getInstance().httpPost((Activity) getContext(), HttpURLS.INSTANCE.getPayeesQuery(), true, "PayeesQuery", body,
				new OnHttpRequestListener() {
					@Override
					public void OnCompleted(Boolean isError, JSONObject response, int type, IOException error) {
						if(isError) {
							if (onInitdataedListener != null && unit != null && unit.size() > 0) {
								onInitdataedListener.Initdataed(null);
							}
							ToastUtil.Companion.toastError("获取缴费单位失败，请重新选择城市");
						} else {
							JSONObject ResponseHead = response.getJSONObject("ResponseHead");
							String ProcessCode = ResponseHead.getString("ProcessCode");
							String ProcessDes = ResponseHead.getString("ProcessDes");
							// 判断返回是jsonObject还是jsonArray
							switch (type) {
								case 0:
									// unit = new ArrayList<Unit>();
									handler.sendEmptyMessage(CLEAR_VIEW);
									// onInitdataedListener.Initdataed(null);
									DataBaiduPush.setHaveUnit(0);
									ToastUtil.Companion.toastInfo("该城市暂未开通服务!");
									unitPicker.refresh();
									break;
								case 1:
									String ResponseFields = response.getString("ResponseFields");
									DataBaiduPush.setHaveUnit(1);
									if (ResponseFields != null) {
										unit = new ArrayList<>();
										if (ProcessCode.equals("0000")) {
											unit = (ArrayList<Unit>) JSONObject.parseArray(ResponseFields, Unit.class);
										}
										handler.sendEmptyMessage(SETUNIT);
									} else {
										if (onInitdataedListener != null && unit != null && unit.size() > 0) {
											onInitdataedListener.Initdataed(null);
										}
										ToastUtil.Companion.toastInfo("该城市暂未开通服务!");
									}
									break;
								case 2:
									DataBaiduPush.setHaveUnit(1);
									JSONObject ResponseFields1 = response.getJSONObject("ResponseFields");
									if (ResponseFields1 != null) {
										String PayeesDetail = ResponseFields1.getString("PayeesDetail");
										unit = new ArrayList<>();
										if (ProcessCode.equals("0000")) {
											unit.add(JSONObject.parseObject(PayeesDetail, Unit.class));
										}
										handler.sendEmptyMessage(SETUNIT);
									} else {
										if (onInitdataedListener != null && unit != null && unit.size() > 0) {
											onInitdataedListener.Initdataed(null);
										}
										ToastUtil.Companion.toastInfo("该城市暂未开通服务!");
									}
									break;
							}
						}
					}
				});
	}

	public void refreshUnit() {
		unitPicker.refresh();
	}

}
