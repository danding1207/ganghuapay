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
    public @interface GetResultWay {}

//    @GetResultWay int currentDay = INIT;

    //用 @IntDef "包住" 常量；
    // @Retention 定义策略
    // 声明构造器
    @IntDef({SUCCESSS, FAIL, NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {}



    public static final String target = "kefuchannelimid_814947";
    public static final String projectId = "355280";
    public static final String Appkey = "1122161110178774#kefuchannelapp30625";
    public static final String TenantId = "30625";


}
