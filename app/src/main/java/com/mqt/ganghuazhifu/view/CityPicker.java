package com.mqt.ganghuazhifu.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.bean.City;
import com.mqt.ganghuazhifu.http.CusFormBody;
import com.mqt.ganghuazhifu.http.HttpRequest;
import com.mqt.ganghuazhifu.http.HttpRequestParams;
import com.mqt.ganghuazhifu.http.HttpURLS;
import com.mqt.ganghuazhifu.utils.ToastUtil;
import com.mqt.ganghuazhifu.view.ScrollerNumberPicker.OnSelectListener;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 城市Picker
 * 
 * @author yang.lei
 */
public class CityPicker extends LinearLayout {
	/** 滑动控件 */
	private ScrollerNumberPicker provincePicker;
	private ScrollerNumberPicker cityPicker;
	/** 选择监听 */
	private OnSelectingListener onSelectingListener;
	/** 选择监听 */
	private OnSelectedListener onSelectedListener;
	/** 刷新界面 */
	private static final int REFRESH_VIEW = 0x001;
	private static final int REFRESH_PROVINCE = 0x002;
	private static final int REFRESH_CITY = 0x003;

	public int tempProvinceIndex = 0;
	public int temCityIndex = 0;
	private Context context;
	private static ArrayList<City> provinces;
	private static ArrayList<City> citys;
	private String pmttp;

	public CityPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public CityPicker(Context context) {
		super(context);
		this.context = context;
	}

	public void initView(String pmttp) {
		this.pmttp = pmttp;
		getaddressinfo(1);
	}
	
	// 获取城市信息
	public void getaddressinfo(int type) {
		getProvince(pmttp);
		if (type == 2) {
			provincePicker.setCity(provinces);
			provincePicker.setDefault(0);
			cityPicker.setCity(citys);
			cityPicker.setDefault(0);
		}
	}

	public void setDefaut() {
		provincePicker.setCity(provinces);
		provincePicker.setDefault(0);
		if(provinces!=null)
			getCitys(provinces.get(0).CityCode, pmttp);
//		System.out.println("城市编码city = " + provinces.get(0).CityCode);
		temCityIndex = 0;
		tempProvinceIndex = 0;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.city_picker, this);
		// 获取控件引用
		provincePicker = (ScrollerNumberPicker) findViewById(R.id.province);
		cityPicker = (ScrollerNumberPicker) findViewById(R.id.city);
		cityPicker.setOnSingleTouchListener(() -> {
            // ToastUtil.toast(getContext(), "单击--1");
            if (onSelectedListener != null) {
                if (provinces != null && citys != null && provinces.size() > tempProvinceIndex && citys.size() > temCityIndex) {
                    // ToastUtil.toast(getContext(),
                    // "temCityIndex--->"+temCityIndex);
                    onSelectedListener.selected(provinces.get(tempProvinceIndex), citys.get(temCityIndex));
                } else {
                    onSelectedListener.selected(null, null);
                }
                // et_serach_city.setText("");
            }
        });

		// et_serach_city = (EditText) findViewById(R.id.et_serach_city);
		// et_serach_city.setImeOptions(EditorInfo.IME_ACTION_SEND);
		// et_serach_city.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// et_serach_city.requestFocus();
		// if (onMoveUpListener != null) {
		// onMoveUpListener.moveUp(true);
		// }
		// return false;
		// }
		// });
		//
		// et_serach_city.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// if (onSearchListener != null) {
		// onSearchListener.search(et_serach_city
		// .getText().toString().trim());
		// }
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// });

		provincePicker.setCity(provinces);
		provincePicker.setDefault(0);
		cityPicker.setCity(citys);
		cityPicker.setDefault(0);
		provincePicker.setOnSelectListener(new OnSelectListener() {
			@Override
			public void endSelect(int id, String text) {
				if (text.equals(""))
					return;
				if (tempProvinceIndex != id) {
					String selectDay = provincePicker.getSelectedText();
					if (selectDay == null || selectDay.equals(""))
						return;
					// 城市数组
					if(provinces.size()>id)
						getCitys(provinces.get(id).CityCode, pmttp);
					
					int lastDay = provincePicker.getListSize();
					if (id > lastDay) {
						provincePicker.setDefault(lastDay - 1);
					}
				}
				tempProvinceIndex = id;
				Message message = new Message();
				message.what = REFRESH_VIEW;
				handler.sendMessage(message);
			}

			@Override
			public void selecting(int id, String text) {
			}
		});
		cityPicker.setOnSelectListener(new OnSelectListener() {
			@Override
			public void endSelect(int id, String text) {
				if (text == null || text.equals(""))
					return;
				if (temCityIndex != id) {
					String selectDay = cityPicker.getSelectedText();
					if (selectDay == null || selectDay.equals(""))
						return;
					int lastDay = cityPicker.getListSize();
					if (id > lastDay) {
						cityPicker.setDefault(lastDay - 1);
					}
				}
				temCityIndex = id;
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
			case REFRESH_PROVINCE:
				provincePicker.setCity(provinces);
				provincePicker.setDefault(0);
				getCitys(provinces.get(0).CityCode, pmttp);
				break;
			case REFRESH_CITY:
				cityPicker.setCity(citys);
				cityPicker.setDefault(0);
				break;
			}
		}

	};

