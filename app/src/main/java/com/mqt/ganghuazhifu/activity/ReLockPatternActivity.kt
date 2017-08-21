package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Vibrator
import android.view.KeyEvent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityReLockPatternBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
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
 * 手势锁解锁界面

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class ReLockPatternActivity : BaseActivity() {
    private var count = 5
    private var type: Int = 0
    private var vibrator: Vibrator? = null
    private var fristPwd: String? = null
    private var secondPwd: String? = null
    internal val pattern = longArrayOf(100, 400, 100, 400)   // 停止 开启 停止 开启
    private var activityReLockPatternBinding: ActivityReLockPatternBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityReLockPatternBinding = DataBindingUtil.setContentView<ActivityReLockPatternBinding>(this, R.layout.activity_re_lock_pattern)
        type = intent.getIntExtra("TYPE", OTHER)
        initView()
    }

    private fun initView() {

        /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        //重复两次上面的pattern 如果只想震动一次，index设为-1
        when (type) {
            FRIST -> {
                activityReLockPatternBinding!!.tvExplainLock.visibility = View.VISIBLE
                activityReLockPatternBinding!!.tvPwdLogin.visibility = View.INVISIBLE
                activityReLockPatternBinding!!.tvExplainLock.text = "请先设置手势密码"
                activityReLockPatternBinding!!.lockPatternView.setOnRecordListener { isRecord ->
                    if (isRecord == null || isRecord.length < 2) {
                        activityReLockPatternBinding!!.lockPatternView.clearPattern()
                        ToastUtil.toastWarning("请至少连接2个点!")
                        vibrator!!.vibrate(pattern, -1)
                    } else {
                        if (fristPwd == null) {
                            fristPwd = isRecord
                            activityReLockPatternBinding!!.lockPatternView.clearPattern()
                            activityReLockPatternBinding!!.tvExplainLock.text = "再次确认手势密码"
                        } else {
                            secondPwd = isRecord
                            if (secondPwd == fristPwd) {
                                submit()
                            } else {
                                fristPwd = null
                                secondPwd = null
                                activityReLockPatternBinding!!.lockPatternView.clearPattern()
                                activityReLockPatternBinding!!.tvExplainLock.text = "两次密码不同，请重新设置手势密码"
                                vibrator!!.vibrate(pattern, -1)
                            }
                        }
                    }
                }
            }
            OTHER -> {
                activityReLockPatternBinding!!.tvExplainLock.visibility = View.INVISIBLE
                activityReLockPatternBinding!!.lockPatternView.setOnRecordListener { isRecord ->
                    if (isRecord == null || isRecord.length < 2) {
                        activityReLockPatternBinding!!.lockPatternView.clearPattern()
                        ToastUtil.toastWarning("请至少连接2个点!")
                        vibrator!!.vibrate(pattern, -1)
                    } else {
                        veriy(isRecord)
                    }
                }
            }
        }
        activityReLockPatternBinding!!.tvPwdLogin.setOnClickListener(this)
    }

    private fun setNewPWD() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForSetNewGeustPWD(
                user.LoginAccount, "x")
        post(HttpURLS.userUpdate, true, "SetNewGeustPWD", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response
                        .getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead
                        .getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    EncryptedPreferencesUtils.setGesturePwd(MD5Util.getMD5String("x").toUpperCase(Locale.CHINA))
                } else {
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    private fun showDialog() {
        MaterialDialog.Builder(this)
                .title("手势密码已失效！")
                .content("请重新登录，登录后可以在账户管理界面管理手势密码。")
                .onPositive { dialog, which ->
                    startActivity(Intent(this@ReLockPatternActivity, LoginActivity::class.java))
                }
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .positiveText("确定")
                .show()
    }

    private fun submit() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForSetNewGeustPWD(
                user.LoginAccount, secondPwd!!)
        post(HttpURLS.userUpdate, true, "SetNewGeustPWD", body, OnHttpRequestListener { isError, response, type, error ->
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
                    EncryptedPreferencesUtils.setGesturePwd(MD5Util.getMD5String(secondPwd).toUpperCase(Locale.CHINA))
                    finish()
                } else {
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    private fun veriy(isRecord: String) {
        Logger.d(isRecord)
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForValidateGestureCode(user.LoginAccount, isRecord)
        post(HttpURLS.validateVerificationCode, true, "ValidateGestureCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                ToastUtil.toastError("请求失败，请重试！")
                activityReLockPatternBinding!!.tvExplainLock.visibility = View.VISIBLE
                vibrator!!.vibrate(pattern, -1)
                if (count > 0) {
                    activityReLockPatternBinding!!.tvExplainLock.text = "解锁错误，还有" + count + "次机会"
                    activityReLockPatternBinding!!.lockPatternView.clearPattern()
                } else {
                    activityReLockPatternBinding!!.tvExplainLock.text = "解锁错误超过5次，已锁定请用密码登录"
                    activityReLockPatternBinding!!.lockPatternView.clearPattern()
                    activityReLockPatternBinding!!.lockPatternView.disableInput()
                    setNewPWD()
                    showDialog()
                }
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    finish()
                } else {
                    count--
                    activityReLockPatternBinding!!.tvExplainLock.visibility = View.VISIBLE
                    vibrator!!.vibrate(pattern, -1)
                    if (count > 0) {
                        activityReLockPatternBinding!!.tvExplainLock.text = "解锁错误，还有" + count + "次机会"
                        activityReLockPatternBinding!!.lockPatternView.clearPattern()
                    } else {
                        activityReLockPatternBinding!!.tvExplainLock.text = "解锁错误超过5次，已锁定请用密码登录"
                        activityReLockPatternBinding!!.lockPatternView.clearPattern()
                        activityReLockPatternBinding!!.lockPatternView.disableInput()
                        setNewPWD()
                        showDialog()
                    }
                    //                    App.Companion.toast(ProcessDes);
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        vibrator!!.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true
                ToastUtil.toastInfo(resources.getString(R.string.exit))
                mHandler.sendEmptyMessageDelayed(0, 5000)
            } else {
                ScreenManager.getScreenManager().popAllActivity()
                System.exit(0)
            }
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_pwd_login -> {
                startActivity(Intent(this@ReLockPatternActivity, LoginActivity::class.java))
            }
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean(STATE_EXIT, isExit)
        savedInstanceState.putInt(STATE_count, count)
        savedInstanceState.putInt(STATE_type, type)
        savedInstanceState.putString(STATE_fristPwd, fristPwd)
        savedInstanceState.putString(STATE_secondPwd, secondPwd)
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
        isExit = savedInstanceState.getBoolean(STATE_EXIT)
        count = savedInstanceState.getInt(STATE_count)
        type = savedInstanceState.getInt(STATE_type)
        fristPwd = savedInstanceState.getString(STATE_fristPwd)
        secondPwd = savedInstanceState.getString(STATE_secondPwd)
        if (vibrator == null)
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    companion object {

        private var isExit = false
        val FRIST = 0x01
        val OTHER = 0x02

        internal var mHandler: Handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                isExit = false
            }
        }

        private val STATE_EXIT = "isExit"
        private val STATE_count = "count"
        private val STATE_type = "type"
        private val STATE_fristPwd = "fristPwd"
        private val STATE_secondPwd = "secondPwd"
    }
}
