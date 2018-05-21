package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.*
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityNfcLiuchengBinding
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.MD5Util
import com.orhanobut.logger.Logger
import java.util.*

/**
 * NFC和蓝牙 流程 展示
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class NFCLiuChengActivity : BaseActivity() {

    private var activityNfcLiuchengBinding: ActivityNfcLiuchengBinding? = null

    private var wSettings: WebSettings? = null

    var type:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNfcLiuchengBinding = DataBindingUtil.setContentView<ActivityNfcLiuchengBinding>(this,
                R.layout.activity_nfc_liucheng)
        initView()
    }

    private fun initView() {

        type = intent.getIntExtra("TYPE", 1)

        setSupportActionBar(activityNfcLiuchengBinding!!.toolbar)

        when(type) {
            1-> supportActionBar!!.title = "NFC充值流程说明"
            1-> supportActionBar!!.title = "蓝牙充值流程说明"
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activityNfcLiuchengBinding!!.webView.settings.javaScriptCanOpenWindowsAutomatically = true// 设置js可以直接打开窗口，如window.open()，默认为false
        activityNfcLiuchengBinding!!.webView.settings.javaScriptEnabled = true// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        activityNfcLiuchengBinding!!.webView.settings.setSupportZoom(false)// 是否可以缩放，默认true
        activityNfcLiuchengBinding!!.webView.settings.builtInZoomControls = false// 是否显示缩放按钮，默认false
        activityNfcLiuchengBinding!!.webView.settings.useWideViewPort = true// 设置此属性，可任意比例缩放。大视图模式
        activityNfcLiuchengBinding!!.webView.settings.loadWithOverviewMode = true// 和setUseWideViewPort(true)一起解决网页自适应问题
        activityNfcLiuchengBinding!!.webView.settings.setAppCacheEnabled(false)// 是否使用缓存
        activityNfcLiuchengBinding!!.webView.settings.domStorageEnabled = false// DOM Storage
        activityNfcLiuchengBinding!!.webView.settings.defaultTextEncodingName = "GBK"// 设置字符编码
        activityNfcLiuchengBinding!!.webView.setWebViewClient(WebViewClientDemo())
        activityNfcLiuchengBinding!!.webView.setWebChromeClient(WebChromeClientDemo())
        activityNfcLiuchengBinding!!.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val url = HttpURLS.ip + "/m/app.html"
        Logger.i("url--->" + url)
        activityNfcLiuchengBinding!!.webView.loadUrl(url)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    private inner class WebViewClientDemo : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Logger.i("shouldOverrideUrlLoading-url:" + url)
            showRoundProcessDialog()
            view.loadUrl(url)// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            dismissRoundProcessDialog()
            Logger.i("onPageFinished-url:" + url)
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed()
        }
    }

    private inner class WebChromeClientDemo : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

}
