package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.afollestad.materialdialogs.MaterialDialog
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.AndroidToastForJs
import com.mqt.ganghuazhifu.bean.KuaiQian
import com.mqt.ganghuazhifu.databinding.ActivityKuaiQianPayBinding
import com.mqt.ganghuazhifu.http.HttpURLS
import com.orhanobut.logger.Logger
//import com.tencent.smtt.sdk.WebSettings
//import com.tencent.smtt.sdk.WebView
//import com.tencent.smtt.sdk.WebViewClient
import org.parceler.Parcels

/**
 * 联动优势支付——一键支付

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class KuaiQianPayActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var kuaiQian: KuaiQian? = null
    private var isSuccess = false
    private var activityKuaiQianPayBinding: ActivityKuaiQianPayBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityKuaiQianPayBinding = DataBindingUtil.setContentView<ActivityKuaiQianPayBinding>(this, R.layout.activity_kuai_qian_pay)
        kuaiQian = Parcels.unwrap<KuaiQian>(intent.getParcelableExtra<Parcelable>("KuaiQian"))
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityKuaiQianPayBinding!!.toolbar)
        supportActionBar!!.setTitle("支付订单")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // 设置将接收各种通知和请求的WebViewClient（在WebView加载所有的链接）
        activityKuaiQianPayBinding!!.webView.settings.javaScriptCanOpenWindowsAutomatically = true// 设置js可以直接打开窗口，如window.open()，默认为false
        activityKuaiQianPayBinding!!.webView.settings.javaScriptEnabled = true// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        activityKuaiQianPayBinding!!.webView.settings.setSupportZoom(true)// 是否可以缩放，默认true
        activityKuaiQianPayBinding!!.webView.settings.builtInZoomControls = false// 是否显示缩放按钮，默认false
        activityKuaiQianPayBinding!!.webView.settings.useWideViewPort = false// 设置此属性，可任意比例缩放。大视图模式
        activityKuaiQianPayBinding!!.webView.settings.loadWithOverviewMode = true// 和setUseWideViewPort(true)一起解决网页自适应问题
        activityKuaiQianPayBinding!!.webView.settings.setAppCacheEnabled(false)// 是否使用缓存
        activityKuaiQianPayBinding!!.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        activityKuaiQianPayBinding!!.webView.settings.domStorageEnabled = false// DOM Storage
        activityKuaiQianPayBinding!!.webView.settings.defaultTextEncodingName = "GBK"// 设置字符编码
        activityKuaiQianPayBinding!!.webView.settings.savePassword = false
        activityKuaiQianPayBinding!!.webView.setWebViewClient(WebViewClientDemo())
        activityKuaiQianPayBinding!!.webView.removeJavascriptInterface("searchBoxJavaBridge_")
        activityKuaiQianPayBinding!!.webView.removeJavascriptInterface("accessibility")
        activityKuaiQianPayBinding!!.webView.removeJavascriptInterface("accessibilityTraversal")
        if (kuaiQian != null) {
            activityKuaiQianPayBinding!!.webView.loadUrl("file:///android_asset/pay.html")
            activityKuaiQianPayBinding!!.webView.addJavascriptInterface(AndroidToastForJs(kuaiQian), "KuaiQianInterface")
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        val intent: Intent?
        when (v.id) {
//            R.id.ll_jine -> {
//                intent = Intent(this, UnityOrderDetailActivity::class.java)
//                intent.putExtra("LianDong", Parcels.wrap<LianDong>(lianDong))
//                startActivity(intent)
//            }
//            R.id.iv_bank_setting -> {
//                UnityChangeBanksActivity.startActivity(this, lianDong)
//            }
//            R.id.validateButton_unity_pay -> try {
//                getPayData(if (TextUtils.isEmpty(lianDong!!.orderid)) "" else lianDong!!.orderid,
//                        if (TextUtils.isEmpty(lianDong!!.paytype)) "" else lianDong!!.paytype,
//                        if (TextUtils.isEmpty(lianDong!!.gateid)) "" else lianDong!!.gateid)
//            } catch (e: UnsupportedEncodingException) {
//                e.printStackTrace()
//            }
//
//            R.id.tv_submit -> pay(lianDong!!.orderid, lianDong!!.paytype, lianDong!!.gateid, activityUnityQuickPayBinding!!.etPhoneYanzhengma.text.toString().trim { it <= ' ' })
//            else -> {
//            }
        }
    }

    private inner class WebViewClientDemo : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Logger.e("shouldOverrideUrlLoading-url:" + url!!)
            if (url.startsWith(HttpURLS.ReceivePayUrl1) || url.startsWith(HttpURLS.ReceivePayUrl2)) {
                //支付成功跳转
                UnityPayResultActivity.startActivity(this@KuaiQianPayActivity, kuaiQian!!.OrderId)
                isSuccess = true
            } else {
                showRoundProcessDialog()
                view!!.loadUrl(url)// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            }
            return true
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            Logger.e("errorCode:" + errorCode)
            Logger.e("description:" + description!!)
            Logger.e("failingUrl:" + failingUrl!!)
            // 用javascript隐藏系统定义的404页面信息
            // String data = "加载失败，请在网络良好时重试！";
            // view.loadUrl("javascript:document.body.innerHTML=\"" + data +
            // "\"");
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            dismissRoundProcessDialog()
            Logger.e("onPageFinished-url:" + url!!)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isSuccess) {
                UnityPayResultActivity.startActivity(this@KuaiQianPayActivity, kuaiQian!!.OrderId)
                return true
            } else {
                if (activityKuaiQianPayBinding!!.webView.canGoBack()) {
                    activityKuaiQianPayBinding!!.webView.goBack()
                    return true
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.finishAfterTransition()
                    } else {
                        this.finish()
                    }
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                if (isSuccess) {
                    UnityPayResultActivity.startActivity(this@KuaiQianPayActivity, kuaiQian!!.OrderId)
                    return true
                } else {
                    if (activityKuaiQianPayBinding!!.webView.canGoBack()) {
                        activityKuaiQianPayBinding!!.webView.goBack()
                        return true
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            this.finishAfterTransition()
                        } else {
                            this.finish()
                        }
                    }
                    return true
                }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun startActivity(context: Context, kuaiQian: KuaiQian?) {
            val intent = Intent(context, KuaiQianPayActivity::class.java)
            intent.putExtra("KuaiQian", Parcels.wrap(kuaiQian))
            context.startActivity(intent)
        }
    }

}
