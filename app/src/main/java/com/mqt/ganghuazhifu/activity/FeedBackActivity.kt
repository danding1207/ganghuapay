package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityFeedBackBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

/**
 * 意见反馈

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class FeedBackActivity : BaseActivity() {

    private var content: String? = null
    private var titles: String? = null
    private var activityFeedBackBinding: ActivityFeedBackBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFeedBackBinding = DataBindingUtil.setContentView<ActivityFeedBackBinding>(this,
                R.layout.activity_feed_back)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityFeedBackBinding!!.toolbar)
        supportActionBar!!.title = "意见反馈"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityFeedBackBinding!!.tvTitleRight.setOnClickListener(this)
        activityFeedBackBinding!!.etContent.setOnEditorActionListener { v, actionId, event -> event.keyCode == KeyEvent.KEYCODE_ENTER }
        activityFeedBackBinding!!.etContent.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                activityFeedBackBinding!!.tvContentNum.text = String.format(getString(R.string.feed_back_num_tip), s.toString().length.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun submit() {
        if (checkEmpty()) {
            val user = EncryptedPreferencesUtils.getUser()
            val body = HttpRequestParams.getParamsForFeedBack(user.LoginAccount, titles, content,
                    user.PhoneNb)
            post(HttpURLS.feedback, true, "FeedBack", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.i(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        ToastUtil.toastSuccess("提交成功!")
                        finish()
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        }
    }

    private fun checkEmpty(): Boolean {
        content = activityFeedBackBinding!!.etContent.text.toString().trim { it <= ' ' }
        titles = activityFeedBackBinding!!.etTitle.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(titles)) {
            ToastUtil.toastWarning("请输入您的意见标题!")
            return false
        } else if (TextUtils.isEmpty(content)) {
            ToastUtil.toastWarning("请输入您的反馈意见!")
            return false
        }
        return true
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_title_right -> submit()
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
