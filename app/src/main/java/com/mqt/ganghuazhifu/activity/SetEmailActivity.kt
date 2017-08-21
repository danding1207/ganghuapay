package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.databinding.ActivitySetEmailBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.*
import com.orhanobut.logger.Logger
import java.util.regex.Pattern

/**
 * 设置邮箱(设置 身份证号，设置真实姓名)

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class SetEmailActivity : BaseActivity() {

    private var type: Int = 0
    private var phone: String? = null
    private var yzm: String? = null
    private var yzm_return: String? = null
    private var time: Long = 0

    private var ok: Int = 0
    private var ImageKey = ""
    private var ImageCode = ""

    private var activitySetEmailBinding: ActivitySetEmailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySetEmailBinding = DataBindingUtil.setContentView<ActivitySetEmailBinding>(this, R.layout.activity_set_email)
        type = intent.getIntExtra("TYPE", 1)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activitySetEmailBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activitySetEmailBinding!!.etExtraCode.addTextChangedListener(textWatcher)
        activitySetEmailBinding!!.forgetSecurityImage.setOnClickListener(this)
        activitySetEmailBinding!!.tvTitleRight.setOnClickListener(this)
        activitySetEmailBinding!!.tvGetYzm.setOnClickListener(this)
        when (type) {
            SETEMAIL -> {
                supportActionBar!!.title = "设置邮箱"
                activitySetEmailBinding!!.etSetEmail.hint = "请输入您的邮箱"
                activitySetEmailBinding!!.etSetEmail.inputType = InputType.TYPE_CLASS_TEXT
                activitySetEmailBinding!!.tvSetInfoExplain.visibility = View.VISIBLE
            }
            SETIDCARDNUM -> {
                supportActionBar!!.title = "个人资料修改"
                activitySetEmailBinding!!.etSetEmail.inputType = InputType.TYPE_CLASS_TEXT // 调用数字键盘
                activitySetEmailBinding!!.etSetEmail.hint = "请输入您的身份证号码"
                activitySetEmailBinding!!.etSetEmail.maxEms = 18
                TextValidation.setRegularValidationIDCard(this, activitySetEmailBinding!!.etSetEmail)
                activitySetEmailBinding!!.tvSetInfoExplain.visibility = View.INVISIBLE
            }
            SETREALNAME -> {
                supportActionBar!!.title = "个人资料修改"
                activitySetEmailBinding!!.etSetEmail.inputType = InputType.TYPE_CLASS_TEXT
                activitySetEmailBinding!!.etSetEmail.hint = "请输入您的真实姓名"
                activitySetEmailBinding!!.etSetEmail.maxEms = 8
                TextValidation.setRegularValidationChina(this, activitySetEmailBinding!!.etSetEmail)
                activitySetEmailBinding!!.tvSetInfoExplain.visibility = View.INVISIBLE
            }
            SETPHONE -> {
                supportActionBar!!.title = "修改手机号码"
                activitySetEmailBinding!!.etYzm.visibility = View.VISIBLE
                activitySetEmailBinding!!.tvGetYzm.visibility = View.VISIBLE
                activitySetEmailBinding!!.llTuxingYzm.visibility = View.VISIBLE
                activitySetEmailBinding!!.etSetEmail.inputType = InputType.TYPE_CLASS_NUMBER // 调用数字键盘
                activitySetEmailBinding!!.etSetEmail.maxEms = 11
                activitySetEmailBinding!!.etSetEmail.hint = "请输入您的手机号码"
                activitySetEmailBinding!!.tvSetInfoExplain.visibility = View.INVISIBLE
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_get_yzm -> getCaptcha()
            R.id.tv_title_right -> submit()
        }
    }

    /**
     * 前段获取验证码
     */
    private fun getImageValidateCode() {
        activitySetEmailBinding!!.etExtraCode.setText("")
        resetvaIidateCodeDrawableNull()
        val body = HttpRequestParams.getParamsGetImageValidateCode("1003")
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
                        activitySetEmailBinding!!.forgetSecurityImage.setImageBitmap(getBitmap(ImageCode))

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
        val body = HttpRequestParams.getParamsForValidateCode(activitySetEmailBinding!!.etExtraCode.text.toString(), ImageKey)
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
        activitySetEmailBinding!!.etExtraCode.setCompoundDrawables(activitySetEmailBinding!!.etExtraCode.compoundDrawables[0],
                activitySetEmailBinding!!.etExtraCode.compoundDrawables[1], null, activitySetEmailBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableWrong() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.err_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activitySetEmailBinding!!.etExtraCode.setCompoundDrawables(activitySetEmailBinding!!.etExtraCode.compoundDrawables[0],
                activitySetEmailBinding!!.etExtraCode.compoundDrawables[1], drawable, activitySetEmailBinding!!.etExtraCode.compoundDrawables[3])
    }

    private fun resetvaIidateCodeDrawableRight() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.right_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activitySetEmailBinding!!.etExtraCode.setCompoundDrawables(activitySetEmailBinding!!.etExtraCode.compoundDrawables[0],
                activitySetEmailBinding!!.etExtraCode.compoundDrawables[1], drawable, activitySetEmailBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 生成验证码图片
    fun getBitmap(str: String): Bitmap {

        val decode = Base64.decode(str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
        return bitmap

    }

    /**
     * 网络获取验证码
     */
    private fun getCaptcha() {
        val user = EncryptedPreferencesUtils.getUser()
        phone = user.PhoneNb!!.toString().trim { it <= ' ' }
        if (ok == 10 && activitySetEmailBinding!!.etExtraCode.text.length == 4) {
            val body = HttpRequestParams.getParamsForVerificationCode(phone!!, "05", activitySetEmailBinding!!.etExtraCode.text.toString().trim { it <= ' ' }, ImageKey)
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
                        val VerificationCode = ResponseFields.getString("VerificationCode")
                        yzm_return = VerificationCode
                        time = System.currentTimeMillis()
                        activitySetEmailBinding!!.tvGetYzm.startTimer()
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        } else {
            ToastUtil.toastWarning("请正确输入图形验证码!")
        }
    }

    private fun checkEmpty(): Boolean {
        if (TextUtils.isEmpty(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })) {
            when (type) {
                SETEMAIL -> ToastUtil.toastWarning("请填写邮箱!")
                SETIDCARDNUM -> ToastUtil.toastWarning("请填写身份证号!")
                SETREALNAME -> ToastUtil.toastWarning("请填写真实姓名!")
                SETPHONE -> ToastUtil.toastWarning("请填写手机号!")
            }
            return false
        } else {
            when (type) {
                SETEMAIL -> if (!isEmail(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })) {
                    ToastUtil.toastWarning("请填写正确邮箱!")
                    return false
                }
                SETIDCARDNUM -> {
                    val idCard = IDCard()
                    if (!idCard.verify(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })) {
                        ToastUtil.toastWarning(idCard.codeError)
                        return false
                    }
                }
                SETREALNAME -> {
                }
                SETPHONE -> if (!VerifyUtils.isMobileNO(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })) {
                    ToastUtil.toastWarning("请填写正确手机号!")
                    return false
                }
            }
        }
        if (type == SETPHONE) {
            yzm = activitySetEmailBinding!!.etYzm.text.toString().trim { it <= ' ' }
            if (time == 0L) {
                ToastUtil.toastWarning("请先获取验证码!")
                return false
            } else if (ok == 11 || activitySetEmailBinding!!.etExtraCode.text.length < 4) {
                ToastUtil.toastWarning("请正确输入图形验证码!")
                return false
            } else if (TextUtils.isEmpty(yzm)) {
                ToastUtil.toastWarning("请填写短信验证码!")
                return false
            } else if (System.currentTimeMillis() - time > 300 * 1000) {
                ToastUtil.toastWarning("验证码已失效，请重新获取!")
                return false
            } else if (yzm != yzm_return) {
                ToastUtil.toastWarning("验证码错误!")
                return false
            }
        }
        return true
    }

    // 判断email格式是否正确
    fun isEmail(email: String): Boolean {
        val str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
        val p = Pattern.compile(str)
        val m = p.matcher(email)
        return m.matches()
    }

    // 提交修改邮箱
    private fun submit() {
        if (checkEmpty()) {
            val user = EncryptedPreferencesUtils.getUser()
            var body: CusFormBody? = null
            when (type) {
                SETEMAIL -> body = HttpRequestParams.getParamsForSetEmail(user.LoginAccount, user.Email,
                        activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                SETIDCARDNUM -> body = HttpRequestParams.getParamsForSetIDCardNbNewRealName(user.LoginAccount, user.RealName,
                        activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                SETREALNAME -> body = HttpRequestParams.getParamsForSetIDCardNbNewRealName(user.LoginAccount,
                        activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' }, user.IdcardNb)
                SETPHONE -> body = HttpRequestParams.getParamsForSetPhoneNum(user.LoginAccount, user.PhoneNb,
                        activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' }, null, null)
            }
            if (body != null) {
                post(HttpURLS.userUpdate, true, "SetPhoneNum", body, OnHttpRequestListener { isError, response, type, error ->
                    if (isError) {
                        Logger.e(error.toString())
                    } else {
                        Logger.d(response.toString())
                        val ResponseHead = response.getJSONObject("ResponseHead")
                        val ProcessCode = ResponseHead.getString("ProcessCode")
                        val ProcessDes = ResponseHead.getString("ProcessDes")
                        if (ProcessCode == "0000") {
                            Logger.d("type--->" + this@SetEmailActivity.type)
                            when (this@SetEmailActivity.type) {
                                SETEMAIL -> EncryptedPreferencesUtils.setEmail(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                                SETIDCARDNUM -> EncryptedPreferencesUtils.setIdcardNb(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                                SETREALNAME -> EncryptedPreferencesUtils.setRealName(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                                SETPHONE -> EncryptedPreferencesUtils.setPhoneNb(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                            }
                            finish()
                        } else {
                            ToastUtil.toastError(ProcessDes)
                        }
                    }
                })
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                if (activitySetEmailBinding!!.etExtraCode.text.length == 4) {
                    // 通信校验验证码
                    validateCode()
                } else {
                    resetvaIidateCodeDrawableNull()
                }
            } catch (e: Exception) {
            }

        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    companion object {
        private val SETEMAIL = 1
        private val SETIDCARDNUM = 2
        private val SETREALNAME = 3
        private val SETPHONE = 4
    }
}
