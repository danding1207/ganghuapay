package com.mqt.ganghuazhifu.activity

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityUsingHelpBinding
import com.mqt.ganghuazhifu.http.HttpURLS
import com.orhanobut.logger.Logger

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

//import com.tencent.smtt.sdk.WebView
//import com.tencent.smtt.sdk.WebViewClient

/**
 * 使用帮助

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UsingHelpActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var activityUsingHelpBinding: ActivityUsingHelpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUsingHelpBinding = DataBindingUtil.setContentView<ActivityUsingHelpBinding>(this, R.layout.activity_using_help)
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        setSupportActionBar(activityUsingHelpBinding!!.toolbar)
        supportActionBar!!.title = "使用帮助"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // 设置将接收各种通知和请求的WebViewClient（在WebView加载所有的链接）
        activityUsingHelpBinding!!.connectWebView.settings.javaScriptCanOpenWindowsAutomatically = true// 设置js可以直接打开窗口，如window.open()，默认为false
        activityUsingHelpBinding!!.connectWebView.settings.javaScriptEnabled = true// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        activityUsingHelpBinding!!.connectWebView.settings.setSupportZoom(false)// 是否可以缩放，默认true
        activityUsingHelpBinding!!.connectWebView.settings.builtInZoomControls = false// 是否显示缩放按钮，默认false
        activityUsingHelpBinding!!.connectWebView.settings.useWideViewPort = true// 设置此属性，可任意比例缩放。大视图模式
        activityUsingHelpBinding!!.connectWebView.settings.loadWithOverviewMode = true// 和setUseWideViewPort(true)一起解决网页自适应问题
        activityUsingHelpBinding!!.connectWebView.settings.setAppCacheEnabled(false)// 是否使用缓存
        activityUsingHelpBinding!!.connectWebView.settings.domStorageEnabled = false// DOM Storage
        activityUsingHelpBinding!!.connectWebView.settings.defaultTextEncodingName = "GBK"// 设置字符编码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activityUsingHelpBinding!!.connectWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        activityUsingHelpBinding!!.connectWebView.setWebViewClient(WebViewClientDemo())
        activityUsingHelpBinding!!.connectWebView.loadUrl(HttpURLS.ip + "/www/help.html")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        activityUsingHelpBinding!!.connectWebView.destroy()
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    private inner class WebViewClientDemo : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Logger.d("shouldOverrideUrlLoading-url:" + url)
            showRoundProcessDialog()
            view.loadUrl(url)// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            dismissRoundProcessDialog()
            Logger.d("onPageFinished-url:" + url)
        }
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {
            //handler.cancel(); 默认的处理方式，WebView变成空白页
            handler.proceed()//接受证书
        }
    }

}
