package com.mqt.ganghuazhifu.view;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Locale;

import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.utils.ToastUtil;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class TimePickerDialog extends Dialog implements View.OnClickListener {

	private DatePicker datePicker_start, datePicker_end;
	private CardView button_select_time;
	private OnTimeSelectedListener onTimeSelectedListener;
	private int endyear, endmonth, endday;
	private int startyear, startmonth, startday;

	// key 1,限制日期;2,不限制
	public TimePickerDialog(final Activity context, int type, final int key) {
		super(context, R.style.dialog);
		View views = LayoutInflater.from(context).inflate(R.layout.time_picker_dialog, null);
		datePicker_start = (DatePicker) views.findViewById(R.id.datePicker_start);
		hintPicker(datePicker_start, type);
		datePicker_end = (DatePicker) views.findViewById(R.id.datePicker_end);
		hintPicker(datePicker_end, type);

		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		endyear = calendar.get(Calendar.YEAR);
		endmonth = calendar.get(Calendar.MONTH);
		endday = calendar.get(Calendar.DAY_OF_MONTH);
		startday = endday;
		if (endmonth - 5 > 0) {
			startyear = endyear;
			startmonth = endmonth - 5;
		} else {
			startyear = endyear - 1;
			startmonth = endmonth - 5 + 12;
		}
		datePicker_start.init(startyear, startmonth, startday, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if (key == 1) {
					if (year < startyear) {
						datePicker_start.init(startyear, startmonth + 1, 0, this);
						ToastUtil.Companion.toastWarning("只能查询最近6月!");
					} else if (year == startyear && monthOfYear < startmonth) {
						datePicker_start.init(startyear, startmonth + 1, 0, this);
						ToastUtil.Companion.toastWarning("只能查询最近6月!");
					}
					if (year > endyear) {
						datePicker_start.init(endyear, endmonth + 1, 0, this);
						ToastUtil.Companion.toastWarning("只能查询最近6月!");
					} else if (year == endyear && monthOfYear > endmonth) {
						datePicker_start.init(endyear, endmonth + 1, 0, this);
						ToastUtil.Companion.toastWarning("只能查询最近6月!");
					}
				} else {
					if (year < startyear) {
						datePicker_start.init(startyear, startmonth + 1, 0, this);
					} else if (year == startyear && monthOfYear < startmonth) {
						datePicker_start.init(startyear, startmonth + 1, 0, this);
					}
					if (year > endyear) {
						datePicker_start.init(endyear, endmonth + 1, 0, this);
					} else if (year == endyear && monthOfYear > endmonth) {
						datePicker_start.init(endyear, endmonth + 1, 0, this);
					}
				}
			}
		});
		datePicker_end.init(endyear, endmonth, endday, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if (key == 1) {
					if (year < startyear) {
						datePicker_start.init(startyear, startmonth + 1, 0, this);
						ToastUtil.Companion.toastWarning("只能查询最近6月!");
					} else if (year == startyear && monthOfYear < startmonth) {
						datePicker_start.init(startyear, startmonth + 1, 0, this);
						ToastUtil.Companion.toastWarning("只能查询最近6月!");
					}
					if (year > endyear) {
						datePicker_end.init(endyear, endmonth + 1, 0, this);
						ToastUtil.Companion.toastWarning("只能查询最近6月!");
					} else if (year == endyear && monthOfYear > endmonth) {
						datePicker_end.init(endyear, endmonth + 1, 0, this);
						ToastUtil.Companion.toastWarning("只能查询最近6月!");
					}
				} else {
					if (year < startyear) {
						datePicker_start.init(startyear, startmonth + 1, 0, this);
					} else if (year == startyear && monthOfYear < startmonth) {
						datePicker_start.init(startyear, startmonth + 1, 0, this);
					}
					if (year > endyear) {
						datePicker_end.init(endyear, endmonth + 1, 0, this);
					} else if (year == endyear && monthOfYear > endmonth) {
						datePicker_end.init(endyear, endmonth + 1, 0, this);
					}
				}
			}
		});
		button_select_time = (CardView) views.findViewById(R.id.button_select_time);
		button_select_time.setOnClickListener(this);
		this.setContentView(views);
	}

	/**
	 * 隐藏或者显示相应的时间项
	 * 
	 * @param picker
	 *            传入一个DatePicker对象
	 * @param type
	 *            0:全有;1:只有年year;2:只有月month;3:只有日day;4:有年和月;5:有年和日;6:有月和日;
	 */
	public void hintPicker(DatePicker picker, int type) {
		// 利用java反射技术得到picker内部的属性，并对其进行操作
		Class<? extends DatePicker> c = picker.getClass();
		try {
			// 为了简单，缩写了... fd是field_day,fm是field_month。
			Field fd = null, fm = null, fy = null;
			// 在这里做判断，入过系统版本大于4.0，就让他执行这一块,不做判断在有的手机可能没法实现
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				fd = c.getDeclaredField("mDaySpinner");
				fm = c.getDeclaredField("mMonthSpinner");
				fy = c.getDeclaredField("mYearSpinner");
			} else {
				fd = c.getDeclaredField("mDayPicker");
				fm = c.getDeclaredField("mMonthPicker");
				fy = c.getDeclaredField("mYearSpinner");
			}
			// 对字段获取设置权限
			fd.setAccessible(true);
			fm.setAccessible(true);
			fy.setAccessible(true);
			// 得到对应的控件
			View vd = (View) fd.get(picker);
			View vm = (View) fm.get(picker);
			View vy = (View) fy.get(picker);
			// Type是3表示显示年月，隐藏日,vd可以从下面提取出来的，懒的 ...o(╯□╰)o
			switch (type) {
			case 0:
				vd.setVisibility(View.VISIBLE);
				vm.setVisibility(View.VISIBLE);
				vy.setVisibility(View.VISIBLE);
				break;
			case 1:
				vd.setVisibility(View.GONE);
				vm.setVisibility(View.GONE);
				vy.setVisibility(View.VISIBLE);
				break;
			case 2:
				vd.setVisibility(View.GONE);
				vm.setVisibility(View.VISIBLE);
				vy.setVisibility(View.GONE);
				break;
			case 3:
				vd.setVisibility(View.VISIBLE);
				vm.setVisibility(View.GONE);
				vy.setVisibility(View.GONE);
				break;
			case 4:
				vd.setVisibility(View.GONE);
				vm.setVisibility(View.VISIBLE);
				vy.setVisibility(View.VISIBLE);
				break;
			case 5:
				vd.setVisibility(View.VISIBLE);
				vm.setVisibility(View.GONE);
				vy.setVisibility(View.VISIBLE);
				break;
			case 6:
				vd.setVisibility(View.VISIBLE);
				vm.setVisibility(View.VISIBLE);
				vy.setVisibility(View.GONE);
				break;
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if (onTimeSelectedListener != null) {
			onTimeSelectedListener.OnTimeSelected(datePicker_start.getYear(), datePicker_start.getMonth() + 1,
					datePicker_start.getDayOfMonth(), datePicker_end.getYear(), datePicker_end.getMonth() + 1,
					datePicker_end.getDayOfMonth());
			this.dismiss();
		}
	}

	public void setOnTimeSelectedListener(OnTimeSelectedListener onTimeSelectedListener) {
		this.onTimeSelectedListener = onTimeSelectedListener;
	}

	public interface OnTimeSelectedListener {
		void OnTimeSelected(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay);
	}

}
