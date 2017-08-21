package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivitySetEmailBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.mqt.ganghuazhifu.utils.VerifyUtils
import com.orhanobut.logger.Logger
import java.util.regex.Pattern

/**
 * 设置手机号码

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class SetPhoneActivity : BaseActivity() {

    private var phone: String? = null
    private var verificationKey: String? = null
    private var yzm: String? = null
    private var activitySetEmailBinding: ActivitySetEmailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySetEmailBinding = DataBindingUtil.setContentView<ActivitySetEmailBinding>(this, R.layout.activity_set_email)
        yzm = intent.getStringExtra("VerificationCode")
        verificationKey = intent.getStringExtra("VerificationKey")
        initView()
    }

    private fun initView() {
        setSupportActionBar(activitySetEmailBinding!!.toolbar)
        supportActionBar!!.title = "修改手机号码"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activitySetEmailBinding!!.tvTitleRight.visibility = View.VISIBLE
        activitySetEmailBinding!!.tvTitleRight.setOnClickListener(this)
        activitySetEmailBinding!!.etSetEmail.inputType = InputType.TYPE_CLASS_NUMBER // 调用数字键盘
        activitySetEmailBinding!!.etSetEmail.maxEms = 11
        activitySetEmailBinding!!.etSetEmail.hint = "请输入您的手机号码"
        activitySetEmailBinding!!.tvSetInfoExplain.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_title_right -> submit()
        }
    }

    private fun checkEmpty(): Boolean {
        phone = activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.toastWarning("请填写手机号!")
            return false
        } else if (!VerifyUtils.isMobileNO(phone)) {
            ToastUtil.toastWarning("请填写正确手机号!")
            return false
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
                        finish()
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
