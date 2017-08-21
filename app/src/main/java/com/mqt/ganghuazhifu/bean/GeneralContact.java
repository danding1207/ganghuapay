package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class GeneralContact {

	public String id;// 常用联系人主键
	public String userid;// 登录用户主键
	public String privincecode;// 省编码
	public String citycode;// 市编码
	public String payeecode;// 收款单位编号
	public String cdtrnm;// 收款单位简称
	public String payeename;// 收款单位名称
	public String usernb;// 户号
	public String usernm;// 户名
	public String isdefault;// 是否默认联系人: 0 否 1 是
	public String remark;// 备注
	public String pcityname;//省名称
	public String ccityname;//市名称
	

	public GeneralContact(String id, String userid, String privincecode, String citycode, String payeecode,
			String cdtrnm, String payeenm, String usernb, String usernm, String isdefault, String remark,
			String pcityname, String ccityname) {
		super();
		this.id = id;
		this.userid = userid;
		this.privincecode = privincecode;
		this.citycode = citycode;
		this.payeecode = payeecode;
		this.cdtrnm = cdtrnm;
		this.payeename = payeenm;
		this.usernb = usernb;
		this.usernm = usernm;
		this.isdefault = isdefault;
		this.remark = remark;
		this.pcityname = pcityname;
		this.ccityname = ccityname;
	}

	public GeneralContact() {
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id--->" + id + "/n");
		sb.append("userid--->" + userid + "/n");
		sb.append("privincecode--->" + privincecode + "/n");
		sb.append("citycode--->" + citycode + "/n");
		sb.append("payeecode--->" + payeecode + "/n");
		sb.append("cdtrnm--->" + cdtrnm + "/n");
		sb.append("payeenm--->" + payeename + "/n");
		sb.append("usernb--->" + usernb + "/n");
		sb.append("usernm--->" + usernm + "/n");
		sb.append("isdefault--->" + isdefault + "/n");
		sb.append("remark--->" + remark + "/n");
		sb.append("pcityname--->" + pcityname + "/n");
		sb.append("ccityname--->" + ccityname + "/n");
		return sb.toString();
	}
}
