package com.mqt.ganghuazhifu.activity

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
//import com.tencent.smtt.sdk.WebChromeClient
//import com.tencent.smtt.sdk.WebSettings
//import com.tencent.smtt.sdk.WebView
//import com.tencent.smtt.sdk.WebViewClient
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityMovableBinding
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.MD5Util
import com.orhanobut.logger.Logger
import java.util.Arrays

/**
 * 活动
 * @author yang.lei
 * @date 2014-12-24
 */
@SuppressLint("SetJavaScriptEnabled")
class MovableActivity : BaseActivity() {
    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var wSettings: WebSettings? = null
    private var activityMovableBinding: ActivityMovableBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMovableBinding = DataBindingUtil.setContentView<ActivityMovableBinding>(this,
                R.layout.activity_movable)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityMovableBinding!!.toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activityMovableBinding!!.webView.settings.javaScriptCanOpenWindowsAutomatically = true// 设置js可以直接打开窗口，如window.open()，默认为false
        activityMovableBinding!!.webView.settings.javaScriptEnabled = true// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        activityMovableBinding!!.webView.settings.setSupportZoom(false)// 是否可以缩放，默认true
        activityMovableBinding!!.webView.settings.builtInZoomControls = false// 是否显示缩放按钮，默认false
        activityMovableBinding!!.webView.settings.useWideViewPort = true// 设置此属性，可任意比例缩放。大视图模式
        activityMovableBinding!!.webView.settings.loadWithOverviewMode = true// 和setUseWideViewPort(true)一起解决网页自适应问题
        activityMovableBinding!!.webView.settings.setAppCacheEnabled(false)// 是否使用缓存
        activityMovableBinding!!.webView.settings.domStorageEnabled = false// DOM Storage
        activityMovableBinding!!.webView.settings.defaultTextEncodingName = "GBK"// 设置字符编码
        activityMovableBinding!!.webView.setWebViewClient(WebViewClientDemo())
        activityMovableBinding!!.webView.setWebChromeClient(WebChromeClientDemo())
        activityMovableBinding!!.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val url = drawsUrlLogin
        Logger.d("url--->" + url)
        activityMovableBinding!!.webView.loadUrl(url)
    }

    private // _wid,_api,_uid,_username,_groups,_avatar_url,_create_time
    val drawsUrlLogin: String
        get() {
            val api_key = "xixqrrwmjf67592a4d9713d38"
            val user = EncryptedPreferencesUtils.getUser()
            val create_time = System.currentTimeMillis().toString()
            val args0 = arrayOf("url_login", api_key.substring(0, 9), user.Uid, user.PhoneNb, "0", "", create_time)
            val args2 = arrayOf(api_key.substring(0, 9), "url_login", user.Uid, user.PhoneNb, "0", "", create_time)
            Arrays.sort(args2)
            val params = join(args2)
            val key = MD5Util.getMD5String(api_key + params)
            val args1 = newStringArray(args0, key)
            val url_login = "http://draws1.com/?api=%s&wid=%s&uid=%s&username=%s&groups=%s&avatar_url=%s&create_time=%s&key=%s"
            return String.format(url_login, *args1)
        }

    private fun newStringArray(args0: Array<String?>, key: String): Array<String?> {
        val n = arrayOfNulls<String?>(args0.size + 1)
        for (i in args0.indices) {
            n[i] = args0[i]
        }
        n[args0.size] = key
        return n
    }

    private fun join(args: Array<String?>): String {
        val sb = StringBuffer()
        for (ss in args) {
            sb.append(ss)
        }
        return sb.toString()
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


    private inner class WebChromeClientDemo : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            supportActionBar!!.title = title
        }
    }

}
