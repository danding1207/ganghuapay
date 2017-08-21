package com.mqt.ganghuazhifu.bean;

public class Version {
    private String url;   //http://cdn.longtugame.com/channel_bin/520006/apk/2.1.4/520006_397.apk",
    private String versionCode;   //最新版本号
    private int versionNum;  //最新版本号(数字化)
    private String platform;  //平台 0:Android   1:iOS
    private String minpermVersionCode;  //兼容的最低版本号
    private int minpermVersionNum;  //兼容的最低版本号(数字化)
	public int getVersionNum() {
		return versionNum;
	}
	public void setVersionNum(int versionNum) {
		this.versionNum = versionNum;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getMinpermVersionCode() {
		return minpermVersionCode;
	}
	public void setMinpermVersionCode(String minpermVersionCode) {
		this.minpermVersionCode = minpermVersionCode;
	}
	public int getMinpermVersionNum() {
		return minpermVersionNum;
	}
	public void setMinpermVersionNum(int minpermVersionNum) {
		this.minpermVersionNum = minpermVersionNum;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
}
