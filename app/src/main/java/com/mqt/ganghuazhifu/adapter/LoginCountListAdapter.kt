package com.mqt.ganghuazhifu.adapter

import android.view.View
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.GeneralContact

import java.util.ArrayList

class LoginCountListAdapter : BaseAdapter<String>() {
    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }
    private var onItemClickListener: OnItemClickListener? = null

    init {
        list = ArrayList<String>()
        layoutResId = R.layout.login_list_item
    }

    override fun convert(helper: BaseViewHolder, position: Int, item: String) {
        helper.setText(R.id.tv_logincount, item)
                .setOnClickListener(R.id.tv_logincount, View.OnClickListener {
                    if (onItemClickListener != null)
                        onItemClickListener!!.onItemClick(1, position)
                })
                .setOnClickListener(R.id.iv_cancle, View.OnClickListener {
                    if (onItemClickListener != null)
                        onItemClickListener!!.onItemClick(2, position)
                })
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(type: Int, position: Int) // 1, 设为默认， 2， 编辑 ，3， 删除
    }

}
