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
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.databinding.ActivityForgetPasswordBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

/**
 * 忘记密码

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class ForgetPasswordActivity : BaseActivity() {

    private var phone: String? = null

    private var activityForgetPasswordBinding: ActivityForgetPasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityForgetPasswordBinding = DataBindingUtil.setContentView<ActivityForgetPasswordBinding>(this,
                R.layout.activity_forget_password)
        initView()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView() {
        setSupportActionBar(activityForgetPasswordBinding!!.toolbar)
        supportActionBar!!.title = "忘记密码"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        activityForgetPasswordBinding!!.tvNext.setOnClickListener(this)
    }



    /**
     * 提交注册
     */
    private fun submit() {
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForAccountInfo(phone!!, "1001000004")
            post(HttpURLS.processQuery, true, "AccountInfo", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        val QryResults = ResponseFields.getJSONObject("QryResults")
                        val safequestion = QryResults.getString("safequestion")
                        val safeanswer = QryResults.getString("safeanswer")
                        val loginaccount = QryResults.getString("loginaccount")
                        val phoneNb = QryResults.getString("phonenb")
                        VerificationQuestionActivity.startActivity(this@ForgetPasswordActivity, phone!!,
                                loginaccount)
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
        phone = activityForgetPasswordBinding!!.etPhone.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.toastWarning("请输入登录名！")
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
