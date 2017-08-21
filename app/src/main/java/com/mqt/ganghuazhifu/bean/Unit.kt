package com.mqt.ganghuazhifu.bean

import org.parceler.Parcel

@Parcel
class Unit {

    var flag: Boolean = false
    var PayeeNm: String = ""//收款单位名称
    var PayeeCode: String = ""//收款单位编号
    var Capital: String = ""

    constructor(payeeNm: String, payeeCode: String, capital: String) : super() {
        PayeeNm = payeeNm
        PayeeCode = payeeCode
        Capital = capital
        flag = false
    }

    constructor() : super()

}
