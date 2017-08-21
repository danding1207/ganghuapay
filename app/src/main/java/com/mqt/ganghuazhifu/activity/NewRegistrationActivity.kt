package com.mqt.ganghuazhifu.activity

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.RecordChangedEvent
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.bean.Unit
import com.mqt.ganghuazhifu.databinding.ActivityNewRegistrationBinding
import com.mqt.ganghuazhifu.event.UnitySelectedEvent
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.RegularExpressionUtils
import com.mqt.ganghuazhifu.utils.TextValidation
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.mqt.ganghuazhifu.utils.VerifyUtils
import com.orhanobut.logger.Logger
import org.parceler.Parcels

/**
 * 注册

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class NewRegistrationActivity : BaseActivity() {

    private var password: String? = null
    private var phone: String? = null
    private var yzm: String? = null
    private var password_again: String? = null
    private var verificationKey: String? = null
    private var name: String? = null
    private var time: Long = 0
    private var unit: Unit? = null
    private var ok: Int = 0
    private var ImageKey = ""
    private var ImageCode = ""

    private var activityNewRegistrationBinding: ActivityNewRegistrationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewRegistrationBinding = DataBindingUtil.setContentView<ActivityNewRegistrationBinding>(this, R.layout.activity_new_registration)
        initView()
        getImageValidateCode()
        getLoginAccount()
    }

    private fun initView() {
        setSupportActionBar(activityNewRegistrationBinding!!.toolbar)
        supportActionBar!!.title = "注册"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityNewRegistrationBinding!!.checkBoxUserAgreement.isChecked = true

        activityNewRegistrationBinding!!.tvUserAgreement.setOnClickListener(this)
        activityNewRegistrationBinding!!.btSelect.setOnClickListener(this)
        activityNewRegistrationBinding!!.tvGetYzm.setOnClickListener(this)
        activityNewRegistrationBinding!!.forgetSecurityImage.setOnClickListener(this)
        activityNewRegistrationBinding!!.tvLogin.setOnClickListener(this)

        activityNewRegistrationBinding!!.etExtraCode.addTextChangedListener(textWatcher)

        TextValidation.setRegularValidation(this, activityNewRegistrationBinding!!.etPassword)
        //        TextValidation.setRegularValidationName(this, activityNewRegistrationBinding.etLoginName);
        TextValidation.setRegularValidation(this, activityNewRegistrationBinding!!.etPasswordAgain)

        TextValidation.setOnFocusChangeListener({ v, hasFocus ->
            if (v == activityNewRegistrationBinding!!.etPhone && !hasFocus) {
                checkPhone()
            }
        }, activityNewRegistrationBinding!!.etPhone)

        TextValidation.setOnFocusChangeListener({ v, hasFocus ->
            if (v == activityNewRegistrationBinding!!.etPassword && !hasFocus) {
                checkPassword()
            }
        }, activityNewRegistrationBinding!!.etPassword)

        TextValidation.setOnFocusChangeListener({ v, hasFocus ->
            if (v == activityNewRegistrationBinding!!.etPasswordAgain && !hasFocus) {
                checkPasswordAgain()
            }
        }, activityNewRegistrationBinding!!.etPasswordAgain)

        TextValidation.setOnFocusChangeListener({ v, hasFocus ->
            if (v == activityNewRegistrationBinding!!.etLoginName && !hasFocus) {
                checkLoginName()
            }
        }, activityNewRegistrationBinding!!.etLoginName)

        activityNewRegistrationBinding!!.checkBoxUserAgreement.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                activityNewRegistrationBinding!!.tvLogin.isClickable = true
                activityNewRegistrationBinding!!.cardViewLogin.setCardBackgroundColor(Color.parseColor("#46872B"))
            } else {
                activityNewRegistrationBinding!!.tvLogin.isClickable = false
                activityNewRegistrationBinding!!.cardViewLogin.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                if (activityNewRegistrationBinding!!.etExtraCode.text.length == 4) {
                    // 通信校验验证码
                    validateCode()
                } else {
                    resetvaIidateCodeDrawableNull()
                }
            } catch (e: Exception) {
            }

        }
    }

    /**
     * 提交注册
     */
    private fun submit() {
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForRegistration(phone, password!!, name, unit!!.PayeeCode, verificationKey, yzm)
            post(HttpURLS.registration, true, "Registration", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val Response = response.getString("ResponseHead")
                    if (Response != null) {
                        val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                        if (head != null && head.ProcessCode == "0000") {
                            finish()
                        } else {
                            if (head != null && head.ProcessDes != null) {
                                ToastUtil.toastError(head.ProcessDes)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun checkPhone() {
        phone = activityNewRegistrationBinding!!.etPhone.text.toString().trim { it <= ' ' }
        if (!VerifyUtils.isMobileNO(activityNewRegistrationBinding!!.etPhone.text.toString().trim { it <= ' ' })) {
            ToastUtil.toastWarning("手机号码错误！")
            activityNewRegistrationBinding!!.etPhone.setText("")
        } else {
            val body = HttpRequestParams.getParamsForcheckData(phone, "02")
            post(HttpURLS.checkData, true, "checkData", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val Response = response.getString("ResponseHead")
                    if (Response != null) {
                        val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                        if (head != null && head.ProcessCode == "0000") {
                        } else {
                            if (head != null && head.ProcessDes != null) {
                                ToastUtil.toastError(head.ProcessDes)
                                activityNewRegistrationBinding!!.etPhone.setText("")
                            }
                        }
                    }
                }
            })
        }
    }

    private fun checkPassword() {
        password = activityNewRegistrationBinding!!.etPassword.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.toastWarning("密码不能为空！")
            activityNewRegistrationBinding!!.etPassword.setText("")
        } else if (password!!.length > 30 || password!!.length < 6) {
            ToastUtil.toastWarning("请输入6-30位的密码！")
            activityNewRegistrationBinding!!.etPassword.setText("")
        }
    }

    private fun checkPasswordAgain() {
        password_again = activityNewRegistrationBinding!!.etPasswordAgain.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(password_again)) {
            ToastUtil.toastWarning("密码不能为空！")
            activityNewRegistrationBinding!!.etPasswordAgain.setText("")
        } else if (password_again!!.length > 30 || password_again!!.length < 6) {
            ToastUtil.toastWarning("请输入6-30位的密码！")
            activityNewRegistrationBinding!!.etPasswordAgain.setText("")
        } else if (password == null || password != password_again) {
            ToastUtil.toastWarning("两次密码不同！")
            activityNewRegistrationBinding!!.etPasswordAgain.setText("")
        }
    }

    private fun checkLoginName() {
        name = activityNewRegistrationBinding!!.etLoginName.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(name)) {
            ToastUtil.toastWarning("请输入登录名！")
            activityNewRegistrationBinding!!.etLoginName.setText("")
        } else if (name!!.length < 6) {
            ToastUtil.toastWarning("请输入至少6位登录名！")
            activityNewRegistrationBinding!!.etLoginName.setText("")
        } else if (!RegularExpressionUtils.regularExpression_Eng(name!![0].toString())) {
            ToastUtil.toastWarning("输入不合法,首位必须为英文字母！")
            activityNewRegistrationBinding!!.etLoginName.setText("")
        }
    }

    /**
     * 检查输入数据是否合格，并给出提示信息

     * @return
     */
    private fun checkEmpty(): Boolean {
        password = activityNewRegistrationBinding!!.etPassword.text.toString().trim { it <= ' ' }
        phone = activityNewRegistrationBinding!!.etPhone.text.toString().trim { it <= ' ' }
        password_again = activityNewRegistrationBinding!!.etPasswordAgain.text.toString().trim { it <= ' ' }
        name = activityNewRegistrationBinding!!.etLoginName.text.toString().trim { it <= ' ' }
        yzm = activityNewRegistrationBinding!!.etPhonevalidate.text.toString().trim { it <= ' ' }
        if (!VerifyUtils.isMobileNO(activityNewRegistrationBinding!!.etPhone.text.toString().trim { it <= ' ' })) {
            ToastUtil.toastWarning("手机号码错误！")
            return false
        } else if (TextUtils.isEmpty(password)) {
            ToastUtil.toastWarning("密码不能为空！")
            return false
        } else if (password!!.length > 30 || password!!.length < 6) {
            ToastUtil.toastWarning("请输入6-30位的密码！")
            return false
        } else if (password != password_again) {
            ToastUtil.toastWarning("两次密码不同！")
            return false
        } else if (TextUtils.isEmpty(name)) {
            ToastUtil.toastWarning("请输入登录名！")
            return false
        } else if (name!!.length < 6) {
            ToastUtil.toastWarning("请输入至少6位登录名！")
            return false
        } else if (!RegularExpressionUtils.regularExpression_Eng(name!!.substring(0, 1))) {
            ToastUtil.toastWarning("输入不合法,首位必须为英文字母！")
            return false
        } else if (ok == 11 || activityNewRegistrationBinding!!.etExtraCode.text.length < 4) {
            ToastUtil.toastWarning("请正确输入图形验证码！")
            return false
        } else if (time == 0L || verificationKey == null) {
            ToastUtil.toastWarning("请先获取验证码！")
            return false
        } else if (System.currentTimeMillis() - time > 300 * 1000) {
            ToastUtil.toastWarning("验证码已失效，请重新获取！")
            return false
        } else if (TextUtils.isEmpty(yzm)) {
            ToastUtil.toastWarning("请填写验证码！")
            return false
        } else if (unit == null) {
            ToastUtil.toastWarning("请选择常用缴费单位！")
            return false
        }
        return true
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_get_yzm -> getCaptcha()
            R.id.tv_login -> submit()
            R.id.tv_user_agreement -> startActivity(Intent(this, UserAgreementActivity::class.java))
            R.id.bt_select -> startActivity(Intent(this, SelectUnityListActivity::class.java))
            R.id.forget_security_image -> getImageValidateCode()
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD)
    fun onUnitySelectedEvent(event: UnitySelectedEvent) {
        Logger.e("UnitySelectedEvent")
        if (event != null) {
            if (event.data!=null) {
                if(event.data.getParcelableExtra<Parcelable>("Unity")!=null){
                    unit = Parcels.unwrap<Unit>(event.data.getParcelableExtra<Parcelable>("Unity"))
                    if (unit != null && activityNewRegistrationBinding!=null) {
                        activityNewRegistrationBinding!!.tvQuyu.text = unit!!.PayeeNm
                    }
                }
            }
        }
    }

    /**
     * 网络获取验证码
     */
    private fun getCaptcha() {
        val phone = activityNewRegistrationBinding!!.etPhone.text.toString().trim { it <= ' ' }
        val GetType = "01"
        if (!TextUtils.isEmpty(phone)) {
            if (VerifyUtils.isMobileNO(phone)) {
                if (ok == 10 && activityNewRegistrationBinding!!.etExtraCode.text.length == 4) {
                    val body = HttpRequestParams.getParamsForVerificationCode(phone, GetType, activityNewRegistrationBinding!!.etExtraCode.text.toString().trim { it <= ' ' }, ImageKey)
                    post(HttpURLS.getVerificationCode, true, "VerificationCode", body, OnHttpRequestListener { isError, response, type, error ->
                        if (isError) {
                            Logger.e(error.toString())
                        } else {
                            Logger.i(response.toString())
                            val ResponseFields = response.getJSONObject("ResponseFields")
                            val ResponseHead = response.getJSONObject("ResponseHead")
                            val ProcessCode = ResponseHead.getString("ProcessCode")
                            val ProcessDes = ResponseHead.getString("ProcessDes")
                            if (ProcessCode == "0000") {
                                activityNewRegistrationBinding!!.tvGetYzm.startTimer()
                                val VerificationKey = ResponseFields.getString("VerificationCode")
                                verificationKey = VerificationKey
                                time = System.currentTimeMillis()
                            } else {
                                ToastUtil.toastError(ProcessDes)
                            }
                        }
                    })
                } else {
                    ToastUtil.toastWarning("请正确输入图形验证码！")
                }
            } else {
                ToastUtil.toastWarning("手机号码格式错误！")
            }
        } else {
            ToastUtil.toastWarning("请填写手机号码！")
        }
    }

    /**
     * 获取登录名
     */
    private fun getLoginAccount() {
        val body = HttpRequestParams.getParamsGetLoginAccount()
        post(HttpURLS.loginAccount, true, "loginAccount", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseFields = response.getJSONObject("ResponseFields")
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        val LoginAccount = ResponseFields.getString("LoginAccount")
                        Logger.d(LoginAccount)
                        activityNewRegistrationBinding!!.etLoginName.setText(LoginAccount.trim { it <= ' ' })
                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    /**
     * 前段获取验证码
     */
    private fun getImageValidateCode() {
        activityNewRegistrationBinding!!.etExtraCode.setText("")
        resetvaIidateCodeDrawableNull()
        val body = HttpRequestParams.getParamsGetImageValidateCode("1003")
        post(HttpURLS.imageValidateCode, false, "imageValidateCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseFields = response.getJSONObject("ResponseFields")
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        ImageKey = ResponseFields.getString("ImageKey")
                        ImageCode = ResponseFields.getString("ImageCode")
                        activityNewRegistrationBinding!!.forgetSecurityImage.setImageBitmap(getBitmap(ImageCode))
                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    /**
     * 校验验证码
     */
    private fun validateCode() {
        val body = HttpRequestParams.getParamsForValidateCode(activityNewRegistrationBinding!!.etExtraCode.text.toString(), ImageKey)
        post(HttpURLS.validateCode, true, "validateCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseFields = response.getJSONObject("ResponseFields")
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {

                        val VerificationResult = ResponseFields.getString("VerificationResult")// 验证结果
                        // 10：成功
                        // 。11：失败
                        ok = Integer.parseInt(VerificationResult)
                        val VerificationResultDesc = ResponseFields.getString("VerificationResultDesc")// 验证结果描述
                        // toast(VerificationResultDesc);
                        if (ok == 10) {
                            resetvaIidateCodeDrawableRight()
                        } else if (ok == 11) {
                            resetvaIidateCodeDrawableWrong()
                        }

                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableNull() {
        activityNewRegistrationBinding!!.etExtraCode.setCompoundDrawables(activityNewRegistrationBinding!!.etExtraCode.compoundDrawables[0],
                activityNewRegistrationBinding!!.etExtraCode.compoundDrawables[1], null, activityNewRegistrationBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableWrong() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.err_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityNewRegistrationBinding!!.etExtraCode.setCompoundDrawables(activityNewRegistrationBinding!!.etExtraCode.compoundDrawables[0],
                activityNewRegistrationBinding!!.etExtraCode.compoundDrawables[1], drawable, activityNewRegistrationBinding!!.etExtraCode.compoundDrawables[3])
    }

    private fun resetvaIidateCodeDrawableRight() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.right_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityNewRegistrationBinding!!.etExtraCode.setCompoundDrawables(activityNewRegistrationBinding!!.etExtraCode.compoundDrawables[0],
                activityNewRegistrationBinding!!.etExtraCode.compoundDrawables[1], drawable, activityNewRegistrationBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 生成验证码图片
    fun getBitmap(str: String): Bitmap {
        val decode = Base64.decode(str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
        return bitmap
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
