package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class City {

	public boolean flag;//是否选中
	public String CityName;//城市名称
	public String CityCode;//城市编码
	public String PcityCode;//城市编码
	public String Capital;//城市名称首字母
	public City(String cityName, String cityCode, String capital) {
		super();
		CityName = cityName;
		CityCode = cityCode;
		Capital = capital;
		flag = false;
	}
	public City() {
		super();
	}

}
