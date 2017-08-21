package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.LianDong
import com.mqt.ganghuazhifu.databinding.ActivityUnityNormalPayBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.*
import com.mqt.ganghuazhifu.view.PasswordsKeyBoardDialog
import com.mqt.ganghuazhifu.view.PasswordsKeyBoardDialog.OnPasswordsCompletedListener
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.io.UnsupportedEncodingException

/**
 * 联动优势支付——输入信息

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UnityNormalPayActivity : BaseActivity(), OnPasswordsCompletedListener {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var yzm: String? = null
    private var lianDong: LianDong? = null
    private var idCard: IDCard? = null
    private var keyBoardDialog: PasswordsKeyBoardDialog? = null
    private var activityUnityNormalPayBinding: ActivityUnityNormalPayBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUnityNormalPayBinding = DataBindingUtil.setContentView<ActivityUnityNormalPayBinding>(this, R.layout.activity_unity_normal_pay)
        lianDong = Parcels.unwrap<LianDong>(intent.getParcelableExtra<Parcelable>("LianDong"))
        idCard = IDCard()
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityUnityNormalPayBinding!!.toolbar)
        supportActionBar!!.title = "卡信息"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        keyBoardDialog = PasswordsKeyBoardDialog(this, this)

        activityUnityNormalPayBinding!!.ivBankIcon.setImageResource(BanksUtils.getBanksUtils().getBankIconResId(lianDong!!.gateid))

        activityUnityNormalPayBinding!!.etPhoneYanzhengma.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                activityUnityNormalPayBinding!!.tvSubmit.isEnabled = s.length == 6 && yzm != null && activityUnityNormalPayBinding!!.checkBoxPaymentAgreement.isChecked
            }
        })

        activityUnityNormalPayBinding!!.checkBoxPaymentAgreement.setOnCheckedChangeListener { buttonView, isChecked ->
            activityUnityNormalPayBinding!!.tvSubmit.isEnabled = activityUnityNormalPayBinding!!.etPhoneYanzhengma.text.toString().trim { it <= ' ' }.length == 6 && yzm != null
                    && isChecked
        }

        activityUnityNormalPayBinding!!.tvAmonut.text = "￥ " + PreciseCompute.div(lianDong!!.amount, 1.0, 2)

        if ("CREDITCARD" == lianDong!!.paytype) {
            activityUnityNormalPayBinding!!.etBankNum.hint = "信用卡卡号"
        } else {
            activityUnityNormalPayBinding!!.llValidDate.visibility = View.GONE
            activityUnityNormalPayBinding!!.llCvn2.visibility = View.GONE
            activityUnityNormalPayBinding!!.etBankNum.hint = "银行卡卡号"
        }

        activityUnityNormalPayBinding!!.tvPaymentAgreement.setOnClickListener(this)
        activityUnityNormalPayBinding!!.tvSubmit.setOnClickListener(this)
        activityUnityNormalPayBinding!!.llJine.setOnClickListener(this)
        activityUnityNormalPayBinding!!.validateButtonUnityPay.setOnClickListener(this)


        activityUnityNormalPayBinding!!.etBankNum.setOnClickListener { v -> keyBoardDialog!!.showDialog(activityUnityNormalPayBinding!!.etBankNum, 23, activityUnityNormalPayBinding!!.etBankNum.text.toString().trim { it <= ' ' }, 3) }
        activityUnityNormalPayBinding!!.etShenfenNum.setOnClickListener { v -> keyBoardDialog!!.showDialog(activityUnityNormalPayBinding!!.etShenfenNum, 20, activityUnityNormalPayBinding!!.etShenfenNum.text.toString().trim { it <= ' ' }, 2) }
        activityUnityNormalPayBinding!!.etPhoneYanzhengma.setOnClickListener { v -> keyBoardDialog!!.showDialog(activityUnityNormalPayBinding!!.etPhoneYanzhengma, 6, activityUnityNormalPayBinding!!.etPhoneYanzhengma.text.toString().trim { it <= ' ' }, 1) }

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
            R.id.validateButton_unity_pay -> if (checkEmpty()) {
                try {
                    getPayData(lianDong!!.orderid, lianDong!!.paytype, lianDong!!.gateid,
                            activityUnityNormalPayBinding!!.etPhoneNum.text.toString().trim { it <= ' ' },
                            BanksUtils.formatBankNum(activityUnityNormalPayBinding!!.etBankNum.text.toString().trim { it <= ' ' }), "IDENTITY_CARD",
                            activityUnityNormalPayBinding!!.etShenfenNum.text.toString().trim { it <= ' ' }, activityUnityNormalPayBinding!!.etName.text.toString().trim { it <= ' ' },
                            if (TextUtils.isEmpty(activityUnityNormalPayBinding!!.etValidDate.text.toString()))
                                ""
                            else
                                formatValidDate(activityUnityNormalPayBinding!!.etValidDate.text.toString().trim { it <= ' ' }),
                            if (TextUtils.isEmpty(activityUnityNormalPayBinding!!.etCvn2.text.toString())) "" else activityUnityNormalPayBinding!!.etCvn2.text.toString().trim { it <= ' ' },
                            if (activityUnityNormalPayBinding!!.checkBoxPaymentAgreement.isChecked) "11" else "10")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

            }
            R.id.tv_submit -> pay(lianDong!!.orderid, lianDong!!.paytype, lianDong!!.gateid,
                    activityUnityNormalPayBinding!!.etPhoneYanzhengma.text.toString().trim { it <= ' ' },
                    activityUnityNormalPayBinding!!.etPhoneNum.text.toString().trim { it <= ' ' }, BanksUtils.formatBankNum(activityUnityNormalPayBinding!!.etBankNum.text.toString().trim { it <= ' ' }),
                    lianDong!!.tradeno, activityUnityNormalPayBinding!!.etShenfenNum.text.toString().trim { it <= ' ' }, activityUnityNormalPayBinding!!.etName.text.toString().trim { it <= ' ' },
                    if (TextUtils.isEmpty(activityUnityNormalPayBinding!!.etValidDate.text.toString()))
                        ""
                    else
                        formatValidDate(activityUnityNormalPayBinding!!.etValidDate.text.toString().trim { it <= ' ' }),
                    if (TextUtils.isEmpty(activityUnityNormalPayBinding!!.etCvn2.text.toString())) "" else activityUnityNormalPayBinding!!.etCvn2.text.toString().trim { it <= ' ' })
            R.id.tv_payment_agreement -> startActivity(Intent(this, UnityPaymentAgreementActivity::class.java))
            else -> {
            }
        }
    }

    private fun checkEmpty(): Boolean {
        if (TextUtils.isEmpty(activityUnityNormalPayBinding!!.etBankNum.text.toString().trim { it <= ' ' })) {
            ToastUtil.Companion.toastWarning("请填写银行卡号!")
            return false
        } else if (TextUtils.isEmpty(activityUnityNormalPayBinding!!.etShenfenNum.text.toString().trim { it <= ' ' })) {
            ToastUtil.Companion.toastWarning("请填写身份证号!")
            return false
        } else if (TextUtils.isEmpty(activityUnityNormalPayBinding!!.etName.text.toString().trim { it <= ' ' })) {
            ToastUtil.Companion.toastWarning("请填写持卡人姓名!")
            return false
        } else if (activityUnityNormalPayBinding!!.llCvn2.visibility == View.VISIBLE && TextUtils.isEmpty(activityUnityNormalPayBinding!!.etCvn2.text.toString().trim { it <= ' ' })) {
            ToastUtil.Companion.toastWarning("请填写信用卡cvn2/cvv2!")
            return false
        } else if (activityUnityNormalPayBinding!!.llValidDate.visibility == View.VISIBLE && TextUtils.isEmpty(activityUnityNormalPayBinding!!.etValidDate.text.toString().trim { it <= ' ' })) {
            ToastUtil.Companion.toastWarning("请填写信用卡有效日期!")
            return false
        } else if (TextUtils.isEmpty(activityUnityNormalPayBinding!!.etPhoneNum.text.toString().trim { it <= ' ' })) {
            ToastUtil.Companion.toastWarning("请填写银行预留手机号!")
            return false
        } else if (!VerifyUtils.checkBankCard(BanksUtils.formatBankNum(activityUnityNormalPayBinding!!.etBankNum.text.toString().trim { it <= ' ' }))) {
            ToastUtil.Companion.toastWarning("银行卡号码校验失败!")
            return false
        } else if (!idCard!!.verify(activityUnityNormalPayBinding!!.etShenfenNum.text.toString().trim { it <= ' ' })) {
            ToastUtil.Companion.toastWarning(idCard!!.codeError)
            return false
        } else if (!VerifyUtils.isMobileNO(activityUnityNormalPayBinding!!.etPhoneNum.text.toString().trim { it <= ' ' })) {
            ToastUtil.Companion.toastWarning("手机号码校验失败!")
            return false
        }
        return true
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getPayData(ordernb: String, pay_type: String, gate_id: String, media_id: String, card_id: String,
                             identity_type: String, identity_code: String, card_holder: String, valid_date: String, cvv2: String,
                             isnot_agree: String) {

        val body = HttpRequestParams.getParamsForGetPayData(ordernb, pay_type, gate_id, media_id,
                card_id, identity_type, identity_code, card_holder, valid_date, cvv2, isnot_agree,
                if (lianDong!!.tradeno == null) "" else lianDong!!.tradeno, "12")
        post(HttpURLS.getPayDataForLDYS, true, "GetPayData", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getString("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                val lianDong1 = JSONObject.parseObject(ResponseFields, LianDong::class.java)
                this@UnityNormalPayActivity.lianDong = lianDong1
                activityUnityNormalPayBinding!!.validateButtonUnityPay.startTimer()
                yzm = "123456"
                activityUnityNormalPayBinding!!.checkBoxPaymentAgreement.isEnabled = false
                if (ProcessCode == "0000") {
                } else {
                    ToastUtil.Companion.toastError(ProcessDes)
                }
            }
        })
    }

    protected fun getPayData(ordernb: String, pay_type: String, gate_id: String, media_id: String, card_id: String,
                             identity_type: String, identity_code: String, card_holder: String, valid_date: String, cvv2: String,
                             isnot_agree: String, trade_no: String) {

        val body = HttpRequestParams.getParamsForGetPayData(ordernb, pay_type, gate_id, "", "",
                "", "", "", "", "", "", "", "12")
        post(HttpURLS.getPayDataForLDYS, true, "GetPayData", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getString("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                val lianDong1 = JSONObject.parseObject(ResponseFields, LianDong::class.java)
                this@UnityNormalPayActivity.lianDong = lianDong1
                activityUnityNormalPayBinding!!.validateButtonUnityPay.startTimer()
                yzm = "123456"
                activityUnityNormalPayBinding!!.checkBoxPaymentAgreement.isEnabled = false
                if (ProcessCode == "0000") {
                } else {
                    ToastUtil.Companion.toastError(ProcessDes)
                }
            }
        })
    }

    protected fun pay(ordernb: String, pay_type: String, gate_id: String, verifycode: String, media_id: String, card_id: String, tradeno: String,
                      identity_code: String, card_holder: String, valid_date: String, cvv2: String) {

        val body = HttpRequestParams.getParamsForconfirmPayForLDYS(ordernb, pay_type, gate_id, verifycode, media_id, card_id,
                tradeno, identity_code, card_holder, valid_date, cvv2)
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
//                    UnityPayResultActivity.startActivity(this@UnityNormalPayActivity, lianDong!!.orderid)
                } else {
//                    ToastUtil.Companion.toastError(ProcessDes)
                }
                UnityPayResultActivity.startActivity(this@UnityNormalPayActivity, lianDong!!.orderid)

            }
        })
    }

    private fun formatValidDate(valid_date: String): String {
        var valid_date = valid_date
        val valid_date_int = Integer.parseInt(valid_date)
        valid_date = (valid_date_int % 100 * 100 + valid_date_int / 100).toString() + ""
        return valid_date
    }

    override fun OnPasswordsCompleted(editView: TextView, passwords: String) {
        editView.text = passwords
    }

    companion object {

        fun startActivity(context: Context, lianDong: LianDong) {
            val intent = Intent(context, UnityNormalPayActivity::class.java)
            intent.putExtra("LianDong", Parcels.wrap(lianDong))
            context.startActivity(intent)
        }
    }

}