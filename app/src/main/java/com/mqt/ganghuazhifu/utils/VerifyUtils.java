package com.mqt.ganghuazhifu.utils;

import android.text.TextUtils;

public class VerifyUtils {

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {

		/*
         * 130-139 (134x（0-8）1349)
		 * 150-153 155-159 
		 * 180-189 
		 * 147 145 149
		 * 172-173   175-178
		 *
		 * 170  171 虚拟运营商号段
		 * 1700 1705 1709
		 */
        String telRegex;
        telRegex = "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(14[579])|(17[2-3])|(17[5-8]))\\d{8}$";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
//			if (mobiles.matches(telRegex)) {
//				return true;
//			} else {
//				telRegex = "^(170[059])\\d{7}$";
            return mobiles.matches(telRegex);
//			}
        }
    }


    /**
     * 校验银行卡卡号
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

}
