package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

import com.alibaba.fastjson.JSONObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.User
import com.mqt.ganghuazhifu.databinding.ActivityTwoWeiMaBinding
import com.mqt.ganghuazhifu.databinding.ActivityTwoWeiMaItemBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.AESUtils
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.mqt.ganghuazhifu.utils.UnitConversionUtils
import com.orhanobut.logger.Logger

import java.util.ArrayList
import java.util.Hashtable

import okhttp3.RequestBody

/**
 * 员工二维码

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class TwoWeiMaActivity : BaseActivity() {
    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var adapter: MyPagerAdapter? = null
    private val viewsList = ArrayList<View>()
    private var QR_WIDTH: Int = 0
    private var QR_HEIGHT: Int = 0
    private var activityTwoWeiMaBinding: ActivityTwoWeiMaBinding? = null
    private var activityTwoWeiMaItemBinding1: ActivityTwoWeiMaItemBinding? = null
    private var activityTwoWeiMaItemBinding2: ActivityTwoWeiMaItemBinding? = null
    private var activityTwoWeiMaItemBinding3: ActivityTwoWeiMaItemBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTwoWeiMaBinding = DataBindingUtil.setContentView<ActivityTwoWeiMaBinding>(this, R.layout.activity_two_wei_ma)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityTwoWeiMaBinding!!.toolbar)
        supportActionBar!!.setTitle("员工二维码")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (WelcomeActivity.screenwidth == 0 || WelcomeActivity.screenhigh == 0) {
            WelcomeActivity.screenwidth = EncryptedPreferencesUtils.getScreenSize()[0]
            WelcomeActivity.screenhigh = EncryptedPreferencesUtils.getScreenSize()[1]
        }
        Logger.i("screenwidth--->" + WelcomeActivity.screenwidth)
        Logger.i("screenhigh--->" + WelcomeActivity.screenhigh)
        QR_WIDTH = WelcomeActivity.screenwidth * 4 / 5
        QR_HEIGHT = QR_WIDTH
        getStaffQRcode()

        activityTwoWeiMaItemBinding1 = ActivityTwoWeiMaItemBinding.inflate(LayoutInflater.from(this), null, false)
        activityTwoWeiMaItemBinding1!!.toLeft.visibility = View.INVISIBLE

        activityTwoWeiMaItemBinding1!!.toRight.setOnClickListener { v -> activityTwoWeiMaBinding!!.viewPager.setCurrentItem(1) }

        activityTwoWeiMaItemBinding1!!.tvTitle.text = "注册二维码"
        viewsList.add(activityTwoWeiMaItemBinding1!!.root)

        activityTwoWeiMaItemBinding2 = ActivityTwoWeiMaItemBinding.inflate(LayoutInflater.from(this), null, false)

        activityTwoWeiMaItemBinding2!!.toLeft.setOnClickListener { v -> activityTwoWeiMaBinding!!.viewPager.setCurrentItem(0) }
        activityTwoWeiMaItemBinding2!!.toRight.setOnClickListener { v -> activityTwoWeiMaBinding!!.viewPager.setCurrentItem(2) }

        activityTwoWeiMaItemBinding2!!.tvTitle.text = "营业费预存二维码"
        viewsList.add(activityTwoWeiMaItemBinding2!!.root)

        activityTwoWeiMaItemBinding3 = ActivityTwoWeiMaItemBinding.inflate(LayoutInflater.from(this), null, false)
        activityTwoWeiMaItemBinding3!!.toLeft.setOnClickListener { v -> activityTwoWeiMaBinding!!.viewPager.setCurrentItem(1) }
        val params = LinearLayout.LayoutParams(QR_WIDTH, QR_HEIGHT)
        params.topMargin = UnitConversionUtils.dipTopx(this, 12f)
        params.bottomMargin = UnitConversionUtils.dipTopx(this, 12f)
        params.leftMargin = UnitConversionUtils.dipTopx(this, 12f)
        params.rightMargin = UnitConversionUtils.dipTopx(this, 12f)
        activityTwoWeiMaItemBinding3!!.sweepIV.layoutParams = params
        activityTwoWeiMaItemBinding3!!.sweepIV.setImageResource(R.drawable.qrcord)
        activityTwoWeiMaItemBinding3!!.tvTitle.text = "下载二维码"
        activityTwoWeiMaItemBinding3!!.toRight.visibility = View.INVISIBLE
        viewsList.add(activityTwoWeiMaItemBinding3!!.root)

        activityTwoWeiMaBinding!!.viewPager.setFocusable(true)
        activityTwoWeiMaBinding!!.viewPager.setOffscreenPageLimit(3)
        adapter = MyPagerAdapter()
        activityTwoWeiMaBinding!!.viewPager.setAdapter(adapter)
        activityTwoWeiMaBinding!!.viewPager.setCurrentItem(0)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    private fun getStaffQRcode() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForStaffQRcode(user.PhoneNb)
        post(HttpURLS.getStaffQRcode, true, "StaffQRcode", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d("---------------------------" + response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getJSONObject("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000" && ResponseFields != null) {
                    val RegisterQRcode = ResponseFields.getString("RegisterQRcode")
                    val PrestoreQRcode = ResponseFields.getString("PrestoreQRcode")
                    activityTwoWeiMaItemBinding1!!.sweepIV.setImageBitmap(createQRImage(RegisterQRcode))
                    if (PrestoreQRcode != null) {
                        Logger.d("---------------------------" + PrestoreQRcode.toString())
                        // 加密
                        Log.e("CODE", "加密前：" + PrestoreQRcode)
                        val encryptResultStr = AESUtils.encrypt(PrestoreQRcode)
                        Log.e("CODE", "加密后：" + encryptResultStr)
                        activityTwoWeiMaItemBinding2!!.sweepIV.setImageBitmap(createQRImage(encryptResultStr))
                    }
                } else {
                    ToastUtil.Companion.toastError(ProcessDes)
                }
            }
        })
    }

    // 要转换的地址或字符串,可以是中文
    fun createQRImage(url: String?): Bitmap? {

        try {
            // 判断URL合法性
            if (url == null || "" == url || url.length < 1) {
                return null
            }
            val hints = Hashtable<EncodeHintType, String>()
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8")
            // 图像数据转换，使用了矩阵转换
            val bitMatrix = QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints)
            val pixels = IntArray(QR_WIDTH * QR_HEIGHT)
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (y in 0..QR_HEIGHT - 1) {
                for (x in 0..QR_WIDTH - 1) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000.toInt()
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff.toInt()
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            val bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT)
            // 显示到一个ImageView上面
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }

    }

    private inner class MyPagerAdapter : PagerAdapter() {

        override fun destroyItem(container: View?, position: Int, `object`: Any?) {
            // ((ViewPager) container).removeView(imageViewsList.get(position));
        }

        override fun instantiateItem(container: View?, position: Int): Any {
            if (viewsList[position].parent == null) {
                (container as ViewPager).addView(viewsList[position])
            }
            return viewsList[position]
        }

        override fun getCount(): Int {
            return viewsList.size
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun restoreState(arg0: Parcelable?, arg1: ClassLoader?) {

        }

        override fun saveState(): Parcelable? {
            return null
        }

    }

}
