package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.databinding.ActivityChangePhoneBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.TextValidation
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.mqt.ganghuazhifu.utils.VerifyUtils
import com.orhanobut.logger.Logger

/**
 * 验证手机短信

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class ChangePhoneActivity : BaseActivity() {

    private var phone: String? = null
    private var yzm: String? = null
    private var verificationKey: String? = null
    private var ok: Int = 0
    private var ImageKey = ""
    private var ImageCode = ""
    private var time: Long = 0

    private var activityChangePhoneBinding: ActivityChangePhoneBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChangePhoneBinding = DataBindingUtil.setContentView<ActivityChangePhoneBinding>(this,
                R.layout.activity_change_phone)
        initView()
        getImageValidateCode()
    }

    private fun initView() {
        activityChangePhoneBinding!!.phoneNumber.text = String.format(getString(R.string.short_message_tip), EncryptedPreferencesUtils.getUser().PhoneNb)
        setSupportActionBar(activityChangePhoneBinding!!.toolbar)
        supportActionBar!!.title = "验证手机号"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activityChangePhoneBinding!!.etExtraCode.addTextChangedListener(textWatcher)
        activityChangePhoneBinding!!.cardViewNext.setOnClickListener(this)
        activityChangePhoneBinding!!.forgetSecurityImage.setOnClickListener(this)
        activityChangePhoneBinding!!.tvGetYzm.setOnClickListener(this)

        TextValidation.setOnFocusChangeListener({ v, hasFocus ->
            if (v == activityChangePhoneBinding!!.etSetPhone && !hasFocus) {
                checkPhone()
            }
        }, activityChangePhoneBinding!!.etSetPhone)
    }

    private fun checkPhone() {
        phone = activityChangePhoneBinding!!.etSetPhone.text.toString().trim { it <= ' ' }
        if (!VerifyUtils.isMobileNO(activityChangePhoneBinding!!.etSetPhone.text.toString().trim { it <= ' ' })) {
            ToastUtil.toastError("手机号码错误！")
            activityChangePhoneBinding!!.etSetPhone.setText("")
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
                                activityChangePhoneBinding!!.etSetPhone.setText("")
                            }
                        }
                    }
                }
            })
        }
    }

    /**
     * 网络获取短信验证码
     */
    private fun getCaptcha() {
        phone = activityChangePhoneBinding!!.etSetPhone.text.toString().trim { it <= ' ' }
        val to = phone
        val GetType = "05"
        if (to != null) {
            if (ok == 10 && activityChangePhoneBinding!!.etExtraCode.text.length == 4) {
                val body = HttpRequestParams.getParamsForChangePhoneVerificationCode(to, GetType, activityChangePhoneBinding!!.etExtraCode.text.toString().trim { it <= ' ' }, ImageKey)
                post(HttpURLS.getVerificationCode, true, "VerificationCode", body, OnHttpRequestListener { isError, response, type, error ->
                    if (isError) {
                        Logger.e(error.toString())
                    } else {
                        Logger.d(response.toString())
                        val ResponseFields = response.getJSONObject("ResponseFields")
                        val ResponseHead = response.getJSONObject("ResponseHead")
                        val ProcessCode = ResponseHead.getString("ProcessCode")
                        val ProcessDes = ResponseHead.getString("ProcessDes")
                        if (ProcessCode == "0000") {
                            activityChangePhoneBinding!!.tvGetYzm.startTimer()
                            val VerificationCode = ResponseFields.getString("VerificationCode")
                            verificationKey = VerificationCode
                            time = System.currentTimeMillis()
                            activityChangePhoneBinding!!.phoneNumber.visibility = View.VISIBLE
                        } else {
                            ToastUtil.toastError(ProcessDes)
                        }
                    }
                })
            } else {
                ToastUtil.toastWarning("请正确输入图形验证码")
            }
        }
    }

    /**
     * 检查输入数据是否合格，并给出提示信息
     * @return
     */
    private fun checkEmpty(): Boolean {
        yzm = activityChangePhoneBinding!!.etPhonevalidate.text.toString().trim { it <= ' ' }
        if (time == 0L || verificationKey == null) {
            ToastUtil.toastWarning("请先获取验证码!")
            return false
        } else if (ok == 11 || activityChangePhoneBinding!!.etExtraCode.text.length < 4) {
            ToastUtil.toastWarning("请正确输入图形验证码!")
            return false
        } else if (System.currentTimeMillis() - time > 300 * 1000) {
            ToastUtil.toastWarning("验证码已失效，请重新获取!")
            return false
        } else if (TextUtils.isEmpty(yzm)) {
            ToastUtil.toastWarning("请填写验证码!")
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_get_yzm -> {
                getCaptcha()
            }
            R.id.cardView_next -> if (checkEmpty()) {
                submit()
            }
            R.id.forget_security_image -> getImageValidateCode()
        }
    }

    // 提交修改邮箱
    private fun submit() {
        if (checkEmpty()) {
            val user = EncryptedPreferencesUtils.getUser()
            val body = HttpRequestParams.getParamsForSetPhoneNum(user.LoginAccount, user.PhoneNb,
                    phone, verificationKey, yzm)
            post(HttpURLS.userUpdate, true, "SetPhoneNum", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        EncryptedPreferencesUtils.setPhoneNb(phone)
                        MaterialDialog.Builder(this@ChangePhoneActivity)
                                .title("提醒")
                                .content("修改手机号码成功")
                                .onPositive { dialog, which -> finish() }
                                .cancelable(false)
                                .positiveText("确定")
                                .canceledOnTouchOutside(false)
                                .show()
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                if (activityChangePhoneBinding!!.etExtraCode.text.length == 4) {
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
     * 前段获取验证码
     */
    private fun getImageValidateCode() {
        activityChangePhoneBinding!!.etExtraCode.setText("")
        resetvaIidateCodeDrawableNull()
        val body = HttpRequestParams.getParamsGetImageValidateCode("1003")
        Logger.d(body.toString())
        post(HttpURLS.imageValidateCode, true, "imageValidateCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseFields = response.getJSONObject("ResponseFields")
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        ImageKey = ResponseFields.getString("ImageKey")
                        ImageCode = ResponseFields.getString("ImageCode")
                        activityChangePhoneBinding!!.forgetSecurityImage.setImageBitmap(getBitmap(ImageCode))
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
        val body = HttpRequestParams.getParamsForValidateCode(activityChangePhoneBinding!!.etExtraCode.text.toString(), ImageKey)
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
        activityChangePhoneBinding!!.etExtraCode.setCompoundDrawables(activityChangePhoneBinding!!.etExtraCode.compoundDrawables[0],
                activityChangePhoneBinding!!.etExtraCode.compoundDrawables[1], null, activityChangePhoneBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableWrong() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.err_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityChangePhoneBinding!!.etExtraCode.setCompoundDrawables(activityChangePhoneBinding!!.etExtraCode.compoundDrawables[0],
                activityChangePhoneBinding!!.etExtraCode.compoundDrawables[1], drawable, activityChangePhoneBinding!!.etExtraCode.compoundDrawables[3])
    }

    private fun resetvaIidateCodeDrawableRight() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.right_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityChangePhoneBinding!!.etExtraCode.setCompoundDrawables(activityChangePhoneBinding!!.etExtraCode.compoundDrawables[0],
                activityChangePhoneBinding!!.etExtraCode.compoundDrawables[1], drawable, activityChangePhoneBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 生成验证码图片
    fun getBitmap(str: String): Bitmap {
        val decode = Base64.decode(str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
        return bitmap
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString(STATE_PHONE, phone)
        savedInstanceState.putString(STATE_YZM, yzm)
        savedInstanceState.putString(STATE_VKEY, verificationKey)
        savedInstanceState.putInt(STATE_OK, ok)
        savedInstanceState.putString(STATE_IMAGEKEY, ImageKey)
        savedInstanceState.putString(STATE_IMAGECODE, ImageCode)
        savedInstanceState.putLong(STATE_TIME, time)
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
        phone = savedInstanceState.getString(STATE_PHONE)
        yzm = savedInstanceState.getString(STATE_YZM)
        verificationKey = savedInstanceState.getString(STATE_VKEY)
        ok = savedInstanceState.getInt(STATE_OK)
        ImageKey = savedInstanceState.getString(STATE_IMAGEKEY)
        ImageCode = savedInstanceState.getString(STATE_IMAGECODE)
        time = savedInstanceState.getLong(STATE_TIME)
    }

    companion object {

        private val STATE_PHONE = "phone"
        private val STATE_YZM = "yzm"
        private val STATE_VKEY = "verificationKey"
        private val STATE_OK = "ok"
        private val STATE_IMAGEKEY = "ImageKey"
        private val STATE_IMAGECODE = "ImageCode"
        private val STATE_TIME = "time"
    }
}
