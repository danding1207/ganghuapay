package com.mqt.ganghuazhifu.bean

class Invoice {

    var id: String? = ""//主键

    var PayeeName: String? = null//收款单位名称
    var PayeeCode: String? = ""//收款单位编号
    var InvoiceAmount: String? = ""//发票金额
    var InvoiceTaitou: String? = ""//发票金额
    var InvoiceTime: String? = ""//发票时间

    constructor() {
    }

    constructor(id: String?, PayeeName: String?, PayeeCode: String?, InvoiceAmount: String?, InvoiceTaitou: String?, InvoiceTime: String?) {
        this.id = id
        this.PayeeName = PayeeName
        this.PayeeCode = PayeeCode
        this.InvoiceAmount = InvoiceAmount
        this.InvoiceTaitou = InvoiceTaitou
        this.InvoiceTime = InvoiceTime
    }
}
