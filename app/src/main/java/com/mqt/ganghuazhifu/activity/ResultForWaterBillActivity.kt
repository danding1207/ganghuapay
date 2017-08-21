package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.BaseAdapter
import com.mqt.ganghuazhifu.adapter.BaseViewHolder
import com.mqt.ganghuazhifu.bean.WaterBillRecord
import com.mqt.ganghuazhifu.bean.WaterBillResult
import com.mqt.ganghuazhifu.databinding.ActivityResultForWaterBillBinding
import org.parceler.Parcels
import java.util.*

/**
 * 水费账单查询结果页面

 * @author sun.bo
 * *
 * @date 2015-10-22
 */
class ResultForWaterBillActivity : BaseActivity() {

    private var waterBillResult: WaterBillResult? = null
    private var adapter: WaterBillAdapter? = null
    private var activityResultForWaterBillBinding: ActivityResultForWaterBillBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultForWaterBillBinding = DataBindingUtil.setContentView<ActivityResultForWaterBillBinding>(this, R.layout.activity_result_for_water_bill)
        waterBillResult = Parcels.unwrap<WaterBillResult>(intent.getParcelableExtra<Parcelable>("WaterBillResult"))
        initView()
        if (waterBillResult != null) {
            setDatatoView()
        }
    }

    private fun setDatatoView() {
        val name: String = waterBillResult!!.UserName
        val nameBuilder: StringBuilder
        nameBuilder = StringBuilder(name)
        if (name.length <= 1) {
        } else if (name.length <= 3) {
            nameBuilder.setCharAt(0, '*')
        } else {
            for (i in 0..name.length - 2 - 1) {
                nameBuilder.setCharAt(i, '*')
            }
        }
        activityResultForWaterBillBinding!!.waterBillUserName.text = nameBuilder.toString()
        activityResultForWaterBillBinding!!.waterBillAdvPayBalance.text = "￥" + waterBillResult!!.AdvPay_Balance
        activityResultForWaterBillBinding!!.waterBillUserNumber.text = waterBillResult!!.UserNb
        activityResultForWaterBillBinding!!.waterBillMETERBOOK.text = waterBillResult!!.METER_BOOK
        activityResultForWaterBillBinding!!.waterBillBELONGStation.text = waterBillResult!!.BELONG_Station
        activityResultForWaterBillBinding!!.waterAccountAddress.text = waterBillResult!!.UserAddr

        adapter = WaterBillAdapter(this)
        activityResultForWaterBillBinding!!.listViewWaterBill.adapter = adapter
        adapter!!.updateList(waterBillResult!!.WaterBillFeeCountDetail as ArrayList<WaterBillRecord>)
    }

    private fun initView() {
        setSupportActionBar(activityResultForWaterBillBinding!!.toolbar)
        supportActionBar!!.title = "水费账单"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    private inner class WaterBillAdapter(private val mContext: Context) : BaseAdapter<WaterBillRecord>() {

        init {
            list = ArrayList<WaterBillRecord>()
            layoutResId = R.layout.activity_result_for_water_bill_item
        }

        override fun convert(helper: BaseViewHolder, position: Int, item: WaterBillRecord) {
            val month = item.FeeMonth
            val tv_gas_fee_time: String
            if (month != null) {
                tv_gas_fee_time = month.substring(0, 4) + "年" + month.subSequence(4, 6) + "月"
            } else {
                val calendar = Calendar.getInstance(Locale.CHINA)
                val year = calendar.get(Calendar.YEAR)
                val montha = calendar.get(Calendar.MONTH) + 1
                tv_gas_fee_time = year.toString() + "年" + montha + "月"
            }
            val water_bill_Index_Now = if (item.Index_Now == null) "本期示度：0" else "本期示度：" + item.Index_Now
            val water_bill_WATER_NUM = if (item.WATER_NUM == null) "水\t\t\t量：0.00" else "水\t\t\t量：" + item.WATER_NUM
            val water_bill_AMOUNT = if (item.AMOUNT == null) "账单金额：￥0.00" else "账单金额：￥" + item.AMOUNT
            val water_bill_AMOUNT_YSF = if (item.AMOUNT_YSF == null) "用\t水\t费：￥0.00" else "用\t水\t费：￥" + item.AMOUNT_YSF
            val water_bill_AMOUNT_DZF = if (item.AMOUNT_DZF == null) "代征费用：￥0.00" else "代征费用：￥" + item.AMOUNT_DZF
            val water_bill_AMOUNT_WYJ = if (item.AMOUNT_WYJ == null) "违\t约\t金：￥0.00" else "违\t约\t金：￥" + item.AMOUNT_WYJ
            val water_bill_DATE_Pay = if (item.DATE_Pay == null) "缴费日期：" else "缴费日期：" + item.DATE_Pay
            val water_bill_AMOUNT_Pay = if (item.AMOUNT_Pay == null) "实缴金额：￥" else "实缴金额：￥" + item.AMOUNT_Pay
            val water_bill_AMOUNT_SQY = if (item.AMOUNT_SQY == null) "上期余额：￥0.00" else "上期余额：￥" + item.AMOUNT_SQY
            val water_bill_AMOUNT_YE = if (item.AMOUNT_YE == null) "本期余额：￥0.00" else "本期余额：￥" + item.AMOUNT_YE
            helper
                    .setText(R.id.water_bill_FeeMonth, tv_gas_fee_time)
                    .setText(R.id.water_bill_Index_Now, water_bill_Index_Now)
                    .setText(R.id.water_bill_WATER_NUM, water_bill_WATER_NUM)
                    .setText(R.id.water_bill_AMOUNT, water_bill_AMOUNT)
                    .setText(R.id.water_bill_AMOUNT_YSF, water_bill_AMOUNT_YSF)
                    .setText(R.id.water_bill_AMOUNT_DZF, water_bill_AMOUNT_DZF)
                    .setText(R.id.water_bill_AMOUNT_WYJ, water_bill_AMOUNT_WYJ)
                    .setText(R.id.water_bill_DATE_Pay, water_bill_DATE_Pay)
                    .setText(R.id.water_bill_AMOUNT_Pay, water_bill_AMOUNT_Pay)
                    .setText(R.id.water_bill_AMOUNT_SQY, water_bill_AMOUNT_SQY)
                    .setText(R.id.water_bill_AMOUNT_YE, water_bill_AMOUNT_YE)
        }

        override fun convertHeader(helper: BaseViewHolder, position: Int) {

        }

        override fun convertFooter(helper: BaseViewHolder, position: Int) {

        }
    }

    override fun OnViewClick(v: View) {}

}
