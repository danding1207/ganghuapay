package com.mqt.ganghuazhifu.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.User
import com.mqt.ganghuazhifu.databinding.ActivityVerifyPasswordBinding
import com.mqt.ganghuazhifu.utils.Encrypt
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.TextValidation
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import java.util.*

/**
 * 验证密码（密码&手势密码）

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class VerifyPasswordActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var type: Int = 1// 1:安保问题管理;2:修改登录密码;3:修改手势密码;4:待定;
    private var password: String? = null
    private var activityVerifyPasswordBinding: ActivityVerifyPasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVerifyPasswordBinding = DataBindingUtil.setContentView<ActivityVerifyPasswordBinding>(this, R.layout.activity_verify_password)
        type = intent.getIntExtra("TYPE", 1)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityVerifyPasswordBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        TextValidation.setRegularValidation(this, activityVerifyPasswordBinding!!.etPassword)
        when (type) {
            1 -> {
                supportActionBar!!.title = "安保问题管理"
                activityVerifyPasswordBinding!!.tvExplain.text = "您已经设置了安保问题，修改安保需要重新验证您的手机与登录密码"
            }
            2 -> {
                supportActionBar!!.title = "登录密码修改"
                activityVerifyPasswordBinding!!.tvExplain.text = "您好！填写下面信息完成密码修改"
            }
            3 -> {
                supportActionBar!!.title = "用密码修改"
                activityVerifyPasswordBinding!!.tvExplain.visibility = View.GONE
                findViewById(R.id.view_1).visibility = View.GONE
            }
        }
        activityVerifyPasswordBinding!!.tvTitleRight.setOnClickListener(this)
    }

    /**
     * 提交注册
     */
    private fun submit() {
        if (checkEmpty()) {
            val user: User = EncryptedPreferencesUtils.getUser()
            val accountManager = AccountManager.get(this)
            val accounts: Array<Account> = accountManager.getAccountsByType(resources.getString(R.string.account_type))
            if (accounts != null && accounts!!.isNotEmpty()) {
                var myAccount: Account? = null
                accounts!!.forEach { account ->
                    if (account.name == user.LoginAccount || account.name == user.PhoneNb) {
                        myAccount = account
                    }
                }
                if (myAccount != null) {
                    Logger.i("PasswordStatus:" + accountManager.getUserData(myAccount, "PasswordStatus"))
                    Logger.i("PasswordNum:" + accountManager.getUserData(myAccount, "PasswordNum"))
                    var passworded = accountManager.getPassword(myAccount)
                    Logger.i("passworded:" + passworded)
                    password = Encrypt.SHA256(Encrypt.MD5(password).toUpperCase(Locale.CHINA) + Encrypt.MD5(password).toUpperCase(Locale.CHINA).substring(0, 6)).toUpperCase(Locale.CHINA)
                    if (password.equals(passworded)) {
                        val intent: Intent
                        when (type) {
                        // 1:安保问题管理;2:修改登录密码;3:修改手势密码;4:待定;
                            1 -> VerifyPhoneActivity.startActivity(this@VerifyPasswordActivity, 3, user.PhoneNb)
                            2 -> VerifyPhoneActivity.startActivity(this@VerifyPasswordActivity, 4, user.PhoneNb)
                            3 -> {
                                intent = Intent(
                                        this@VerifyPasswordActivity,
                                        LockPatternActivity::class.java)
                                intent.putExtra("TYPE", 2)
                                intent.putExtra("phoneNb", user.PhoneNb)
                                startActivity(intent)
                            }
                        }
                    } else {
                        ToastUtil.Companion.toastError("密码输入错误！")
                    }
                }
            }
        }
    }

    /**
     * 检查输入数据是否合格，并给出提示信息

     * @return
     */
    private fun checkEmpty(): Boolean {
        password = activityVerifyPasswordBinding!!.etPassword.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.Companion.toastWarning("密码不能为空!")
            return false
        } else if (password!!.length > 30 || password!!.length < 6) {
            ToastUtil.Companion.toastWarning("请输入6-30位的密码!")
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_title_right -> submit()
        }
    }

    companion object {
        fun startActivity(context: Context, type: Int) {
            val intent = Intent(context, VerifyPasswordActivity::class.java)
            intent.putExtra("TYPE", type)
            context.startActivity(intent)
        }
    }

}
