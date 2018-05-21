package com.mqt.ganghuazhifu.activity

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityConnectUsBinding
import com.mqt.ganghuazhifu.http.HttpURLS
import com.orhanobut.logger.Logger

/**
 * 联系我们
 * @author yang.lei
 * @date 2014-12-24
 */
@SuppressLint("SetJavaScriptEnabled")
class ConnectUsActivity : BaseActivity() {

    private var activityConnectUsBinding: ActivityConnectUsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityConnectUsBinding = DataBindingUtil.setContentView<ActivityConnectUsBinding>(this, R.layout.activity_connect_us)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityConnectUsBinding!!.toolbar)
        supportActionBar!!.title = "联系我们"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // 设置将接收各种通知和请求的WebViewClient（在WebView加载所有的链接）
        activityConnectUsBinding!!.connectWebView.settings.javaScriptCanOpenWindowsAutomatically = true// 设置js可以直接打开窗口，如window.open()，默认为false
        activityConnectUsBinding!!.connectWebView.settings.javaScriptEnabled = true// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        activityConnectUsBinding!!.connectWebView.settings.setSupportZoom(false)// 是否可以缩放，默认true
        activityConnectUsBinding!!.connectWebView.settings.builtInZoomControls = false// 是否显示缩放按钮，默认false
        activityConnectUsBinding!!.connectWebView.settings.useWideViewPort = true// 设置此属性，可任意比例缩放。大视图模式
        activityConnectUsBinding!!.connectWebView.settings.loadWithOverviewMode = true// 和setUseWideViewPort(true)一起解决网页自适应问题
        activityConnectUsBinding!!.connectWebView.settings.setAppCacheEnabled(false)// 是否使用缓存
        activityConnectUsBinding!!.connectWebView.settings.domStorageEnabled = false// DOM Storage
        activityConnectUsBinding!!.connectWebView.settings.defaultTextEncodingName = "GBK"// 设置字符编码

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activityConnectUsBinding!!.connectWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        activityConnectUsBinding!!.connectWebView.setWebViewClient(WebViewClientDemo())
        activityConnectUsBinding!!.connectWebView.loadUrl(HttpURLS.ip + "/www/help4Mbl.html")

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        activityConnectUsBinding!!.connectWebView.destroy()
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    private inner class WebViewClientDemo : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Logger.i(TAG, "shouldOverrideUrlLoading-url:" + url!!)
            view!!.loadUrl(url)// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Logger.i(TAG, "onPageFinished-url:" + url!!)
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {
            //handler.cancel(); 默认的处理方式，WebView变成空白页
            handler.proceed()//接受证书
        }
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

}
