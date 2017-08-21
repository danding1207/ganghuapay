package com.mqt.ganghuazhifu.activity

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityUserAgreementBinding
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mqt.ganghuazhifu.http.HttpURLS
//import com.tencent.smtt.sdk.WebView
//import com.tencent.smtt.sdk.WebViewClient
import com.orhanobut.logger.Logger

/**
 * 用户协议
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UserAgreementActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var activityUserAgreementBinding: ActivityUserAgreementBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUserAgreementBinding = DataBindingUtil.setContentView<ActivityUserAgreementBinding>(this,
        R.layout.activity_user_agreement)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityUserAgreementBinding!!.toolbar)
        supportActionBar!!.title = "港华交易宝用户服务协议"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activityUserAgreementBinding!!.webView.settings.javaScriptCanOpenWindowsAutomatically = true// 设置js可以直接打开窗口，如window.open()，默认为false
        activityUserAgreementBinding!!.webView.settings.javaScriptEnabled = true// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        activityUserAgreementBinding!!.webView.settings.setSupportZoom(false)// 是否可以缩放，默认true
        activityUserAgreementBinding!!.webView.settings.builtInZoomControls = false// 是否显示缩放按钮，默认false
        activityUserAgreementBinding!!.webView.settings.useWideViewPort = true// 设置此属性，可任意比例缩放。大视图模式
        activityUserAgreementBinding!!.webView.settings.loadWithOverviewMode = true// 和setUseWideViewPort(true)一起解决网页自适应问题
        activityUserAgreementBinding!!.webView.settings.setAppCacheEnabled(false)// 是否使用缓存
        activityUserAgreementBinding!!.webView.settings.domStorageEnabled = false// DOM Storage
        activityUserAgreementBinding!!.webView.settings.defaultTextEncodingName = "GBK"// 设置字符编码
        activityUserAgreementBinding!!.webView.loadUrl(HttpURLS.ganghua_user_agreement)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        activityUserAgreementBinding!!.webView.destroy()
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
    }

}
