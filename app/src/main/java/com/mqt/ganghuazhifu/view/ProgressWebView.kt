package com.mqt.ganghuazhifu.view

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.utils.UnitConversionUtils
import com.orhanobut.logger.Logger
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
//import com.tencent.smtt.export.external.interfaces.JsResult
//import com.tencent.smtt.sdk.WebChromeClient
//import com.tencent.smtt.sdk.WebView
import android.widget.AbsoluteLayout
import android.widget.ProgressBar
import com.tencent.bugly.crashreport.CrashReport

/**
 * 带进度条的WebView
 * @author 农民伯伯
 */
@Suppress("DEPRECATION")
@SuppressWarnings("deprecation")
class ProgressWebView : WebView {

    private var progressbar: ProgressBar? = null
    private var context: BaseActivity? = null

    constructor(context: Context) : super(context) {
        this.context = context as BaseActivity
        progressbar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        progressbar!!.layoutParams = AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.FILL_PARENT, UnitConversionUtils.dipTopx(getContext(), 8f), 0, 0)
        addView(progressbar)
        setWebChromeClient(MyWebChromeClient())
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.context = context as BaseActivity
        progressbar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        progressbar!!.layoutParams = AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT, UnitConversionUtils.dipTopx(getContext(), 8f), 0, 0)
        addView(progressbar)
        setWebChromeClient(MyWebChromeClient())
    }

    inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress == 100) {
                progressbar!!.visibility = View.GONE
            } else {
                if (progressbar!!.visibility == View.GONE)
                    progressbar!!.visibility = View.VISIBLE
                progressbar!!.progress = newProgress
            }
            // 增加Javascript异常监控
            CrashReport.setJavascriptMonitor(view, true)
            super.onProgressChanged(view, newProgress)
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            Logger.e(message)
            result.confirm()
            return true
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val lp = progressbar!!.layoutParams as AbsoluteLayout.LayoutParams
        lp.width = l
        lp.height = t
        progressbar!!.layoutParams = lp
        super.onScrollChanged(l, t, oldl, oldt)
    }
}