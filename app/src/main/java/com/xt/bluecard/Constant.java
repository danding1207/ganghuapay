package com.xt.bluecard;

public class Constant {
	public static final int BASE_NUM = 0xAA;
	/** 蓝牙连接失败 */
	public static final int BLE_CONNECT_FAIL = BASE_NUM + 1;
	/** 蓝牙连接成功 */
	public static final int BLE_CONNECT_SUCCESS = BASE_NUM + 2;
	/** 上电失败 */
	public static final int POWER_ON_FAIL = BASE_NUM + 3;
	/** 上电成功 */
	public static final int POWER_ON_SUCCESS = BASE_NUM + 4;
	/** 下电失败 */
	public static final int POWER_OFF_FAIL = BASE_NUM + 5;
	/** 下电成功 */
	public static final int POWER_OFF_SUCCESS = BASE_NUM + 6;
	/** 读卡失败 */
	public static final int READ_CARD_FAIL = BASE_NUM + 7;
	/** 读卡成功 */
	public static final int READ_CARD_SUCCESS = BASE_NUM + 8;
	/** 未刷表 */
	public static final int CARD_NOT_FLUSH = BASE_NUM + 9;
	/** 获取序列号失败 */
	public static final int GET_SERIALNUM_FAIL = BASE_NUM + 10;
	/** 获取序列号失败 */
	public static final int CHECK_SERIALNUM_FAIL = BASE_NUM + 11;
	/** 进入3F02目录失败 */
	public static final int ENTER_3F02_FAIL = BASE_NUM + 12;
	/** 获取随机数失败 */
	public static final int GET_RANDOM8_FAIL = BASE_NUM + 13;
	/** IP端口错误 */
	public static final int IP_PORT_ERROR = BASE_NUM + 14;
	/** 外部认证登录失败 */
	public static final int NET_OUTAUTH_LOGIN_FAIL = BASE_NUM + 15;
	/** 返回值错误 */
	public static final int NET_OUTAUTH_VALUE_ERROR = BASE_NUM + 16;
	/** 外部认证指令错误 */
	public static final int TRANS_OUTAUTH_FAIL = BASE_NUM + 17;
	/** 获取随机数失败 */
	public static final int GET_RANDOM4_FAIL = BASE_NUM + 18;
	/** 获取卡待加密数据失败 */
	public static final int GET_CARDINFO_FAIL = BASE_NUM + 19;
	/** 获取加密数据失败 */
	public static final int GET_DESDATA_FAIL = BASE_NUM + 20;
	/** 发送加密数据指令失败 */
	public static final int SEND_DESDATA_FAIL = BASE_NUM + 21;
	/** 发送金额数据指令失败 */
	public static final int SEND_MONEY_FAIL = BASE_NUM + 22;
	/** 圈存失败 */
	public static final int GET_QUAN_FAIL = BASE_NUM + 23;
	/** 圈存指令失败 */
	public static final int SEND_QUAN_MAC = BASE_NUM + 24;
	/** 选择文件失败 */
	public static final int SELECT_FILE_FAIL = BASE_NUM + 25;
	/** 写200字节失败 */
	public static final int SEND_200_FAIL = BASE_NUM + 26;
	/** 写卡成功 */
	public static final int WRITE_CARD_OK = BASE_NUM + 27;
}
