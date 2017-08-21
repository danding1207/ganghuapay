package com.mqt.ganghuazhifu.http

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.view.KeyEvent
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.activity.LoginActivity
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.mqt.ganghuazhifu.view.LoadingDialog
import com.orhanobut.logger.Logger
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


class HttpRequest private constructor() {

    private val callMaps = HashMap<String, Call>()

    fun onDetachActivity() {
        dismissRoundProcessDialog()
    }

    /**
     * Http 发送post请求

     * @param url             网络路径
     * *
     * @param body            上传参数
     * *
     * @param requestListener 返回回调
     */
    fun httpPost(mContext: Activity?, url: String, isShow: Boolean, tag: String, body: RequestBody?,
                 requestListener: OnHttpRequestListener?) {
        if (isShow)
            showRoundProcessDialog(mContext, tag)
        Logger.i(url)
        val request = okhttp3.Request.Builder().url(url).post(body).build()

        val call = httpClient!!.newCall(request)
        callMaps.put(tag, call)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, error: IOException?) {
                if (isShow)
                    dismissRoundProcessDialog()
                callMaps.remove(tag)
                mContext?.runOnUiThread {
                    if (isShow)
                        dismissRoundProcessDialog()
                    if (error != null && error.cause != null && error.cause == SocketTimeoutException::class.java)
                        ToastUtil.toastError("请求超时！")
                    requestListener!!.OnCompleted(true, null, 0, error)
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {
                callMaps.remove(tag)
                if (isShow)
                    dismissRoundProcessDialog()
                val str = response.body().string()
                Logger.e(str)
                val `object`: JSONObject
                try {
                    `object` = JSONObject.parseObject(str)
                    val message = `object`.getString("message")
                    val code = `object`.getString("code")
                    val info = JSONObject.parseObject(`object`.getString("info"))
                    val ResponseHead = info.getJSONObject("ResponseHead")
                    Logger.e(str)
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    mContext?.runOnUiThread {
                        if (ProcessCode == "0013") {
                            MaterialDialog.Builder(mContext)
                                    .title("提醒")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .onPositive { dialog, which ->
                                        ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity::class.java)
                                    }
                                    .positiveText("确定")
                                    .content(ProcessDes).build().show()
                        } else {
                            if (code == "0") {
                                val ResponseFields = info.getString("ResponseFields")
                                if (ResponseFields == null) {
                                    requestListener!!.OnCompleted(false, info, 0, null)
                                } else if (ResponseFields.startsWith("[")) {
                                    requestListener!!.OnCompleted(false, info, 1, null)
                                } else if (ResponseFields.startsWith("{")) {
                                    requestListener!!.OnCompleted(false, info, 2, null)
                                }
                            } else {
                                val error = IOException(message)
                                requestListener!!.OnCompleted(true, null, 0, error)
                                ToastUtil.toastError(message)
                            }
                        }
                    }
                } catch (e: JSONException) {
                    mContext?.runOnUiThread { ToastUtil.toastError("请求结果无法解析") }
                }

            }
        })
    }


    /**
     * Http 发送get请求

     * @param url             网络路径
     * *
     * @param body            上传参数
     * *
     * @param requestListener 返回回调
     */
    fun httpGet(mContext: Activity?, url: String, isShow: Boolean, tag: String,
                requestListener: OnHttpRequestListener?) {
        if (isShow)
            showRoundProcessDialog(mContext, tag)
        Logger.i(url)

        val request = okhttp3.Request.Builder().url(url).get().build()

        val call = httpClient!!.newCall(request)
        callMaps.put(tag, call)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, error: IOException?) {
                if (isShow)
                    dismissRoundProcessDialog()
                callMaps.remove(tag)
                mContext?.runOnUiThread {
                    if (isShow)
                        dismissRoundProcessDialog()
                    if (error != null && error.cause != null && error.cause == SocketTimeoutException::class.java)
                        ToastUtil.toastError("请求超时！")
                    requestListener!!.OnCompleted(true, null, 0, error)
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {
                callMaps.remove(tag)
                if (isShow)
                    dismissRoundProcessDialog()
                val str = response.body().string()
                val `object`: JSONObject
                try {
                    `object` = JSONObject.parseObject(str)
                    val message = `object`.getString("message")
                    val code = `object`.getString("code")
                    val info = JSONObject.parseObject(`object`.getString("info"))
                    if (info == null) {
                        mContext?.runOnUiThread {
                            requestListener!!.OnCompleted(false, `object`, 0, null)
                        }
                        return
                    }
                    val ResponseHead = info.getJSONObject("ResponseHead")
                    Logger.e(str)
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    mContext?.runOnUiThread {
                        if (ProcessCode == "0013") {
                            MaterialDialog.Builder(mContext)
                                    .title("提醒")
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .onPositive { dialog, which ->
                                        mContext.startActivity(Intent(mContext, LoginActivity::class.java))
                                        ScreenManager.getScreenManager().popAllActivity()
                                    }
                                    .positiveText("确定")
                                    .content(ProcessDes).build().show()
                        } else {
                            if (code == "0") {
                                val ResponseFields = info.getString("ResponseFields")
                                if (ResponseFields == null) {
                                    requestListener!!.OnCompleted(false, info, 0, null)
                                } else if (ResponseFields.startsWith("[")) {
                                    requestListener!!.OnCompleted(false, info, 1, null)
                                } else if (ResponseFields.startsWith("{")) {
                                    requestListener!!.OnCompleted(false, info, 2, null)
                                }
                            } else {
                                val error = IOException(message)
                                requestListener!!.OnCompleted(true, null, 0, error)
                                ToastUtil.toastError(message)
                            }
                        }
                    }
                } catch (e: JSONException) {
                    mContext?.runOnUiThread { ToastUtil.toastError("请求结果无法解析") }
                }

            }
        })
    }


    fun showRoundProcessDialog(mContext: Activity?, tag: String) {
        val keyListener = DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK) {
                if (callMaps[tag] != null)
                    callMaps[tag]!!.cancel()
                callMaps.remove(tag)
                dismissRoundProcessDialog()
                true
            } else {
                false
            }
        }
        if (mContext != null) {
            loadingDialog = LoadingDialog(mContext)
            loadingDialog!!.setOnKeyListener(keyListener)
            loadingDialog!!.setCanceledOnTouchOutside(false)
            loadingDialog!!.tag = tag
            loadingDialog!!.show()
        }
    }

    fun dismissRoundProcessDialog() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
            loadingDialog = null
        }
    }

    companion object {
        var loadingDialog: LoadingDialog? = null
        var httpClient: OkHttpClient? = null
        var request: HttpRequest? = null
        val instance: HttpRequest by lazy {

            val builder = OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
            try {
                val certificates: InputStream = App.instance.assets.open("pay.jiaoyibao.com.cn.cer")
                val certificateFactory = CertificateFactory.getInstance("X.509")
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null)
                val certificateAlias = Integer.toString(0)
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificates))
                try {
                    if (certificates != null)
                        certificates!!.close()
                } catch (e: IOException) {
                }
                val sslContext = SSLContext.getInstance("TLS")
                val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(keyStore)
                sslContext.init(
                        null,
                        trustManagerFactory.trustManagers,
                        SecureRandom()
                )

                builder.retryOnConnectionFailure(false)
                if (HttpURLS.ip.equals("https://pay.jiaoyibao.com.cn")) {
                    builder.sslSocketFactory(sslContext.socketFactory)
                }
                httpClient = builder.build()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            request = HttpRequest()
            request!!
        }

        private val interceptor = Interceptor { chain ->
            var request: okhttp3.Request = chain.request().newBuilder().addHeader("Content-Type", "application/json")
                    .addHeader("Accept-Encoding", "gzip").addHeader("Connection", "keep-alive").build()
            //            if (!isNetworkReachable(mContext)) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            //            }
            val response = chain.proceed(request)

            //            if (isNetworkReachable(mContext)) {
            val maxAge = 60 * 60
            response.newBuilder().removeHeader("Pragma").header("Cache-Control", "public, max-age=" + maxAge)
                    .build()
            //            } else {
            val maxStale = 60 * 60 * 24 * 7 // 设置超时为一周
            response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale).build()
            //            }
            response
        }

        /**
         * 判断网络是否可用

         * @param context Context对象
         */
        fun isNetworkReachable(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val current = cm.activeNetworkInfo ?: return false
            return current.isAvailable
        }

        fun getNetworkType(context: Context): String {
            var strNetworkType = ""
            val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = "WIFI"
                } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    val _strSubTypeName = networkInfo.subtypeName
                    Logger.e("Network getSubtypeName : " + _strSubTypeName)
                    // TD-SCDMA   networkType is 17
                    val networkType = networkInfo.subtype
                    when (networkType) {
                        TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN //api<8 : replace by 11
                        -> strNetworkType = "2G"
                        TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B //api<9 : replace by 14
                            , TelephonyManager.NETWORK_TYPE_EHRPD  //api<11 : replace by 12
                            , TelephonyManager.NETWORK_TYPE_HSPAP  //api<13 : replace by 15
                        -> strNetworkType = "3G"
                        TelephonyManager.NETWORK_TYPE_LTE    //api<11 : replace by 13
                        -> strNetworkType = "4G"
                        else ->
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equals("TD-SCDMA", ignoreCase = true) || _strSubTypeName.equals("WCDMA", ignoreCase = true) || _strSubTypeName.equals("CDMA2000", ignoreCase = true)) {
                                strNetworkType = "3G"
                            } else {
                                strNetworkType = _strSubTypeName
                            }
                    }
                    Logger.e("Network getSubtype : " + Integer.valueOf(networkType)!!.toString())
                }
            }
            Logger.e("Network Type : " + strNetworkType)
            return strNetworkType
        }
    }

}
