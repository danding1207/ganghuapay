package com.mqt.ganghuazhifu.bean

import org.parceler.Parcel

@Parcel
class BaiduPushData {

    var errorCode: String? = ""
    var appid: String? = ""
    var userId: String? = ""
    var channelId: String? = ""
    var requestId: String? = ""

    constructor() {
    }

    constructor(errorCode: String?, appid: String?, userId: String?, channelId: String?, requestId: String?) : super() {
        this.errorCode = errorCode
        this.appid = appid
        this.userId = userId
        this.channelId = channelId
        this.requestId = requestId
    }

    override fun toString(): String {
        val sb = StringBuffer()
        sb.append("\t|" + "errorCode： " + errorCode + "\n\t")
        sb.append("|" + "appid： " + appid + "\n\t")
        sb.append("|" + "userId： " + userId + "\n\t")
        sb.append("|" + "channelId： " + channelId + "\n\t")
        sb.append("|" + "requestId： " + requestId + "\n\t")
        return sb.toString()
    }

}
