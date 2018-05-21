package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.BaseAdapter
import com.mqt.ganghuazhifu.adapter.BaseViewHolder
import com.mqt.ganghuazhifu.bean.BusiFeeRecord
import com.mqt.ganghuazhifu.bean.BusiFeeResult
import com.mqt.ganghuazhifu.bean.GasFeeRecord
import com.mqt.ganghuazhifu.bean.GasFeeResult
import com.mqt.ganghuazhifu.databinding.ActivityResultForGasFeeDetailsBinding
import com.mqt.ganghuazhifu.event.ConstantKotlin
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.util.*

/**
 * 缴纳气费(营业费)欠费查询结果明细

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class ResultForGasFeeDetailsActivity : BaseActivity() {

    private var gasFeeResult: GasFeeResult? = null
    private var busiFeeResult: BusiFeeResult? = null
    private var gasFeeResultAdapter: GasFeeResultAdapter? = null
    private var nanJingGasFeeResultAdapter: NanJingGasFeeResultAdapter? = null
    private var baoHuaGasFeeResultAdapter: BaoHuaGasFeeResultAdapter? = null
    private var busiFeeResultAdapter: BusiFeeResultAdapter? = null
    private var orderType: ConstantKotlin.OrderType = ConstantKotlin.OrderType.GASFEEARREARS

    private var activityResultForGasFeeDetailsBinding: ActivityResultForGasFeeDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultForGasFeeDetailsBinding = DataBindingUtil.setContentView<ActivityResultForGasFeeDetailsBinding>(this,
                R.layout.activity_result_for_gas_fee_details)

        Logger.e("ConstantKotlin.OrderType.name:  " + intent.getStringExtra("OrderType"))
        orderType = ConstantKotlin.OrderType.valueOf(intent.getStringExtra("OrderType"))

        gasFeeResult = Parcels.unwrap<GasFeeResult>(intent.getParcelableExtra<Parcelable>("GasFeeResult"))
        busiFeeResult = Parcels.unwrap<BusiFeeResult>(intent.getParcelableExtra<Parcelable>("BusiFeeResult"))

        if (gasFeeResult != null) {
            Logger.i("size--->" + if (gasFeeResult!!.FeeCountDetail == null) 0 else gasFeeResult!!.FeeCountDetail.size)
        } else if (busiFeeResult != null) {
            Logger.i("size--->" + if (busiFeeResult!!.BusifeeCountDetail == null) 0 else busiFeeResult!!.BusifeeCountDetail.size)
        }

        initView()
        setDatatoView()
    }

    private fun setDatatoView() {
        when (orderType) {
            ConstantKotlin.OrderType.GASFEEARREARS -> if (gasFeeResult != null) {
                Logger.i("PayeeCode--->" + gasFeeResult!!.PayeeCode)
                when (gasFeeResult!!.PayeeCode) {
                    ConstantKotlin.NanJingCode -> {
                        Logger.i("NanJingGasFeeResultAdapter")
                        nanJingGasFeeResultAdapter = NanJingGasFeeResultAdapter(this)
                        activityResultForGasFeeDetailsBinding!!.listViewGasFeeDetail.adapter = nanJingGasFeeResultAdapter
                        nanJingGasFeeResultAdapter!!.updateList(gasFeeResult!!.FeeCountDetail as ArrayList<GasFeeRecord>)
                    }
                    ConstantKotlin.BaoHuaCode -> {
                        Logger.i("BaoHuaGasFeeResultAdapter")
                        baoHuaGasFeeResultAdapter = BaoHuaGasFeeResultAdapter(this)
                        activityResultForGasFeeDetailsBinding!!.listViewGasFeeDetail.adapter = baoHuaGasFeeResultAdapter
                        baoHuaGasFeeResultAdapter!!.updateList(gasFeeResult!!.FeeCountDetail as ArrayList<GasFeeRecord>)
                    }
                    else -> {
                        Logger.i("GasFeeResultAdapter")
                        gasFeeResultAdapter = GasFeeResultAdapter(this)
                        activityResultForGasFeeDetailsBinding!!.listViewGasFeeDetail.adapter = gasFeeResultAdapter
                        gasFeeResultAdapter!!.updateList(gasFeeResult!!.FeeCountDetail as ArrayList<GasFeeRecord>)
                    }
                }
            }
            ConstantKotlin.OrderType.OPERATINGFEEARREARS -> if (busiFeeResult != null) {
                busiFeeResultAdapter = BusiFeeResultAdapter(this)
                activityResultForGasFeeDetailsBinding!!.listViewGasFeeDetail.adapter = busiFeeResultAdapter
                busiFeeResultAdapter!!.updateList(busiFeeResult!!.BusifeeCountDetail as ArrayList<BusiFeeRecord>)
            }
        }
    }

     fun initView() {
        setSupportActionBar(activityResultForGasFeeDetailsBinding!!.toolbar)
        supportActionBar!!.title = "明细"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityResultForGasFeeDetailsBinding!!.listViewGasFeeDetail.layoutManager = LinearLayoutManager(this)
        activityResultForGasFeeDetailsBinding!!.listViewGasFeeDetail.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    private inner class GasFeeResultAdapter(private val mContext: Context) : BaseAdapter<GasFeeRecord>() {

        init {
            list = ArrayList<GasFeeRecord>()
            layoutResId = R.layout.activity_result_for_gas_fee_item
        }

        override fun convert(helper: BaseViewHolder, position: Int, item: GasFeeRecord) {
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
            helper
                    .setText(R.id.tv_gas_fee_time, tv_gas_fee_time)
                    .setText(R.id.tv_gas_fee_presave, "上期预存:" + gasFeeResult!!.FeeCountDetail[position].UserPresave + "元")
                    .setText(R.id.tv_gas_fee_use, "使用燃气:" + gasFeeResult!!.FeeCountDetail[position].GasNb + "m³燃气")
                    .setText(R.id.tv_gas_fee_money, "气费金额:" + gasFeeResult!!.FeeCountDetail[position].CurrentGasTotalAmount + "元")
                    .setText(R.id.tv_gas_fee_late_amount, "违约金:" + gasFeeResult!!.FeeCountDetail[position].CurrentLateAmount + "元")
                    .setText(R.id.tv_gas_fee_other_amount, "其他费用:" + gasFeeResult!!.FeeCountDetail[position].CurrentOtherAmount + "元")
                    .setText(R.id.tv_gas_fee_total_amount, "本期总额:" + gasFeeResult!!.FeeCountDetail[position].CurrentTotalAmount + "元")
        }

        override fun convertHeader(helper: BaseViewHolder, position: Int) {

        }

        override fun convertFooter(helper: BaseViewHolder, position: Int) {

        }
    }


    private inner class BaoHuaGasFeeResultAdapter(private val mContext: Context) : BaseAdapter<GasFeeRecord>() {

        init {
            list = ArrayList<GasFeeRecord>()
            layoutResId = R.layout.activity_result_for_baohua_gas_fee_item
        }

        override fun convert(helper: BaseViewHolder, position: Int, item: GasFeeRecord) {
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

            val tv_nanjing_gas_fee_time_color = if ("true" == gasFeeResult!!.FeeCountDetail[position].isChecked) ContextCompat.getColor(mContext, R.color.dark_green_slow) else ContextCompat.getColor(mContext, R.color.dark_gray)
            val tv_nanjing_gas_color = if ("true" == gasFeeResult!!.FeeCountDetail[position].isChecked) ContextCompat.getColor(mContext, R.color.black) else ContextCompat.getColor(mContext, R.color.dark_gray)

            val ss = gasFeeResult!!.FeeCountDetail[position].Details
            val sb = StringBuilder()
            if (ss != null) {
                sb.append("<table align=\"center\" class=\"table table-bordered table-striped table-condensed\">")
                for (i in ss.indices) {
                    sb.append("<tr>")
                    Logger.i("MarkedView", ss[i])
                    val ssss = ss[i].split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    Logger.i("MarkedView", ssss.size.toString() + "~~~~~~~~~~~~")
                    for (j in ssss.indices) {
                        Logger.i("MarkedView", "---------------------" + ssss[j])
                        if (ssss.size > 3) {
                            if (i == 0) {
                                if (j == 4) {
                                    sb.append("<th style=\"text-align:center;\" width=\"60\">" + ssss[j] + "</th>")
                                } else {
                                    sb.append("<th style=\"text-align:center;\" width=\"30\">" + ssss[j] + "</th>")
                                }
                            } else {
                                sb.append("<td align=\"center\">" + ssss[j] + "</td>")
                            }
                        } else {
                            if (j == 2) {
                                sb.append("<td colspan=\"2\" align=\"center\">" + ssss[j] + "</td>")
                            } else {
                                sb.append("<td colspan=\"3\" align=\"center\">" + ssss[j] + "</td>")
                            }
                        }
                    }
                    sb.append("</tr>")
                }
                sb.append("</table>")
            }
            helper
                    .setText(R.id.tv_nanjing_gas_fee_time, tv_gas_fee_time)
                    .setText(R.id.tv_nanjing_gas, "气量:" + gasFeeResult!!.FeeCountDetail[position].GasNb + "m³")
                    .setText(R.id.tv_nanjing_gas_fee_use, "气费金额:" + gasFeeResult!!.FeeCountDetail[position].CurrentGasTotalAmount + "元")
                    .setText(R.id.tv_nanjing_gas_fee_late, "违约金:" + gasFeeResult!!.FeeCountDetail[position].CurrentLateAmount + "元")
                    .setText(R.id.tv_nanjing_gas_fee_total_amount, "本期总额:" + gasFeeResult!!.FeeCountDetail[position].CurrentTotalAmount + "元")
                    .setTextColor(R.id.tv_nanjing_gas_fee_time, tv_nanjing_gas_fee_time_color)
                    .setTextColor(R.id.tv_nanjing_gas, tv_nanjing_gas_color)
                    .setTextColor(R.id.tv_nanjing_gas_fee_use, tv_nanjing_gas_color)
                    .setTextColor(R.id.tv_nanjing_gas_fee_late, tv_nanjing_gas_color)
                    .setTextColor(R.id.tv_nanjing_gas_fee_total_amount, tv_nanjing_gas_color)
                    .setMDText(R.id.md_view, sb.toString())
        }

        override fun convertHeader(helper: BaseViewHolder, position: Int) {

        }

        override fun convertFooter(helper: BaseViewHolder, position: Int) {

        }
    }

    private inner class NanJingGasFeeResultAdapter(private val mContext: Context) : BaseAdapter<GasFeeRecord>() {

        init {
            list = ArrayList<GasFeeRecord>()
            layoutResId = R.layout.activity_result_for_nanjing_gas_fee_item
        }

        override fun convert(helper: BaseViewHolder, position: Int, item: GasFeeRecord) {
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
            helper
                    .setText(R.id.tv_nanjing_gas_fee_time, tv_gas_fee_time)
                    .setText(R.id.tv_nanjing_gas, "气量:" + gasFeeResult!!.FeeCountDetail[position].GasNb + "m³")
                    .setText(R.id.tv_nanjing_gas_fee_use, "气费金额:" + gasFeeResult!!.FeeCountDetail[position].CurrentGasTotalAmount + "元")
                    .setText(R.id.tv_nanjing_gas_fee_late, "违约金:" + gasFeeResult!!.FeeCountDetail[position].CurrentLateAmount + "元")
                    .setText(R.id.tv_nanjing_gas_fee_total_amount, "本期总额:" + gasFeeResult!!.FeeCountDetail[position].CurrentTotalAmount + "元")
        }

        override fun convertHeader(helper: BaseViewHolder, position: Int) {

        }

        override fun convertFooter(helper: BaseViewHolder, position: Int) {

        }
    }

    private inner class BusiFeeResultAdapter(private val mContext: Context) : BaseAdapter<BusiFeeRecord>() {

        init {
            list = ArrayList<BusiFeeRecord>()
            layoutResId = R.layout.activity_result_for_busi_fee_item
        }

        override fun convert(helper: BaseViewHolder, position: Int, item: BusiFeeRecord) {
            helper
                    .setText(R.id.tv_busi_fee_type, item.BusiType + ":")
                    .setText(R.id.tv_busi_fee_amount, item.BusiAmount + "元")
        }

        override fun convertHeader(helper: BaseViewHolder, position: Int) {

        }

        override fun convertFooter(helper: BaseViewHolder, position: Int) {

        }
    }

}
