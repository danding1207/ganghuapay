package com.mqt.ganghuazhifu.bean;

public class IP {

	public String code;// 0:成功
	public IPData data;// 城市名称

	public IP() {
	}

	public static class IPData {
		public String country;// 中国
		public String country_id;// CN
		public String area;// 华北
		public String area_id;// 100000
		public String region;// 北京市
		public String region_id;// 110000
		public String city;// 北京市
		public String city_id;// 110100
		public String county;//
		public String county_id;//
		public String isp;//
		public String isp_id;//
		public String ip;// 222.249.172.254
	}

}
