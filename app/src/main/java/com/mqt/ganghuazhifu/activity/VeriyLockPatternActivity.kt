package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityLockPatternBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.MD5Util
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import java.util.*

/**
 * 手势锁界面

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class VeriyLockPatternActivity : BaseActivity() {

    private var guestPwd: String = ""
    private var guestPwdAgain: String? = null
    private var vibrator: Vibrator? = null

    private var activityLockPatternBinding: ActivityLockPatternBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLockPatternBinding = DataBindingUtil.setContentView<ActivityLockPatternBinding>(this, R.layout.activity_lock_pattern)

        initView()
    }

    private fun initView() {
        /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(100, 400, 100, 400)   // 停止 开启 停止 开启
        //        重复两次上面的pattern 如果只想震动一次，index设为-1

        setSupportActionBar(activityLockPatternBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar!!.title = "用原手势修改"
        activityLockPatternBinding!!.tvTitleRight.visibility = View.VISIBLE
        activityLockPatternBinding!!.tvTitleRight.text = "下一步"

        activityLockPatternBinding!!.tvTitleRight.setOnClickListener { v ->
            if (checkEmpty()) {
                veriy(guestPwd)
            }
        }

        activityLockPatternBinding!!.tvEplainer.visibility = View.INVISIBLE

        activityLockPatternBinding!!.lockPatternView.setOnRecordListener { isRecord ->
            if (isRecord == null || isRecord.length < 2) {
                activityLockPatternBinding!!.lockPatternView.clearPattern()
                ToastUtil.toastWarning("请至少连接2个点！")
            } else {
                guestPwd = isRecord
            }
        }
    }

    fun veriy(isRecord: String) {
        Logger.i(isRecord)
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForValidateGestureCode(user.LoginAccount, isRecord)
        post(HttpURLS.validateVerificationCode, true, "ValidateGestureCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    val intent = Intent(this@VeriyLockPatternActivity, LockPatternActivity::class.java)

                    startActivity(intent)
                } else {
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    private fun checkEmpty(): Boolean {
        if (guestPwd == "") {
            ToastUtil.toastWarning("请先滑动画出自己的手势密码！")
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

    override fun OnViewClick(v: View) {}

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
