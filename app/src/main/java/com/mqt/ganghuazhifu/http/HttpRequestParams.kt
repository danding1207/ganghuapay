package com.mqt.ganghuazhifu.http

import android.annotation.SuppressLint
import android.text.TextUtils
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.utils.AESCipher
import com.mqt.ganghuazhifu.utils.Encrypt
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.MD5Util
import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat
import java.util.*

/**
 * Post请求 参数 统一 生成，管理类
 * @author yang.lei
 */
object HttpRequestParams {

    /**
     * 登录
     * @param userName         用户名
     * @param password         密码
     * @param appid            系统返回的应用标识
     * @param channelId        系统返回的设备连接的通道标识
     * @param userId           系统返回的绑定Baidu Channel的用户标识
     * @param device_type      设备类型 Android设备：10 IOS设备：11
     * @param VerificationCode 验证码
     * @param ImageKey         验证码图片key
     * @return
     */
    fun getParamsForLogin(userName: String?, password: String?, appid: String?, userId: String?,
                          channelId: String?, device_type: String?, VerificationCode: String?, ImageKey: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011008")
                .add("LoginAccount", userName ?: "")
                .add("LoginPwd", password ?: "")
                .add("AppId", appid ?: "")
                .add("UserId", userId ?: "")
                .add("ChannelId", channelId ?: "")
                .add("DeviceType", device_type ?: "")
                .add("DeviceToken", App.phoneInfo!!.imei ?: "")
                .add("VerificationCode", VerificationCode ?: "")
                .add("ImageKey", ImageKey ?: "")
                .add("MobileTypeNo", (App.phoneInfo!!.brand ?: "") + ":" + (App.phoneInfo!!.model ?: ""))
                .add("MobileSystemNo", "Android:" + (App.phoneInfo!!.release ?: ""))
                .add("DeviceId", App.phoneInfo!!.getsID() ?: "")
        return getParamsRequestHead(builder, userName)
    }

    /**
     * 发送短信并获取手机验证码
     * @param phoneNb          手机号码
     * @param getType          获取验证码类型:01：注册;02：找回密;03：修改密码;04：修改安保
     * @param verificationCode 图片验证码
     * @param imageKey         验证码图片key
     * @return
     */
    fun getParamsForVerificationCode(phoneNb: String, getType: String?, verificationCode: String?,
                                     imageKey: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011014")
                .add("PhoneNb", if (getType != "02") phoneNb else "")
                .add("GetType", getType ?: "")
                .add("VerificationCode", verificationCode ?: "")
                .add("ImageKey", imageKey ?: "")
        return getParamsRequestHead(builder, phoneNb)
    }

