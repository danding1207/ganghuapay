package com.mqt.ganghuazhifu.view

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.activity.WelcomeActivity
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils

class LoadingDialog(context: Context) : Dialog(context, R.style.dialog) {

    var tag: String? = null

    init {
        val views = LayoutInflater.from(context)
                .inflate(R.layout.loading_dialog, null)
        this.setContentView(views)
        val p = window!!.attributes // 获取对话框当前的参数值
        if (WelcomeActivity.screenwidth == 0 || WelcomeActivity.screenhigh == 0) {
            WelcomeActivity.screenwidth = EncryptedPreferencesUtils.getScreenSize()[0]
            WelcomeActivity.screenhigh = EncryptedPreferencesUtils.getScreenSize()[1]
        }
        p.width = WindowManager.LayoutParams.WRAP_CONTENT
        p.height = WindowManager.LayoutParams.WRAP_CONTENT
        this.window!!.attributes = p
    }

}
