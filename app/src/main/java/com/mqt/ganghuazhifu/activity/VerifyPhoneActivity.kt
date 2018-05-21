package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.view.View

import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.bean.User
import com.mqt.ganghuazhifu.databinding.ActivityVerifyPhoneBinding
import com.mqt.ganghuazhifu.ext.Klog
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

import java.util.regex.Matcher
import java.util.regex.Pattern

import okhttp3.RequestBody

/**
 * 验证手机短信

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class VerifyPhoneActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var phoneNb: String? = null
    private var yzm: String? = null
    private var verificationKey: String? = null
    private var time: Long = 0
    private var type: Int = 0// 1:注册;2:忘记密码;3:安保问题管理;4:修改登录密码;5:修改手机号码   ***1,2,3,5 已废弃
    private var ok: Int = 0
    private var ImageKey = ""
    private var ImageCode = ""
    private var activityVerifyPhoneBinding: ActivityVerifyPhoneBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVerifyPhoneBinding = DataBindingUtil.setContentView<ActivityVerifyPhoneBinding>(this,
                R.layout.activity_verify_phone)
        phoneNb = intent.getStringExtra("phoneNb")
        type = intent.getIntExtra("TYPE", 1)
        initView()
        getImageValidateCode()
    }

    private fun initView() {
        setSupportActionBar(activityVerifyPhoneBinding!!.toolbar)
        supportActionBar!!.title = "验证手机号"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val user = EncryptedPreferencesUtils.getUser()
        if (phoneNb == null) {
            activityVerifyPhoneBinding!!.phoneNumber.text = "您好，请注意查收发往手机" + user.PhoneNb + "的验证码信息。"
        } else {
            activityVerifyPhoneBinding!!.phoneNumber.text = "您好，请注意查收发往手机" + phoneNb + "的验证码信息。"
        }
        activityVerifyPhoneBinding!!.etExtraCode.addTextChangedListener(textWatcher)
        activityVerifyPhoneBinding!!.forgetSecurityImage.setOnClickListener(this)
        activityVerifyPhoneBinding!!.tvNext.setOnClickListener(this)
        activityVerifyPhoneBinding!!.tvGetYzm.setOnClickListener(this)
        when (type) {
            1,
            2 -> activityVerifyPhoneBinding!!.linearLayoutContainer.setBackgroundResource(R.drawable.login_bg)
            3, 5, 4 -> {
                activityVerifyPhoneBinding!!.linearLayoutContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.main_bg))
                activityVerifyPhoneBinding!!.tvTitleRight.visibility = View.VISIBLE
                activityVerifyPhoneBinding!!.tvTitleRight.setOnClickListener(this)
                activityVerifyPhoneBinding!!.tvNext.visibility = View.GONE
            }
        }

    }

    fun isMobileNO(mobiles: String): Boolean {
        var flag:Boolean
        try {
            val p = Pattern.compile("^(13|14|15|17|18)\\d{9}$")
            val m = p.matcher(mobiles)
            flag = m.matches()
        } catch (e: Exception) {
            flag = false
        }

        return flag
    }

    fun checkPromoter(mobiles: String): Boolean {
        var flag :Boolean
        try {
            val p = Pattern.compile("^[P|D|S|M|U][a-zA-Z0-9]{6}+$")
            val m = p.matcher(mobiles)
            flag = m.matches()
        } catch (e: Exception) {
            flag = false
        }
        return flag
    }

    /**
     * 网络获取验证码
     */
    private fun getCaptcha() {
        var to: String? = null
        var GetType: String? = null
        val user = EncryptedPreferencesUtils.getUser()
        when (type) {
        // 1:注册;2:忘记密码;3:安保管理;4:修改登录密码;05：修改手机号码
            3 -> {
                to = user.PhoneNb
                GetType = "04"
            }
            4 -> {
                to = user.PhoneNb
                GetType = "03"
            }
        }
        if (to != null) {
            if (ok == 10 && activityVerifyPhoneBinding!!.etExtraCode.text.length == 4) {
                val body = HttpRequestParams.getParamsForVerificationCode(to, GetType,
                        activityVerifyPhoneBinding!!.etExtraCode.text.toString().trim { it <= ' ' }, ImageKey)
                post(HttpURLS.getVerificationCode, true, "VerificationCode", body, OnHttpRequestListener { isError, response, type, error ->
                    if (isError) {
                        Klog.e(error.toString())
                    } else {
                        Klog.i(response.toString())
                        val ResponseFields = response.getJSONObject("ResponseFields")
                        val ResponseHead = response.getJSONObject("ResponseHead")
                        val ProcessCode = ResponseHead.getString("ProcessCode")
                        val ProcessDes = ResponseHead.getString("ProcessDes")
                        if (ProcessCode == "0000") {
                            activityVerifyPhoneBinding!!.tvGetYzm.startTimer()
                            val VerificationCode = ResponseFields.getString("VerificationCode")
                            verificationKey = VerificationCode
                            time = System.currentTimeMillis()
                            activityVerifyPhoneBinding!!.phoneNumber.visibility = View.VISIBLE
                        } else {
                            ToastUtil.Companion.toastError(ProcessDes)
                        }
                    }
                })
            } else {
                ToastUtil.Companion.toastWarning("请正确输入图形验证码!")
            }
        }
    }

    /**
     * 检查输入数据是否合格，并给出提示信息

     * @return
     */
    private fun checkEmpty(): Boolean {
        yzm = activityVerifyPhoneBinding!!.etPhonevalidate.text.toString().trim { it <= ' ' }
        if (time == 0L || verificationKey == null) {
            ToastUtil.Companion.toastWarning("请先获取验证码!")
            return false
        } else if (System.currentTimeMillis() - time > 300 * 1000) {
            ToastUtil.Companion.toastWarning("验证码已失效，请重新获取!")
            return false
        } else if (TextUtils.isEmpty(yzm)) {
            ToastUtil.Companion.toastWarning("请填写验证码!")
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_title_right -> if (checkEmpty()) {
                val intent: Intent
                when (type) {
                // 3:安保问题管理;4:修改登录密码;
                    3 -> {
                        intent = Intent(this@VerifyPhoneActivity, RegistrationSetQuestionActivity::class.java)
                        intent.putExtra("TYPE", 2)
                        intent.putExtra("VerificationCode", yzm)
                        intent.putExtra("VerificationKey", verificationKey)
                        startActivity(intent)
                    }
                    4 -> {
                        intent = Intent(this@VerifyPhoneActivity, SetNewPasswordActivity::class.java)
                        intent.putExtra("TYPE", 2)
                        intent.putExtra("VerificationCode", yzm)
                        intent.putExtra("VerificationKey", verificationKey)
                        startActivity(intent)
                    }
                }
            }
            R.id.tv_get_yzm -> getCaptcha()
            R.id.forget_security_image -> getImageValidateCode()
        }
    }

    /**
     * 前段获取验证码
     */
    private fun getImageValidateCode() {
        activityVerifyPhoneBinding!!.etExtraCode.setText("")
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
                        activityVerifyPhoneBinding!!.forgetSecurityImage.setImageBitmap(getBitmap(ImageCode))

                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.Companion.toastError(head.ProcessDes)
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
        val body = HttpRequestParams.getParamsForValidateCode(activityVerifyPhoneBinding!!.etExtraCode.text.toString(), ImageKey)
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
                            ToastUtil.Companion.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableNull() {
        activityVerifyPhoneBinding!!.etExtraCode.setCompoundDrawables(activityVerifyPhoneBinding!!.etExtraCode.compoundDrawables[0],
                activityVerifyPhoneBinding!!.etExtraCode.compoundDrawables[1], null, activityVerifyPhoneBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableWrong() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.err_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityVerifyPhoneBinding!!.etExtraCode.setCompoundDrawables(activityVerifyPhoneBinding!!.etExtraCode.compoundDrawables[0],
                activityVerifyPhoneBinding!!.etExtraCode.compoundDrawables[1], drawable, activityVerifyPhoneBinding!!.etExtraCode.compoundDrawables[3])
    }

    private fun resetvaIidateCodeDrawableRight() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.right_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityVerifyPhoneBinding!!.etExtraCode.setCompoundDrawables(activityVerifyPhoneBinding!!.etExtraCode.compoundDrawables[0],
                activityVerifyPhoneBinding!!.etExtraCode.compoundDrawables[1], drawable, activityVerifyPhoneBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 生成验证码图片
    fun getBitmap(str: String): Bitmap {
        val decode = Base64.decode(str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
        return bitmap
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                if (activityVerifyPhoneBinding!!.etExtraCode.text.length == 4) {
                    // 通信校验验证码
                    validateCode()
                } else {
                    resetvaIidateCodeDrawableNull()
                }
            } catch (ignored: Exception) {
            }

        }
    }

    companion object {

        fun startActivity(context: Context, type: Int, phone: String? ) {
            val intent = Intent(context, VerifyPhoneActivity::class.java)
            intent.putExtra("TYPE", type)
            intent.putExtra("phoneNb", phone)
            context.startActivity(intent)
        }
    }

}
