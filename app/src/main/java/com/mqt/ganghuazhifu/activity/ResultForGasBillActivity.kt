package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.BaseAdapter
import com.mqt.ganghuazhifu.adapter.BaseViewHolder
import com.mqt.ganghuazhifu.bean.BusiBillRecord
import com.mqt.ganghuazhifu.bean.BusiBillResult
import com.mqt.ganghuazhifu.bean.GasBillRecord
import com.mqt.ganghuazhifu.bean.GasBillResult
import com.mqt.ganghuazhifu.databinding.ActivityResultForGasBillBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.text.DecimalFormat
import java.util.*

/**
 * 缴纳气费(营业费)账单查询结果

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class ResultForGasBillActivity : BaseActivity(), OnRecyclerViewItemClickListener {

    private var gasBillResult: GasBillResult? = null
    private var busiBillResult: BusiBillResult? = null
    private var gasBillAdapter: GasBillResultAdapter? = null
    private var busiBillAdapter: BusiBillResultAdapter? = null
    private var type: Int = 0// 1:气费账单信息;2:营业费账单信息;
    private var activityResultForGasBillBinding: ActivityResultForGasBillBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultForGasBillBinding = DataBindingUtil.setContentView<ActivityResultForGasBillBinding>(this, R.layout.activity_result_for_gas_bill)
        type = intent.getIntExtra("TYPE", 1)
        gasBillResult = Parcels.unwrap<GasBillResult>(intent.getParcelableExtra<Parcelable>("GasBillResult"))
        busiBillResult = Parcels.unwrap<BusiBillResult>(intent.getParcelableExtra<Parcelable>("BusiBillResult"))
        initView()
        setDatatoView()
    }

    private fun setDatatoView() {
        when (type) {
            1 -> if (gasBillResult != null) {
                gasBillAdapter = GasBillResultAdapter(this)
                gasBillAdapter!!.onRecyclerViewItemClickListener = this
                activityResultForGasBillBinding!!.listViewGasBill.adapter = gasBillAdapter
                gasBillAdapter!!.updateList(gasBillResult!!.FeeCountDetail as ArrayList<GasBillRecord>)
            }
            2 -> if (busiBillResult != null) {
                busiBillAdapter = BusiBillResultAdapter(this)
                activityResultForGasBillBinding!!.listViewGasBill.adapter = busiBillAdapter
                busiBillAdapter!!.updateList(busiBillResult!!.BusifeeCountDetail as ArrayList<BusiBillRecord>)
            }
        }
    }

    private fun initView() {
        setSupportActionBar(activityResultForGasBillBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activityResultForGasBillBinding!!.listViewGasBill.layoutManager = LinearLayoutManager(this)
        activityResultForGasBillBinding!!.listViewGasBill.setHasFixedSize(true)
        when (type) {
            1 -> supportActionBar!!.title = "气费账单查询"
            2 -> supportActionBar!!.title = "营业费账单查询"
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {}

    override fun onItemClick(view: View, position: Int) {
//        serach3(gasBillResult!!.FeeCountDetail[position].FeeMonth)
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    private inner class GasBillResultAdapter(private val mContext: Context) : BaseAdapter<GasBillRecord>() {

        init {
            list = ArrayList<GasBillRecord>()
            layoutResId = R.layout.activity_result_for_gas_bill_item
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
                    .setText(R.id.tv_gas_fee_pay_amount, "本期气费应收总额:￥" + df
                            .format(java.lang.Double.valueOf(item.CurrentTotalAmount)!!.toDouble()))
                    .setText(R.id.tv_gas_fee_total_amount, "￥" + df.format(java.lang.Double.valueOf(item.PayAmount)!!.toDouble()))
                    .setText(R.id.bill_name, "已缴金额:")
                    .setText(R.id.tv_gas_fee_payed_amount, df.format(java.lang.Double.valueOf(item.PayAmount)!!.toDouble()))
                    .setVisible(R.id.tv_gas_fee_price, View.GONE)
                    .setVisible(R.id.gas_lay, View.GONE)
                    .setVisible(R.id.tv_gas_fee_pay_amount_lay, View.VISIBLE)
                    .setVisible(R.id.dzzd_text, View.VISIBLE)
        }

        override fun convertHeader(helper: BaseViewHolder, position: Int) {

        }

        override fun convertFooter(helper: BaseViewHolder, position: Int) {

        }
    }

    private inner class BusiBillResultAdapter(private val mContext: Context) : BaseAdapter<BusiBillRecord>() {

        init {
            list = ArrayList<BusiBillRecord>()
            layoutResId = R.layout.activity_result_for_busi_bill_item
        }

        override fun convert(helper: BaseViewHolder, position: Int, item: BusiBillRecord) {
            val month = item.BusiDate
            val PayAmount = java.lang.Float.valueOf(item.BusiAlreadyPayAmount)!!
            val TotalAmount = java.lang.Float.valueOf(item.BusiAmount)!!
            val tv_gas_fee_status: String
            val tv_gas_fee_status_color: Int
            val iv_gas_fee_status_img: Int
            val tv_gas_fee_time: String
            if (month != null) {
                tv_gas_fee_time = month.substring(0, 4) + "年" + month.subSequence(4, 6) + "月"
            } else {
                val calendar = Calendar.getInstance(Locale.CHINA)
                val year = calendar.get(Calendar.YEAR)
                val montha = calendar.get(Calendar.MONTH) + 1
                tv_gas_fee_time = year.toString() + "年" + montha + "月"
            }
            if (TotalAmount > PayAmount || PayAmount <= 0) {
                // 未支付
                tv_gas_fee_status = "未缴费"
                tv_gas_fee_status_color = ContextCompat.getColor(mContext, R.color.dark_green_slow)
                iv_gas_fee_status_img = R.drawable.bill_left_pic_done_short
            } else {
                tv_gas_fee_status = "已缴费"
                tv_gas_fee_status_color = ContextCompat.getColor(mContext, R.color.black)
                iv_gas_fee_status_img = R.drawable.bill_left_pic_undone_short
            }
            val fee_all_amount = if (TextUtils
                    .isEmpty(item.BusiAlreadyPayAmount))
                "￥"
            else
                "￥" + item.BusiAlreadyPayAmount
            val fee_amount = if (TextUtils.isEmpty(item.BusiAmount))
                "欠费金额：￥"
            else
                "欠费金额：￥" + item.BusiAmount

            helper.setText(R.id.tv_gas_fee_status, tv_gas_fee_status)
                    .setTextColor(R.id.tv_gas_fee_status, tv_gas_fee_status_color)
                    .setImageResource(R.id.iv_gas_fee_status, iv_gas_fee_status_img)
                    .setText(R.id.tv_gas_fee_time, tv_gas_fee_time)
                    .setText(R.id.tv_busi_fee_type, item.BusiType)
                    .setText(R.id.tv_gas_fee_all_amount, fee_amount)
                    .setText(R.id.tv_busi_fee_amount, fee_all_amount)
        }

        override fun convertHeader(helper: BaseViewHolder, position: Int) {

        }

        override fun convertFooter(helper: BaseViewHolder, position: Int) {

        }
    }


    /**
     * 查询选择的月份，电子账单详情，阶梯气费
     */
