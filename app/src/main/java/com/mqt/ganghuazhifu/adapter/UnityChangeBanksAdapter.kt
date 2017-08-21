package com.mqt.ganghuazhifu.adapter

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.BindedBanks
import com.mqt.ganghuazhifu.utils.BanksUtils
import java.util.*

class UnityChangeBanksAdapter(private val mContext: Context, private var type: Int// 1:正常状态;2:编辑状态;
                              , private var gateid: String) : BaseAdapter<BindedBanks>() {
    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }

    init {
        list = ArrayList<BindedBanks>()
        layoutResId = R.layout.unity_binded_banks_item
    }

    fun updateType(type: Int) {
        this.type = type
        this.notifyDataSetChanged()
    }

    fun updateGateid(gateid: String) {
        this.gateid = gateid
        initGateid()
        this.notifyDataSetChanged()
    }

    fun initGateid() {
        var ins: Boolean = false
        list!!.forEach { item ->
            if (this.gateid == item.gateid) {
                ins = true
            }
        }
        if (list!!.size>0 && !ins) {
            this.gateid = list!![0].gateid
        }
    }

    override fun updateList(list: ArrayList<BindedBanks>?) {
        this.list!!.clear()
        this.list = ArrayList<BindedBanks>()
        if (list != null)
            this.list!!.addAll(list)
        initGateid()
        this.notifyDataSetChanged()
    }

    fun getBindedBank(): BindedBanks? {
        list!!.forEach { item ->
            if (this.gateid == item.gateid) {
                return item
            }
        }
        if (list!!.size>0) {
            this.gateid = list!![0].gateid
            return list!![0]
        } else {
            return null
        }
    }

    override fun convert(helper: BaseViewHolder, position: Int, item: BindedBanks) {
        var img_resid = R.drawable.dialog_clarity
        var listener: OnClickListener? = null
        if (type == 1) {
            if (gateid == item.gateid) {
                img_resid = R.drawable.unity_right
            }
            listener = OnClickListener { v -> }
        } else {
            img_resid = R.drawable.unity_cancle
            listener = OnClickListener { v -> onBanksSettingClickListener!!.onBanksSettingClick(v, position) }
        }

        val ss = if ("CREDITCARD" == item.paytype) "信用卡" else "借记卡"
        helper.setText(R.id.tv_bank_num, BanksUtils.getBanksUtils().BANKS[item.gateid]!!.name + ss + "(*" + item.cardid + ")")
                .setImageResource(R.id.iv_bank_icon, BanksUtils.getBanksUtils().getBankIconResId(item.gateid))
                .setImageResource(R.id.iv_bank_setting, img_resid)
                .setOnClickListener(R.id.iv_bank_setting, listener)
    }

    var onBanksSettingClickListener: OnBanksSettingClickListener? = null

    interface OnBanksSettingClickListener {
        fun onBanksSettingClick(view: View, position: Int)
    }
}
