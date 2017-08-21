package com.mqt.ganghuazhifu.adapter

import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.GeneralContact
import java.util.ArrayList

class AddNumListAdapter : BaseAdapter<GeneralContact>() {
    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }

    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    init {
        list = ArrayList<GeneralContact>()
        layoutResId = R.layout.num_list_item
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
        helper.setText(R.id.tv_name, nameBuilder.toString())
                .setText(R.id.tv_num, item.usernb)
                .setText(R.id.tv_pname, item.payeename)
    }

}
