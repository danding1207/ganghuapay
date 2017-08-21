package com.mqt.ganghuazhifu.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.LianDong
import com.mqt.ganghuazhifu.databinding.ActivityUnityQuickPayBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.BanksUtils
import com.mqt.ganghuazhifu.utils.PreciseCompute
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.io.UnsupportedEncodingException

/**
 * 联动优势支付——一键支付

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UnityQuickPayActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }


    private var requestCode: Int = 13
    private var yzm: String? = null
    private var lianDong: LianDong? = null
    private var activityUnityQuickPayBinding: ActivityUnityQuickPayBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUnityQuickPayBinding = DataBindingUtil.setContentView<ActivityUnityQuickPayBinding>(this, R.layout.activity_unity_quick_pay)
        lianDong = Parcels.unwrap<LianDong>(intent.getParcelableExtra<Parcelable>("LianDong"))
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityUnityQuickPayBinding!!.toolbar)
        supportActionBar!!.title = "支付"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activityUnityQuickPayBinding!!.etPhoneYanzhengma.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 6 && yzm != null) {
                    activityUnityQuickPayBinding!!.tvSubmit.isEnabled = true
                    activityUnityQuickPayBinding!!.tvSubmit.setBackgroundResource(R.drawable.unity_blue_button)
                } else {
                    activityUnityQuickPayBinding!!.tvSubmit.isEnabled = false
                    activityUnityQuickPayBinding!!.tvSubmit.setBackgroundResource(R.drawable.unity_gray_button)
                }
            }
        })
        activityUnityQuickPayBinding!!.tvSubmit.setOnClickListener(this)
        activityUnityQuickPayBinding!!.validateButtonUnityPay.setOnClickListener(this)
        activityUnityQuickPayBinding!!.ivBankSetting.setOnClickListener(this)
        activityUnityQuickPayBinding!!.llJine.setOnClickListener(this)

        if (lianDong != null) {
            if (!TextUtils.isEmpty(lianDong!!.mediaid)) {
                activityUnityQuickPayBinding!!.tvPhoneNum.text = lianDong!!.mediaid
            }
            if (!TextUtils.isEmpty(lianDong!!.gateid)) {
                val ss = if ("CREDITCARD" == lianDong!!.paytype) "信用卡" else "借记卡"
                activityUnityQuickPayBinding!!.tvBankNum.text = BanksUtils.getBanksUtils().BANKS[lianDong!!.gateid]!!.name + ss + "(*" + lianDong!!.cardid + ")"
                activityUnityQuickPayBinding!!.ivBankIcon.setImageResource(BanksUtils.getBanksUtils().getBankIconResId(lianDong!!.gateid))
            }
            activityUnityQuickPayBinding!!.tvAmonut.text = "￥ " + PreciseCompute.div(lianDong!!.amount, 1.0, 2)
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
//            R.id.ll_jine -> UnityOrderDetailActivity.startActivity(this, lianDong!!)
            R.id.iv_bank_setting -> {
                UnityChangeBanksActivity.startActivityForResult(this, lianDong, requestCode)
            }
            R.id.validateButton_unity_pay -> try {
                getPayData(if (TextUtils.isEmpty(lianDong!!.orderid)) "" else lianDong!!.orderid,
                        if (TextUtils.isEmpty(lianDong!!.paytype)) "" else lianDong!!.paytype,
                        if (TextUtils.isEmpty(lianDong!!.gateid)) "" else lianDong!!.gateid)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            R.id.tv_submit -> pay(lianDong!!.orderid, lianDong!!.paytype, lianDong!!.gateid, activityUnityQuickPayBinding!!.etPhoneYanzhengma.text.toString().trim { it <= ' ' })
//            else -> { }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == this.requestCode && resultCode == Activity.RESULT_OK && data!=null) {
            lianDong = Parcels.unwrap<LianDong>(data.getParcelableExtra<Parcelable>("LianDong"))
            initView()
        }
    }

    @Throws(UnsupportedEncodingException::class)
    protected fun getPayData(ordernb: String, pay_type: String, gate_id: String) {

        val body = HttpRequestParams.getParamsForGetPayData(ordernb, pay_type, gate_id, "", lianDong!!.cardid, "",
                "", "", "", "", "", if (lianDong!!.tradeno == null) "" else lianDong!!.tradeno, "12")

        Logger.e(body.toString())
        post(HttpURLS.getPayDataForLDYS, true, "GetPayData", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getString("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")

                if (ProcessCode == "0000") {
                    val lianDong1 = JSONObject.parseObject(ResponseFields, LianDong::class.java)
                    this@UnityQuickPayActivity.lianDong!!.tradeno = lianDong1.tradeno
                    activityUnityQuickPayBinding!!.validateButtonUnityPay.startTimer()
                    yzm = "123456"
                } else {
                    ToastUtil.Companion.toastError(ProcessDes)
                }
            }
        })
    }

    protected fun pay(ordernb: String, pay_type: String, gate_id: String, verifycode: String) {

        val body = HttpRequestParams.getParamsForconfirmPayForLDYS(ordernb, pay_type, gate_id, verifycode, "",
                lianDong!!.cardid, if (lianDong!!.tradeno == null) "" else lianDong!!.tradeno, "", "", "", "")

        Logger.e(body.toString())
        post(HttpURLS.confirmPayForLDYS, true, "confirmPayForLDYS", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getString("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
//                    UnityPayResultActivity.startActivity(this@UnityQuickPayActivity, lianDong!!.orderid)
                } else {
//                    ToastUtil.Companion.toastError(ProcessDes)
                }
                UnityPayResultActivity.startActivity(this@UnityQuickPayActivity, lianDong!!.orderid)
            }
        })
    }

    companion object {

        fun startActivity(context: Context, lianDong: LianDong) {
            val intent = Intent(context, UnityQuickPayActivity::class.java)
            intent.putExtra("LianDong", Parcels.wrap(lianDong))
            context.startActivity(intent)
        }
    }

}
