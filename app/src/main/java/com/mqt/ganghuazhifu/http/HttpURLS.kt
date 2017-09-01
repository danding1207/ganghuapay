package com.mqt.ganghuazhifu.http

import com.mqt.ganghuazhifu.BuildConfig

/**
 * url 统一管理
 * @author yang.lei
 * @date 2011.13.99
 */
object HttpURLS {

    /** 请求 IP   */
    val ip = BuildConfig.Httpurlip
    /** 快钱接收  */
    val ReceivePayUrl1 = BuildConfig.Receivepayurl1
    val ReceivePayUrl2 = BuildConfig.Receivepayurl2

    /** 注册协议  */
    val ganghua_user_agreement = ip + "/www/ganghua_user_agreement.html"

    /** 燃气费欠费查询 10011001  */
    val gasArrearsQuery = ip + "/ganghuazhifu/gas/gasArrearsQuery"
    /** 营业费欠费查询 10011002  */
    val gasBusinessQuery = ip + "/ganghuazhifu/gas/gasBusinessQuery"
    /** 燃气费账单查询 10011003  */
    val gasArrearsZhangDanQuery = ip + "/ganghuazhifu/gas/gasArrearsZhangDanQuery"
    /** 营业费账单查询 10011004  */
    val gasBusinessZhangDanQuery = ip + "/ganghuazhifu/gas/gasBusinessZhangDanQuery"
    /** 订单提交 10011005  */
    val orderSubmit = ip + "/ganghuazhifu/order/orderSubmit"
    /** 水费订单提交 10011029 */
    val waterOrderSubmit = ip + "/ganghuazhifu/order/waterOrderSubmit"
    /** 订单支付通知 10011006  */
    val gasPayNotify = ip + "/ganghuazhifu/order/gasPayNotify"
    /** 注册 10011007  */
    val registration = ip + "/ganghuazhifu/user/userRegist"
    /** 登录 10011008  */
    val login = ip + "/ganghuazhifu/user/userLogin"
    /** 用户信息修改 10011009  */
    val userUpdate = ip + "/ganghuazhifu/user/userUpdate"
    /**	注册校验 10011010   */
    val checkData = ip + "/ganghuazhifu/user/checkData"
    /** 取消订单 10011011  */
    val orderCancel = ip + "/ganghuazhifu/order/orderCancel"
    /** 根据城市编码查询收款单位 10011012  */
    val payeesQuery = ip + "/ganghuazhifu/common/payeesQuery"
    /** 根据上级城市编码查询下级城市编码 10011013  */
    val cityCodeQuery = ip + "/ganghuazhifu/common/cityCodeQuery"
    /** 发送短信并获取手机验证码 10011014  */
    val getVerificationCode = ip + "/ganghuazhifu/common/getVerificationCode"
    /** 手势密码验证 10011015  */
    val validateVerificationCode = ip + "/ganghuazhifu/user/validateVerificationCode"
    /** 插入意见反馈信息 10011016  */
    val feedback = ip + "/ganghuazhifu/common/feedback"
    /** 查询历史缴费户号 10011017  */
    val queryPayHistory = ip + "/ganghuazhifu/common/queryPayHistory"
    /** 退款申请 10011018   */
    val applyRefund = ip + "/ganghuazhifu/order/applyRefund"
    /** 支付确认 10011019   */
    val payConfirmation = ip + "/ganghuazhifu/order/payConfirmation"
    /** 获取支付数据 10011020  */
    val getPayData = ip + "/ganghuazhifu/order/getPayData"
    /** 联动优势获取支付数据 10011049  */
    val getPayDataForLDYS = ip + "/ganghuazhifu/order/getPayDataForLDYS"
    /** 威富通获取支付数据（10011058）*/
    val getPayDataForWFT = ip + "/ganghuazhifu/order/getPayDataForWFT"
    /** 联动优势确认支付 10011050  */
    val confirmPayForLDYS = ip + "/ganghuazhifu/order/confirmPayForLDYS"
    /** 获取联动优势绑定银行 10011052  */
    val getLDYSBanks = ip + "/ganghuazhifu/order/getLDYSBanks"
    /** 联动优势解绑银行 10011053  */
    val lDYSUnbundlingBank = ip + "/ganghuazhifu/order/lDYSUnbundlingBank"
    /** 删除历史缴费户号 10011023   */
    val deletePayHistory = ip + "/ganghuazhifu/common/deletePayHistory"
    /** 订单查询 10010000，查询码1001000002  */
    val processQuery = ip + "/ganghuazhifu/query/processQuery"
    /** 快钱请求   */
    val SendPayUrl = ip + "/wap/postOrderToKuaiQian.html"
    /** 快钱接收(请求)   */
    val BgPayUrl = ip + "/ganghuazhifu/order/gasPayNotify"
    /** 账单推送设置（10011032） */
    val PushNotice = ip + "/ganghuazhifu/common/setPushNotice"
    /** 南京燃气费欠费查询（10011033） */
    val njGasArrearsQuery = ip + "/ganghuazhifu/gas/njGasArrearsQuery"
    /** 宝华燃气费欠费查询（10011054） */
    val bhGasArrearsQuery = ip + "/ganghuazhifu/gas/bhGasArrearsQuery"
    /** 水费欠费查询（10011028） */
    val waterFeeQuery = ip + "/ganghuazhifu/water/waterFeeQuery"
    /** 水费账单查询(10011034)  */
    val waterBillQuery = ip + "/ganghuazhifu/water/waterZhangDanQuery"
    /** 前端获取验证码（10011030） */
    val imageValidateCode = ip + "/ganghuazhifu/common/getImageValidateCode"
    /** 校验验证码（10011035） */
    val validateCode = ip + "/ganghuazhifu/common/validateCode"
    /** 通知新闻查询（10011036） */
    val noticeNewsQuery = ip + "/ganghuazhifu/common/noticeNewsQuery"
    /** NFC加密串（10011037） */
    val nFCReadNumberLoopBackAndNFCSignMsg = ip + "/ganghuazhifu/order/nFCReadNumberLoopBackAndNFCSignMsg"
    /** NFC燃气表读书回抄（10011038） */
    val updateNFCPayStatus = ip + "/ganghuazhifu/order/updateNFCPayStatus"
    /** 图片处理（10011039） */
    val imageHandle = ip + "/ganghuazhifu/common/imageHandle"
    /** 员工获取二维码（10011040） */
    val getStaffQRcode = ip + "/ganghuazhifu/common/getStaffQRcode"
    /** 推送户号设置（10011041） */
    val setPushUserNb = ip + "/ganghuazhifu/common/setPushUserNb"
    /** 查询推送户号（10011042） */
    val queryPushUser = ip + "/ganghuazhifu/common/queryPushUser"
    /** 蓝牙读卡器加密机调用（10011044） */
    val bluetoothSignMsg = ip + "/ganghuazhifu/order/bluetoothSignMsg"
    /** 查询所有收款单位（10011045） */
    val payeesQueryAll = ip + "/ganghuazhifu/common/payeesQueryAll"
    /** 绑定常用联系人（10011046） */
    val addGeneralContact = ip + "/ganghuazhifu/common/addGeneralContact"
    /** 删除常用联系人（10011047） */
    val deleteGeneralContact = ip + "/ganghuazhifu/common/deleteGeneralContact"
    /** 修改常用联系人（10011048） */
    val updateGeneralContact = ip + "/ganghuazhifu/common/updateGeneralContact"
    /** IC卡客户信息查询（10011055） */
    val gasICMesQuery = ip + "/ganghuazhifu/gas/gasICMesQuery"
    /** IC卡气价查询（10011056） */
    val gasICPayMesQuery = ip + "/ganghuazhifu/gas/gasICPayMesQuery"
    /** 注册获取登录名（10011057） */
    val loginAccount = ip + "/ganghuazhifu/user/getLoginAccount"

}
