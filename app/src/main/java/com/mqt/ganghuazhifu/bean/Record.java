package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class Record {
	
	public String paydate;//支付日期  20141217
	public String pmttp;//业务类型  010001
//	010001	气费    缴纳燃气费
//	010002	营业费   缴纳营业费
	public String ordernb;//订单号  00000000000000000401
	public String amount;//金额  1.00
	public String mount;//气量  1.00 m
	public String status;//交易状态  PR00
//	PR00 已付款
//	PR01 待付款
//	PR02 已取消
//	PR03 支付失败
//	PR04 待退款
//	PR05 已退款
	public String ordersettime;//订单创建时间  2014-12-17T15:18:56
	public String cdtracct;//收款方账号   110110110110
	public String cdtrnm;//收款方户名  徐州燃气费公司
	public String amountflow;//资金流向 //O:支出;I:收入;
	public String paymenttime;//付款时间  2014-12-17T15:19:02
	public String orderdate;//订单日期  20141217
	public String payeecode;//收款单位编号  32000032030062
	public String usernb;//1000168
	public String usernm;//户名
	public String useraddr;//用户地址
	public String dbtracct;//
	public String dbtrnm;//
	public String errormsg;//
	public String ustrd;//
	public String paystatus;//
	public String r;//
	public String pid;//21
	public String id;//281
	public String pic;//
	public int year;
	public int month;
	public int tag;//1:头;2:中;3:尾;4:只有一条数据（既是头又是尾）;
	public String icome;
	public String pay;
	public String nfcpayflag;//NFC充值状态
	public String nfcflag;//NFC标识 11,NFC
	public String nfcpaytime;
	public String iccardno;//IC 卡号 16位 前补8个0

	@Override
	public String toString() {
		return "Record [paydate=" + paydate + ", pmttp=" + pmttp + ", ordernb=" + ordernb + ", amount=" + amount
				+ ", status=" + status + ", ordersettime=" + ordersettime + ", cdtracct=" + cdtracct + ", cdtrnm="
				+ cdtrnm + ", amountflow=" + amountflow + ", paymenttime=" + paymenttime + ", orderdate=" + orderdate
				+ ", payeecode=" + payeecode + ", usernb=" + usernb + ", usernm=" + usernm + ", useraddr=" + useraddr
				+ ", dbtracct=" + dbtracct + ", dbtrnm=" + dbtrnm + ", errormsg=" + errormsg + ", ustrd=" + ustrd
				+ ", paystatus=" + paystatus + ", r=" + r + ", pid=" + pid + ", id=" + id + ", pic=" + pic + ", year="
				+ year + ", month=" + month + ", tag=" + tag + ", icome=" + icome + ", pay=" + pay + ", nfcpayflag="
				+ nfcpayflag + ", nfcflag=" + nfcflag + "]";
	}

}
