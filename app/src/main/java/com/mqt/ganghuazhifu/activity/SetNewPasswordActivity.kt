package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivitySetNewPasswordBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.TextValidation
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

/**
 * 设置新密码

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class SetNewPasswordActivity : BaseActivity() {

    private var password: String? = null
    private var password_again: String? = null
    private var type: Int = 0// 1:注册;2:管理;3:忘记密码;
    private var OldPwd: String? = null
    private var answer: String? = null
    private var loginaccount: String? = null
    private var verificationKey: String? = null
    private var yzm: String? = null

    private var activitySetNewPasswordBinding: ActivitySetNewPasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySetNewPasswordBinding = DataBindingUtil.setContentView<ActivitySetNewPasswordBinding>(this,
                R.layout.activity_set_new_password)
        type = intent.getIntExtra("TYPE", 1)
        answer = intent.getStringExtra("answer")
        loginaccount = intent.getStringExtra("loginaccount")
        yzm = intent.getStringExtra("VerificationCode")
        verificationKey = intent.getStringExtra("VerificationKey")
        initView()
    }

    private fun initView() {
        setSupportActionBar(activitySetNewPasswordBinding!!.toolbar)
        supportActionBar!!.title = "设置新密码"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        TextValidation.setRegularValidation(this, activitySetNewPasswordBinding!!.etPassword)
        TextValidation.setRegularValidation(this, activitySetNewPasswordBinding!!.etPasswordAgain)

        activitySetNewPasswordBinding!!.tvSubmit.setOnClickListener(this)
        when (type) {
            3, 1 -> findViewById(R.id.view_1).visibility = View.VISIBLE
            2 -> {
                findViewById(R.id.view_1).visibility = View.GONE
                activitySetNewPasswordBinding!!.tvTitleRight.visibility = View.VISIBLE
                activitySetNewPasswordBinding!!.tvTitleRight.setOnClickListener(this)
                activitySetNewPasswordBinding!!.etPassword.hint = "请输入新的密码"
                activitySetNewPasswordBinding!!.etPasswordAgain.hint = "请确认新的密码"
                findViewById(R.id.linearLayout_setpassword_container).setBackgroundColor(ContextCompat.getColor(this, R.color.main_bg))
                findViewById(R.id.tv_submit).visibility = View.INVISIBLE
            }
        }
    }

    /**
     * 提交注册
     */
    private fun submit() {
        if (checkEmpty()) {
            val user = EncryptedPreferencesUtils.getUser()
            val body = HttpRequestParams.getParamsForFindPWD(
                    user.LoginAccount, password!!, verificationKey, yzm)
            post(HttpURLS.userUpdate, true, "SetNewPWD", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.i(response.toString())
                    val ResponseHead = response
                            .getJSONObject("ResponseHead")
                    val ProcessCode = ResponseHead
                            .getString("ProcessCode")
                    val ProcessDes = ResponseHead
                            .getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        ScreenManager.getScreenManager()
                                .popAllActivityExceptOne(
                                        MainActivity::class.java)
                        ToastUtil.toastSuccess("修改成功!")
                    } else {
                        ToastUtil.toastError(ProcessDes)
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
        password = activitySetNewPasswordBinding!!.etPassword.text.toString().trim { it <= ' ' }
        password_again = activitySetNewPasswordBinding!!.etPasswordAgain.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.toastWarning("密码不能为空!")
            return false
        } else if (password!!.length > 30 || password!!.length < 6) {
            ToastUtil.toastWarning("请输入6-30位的密码!")
            return false
        } else if (password != password_again) {
            ToastUtil.toastWarning("两次密码不同!")
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_submit -> submit2()
            R.id.tv_title_right -> submit()
        }
    }

    private fun submit2() {
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForFindPWD(
                    loginaccount, password!!, verificationKey, yzm)
            post(HttpURLS.userUpdate, true, "FindPWD", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response
                            .getJSONObject("ResponseHead")
                    val ProcessCode = ResponseHead
                            .getString("ProcessCode")
                    val ProcessDes = ResponseHead
                            .getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        ToastUtil.toastSuccess("注册成功，请登录!")
                        ScreenManager.getScreenManager()
                                .popAllActivityExceptOne(
                                        LoginActivity::class.java)
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
