package com.mqt.ganghuazhifu.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.GeneralContact
import com.mqt.ganghuazhifu.bean.Record
import com.mqt.ganghuazhifu.bean.RecordsList
import com.mqt.ganghuazhifu.listener.OnTapListener

import java.math.BigDecimal
import java.util.ArrayList

class RecordsListAdapter(private val context: Context) : BaseAdapter<Record>() {
    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }
    init {
        list = ArrayList<Record>()
    }

    fun updateRecordsList(list: ArrayList<RecordsList>) {
        this.list!!.clear()
        for (recordsList in list) {
            var icome = 0f
            var pay = 0f
            for (record in recordsList.QryResults) {
                this.list!!.add(record)
                if (record.amountflow == "1") {
                    icome += java.lang.Float.parseFloat(record.amount)
                } else {
                    if (record.status == "PR00")
                        pay += java.lang.Float.parseFloat(record.amount)
                }
            }
            val decimal1 = BigDecimal(icome.toDouble())
            val decimal2 = BigDecimal(pay.toDouble())
            recordsList.QryResults[0].icome = decimal1.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
            recordsList.QryResults[0].pay = decimal2.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
        }
        this.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return list!![position].tag
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            1 -> layoutResId = R.layout.records_list_item_top
            2 -> layoutResId = R.layout.records_list_item_body
            3 -> layoutResId = R.layout.records_list_item_bottom
            4 -> layoutResId = R.layout.records_list_item_only
        }
        return super.onCreateViewHolder(parent, BaseAdapter.NORMALVIEWHOLDER)
    }

    override fun convert(helper: BaseViewHolder, position: Int, item: Record) {

        var tv_card_type = ""
        var tv_card_status = ""

        var tv_card_status_color = ContextCompat.getColor(context, R.color.red)
        if ("11" == item.nfcflag) {
            tv_card_type = "(金额表NFC)"
            if ("11" == item.nfcpayflag) {// 成功
                tv_card_status_color = ContextCompat.getColor(context, R.color.green_slow)
                tv_card_status = "写入成功"
            } else {
                tv_card_status = "未写入"
            }
        } else if ("12" == item.nfcflag) {
            tv_card_type = "(金额表蓝牙)"
            if ("11" == item.nfcpayflag) {// 成功
                tv_card_status_color = ContextCompat.getColor(context, R.color.green_slow)
                tv_card_status = "写卡成功"
            } else {
                tv_card_status = "未写卡"
            }
        } else if ("13" == item.nfcflag) {
            tv_card_type = "(蓝牙表)"
            if ("11" == item.nfcpayflag) {// 成功
                tv_card_status_color = ContextCompat.getColor(context, R.color.green_slow)
                tv_card_status = "写表成功"
            } else {
                tv_card_status = "未写表"
            }
        } else if ("14" == item.nfcflag) {
            tv_card_type = "(气量表NFC)"
            if ("11" == item.nfcpayflag) {// 成功
                tv_card_status_color = ContextCompat.getColor(context, R.color.green_slow)
                tv_card_status = "写表成功"
            } else {
                tv_card_status = "未写表"
            }
        } else if ("15" == item.nfcflag) {
            tv_card_type = "(气量表蓝牙)"
            if ("11" == item.nfcpayflag) {// 成功
                tv_card_status_color = ContextCompat.getColor(context, R.color.green_slow)
                tv_card_status = "写卡成功"
            } else {
                tv_card_status = "未写卡"
            }
        }



        var tv_name = ""
        var iv_record_pic = R.drawable.recorder_gas
        when (item.pmttp) {
            "010001" -> {
                tv_name = "缴纳燃气费"
                iv_record_pic = R.drawable.recorder_gas
            }
            "010002" -> {
                tv_name = "缴纳营业费"
                iv_record_pic = R.drawable.recorder_busi
            }
            "010003" -> {
                tv_name = "预存营业费"
                iv_record_pic = R.drawable.recorder_busi
            }
            "020001" -> {
                tv_name = "缴纳水费"
                iv_record_pic = R.drawable.recorder_water
            }
        }

        var tv_status = ""
        var tv_status_color = ContextCompat.getColor(context, R.color.gray)
        when (item.status) {
            "PR00" ->
                //已付款
                tv_status = "已付款"
            "PR01" -> {
                //待付款
                tv_status = "待付款"
                tv_status_color = ContextCompat.getColor(context, R.color.red)
            }
            "PR02" ->
                //已取消
                tv_status = "已取消"
            "PR03" -> {
                //支付失败
                tv_status = "支付失败"
                tv_status_color = ContextCompat.getColor(context, R.color.red)
            }
            "PR04" ->
                //待退款
                tv_status = "待退款"
            "PR05" ->
                //已退款
                tv_status = "已退款"
            "PR06" ->
                //已退款
                tv_status = "缴费成功"
            "PR07" ->
                //已退款
                tv_status = "缴费失败"
            "PR08" ->
                //已退款
                tv_status = "未缴费"
            "PR09" ->
                //已退款
                tv_status = "退款失败"
            "PR10" ->
                //已退款
                tv_status = "已成功"
            "PR11" ->
                //已退款
                tv_status = "已失败"
            "PR12" ->
                //已退款
                tv_status = "退款中"
            "PR16" ->
                //已退款
                tv_status = "核签失败"
            "PR17" ->
                //已退款
                tv_status = "实际付款金额与订单金额不符"
            "PR18" ->
                //已冲正
                tv_status = "已冲正"
            "PR99" ->
                //已退款
                tv_status = "异常"
        }

        helper.setVisible(R.id.tv_card_status, View.VISIBLE)
                .setVisible(R.id.tv_card_type, View.VISIBLE)
                .setText(R.id.tv_card_status, tv_card_status)
                .setText(R.id.tv_card_type, tv_card_type)
                .setTextColor(R.id.tv_card_status, tv_card_status_color)
                .setText(R.id.tv_name, tv_name)
                .setImageResource(R.id.iv_record_pic, iv_record_pic)
                .setText(R.id.tv_money, if (item.amountflow == "O") "-￥" + item.amount else "+￥" + item.amount)
                .setText(R.id.tv_time, item.ordersettime)
                .setTextColor(R.id.tv_status, tv_status_color)
                .setText(R.id.tv_status, tv_status)
                .setText(R.id.tv_record_income, "收入：￥" + item.icome)
                .setText(R.id.tv_record_pay_out, "支出：￥" + item.pay)
                .setText(R.id.tv_record_month, formatMonth(item))

    }

    private fun formatMonth(record: Record): String {
        return record.year.toString() + "年  " + record.month.toString() + "月"
    }

}
