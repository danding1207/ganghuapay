package com.mqt.ganghuazhifu.adapter

import android.content.Context
import android.view.View
import android.view.View.OnClickListener

import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.QueryHistory

import java.util.ArrayList

class NumListAdapter(private val mContext: Context) : BaseAdapter<QueryHistory>() {
    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }
    init {
        list = ArrayList<QueryHistory>()
        layoutResId = R.layout.query_pay_history_item2
    }

    override fun convert(helper: BaseViewHolder, position: Int, item: QueryHistory) {
        // 用户名缩减
        val nameBuilder: StringBuilder
        var nm: String? = item.UserNm
        // 南京的账户如果没有用户名，字段不存在，就赋值为''
        if (null == nm) {
            nm = ""
        }
        nameBuilder = StringBuilder(nm)

        if (nm.length > 6) {
            nameBuilder.replace(6, nm.length, "***")
        }

        helper.setText(R.id.tv_name, nameBuilder.toString()).setText(R.id.tv_num, item.UserNb)
                .setText(R.id.tv_pname, item.PayeeNm)
                .setOnClickListener(R.id.iv_cancle, OnClickListener { v ->
                    if (onRecyclerViewItemClickListener != null) {
                        onRecyclerViewItemClickListener!!.onItemClick(v, position)
                    }
                })
    }

}
