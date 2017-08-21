package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.BaseAdapter
import com.mqt.ganghuazhifu.adapter.BaseViewHolder
import com.mqt.ganghuazhifu.bean.GasBillRecord
import com.mqt.ganghuazhifu.bean.GasBillResult
import com.mqt.ganghuazhifu.databinding.ActivityResultForGasBillDetailBinding
import org.parceler.Parcels
import java.text.DecimalFormat
import java.util.*

/**
 * 缴纳气费(营业费)账单明细查询结果

 * @author bo.sun
 * *
 * @date 2015-06-03
 */
class ResultForGasBillDetailActivity : BaseActivity() {

    private var gasBillResult: GasBillResult? = null
    private var amount: Float = 0.toFloat()
    private var adapter: GasBillResultAdapter? = null
    private var activityResultForGasBillDetailBinding: ActivityResultForGasBillDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultForGasBillDetailBinding = DataBindingUtil.setContentView<ActivityResultForGasBillDetailBinding>(this, R.layout.activity_result_for_gas_bill_detail)
        gasBillResult = Parcels.unwrap<GasBillResult>(intent.getParcelableExtra<Parcelable>("GasBillResult"))
        amount = 0f
        if (gasBillResult != null && gasBillResult!!.FeeCountDetail != null) {
            gasBillResult!!.FeeCountDetail
                    .filter { it != null && it.PayAmount != null }
                    .forEach { amount += java.lang.Float.parseFloat(it.PayAmount) }
        }
        initView()
        setDatatoView()
    }

    private fun setDatatoView() {
        adapter = GasBillResultAdapter(this)
        activityResultForGasBillDetailBinding!!.listViewGasBill.adapter = adapter
        adapter!!.updateList(gasBillResult!!.FeeCountDetail)
    }

    private fun initView() {
        setSupportActionBar(activityResultForGasBillDetailBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val db = java.lang.Double.valueOf(amount.toDouble())!!.toDouble()
        val df = DecimalFormat("###0.00")
        activityResultForGasBillDetailBinding!!.textViewTotalAmount.text = "合计:￥" + df.format(db)
        activityResultForGasBillDetailBinding!!.listViewGasBill.layoutManager = LinearLayoutManager(this)
        activityResultForGasBillDetailBinding!!.listViewGasBill.setHasFixedSize(true)

        supportActionBar!!.title = gasBillResult!!.FeeCountDetail[0].FeeMonth.substring(0, 4) + "年" + gasBillResult!!.FeeCountDetail[0].FeeMonth.substring(4) + "月" + "电子账单"
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {}

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    private inner class GasBillResultAdapter(private val mContext: Context) : BaseAdapter<GasBillRecord>() {

        init {
            list = ArrayList<GasBillRecord>()
            layoutResId = R.layout.activity_result_for_gas_bill_detail_item
        }

        override fun convert(helper: BaseViewHolder, position: Int, item: GasBillRecord) {
            val month = item.FeeMonth
            val PayAmount = java.lang.Float.valueOf(item.PayAmount)!!
            val TotalAmount = java.lang.Float.valueOf(item.CurrentTotalAmount)!!
            val tv_gas_fee_status: String
            val tv_gas_fee_status_color: Int
            val iv_gas_fee_status_img: Int
            val tv_gas_fee_time: String
            if (TotalAmount > PayAmount || PayAmount <= 0) {
                tv_gas_fee_status = "未缴"
                tv_gas_fee_status_color = ContextCompat.getColor(mContext, R.color.dark_green_slow)
                iv_gas_fee_status_img = R.drawable.bill_left_pic_done_short
            } else {
                tv_gas_fee_status = "已缴"
                tv_gas_fee_status_color = ContextCompat.getColor(mContext, R.color.black)
                iv_gas_fee_status_img = R.drawable.bill_left_pic_undone_short
            }
            if (month != null) {
                tv_gas_fee_time = month.substring(0, 4) + "年" + month.subSequence(4, 6) + "月"
            } else {
                val calendar = Calendar.getInstance(Locale.CHINA)
                val year = calendar.get(Calendar.YEAR)
                val montha = calendar.get(Calendar.MONTH) + 1
                tv_gas_fee_time = year.toString() + "年" + montha + "月"
            }
            val gas_fee_last_reading = if (TextUtils.isEmpty(item.LastReading))
                "上期读数:"
            else
                "上期读数:" + item.LastReading
            val gas_fee_use = if (TextUtils.isEmpty(item.GasNb))
                "使用燃气:"
            else
                "使用燃气:" + item.GasNb
            val gas_fee_current_reading = if (TextUtils.isEmpty(item.CurrentReading))
                "本期读数:"
            else
                "本期读数:" + item.CurrentReading

            val df = DecimalFormat("###0.00")

            helper.setText(R.id.tv_gas_fee_status, tv_gas_fee_status)
                    .setTextColor(R.id.tv_gas_fee_status, tv_gas_fee_status_color)
                    .setImageResource(R.id.iv_gas_fee_status, iv_gas_fee_status_img)
                    .setText(R.id.tv_gas_fee_time, tv_gas_fee_time)
                    .setText(R.id.tv_gas_fee_last_reading, gas_fee_last_reading)
                    .setText(R.id.tv_gas_fee_use, gas_fee_use)
                    .setText(R.id.tv_gas_fee_current_reading, gas_fee_current_reading)
                    .setText(R.id.tv_gas_fee_pay_amount, "气费消费额:￥" + df
                            .format(java.lang.Double.valueOf(item.PayAmount)!!.toDouble()))
                    .setText(R.id.tv_gas_fee_total_amount, "￥" + df.format(java.lang.Double.valueOf(item.CurrentTotalAmount)!!.toDouble()))
                    .setVisible(R.id.title_lay, View.GONE)
        }

        override fun convertHeader(helper: BaseViewHolder, position: Int) {

        }

        override fun convertFooter(helper: BaseViewHolder, position: Int) {

        }
    }
}
