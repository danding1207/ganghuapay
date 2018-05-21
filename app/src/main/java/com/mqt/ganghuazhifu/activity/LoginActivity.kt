package com.mqt.ganghuazhifu.activity

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.github.mzule.activityrouter.annotation.Router
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.LoginCountListAdapter
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.bean.User
import com.mqt.ganghuazhifu.databinding.ActivityLoginBinding
import com.mqt.ganghuazhifu.ext.Klog
import com.mqt.ganghuazhifu.ext.isNotEmpty
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.utils.*
import com.orhanobut.logger.Logger
import java.util.*

/**
 * 登录界面
 * @author yang.lei
 * @date 2014-12-24
 */
@Router("login")
class LoginActivity : BaseActivity(), OnRecyclerViewItemClickListener, LoginCountListAdapter.OnItemClickListener {
    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString("userName", userName)
        savedInstanceState.putString("password", password)
        savedInstanceState.putBoolean("isRemind", isRemind)
        savedInstanceState.putInt("length", length)
        savedInstanceState.putString("ImageKey", ImageKey)
        savedInstanceState.putString("ImageCode", ImageCode)
        savedInstanceState.putString("Title", Title)
        savedInstanceState.putString("Content", Content)
        savedInstanceState.putString("NoticeDate", NoticeDate)
        savedInstanceState.putString("NoticeType", NoticeType)
        savedInstanceState.putInt("ok", ok)
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
        userName = savedInstanceState.getString("userName")
        password = savedInstanceState.getString("password")
        isRemind = savedInstanceState.getBoolean("isRemind")
        length = savedInstanceState.getInt("length")
        ImageKey = savedInstanceState.getString("ImageKey")
        ImageCode = savedInstanceState.getString("ImageCode")
        Title = savedInstanceState.getString("Title")
        Content = savedInstanceState.getString("Content")
        NoticeDate = savedInstanceState.getString("NoticeDate")
        NoticeType = savedInstanceState.getString("NoticeType")
        ok = savedInstanceState.getInt("ok")
    }

    private var userName: String? = null
    private var password: String? = null
    private var isRemind = false
    private var length: Int = 0
    private var ImageKey = ""
    private var ImageCode = ""
    private var Title: String? = null
    private var Content: String? = null
    private var NoticeDate: String? = null
    private var NoticeType: String? = null

    private var AndroidV: String? = null
    private var IosV: String? = null

    private var ok = 11
    private var accounts: Array<Account>? = null
    private var adapter: LoginCountListAdapter? = null
    private var activityLoginBinding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = DataBindingUtil.setContentView<ActivityLoginBinding>(this,
                R.layout.activity_login)
        initPermissions()
        initView()
    }

    internal var ACCESS_GET_ACCOUNTS = 10
    private fun initPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), ACCESS_GET_ACCOUNTS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESS_GET_ACCOUNTS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return
            }
        }
    }


    private fun initView() {
        activityLoginBinding!!.securityCode.addTextChangedListener(textWatcher)
        activityLoginBinding!!.securityImage.setOnClickListener { v -> getImageValidateCode() }
        TextValidation.setRegularValidation(this, activityLoginBinding!!.etPassword)
        activityLoginBinding!!.etName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                isRemind = false
                activityLoginBinding!!.etPassword.text = null
            }
        })
        activityLoginBinding!!.etPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                isRemind = false
            }
        })

        activityLoginBinding!!.tvRegis.setOnClickListener(this)
        activityLoginBinding!!.tvLogin.setOnClickListener(this)
        activityLoginBinding!!.tvForgetPassword.setOnClickListener(this)
        activityLoginBinding!!.ivWeixin.setOnClickListener(this)
        activityLoginBinding!!.ivQq.setOnClickListener(this)
        activityLoginBinding!!.ivAlipay.setOnClickListener(this)
        val user = EncryptedPreferencesUtils.getUser()
        val accountManager = AccountManager.get(this@LoginActivity)
        accounts = accountManager.getAccountsByType(resources.getString(R.string.account_type))
        if (accounts != null && accounts!!.isNotEmpty()) {
            var myAccount: Account? = null
            accounts!!.forEach { account ->
                if (account.name == user.LoginAccount || account.name == user.PhoneNb) {
                    myAccount = account
                }
            }
            if (myAccount != null) {
                if (isNotEmpty(myAccount!!.name)) {
                    activityLoginBinding!!.etName.setText(myAccount!!.name)
                    activityLoginBinding!!.etName.setSelection(myAccount!!.name!!.length)
                }
                Logger.i("PasswordStatus:" + accountManager.getUserData(myAccount, "PasswordStatus"))
                Logger.i("PasswordNum:" + accountManager.getUserData(myAccount, "PasswordNum"))
                if ("true".equals(accountManager.getUserData(myAccount, "PasswordStatus"), true)) {
                    password = accountManager.getPassword(myAccount)

                    if (password != null && password!!.length == 32) {
                        password = Encrypt.SHA256(password
                                + password!!.substring(0, 6)).toUpperCase(Locale.CHINA)
                    }

                    val stringBuffer = StringBuffer()
                    length = Integer.parseInt(accountManager.getUserData(myAccount, "PasswordNum"))
                    for (i in 0..length - 1) {
                        stringBuffer.append("0")
                    }
                    activityLoginBinding!!.etPassword.setText(stringBuffer.toString())
                    activityLoginBinding!!.etPassword.setSelection(stringBuffer.toString().length)
                    isRemind = true
                    activityLoginBinding!!.checkBoxRemenberPassword.isChecked = true
                } else {
                    activityLoginBinding!!.checkBoxRemenberPassword.isChecked = false
                }
            }
            val list = ArrayList<String>()
            accounts!!.forEach { account -> list.add(account.name) }
            adapter = LoginCountListAdapter()
            activityLoginBinding!!.etName.setAdapter(adapter)
            adapter!!.updateList(list)
            adapter!!.setOnItemClickListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        getImageValidateCode()
        getNoticeNewsQuery()
    }

    override fun onItemClick(view: View?, position: Int) {

    }

    private fun checkEmpty(): Boolean {
        userName = activityLoginBinding!!.etName.text.toString().trim { it <= ' ' }

        if (!isRemind) {
            password = activityLoginBinding!!.etPassword.text.toString().trim { it <= ' ' }
            length = password!!.length


            if (TextUtils.isEmpty(password) || length == 0) {
                ToastUtil.Companion.toastWarning("请输入您的密码!")
                return false
            } else if (length > 30 || length < 6) {
                ToastUtil.Companion.toastWarning("请输入6-30位的密码!")
                return false
            }

//            password = Encrypt.MD5(password).toUpperCase(Locale.CHINA)

            Logger.i("length--->" + length)
            Logger.e(Encrypt.MD5(password).toUpperCase(Locale.CHINA))

            password = Encrypt.SHA256(Encrypt.MD5(password).toUpperCase(Locale.CHINA)
                    + Encrypt.MD5(password).toUpperCase(Locale.CHINA).substring(0, 6)).toUpperCase(Locale.CHINA)

            Logger.e(MD5Util.getMD5String(password).toUpperCase(Locale.CHINA).substring(0, 6))
            Logger.e(password.toString())

        }

        if (TextUtils.isEmpty(userName)) {
            ToastUtil.Companion.toastWarning("请输入您的手机号/会员号/邮箱!")
            return false
        }
        if (TextUtils.isEmpty(password) || length == 0) {
            ToastUtil.Companion.toastWarning("请输入您的密码!")
            return false
        } else if (length > 30 || length < 6) {
            ToastUtil.Companion.toastWarning("请输入6-30位的密码!")
            return false
        }
        if (ok == 11) {
            ToastUtil.Companion.toastWarning("请正确输入验证码!")
            return false
        }

        return true
    }

    /**
     * 提交登录
     */
    private fun submit() {
        if (checkEmpty()) {
            val baiduPushData = EncryptedPreferencesUtils.getBaiduPushData()
            Logger.d(baiduPushData.toString())
            val body = HttpRequestParams.getParamsForLogin(userName, password, baiduPushData.appid,
                    baiduPushData.userId, baiduPushData.channelId, "10", activityLoginBinding!!.securityCode.text.toString(),
                    ImageKey)
            Logger.i(body.toString())
            post(HttpURLS.login, true, "Login", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.i(response.toString())
                    val ResponseFields = response.getString("ResponseFields")
                    var user = User()
                    if (ResponseFields != null) {
                        user = JSONObject.parseObject(ResponseFields, User::class.java)
                    }

                    Logger.i(user.toString())

                    val Response = response.getString("ResponseHead")
                    if (Response != null) {
                        val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                        if (head != null && head.ProcessCode == "0000") {

                            ToastUtil.toastSuccess("登录成功!")
                            DataBaiduPush.setPushStatus(user.PushStatus)
                            if (activityLoginBinding!!.checkBoxRemenberPassword.isChecked) {
                                user.Password = password
                                user.PasswordStatus = true
                                user.PasswordNum = length
                            } else {
                                user.Password = password
                                user.PasswordStatus = false
                                user.PasswordNum = length
                            }

                            val accountManager = AccountManager.get(this@LoginActivity)
                            val accounts = accountManager.getAccountsByType(resources.getString(R.string.account_type))
                            var myAccount: Account? = null
                            for (account in accounts) {
                                if (account.name.equals(userName, true)) {
                                    myAccount = account
                                }
                            }
                            if (myAccount == null) {
                                myAccount = Account(userName, resources.getString(R.string.account_type))
                                accountManager.addAccountExplicitly(myAccount, password, null)
                                accountManager.setUserData(myAccount, "PasswordStatus", user.PasswordStatus.toString())
                                accountManager.setUserData(myAccount, "PasswordNum", user.PasswordNum.toString())
                            } else {
                                accountManager.setPassword(myAccount, password)
                                accountManager.setUserData(myAccount, "PasswordStatus", user.PasswordStatus.toString())
                                accountManager.setUserData(myAccount, "PasswordNum", user.PasswordNum.toString())
                            }
                            EncryptedPreferencesUtils.setUser(user)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        } else {
                            if (head != null && head.ProcessDes != null) {
                                ToastUtil.toastError(head.ProcessDes)
                                getImageValidateCode()
                            }
                        }
                    }
                }
            })
        }
    }

    /**
     * 通知新闻查询
     */
    private fun getNoticeNewsQuery() {
        val body = HttpRequestParams.getParamsNoticeNewsQuery("10")
        post(HttpURLS.noticeNewsQuery, false, "noticeNewsQuery", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseFields = response.getJSONObject("ResponseFields")
                val Response = response.getString("ResponseHead")
                if (ResponseFields != null && Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        Title = ResponseFields.getString("Title")
                        Content = ResponseFields.getString("Content")
                        NoticeDate = ResponseFields.getString("NoticeDate")
                        NoticeType = ResponseFields.getString("NoticeType")

                        AndroidV = ResponseFields.getString("AndroidV")
                        IosV = ResponseFields.getString("IosV")

                        if(TextUtils.isEmpty(AndroidV)) {
                            if ("11" == NoticeType) {
                                MaterialDialog.Builder(this@LoginActivity)
                                        .title(Title!!)
                                        .canceledOnTouchOutside(false)
                                        .cancelable(false)
                                        .content("\t\t\t\t" + Content + "\n\t\t\t\t" + NoticeDate)
                                        .positiveText("关闭").show()
                            } else if ("12" == NoticeType) {
                                MaterialDialog.Builder(this@LoginActivity).title(Title!!).onPositive { dialog, which ->
                                    ScreenManager.getScreenManager().popAllActivity()
                                    System.exit(0)
                                }.canceledOnTouchOutside(false).cancelable(false)
                                        .content("\t\t\t\t" + Content + "\n\t\t\t\t" + NoticeDate)
                                        .positiveText("退出系统").show()
                            }
                        }
                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.Companion.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    /**
     * 前段获取验证码
     */
    private fun getImageValidateCode() {
        ok = 11
        activityLoginBinding!!.securityCode.setText("")
        activityLoginBinding!!.securityImage.setImageResource(R.drawable.fresh)
        val body = HttpRequestParams.getParamsGetImageValidateCode("1003")
        post(HttpURLS.imageValidateCode, false, "imageValidateCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Klog.e(error.toString())
                getImageValidateCode()
            } else {
                Klog.i(response.toString())
                val ResponseFields = response.getJSONObject("ResponseFields")
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        ImageKey = ResponseFields.getString("ImageKey")
                        ImageCode = ResponseFields.getString("ImageCode")
                        activityLoginBinding!!.securityImage.setImageBitmap(getBitmap(ImageCode))
                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.Companion.toastError(head.ProcessDes)
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
        ok = 11
        val body = HttpRequestParams.getParamsForValidateCode(activityLoginBinding!!.securityCode.text.toString(), ImageKey)
        post(HttpURLS.validateCode, false, "validateCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseFields = response.getJSONObject("ResponseFields")
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        val VerificationResult = ResponseFields.getString("VerificationResult")// 验证结果
                        // 10：成功
                        // 。11：失败
                        ok = Integer.parseInt(VerificationResult)
                        //								String VerificationResultDesc = ResponseFields.getString("VerificationResultDesc");// 验证结果描述
                        // toast(VerificationResultDesc);
                        if (ok == 10) {
                            resetvaIidateCodeDrawableRight()
                        } else if (ok == 11) {
                            resetvaIidateCodeDrawableWrong()
                        }
                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.Companion.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    // 生成验证码图片
    fun getBitmap(str: String): Bitmap {
        val decode = Base64.decode(str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
        return bitmap
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    var isExit = false

    internal var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            isExit = false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true
                ToastUtil.Companion.toastInfo(resources.getString(R.string.exit))
                mHandler.sendEmptyMessageDelayed(0, 5000)
            } else {
//                ScreenManager.getScreenManager().popAllActivity()
                moveTaskToBack(true)
                return true
            }
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_regis -> startActivity(Intent(this, NewRegistrationActivity::class.java))
            R.id.tv_login -> submit()
            R.id.tv_forget_password -> startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
            R.id.iv_weixin// 微信快捷登陆
            -> {
            }
            R.id.iv_qq// QQ快捷登陆
            -> {
            }
            R.id.iv_alipay// 支付宝快捷登陆
            -> {
            }
        }
    }

    override fun onItemClick(type: Int, position: Int) {
        if (type == 1) {
            activityLoginBinding!!.etName.hidePopWindow()
            val accountManager = AccountManager.get(this@LoginActivity)
            val myAccount: Account? = accounts!![position]
            if (myAccount != null) {
                if (isNotEmpty(myAccount.name)) {
                    activityLoginBinding!!.etName.setText(myAccount.name)
                    activityLoginBinding!!.etName.setSelection(myAccount.name!!.length)
                }
                Logger.i("PasswordStatus:" + accountManager.getUserData(myAccount, "PasswordStatus"))
                Logger.i("PasswordNum:" + accountManager.getUserData(myAccount, "PasswordNum"))
                if ("true".equals(accountManager.getUserData(myAccount, "PasswordStatus"), true)) {
                    password = accountManager.getPassword(myAccount)
                    val stringBuffer = StringBuffer()
                    length = Integer.parseInt(accountManager.getUserData(myAccount, "PasswordNum"))
                    for (i in 0..length - 1) {
                        stringBuffer.append("0")
                    }
                    activityLoginBinding!!.etPassword.setText(stringBuffer.toString())
                    activityLoginBinding!!.etPassword.setSelection(stringBuffer.toString().length)
                    isRemind = true
                    activityLoginBinding!!.checkBoxRemenberPassword.setChecked(true)
                } else {
                    activityLoginBinding!!.checkBoxRemenberPassword.setChecked(false)
                }
            }
        } else if (type == 2) {
            val accountManager = AccountManager.get(this@LoginActivity)
            val myAccount: Account? = accounts!![position]

            accounts = accounts!!.filter({ account -> account != myAccount }).toTypedArray()

            val list = ArrayList<String>()

            accounts!!.forEach { account ->
                if (account != myAccount)
                    list.add(account.name)
            }

            activityLoginBinding!!.etName.setText("")
            activityLoginBinding!!.etPassword.setText("")
            adapter!!.updateList(list)
            accountManager.removeAccount(myAccount, this, {
                fun run(arg0: android.accounts.AccountManagerFuture<Boolean>) {
                }
            }, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        //		mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                if (activityLoginBinding!!.securityCode.text.length == 4) {
                    validateCode()
                } else {
                    resetvaIidateCodeDrawableNull()
                }
            } catch (e: Exception) {
            }

        }
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableNull() {
        activityLoginBinding!!.securityCode.setCompoundDrawables(activityLoginBinding!!.securityCode.compoundDrawables[0],
                activityLoginBinding!!.securityCode.compoundDrawables[1], null, activityLoginBinding!!.securityCode.compoundDrawables[3])
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableWrong() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.err_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityLoginBinding!!.securityCode.setCompoundDrawables(activityLoginBinding!!.securityCode.compoundDrawables[0],
                activityLoginBinding!!.securityCode.compoundDrawables[1], drawable, activityLoginBinding!!.securityCode.compoundDrawables[3])
    }

    private fun resetvaIidateCodeDrawableRight() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.right_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activityLoginBinding!!.securityCode.setCompoundDrawables(activityLoginBinding!!.securityCode.compoundDrawables[0],
                activityLoginBinding!!.securityCode.compoundDrawables[1], drawable, activityLoginBinding!!.securityCode.compoundDrawables[3])
    }

}
