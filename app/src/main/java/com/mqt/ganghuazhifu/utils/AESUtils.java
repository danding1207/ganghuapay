package com.mqt.ganghuazhifu.utils;

public class AESUtils {

	public static final String PASSWORD = "123";

	/**
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @return
	 */
	public static String encrypt(String content) {
		if (content.contains("1"))
			content = content.replace("1", "q");
		if (content.contains("2"))
			content = content.replace("2", "w");
		if (content.contains("3"))
			content = content.replace("3", "e");
		if (content.contains("4"))
			content = content.replace("4", "r");
		if (content.contains("5"))
			content = content.replace("5", "t");
		if (content.contains("6"))
			content = content.replace("6", "y");
		if (content.contains("7"))
			content = content.replace("7", "u");
		if (content.contains("8"))
			content = content.replace("8", "i");
		if (content.contains("9"))
			content = content.replace("9", "o");
		if (content.contains("0"))
			content = content.replace("0", "p");
		if (content.contains("|"))
			content = content.replace("|", "l");
		return content;
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 */
	public static String decrypt(String content) {
		if (content.contains("q"))
			content = content.replace("q", "1");
		if (content.contains("w"))
			content = content.replace("w", "2");
		if (content.contains("e"))
			content = content.replace("e", "3");
		if (content.contains("r"))
			content = content.replace("r", "4");
		if (content.contains("t"))
			content = content.replace("t", "5");
		if (content.contains("y"))
			content = content.replace("y", "6");
		if (content.contains("u"))
			content = content.replace("u", "7");
		if (content.contains("i"))
			content = content.replace("i", "8");
		if (content.contains("o"))
			content = content.replace("o", "9");
		if (content.contains("p"))
			content = content.replace("p", "0");
		if (content.contains("l"))
			content = content.replace("l", "|");
		return content;
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

}