    /**
     * 发送短信并获取手机验证码
     * @param phoneNb          手机号码
     * @param getType          获取验证码类型:01：注册;02：找回密;03：修改密码;04：修改安保
     * @param verificationCode 图片验证码
     * @param imageKey         验证码图片key
     * @return
     */
    fun getParamsForChangePhoneVerificationCode(phoneNb: String, getType: String?, verificationCode: String?,
                                                imageKey: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011014")
                .add("PhoneNb", if (getType != "02") phoneNb else "")
                .add("GetType", getType ?: "")
                .add("VerificationCode", verificationCode ?: "")
                .add("ImageKey", imageKey ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 注册校验
     * @param checkData 校验数据
     * @param checkType 校验类型:01：登录账号;02：校验手机号;03：校验邮箱
     * @return
     */
    fun getParamsForcheckData(checkData: String?, checkType: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011010")
                .add("CheckData", checkData ?: "")
                .add("CheckType", checkType ?: "")
        return getParamsRequestHead(builder, checkData)
    }

    /**
     * 注册
     * @param PhoneNb       手机号
     * @param LoginPwd      密码
     * @param LoginAccount  用户名
     * @param PayeeCode     收款单位编号
     * @param VerificationKey     手机验证码key
     * @param VerificationCode    手机验证码
     * @return
     */
    fun getParamsForRegistration(PhoneNb: String?, LoginPwd: String, LoginAccount: String?,
                                 PayeeCode: String?, VerificationKey: String?, VerificationCode: String?): CusFormBody {

        val Pwd = Encrypt.SHA256(Encrypt.MD5(LoginPwd).toUpperCase(Locale.CHINA) + Encrypt.MD5(LoginPwd).toUpperCase(Locale.CHINA).substring(0, 6)).toUpperCase(Locale.CHINA)

        Logger.e(Encrypt.MD5(LoginPwd).toUpperCase(Locale.CHINA))
        Logger.e(MD5Util.getMD5String(LoginPwd).toUpperCase(Locale.CHINA).substring(0, 6))
        Logger.e(Pwd)

        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011007")
                .add("PhoneNb", PhoneNb ?: "")
                .add("LoginPwd", Pwd)
                .add("LoginAccount", LoginAccount ?: "")
                .add("PhoneFlag", "1")
                .add("PayeeCode", PayeeCode ?: "")
                .add("EmailFlag", "2")
                .add("VerificationKey", VerificationKey ?: "")
                .add("VerificationCode", VerificationCode ?: "")
        return getParamsRequestHead(builder, PhoneNb)
    }

    /**
     * 水费欠费查询(10011028)
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param UserNb       户号或者速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForWaterFee(ProvinceCode: String?, CityCode: String?, UserNb: String?,
                             PayeeCode: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011028")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 水费账单查询(10011034)
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param UserNb       户号或者速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForWaterBill(ProvinceCode: String?, CityCode: String?, UserNb: String?,
                              PayeeCode: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011034")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 燃气费欠费查询
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param UserNb       户号或者速记号
     * @param NbType       号码类别 1: 用户号 ;2: 速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForGasFee(ProvinceCode: String?, CityCode: String?, UserNb: String?, NbType: String?,
                           PayeeCode: String?, CheckType: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011001")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("NbType", NbType ?: "")
        if (CheckType != null)
            builder.add("CheckType", CheckType)
        return getParamsRequestHead(builder, null)
    }

    /**
     * 燃气费欠费查询（蓝牙充值）
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param UserNb       户号或者速记号
     * @param NbType       号码类别 1: 用户号 ;2: 速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForGasFeeBlue(ProvinceCode: String?, CityCode: String?, UserNb: String?, NbType: String?,
                               PayeeCode: String?, CheckType: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011001")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("NbType", NbType ?: "")
                .add("BluetoothFlag", "12")
        if (CheckType != null)
            builder.add("CheckType", CheckType)
        return getParamsRequestHead(builder, null)
    }

    /**
     * 燃气费欠费查询
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param UserNb       户号或者速记号
     * @param NbType       号码类别 1: 用户号 ;2: 速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForGasICMesQuery(ProvinceCode: String?, CityCode: String?, UserNb: String?, NbType: String?,
                                  PayeeCode: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011055")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("NbType", NbType ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 燃气费欠费查询
     * @param UserNb       省编码
     * @param ICcardNo     IC卡号
     * @param UserCanAmount 购买气量
     * @param PayeeCode    收款单位编号
     * @param NoType    号码类别
     * @param Arrears    收款单位编号
     * @param QueryId    收款单位编号
     * @return
     */
    fun getParamsForGasICPayMesQuery( UserNb: String?, ICcardNo: String?,
                                      UserCanAmount: String?, PayeeCode: String?,
                                      NoType: String?, Arrears: String?, QueryId: String?
    ): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011056")
                .add("ICcardNo", ICcardNo ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("UserCanAmount", UserCanAmount ?: "")
                .add("NoType", NoType ?: "")
                .add("QueryId", QueryId ?: "")
        return getParamsRequestHead(builder, null)
    }




    /**
     * 南京燃气费欠费查询
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param UserNb       户号或者速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForNanJingGasFee(ProvinceCode: String?, CityCode: String?, UserNb: String?,
                                  PayeeCode: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011033")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 南京燃气费欠费查询（蓝牙充值）
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param UserNb       户号或者速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForNanJingGasFeeBlue(ProvinceCode: String?, CityCode: String?, UserNb: String?,
                                      PayeeCode: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011033").add("ProvinceCode", ProvinceCode ?: "").add("CityCode", CityCode ?: "").add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("BluetoothFlag", "12")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 宝华燃气费欠费查询
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param UserNb       户号或者速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForBaoHuaGasFee(ProvinceCode: String?, CityCode: String?, UserNb: String?,
                                 PayeeCode: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011054").add("ProvinceCode", ProvinceCode ?: "").add("CityCode", CityCode ?: "").add("UserNb", UserNb ?: "").add("PayeeCode", PayeeCode ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 宝华燃气费欠费查询（蓝牙充值）
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param UserNb       户号或者速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForBaoHuaGasFeeBlue(ProvinceCode: String?, CityCode: String?, UserNb: String?,
                                     PayeeCode: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011054").add("ProvinceCode", ProvinceCode ?: "").add("CityCode", CityCode ?: "").add("UserNb", UserNb ?: "").add("PayeeCode", PayeeCode ?: "")
                .add("BluetoothFlag", "12")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 查询历史缴费户号（10011017）
     * @param Pmttp 业务类型 :010001:气费;010002:营业费;020001:水务
     * @return
     */
    fun getParamsForQueryPayHistory(Pmttp: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011017")
                .add("Pmttp", Pmttp ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 删除历史缴费户号（10011023）
     * @param Pmttp        业务类型 :010001:气费;010002:营业费;
     * @param LoginAccount 登录账号
     * @param PayeeCode    收款单位编号
     * @param UserNb       户号
     * @return
     */
    fun getParamsForQueryCancleHistory(Pmttp: String?, LoginAccount: String?, PayeeCode: String?,
                                       UserNb: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011023")
                .add("Pmttp", Pmttp ?: "")
                .add("LoginAccount", LoginAccount ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("UserNb", UserNb ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 营业费欠费查询
     * @param UserNb       户号或者速记号
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param NbType       号码类别 1: 用户号 ;2: 速记号
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForBusiFee(ProvinceCode: String?, CityCode: String?, UserNb: String?, NbType: String?,
                            PayeeCode: String?, CheckType: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011002")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("NbType", NbType ?: "")
                .add("CheckType", CheckType ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 气费账单查询
     * @param UserNb       户号或者速记号
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param NbType       号码类别 1: 用户号 ;2: 速记号
     * @param StartDate    起始月份
     * @param EndDate      结束月份
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForGasBill(ProvinceCode: String?, CityCode: String?, UserNb: String?, NbType: String?,
                            PayeeCode: String?, StartDate: String?, EndDate: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011003")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("NbType", NbType ?: "")
                .add("StartDate", StartDate ?: "")
                .add("EndDate", EndDate ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 营业费账单查询
     * @param UserNb       户号或者速记号
     * @param ProvinceCode 省编码
     * @param CityCode     市编码
     * @param NbType       号码类别 1: 用户号 ;2: 速记号
     * @param StartDate    起始月份
     * @param EndDate      结束月份
     * @param PayeeCode    收款单位编号
     * @return
     */
    fun getParamsForBusiBill(ProvinceCode: String?, CityCode: String?, UserNb: String?, NbType: String?,
                             PayeeCode: String?, StartDate: String?, EndDate: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011004")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("NbType", NbType ?: "")
                .add("StartDate", StartDate ?: "")
                .add("EndDate", EndDate ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 订单提交
     * @param Pmttp        业务类型
     * @param Amount       交易金额
     * @param UserNb       户号
     * @param UserNm       户名
     * @param UserAddr     用户地址
     * @param CityCode     收款单位所在市编码
     * @param ProvinceCode 收款单位所在省编码
     * @param PayeeCode    收款单位编码
     * @param LoginAccount 登录账号
     * @param FeeCount     账期数目 南京港华需要上送
     * @param QuerySeq     查询序列号 南京港华需要上送
     * @param DueMonth     缴费账期 宝华燃气需要上送
     * @param NFCFlag      NFC标识
     * @param PrestoreFlag 预存标识
     * @param StaffPhone   员工手机号
     * @param NFCICSumCount   NFCIC卡购气次数
     * @param ICcardNo   IC卡号
     * @param Mount   充值气量
     * @return
     */
    fun getParamsForOrderSubmit(Pmttp: String?, Amount: String?, UserNb: String?, UserNm: String?,
                                UserAddr: String?, CityCode: String?, ProvinceCode: String?, PayeeCode: String?, LoginAccount: String?,
                                FeeCount: String?, QuerySeq: String?, DueMonth: String?, NFCFlag: String?, PrestoreFlag: String?,
                                StaffPhone: String?, NFCICSumCount: String?, ICcardNo: String?, QueryId: String?
                                , Mount: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011005")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("Pmttp", Pmttp ?: "")
                .add("Amount", Amount ?: "")
                .add("Mount", Mount ?: "0")
                .add("UserNm", UserNm ?: "")
                .add("UserAddr", UserAddr ?: "")
                .add("LoginAccount", LoginAccount ?: "")
                .add("QueryId", QueryId ?: "")
        if (FeeCount != null)
            builder.add("FeeCount", FeeCount)
        if (QuerySeq != null)
            builder.add("QuerySeq", QuerySeq)
        if (DueMonth != null)
            builder.add("DueMonth", DueMonth)
        if (NFCFlag != null)
            builder.add("NFCFlag", NFCFlag)
        if (PrestoreFlag != null)
            builder.add("PrestoreFlag", PrestoreFlag)
        if (StaffPhone != null)
            builder.add("StaffPhone", StaffPhone)
        if (ICcardNo != null)
            builder.add("ICcardNo", ICcardNo)
        if (NFCICSumCount != null)
            builder.add("NFCICSumCount", NFCICSumCount)
        return getParamsRequestHead(builder, null)
    }

    /**
     * 水费订单提交(10011029)
     * @param Pmttp          业务类型
     * @param Amount         交易金额
     * @param UserNb         户号
     * @param UserNm         户名
     * @param UserAddr       用户地址
     * @param CityCode       收款单位所在市编码
     * @param ProvinceCode   收款单位所在省编码
     * @param PayeeCode      收款单位编码
     * @param LoginAccount   登录账号
     * @param FeeCountDetail 可循环内容
     * @param QueryId QueryId
     * @return
     */
    fun getParamsForWaterOrderSubmit(Pmttp: String?, Amount: String?, UserNb: String?, UserNm: String?,
                                     UserAddr: String?, CityCode: String?, ProvinceCode: String?, PayeeCode: String?, LoginAccount: String?,
                                     FeeCountDetail: String?, QueryId: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011029")
                .add("ProvinceCode", ProvinceCode ?: "")
                .add("CityCode", CityCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("Pmttp", Pmttp ?: "")
                .add("Amount", Amount ?: "")
                .add("UserNm", UserNm ?: "")
                .add("UserAddr", UserAddr ?: "")
                .add("LoginAccount", LoginAccount ?: "")
                .add("FeeCountDetail", FeeCountDetail ?: "")
                .add("QueryId", QueryId ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 获取快钱支付数据（10011020）
     * @param OrderNb    订单号
     * @param PayChannel 支付渠道 01:快钱
     * @param BgUrl      服务器接收支付结果的地址
     * @return
     */
    fun getParamsForGetPayData(OrderNb: String?, PayChannel: String?, BgUrl: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011020")
                .add("OrderNb", OrderNb ?: "")
                .add("PayChannel", PayChannel ?: "")
                .add("BgUrl", BgUrl ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 获取联动优势支付数据（10011049）
     * @param order_id      订单号
     * @param pay_type      支付方式
     * @param gate_id       支付银行
     * @param media_id      手机号
     * @param card_id       卡号 (需加密)
     * @param identity_type 证件类型
     * @param identity_code 证件号 (需加密)
     * @param card_holder   持卡人姓名 (需加密)
     * @param valid_date    信用卡有效期 (需加密)
     * @param cvv2          信用卡CVN2/CVV2 (需加密)
     * @param isnot_agree   是否同意协议
     * @param trade_no      U付订单号
     * @param req_mode      请求模式 10：选择联动优势, 11：选择银行, 12：发送短信
     * @return
     */
    fun getParamsForGetPayData(order_id: String, pay_type: String, gate_id: String,
                               media_id: String, card_id: String, identity_type: String, identity_code: String, card_holder: String,
                               valid_date: String, cvv2: String, isnot_agree: String, trade_no: String, req_mode: String): CusFormBody {
        val user = EncryptedPreferencesUtils.getUser()
        val key = user.SessionId!!.substring(0, 16)
        Logger.e("key--->" + key)
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011049")
                .add("orderid", order_id)
                .add("paytype", pay_type)
                .add("gateid", gate_id)
                .add("mediaid", media_id)
                .add("cardid", AESCipher.encryptAES(card_id, key))
                .add("identitytype", identity_type)
                .add("identitycode", AESCipher.encryptAES(identity_code, key))
                .add("cardholder", AESCipher.encryptAES(card_holder, key))
                .add("validdate", AESCipher.encryptAES(valid_date, key))
                .add("cvv2", AESCipher.encryptAES(cvv2, key))
                .add("isnotagree", isnot_agree)
                .add("tradeno", trade_no)
                .add("reqmode", req_mode)
        return getParamsRequestHead(builder, null)
    }

    /**
     * 威富通获取支付数据（10011058）
     * @param OrderNb      订单号
     * @param PayChannel      支付渠道  07-01：威富通-微信
     * @return
     */
    fun getParamsForGetPayData(order_id: String): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011058")
                .add("OrderNb", order_id)
                .add("AuthCode", "")
                .add("PayChannel", "07-01")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 获取联动优势支付数据（10011050）
     * @param orderid      订单号
     * @param verifycode    验证码
     * @param mediaid      手机号
     * @param cardid       卡号  (需加密)
     * @param tradeno       U付订单号
     * @param identitycode 证件号  (需加密)
     * @param cardholder   持卡人姓名  (需加密)
     * @param validdate    信用卡有效期  (需加密)
     * @param cvv2          信用卡CVN2/CVV2  (需加密)
     * @return
     */
    fun getParamsForconfirmPayForLDYS(orderid: String?, paytype: String?, gateid: String?,
                                      verifycode: String?, mediaid: String?, cardid: String?, tradeno: String?, identitycode: String?,
                                      cardholder: String?, validdate: String?, cvv2: String?): CusFormBody {
        val user = EncryptedPreferencesUtils.getUser()
        val key = user.SessionId!!.substring(0, 16)
        Logger.e("key--->" + key)
        val builder = CusFormBody.Builder().add("TransactionId", "10011050")
                .add("orderid", orderid ?: "")
                .add("gateid", gateid ?: "")
                .add("paytype", paytype ?: "")
                .add("verifycode", verifycode ?: "")
                .add("mediaid", mediaid ?: "")
                .add("cardid", AESCipher.encryptAES(cardid, key))
                .add("tradeno", tradeno ?: "")
                .add("identitycode", AESCipher.encryptAES(identitycode, key))
                .add("cardholder", AESCipher.encryptAES(cardholder, key))
                .add("validdate", AESCipher.encryptAES(validdate, key))
                .add("cvv2", AESCipher.encryptAES(cvv2, key))
        return getParamsRequestHead(builder, null)
    }

    /**
     * 获取联动优势绑定银行（10011052）
     * @param LoginAccount 登录账号
     * @return
     */
    fun getParamsForGetLDYSBanks(LoginAccount: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011052").add("LoginAccount", LoginAccount ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 联动优势解绑银行（10011053）
     * @param LoginAccount 登录账号
     * @param paytype      支付方式
     * @param gateid       支付银行
     * @param cardid       支付银行
     * @return
     */
    fun getParamsForlDYSUnbundlingBank(LoginAccount: String?, paytype: String?, gateid: String?, cardid: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011053").add("LoginAccount", LoginAccount ?: "")
                .add("paytype", paytype ?: "")
                .add("gateid", gateid ?: "")
                .add("cardid", cardid ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 获取NFC加密串（10011037）
     * @param OrderNb            订单号
     * @param NFCPayTime         NFC充值次数
     * @param UserNb             户号
     * @param CurrentTime        表当前时间
     * @param CurrentPrice       表当前单价
     * @param SurplusPrice       剩余金额
     * @param SumBuyPrice        表累计购气金额
     * @param NoUserDays         没有用气天数
     * @param NoUserSeconds      没有用气秒数
     * @param CurrentStatus      表当前状态
     * @param PurchaseStatus     消费状态字
     * @param SumTotal           表累计用量
     * @param SumTotalTwentyFour 最近24个月的累计用气量集合(24条)
     * @param SecurityCheckNb    安检返回条数
     * @param ThreeSecurityCheck 3条安检记录信息(3条)
     * @param NFCPayAmount       NFC购气金额
     * @param NFCPaySumAmount    NFC购气总金额
     * @param Random             NFC加密随机数
     * @return
     */
    fun getParamsForGetNFCCode(OrderNb: String?, NFCPayTime: Int, UserNb: String?, CurrentTime: String?,
                               CurrentPrice: String?, SurplusPrice: String?, SumBuyPrice: String?, NoUserDays: String?, NoUserSeconds: String?,
                               CurrentStatus: String?, PurchaseStatus: String?, SumTotal: String?, SumTotalTwentyFour: String?,
                               SecurityCheckNb: String?, ThreeSecurityCheck: String?, NFCPayAmount: String?, NFCPaySumAmount: String?,
                               Random: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011037")
        if (OrderNb != null) {
            builder.add("OrderNb", OrderNb)
                    .add("NFCPayTime", NFCPayTime.toString())
                    .add("UserNb", UserNb ?: "")
                    .add("CurrentTime", CurrentTime ?: "")
                    .add("CurrentPrice", CurrentPrice ?: "")
                    .add("SurplusPrice", SurplusPrice ?: "")
                    .add("SumBuyPrice", SumBuyPrice ?: "")
                    .add("NoUserDays", NoUserDays ?: "")
                    .add("NoUserSeconds", NoUserSeconds ?: "")
                    .add("CurrentStatus", CurrentStatus ?: "")
                    .add("PurchaseStatus", PurchaseStatus ?: "")
                    .add("SumTotal", SumTotal ?: "")
                    .add("SumTotalTwentyFour", if (SumTotalTwentyFour != null && SumTotalTwentyFour.length > 0) SumTotalTwentyFour else "")
                    .add("SecurityCheckNb", SecurityCheckNb ?: "")
                    .add("ThreeSecurityCheck", if (ThreeSecurityCheck != null && ThreeSecurityCheck.length > 0) ThreeSecurityCheck else "")
                    .add("NFCPayAmount", NFCPayAmount ?: "")
                    .add("NFCPaySumAmount", NFCPaySumAmount ?: "")
        }
        if (Random != null)
            builder.add("Random", Random)
        return getParamsRequestHead(builder, null)
    }

    /**
     * NFC燃气表读书回抄（10011038）
     * @param OrderNb             订单号
     * @param NFCPayStatus        NFC充值状态
     * @param TcisModifyPriceCode TCIS系统调价生效日时表止码
     * @param ModifyPriceCode     表具调价生效时表止码
     * @return
     */
    fun getParamsForUpdateNFCPayStatus(OrderNb: String?, NFCPayStatus: String?,
                                       TcisModifyPriceCode: String?, ModifyPriceCode: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011038")
                .add("OrderNb", OrderNb ?: "")
                .add("NFCPayStatus", NFCPayStatus ?: "")
                .add("TcisModifyPriceCode", TcisModifyPriceCode ?: "")
                .add("ModifyPriceCode", ModifyPriceCode ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 订单记录查询
     * @param status         交易状态
     * @param pmttp          业务类型
     * @param amountflow     资金流向
     * @param ordernb        订单号
     * @param startamount    起始金额
     * @param endamount      结束金额
     * @param startorderdate 订单起始时间
     * @param endorderdate   业务结束时间
     * @param page           分页数
     * @param usernm         户名
     * @param usernb         户号
     * @param useraddr       用户地址
     * @return
     */
    fun getParamsForProcessQuery(usernm: String?, usernb: String?, useraddr: String?, status: String?,
                                 statusone: String?, statustwo: String?, pmttp: String?, amountflow: String?, ordernb: String?, payeecode: String?,
                                 startamount: String?, endamount: String?, startorderdate: String?, endorderdate: String?, page: Int): CusFormBody {


        val builder = CusFormBody.Builder()
                .add("TransactionId", "10010000")
                .add("QueryCd", "1001000002")
                .add("TurnPageBeginPos", (page * 20 + 1).toString())
                .add("TurnPageShowNum", "20")
                .add("usernm", usernm ?: "")
                .add("usernb", usernb ?: "")
                .add("status", status ?: "")
                .add("statusone", statusone ?: "")
                .add("statustwo", statustwo ?: "")
                .add("pmttp", pmttp ?: "")
                .add("amountflow", amountflow ?: "")
                .add("ordernb", ordernb ?: "")
                .add("payeecode", payeecode ?: "")
                .add("useraddr", useraddr ?: "")
                .add("startamount", startamount ?: "")
                .add("endamount", endamount ?: "")
                .add("startorderdate", startorderdate ?: "")
                .add("endorderdate", endorderdate ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 支付确认
     * @param orderNb    订单号
     * @param payChannel 支付渠道:01:快钱
     * @return
     */
    fun getParamsForOrderConfig(orderNb: String?, payChannel: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011019")
                .add("OrderNb", orderNb ?: "")
                .add("PayChannel", payChannel ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 取消订单
     * @param orderNb 订单号
     * @return
     */
    fun getParamsForOrderCancle(orderNb: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011011").add("OrderNb", orderNb ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 申请退款
     * @param orderNb 订单号
     * @return
     */
    fun getParamsForOrderRefund(orderNb: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011018").add("OrderNb", orderNb ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 手势密码验证（10011015）
     * @param loginAccount 登录账号
     * @param GesturePwd   手势密码
     * @return
     */
    fun getParamsForValidateGestureCode(loginAccount: String?, GesturePwd: String): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011015")
                .add("loginAccount", loginAccount ?: "")
                .add("GesturePwd", MD5Util.getMD5String(GesturePwd).toUpperCase(Locale.CHINA))
        return getParamsRequestHead(builder, loginAccount)
    }

    /**
     * 用户查询（10010000，查询码1001000001）
     * @param loginAccount 登录账号
     * @return
     */
    fun getParamsForAccountInfo(loginAccount: String, QueryCd: String): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10010000")
                .add("QueryCd", QueryCd)
                .add("TurnPageBeginPos", "1")
                .add("TurnPageShowNum", "10")
        return getParamsRequestHead(builder, loginAccount)
    }

    /**
     * 用户查询（10010000，查询码1001000005）
     * @param loginAccount 登录账号
     * @return
     */
    fun getParamsForGeneralContact(loginAccount: String?, userId: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10010000")
                .add("QueryCd", "1001000005")
                .add("UserId", userId ?: "")
                .add("TurnPageBeginPos", "1").add("TurnPageShowNum", "10")
        return getParamsRequestHead(builder, loginAccount)
    }

    /**
     * 意见反馈查询（10010000，查询码1001000006）
     * @param loginaccount 登录账号
     * @param id 主键
     * @return
     */
    fun getParamsForFeedbacks(loginaccount: String, id: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10010000")
                .add("QueryCd", "1001000006")
                .add("id", id ?: "")
                .add("loginaccount", loginaccount)
                .add("TurnPageBeginPos", "1").add("TurnPageShowNum", "10")
        return getParamsRequestHead(builder, loginaccount)
    }

    /**
     * 绑定常用联系人（10011046）
     * @param UserId       登录用户主键
     * @param PayeeCode    收款单位编号
     * @param UserNb       户号
     * @param UserNm       户名
     * @param IsDefault    是否默认联系人
     * @param Remark       备注
     * @return
     */
    fun getParamsForAddGeneralContact(UserId: String?,
                                      PayeeCode: String?, UserNb: String?, UserNm: String?, IsDefault: String?, Remark: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011046")
                .add("UserId", UserId ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("UserNb", UserNb ?: "")
                .add("UserNm", UserNm ?: "")
                .add("IsDefault", IsDefault ?: "")
                .add("Remark", Remark ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 删除常用联系人（10011047）
     * @param Id 常用联系人主键
     * @return
     */
    fun getParamsForDeleteGeneralContact(Id: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011047").add("Id", Id ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 修改常用联系人（10011048）
     * @param Id        常用联系人主键
     * @param Remark    备注
     * @param IsDefault 是否默认联系人
     * @return
     */
    fun getParamsForUpdateGeneralContact(Id: String?, Remark: String?, IsDefault: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011048").add("Id", Id ?: "").add("Remark", Remark ?: "").add("IsDefault", IsDefault ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 账单推送设置（10011032）
     * @param loginAccount 登录账号
     * @param setType      设置类型：11为关 10为
     * @return
     */
    fun getPushStatus(loginAccount: String?, setType: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011032").add("LoginAccount", loginAccount ?: "").add("SetType", setType ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 根据城市编码查询收款单位（10011012）
     * @param cityCode 城市编码
     * @param pmttpB   业务类型大类
     * @return
     */
    fun getParamsForPayeesQuery(cityCode: String?, pmttpB: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011012")
                .add("CityCode", cityCode ?: "")
                .add("PmttpB", pmttpB ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 根据上级城市编码查询下级城市编码（10011013）
     * @param cityCode  城市编码
     * @param queryType 查询类型 01：查询省市城市代码送086 02：根据省市代码查询下级市（城市代码送省级城市代码）
     *                           03：查询市级城市（城市代码送%%0000）
     * @return
     */
    fun getParamsForCityCodeQuery(cityCode: String?, queryType: String?, pmtty: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011013")
                .add("CityCode", cityCode ?: "")
                .add("QueryType", queryType ?: "")
                .add("Pmttp", pmtty ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 重置密码
     * @param LoginAccount 登录账号
     * @param OldPwd       老密码
     * @param NewPwd       新密码
     * @param VerificationKey  手机验证码key
     * @param VerificationCode   手机验证码
     * @return
     */
    fun getParamsForSetNewPWD(LoginAccount: String?, OldPwd: String, NewPwd: String, VerificationKey: String?, VerificationCode: String?): CusFormBody {

        val NewPwd = Encrypt.SHA256(Encrypt.MD5(NewPwd).toUpperCase(Locale.CHINA)
                + Encrypt.MD5(NewPwd).toUpperCase(Locale.CHINA).substring(0, 6)).toUpperCase(Locale.CHINA)

        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011009")
                .add("ModifyType", "A")
                .add("OldPwd", OldPwd)
                .add("NewPwd", NewPwd)
                .add("VerificationKey", VerificationKey ?: "")
                .add("VerificationCode", VerificationCode ?: "")
                .add("LoginAccount", LoginAccount ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 重置手势密码
     * @param LoginAccount 登录账号
     * @param GesturePwd   手势密码
     * @return
     */
    fun getParamsForSetNewGeustPWD(LoginAccount: String?, GesturePwd: String): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011009")
                .add("ModifyType", "I")
                .add("GesturePwd", MD5Util.getMD5String(GesturePwd).toUpperCase(Locale.CHINA))
                .add("LoginAccount", LoginAccount ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 重置收款单位
     * @param PayeeCode 收款单位编号
     * @return
     */
    fun getParamsForSetNewPayee(LoginAccount: String?, PayeeCode: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011009")
                .add("ModifyType", "J")
                .add("LoginAccount", LoginAccount ?: "")
                .add("PayeeCode", PayeeCode ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 重置身份证号码,姓名
     * @param LoginAccount 登录账号
     * @param IDCardNb     身份证号码
     * @param RealName     姓名
     * @return
     */
    fun getParamsForSetIDCardNbNewRealName(LoginAccount: String?, RealName: String?, IDCardNb: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011009")
                .add("ModifyType", "D")
                .add("LoginAccount", LoginAccount ?: "")
                .add("RealName", RealName ?: "")
                .add("IDCardNb", IDCardNb ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 重置邮箱
     * @param LoginAccount 登录账号
     * @param OldEmail     老邮箱
     * @param NewEmail     新邮箱
     * @return
     */
    fun getParamsForSetEmail(LoginAccount: String?, OldEmail: String?, NewEmail: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011009")
                .add("ModifyType", "G")
                .add("LoginAccount", LoginAccount ?: "")
                .add("OldEmail", OldEmail ?: "")
                .add("NewEmail", NewEmail ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 重置手机号
     * @param LoginAccount 登录账号
     * @param OldPhoneNb   老手机号码
     * @param NewPhoneNb   新手机号码
     * @param VerificationKey  手机验证码key
     * @param VerificationCode   手机验证码
     * @return
     */
    fun getParamsForSetPhoneNum(LoginAccount: String?, OldPhoneNb: String?, NewPhoneNb: String?, VerificationKey: String?, VerificationCode: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011009")
                .add("ModifyType", "F")
                .add("LoginAccount", LoginAccount ?: "")
                .add("OldPhoneNb", OldPhoneNb ?: "")
                .add("VerificationKey", VerificationKey ?: "")
                .add("VerificationCode", VerificationCode ?: "")
                .add("NewPhoneNb", NewPhoneNb ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 重置手机号
     * @param Ext1 序号
     * @return
     */
    fun getParamsForSetFuntion(LoginAccount: String?, Ext1: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011009")
                .add("ModifyType", "K")
                .add("LoginAccount", LoginAccount ?: "")
                .add("Ext1", Ext1 ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 找回密码
     * @param LoginAccount 登录账号
     * @param NewPwd       新密码
     * @param VerificationKey       手机验证码key
     * @param VerificationCode       手机验证码
     * @return
     */
    fun getParamsForFindPWD(LoginAccount: String?, NewPwd: String,
                            VerificationKey: String?, VerificationCode: String?): CusFormBody {
        val Pwd = Encrypt.SHA256(Encrypt.MD5(NewPwd).toUpperCase(Locale.CHINA)
                + Encrypt.MD5(NewPwd).toUpperCase(Locale.CHINA).substring(0, 6)).toUpperCase(Locale.CHINA)
        Logger.e(Encrypt.MD5(NewPwd).toUpperCase(Locale.CHINA))
        Logger.e(MD5Util.getMD5String(NewPwd).toUpperCase(Locale.CHINA).substring(0, 6))
        Logger.e(Pwd)
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011009")
                .add("ModifyType", "E")
                .add("RetrieveType", "1")
                .add("LoginAccount", LoginAccount ?: "")
                .add("VerificationKey", VerificationKey ?: "")
                .add("VerificationCode", VerificationCode ?: "")
                .add("NewPwd", Pwd)
        return getParamsRequestHead(builder, LoginAccount, false)
    }

    /**
     * 设置安保问题
     * @param LoginAccount 登录账号
     * @param SafeQuestion 安全保护问题编码
     * @param SafeAnswer   老密码
     * @param Pwd          用户密码
     * @param VerificationKey       手机验证码key
     * @param VerificationCode       手机验证码
     * @return
     */
    fun getParamsForSetNewQuestion(LoginAccount: String?, SafeQuestion: String?, SafeAnswer: String?,
                                   Pwd: String, VerificationKey: String?, VerificationCode: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011009")
                .add("ModifyType", "B")
                .add("RetrieveType", "2")
                .add("LoginAccount", LoginAccount ?: "")
                .add("SafeQuestion", SafeQuestion ?: "")
                .add("SafeAnswer", SafeAnswer ?: "")
                .add("VerificationKey", VerificationKey ?: "")
                .add("VerificationCode", VerificationCode ?: "")
                .add("Pwd", MD5Util.getMD5String(Pwd).toUpperCase(Locale.CHINA))
        return getParamsRequestHead(builder, LoginAccount)
    }

    /**
     * 反馈
     * @param phone     登录账号
     * @param titles            标题
     * @param content 反馈问题
     * @param contactWay       联系方式
     * @return
     */
    fun getParamsForFeedBack(phone: String?, titles: String?, content: String?, contactWay: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011016")
                .add("LoginAccount", phone ?: "")
                .add("Title", titles ?: "")
                .add("FeekbackQuestion", content ?: "")
                .add("ContactWay", contactWay ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 公共参数查询(01：版本；02：轮播图)
     * @param comkey  Key 01版本号
     * @param comtype 类别 01系统参数 02轮播图
     * @return
     */
    fun getParamsForAdvertisement(comkey: String?, comtype: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10010000")
                .add("QueryCd", "1001000003")
                .add("TurnPageBeginPos", "1")
                .add("TurnPageShowNum", "20")
                .add("comkey", comkey ?: "")
                .add("comtype", comtype ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 图片处理
     * @param ImageBase64  图片base64编码
     * @param HandleType   处理类型
     * @return
     */
    fun getParamsForImageHandle(ImageBase64: String?, HandleType: String?): CusFormBody {
        val user = EncryptedPreferencesUtils.getUser()
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011039")
                .add("ImageBase64", ImageBase64 ?: "")
                .add("HandleType", HandleType ?: "")
                .add("LoginAccount", if (user != null) user.LoginAccount else "duxg")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 验证码校验（10011035）
     * @param VerificationCode 验证码
     * @param ImageKey 验证码key
     * @return
     */
    fun getParamsForValidateCode(VerificationCode: String?, ImageKey: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011035")
                .add("VerificationCode", VerificationCode ?: "")
                .add("ImageKey", ImageKey ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 前端获取验证码（10011030）
     * @param DeviceType 设备类型
     * @return
     */
    fun getParamsGetImageValidateCode(DeviceType: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011030")
                .add("DeviceType", DeviceType ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 通知新闻查询（10011036）
     * @param QueryNoticeType 查询通知 10：查询维护欲通知和维护通知 20：待定
     * @return
     */
    fun getParamsNoticeNewsQuery(QueryNoticeType: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011036").add("QueryNoticeType", QueryNoticeType ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 员工获取二维码（10011040）
     * @param PhoneNb 手机号
     * @return
     */
    fun getParamsForStaffQRcode(PhoneNb: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011040").add("PhoneNb", PhoneNb ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 推送户号设置（10011041）
     * @param LoginAccount 登录账号
     * @param UserNb       户号
     * @param PayeeCode    收款单位编号
     * @param Type         操作类型 10：订阅 ; 11：取消订阅
     * @return
     */
    fun getParamsForPushUserNb(LoginAccount: String?, UserNb: String?, PayeeCode: String?, Type: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011041")
                .add("LoginAccount", LoginAccount ?: "")
                .add("UserNb", UserNb ?: "")
                .add("PayeeCode", PayeeCode ?: "")
                .add("Type", Type ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 查询推送户号（10011042）
     * @param LoginAccount 登录账号
     * @return
     */
    fun getParamsForQueryPushUser(LoginAccount: String?): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011042").add("LoginAccount", LoginAccount ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 蓝牙读卡器加密机调用（10011044）
     * @param OrderNb      订单号
     * @param Type         操作类型 10：外部认证; 11：获取DES; 12：圈存
     * @param CardID       卡ID
     * @param Random       随机数
     * @param NFCPayTime   NFC充值次数
     * @param CardMsg      卡信息
     * @param QuanRes      圈存信息
     * @param NFCPayAmount NFC购气金额
     * @return
     */
    fun getParamsForBluetoothSignMsg(OrderNb: String?, UserNb: String?, Type: String?, CardID: String?,
                                     Random: String?, NFCPayTime: String?, CardMsg: String?, QuanTime: String?, QuanRes: String?, NFCPayAmount: String?): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011044")
                .add("OrderNb", OrderNb ?: "")
                .add("UserNb", UserNb ?: "")
                .add("Type", Type ?: "")
                .add("CardID", CardID ?: "")
                .add("Random", Random ?: "")
                .add("NFCPayTime", NFCPayTime ?: "")
                .add("CardMsg", CardMsg ?: "")
                .add("QuanRes", QuanRes ?: "")
                .add("NFCPayAmount", NFCPayAmount ?: "0")
                .add("QuanTime", QuanTime ?: "")
        return getParamsRequestHead(builder, null)
    }

    /**
     * 蓝牙读卡器加密机调用（10011044）
     * @param OrderNb      订单号
     * @param Type         操作类型 10：外部认证; 11：获取DES; 12：圈存
     * @param CardID       卡ID
     * @param Random       随机数
     * @param NFCPayTime   NFC充值次数
     * @param CardMsg      卡信息
     * @param QuanRes      圈存信息
     * @param NFCPayAmount NFC购气金额
     *
     * @param CurrentTime 表当前时间
     * @param CurrentPrice 表当前单价
     * @param SurplusPrice 剩余金额
     * @param SumBuyPrice 表累计购气金额
     * @param NoUserDays 没有用气天数
     * @param NoUserSeconds 没有用气秒数
     * @param CurrentStatus 表当前状态
     * @param PurchaseStatus 消费状态字
     * @param SumTotal 表累计用量
     * @param SumTotalTwentyFour 最近24个月的累计用气量集合(24条)
     * @param SecurityCheckNb 安检返回条数
     * @param ThreeSecurityCheck 3条安检记录信息(3条)
     * @param NFCPaySumAmount NFC购气总金额
     *
     * @return
     */
    fun getParamsForBluetoothSignMsg(OrderNb: String?, UserNb: String?, Type: String?, CardID: String?,
                                     Random: String?, NFCPayTime: String?, CardMsg: String?, QuanTime: String?, QuanRes: String?, NFCPayAmount: String?,
                                     CurrentTime: String?, CurrentPrice: String?, SurplusPrice: String?,
                                     SumBuyPrice: String?, NoUserDays: String?, NoUserSeconds: String?,
                                     CurrentStatus: String?, PurchaseStatus: String?, SumTotal: String?,
                                     SumTotalTwentyFour: String?, SecurityCheckNb: String?, ThreeSecurityCheck: String?,
                                     NFCPaySumAmount: String?
    ): CusFormBody {
        val builder = CusFormBody.Builder()
                .add("TransactionId", "10011044")
                .add("OrderNb", OrderNb ?: "")
                .add("UserNb", UserNb ?: "")
                .add("Type", Type ?: "")
                .add("CardID", CardID ?: "")
                .add("Random", Random ?: "")
                .add("NFCPayTime", NFCPayTime ?: "")
                .add("CardMsg", CardMsg ?: "")
                .add("QuanRes", QuanRes ?: "")
                .add("NFCPayAmount", NFCPayAmount ?: "0")
                .add("QuanTime", QuanTime ?: "")
                .add("CurrentTime", CurrentTime ?: "")
                .add("CurrentPrice", CurrentPrice ?: "0")
                .add("SurplusPrice", SurplusPrice ?: "0")
                .add("SumBuyPrice", SumBuyPrice ?: "0")
                .add("NoUserDays", NoUserDays ?: "")
                .add("NoUserSeconds", NoUserSeconds ?: "")
                .add("CurrentStatus", CurrentStatus ?: "")
                .add("PurchaseStatus", PurchaseStatus ?: "")
                .add("SumTotal", SumTotal ?: "")
                .add("SumTotalTwentyFour", SumTotalTwentyFour ?: "")
                .add("SecurityCheckNb", SecurityCheckNb ?: "")
                .add("ThreeSecurityCheck", ThreeSecurityCheck ?: "")
                .add("NFCPaySumAmount", NFCPaySumAmount ?: "0")

        return getParamsRequestHead(builder, null)
    }

    /**
     * 查询所有收款单位（10011045）
     * @return
     */
    val paramsForPayeesQueryAll: CusFormBody
        get() {
            val builder = CusFormBody.Builder()
            return getParamsRequestHead(builder, null)
        }

    /**
     * 注册获取登录名（10011057）
     * @return
     */
    fun getParamsGetLoginAccount(): CusFormBody {
        val builder = CusFormBody.Builder().add("TransactionId", "10011057")
        return getParamsRequestHead(builder, null)
    }


    /**
     * 添加通用报文头
     * @param
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getParamsRequestHead(builder: CusFormBody.Builder, LoginAccount: String?): CusFormBody {
        val formatter = SimpleDateFormat("yyyy-MM-dd*HH:mm:ss")
        val curDate = Date(System.currentTimeMillis())// 获取当前时间
        var str = formatter.format(curDate)
        str = str.replace("*", "T")
        val user = EncryptedPreferencesUtils.getUser()
        val formBody = builder
                .add("Source", "1001")
                .add("SerialNumber", System.currentTimeMillis().toString())
                .add("TermId", "28.0.0.234")
                .add("Channel", "1003")
                .add("OperUser", LoginAccount ?: if (user != null && !TextUtils.isEmpty(user.LoginAccount)) user.LoginAccount else "duxg")
                .add("SessionId", if (user != null) user.SessionId else "")
                .add("Destination", "GHPS")
                .add("SendDateTime", str)
                .build()
        Logger.i(formBody.toString())
        return formBody
    }

    /**
     * 添加通用报文头
     * @param
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getParamsRequestHead(builder: CusFormBody.Builder, LoginAccount: String?, isSession: Boolean): CusFormBody {
        val formatter = SimpleDateFormat("yyyy-MM-dd*HH:mm:ss")
        val curDate = Date(System.currentTimeMillis())// 获取当前时间
        var str = formatter.format(curDate)
        str = str.replace("*", "T")
        val user = EncryptedPreferencesUtils.getUser()
        val formBody = builder
                .add("Source", "1001")
                .add("SerialNumber", System.currentTimeMillis().toString())
                .add("TermId", "28.0.0.234")
                .add("Channel", "1003")
                .add("OperUser", LoginAccount ?: if (user != null && !TextUtils.isEmpty(user.LoginAccount)) user.LoginAccount else "duxg")
                .add("SessionId", if (isSession) (if (user != null) user.SessionId else "") else "")
                .add("Destination", "GHPS")
                .add("SendDateTime", str)
                .build()
        Logger.i(formBody.toString())
        return formBody
    }

}
