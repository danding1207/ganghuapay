package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
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
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.databinding.ActivityVerificationQuestionBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.TextValidation
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

/**
 * 验证安保问题

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class VerificationQuestionActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var time: Long = 0
    private var loginaccount: String? = null
    private var phoneNb: String? = null
    private var password: String? = null
    private var password_again: String? = null
    private var yzm: String? = null
    private var verificationKey: String? = null
    private var extra_code: String? = null
    private var ok: Int = 0
    private var ImageKey = ""
    private var ImageCode = ""
    private var activityVerificationQuestionBinding: ActivityVerificationQuestionBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVerificationQuestionBinding = DataBindingUtil.setContentView<ActivityVerificationQuestionBinding>(this,
                R.layout.activity_verification_question)
        loginaccount = intent.getStringExtra("loginaccount")
        phoneNb = intent.getStringExtra("phoneNb")
        initView()
    }

    override fun onResume() {
        super.onResume()
        getImageValidateCode()
    }

    private fun initView() {
        setSupportActionBar(activityVerificationQuestionBinding!!.toolbar)
        supportActionBar!!.title = "忘记密码"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        TextValidation.setOnFocusChangeListener({ v, hasFocus ->
            if (v == activityVerificationQuestionBinding!!.etPassword && !hasFocus) {
                checkPassword()
            }
        }, activityVerificationQuestionBinding!!.etPassword)

        TextValidation.setOnFocusChangeListener({ v, hasFocus ->
            if (v == activityVerificationQuestionBinding!!.etPasswordAgain && !hasFocus) {
                checkPasswordAgain()
            }
        }, activityVerificationQuestionBinding!!.etPasswordAgain)

        activityVerificationQuestionBinding!!.etExtraCode.addTextChangedListener(textWatcher)
        activityVerificationQuestionBinding!!.forgetSecurityImage.setOnClickListener { v -> getImageValidateCode() }
        activityVerificationQuestionBinding!!.tvNext.setOnClickListener(this)
        activityVerificationQuestionBinding!!.tvGetYzm.setOnClickListener(this)
        activityVerificationQuestionBinding!!.tvExplain.text = loginaccount!! + "，您好！"
        activityVerificationQuestionBinding!!.phoneNumber.text = "您好，请注意查收发往手机" + phoneNb + "的验证码信息。"
    }

    private fun checkPassword() {
        password = activityVerificationQuestionBinding!!.etPassword.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.Companion.toastWarning("密码不能为空!")
            activityVerificationQuestionBinding!!.etPassword.setText("")
        } else if (password!!.length > 30 || password!!.length < 6) {
            ToastUtil.Companion.toastWarning("请输入6-30位的密码!")
            activityVerificationQuestionBinding!!.etPassword.setText("")
        }
    }

    private fun checkPasswordAgain() {
        password_again = activityVerificationQuestionBinding!!.etPasswordAgain.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(password_again)) {
            ToastUtil.Companion.toastWarning("密码不能为空!")
            activityVerificationQuestionBinding!!.etPasswordAgain.setText("")
        } else if (password_again!!.length > 30 || password_again!!.length < 6) {
            ToastUtil.Companion.toastWarning("请输入6-30位的密码!")
            activityVerificationQuestionBinding!!.etPasswordAgain.setText("")
        } else if (password != password_again) {
            ToastUtil.Companion.toastWarning("两次密码不同!")
            activityVerificationQuestionBinding!!.etPasswordAgain.setText("")
        }
    }

    /**
     * 提交注册
     */
    private fun submit() {
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForFindPWD(loginaccount, password!!, verificationKey, yzm)
            post(HttpURLS.userUpdate, true, "FindPWD", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.i(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        ToastUtil.Companion.toastSuccess("密码重置成功，请重新登录!")
                        ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity::class.java)
                    } else {
                        ok = 11
                        getImageValidateCode()
                        ToastUtil.Companion.toastError(ProcessDes)
                    }
                }
            })
        }
    }

    /**
     * 网络获取验证码
     */
    private fun getCaptcha() {
        val to = loginaccount
        val GetType = "02"
        extra_code = activityVerificationQuestionBinding!!.etExtraCode.text.toString().trim { it <= ' ' }

        if (ok == 11 || extra_code == null || extra_code!!.length != 4) {
            ToastUtil.Companion.toastWarning("请先输入正确图形验证码!")
            return
        }

        if (to != null) {
            val body = HttpRequestParams.getParamsForVerificationCode(to, GetType, extra_code, ImageKey)
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
                        activityVerificationQuestionBinding!!.tvGetYzm.startTimer()
                        activityVerificationQuestionBinding!!.phoneNumber.visibility = View.VISIBLE
                        val VerificationCode1 = ResponseFields.getString("VerificationCode")
                        verificationKey = VerificationCode1
                        time = System.currentTimeMillis()
                    } else {
                        ToastUtil.Companion.toastError(ProcessDes)



                    }
                }
            })
        }
    }

    /**
     * 检查输入数据是否合格，并给出提示信息

     * @return
     */
    private fun checkEmpty(): Boolean {
        yzm = activityVerificationQuestionBinding!!.etPhonevalidate.text.toString().trim { it <= ' ' }
        password = activityVerificationQuestionBinding!!.etPassword.text.toString().trim { it <= ' ' }
        password_again = activityVerificationQuestionBinding!!.etPasswordAgain.text.toString().trim { it <= ' ' }
        extra_code = activityVerificationQuestionBinding!!.etExtraCode.text.toString().trim { it <= ' ' }
        if (time == 0L || verificationKey == null) {
            ToastUtil.Companion.toastWarning("请先获取验证码!")
            return false
        } else if (ok == 11 || activityVerificationQuestionBinding!!.etExtraCode.text.length < 4) {
            ToastUtil.toastWarning("请正确输入验证码！")
            return false
        } else if (System.currentTimeMillis() - time > 300 * 1000) {
            ToastUtil.Companion.toastWarning("验证码已失效，请重新获取!")
            return false
        } else if (TextUtils.isEmpty(yzm)) {
            ToastUtil.Companion.toastWarning("请填写验证码!")
            return false
        } else if (TextUtils.isEmpty(password)) {
            ToastUtil.Companion.toastWarning("请输入新的密码!")
            return false
        } else if (TextUtils.isEmpty(password_again)) {
            ToastUtil.Companion.toastWarning("请再次输入新的密码!")
            return false
        } else if (password!!.length > 30 || password!!.length < 6) {
            ToastUtil.Companion.toastWarning("请输入6-30位的密码!")
            return false
        } else if (password != password_again) {
            ToastUtil.Companion.toastWarning("两次密码不同!")
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_next -> submit()
            R.id.tv_get_yzm -> getCaptcha()
        }
    }


    /**
     * 前段获取验证码
     */
    private fun getImageValidateCode() {
        activityVerificationQuestionBinding!!.etExtraCode.setText("")
        resetvaIidateCodeDrawableNull()
        val body = HttpRequestParams.getParamsGetImageValidateCode("1003")
        post(HttpURLS.imageValidateCode, true, "imageValidateCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseFields = response
                        .getJSONObject("ResponseFields")
                val Response = response
                        .getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(
                            Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        ImageKey = ResponseFields.getString("ImageKey")
                        ImageCode = ResponseFields.getString("ImageCode")
                        activityVerificationQuestionBinding!!.forgetSecurityImage.setImageBitmap(getBitmap(ImageCode))
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
        val body = HttpRequestParams.getParamsForValidateCode(activityVerificationQuestionBinding!!.etExtraCode.text.toString(), ImageKey)
        post(HttpURLS.validateCode, true, "validateCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseFields = response
                        .getJSONObject("ResponseFields")
                val Response = response
                        .getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(
                            Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {

                        val VerificationResult = ResponseFields.getString("VerificationResult")//验证结果  10：成功 。11：失败
                        ok = Integer.parseInt(VerificationResult)
                        val VerificationResultDesc = ResponseFields.getString("VerificationResultDesc")//验证结果描述
                        //								toast(VerificationResultDesc);
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

    //重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableNull() {
        activityVerificationQuestionBinding!!.etExtraCode.setCompoundDrawables(activityVerificationQuestionBinding!!.etExtraCode.compoundDrawables[0], activityVerificationQuestionBinding!!.etExtraCode.compoundDrawables[1], null, activityVerificationQuestionBinding!!.etExtraCode.compoundDrawables[3])
    }

    //重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableWrong() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.err_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityVerificationQuestionBinding!!.etExtraCode.setCompoundDrawables(activityVerificationQuestionBinding!!.etExtraCode.compoundDrawables[0], activityVerificationQuestionBinding!!.etExtraCode.compoundDrawables[1], drawable, activityVerificationQuestionBinding!!.etExtraCode.compoundDrawables[3])
    }

    private fun resetvaIidateCodeDrawableRight() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.right_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityVerificationQuestionBinding!!.etExtraCode.setCompoundDrawables(activityVerificationQuestionBinding!!.etExtraCode.compoundDrawables[0], activityVerificationQuestionBinding!!.etExtraCode.compoundDrawables[1], drawable, activityVerificationQuestionBinding!!.etExtraCode.compoundDrawables[3])
    }


    //生成验证码图片
    fun getBitmap(str: String): Bitmap {
        val decode = Base64.decode(str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
        return bitmap
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
            try {
                if (activityVerificationQuestionBinding!!.etExtraCode.text.length == 4) {
                    validateCode()
                } else {
                    resetvaIidateCodeDrawableNull()
                }
            } catch (e: Exception) {
            }

        }
    }

    companion object {

        fun startActivity(context: Context, phone: String, loginaccount: String) {
            val intent = Intent(context, VerificationQuestionActivity::class.java)
            intent.putExtra("phoneNb", phone)
            intent.putExtra("loginaccount", loginaccount)
            context.startActivity(intent)
        }
    }

}
