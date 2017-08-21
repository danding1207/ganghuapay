package com.mqt.ganghuazhifu.bean;

import org.parceler.Parcel;

@Parcel
public class BeanBinnal {

	public String comkey;//图片id
	public String comval;//图片url
	public String comtype;//02
	public String remark;//注释

	public BeanBinnal() {
	}

	public BeanBinnal(String comkey, String comval, String comtype,
			String remark) {
		super();
		this.comkey = comkey;
		this.comval = comval;
		this.comtype = comtype;
		this.remark = remark;
	}

}
