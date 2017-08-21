package com.mqt.ganghuazhifu.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;

import android.util.Base64;
import android.util.Log;

public class RSAUtils {

	public static final String TAG = "RSAUtils";// 非对称加密密钥算法
	public static final String RSA = "RSA";// 非对称加密密钥算法
	public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";// 加密填充方式
	public static final int DEFAULT_KEY_SIZE = 2048;// 秘钥默认长度
	public static final byte[] DEFAULT_SPLIT = "#PART#".getBytes(); // 当要加密的内容超过bufferSize，则采用partSplit进行分块加密
	public static final int DEFAULT_BUFFERSIZE = (DEFAULT_KEY_SIZE / 8) - 11;// 当前秘钥支持加密的最大字节数

	/**
	 * 随机生成RSA密钥对
	 *
	 * @param keyLength
	 *            密钥长度，范围：512～2048 一般1024
	 * @return
	 */
	public static KeyPair generateRSAKeyPair(int keyLength) {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
			kpg.initialize(keyLength);
			return kpg.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用公钥对字符串进行加密
	 *
	 * @param data
	 *            原文
	 */
	public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
		// 得到公钥
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory kf = KeyFactory.getInstance(RSA);
		PublicKey keyPublic = kf.generatePublic(keySpec);
		// 加密数据
		Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
		cipher.init(Cipher.ENCRYPT_MODE, keyPublic);
		return cipher.doFinal(data);
	}

