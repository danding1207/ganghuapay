package com.mqt.ganghuazhifu.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by msc on 2016/3/28.
 */
public class Constant {

    public static final int PAZE_SIZE = 10;

    //先定义 常量
    public static final int INIT = 0;
    public static final int UPDATA = 1;
    public static final int LOADMORE = 2;

    public static final int SUCCESSS = 3;
    public static final int FAIL = 4;
    public static final int NORMAL = 5;


    //用 @IntDef "包住" 常量；
    // @Retention 定义策略
    // 声明构造器
    @IntDef({INIT, UPDATA, LOADMORE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GetResultWay {
    }

//    @GetResultWay int currentDay = INIT;

    //用 @IntDef "包住" 常量；
    // @Retention 定义策略
    // 声明构造器
    @IntDef({SUCCESSS, FAIL, NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }


    // 1:缴纳气费;     2:缴纳营业费;
    // 3:查询气费账单;  4:查询营业费账单;
    // 5：缴纳水费;    6:查询水费账单;
    // 7:NFC预存气费;  8:营业费预存;(弃用)
    // 9:蓝牙读卡器缴气费; 10:常用联系人缴费;(弃用)
    // 11:气量表缴费NFC;  12:蓝牙表预存气费;
    // 13:气量表缴费蓝牙
    public static final int GASFEEARREARS = 1001;//缴纳气费
    public static final int OPERATINGFEEARREARS = 1002;//缴纳营业费
    public static final int GASFEEBILL = 1003;//查询气费账单
    public static final int OPERATINGFEEBILL = 1004;//查询营业费账单

    public static final int WATERFEEARREARS = 1005;//缴纳水费
    public static final int WATERFEEBILL = 1006;//查询水费账单

    public static final int NFCGASFEEPREDEPOSIT = 1007;//金额表NFC预存气费
    public static final int BLUETOOTHCARDGASFEEPREDEPOSIT = 1008;//金额表蓝牙读卡器缴气费

    public static final int NFCGASFEEICPREDEPOSIT = 1009;//气量表NFC预存气费
    public static final int BLUETOOTHCARDGASFEEICPREDEPOSIT = 1010;//气量表蓝牙读卡器缴气费

    public static final int BLUETOOTHGASFEEPREDEPOSIT = 1011;//蓝牙表预存气费


    @IntDef({GASFEEARREARS, OPERATINGFEEARREARS, GASFEEBILL, OPERATINGFEEBILL,
            WATERFEEARREARS, WATERFEEBILL, NFCGASFEEPREDEPOSIT, BLUETOOTHCARDGASFEEPREDEPOSIT,
            NFCGASFEEICPREDEPOSIT, BLUETOOTHCARDGASFEEICPREDEPOSIT, BLUETOOTHGASFEEPREDEPOSIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrderType {
    }


    public static final String target = "kefuchannelimid_814947";
    public static final String projectId = "355280";
    public static final String Appkey = "1122161110178774#kefuchannelapp30625";
    public static final String TenantId = "30625";


}
