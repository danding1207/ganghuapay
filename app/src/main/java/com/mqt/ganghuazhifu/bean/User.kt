package com.mqt.ganghuazhifu.bean

class User {

    var LoginAccount: String?//账号
    var RealName: String?//真实姓名
    var IdcardNb: String?//身份证号码
    var Gender: String?//性别
    var Occupation: String?//职业
    var PhoneNb: String?//手机号码
    var PhoneFlag: String?//手机验证标识
    var LoginTime: String?//上次登录时间
    var Email: String?//邮箱
    var GesturePwd: String?//手势密码
    var SessionId: String?//sessionid
    var PushStatus: String?//推送开关状态
    var EmployeeFlag: String?//员工标识
    var Uid: String?//用户id
    var AscriptionFlag: String?//归属标识
    var PayeeNm: String?//收款单位名称
    var PayeeCode: String?//收款单位编号
    var GeneralContactCount: Int = 0//常用联系人数量
    var function1: String?//新功能标识1
    var function2: String?//新功能标识2
    var function3: String?//新功能标识3

    var Password: String?
    var PasswordStatus: Boolean = false//true:记住密码; false:不记住.
    var PasswordNum: Int = 0


    var ComVal: String? = null


    override fun toString(): String {
        return "User{" +
                "LoginAccount='" + LoginAccount + '\'' +
                ", RealName='" + RealName + '\'' +
                ", IdcardNb='" + IdcardNb + '\'' +
                ", Gender='" + Gender + '\'' +
                ", Occupation='" + Occupation + '\'' +
                ", PhoneNb='" + PhoneNb + '\'' +
                ", PhoneFlag='" + PhoneFlag + '\'' +
                ", LoginTime='" + LoginTime + '\'' +
                ", Email='" + Email + '\'' +
                ", GesturePwd='" + GesturePwd + '\'' +
                ", SessionId='" + SessionId + '\'' +
                ", PushStatus='" + PushStatus + '\'' +
                ", EmployeeFlag='" + EmployeeFlag + '\'' +
                ", Uid='" + Uid + '\'' +
                ", AscriptionFlag='" + AscriptionFlag + '\'' +
                ", PayeeNm='" + PayeeNm + '\'' +
                ", PayeeCode='" + PayeeCode + '\'' +
                ", GeneralContactCount=" + GeneralContactCount +
                ", function1='" + function1 + '\'' +
                ", function2='" + function2 + '\'' +
                ", function3='" + function3 + '\'' +
                ", Password='" + Password + '\'' +
                ", PasswordStatus=" + PasswordStatus +
                ", PasswordNum=" + PasswordNum +
                ", ComVal=" + ComVal +
                '}'
    }

    constructor(loginAccount: String?, realName: String?, idcardNb: String?,
                gender: String?, occupation: String?, phoneNb: String?,
                phoneFlag: String?, loginTime: String?, email: String?,
                gesturePwd: String?, sessionId: String?, pushStatus: String?,
                employeeFlag: String?, uid: String, ascriptionFlag: String?,
                payeeNm: String?, payeeCode: String?, generalContactCount: Int,
                function1: String?, function2: String?, function3: String?,
                password: String?, passwordStatus: Boolean, passwordNum: Int,
                comVal: String?) {
        LoginAccount = loginAccount ?: ""
        RealName = realName ?: ""
        IdcardNb = idcardNb ?: ""
        Gender = gender ?: ""
        Occupation = occupation ?: ""
        PhoneNb = phoneNb ?: ""
        PhoneFlag = phoneFlag ?: ""
        LoginTime = loginTime ?: ""
        Email = email ?: ""
        GesturePwd = gesturePwd ?: ""
        SessionId = sessionId ?: ""
        PushStatus = pushStatus ?: ""
        EmployeeFlag = employeeFlag ?: ""
        Uid = uid
        AscriptionFlag = ascriptionFlag ?: ""
        PayeeNm = payeeNm ?: ""
        PayeeCode = payeeCode ?: ""
        GeneralContactCount = generalContactCount
        this.function1 = function1 ?: ""
        this.function2 = function2 ?: ""
        this.function3 = function3 ?: ""
        Password = password ?: ""
        PasswordStatus = passwordStatus
        PasswordNum = passwordNum
        ComVal = comVal ?: ""
    }

    constructor() {
        LoginAccount = ""
        RealName = ""
        IdcardNb = ""
        Gender = ""
        Occupation = ""
        PhoneNb = ""
        PhoneFlag = ""
        LoginTime = ""
        Email = ""
        GesturePwd = ""
        SessionId = ""
        PushStatus = ""
        EmployeeFlag = ""
        Uid = ""
        AscriptionFlag = ""
        PayeeNm = ""
        PayeeCode = ""
        GeneralContactCount = 0
        this.function1 = ""
        this.function2 = ""
        this.function3 = ""
        Password = ""
        PasswordStatus = false
        PasswordNum = 0
        ComVal = "0"
    }

}