//    private fun serach3(time: String) {
//        val StartDate = time
//        val EndDate = time
//        val body = HttpRequestParams.getParamsForGasBill(gasBillResult!!.ProvinceCode, gasBillResult!!.CityCode,
//                gasBillResult!!.UserNb, "1", gasBillResult!!.PayeeCode, StartDate, EndDate)
//        post(HttpURLS.gasArrearsZhangDanQuery, true, "GasBill", body, OnHttpRequestListener { isError, response, type, error ->
//            if (isError) {
//                Logger.e(error.toString())
//            } else {
//                Logger.d(response.toString())
//
//                val ResponseHead = response.getJSONObject("ResponseHead")
//                val ResponseFields = response.getJSONObject("ResponseFields")
//                val ProcessCode = ResponseHead.getString("ProcessCode")
//                val ProcessDes = ResponseHead.getString("ProcessDes")
//                if (ProcessCode == "0000") {
//                    val result = GasBillResult()
//                    val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
//                    result.EasyNo = ResponseFields.getString("EasyNo")
//                    result.UserAddr = ResponseFields.getString("UserAddr")
//                    result.UserNb = ResponseFields.getString("UserNb")
//                    result.UserName = ResponseFields.getString("UserName")
//                    result.FeeCount = ResponseFields.getString("FeeCount")
//                    result.HasBusifee = ResponseFields.getString("HasBusifee")
//                    if (null != ResponseFields.getString("GasPrice")) {
//                        result.HasBusifee = ResponseFields.getString("GasPrice")
//                    } else {
//                        result.HasBusifee = ""
//                    }
//                    var lists = ArrayList<GasBillRecord>()
//                    if (FeeCountDetail == null) {
//                        ToastUtil.toastInfo("没有查到气费账单记录!")
//                    } else if (FeeCountDetail.startsWith("{")) {
//                        lists.add(JSONObject.parseObject(FeeCountDetail, GasBillRecord::class.java))
//                        result.FeeCountDetail = lists
//                        Logger.d(result.toString())
//                        val intent = Intent(this@ResultForGasBillActivity,
//                                ResultForGasBillDetailActivity::class.java)
//                        intent.putExtra("TYPE", 1)
//                        intent.putExtra("GasBillResult", Parcels.wrap(result))
//                        startActivity(intent)
//                    } else if (FeeCountDetail.startsWith("[")) {
//                        lists = JSONObject.parseArray(FeeCountDetail, GasBillRecord::class.java) as ArrayList<GasBillRecord>
//                        result.FeeCountDetail = lists
//                        Logger.d(result.toString())
//                        val intent = Intent(this@ResultForGasBillActivity,
//                                ResultForGasBillDetailActivity::class.java)
//                        intent.putExtra("TYPE", 1)
//                        intent.putExtra("GasBillResult", Parcels.wrap(result))
//                        startActivity(intent)
//                    }
//                } else {
//                    ToastUtil.toastError(ProcessDes)
//                }
//            }
//        })
//    }
}
