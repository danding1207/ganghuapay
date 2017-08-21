package com.mqt.ganghuazhifu.adapter

import android.text.TextUtils

import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.GeneralContact

import java.util.ArrayList

/**
 * Created by danding1207 on 16/10/27.
 */

class GeneralContactAdapter : BaseAdapter<GeneralContact>() {
    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }
    init {
        list = ArrayList<GeneralContact>()
        layoutResId = R.layout.query_pay_history_item
    }

    override fun convert(helper: BaseViewHolder, position: Int, item: GeneralContact) {
        // 用户名缩减
        val nameBuilder: StringBuilder
        var nm: String? = item.usernm
        // 南京的账户如果没有用户名，字段不存在，就赋值为''
        if (null == nm) {
            nm = ""
        }
        nameBuilder = StringBuilder(nm)
        if (nm.length > 6) {
            nameBuilder.replace(6, nm.length, "***")
        }
        var s = ""
        if (!TextUtils.isEmpty(item.remark) && item.remark != "[]") {
            s = "(" + item.remark + ")"
        }
        helper.setText(R.id.tv_name, nameBuilder.toString() + s)
                .setText(R.id.tv_num, item.usernb)
                .setText(R.id.tv_pname, item.payeename)
    }
}
