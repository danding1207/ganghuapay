package com.mqt.ganghuazhifu.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.KuaiQian
import com.mqt.ganghuazhifu.bean.LianDong
import com.mqt.ganghuazhifu.bean.Order
import com.mqt.ganghuazhifu.bean.Record
import com.mqt.ganghuazhifu.databinding.ActivityPayBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.mqt.ganghuazhifu.view.PaymentMethodDialog
import com.orhanobut.logger.Logger
import com.switfpass.pay.MainApplication
import com.switfpass.pay.activity.PayPlugin
import com.switfpass.pay.bean.RequestMsg
import org.parceler.Parcels

/**
 * 快钱支付

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
open class PayActivity : BaseActivity() {

    private var position: Int = 0
    private var kuaiQian: KuaiQian? = null
    private var record: Record? = null
    private var order: Order? = null
    // 界面文件命名
    private var activityPayBinding: ActivityPayBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPayBinding = DataBindingUtil.setContentView<ActivityPayBinding>(this,
                R.layout.activity_pay)
        record = Parcels.unwrap<Record>(intent.getParcelableExtra<Parcelable>(RECORD))
        order = Parcels.unwrap<Order>(intent.getParcelableExtra<Parcelable>(ORDER))
        position = intent.getIntExtra("Position", -1)
        if (record != null)
            Logger.e(record!!.toString())
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityPayBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityPayBinding!!.tvExplainerBefore3.visibility = View.VISIBLE
        activityPayBinding!!.tvExplainerBefore3.text = getString(R.string.pay_tip)
        activityPayBinding!!.cardViewGoPayKuaiqian.isClickable = true
        supportActionBar!!.title = "支付订单"
        if (order != null) {
            activityPayBinding!!.tvNameBefore.text = String.format(getString(R.string.pay_product_name), order!!.ProductName)
            activityPayBinding!!.tvOrderNbBefore.text = String.format(getString(R.string.pay_ordernb), order!!.OrderNb)
            activityPayBinding!!.tvOrderSetTimeBefore.text = String.format(getString(R.string.pay_order_set_time), order!!.OrderSetTime)
            activityPayBinding!!.tvAmountBefore.text = String.format(getString(R.string.pay_order_amount), order!!.OrderAmount)
        }
        if (record != null) {
            activityPayBinding!!.tvNameBefore.text = String.format(getString(R.string.pay_product_name), record!!.cdtrnm)
            activityPayBinding!!.tvOrderNbBefore.text = String.format(getString(R.string.pay_ordernb), record!!.ordernb)
            activityPayBinding!!.tvOrderSetTimeBefore.text = String.format(getString(R.string.pay_order_set_time), record!!.orderdate)
            activityPayBinding!!.tvAmountBefore.text = String.format(getString(R.string.pay_order_amount), record!!.amount)
        }
        activityPayBinding!!.llWeixin.setOnClickListener { view ->
            activityPayBinding!!.checkBoxWeixin.isChecked = true
            activityPayBinding!!.checkBoxYinhang.isChecked = false
        }
        activityPayBinding!!.llYinhang.setOnClickListener { view ->
            activityPayBinding!!.checkBoxWeixin.isChecked = false
            activityPayBinding!!.checkBoxYinhang.isChecked = true
        }

        activityPayBinding!!.tvChangePaymentMethod.setOnClickListener { view ->
            MaterialDialog.Builder(this@PayActivity)
                    .title("提示")
                    .content("更换支付方式会重新下单且取消原订单，请确定订单是否已支付（网络原因可能未显示已支付，请刷新订单列表）")
                    .onPositive { dialog, which ->
                        PaymentMethodDialog(this, PaymentMethodDialog.OnPaymentMethodSelectedListener {
                            position ->
                            run {
                                when (position) {
                                    0 -> {

                                    }
                                    1 -> {

                                    }
                                }
                            }
                        }).showDialog()
                    }
                    .negativeText("取消")
                    .positiveText("确定")
                    .show()
        }


        when (EncryptedPreferencesUtils.getUser().ComVal) {
            "0" -> {
                activityPayBinding!!.llPayWays.visibility = View.INVISIBLE
                activityPayBinding!!.cardViewGoPayKuaiqian.setOnClickListener { v ->
                    dialog()
                }
            }
            "1" -> {
                activityPayBinding!!.llPayWays.visibility = View.VISIBLE
                activityPayBinding!!.cardViewGoPayKuaiqian.setOnClickListener { v ->
                    if (activityPayBinding!!.checkBoxWeixin.isChecked) {
                        dialog(1)
                    } else if (activityPayBinding!!.checkBoxYinhang.isChecked) {
                        dialog(2)
                    } else {
                        ToastUtil.toastWarning("请选择支付方式！")
                    }
                }
            }
            else -> {
                activityPayBinding!!.llPayWays.visibility = View.INVISIBLE
                activityPayBinding!!.cardViewGoPayKuaiqian.setOnClickListener { v ->
                    dialog()
                }
            }
        }

    }

    private fun showDialog() {
        MaterialDialog.Builder(this@PayActivity)
                .title("提示")
                .content("支付未完成，确定要退出？")
                .onPositive { dialog, which -> finish() }
                .negativeText("取消")
                .positiveText("确定")
                .show()
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        dismissRoundProcessDialog()
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {

    }

    /**
     * 但是提示框
     */
    @SuppressLint("NewApi")
    private fun dialog(type: Int) {
        var content: String? = null
        when (type) {
            1 -> content = "请注意，您将进入微信支付页面"
            2 -> content = "请注意，您将进入银行卡支付页面"
        }
        MaterialDialog.Builder(this@PayActivity)
                .title("提示")
                .content(content!!)
                .onPositive { dialog, which ->
                    var Ordernb: String = ""
                    if (order != null)
                        Ordernb = order!!.OrderNb
                    else if (record != null)
                        Ordernb = record!!.ordernb
                    when (type) {
                        1 -> getWXPayData(Ordernb)
                        2 -> getLDPayData(Ordernb)
                    }
                }
                .negativeText("取消")
                .positiveText("支付")
                .show()
    }

    /**
     * 但是提示框
     */
    @SuppressLint("NewApi")
    private fun dialog() {
        MaterialDialog.Builder(this@PayActivity)
                .title("提示")
                .content("请注意，您将进入支付页面")
                .onPositive { dialog, which ->
                    var Ordernb: String = ""
                    if (order != null)
                        Ordernb = order!!.OrderNb
                    else if (record != null)
                        Ordernb = record!!.ordernb
                    getKQPayData(Ordernb)
                }
                .negativeText("取消")
                .positiveText("支付")
                .show()
    }

    protected fun getKQPayData(orderNb: String) {
        val body = HttpRequestParams.getParamsForGetPayData(orderNb, "01", HttpURLS.BgPayUrl)
        post(HttpURLS.getPayData, true, "GetPayData", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getString("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    kuaiQian = JSONObject.parseObject(ResponseFields, KuaiQian::class.java)
                    Logger.d(kuaiQian!!.toString())
                    KuaiQianPayActivity.startActivity(this@PayActivity, kuaiQian)
                } else {
                    ToastUtil.toastError(ProcessDes)
                    if (ProcessDes.startsWith("由于每个")) {
                        activityPayBinding!!.cardViewGoPayKuaiqian.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                        activityPayBinding!!.tvGoPayKuaiqian.setTextColor(ContextCompat.getColor(this@PayActivity, R.color.dark_gray))
                        activityPayBinding!!.cardViewGoPayKuaiqian.isClickable = false
                    }
                }
            }
        })
    }

    protected fun getWXPayData(ordernb: String) {
        val body = HttpRequestParams.getParamsForGetPayData(ordernb)
        post(HttpURLS.getPayDataForWFT, true, "GetPayDataForWFT", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getJSONObject("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    val TokenId = ResponseFields.getString("TokenId") // 授权码
                    val OrderAmount = ResponseFields.getString("OrderAmount") // 订单金额
                    val AppId = ResponseFields.getString("AppId") // Appid
                    val PayeeCode = ResponseFields.getString("PayeeCode") // 收款单位编号

                    val msg = RequestMsg()
                    msg.tokenId = TokenId //token_id为服务端预下单返回
                    msg.tradeType = MainApplication.WX_APP_TYPE //app支付类型
                    msg.appId = AppId//appid为商户自己在微信开放平台的应用appid
                    PayPlugin.unifiedAppPay(this@PayActivity, msg)
                    EncryptedPreferencesUtils.setWXPayId(ordernb)

                } else {
                    ToastUtil.toastError(ProcessDes)
                    if (ProcessDes.startsWith("由于每个")) {
                        activityPayBinding!!.cardViewGoPayKuaiqian.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                        activityPayBinding!!.tvGoPayKuaiqian.setTextColor(ContextCompat.getColor(this@PayActivity, R.color.dark_gray))
                        activityPayBinding!!.cardViewGoPayKuaiqian.isClickable = false
                    }
                }
            }
        })
    }

    protected fun getLDPayData(ordernb: String) {
        val body = HttpRequestParams.getParamsForGetPayData(ordernb, "", "", "", "", "", "", "",
                "", "", "", "", "10")
        post(HttpURLS.getPayDataForLDYS, true, "GetPayDataForLDYS", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getString("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    val lianDong = JSONObject.parseObject(ResponseFields, LianDong::class.java)
                    Logger.i(lianDong.toString())
                    val intent: Intent? = null
                    if ("11" == lianDong.flag) {
                        UnityQuickPayActivity.startActivity(this@PayActivity, lianDong)
                    } else {
                        UnitySelectBanksActivity.startActivity(this@PayActivity, lianDong)
                    }
                } else {
                    ToastUtil.toastError(ProcessDes)
                    if (ProcessDes.startsWith("由于每个")) {
                        activityPayBinding!!.cardViewGoPayKuaiqian.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                        activityPayBinding!!.tvGoPayKuaiqian.setTextColor(ContextCompat.getColor(this@PayActivity, R.color.dark_gray))
                        activityPayBinding!!.cardViewGoPayKuaiqian.isClickable = false
                    }
                }
            }
        })
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //主界面左上角的icon点击反应
                showDialog()
                return true
            }
        }
        return true
    }

    companion object {
        val RECORD = "Record"
        val ORDER = "Order"
    }

}
