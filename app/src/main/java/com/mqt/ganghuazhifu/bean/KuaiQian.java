package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class KuaiQian {

	public String MerchantAcctId;//人民币网关账号
	public String InputCharset;//编码方式
	public String PageUrl;//接收支付结果的页面地址
	public String BgUrl;//服务器接收支付结果的地址
	public String Version;//网关版本
	public String Language;//语言种类
	public String SignType;//签名类型
	public String PayerName;//支付人姓名
	public String PayerContactType;//支付人联系类型
	public String PayerContact;//支付人联系方式
	public String OrderId;//商户订单号
	public String OrderAmount;//订单金额
	public String OrderTime;//订单提交时间
	public String ProductName;//商品名称
	public String ProductNum;//商品数量
	public String ProductId;//商品代码
	public String ProductDesc;//商品描述
	public String MobileGateway;//商品描述
	public String Ext1;//扩展字段1
	public String Ext2;//扩展字段2
	public String PayType;//支付方式
	public String BankId;//银行代码
	public String RedoFlag;//同一订单禁止重复提交标志
	public String Pid;//快钱合作伙伴的帐户号
	public String SignMsg;//加签后的串
	public String PayerIdType;//指定付款人
	public String PayerId;//付款人标识
	public String InterFaceName;//接口名称
	public String InterFaceVersion;//接口版本号
	public String TranData;//交易数据
	public String MerSignMsg;//订单签名数据
	public String MerCert;//商城公钥数据
	public String SubmitURL;//提交URL
	
	public KuaiQian() {
    }
	
	@Override
	public String toString() {
		return "KuaiQian [MerchantAcctId=" + MerchantAcctId + ", InputCharset="
				+ InputCharset + ", PageUrl=" + PageUrl + ", BgUrl=" + BgUrl
				+ ", Version=" + Version + ", Language=" + Language
				+ ", SignType=" + SignType + ", PayerName=" + PayerName
				+ ", PayerContactType=" + PayerContactType + ", PayerContact="
				+ PayerContact + ", OrderId=" + OrderId + ", OrderAmount="
				+ OrderAmount + ", OrderTime=" + OrderTime + ", ProductName="
				+ ProductName + ", ProductNum=" + ProductNum + ", ProductId="
				+ ProductId + ", ProductDesc=" + ProductDesc
				+ ", MobileGateway=" + MobileGateway + ", Ext1=" + Ext1
				+ ", Ext2=" + Ext2 + ", PayType=" + PayType + ", BankId="
				+ BankId + ", RedoFlag=" + RedoFlag + ", Pid=" + Pid
				+ ", SignMsg=" + SignMsg 
				+ ", PayerIdType=" + PayerIdType 
				+ ", PayerId=" + PayerId
				+ ", InterFaceName=" + InterFaceName
				+ ", InterFaceVersion=" + InterFaceVersion
				+ ", TranData=" + TranData
				+ ", MerSignMsg=" + MerSignMsg
				+ ", MerCert=" + MerCert
				+ ", SubmitURL=" + SubmitURL
				+"]";
	}

}
