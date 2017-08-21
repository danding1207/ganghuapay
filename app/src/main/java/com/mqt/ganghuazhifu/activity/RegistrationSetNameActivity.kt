package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.databinding.ActivityRegistrationSetNameBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.TextValidation
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

/**
 * 注册 设置用户名

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class RegistrationSetNameActivity : BaseActivity() {

    private var phone: String? = null
    private var password: String? = null
    private var name: String? = null
    private var invite_code: String? = null
    private var activityRegistrationSetNameBinding: ActivityRegistrationSetNameBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRegistrationSetNameBinding = DataBindingUtil.setContentView<ActivityRegistrationSetNameBinding>(this, R.layout.activity_registration_set_name)
        phone = intent.getStringExtra("phone")
        password = intent.getStringExtra("password")
        invite_code = intent.getStringExtra("invite_code")
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityRegistrationSetNameBinding!!.toolbar)
        supportActionBar!!.title = "设置登录名"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        TextValidation.setRegularValidationName(this, activityRegistrationSetNameBinding!!.etName)
        activityRegistrationSetNameBinding!!.tvNext.setOnClickListener(this)
    }

    /**
     * 提交注册
     */
    private fun submit() {
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForcheckData(name, "01")
            post(HttpURLS.checkData, true, "checkData", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val Response = response
                            .getString("ResponseHead")
                    if (Response != null) {
                        val head = JSONObject.parseObject(
                                Response, ResponseHead::class.java)

                        if (head != null && head.ProcessCode == "0000") {

                            val intent = Intent(this@RegistrationSetNameActivity, RegistrationSetQuestionActivity::class.java)
                            intent.putExtra("phone", phone)
                            intent.putExtra("name", name)
                            intent.putExtra("password", password)
                            intent.putExtra("invite_code", invite_code)
                            startActivity(intent)
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
        name = activityRegistrationSetNameBinding!!.etName.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(name)) {
            ToastUtil.toastWarning("请输入登录名!")
            return false
        } else if (name!!.length < 6) {
            ToastUtil.toastWarning("请输入至少6位登录名!")
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
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
