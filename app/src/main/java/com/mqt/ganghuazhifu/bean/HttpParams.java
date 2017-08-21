package com.mqt.ganghuazhifu.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * post参数封装类
 * 
 * @author yang.lei
 */
public class HttpParams {

	Map<String, String> map = new HashMap<String, String>();

	public void put(String key, String value) {
		map.put(key, value);
	}

	public Map<String, String> getMap() {
		return map;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append("\""+entry.getKey()+"\":\""+entry.getValue()+"\",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");
		return sb.toString();
	}
	
}
