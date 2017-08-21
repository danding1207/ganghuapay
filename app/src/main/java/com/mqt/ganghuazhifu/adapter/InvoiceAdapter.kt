package com.mqt.ganghuazhifu.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.FeedBack
import com.mqt.ganghuazhifu.bean.Invoice
import com.mqt.ganghuazhifu.fragment.AccountFragment
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils

import java.util.ArrayList

class InvoiceAdapter(private val mContext: BaseActivity) : BaseAdapter<Invoice>() {

    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }

    init {
        list = ArrayList<Invoice>()
        layoutResId = R.layout.activity_invoice_item
    }

    @SuppressLint("ResourceAsColor")
    override fun convert(helper: BaseViewHolder, position: Int, item: Invoice) {
        helper.setText(R.id.tv_invoice_payeeName, item.PayeeName)
                .setText(R.id.tv_invoice_taitou, item.InvoiceTaitou)
                .setText(R.id.tv_invoice_amount, item.InvoiceAmount)
                .setText(R.id.tv_invoice_time, item.InvoiceTime)
    }

}
