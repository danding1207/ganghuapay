package com.mqt.ganghuazhifu.bean

class FeedBack {

    var id: String? = ""//主键
    var loginaccount: String? = ""//登录账号
    var title: String? = ""//标题
    var feekbackquestion: String? = ""//反馈内容
    var contactway: String? = ""//联系方式
    var operationtime: String? = ""//操作时间
    var replytime: String? = ""//回复内容
    var replycontent: String? = ""//回复内容


    constructor(id: String, loginaccount: String, title: String, feekbackquestion: String, contactway: String, operationtime: String, replytime: String, replycontent: String) {
        this.id = id
        this.loginaccount = loginaccount
        this.title = title
        this.feekbackquestion = feekbackquestion
        this.contactway = contactway
        this.operationtime = operationtime
        this.replycontent = replycontent
        this.replytime = replytime
    }

    constructor() {
    }
}
