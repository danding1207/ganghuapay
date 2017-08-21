/**
 * 解决Https通信兼容问题
 * @author bo.sun
 */
package com.mqt.ganghuazhifu.http

import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class FakeX509TrustManager : X509TrustManager {

    @Throws(java.security.cert.CertificateException::class)
    override fun checkClientTrusted(x509Certificates: Array<java.security.cert.X509Certificate>, s: String) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Throws(java.security.cert.CertificateException::class)
    override fun checkServerTrusted(x509Certificates: Array<java.security.cert.X509Certificate>, s: String) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    fun isClientTrusted(chain: Array<X509Certificate>): Boolean {
        return true
    }

    fun isServerTrusted(chain: Array<X509Certificate>): Boolean {
        return true
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return _AcceptedIssuers
    }

    companion object {

        private var trustManagers: Array<TrustManager>? = null
        private val _AcceptedIssuers = arrayOf<X509Certificate>()

        fun allowAllSSL() {
            HttpsURLConnection.setDefaultHostnameVerifier { arg0, arg1 ->
                // TODO Auto-generated method stub
                true
            }

            var context: SSLContext? = null
            if (trustManagers == null) {
                trustManagers = arrayOf<TrustManager>(FakeX509TrustManager())
            }

            try {
                context = SSLContext.getInstance("TLS")
                context!!.init(null, trustManagers, SecureRandom())
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }

            HttpsURLConnection.setDefaultSSLSocketFactory(context!!.socketFactory)
        }
    }

}
