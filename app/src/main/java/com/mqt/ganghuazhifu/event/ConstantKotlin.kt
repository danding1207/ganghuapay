package com.mqt.ganghuazhifu.event

/**
 * Created by msc on 2016/3/28.
 */
object ConstantKotlin {

    // 1:缴纳气费;     2:缴纳营业费;
    // 3:查询气费账单;  4:查询营业费账单;
    // 5：缴纳水费;    6:查询水费账单;

    // 7:NFC预存气费;  8:营业费预存;(弃用)
    // 9:蓝牙读卡器缴气费; 10:常用联系人缴费;(弃用)
    // 11:气量表缴费NFC;  12:蓝牙表预存气费;
    // 13:气量表缴费蓝牙

    enum class OrderType(value:Int){
        /**
         * 缴纳气费
         */
        GASFEEARREARS(1001),//缴纳气费

        /**
         * 查询气费账单
         */
        GASFEEBILL(1002),//查询气费账单

        OPERATINGFEEARREARS(1003),//缴纳营业费
        OPERATINGFEEBILL(1004),//查询营业费账单

        WATERFEEARREARS(1005),//缴纳水费
        WATERFEEBILL(1006),//查询水费账单

        GASFEEPREDEPOSIT(1007),//金额表 预存气费

        NFCGASFEEPREDEPOSIT(1008),//金额表NFC预存气费
        BLUETOOTHCARDGASFEEPREDEPOSIT(1009),//金额表蓝牙读卡器缴气费

        NFCGASFEEICPREDEPOSIT(1010),//气量表NFC预存气费
        BLUETOOTHCARDGASFEEICPREDEPOSIT(1011),//气量表蓝牙读卡器缴气费

        BLUETOOTHGASFEEPREDEPOSIT(1012)//蓝牙表预存气费

    }


    const val NanJingCode = "320000320100019999"
    const val BaoHuaCode = "320000321100019998"


}
