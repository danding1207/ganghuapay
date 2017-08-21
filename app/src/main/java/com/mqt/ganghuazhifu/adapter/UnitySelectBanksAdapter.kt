package com.mqt.ganghuazhifu.adapter

import android.content.Context
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.utils.BanksUtils
import java.util.*

class UnitySelectBanksAdapter(private val mContext: Context, type: Int) : BaseAdapter<BanksUtils.Banks>() {

    var type: Int = 0
        private set// 1:debitCard;2:creditCard;

    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }

    init {
        list = ArrayList<BanksUtils.Banks>()
        layoutResId = R.layout.unity_select_banks_item
        footerLayoutResId = R.layout.unity_select_banks_item_footer
        this.type = type
    }

    fun updateListType(type: Int) {
        this.type = type
    }

    override fun convert(helper: BaseViewHolder, position: Int, item: BanksUtils.Banks) {
        helper.setText(R.id.tv_bank_name, item.name)
                .setImageResource(R.id.iv_bank_icon, item.resid)
    }

}
