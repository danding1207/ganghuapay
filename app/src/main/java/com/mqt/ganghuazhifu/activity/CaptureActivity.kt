package com.mqt.ganghuazhifu.activity

import com.google.zxing.Result

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityCaptureBinding
import com.mqt.ganghuazhifu.utils.AESUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
* Created by danding1207 on 16/10/25.
*/
class CaptureActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    private var activityCaptureBinding: ActivityCaptureBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCaptureBinding = DataBindingUtil.setContentView<ActivityCaptureBinding>(this, R.layout.activity_capture)
        initPermissions()
        initView()
    }

    internal val ACCESS_GET_CAMERA = 10

    private fun initPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), ACCESS_GET_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESS_GET_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                finish()
            }
        }
    }

    private fun initView() {
        setSupportActionBar(activityCaptureBinding!!.toolbar)
        supportActionBar!!.title = "二维码扫描"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        activityCaptureBinding!!.zXingScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        activityCaptureBinding!!.zXingScannerView.startCamera()          // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        activityCaptureBinding!!.zXingScannerView.stopCamera()           // Stop camera on pause
    }

    override fun OnViewClick(v: View) {

    }

    override fun handleResult(rawResult: Result) {
        // Do something with the result here
        Logger.i(rawResult.text) // Prints scan results
        Logger.i(rawResult.barcodeFormat.toString()) // Prints the scan format (qrcode, pdf417 etc.)
        val result = rawResult.text
        if (result.startsWith("http")) {
            val uri = Uri.parse(result)
            val it = Intent(Intent.ACTION_VIEW, uri)
            startActivity(it)
        } else {
            // 解密
//            val decryptResult = AESUtils.decrypt(result)
//            Logger.e("解密后：" + decryptResult)
//            val ss = decryptResult.split("\\|".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
//            if (ss.size >= 4)
//                YucunActivity.startActivity(this@CaptureActivity, ss[0], ss[1], ss[2], ss[3])
//            else
//                ToastUtil.toastInfo(result)

            ToastUtil.toastInfo(result)
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