	/**
	 * 私钥加密
	 *
	 * @param data
	 *            待加密数据
	 * @param privateKey
	 *            密钥
	 * @return byte[] 加密数据
	 */
	public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) throws Exception {
		// 得到私钥
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory kf = KeyFactory.getInstance(RSA);
		PrivateKey keyPrivate = kf.generatePrivate(keySpec);
		// 数据加密
		Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
		cipher.init(Cipher.ENCRYPT_MODE, keyPrivate);
		return cipher.doFinal(data);
	}

	/**
	 * 公钥解密
	 *
	 * @param data
	 *            待解密数据
	 * @param publicKey
	 *            密钥
	 * @return byte[] 解密数据
	 */
	public static byte[] decryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
		// 得到公钥
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory kf = KeyFactory.getInstance(RSA);
		PublicKey keyPublic = kf.generatePublic(keySpec);
		// 数据解密
		Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
		cipher.init(Cipher.DECRYPT_MODE, keyPublic);
		return cipher.doFinal(data);
	}

	/**
	 * 使用私钥进行解密
	 */
	public static byte[] decryptByPrivateKey(byte[] encrypted, byte[] privateKey) throws Exception {
		// 得到私钥
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory kf = KeyFactory.getInstance(RSA);
		PrivateKey keyPrivate = kf.generatePrivate(keySpec);

		// 解密数据
		Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
		cp.init(Cipher.DECRYPT_MODE, keyPrivate);
		byte[] arr = cp.doFinal(encrypted);
		return arr;
	}

	/**
	 * 用公钥对字符串进行分段加密
	 *
	 */
	public static byte[] encryptByPublicKeyForSpilt(byte[] data, byte[] publicKey) throws Exception {
		int dataLen = data.length;
		if (dataLen <= DEFAULT_BUFFERSIZE) {
			return encryptByPublicKey(data, publicKey);
		}
		List<Byte> allBytes = new ArrayList<Byte>(2048);
		int bufIndex = 0;
		int subDataLoop = 0;
		byte[] buf = new byte[DEFAULT_BUFFERSIZE];
		for (int i = 0; i < dataLen; i++) {
			buf[bufIndex] = data[i];
			if (++bufIndex == DEFAULT_BUFFERSIZE || i == dataLen - 1) {
				subDataLoop++;
				if (subDataLoop != 1) {
					for (byte b : DEFAULT_SPLIT) {
						allBytes.add(b);
					}
				}
				byte[] encryptBytes = encryptByPublicKey(buf, publicKey);
				for (byte b : encryptBytes) {
					allBytes.add(b);
				}
				bufIndex = 0;
				if (i == dataLen - 1) {
					buf = null;
				} else {
					buf = new byte[Math.min(DEFAULT_BUFFERSIZE, dataLen - i - 1)];
				}
			}
		}
		byte[] bytes = new byte[allBytes.size()];
		{
			int i = 0;
			for (Byte b : allBytes) {
				bytes[i++] = b.byteValue();
			}
		}
		return bytes;
	}

	/**
	 * 分段加密
	 *
	 * @param data
	 *            要加密的原始数据
	 * @param privateKey
	 *            秘钥
	 */
	public static byte[] encryptByPrivateKeyForSpilt(byte[] data, byte[] privateKey) throws Exception {
		int dataLen = data.length;
		if (dataLen <= DEFAULT_BUFFERSIZE) {
			return encryptByPrivateKey(data, privateKey);
		}
		List<Byte> allBytes = new ArrayList<Byte>(2048);
		int bufIndex = 0;
		int subDataLoop = 0;
		byte[] buf = new byte[DEFAULT_BUFFERSIZE];
		for (int i = 0; i < dataLen; i++) {
			buf[bufIndex] = data[i];
			if (++bufIndex == DEFAULT_BUFFERSIZE || i == dataLen - 1) {
				subDataLoop++;
				if (subDataLoop != 1) {
					for (byte b : DEFAULT_SPLIT) {
						allBytes.add(b);
					}
				}
				byte[] encryptBytes = encryptByPrivateKey(buf, privateKey);
				for (byte b : encryptBytes) {
					allBytes.add(b);
				}
				bufIndex = 0;
				if (i == dataLen - 1) {
					buf = null;
				} else {
					buf = new byte[Math.min(DEFAULT_BUFFERSIZE, dataLen - i - 1)];
				}
			}
		}
		byte[] bytes = new byte[allBytes.size()];
		{
			int i = 0;
			for (Byte b : allBytes) {
				bytes[i++] = b.byteValue();
			}
		}
		return bytes;
	}

	/**
	 * 公钥分段解密
	 *
	 * @param encrypted
	 *            待解密数据
	 * @param publicKey
	 *            密钥
	 */
	public static byte[] decryptByPublicKeyForSpilt(byte[] encrypted, byte[] publicKey) throws Exception {
		int splitLen = DEFAULT_SPLIT.length;
		if (splitLen <= 0) {
			return decryptByPublicKey(encrypted, publicKey);
		}
		int dataLen = encrypted.length;
		List<Byte> allBytes = new ArrayList<Byte>(1024);
		int latestStartIndex = 0;
		for (int i = 0; i < dataLen; i++) {
			byte bt = encrypted[i];
			boolean isMatchSplit = false;
			if (i == dataLen - 1) {
				// 到data的最后了
				byte[] part = new byte[dataLen - latestStartIndex];
				System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
				byte[] decryptPart = decryptByPublicKey(part, publicKey);
				for (byte b : decryptPart) {
					allBytes.add(b);
				}
				latestStartIndex = i + splitLen;
				i = latestStartIndex - 1;
			} else if (bt == DEFAULT_SPLIT[0]) {
				// 这个是以split[0]开头
				if (splitLen > 1) {
					if (i + splitLen < dataLen) {
						// 没有超出data的范围
						for (int j = 1; j < splitLen; j++) {
							if (DEFAULT_SPLIT[j] != encrypted[i + j]) {
								break;
							}
							if (j == splitLen - 1) {
								// 验证到split的最后一位，都没有break，则表明已经确认是split段
								isMatchSplit = true;
							}
						}
					}
				} else {
					// split只有一位，则已经匹配了
					isMatchSplit = true;
				}
			}
			if (isMatchSplit) {
				byte[] part = new byte[i - latestStartIndex];
				System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
				byte[] decryptPart = decryptByPublicKey(part, publicKey);
				for (byte b : decryptPart) {
					allBytes.add(b);
				}
				latestStartIndex = i + splitLen;
				i = latestStartIndex - 1;
			}
		}
		byte[] bytes = new byte[allBytes.size()];
		{
			int i = 0;
			for (Byte b : allBytes) {
				bytes[i++] = b.byteValue();
			}
		}
		return bytes;
	}

	/**
	 * 使用私钥分段解密
	 *
	 */
	public static byte[] decryptByPrivateKeyForSpilt(byte[] encrypted, byte[] privateKey) throws Exception {
		int splitLen = DEFAULT_SPLIT.length;
		if (splitLen <= 0) {
			return decryptByPrivateKey(encrypted, privateKey);
		}
		int dataLen = encrypted.length;
		List<Byte> allBytes = new ArrayList<Byte>(1024);
		int latestStartIndex = 0;
		for (int i = 0; i < dataLen; i++) {
			byte bt = encrypted[i];
			boolean isMatchSplit = false;
			if (i == dataLen - 1) {
				// 到data的最后了
				byte[] part = new byte[dataLen - latestStartIndex];
				System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
				byte[] decryptPart = decryptByPrivateKey(part, privateKey);
				for (byte b : decryptPart) {
					allBytes.add(b);
				}
				latestStartIndex = i + splitLen;
				i = latestStartIndex - 1;
			} else if (bt == DEFAULT_SPLIT[0]) {
				// 这个是以split[0]开头
				if (splitLen > 1) {
					if (i + splitLen < dataLen) {
						// 没有超出data的范围
						for (int j = 1; j < splitLen; j++) {
							if (DEFAULT_SPLIT[j] != encrypted[i + j]) {
								break;
							}
							if (j == splitLen - 1) {
								// 验证到split的最后一位，都没有break，则表明已经确认是split段
								isMatchSplit = true;
							}
						}
					}
				} else {
					// split只有一位，则已经匹配了
					isMatchSplit = true;
				}
			}
			if (isMatchSplit) {
				byte[] part = new byte[i - latestStartIndex];
				System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
				byte[] decryptPart = decryptByPrivateKey(part, privateKey);
				for (byte b : decryptPart) {
					allBytes.add(b);
				}
				latestStartIndex = i + splitLen;
				i = latestStartIndex - 1;
			}
		}
		byte[] bytes = new byte[allBytes.size()];
		{
			int i = 0;
			for (Byte b : allBytes) {
				bytes[i++] = b.byteValue();
			}
		}
		return bytes;
	}

	{
		KeyPair keyPair = RSAUtils.generateRSAKeyPair(RSAUtils.DEFAULT_KEY_SIZE);
		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		String jsonData = "jsonData";

		// 公钥加密
		try {
			long start = System.currentTimeMillis();
			byte[] encryptBytes = RSAUtils.encryptByPublicKeyForSpilt(jsonData.getBytes(), publicKey.getEncoded());
			long end = System.currentTimeMillis();
			Log.e(TAG, "公钥加密耗时 cost time---->" + (end - start));
			String encryStr = Base64.encodeToString(encryptBytes, Base64.DEFAULT);
			Log.e(TAG, "加密后json数据 --1-->" + encryStr);
			Log.e(TAG, "加密后json数据长度 --1-->" + encryStr.length());
			// 私钥解密
			start = System.currentTimeMillis();
			byte[] decryptBytes = RSAUtils.decryptByPrivateKeyForSpilt(Base64.decode(encryStr, Base64.DEFAULT),
					privateKey.getEncoded());
			String decryStr = new String(decryptBytes);
			end = System.currentTimeMillis();
			Log.e(TAG, "私钥解密耗时 cost time---->" + (end - start));
			Log.e(TAG, "解密后json数据 --1-->" + decryStr);

			// 私钥加密
			start = System.currentTimeMillis();
			encryptBytes = RSAUtils.encryptByPrivateKeyForSpilt(jsonData.getBytes(), privateKey.getEncoded());
			end = System.currentTimeMillis();
			Log.e(TAG, "私钥加密密耗时 cost time---->" + (end - start));
			encryStr = Base64.encodeToString(encryptBytes, Base64.DEFAULT);
			Log.e(TAG, "加密后json数据 --2-->" + encryStr);
			Log.e(TAG, "加密后json数据长度 --2-->" + encryStr.length());
			// 公钥解密
			start = System.currentTimeMillis();
			decryptBytes = RSAUtils.decryptByPublicKeyForSpilt(Base64.decode(encryStr, Base64.DEFAULT), publicKey.getEncoded());
			decryStr = new String(decryptBytes);
			end = System.currentTimeMillis();
			Log.e(TAG, "公钥解密耗时 cost time---->" + (end - start));
			Log.e(TAG, "解密后json数据 --2-->" + decryStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 通过公钥byte[]将公钥还原，适用于RSA算法
	public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	// 通过私钥byte[]将私钥还原，适用于RSA算法
	public static PrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	// 打印公钥信息
	public static void printPublicKeyInfo(PublicKey key) {
		RSAPublicKey rsaPublicKey = (RSAPublicKey) key;
		Log.e(TAG, "RSAPublicKey:");
		Log.e(TAG, "Modulus.length=" + rsaPublicKey.getModulus().bitLength());
		Log.e(TAG, "Modulus=" + rsaPublicKey.getModulus().toString());
		Log.e(TAG, "PublicExponent.length=" + rsaPublicKey.getPublicExponent().bitLength());
		Log.e(TAG, "PublicExponent=" + rsaPublicKey.getPublicExponent().toString());
	}

	// 打印私钥信息
	public static void printPublicKeyInfo(PrivateKey key) {
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) key;
		Log.e(TAG, "RSAPrivateKey:");
		Log.e(TAG, "Modulus.length=" + rsaPrivateKey.getModulus().bitLength());
		Log.e(TAG, "Modulus=" + rsaPrivateKey.getModulus().toString());
		Log.e(TAG, "PublicExponent.length=" + rsaPrivateKey.getPrivateExponent().bitLength());
		Log.e(TAG, "PublicExponent=" + rsaPrivateKey.getPrivateExponent().toString());
	}

}
