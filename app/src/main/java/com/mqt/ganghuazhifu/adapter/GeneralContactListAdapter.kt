package com.mqt.ganghuazhifu.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.View.OnClickListener

import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.GeneralContact

import java.util.ArrayList

class GeneralContactListAdapter(private val mContext: Context) : BaseAdapter<GeneralContact>() {
    private var onItemClickListener: OnItemClickListener? = null
    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }
    init {
        list = ArrayList<GeneralContact>()
        layoutResId = R.layout.general_contact_item
    }

    override fun convert(helper: BaseViewHolder, position: Int, item: GeneralContact) {
        val img_off: Drawable
        if (item.isdefault == "0") {
            img_off = ContextCompat.getDrawable(mContext, R.drawable.checkbox_off)
            img_off.setBounds(0, 0, img_off.minimumWidth, img_off.minimumHeight)
        } else {
            img_off = ContextCompat.getDrawable(mContext, R.drawable.checkbox_on)
            img_off.setBounds(0, 0, img_off.minimumWidth, img_off.minimumHeight)
        }

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
        val s = if (item.remark != null && item.remark == "[]") "" else item.remark

        helper.setText(R.id.tv_name, nameBuilder.toString())
                .setText(R.id.tv_num, item.usernb)
                .setText(R.id.tv_danwei, item.cdtrnm)
                .setText(R.id.tv_mark, s)
                .setCompoundDrawables(R.id.tv_moren, img_off, null, null, null)
                .setText(R.id.tv_moren, if (item.isdefault == "0") "设为默认" else "默认联系人")
                .setTextColor(R.id.tv_moren, if (item.isdefault == "0") ContextCompat.getColor(mContext, R.color.orange) else ContextCompat.getColor(mContext, R.color.dark_gray))
                .setVisible(R.id.tv_shanchu, if(item.isdefault == "0") View.VISIBLE else View.GONE)
                .setOnClickListener(R.id.tv_moren, OnClickListener {
                    if (onItemClickListener != null)
                        onItemClickListener!!.onItemClick(1, position)
                })
                .setOnClickListener(R.id.tv_bianji, OnClickListener {
                    if (onItemClickListener != null)
                        onItemClickListener!!.onItemClick(2, position)
                })
                .setOnClickListener(R.id.tv_shanchu, OnClickListener {
                    if (onItemClickListener != null)
                        onItemClickListener!!.onItemClick(3, position)
                })
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(type: Int, position: Int) // 1, 设为默认， 2， 编辑 ，3， 删除
    }

}
