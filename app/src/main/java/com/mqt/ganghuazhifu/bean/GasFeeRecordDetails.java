package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

/**
 * 气费
 * 
 * @author yang.lei
 *
 */
@Parcel
public class GasFeeRecordDetails {

	public String Nature;// 性质
	public String LastTime;// 上次读数
	public String ThisTime;// 本次读数
	public String Volume;// 气量
	public String LadderYearSurplus;// 阶梯年剩余
	public String LadderVolume;// 阶梯气量
	public String LadderPrice;// 阶梯气价
	public String LadderMoney;// 阶梯金额

	public GasFeeRecordDetails(String nature, String lastTime, String thisTime, String volume, String ladderYearSurplus, String ladderVolume, String ladderPrice, String ladderMoney) {
		Nature = nature;
		LastTime = lastTime;
		ThisTime = thisTime;
		Volume = volume;
		LadderYearSurplus = ladderYearSurplus;
		LadderVolume = ladderVolume;
		LadderPrice = ladderPrice;
		LadderMoney = ladderMoney;
	}

	public GasFeeRecordDetails() {
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("GasFeeRecord [Nature=" + Nature + ", LastTime=" + LastTime
				+ ", ThisTime=" + ThisTime + ", Volume=" + Volume + ", LadderYearSurplus="
				+ LadderYearSurplus + ", LadderVolume=" + LadderVolume + ", LadderPrice="
				+ LadderPrice + ", LadderMoney=" + LadderMoney + "]");
		return sb.toString();
	}

}
