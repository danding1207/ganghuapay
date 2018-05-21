package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Base64
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivitySetAccountBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.IDCard
import com.mqt.ganghuazhifu.utils.TextValidation
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import java.io.ByteArrayOutputStream
import java.net.URLDecoder
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * 设置身份证号，设置真实姓名

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class SetAccountActivity : BaseActivity() {

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


    private var activitySetAccountBinding: ActivitySetAccountBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySetAccountBinding = DataBindingUtil.setContentView<ActivitySetAccountBinding>(this,
                R.layout.activity_set_account)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activitySetAccountBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activitySetAccountBinding!!.tvTitleRight.setOnClickListener(this)

        supportActionBar!!.title = "个人资料修改"
        activitySetAccountBinding!!.etSetIccardnb.inputType = InputType.TYPE_CLASS_TEXT // 调用数字键盘
        activitySetAccountBinding!!.etSetIccardnb.hint = "请输入您的身份证号码"
        activitySetAccountBinding!!.etSetIccardnb.maxEms = 18
        TextValidation.setRegularValidationIDCard(this, activitySetAccountBinding!!.etSetIccardnb)

        supportActionBar!!.title = "个人资料修改"
        activitySetAccountBinding!!.etSetRealname.inputType = InputType.TYPE_CLASS_TEXT
        activitySetAccountBinding!!.etSetRealname.hint = "请输入您的真实姓名"
        activitySetAccountBinding!!.etSetRealname.maxEms = 8
        TextValidation.setRegularValidationChina(this, activitySetAccountBinding!!.etSetRealname)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_title_right -> submit()
        }
    }

    private fun checkEmpty(): Boolean {
        val idCard = IDCard()
        if (TextUtils.isEmpty(activitySetAccountBinding!!.etSetIccardnb.text.toString().trim { it <= ' ' })) {
            ToastUtil.toastWarning("请填写身份证号!")
            return false
        } else if (TextUtils.isEmpty(activitySetAccountBinding!!.etSetRealname.text.toString().trim { it <= ' ' })) {
            ToastUtil.toastWarning("请填写真实姓名!")
            return false
        } else if (!idCard.verify(activitySetAccountBinding!!.etSetIccardnb.text.toString().trim { it <= ' ' })) {
            ToastUtil.toastWarning(idCard.codeError)
            return false
        }
        return true
    }

    // 提交修改邮箱
    private fun submit() {
        if (checkEmpty()) {
            val user = EncryptedPreferencesUtils.getUser()
            var body: CusFormBody?

            var iccardnums = activitySetAccountBinding!!.etSetIccardnb.text.toString().trim { it <= ' ' }
            Logger.i("加密前--->" + iccardnums)
            var iccardnum = encrypt(iccardnums.toByteArray(Charsets.UTF_8),
                    getPublicKey(publicKeyString))
            Logger.i("加密后--->" + Base64.encodeToString(iccardnum, Base64.NO_WRAP))

            var realnames = activitySetAccountBinding!!.etSetRealname.text.toString().trim { it <= ' ' }
            Logger.i("加密前--->" + realnames)
            var realname = encrypt(realnames.toByteArray(Charsets.UTF_8),
                    getPublicKey(publicKeyString))
            Logger.i("加密后--->" + Base64.encodeToString(realname, Base64.NO_WRAP))

            body = HttpRequestParams.getParamsForSetIDCardNbNewRealName(user.LoginAccount,
                    Base64.encodeToString(realname, Base64.NO_WRAP),
                    Base64.encodeToString(iccardnum, Base64.NO_WRAP))

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
                            EncryptedPreferencesUtils.setIdcardNb(activitySetAccountBinding!!.etSetIccardnb.text.toString().trim { it <= ' ' })
                            EncryptedPreferencesUtils.setRealName(activitySetAccountBinding!!.etSetRealname.text.toString().trim { it <= ' ' })
                            finish()
                        } else {
                            ToastUtil.toastError(ProcessDes)
                        }
                    }
                })
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
