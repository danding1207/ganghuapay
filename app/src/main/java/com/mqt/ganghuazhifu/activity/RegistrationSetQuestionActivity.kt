package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.databinding.ActivityRegistrationSetQuestionBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import java.util.*


/**
 * 注册 设置安保问题

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class RegistrationSetQuestionActivity : BaseActivity() {

    private var questionList: ArrayList<String>? = null
    private var type: Int = 0// 1:注册;2:安保问题管理;
    private var phone: String? = null
    private var password: String? = null
    private var name: String? = null
    private var answer: String? = null
    private var SafeQuestion: String? = null
    private var invite_code: String? = null
    private var yzm: String? = null
    private var verificationKey: String? = null
    private var activityRegistrationSetQuestionBinding: ActivityRegistrationSetQuestionBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRegistrationSetQuestionBinding = DataBindingUtil.setContentView<ActivityRegistrationSetQuestionBinding>(this, R.layout.activity_registration_set_question)
        type = intent.getIntExtra("TYPE", 1)
        phone = intent.getStringExtra("phone")
        password = intent.getStringExtra("password")
        name = intent.getStringExtra("name")
        invite_code = intent.getStringExtra("invite_code")
        yzm = intent.getStringExtra("VerificationCode")
        verificationKey = intent.getStringExtra("VerificationKey")
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityRegistrationSetQuestionBinding!!.toolbar)
        supportActionBar!!.title = "设置安保问题"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when (type) {
            2 -> {
                supportActionBar!!.title = "重置安保问题"
                activityRegistrationSetQuestionBinding!!.tvTitleRight.visibility = View.VISIBLE
                activityRegistrationSetQuestionBinding!!.tvTitleRight.setOnClickListener(this)
                activityRegistrationSetQuestionBinding!!.linearLayoutContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.main_bg))
                findViewById(R.id.tv_submit).visibility = View.INVISIBLE
            }
        }
        activityRegistrationSetQuestionBinding!!.tvSubmit.setOnClickListener(this)
        questionList = ArrayList<String>()
        questionList!!.add("您身份证的最后的3位")
        questionList!!.add("您的幸运数字")
        questionList!!.add("您就读的中学")
        questionList!!.add("您最喜欢的一部电影")
        questionList!!.add("您心目中的英雄")
        questionList!!.add("您的启蒙老师的名字")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
                questionList!!)
        adapter.setDropDownViewResource(R.layout.activity_registration_set_question_item)
        activityRegistrationSetQuestionBinding!!.spinner.adapter = adapter
    }

    private fun submit2() {
        if (checkEmpty()) {
            val user = EncryptedPreferencesUtils.getUser()
            val body = HttpRequestParams.getParamsForSetNewQuestion(user.LoginAccount, SafeQuestion,
                    answer, password!!.trim { it <= ' ' }, verificationKey, yzm)
            post(HttpURLS.userUpdate, true, "SetNewQuestion", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        when (type) {
                            1 -> ToastUtil.toastSuccess("注册成功!")
                            2 -> ToastUtil.toastSuccess("重置成功!")
                        }
                        ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity::class.java)
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        }
    }

    /**
     * 提交注册
     */
    private fun submit() {
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForRegistration(phone, password!!, name, null, verificationKey, yzm)
            post(HttpURLS.registration, true, "Registration", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val Response = response.getString("ResponseHead")
                    if (Response != null) {
                        val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                        if (head != null && head.ProcessCode == "0000") {
                            ToastUtil.toastSuccess("注册成功!")
                            ScreenManager.getScreenManager().popAllActivity()
                            startActivity(
                                    Intent(this@RegistrationSetQuestionActivity, LoginActivity::class.java))
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

    /**
     * 检查输入数据是否合格，并给出提示信息

     * @return
     */
    private fun checkEmpty(): Boolean {
        answer = activityRegistrationSetQuestionBinding!!.etAnswer.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(answer)) {
            ToastUtil.toastWarning("请输入安保问题答案!")
            return false
        }
        if (activityRegistrationSetQuestionBinding!!.spinner.selectedItemId == Spinner.INVALID_ROW_ID) {
            ToastUtil.toastWarning("请选择安保问题!")
            return false
        }
        when (activityRegistrationSetQuestionBinding!!.spinner.selectedItemId.toInt()) {
            0 -> SafeQuestion = "01"
            1 -> SafeQuestion = "02"
            2 -> SafeQuestion = "03"
            3 -> SafeQuestion = "04"
            4 -> SafeQuestion = "05"
            5 -> SafeQuestion = "06"
        }
        Logger.d("SafeQuestion---->" + SafeQuestion!!)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_submit -> submit()
            R.id.tv_title_right -> submit2()
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

}
