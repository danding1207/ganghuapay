package com.mqt.ganghuazhifu.bean;

/**
 * 营业费账单
 * @author yang.lei
 *
 */
public class QueryHistory {
	public String UserNb;//户号
	public String UserNm;//户名
	public String PayeeNm;//收款单位名称
	public String PayeeCode;//收款单位编号
	public String CityName;//市名称
	public String ProvinceName;//省名称
	public String FullPayeeNm;//收款单位全称
	public String ProvinceCode;//省编码
	public String CityCode;//市编码

	public QueryHistory(String userNb, 
			String userNm,
			String payeeNm, 
			String payeeCode,
			String cityName, 
			String provinceName,
			String fullPayeeNm,
			String provinceCode,
			String cityCode) {
		super();
		UserNb = userNb;
		UserNm = userNm;
		PayeeNm = payeeNm;
		PayeeCode = payeeCode;
		CityName  = cityName;
		ProvinceName = provinceName;
		FullPayeeNm = fullPayeeNm;
		ProvinceCode = provinceCode;
		CityCode = cityCode;
	}
	
	public QueryHistory() {
	}
}
