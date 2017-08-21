package com.mqt.ganghuazhifu.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.baidu.android.pushservice.PushConstants
import com.baidu.android.pushservice.PushManager
import com.github.mzule.activityrouter.annotation.Router
import com.github.mzule.activityrouter.router.Routers
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.BuildConfig
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityWelcomeBinding
import com.mqt.ganghuazhifu.event.InstallFailureEvent
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.*
import com.orhanobut.logger.Logger
import rx.Subscriber
import rx.lang.kotlin.observable
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * 欢迎页面
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
@Router("welcome")
class WelcomeActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt("screenwidth", screenwidth)
        savedInstanceState.putInt("screenhigh", screenhigh)
        savedInstanceState.putString("flavor", flavor)
        savedInstanceState.putString("version", version)
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
        val width = savedInstanceState.getInt("screenwidth", 0)
        val high = savedInstanceState.getInt("screenhigh", 0)
        screenwidth = if (width == 0) screenwidth else width
        screenhigh = if (high == 0) screenhigh else high
        flavor = savedInstanceState.getString("flavor")
        version = savedInstanceState.getString("version")
    }

    private var flavor: String? = null
    private var version: String? = null

    private var activityWelcomeBinding: ActivityWelcomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWelcomeBinding = DataBindingUtil.setContentView<ActivityWelcomeBinding>(this, R.layout.activity_welcome)
        context = this

        if (!this.isTaskRoot) { //判断该Activity是不是任务空间的源Activity
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action.equals(Intent.ACTION_MAIN)) {
                finish()
                return
            }
        }

    }

    override fun onResume() {
        super.onResume()
        observable<Boolean> { subscriber ->
            if (!BuildConfig.DEBUG)
                initSignature(applicationContext, subscriber)
            subscriber.onNext(true)
        }
                .map {
                    EncryptedPreferencesUtils.init(this)
                    if (App.phoneInfo!!.appVersion != "NULL") {
                        version = App.phoneInfo!!.appVersion
                        when (BuildConfig.FLAVOR) {
                            "ganghua" -> flavor = "官网"
//                            "qihu360" -> flavor = "360应用市场"
//                            "tencent" -> flavor = "腾讯应用宝"
//                            "mi" -> flavor = "小米应用市场"
//                            "huawei" -> flavor = "华为应用商城"
//                            "bidu" -> flavor = "百度应用市场"
//                            "anzhi" -> flavor = "安智应用市场"
//                            "mumayi" -> flavor = "木蚂蚁应用市场"
//                            "appchina" -> flavor = "应用汇"
                            "beijing" -> flavor = "北京测试"
                            "dongguan" -> flavor = "东莞测试"
                        }
                        activityWelcomeBinding!!.tvCurrtentVersion.text = flavor + " : " + version
                    }
                    getScreenSize()
                    val api_key = Utils.getMetaValue(this@WelcomeActivity, "com.baidu.push.apikey")
                    Logger.e("api_key--->" + api_key)
                    // 百度云推送，向百度注册设备
                    PushManager.startWork(applicationContext, PushConstants.LOGIN_TYPE_API_KEY, api_key)
                    true
                }
                .onErrorReturn {
                    if (it != null) {
                        MaterialDialog.Builder(this@WelcomeActivity).title("特别警告").content("您正在使用破解版或盗版产品，可能会损害您的利益，请立即卸载并下载正版应用。举报电话：400-850-5959！").onPositive { dialog, which ->
                            finish()
                            System.exit(0)
                        }.cancelable(false).canceledOnTouchOutside(false).positiveText("退出应用").show()
                    }
                    false
                }
                .subscribe {
                    if (it!!) {
                        if (FileDownloadManager.getInstance().materialDialog == null || !FileDownloadManager.getInstance().materialDialog.isShowing)
                            checkVersion()
                    }
                }
    }

    private fun getScreenSize() {
        Thread {
            while (screenwidth == 0 || screenhigh == 0) {
                screenwidth = UnitConversionUtils.getScreenWidth(this@WelcomeActivity)
                screenhigh = UnitConversionUtils.getScreenHigh(this@WelcomeActivity)
            }
            EncryptedPreferencesUtils.setScreenSize(intArrayOf(screenwidth, screenhigh))
            Logger.d("screenwidth--->" + screenwidth)
            Logger.d("screenhigh--->" + screenhigh)
        }.start()
    }

    override fun OnViewClick(v: View) {

    }

    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            if (msg.what == 1) {
                Routers.open(this@WelcomeActivity, "mqt://login")
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun checkVersion() {
        val body = HttpRequestParams.getParamsForAdvertisement("01", "01")
        post(HttpURLS.processQuery, false, "Advertisement", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
                MaterialDialog.Builder(this@WelcomeActivity)
                        .title("错误")
                        .content("连接服务器失败，可能原因有：\n*网络不稳定\n*尚未接入互联网\n*安全软件禁止访问网络").onPositive { dialog, which -> ScreenManager.getScreenManager().popAllActivity() }
                        .cancelable(false).canceledOnTouchOutside(false)
                        .positiveText("确定").show()
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getJSONArray("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                if (ProcessCode == "0000" && ResponseFields != null) {
                    val QryResults1 = ResponseFields.getJSONObject(0)
                    val QryResults2 = ResponseFields.getJSONObject(1)
                    val appPath = QryResults2.getString("comval")
                    var comval = QryResults1.getString("comval")
                    comval = comval.substring(1, comval.length)
                    Logger.i(version!!.toString())

                    Logger.i("新版本：" + comval + "当前版本：" + version + "  需要更新")
                    val ss = comval.split("\\.".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
                    val sss = version!!.split("\\.".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
                    val a = Integer.parseInt(ss[0])
                    val b = Integer.parseInt(ss[1])
                    val c = Integer.parseInt(ss[2])
                    val d = Integer.parseInt(ss[3])
                    val aa = Integer.parseInt(sss[0])
                    val bb = Integer.parseInt(sss[1])
                    val cc = Integer.parseInt(sss[2])
                    val dd = Integer.parseInt(sss[3])

                    if (a > aa) {
                        Logger.i("新版本：" + comval + "当前版本：" + version + "  需要强制更新")
                        mandatoryUpdate(comval, appPath)
                    } else if (a >= aa && b > bb) {
                        Logger.i("新版本：" + comval + "当前版本：" + version + "  需要强制更新")
                        mandatoryUpdate(comval, appPath)
                    } else if (a >= aa && b >= bb && c > cc) {
                        Logger.i("新版本：" + comval + "当前版本：" + version + "  需要强制更新")
                        mandatoryUpdate(comval, appPath)
                    } else if (a >= aa && b >= bb && c >= cc && d > dd) {
                        Logger.i("新版本：" + comval + "当前版本：" + version + "  需要提醒更新")
                        proposedUpdate(comval, appPath)
                    } else {
                        Logger.i("新版本：" + comval + "当前版本：" + version + "  不需要更新")
                        handler.sendEmptyMessageDelayed(1, 3000)
                    }

                } else {
                    handler.sendEmptyMessageDelayed(1, 3000)
                }
            }
        })
    }

    /**
     * 强制更新
     */
    private fun mandatoryUpdate(newVersion: String, appPath: String) {
        // 强制更新
        isNewVersion = true
        Logger.i("强制更新")
        MaterialDialog.Builder(this@WelcomeActivity).title("提醒")
                .content("您当前版本过低，请您更新版本$newVersion，享受新的体验。")
                .onPositive { dialog, which ->
                    val networkType = HttpRequest.getNetworkType(this@WelcomeActivity);
                    if (networkType.equals("WIFI")) {
                        download(this@WelcomeActivity, appPath, newVersion)
                    } else {
                        MaterialDialog.Builder(this@WelcomeActivity)
                                .title("提醒")
                                .content("当前网络不是wifi，是否继续更新").onPositive { dialog, which ->
                            download(this@WelcomeActivity, appPath, newVersion)
                        }.onNegative { dialog, which ->
                            System.exit(0)
                            ScreenManager.getScreenManager().popAllActivity()
                            finish()
                        }
                                .cancelable(false).canceledOnTouchOutside(false)
                                .positiveText("立即更新").negativeText("退出应用").show()
                    }
                }
                .onNegative { dialog, which ->
                    System.exit(0)
                    ScreenManager.getScreenManager().popAllActivity()
                    finish()
                }.cancelable(false).canceledOnTouchOutside(false).positiveText("立即更新").negativeText("退出应用").show()
    }

    /**
     * 建议更新
     */
    private fun proposedUpdate(newVersion: String, appPath: String) {
        // 提醒更新
        isNewVersion = true
        Logger.i("提醒更新")
        MaterialDialog.Builder(this@WelcomeActivity)
                .title("提醒")
                .content("有新的版本，更新版本" + newVersion).onPositive { dialog, which ->
            val networkType = HttpRequest.getNetworkType(this@WelcomeActivity)
            if (networkType.equals("WIFI")) {
                download(this@WelcomeActivity, appPath, newVersion)
            } else {
                MaterialDialog.Builder(this@WelcomeActivity)
                        .title("提醒")
                        .content("当前网络不是wifi，是否继续更新").onPositive { dialog, which ->
                    download(this@WelcomeActivity, appPath, newVersion)
                }.onNegative { dialog, which -> handler.sendEmptyMessageDelayed(1, 3000) }
                        .cancelable(false).canceledOnTouchOutside(false)
                        .positiveText("立即更新").negativeText("下次更新").show()
            }
        }.onNegative { dialog, which -> handler.sendEmptyMessageDelayed(1, 3000) }
                .cancelable(false).canceledOnTouchOutside(false)
                .positiveText("立即更新").negativeText("下次更新").show()
    }

    private fun initSignature(context: Context, subscriber: Subscriber<in Boolean>) {
        Logger.i("initSignature")
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName,
                    PackageManager.GET_SIGNATURES)
            val signatures = packageInfo.signatures
            val sign = signatures[0]
            parseSignature(sign.toByteArray())

            val sb = StringBuilder()
            for (signature in signatures) {
                sb.append(signature.toCharsString())
            }
            Logger.i(sb.toString())

            Logger.i("MD5:" + encryptionMD5(sign.toByteArray()))
            if (!checkMD5(encryptionMD5(sign.toByteArray()))) {
                subscriber.onError(Throwable())
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun checkMD5(encryptionMD5: String): Boolean {
        return "042278B077BAF41D6D0320972D57757E" == encryptionMD5.toUpperCase()
    }

    fun parseSignature(signature: ByteArray) {
        try {
            val certFactory = CertificateFactory.getInstance("X.509")
            val cert = certFactory.generateCertificate(ByteArrayInputStream(signature)) as X509Certificate
            val pubKey = cert.publicKey.toString()
            val signNumber = cert.serialNumber.toString()
            Logger.d("signName:" + cert.sigAlgName)
            Logger.d("pubKey:" + pubKey)
            Logger.d("signNumber:" + signNumber)
            Logger.d("subjectDN:" + cert.subjectDN.toString())
        } catch (e: CertificateException) {
            e.printStackTrace()
        }

    }

    /**
     * MD5加密

     * @param byteStr 需要加密的内容
     * *
     * @return 返回 byteStr的md5值
     */
    fun encryptionMD5(byteStr: ByteArray): String {
        var messageDigest: MessageDigest
        val md5StrBuff = StringBuffer()
        try {
            messageDigest = MessageDigest.getInstance("MD5")
            messageDigest!!.reset()
            messageDigest.update(byteStr)
            val byteArray = messageDigest.digest()
            for (i in byteArray.indices) {
                if (Integer.toHexString(0xFF and byteArray[i].toInt()).length == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF and byteArray[i].toInt()))
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF and byteArray[i].toInt()))
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return md5StrBuff.toString()
    }

    @Subscribe(thread = EventThread.MAIN_THREAD)
    fun onInstallFailureEvent(event: InstallFailureEvent) {
        Logger.i("onInstallFailureEvent")
        FileDownloadManager.getInstance().materialDialog.dismiss()
        MaterialDialog.Builder(this)
                .title("提醒")
                .content("未找到更新文件，请检查是否取消下载或已删除下载文件！")
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .positiveText("确定")
                .onPositive { dialog, which ->
                    if (FileDownloadManager.getInstance().materialDialog == null || !FileDownloadManager.getInstance().materialDialog.isShowing)
                        checkVersion()
                }
                .show()
    }

    companion object {

        var screenwidth = 0
        var screenhigh = 0
        var isNewVersion = false
        var context: WelcomeActivity? = null
        val instance: WelcomeActivity by lazy {
            context!!
        }

        fun download(context: Activity, url: String, versionName: String): Boolean {
            if (!canDownloadState(context)) {
                ToastUtil.Companion.toastWarning("下载服务不可用,请您启用!")
                showDownloadSetting(context)
                return true
            }
            return ApkUpdateUtils.download(context, url, context.resources.getString(R.string.app_name), versionName)
        }

        private fun canDownloadState(context: Activity): Boolean {
            try {
                val state = context.packageManager.getApplicationEnabledSetting("com.android.providers.downloads")
                if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                        || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                        || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                    return false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

            return true
        }

        private fun showDownloadSetting(context: Activity) {
            val packageName = "com.android.providers.downloads"
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + packageName)
            if (intentAvailable(context, intent)) {
                context.startActivity(intent)
            }
        }

        private fun intentAvailable(context: Activity, intent: Intent): Boolean {
            val packageManager = context.packageManager
            val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            return list.size > 0
        }

        fun finsh() {
            ScreenManager.getScreenManager().finsh(WelcomeActivity::class.java)
        }

    }

}
