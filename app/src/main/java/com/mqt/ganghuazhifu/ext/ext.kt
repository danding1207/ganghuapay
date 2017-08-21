package com.mqt.ganghuazhifu.ext

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import okhttp3.RequestBody

/**
 * Created by danding1207 on 16/10/20.
 */

fun Context.Toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Activity.post(url: String, isShow: Boolean = true, tag: String, body: RequestBody?, requestListener: OnHttpRequestListener?) {
    HttpRequest.instance.httpPost(this, url, isShow, tag, body, requestListener)
}

fun Activity.showRoundProcessDialog( tag: String) {
//    HttpRequest.instance.httpGet(this, url, isShow, tag, requestListener)
}

fun Activity.get(url: String, isShow: Boolean = true, tag: String, requestListener: OnHttpRequestListener?) {
    HttpRequest.instance.httpGet(this, url, isShow, tag, requestListener)
}



fun Context.isNotEmpty(message: String?): Boolean {
    return !TextUtils.isEmpty(message)
}
