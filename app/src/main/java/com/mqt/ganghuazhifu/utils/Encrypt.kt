package com.mqt.ganghuazhifu.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by danding1207 on 17/5/22.
 */

object Encrypt {

    /**
     * 传入文本内容，返回 MD5 串
     */
    fun MD5(strText: String?): String {
        return SHA(strText, "MD5")
    }

    /**
     * 传入文本内容，返回 SHA-256 串
     */
    fun SHA256(strText: String?): String {
        return SHA(strText, "SHA-256")
    }

    /**
     * 传入文本内容，返回 SHA-512 串
     */
    fun SHA512(strText: String?): String {
        return SHA(strText, "SHA-512")
    }

    /**
     * 字符串 SHA 加密
     */
    private fun SHA(strSourceText: String?, strType: String): String {
        // 返回值
        var strResult: String = ""
        // 是否是有效字符串
        if (strSourceText != null && strSourceText.length > 0) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                val messageDigest = MessageDigest.getInstance(strType)
                // 传入要加密的字符串
                messageDigest.update(strSourceText.toByteArray())
                // 得到 byte 類型结果
                val byteBuffer = messageDigest.digest()
                // 將 byte 轉換爲 string
                val strHexString = StringBuffer()
                // 遍歷 byte buffer
                for (i in byteBuffer.indices) {
                    val hex = Integer.toHexString(0xff and byteBuffer[i].toInt())
                    if (hex.length == 1) {
                        strHexString.append('0')
                    }
                    strHexString.append(hex)
                }
                // 得到返回結果
                strResult = strHexString.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }
        return strResult
    }

}