	public void setOnSelectingListener(OnSelectingListener onSelectingListener) {
		this.onSelectingListener = onSelectingListener;
	}

	public interface OnSelectingListener {
		void selecting(boolean selected);
	}

	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}

	public interface OnSelectedListener {
		void selected(City province, City city);
	}

	private void getProvince(String pmtty) {
		CusFormBody body = HttpRequestParams.INSTANCE.getParamsForCityCodeQuery("086", "01", pmtty);
		HttpRequest.Companion.getInstance().httpPost((Activity) getContext(), HttpURLS.INSTANCE.getCityCodeQuery(), false, "cityCodeQuery", body,
				(isError, response, type, error) -> {
                    if(isError) {
						ToastUtil.Companion.toastError("获取省份失败！");
                    } else {
                        // TODO Auto-generated method stub
                        JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                        String ProcessCode = ResponseHead.getString("ProcessCode");
                        String ProcessDes = ResponseHead.getString("ProcessDes");
                        String a = response.getString("ResponseFields");
                        if (ProcessCode.equals("0000")) {
                            switch (type) {
                                case 0:
                                    break;
                                case 1:
                                    String ResponseFields = response.getString("ResponseFields");
                                    if(ResponseFields != null){
                                        provinces = new ArrayList<>();
                                        provinces = (ArrayList<City>) JSONObject.parseArray(ResponseFields, City.class);
                                        Message message = new Message();
                                        message.what = REFRESH_PROVINCE;
                                        handler.sendMessage(message);
                                    }
                                    break;

                                case 2:
                                    JSONObject ResponseFields1 = response.getJSONObject("ResponseFields");
                                    if (ResponseFields1 != null){
                                        String proinfo = ResponseFields1.getString("CityDetail");
                                        provinces = new ArrayList<>();
                                        if (proinfo.startsWith("[")) {
                                            provinces = (ArrayList<City>) JSONObject.parseArray(proinfo,City.class);
                                        } else if (proinfo.startsWith("{")) {
                                            provinces.add(JSONObject.parseObject(proinfo, City.class));
                                        };
                                        Message message = new Message();
                                        message.what = REFRESH_PROVINCE;
                                        handler.sendMessage(message);
                                    }
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                });
	}

	private void getCitys(String provinceCode, String pmtty) {
		CusFormBody body = HttpRequestParams.INSTANCE.getParamsForCityCodeQuery(provinceCode, "02", pmtty);

		HttpRequest.Companion.getInstance().httpPost((Activity) getContext(), HttpURLS.INSTANCE.getCityCodeQuery(), false, "cityCodeQuery", body,
				(isError, response, type, error) -> {
                    if(isError) {
						ToastUtil.Companion.toastError("获取市失败！");
                    } else {
                        // TODO Auto-generated method stub
                        JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                        String ProcessCode = ResponseHead.getString("ProcessCode");
                        if (ProcessCode.equals("0000")) {
                            citys = new ArrayList<>();
                            String ResponseFields = response.getString("ResponseFields");
                            if (type == 1) {
                                citys = (ArrayList<City>) JSONObject.parseArray(ResponseFields, City.class);
                            } else if (type == 2) {
                                JSONObject ResponseField1 = response.getJSONObject("ResponseFields");
                                String cityDetail = ResponseField1.getString("CityDetail");
                                citys.add(JSONObject.parseObject(cityDetail, City.class));
                            }
                            Message message = new Message();
                            message.what = REFRESH_CITY;
                            handler.sendMessage(message);
                        }
                    }
                });

	}

	// private ArrayList<City> initProvince() {
	// dbm = DBManager.getInstance();
	// db = dbm.openDatabase();
	// ArrayList<City> list = new ArrayList<City>();
	// try {
	// String sql = "select * from province where flag = '1'";
	// Cursor cursor = db.rawQuery(sql, null);
	// cursor.moveToFirst();
	// while (!cursor.isLast()) {
	// String code = cursor.getString(cursor.getColumnIndex("code"));
	// String capital = cursor.getString(cursor
	// .getColumnIndex("capital"));
	// byte bytes[] = cursor.getBlob(cursor.getColumnIndex("name"));
	// String name = new String(bytes, "UTF-8");
	// City city = new City();
	// city.CityName = name;
	// city.CityCode = code;
	// city.Capital = capital;
	// list.add(city);
	// cursor.moveToNext();
	// }
	// String code = cursor.getString(cursor.getColumnIndex("code"));
	// String capital = cursor.getString(cursor.getColumnIndex("capital"));
	// byte bytes[] = cursor.getBlob(cursor.getColumnIndex("name"));
	// String name = new String(bytes, "UTF-8");
	// City city = new City();
	// city.CityName = name;
	// city.CityCode = code;
	// city.Capital = capital;
	// list.add(city);
	// } catch (Exception e) {
	// }
	// dbm.closeDatabase();
	// return list;
	// }

	// private ArrayList<City> initCity(String pcode) {
	// if (!pcode.equals("9999")) {
	// dbm = DBManager.getInstance();
	// db = dbm.openDatabase();
	// ArrayList<City> list = new ArrayList<City>();
	// try {
	// String sql = "select * from city where flag = '1' and pcode='" + pcode +
	// "'";
	// Cursor cursor = db.rawQuery(sql, null);
	// cursor.moveToFirst();
	// while (!cursor.isLast()) {
	// String code = cursor.getString(cursor
	// .getColumnIndex("code"));
	// String capital = cursor.getString(cursor
	// .getColumnIndex("capital"));
	// byte bytes[] = cursor
	// .getBlob(cursor.getColumnIndex("name"));
	// String name = new String(bytes, "UTF-8");
	// City city = new City();
	// city.CityName = name;
	// System.out.println("city.CityName = " + city.CityName);
	// city.CityCode = code;
	// city.Capital = capital;
	// city.PcityCode = pcode;
	// list.add(city);
	// cursor.moveToNext();
	// }
	// String code = cursor.getString(cursor.getColumnIndex("code"));
	// String capital = cursor.getString(cursor
	// .getColumnIndex("capital"));
	// byte bytes[] = cursor.getBlob(cursor.getColumnIndex("name"));
	// String name = new String(bytes, "UTF-8");
	// City city = new City();
	// city.CityName = name;
	// city.CityCode = code;
	// city.Capital = capital;
	// city.PcityCode = pcode;
	// list.add(city);
	// } catch (Exception e) {
	// }
	// if (dbm != null)
	// dbm.closeDatabase();
	// return list;
	// } else {
	// return tempCitys;
	// }
	// }

	// public City searchCityByCode(final String cityCode) {
	// City city = new City();
	// dbm = DBManager.getInstance();
	// db = dbm.openDatabase();
	// String code,pcode;
	// String capital;
	// byte bytes[];
	// String name;
	// try {
	// String sql = "select * from city where flag = '1' and ampcode = '" +
	// cityCode
	// + "'";
	// Cursor cursor = db.rawQuery(sql, null);
	// if(cursor.getCount()>0) {
	// cursor.moveToFirst();
	// code = cursor.getString(cursor.getColumnIndex("code"));
	// capital = cursor.getString(cursor.getColumnIndex("capital"));
	// bytes = cursor.getBlob(cursor.getColumnIndex("name"));
	// pcode = cursor.getString(cursor.getColumnIndex("pcode"));
	// name = new String(bytes, "UTF-8");
	// if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(capital)
	// && !TextUtils.isEmpty(code)) {
	// city.CityName = name;
	// city.CityCode = code;
	// city.PcityCode = pcode;
	// city.Capital = capital;
	// }
	// }
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	//
	// if (dbm != null)
	// dbm.closeDatabase();
	//
	// return city;
	// }

	// public void initCityBySearch(final String cityName) {
	// dbm = DBManager.getInstance();
	// db = dbm.openDatabase();
	// ArrayList<City> provinces = new ArrayList<City>();
	// ArrayList<City> citys = new ArrayList<City>();
	// String code,pcode;
	// String capital;
	// byte bytes[];
	// String name;
	// City city;
	// try {
	//// Log.i("RND", "1");
	// String sql = "select * from province where flag = '1' and name like '%" +
	// cityName
	// + "%' or capital like '%" + cityName + "%'";
	// Cursor cursor = db.rawQuery(sql, null);
	// if(cursor.getCount()>0) {
	// cursor.moveToFirst();
	// while (!cursor.isLast()) {
	// code = cursor.getString(cursor.getColumnIndex("code"));
	// capital = cursor.getString(cursor.getColumnIndex("capital"));
	// bytes = cursor.getBlob(cursor.getColumnIndex("name"));
	// name = new String(bytes, "UTF-8");
	// if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(capital)
	// && !TextUtils.isEmpty(code)) {
	// city = new City();
	// city.CityName = name;
	// city.CityCode = code;
	// city.Capital = capital;
	// provinces.add(city);
	// cursor.moveToNext();
	// }
	// }
	//// Log.i("RND", "2");
	// code = cursor.getString(cursor.getColumnIndex("code"));
	// capital = cursor.getString(cursor.getColumnIndex("capital"));
	// bytes = cursor.getBlob(cursor.getColumnIndex("name"));
	// name = new String(bytes, "UTF-8");
	// if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(capital)
	// && !TextUtils.isEmpty(code)) {
	// city = new City();
	// city.CityName = name;
	// city.CityCode = code;
	// city.Capital = capital;
	// provinces.add(city);
	// }
	// }
	// sql = "select * from city where flag = '1' and name like '%" + cityName
	// + "%' or capital like '%" + cityName + "%'";
	//
	//// Log.i("RND", "sql--->"+sql);
	//// Log.i("RND", "3");
	// cursor = db.rawQuery(sql, null);
	// if(cursor.getCount()>0) {
	// cursor.moveToFirst();
	// while (!cursor.isLast()) {
	// code = cursor.getString(cursor.getColumnIndex("code"));
	// capital = cursor.getString(cursor.getColumnIndex("capital"));
	// bytes = cursor.getBlob(cursor.getColumnIndex("name"));
	// pcode = cursor.getString(cursor.getColumnIndex("pcode"));
	// name = new String(bytes, "UTF-8");
	// if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(capital)
	// && !TextUtils.isEmpty(code)) {
	// city = new City();
	// city.CityName = name;
	// city.CityCode = code;
	// city.PcityCode = pcode;
	// city.Capital = capital;
	// citys.add(city);
	// cursor.moveToNext();
	// }
	// }
	//// Log.i("RND", "4");
	// code = cursor.getString(cursor.getColumnIndex("code"));
	// capital = cursor.getString(cursor.getColumnIndex("capital"));
	// bytes = cursor.getBlob(cursor.getColumnIndex("name"));
	// pcode = cursor.getString(cursor.getColumnIndex("pcode"));
	// name = new String(bytes, "UTF-8");
	// if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(capital)
	// && !TextUtils.isEmpty(code)) {
	// city = new City();
	// city.CityName = name;
	// city.CityCode = code;
	// city.PcityCode = pcode;
	// city.Capital = capital;
	// citys.add(city);
	// }
	// }
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	//
	// if (dbm != null)
	// dbm.closeDatabase();
	//
	//// Log.i("RND", "citys.size()--->"+citys.size());
	//
	// if (citys.size() != 0) {
	// provinces.add(new City("江苏省", "9999", "QTCS"));
	// }
	//
	// tempCitys = citys;
	// if(provinces.size() != 0) {
	// CityPicker.this.citys = initCity(provinces.get(0).CityCode);
	// Collections.sort(CityPicker.this.citys, comparator);
	// } else {
	// CityPicker.this.citys = new ArrayList<City>();
	// }
	// CityPicker.this.provinces = provinces;
	// provincePicker.setCity(provinces);
	// provincePicker.setDefault(0);
	// tempProvinceIndex = 0;
	// temCityIndex = 0;
	// cityPicker.setCity(CityPicker.this.citys);
	// cityPicker.setDefault(0);
	// }

	class ComparatorRecordByCapital implements Comparator<City> {
		@Override
		public int compare(City city1, City city2) {
			if (city1.Capital.compareTo(city2.Capital) > 0) {
				return 1;
			} else if (city1.Capital.compareTo(city2.Capital) == 0) {
				return 0;
			} else {
				return -1;
			}
		}
	}

}
