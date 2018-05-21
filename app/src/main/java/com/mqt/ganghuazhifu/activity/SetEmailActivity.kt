package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.databinding.ActivitySetEmailBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.*
import com.orhanobut.logger.Logger
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.regex.Pattern
import javax.crypto.Cipher

/**
 * 设置邮箱(设置 身份证号，设置真实姓名)

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class SetEmailActivity : BaseActivity() {

    private var publicKeyString = "" +
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCISLP98M/56HexX/9FDM8iuIEQ\n" +
            "ozy6kn2JMcbZS5/BhJ+U4PZIChJfggYlWnd8NWn4BYr2kxxyO8Qgvc8rpRZCkN0O\n" +
            "SLqLgZGmNvoSlDw80UXq90ZsVHDTOHuSFHw8Bv//B4evUNJBB8g9tpVxr6P5EJ6F\n" +
            "MoR/kY2dVFQCQM4+5QIDAQAB\n" +
            ""

    private var privateKeyString = "" +
            "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIhIs/3wz/nod7Ff\n" +
            "/0UMzyK4gRCjPLqSfYkxxtlLn8GEn5Tg9kgKEl+CBiVad3w1afgFivaTHHI7xCC9\n" +
            "zyulFkKQ3Q5IuouBkaY2+hKUPDzRRer3RmxUcNM4e5IUfDwG//8Hh69Q0kEHyD22\n" +
            "lXGvo/kQnoUyhH+RjZ1UVAJAzj7lAgMBAAECgYAVh26vsggY0Yl/Asw/qztZn837\n" +
            "w93HF3cvYiaokxLErl/LVBJz5OtsHQ09f2IaxBFedfmy5CB9R0W/aly851JxrI8W\n" +
            "Akx2W2FNllzhha01fmlNlOSumoiRF++JcbsAjDcrcIiR8eSVNuB6ymBCrx/FqhdX\n" +
            "3+t/VUbSAFXYT9tsgQJBALsHurnovZS1qjCTl6pkNS0V5qio88SzYP7lzgq0eYGl\n" +
            "vfupdlLX8/MrSdi4DherMTcutUcaTzgQU20uAI0EMyECQQC6il1Kdkw8Peeb0JZM\n" +
            "Hbs+cMCsbGATiAt4pfo1b/i9/BO0QnRgDqYcjt3J9Ux22dPYbDpDtMjMRNrAKFb4\n" +
            "BJdFAkBMrdWTZOVc88IL2mcC98SJcII5wdL3YSeyOZto7icmzUH/zLFzM5CTsLq8\n" +
            "/HDiqVArNJ4jwZia/q6Fg6e8KO2hAkB0EK1VLF/ox7e5GkK533Hmuu8XGWN6I5bH\n" +
            "nbYd06qYQyTbbtHMBrFSaY4UH91Qwd3u9gAWqoCZoGnfT/o03V5lAkBqq8jZd2lH\n" +
            "ifey+9cf1hsHD5WQbjJKPPIb57CK08hn7vUlX5ePJ02Q8AhdZKETaW+EsqJWpNgs\n" +
            "u5wPqsy2UynO\n" +
            ""

    private val ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding"//加密填充方式

    private var type: Int = 0
    private var phone: String? = null
    private var yzm: String? = null
    private var yzm_return: String? = null
    private var time: Long = 0

    private var ok: Int = 0
    private var ImageKey = ""
    private var ImageCode = ""

    private var activitySetEmailBinding: ActivitySetEmailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySetEmailBinding = DataBindingUtil.setContentView<ActivitySetEmailBinding>(this, R.layout.activity_set_email)
        type = intent.getIntExtra("TYPE", 1)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activitySetEmailBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activitySetEmailBinding!!.etExtraCode.addTextChangedListener(textWatcher)
        activitySetEmailBinding!!.forgetSecurityImage.setOnClickListener(this)
        activitySetEmailBinding!!.tvTitleRight.setOnClickListener(this)
        activitySetEmailBinding!!.tvGetYzm.setOnClickListener(this)
        when (type) {
            SETEMAIL -> {
                supportActionBar!!.title = "设置邮箱"
                activitySetEmailBinding!!.etSetEmail.hint = "请输入您的邮箱"
                activitySetEmailBinding!!.etSetEmail.inputType = InputType.TYPE_CLASS_TEXT
                activitySetEmailBinding!!.tvSetInfoExplain.visibility = View.VISIBLE
            }
            SETIDCARDNUM -> {
                supportActionBar!!.title = "个人资料修改"
                activitySetEmailBinding!!.etSetEmail.inputType = InputType.TYPE_CLASS_TEXT // 调用数字键盘
                activitySetEmailBinding!!.etSetEmail.hint = "请输入您的身份证号码"
                activitySetEmailBinding!!.etSetEmail.maxEms = 18
                TextValidation.setRegularValidationIDCard(this, activitySetEmailBinding!!.etSetEmail)
                activitySetEmailBinding!!.tvSetInfoExplain.visibility = View.INVISIBLE
            }
            SETREALNAME -> {
                supportActionBar!!.title = "个人资料修改"
                activitySetEmailBinding!!.etSetEmail.inputType = InputType.TYPE_CLASS_TEXT
                activitySetEmailBinding!!.etSetEmail.hint = "请输入您的真实姓名"
                activitySetEmailBinding!!.etSetEmail.maxEms = 8
                TextValidation.setRegularValidationChina(this, activitySetEmailBinding!!.etSetEmail)
                activitySetEmailBinding!!.tvSetInfoExplain.visibility = View.INVISIBLE
            }
            SETPHONE -> {
                supportActionBar!!.title = "修改手机号码"
                activitySetEmailBinding!!.etYzm.visibility = View.VISIBLE
                activitySetEmailBinding!!.tvGetYzm.visibility = View.VISIBLE
                activitySetEmailBinding!!.llTuxingYzm.visibility = View.VISIBLE
                activitySetEmailBinding!!.etSetEmail.inputType = InputType.TYPE_CLASS_NUMBER // 调用数字键盘
                activitySetEmailBinding!!.etSetEmail.maxEms = 11
                activitySetEmailBinding!!.etSetEmail.hint = "请输入您的手机号码"
                activitySetEmailBinding!!.tvSetInfoExplain.visibility = View.INVISIBLE
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_get_yzm -> getCaptcha()
            R.id.tv_title_right -> submit()
        }
    }

    /**
     * 前段获取验证码
     */
    private fun getImageValidateCode() {
        activitySetEmailBinding!!.etExtraCode.setText("")
        resetvaIidateCodeDrawableNull()
        val body = HttpRequestParams.getParamsGetImageValidateCode("1003")
        post(HttpURLS.imageValidateCode, true, "imageValidateCode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseFields = response.getJSONObject("ResponseFields")
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {

                        ImageKey = ResponseFields.getString("ImageKey")
                        ImageCode = ResponseFields.getString("ImageCode")
                        activitySetEmailBinding!!.forgetSecurityImage.setImageBitmap(getBitmap(ImageCode))

                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.toastError(head.ProcessDes)
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
        val body = HttpRequestParams.getParamsForValidateCode(activitySetEmailBinding!!.etExtraCode.text.toString(), ImageKey)
        post(HttpURLS.validateCode, true, "validateCode", body, OnHttpRequestListener { isError, response, type, error ->
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
                        val VerificationResultDesc = ResponseFields.getString("VerificationResultDesc")// 验证结果描述
                        // toast(VerificationResultDesc);
                        if (ok == 10) {
                            resetvaIidateCodeDrawableRight()
                        } else if (ok == 11) {
                            resetvaIidateCodeDrawableWrong()
                        }

                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableNull() {
        activitySetEmailBinding!!.etExtraCode.setCompoundDrawables(activitySetEmailBinding!!.etExtraCode.compoundDrawables[0],
                activitySetEmailBinding!!.etExtraCode.compoundDrawables[1], null, activitySetEmailBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 重置验证码表示图片为叉
    private fun resetvaIidateCodeDrawableWrong() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.err_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activitySetEmailBinding!!.etExtraCode.setCompoundDrawables(activitySetEmailBinding!!.etExtraCode.compoundDrawables[0],
                activitySetEmailBinding!!.etExtraCode.compoundDrawables[1], drawable, activitySetEmailBinding!!.etExtraCode.compoundDrawables[3])
    }

    private fun resetvaIidateCodeDrawableRight() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.right_4)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        activitySetEmailBinding!!.etExtraCode.setCompoundDrawables(activitySetEmailBinding!!.etExtraCode.compoundDrawables[0],
                activitySetEmailBinding!!.etExtraCode.compoundDrawables[1], drawable, activitySetEmailBinding!!.etExtraCode.compoundDrawables[3])
    }

    // 生成验证码图片
    fun getBitmap(str: String): Bitmap {

        val decode = Base64.decode(str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
        return bitmap

    }

    /**
     * 网络获取验证码
     */
    private fun getCaptcha() {
        val user = EncryptedPreferencesUtils.getUser()
        phone = user.PhoneNb!!.toString().trim { it <= ' ' }
        if (ok == 10 && activitySetEmailBinding!!.etExtraCode.text.length == 4) {
            val body = HttpRequestParams.getParamsForVerificationCode(phone!!, "05", activitySetEmailBinding!!.etExtraCode.text.toString().trim { it <= ' ' }, ImageKey)
            post(HttpURLS.getVerificationCode, true, "VerificationCode", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        val VerificationCode = ResponseFields.getString("VerificationCode")
                        yzm_return = VerificationCode
                        time = System.currentTimeMillis()
                        activitySetEmailBinding!!.tvGetYzm.startTimer()
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        } else {
            ToastUtil.toastWarning("请正确输入图形验证码!")
        }
    }

    private fun checkEmpty(): Boolean {
        if (TextUtils.isEmpty(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })) {
            when (type) {
                SETEMAIL -> ToastUtil.toastWarning("请填写邮箱!")
                SETIDCARDNUM -> ToastUtil.toastWarning("请填写身份证号!")
                SETREALNAME -> ToastUtil.toastWarning("请填写真实姓名!")
                SETPHONE -> ToastUtil.toastWarning("请填写手机号!")
            }
            return false
        } else {
            when (type) {
                SETEMAIL -> if (!isEmail(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })) {
                    ToastUtil.toastWarning("请填写正确邮箱!")
                    return false
                }
                SETIDCARDNUM -> {
                    val idCard = IDCard()
                    if (!idCard.verify(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })) {
                        ToastUtil.toastWarning(idCard.codeError)
                        return false
                    }
                }
                SETREALNAME -> {
                }
                SETPHONE -> if (!VerifyUtils.isMobileNO(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })) {
                    ToastUtil.toastWarning("请填写正确手机号!")
                    return false
                }
            }
        }
        if (type == SETPHONE) {
            yzm = activitySetEmailBinding!!.etYzm.text.toString().trim { it <= ' ' }
            if (time == 0L) {
                ToastUtil.toastWarning("请先获取验证码!")
                return false
            } else if (ok == 11 || activitySetEmailBinding!!.etExtraCode.text.length < 4) {
                ToastUtil.toastWarning("请正确输入图形验证码!")
                return false
            } else if (TextUtils.isEmpty(yzm)) {
                ToastUtil.toastWarning("请填写短信验证码!")
                return false
            } else if (System.currentTimeMillis() - time > 300 * 1000) {
                ToastUtil.toastWarning("验证码已失效，请重新获取!")
                return false
            } else if (yzm != yzm_return) {
                ToastUtil.toastWarning("验证码错误!")
                return false
            }
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
            var body: CusFormBody? = null
            when (type) {
                SETEMAIL -> body = HttpRequestParams.getParamsForSetEmail(user.LoginAccount, user.Email,
                        activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                SETIDCARDNUM -> {
                    var iccardnums = activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' }
                    Logger.i("加密前--->"+iccardnums)
                    var iccardnum = encrypt(iccardnums.toByteArray(Charsets.UTF_8),
                            getPublicKey(publicKeyString))
                    Logger.i("加密后--->"+Base64.encodeToString(iccardnum, Base64.NO_WRAP))

                    var realname = encrypt(user.RealName!!.toByteArray(Charsets.UTF_8),
                            getPublicKey(publicKeyString))

                    Logger.i("加密后--->"+Base64.encodeToString(realname, Base64.NO_WRAP))

                    body = HttpRequestParams.getParamsForSetIDCardNbNewRealName(user.LoginAccount,
                            Base64.encodeToString(realname, Base64.NO_WRAP),
                            Base64.encodeToString(iccardnum, Base64.NO_WRAP))
                }
                SETREALNAME -> {
                    var realnames = activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' }
                    Logger.i("加密前--->"+realnames)
                    var realname = encrypt(realnames.toByteArray(Charsets.UTF_8),
                            getPublicKey(publicKeyString))
                    Logger.i("加密后--->"+Base64.encodeToString(realname, Base64.NO_WRAP))

                    var iccardnum = encrypt(user.IdcardNb!!.toByteArray(Charsets.UTF_8),
                            getPublicKey(publicKeyString))

                    Logger.i("加密后--->"+Base64.encodeToString(iccardnum, Base64.NO_WRAP))

                    body = HttpRequestParams.getParamsForSetIDCardNbNewRealName(user.LoginAccount,
                            Base64.encodeToString(realname, Base64.NO_WRAP),
                            Base64.encodeToString(iccardnum, Base64.NO_WRAP))
                }
                SETPHONE -> body = HttpRequestParams.getParamsForSetPhoneNum(user.LoginAccount, user.PhoneNb,
                        activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' }, null, null)
            }
            if (body != null) {
                post(HttpURLS.userUpdate, true, "SetPhoneNum", body, OnHttpRequestListener { isError, response, type, error ->
                    if (isError) {
                        Logger.e(error.toString())
                    } else {
                        Logger.i(response.toString())
                        val ResponseHead = response.getJSONObject("ResponseHead")
                        val ProcessCode = ResponseHead.getString("ProcessCode")
                        val ProcessDes = ResponseHead.getString("ProcessDes")
                        if (ProcessCode == "0000") {
                            Logger.i("type--->" + this@SetEmailActivity.type)
                            when (this@SetEmailActivity.type) {
                                SETEMAIL -> EncryptedPreferencesUtils.setEmail(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                                SETIDCARDNUM -> EncryptedPreferencesUtils.setIdcardNb(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                                SETREALNAME -> EncryptedPreferencesUtils.setRealName(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                                SETPHONE -> EncryptedPreferencesUtils.setPhoneNb(activitySetEmailBinding!!.etSetEmail.text.toString().trim { it <= ' ' })
                            }
                            finish()
                        } else {
                            ToastUtil.toastError(ProcessDes)
                        }
                    }
                })
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                if (activitySetEmailBinding!!.etExtraCode.text.length == 4) {
                    // 通信校验验证码
                    validateCode()
                } else {
                    resetvaIidateCodeDrawableNull()
                }
            } catch (e: Exception) {
            }

        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    companion object {
        private val SETEMAIL = 1
        private val SETIDCARDNUM = 2
        private val SETREALNAME = 3
        private val SETPHONE = 4
    }

    @Throws(Exception::class)
    fun encrypt(content: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(content)
    }

    @Throws(Exception::class)
    fun decrypt(content: ByteArray, privateKey: PrivateKey): ByteArray {
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(content)
    }

    //将base64编码后的公钥字符串转成PublicKey实例
    @Throws(Exception::class)
    fun getPublicKey(publicKey: String): PublicKey {
        val keyBytes = Base64.decode(publicKey.toByteArray(), Base64.NO_WRAP)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(keySpec)
        Logger.i("publicKey--->" + Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP))
        return publicKey
    }

    @Throws(Exception::class)
    fun getPrivateKey(privateKey: String): PrivateKey {
        val keyBytes = Base64.decode(privateKey.toByteArray(), Base64.NO_WRAP)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey = keyFactory.generatePrivate(keySpec)
        Logger.i("privateKey--->" + Base64.encodeToString(privateKey.encoded, Base64.NO_WRAP))
        return privateKey
    }

    @Throws(Exception::class)
    fun decryptByPrivateKey(encryptedData: ByteArray, privateKey: PrivateKey): ByteArray {
        val cipher = Cipher.getInstance(privateKey.algorithm)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val inputLen = encryptedData.size
        val out = ByteArrayOutputStream()
        var offSet = 0
        var i = 0
        while (inputLen - offSet > 0) {
            val cache: ByteArray
            if (inputLen - offSet > 256) {
                cache = cipher.doFinal(encryptedData, offSet, 256)
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet)
            }
            out.write(cache, 0, cache.size)
            ++i
            offSet = i * 256
        }
        val decryptedData = out.toByteArray()
        out.close()
        return decryptedData
    }

    @Throws(Exception::class)
    fun encryptByPublicKey(data: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(publicKey.algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val inputLen = data.size
        val out = ByteArrayOutputStream()
        var offSet = 0
        var i = 0
        while (inputLen - offSet > 0) {
            val cache: ByteArray
            if (inputLen - offSet > 244) {
                cache = cipher.doFinal(data, offSet, 244)
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet)
            }
            out.write(cache, 0, cache.size)
            ++i
            offSet = i * 244
        }
        val encryptedData = out.toByteArray()
        out.close()
        return encryptedData
    }

}
