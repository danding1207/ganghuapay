package com.mqt.ganghuazhifu.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mqt.ganghuazhifu.App;
import com.mqt.ganghuazhifu.bean.BaiduPushData;
import com.mqt.ganghuazhifu.bean.User;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

/**
 * Created by danding1207 on 16/10/17.
 */
public class EncryptedPreferencesUtils {

    private static EncryptedPreferences encryptedPreferences;

    public static void init(@NonNull Context context) {
        String encryptionPassword = "123456";
        encryptedPreferences = new EncryptedPreferences.Builder(context).withEncryptionPassword(encryptionPassword).build();
    }

    public static void setUser(User user) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_LOGINACCOUNT, user.getLoginAccount())
                    .putString(USER_PREFERENCES_USER_REALNAME, user.getRealName())
                    .putString(USER_PREFERENCES_USER_IDCARDNB, user.getIdcardNb())
                    .putString(USER_PREFERENCES_USER_GENDER, user.getGender())
                    .putString(USER_PREFERENCES_USER_OCCUPATION, user.getOccupation())
                    .putString(USER_PREFERENCES_USER_PHONENB, user.getPhoneNb())
                    .putString(USER_PREFERENCES_USER_PHONEFLAG, user.getPhoneFlag())
                    .putString(USER_PREFERENCES_USER_LOGINTIME, user.getLoginTime())
                    .putString(USER_PREFERENCES_USER_EMAIL, user.getEmail())
                    .putString(USER_PREFERENCES_USER_GESTUREPWD, user.getGesturePwd())
                    .putString(USER_PREFERENCES_USER_SESSIONID, user.getSessionId())
                    .putString(USER_PREFERENCES_USER_PUSHSTATUS, user.getPushStatus())
                    .putString(USER_PREFERENCES_USER_EMPLOYEEFLAG, user.getEmployeeFlag())
                    .putString(USER_PREFERENCES_USER_UID, user.getUid())
                    .putString(USER_PREFERENCES_USER_ASCRIPTIONFLAG, user.getAscriptionFlag())
                    .putString(USER_PREFERENCES_USER_PAYEENAME, user.getPayeeNm())
                    .putString(USER_PREFERENCES_USER_PAYEECODE, user.getPayeeCode())
                    .putInt(USER_PREFERENCES_USER_GENERALCONTACTCOUNT, user.getGeneralContactCount())
                    .putString(USER_PREFERENCES_USER_FUNCTION1, user.getFunction1())
                    .putString(USER_PREFERENCES_USER_FUNCTION2, user.getFunction2())
                    .putString(USER_PREFERENCES_USER_FUNCTION3, user.getFunction3())
                    .putString(USER_PREFERENCES_USER_PASSWORD, user.getPassword())
                    .putBoolean(USER_PREFERENCES_PASSWORD_STATUS, user.getPasswordStatus())
                    .putInt(USER_PREFERENCES_PASSWORD_NUM, user.getPasswordNum())
                    .putString(USER_PREFERENCES_COMVAL, user.getComVal())
                    .apply();
    }

    public static User getUser() {
        User user = new User();
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            user.setLoginAccount(encryptedPreferences.getString(USER_PREFERENCES_USER_LOGINACCOUNT, ""));
        if (encryptedPreferences != null)
            user.setRealName(encryptedPreferences.getString(USER_PREFERENCES_USER_REALNAME, ""));
        if (encryptedPreferences != null)
            user.setIdcardNb(encryptedPreferences.getString(USER_PREFERENCES_USER_IDCARDNB, ""));
        if (encryptedPreferences != null)
            user.setGender(encryptedPreferences.getString(USER_PREFERENCES_USER_GENDER, ""));
        if (encryptedPreferences != null)
            user.setOccupation(encryptedPreferences.getString(USER_PREFERENCES_USER_OCCUPATION, ""));
        if (encryptedPreferences != null)
            user.setPhoneNb(encryptedPreferences.getString(USER_PREFERENCES_USER_PHONENB, ""));
        if (encryptedPreferences != null)
            user.setPhoneFlag(encryptedPreferences.getString(USER_PREFERENCES_USER_PHONEFLAG, ""));
        if (encryptedPreferences != null)
            user.setLoginTime(encryptedPreferences.getString(USER_PREFERENCES_USER_LOGINTIME, ""));
        if (encryptedPreferences != null)
            user.setEmail(encryptedPreferences.getString(USER_PREFERENCES_USER_EMAIL, ""));
        if (encryptedPreferences != null)
            user.setGesturePwd(encryptedPreferences.getString(USER_PREFERENCES_USER_GESTUREPWD, ""));
        if (encryptedPreferences != null)
            user.setSessionId(encryptedPreferences.getString(USER_PREFERENCES_USER_SESSIONID, ""));
        if (encryptedPreferences != null)
            user.setPushStatus(encryptedPreferences.getString(USER_PREFERENCES_USER_PUSHSTATUS, ""));
        if (encryptedPreferences != null)
            user.setEmployeeFlag(encryptedPreferences.getString(USER_PREFERENCES_USER_EMPLOYEEFLAG, ""));
        if (encryptedPreferences != null)
            user.setUid(encryptedPreferences.getString(USER_PREFERENCES_USER_UID, ""));
        if (encryptedPreferences != null)
            user.setAscriptionFlag(encryptedPreferences.getString(USER_PREFERENCES_USER_ASCRIPTIONFLAG, ""));
        if (encryptedPreferences != null)
            user.setPayeeNm(encryptedPreferences.getString(USER_PREFERENCES_USER_PAYEENAME, ""));
        if (encryptedPreferences != null)
            user.setPayeeCode(encryptedPreferences.getString(USER_PREFERENCES_USER_PAYEECODE, ""));
        if (encryptedPreferences != null)
            user.setGeneralContactCount(encryptedPreferences.getInt(USER_PREFERENCES_USER_GENERALCONTACTCOUNT, 0));
        if (encryptedPreferences != null)
            user.setFunction1(encryptedPreferences.getString(USER_PREFERENCES_USER_FUNCTION1, ""));
        if (encryptedPreferences != null)
            user.setFunction2(encryptedPreferences.getString(USER_PREFERENCES_USER_FUNCTION2, ""));
        if (encryptedPreferences != null)
            user.setFunction3(encryptedPreferences.getString(USER_PREFERENCES_USER_FUNCTION3, ""));
        if (encryptedPreferences != null)
            user.setPassword(encryptedPreferences.getString(USER_PREFERENCES_USER_PASSWORD, ""));
        if (encryptedPreferences != null)
            user.setPasswordStatus(encryptedPreferences.getBoolean(USER_PREFERENCES_PASSWORD_STATUS, false));
        if (encryptedPreferences != null)
            user.setPasswordNum(encryptedPreferences.getInt(USER_PREFERENCES_PASSWORD_NUM, 0));
        if (encryptedPreferences != null)
            user.setComVal(encryptedPreferences.getString(USER_PREFERENCES_COMVAL, "0"));
        return user;
    }

    public static void setPhoneNb(String phone) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_PHONENB, phone)
                    .apply();
    }

    public static void setGeneralContactCount(int count) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putInt(USER_PREFERENCES_USER_GENERALCONTACTCOUNT, count)
                    .apply();
    }

    public static void setGesturePwd(String gesturePwd) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_GESTUREPWD, gesturePwd)
                    .apply();
    }

    public static void setAscriptionFlag(String flag) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_ASCRIPTIONFLAG, flag)
                    .apply();
    }

    public static void setPayeeName(String payeeNm) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_PAYEENAME, payeeNm)
                    .apply();
    }

    public static void setPayeeCode(String payeeCode) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_PAYEECODE, payeeCode)
                    .apply();
    }

    public static void setEmail(String email) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_EMAIL, email)
                    .apply();
    }

    public static void setIdcardNb(String idcardNb) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_IDCARDNB, idcardNb)
                    .apply();
    }

    public static void setRealName(String realName) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_REALNAME, realName)
                    .apply();
    }

    public static void setFunction1(String function1) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_FUNCTION1, function1)
                    .apply();
    }

    public static void setOccupation(String occupation) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_OCCUPATION, occupation)
                    .apply();
    }

    public static void setLoginTime(String loginTime) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_LOGINTIME, loginTime)
                    .apply();
    }

    public static void setGender(String gender) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_GENDER, gender)
                    .apply();
    }

    public static void setPhoneFlag(String phoneFlag) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(USER_PREFERENCES_USER_PHONEFLAG, phoneFlag)
                    .apply();
    }

    public static void setBaiduPushData(BaiduPushData baiduPushData) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(PUSH_DATA_PREFERENCES_ERRORCODE, baiduPushData.getErrorCode())
                    .putString(PUSH_DATA_PREFERENCES_APPID, baiduPushData.getAppid())
                    .putString(PUSH_DATA_PREFERENCES_USERID, baiduPushData.getUserId())
                    .putString(PUSH_DATA_PREFERENCES_CHANNELID, baiduPushData.getChannelId())
                    .putString(PUSH_DATA_PREFERENCES_REQUESTID, baiduPushData.getRequestId())
                    .apply();
    }

    public static BaiduPushData getBaiduPushData() {
        checkEncryptedPreferencesNull();
        BaiduPushData baiduPushData = new BaiduPushData();
        if (encryptedPreferences != null)
            baiduPushData.setErrorCode(encryptedPreferences.getString(PUSH_DATA_PREFERENCES_ERRORCODE, ""));
        if (encryptedPreferences != null)
            baiduPushData.setAppid(encryptedPreferences.getString(PUSH_DATA_PREFERENCES_APPID, ""));
        if (encryptedPreferences != null)
            baiduPushData.setUserId(encryptedPreferences.getString(PUSH_DATA_PREFERENCES_USERID, ""));
        if (encryptedPreferences != null)
            baiduPushData.setChannelId(encryptedPreferences.getString(PUSH_DATA_PREFERENCES_CHANNELID, ""));
        if (encryptedPreferences != null)
            baiduPushData.setRequestId(encryptedPreferences.getString(PUSH_DATA_PREFERENCES_REQUESTID, ""));
        return baiduPushData;
    }

    public static void setDownId(long downId) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putLong(DOWNID, downId)
                    .apply();
    }

    public static long getDownId() {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            return encryptedPreferences.getLong(DOWNID, -1L);
        return -1L;
    }

    public static void setWXPayId(String orderId) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putString(DOWNID, orderId)
                    .apply();
    }

    public static String getWXPayId() {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            return encryptedPreferences.getString(DOWNID, "");
        return "";
    }


    /**
     * @return int[0]==ScreenWidth,  int[1]==ScreenHigh
     */
    public static int[] getScreenSize() {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            return new int[]{encryptedPreferences.getInt(SCREEN_SIZE_PREFERENCES_WIDTH, 0), encryptedPreferences.getInt(SCREEN_SIZE_PREFERENCES_HIGH, 0)};
        return new int[]{0, 0};
    }

    public static void setScreenSize(int[] size) {
        checkEncryptedPreferencesNull();
        if (encryptedPreferences != null)
            encryptedPreferences.edit()
                    .putInt(SCREEN_SIZE_PREFERENCES_WIDTH, size[0])
                    .putInt(SCREEN_SIZE_PREFERENCES_HIGH, size[1])
                    .apply();
    }

    private static void checkEncryptedPreferencesNull() {
        if (encryptedPreferences == null)
            if (App.Companion.getContext() != null)
                init(App.Companion.getContext());
    }

    /**
     * User
     */
    private static final String USER_PREFERENCES_USER_LOGINACCOUNT = "USER_LOGINACCOUNT";//账号
    private static final String USER_PREFERENCES_USER_REALNAME = "USER_REALNAME";//真实姓名
    private static final String USER_PREFERENCES_USER_IDCARDNB = "USER_IDCARDNB";//身份证号码
    private static final String USER_PREFERENCES_USER_GENDER = "USER_GENDER";//性别
    private static final String USER_PREFERENCES_USER_OCCUPATION = "USER_OCCUPATION";//职业
    private static final String USER_PREFERENCES_USER_PHONENB = "USER_PHONENB";//手机号码
    private static final String USER_PREFERENCES_USER_PHONEFLAG = "USER_PHONEFLAG";//手机标识
    private static final String USER_PREFERENCES_USER_LOGINTIME = "USER_LOGINTIME";//登录时间
    private static final String USER_PREFERENCES_USER_EMAIL = "USER_EMAIL";//邮箱
    private static final String USER_PREFERENCES_USER_GESTUREPWD = "USER_GESTUREPWD";//手势密码
    private static final String USER_PREFERENCES_USER_SESSIONID = "USER_SESSIONID";//sessionid
    private static final String USER_PREFERENCES_USER_PUSHSTATUS = "USER_PUSHSTATUS";//推送开关状态
    private static final String USER_PREFERENCES_USER_EMPLOYEEFLAG = "USER_EMPLOYEEFLAG";//员工标识
    private static final String USER_PREFERENCES_USER_UID = "USER_UID";//用户id
    private static final String USER_PREFERENCES_USER_ASCRIPTIONFLAG = "USER_ASCRIPTIONFLAG";//归属标识
    private static final String USER_PREFERENCES_USER_PAYEENAME = "USER_PAYEENAME";//收款单位名称
    private static final String USER_PREFERENCES_USER_PAYEECODE = "USER_PAYEECODE";//收款单位编号
    private static final String USER_PREFERENCES_USER_GENERALCONTACTCOUNT = "USER_GENERALCONTACTCOUNT";//常用联系人数量
    private static final String USER_PREFERENCES_USER_FUNCTION1 = "USER_FUNCTION1";//新功能标识1
    private static final String USER_PREFERENCES_USER_FUNCTION2 = "USER_FUNCTION2";//新功能标识2
    private static final String USER_PREFERENCES_USER_FUNCTION3 = "USER_FUNCTION3";//新功能标识3
    private static final String USER_PREFERENCES_USER_PASSWORD = "USER_PASSWORD";//登录密码
    private static final String USER_PREFERENCES_PASSWORD_STATUS = "PASSWORD_STATUS";//是否记住密码
    private static final String USER_PREFERENCES_PASSWORD_NUM = "PASSWORD_NUM";//密码长度
    private static final String USER_PREFERENCES_COMVAL = "COMVAL";//支付渠道版本


    /**
     * BaiduPushData
     */
    private static final String PUSH_DATA_PREFERENCES_ERRORCODE = "ERRORCODE";//错误码
    private static final String PUSH_DATA_PREFERENCES_APPID = "APPID";//
    private static final String PUSH_DATA_PREFERENCES_USERID = "USERID";//
    private static final String PUSH_DATA_PREFERENCES_CHANNELID = "CHANNELID";//
    private static final String PUSH_DATA_PREFERENCES_REQUESTID = "REQUESTID";//

    /**
     * DOWNID
     */
    private static final String DOWNID = "DOWNID";//下载任务id

    /**
     * WXPAYID
     */
    private static final String WXPAYID = "WXPAYID";//微信支付当前订单号


    /**
     * SCREEN_SIZE
     */
    private static final String SCREEN_SIZE_PREFERENCES_WIDTH = "WIDTH";//屏幕宽度
    private static final String SCREEN_SIZE_PREFERENCES_HIGH = "HIGH";//屏幕高度

}
