package com.mqt.ganghuazhifu.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Point
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.Record
import com.mqt.ganghuazhifu.bean.RecordChangedEvent
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.databinding.ActivityRecordDetailBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 快钱支付

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
open class RecordDetailActivity : BaseActivity() {
    private var position: Int = 0
    private var record: Record? = null
    // 界面文件命名
    private var imageName: String? = null
    private var activityRecordDetailBinding: ActivityRecordDetailBinding? = null

    private var blueType:Int = 1
    private var shebeiType:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRecordDetailBinding = DataBindingUtil.setContentView<ActivityRecordDetailBinding>(this,
                R.layout.activity_record_detail)
        record = Parcels.unwrap<Record>(intent.getParcelableExtra<Parcelable>(RECORD))
        position = intent.getIntExtra("Position", -1)
        if (record != null)
            Logger.e(record!!.toString())
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityRecordDetailBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val nameBuilder: StringBuilder
        supportActionBar!!.title = "交易记录"
        if (!TextUtils.isEmpty(record!!.usernm) && record!!.usernm != "[]") {
            // 用户名*加密
            nameBuilder = StringBuilder(record!!.usernm)
            if (record!!.usernm.length <= 1) {
            } else if (record!!.usernm.length <= 3) {
                nameBuilder.setCharAt(0, '*')
            } else {
                for (i in 0..record!!.usernm.length - 2 - 1) {
                    nameBuilder.setCharAt(i, '*')
                }
            }
            activityRecordDetailBinding!!.tvUserName.text = nameBuilder.toString()
        }

        if (!TextUtils.isEmpty(record!!.usernb) && record!!.usernb != "[]") {
            activityRecordDetailBinding!!.tvUserNumber.text = record!!.usernb
        }

        if (!TextUtils.isEmpty(record!!.useraddr) && record!!.useraddr != "[]") {
            activityRecordDetailBinding!!.tvUserAddress.text = record!!.useraddr
        } else {
            activityRecordDetailBinding!!.tvUserAddress.text = "地址信息暂无"
        }

        if (!TextUtils.isEmpty(record!!.amount) && record!!.amount != "[]") {
            activityRecordDetailBinding!!.tvMoney.text = "￥" + record!!.amount
        }

        if (!TextUtils.isEmpty(record!!.ordersettime) && record!!.ordersettime != "[]") {
            activityRecordDetailBinding!!.tvCreateTime.text = record!!.ordersettime.replace("T", " ")
        }

        if (!TextUtils.isEmpty(record!!.paydate) && record!!.paydate != "[]") {
            val sb = StringBuilder()
            sb.append(record!!.paydate.substring(0, 4))
            sb.append("-")
            sb.append(record!!.paydate.substring(4, 6))
            sb.append("-")
            sb.append(record!!.paydate.substring(6, 8))
            activityRecordDetailBinding!!.tvPayTime.text = sb.toString()
        }

        if (!TextUtils.isEmpty(record!!.ordernb) && record!!.ordernb != "[]") {
            activityRecordDetailBinding!!.tvOrderNum.text = record!!.ordernb
        }

        if (!TextUtils.isEmpty(record!!.cdtrnm) && record!!.cdtrnm != "[]") {
            activityRecordDetailBinding!!.tvGetMember.text = record!!.cdtrnm
        }

        if (!TextUtils.isEmpty(record!!.status) && record!!.status != "[]") {
            if (record!!.status == "PR00") {
                // 已付款
                activityRecordDetailBinding!!.tvStatus.text = "已付款"
                if (record!!.paystatus == "PR07") {
                    activityRecordDetailBinding!!.tvGoPay.text = "申请退款"
                    activityRecordDetailBinding!!.cardViewGoPay.setOnClickListener { v -> refundDialog() }
                } else {
                    if ("11" == record!!.nfcflag) {
                        shebeiType = 1

                        activityRecordDetailBinding!!.llNfcstatus.visibility = View.VISIBLE
                        activityRecordDetailBinding!!.tvNfcstatusName.text = "NFC写表状态："
                        if ("11" == record!!.nfcpayflag) {// 成功
                            activityRecordDetailBinding!!.tvNfcstatus.text = "写入成功"
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.GONE
                        } else {
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.VISIBLE
                            activityRecordDetailBinding!!.tvNfc.text = "NFC刷表"
                            activityRecordDetailBinding!!.cardViewNfc.setOnClickListener { v ->
                                NFC()
                            }
                            activityRecordDetailBinding!!.tvNfcstatus.text = "未写入"
                        }
                    } else if ("12" == record!!.nfcflag) {
                        shebeiType = 1

                        activityRecordDetailBinding!!.llNfcstatus.visibility = View.VISIBLE
                        activityRecordDetailBinding!!.tvNfcstatusName.text = "蓝牙写卡状态："
                        if ("11" == record!!.nfcpayflag) {// 成功
                            activityRecordDetailBinding!!.tvNfcstatus.text = "写入成功"
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.GONE
                        } else {
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.VISIBLE
                            activityRecordDetailBinding!!.tvNfc.text = "蓝牙写卡"
                            activityRecordDetailBinding!!.cardViewNfc.setOnClickListener { v ->
                                blueType = 1
                                Bluetooth()
                            }
                            activityRecordDetailBinding!!.tvNfcstatus.text = "未写入"
                        }
                    } else if ("13" == record!!.nfcflag) {
                        activityRecordDetailBinding!!.llNfcstatus.visibility = View.VISIBLE
                        activityRecordDetailBinding!!.tvNfcstatusName.text = "蓝牙写表状态："
                        if ("11" == record!!.nfcpayflag) {// 成功
                            activityRecordDetailBinding!!.tvNfcstatus.text = "写入成功"
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.GONE
                        } else {
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.VISIBLE
                            activityRecordDetailBinding!!.tvNfc.text = "蓝牙写表"
                            activityRecordDetailBinding!!.cardViewNfc.setOnClickListener { v ->
                                blueType = 2
                                BluetoothToShebei()
                            }
                            activityRecordDetailBinding!!.tvNfcstatus.text = "未写入"
                        }
                    } else if ("14" == record!!.nfcflag) {
                        shebeiType = 2
                        activityRecordDetailBinding!!.llNfcstatus.visibility = View.VISIBLE
                        activityRecordDetailBinding!!.tvNfcstatusName.text = "NFC写表状态："
                        if ("11" == record!!.nfcpayflag) {// 成功
                            activityRecordDetailBinding!!.tvNfcstatus.text = "写表成功"
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.GONE
                        } else {
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.VISIBLE
                            activityRecordDetailBinding!!.tvNfc.text = "NFC刷表"
                            activityRecordDetailBinding!!.cardViewNfc.setOnClickListener { v ->
                                NFC()
                            }
                            activityRecordDetailBinding!!.tvNfcstatus.text = "未写入"
                        }
                    } else if ("15" == record!!.nfcflag) {
                        shebeiType = 2
                        activityRecordDetailBinding!!.llNfcstatus.visibility = View.VISIBLE
                        activityRecordDetailBinding!!.tvNfcstatusName.text = "蓝牙写表状态："
                        if ("11" == record!!.nfcpayflag) {// 成功
                            activityRecordDetailBinding!!.tvNfcstatus.text = "写入成功"
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.GONE
                        } else {
                            activityRecordDetailBinding!!.cardViewNfc.visibility = View.VISIBLE
                            activityRecordDetailBinding!!.tvNfc.text = "蓝牙写表"
                            activityRecordDetailBinding!!.cardViewNfc.setOnClickListener { v ->
                                blueType = 1
                                Bluetooth()
                            }
                            activityRecordDetailBinding!!.tvNfcstatus.text = "未写入"
                        }
                    } else {
                        activityRecordDetailBinding!!.llNfcstatus.visibility = View.GONE
                        activityRecordDetailBinding!!.cardViewNfc.visibility = View.GONE
                    }
                }
            } else if (record!!.status == "PR01") {
                // 待付款
                activityRecordDetailBinding!!.tvStatus.text = "待付款"
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.VISIBLE
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewCanclePay.setOnClickListener { v -> cancleDialog() }
            } else if (record!!.status == "PR02") {
                // 已取消
                activityRecordDetailBinding!!.tvStatus.text = "已取消"
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR03") {
                // 支付失败
                activityRecordDetailBinding!!.tvStatus.text = "支付失败"
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.VISIBLE
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewCanclePay.setOnClickListener { v -> cancleDialog() }
            } else if (record!!.status == "PR04") {
                // 待退款
                activityRecordDetailBinding!!.tvStatus.text = "待退款"
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR05") {
                // 已退款
                activityRecordDetailBinding!!.tvStatus.text = "已退款"
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR09") {
                // 已退款
                activityRecordDetailBinding!!.tvStatus.text = "退款失败"
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR10") {
                // 已退款
                activityRecordDetailBinding!!.tvStatus.text = "已成功"
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR11") {
                // 已退款
                activityRecordDetailBinding!!.tvStatus.text = "已失败"
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR12") {
                // 已退款
                activityRecordDetailBinding!!.tvStatus.text = "退款中"
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR16") {
                // 已退款
                activityRecordDetailBinding!!.tvStatus.text = "核签失败"
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR17") {
                // 已退款
                activityRecordDetailBinding!!.tvStatus.text = "实际付款金额与订单金额不符"
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR18") {
                // 已冲正
                activityRecordDetailBinding!!.tvStatus.text = "已冲正"
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            } else if (record!!.status == "PR99") {
                // 已退款
                activityRecordDetailBinding!!.tvStatus.text = "异常"
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
            }
        }
        if (!TextUtils.isEmpty(record!!.paystatus) && record!!.paystatus != "[]") {
            if (record!!.paystatus == "PR06") {
                activityRecordDetailBinding!!.tvPaystatus.text = "缴费成功"
                activityRecordDetailBinding!!.stampLayout.visibility = View.VISIBLE
                displayImg()
                activityRecordDetailBinding!!.cardViewCanclePay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewGoPay.visibility = View.GONE
                activityRecordDetailBinding!!.cardViewKeep.visibility = View.VISIBLE
                activityRecordDetailBinding!!.cardViewKeep.setOnClickListener(keepImageOnClickListener())
            } else if (record!!.paystatus == "PR07") {
                // 缴费失败
                activityRecordDetailBinding!!.tvPaystatus.text = "缴费失败"
            } else if (record!!.paystatus == "PR08") {
                // 未缴费
                activityRecordDetailBinding!!.tvPaystatus.text = "未缴费"
            }
        }
        if (!TextUtils.isEmpty(record!!.ustrd) && record!!.ustrd != "[]") {
            activityRecordDetailBinding!!.tvMark.text = record!!.ustrd
        }

        if (record == null || record != null && !(record!!.status == "PR00" && record!!.paystatus == "PR07")) {
            activityRecordDetailBinding!!.cardViewGoPay.setOnClickListener { v ->
                val intent1 = Intent(this@RecordDetailActivity, PayActivity::class.java)
                intent1.putExtra("Record", Parcels.wrap<Record>(record))
                startActivity(intent1)
            }
        }

//        if (record != null) {
//            if ("11" == record!!.nfcflag || "12" == record!!.nfcflag || "13" == record!!.nfcflag) {
//                activityRecordDetailBinding!!.llNfcstatus.visibility = View.VISIBLE
//            } else {
//                activityRecordDetailBinding!!.llNfcstatus.visibility = View.GONE
//            }
//        }
    }

    private fun Refund() {
        val body = HttpRequestParams.getParamsForOrderRefund(record!!.ordernb)
        post(HttpURLS.applyRefund, true, "Refund", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        ToastUtil.toastSuccess("已申请退款!")
                        record!!.status = "PR04"
                        record!!.paystatus = "PR07"
                        initView()
                        activityRecordDetailBinding!!.tvExplainerBefore2.visibility = View.VISIBLE
                        if (position != -1) {
                            MainActivity.recordFragment.upDataList(position, record)
                        }
                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    private fun cancleOrder() {
        val body = HttpRequestParams.getParamsForOrderCancle(record!!.ordernb)
        post(HttpURLS.orderCancel, true, "OrderCancle", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        ToastUtil.toastSuccess("订单已取消!")
                        record!!.status = "PR02"
                        record!!.paystatus = "PR08"
                        initView()
                        if (position != -1) {
                            MainActivity.recordFragment.upDataList(position, record)
                        }
                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        dismissRoundProcessDialog()
        super.onDestroy()
    }

    @Subscribe(thread = EventThread.MAIN_THREAD)
    fun onRecordChangedEvent(event: RecordChangedEvent) {
        Logger.d("onEventMainThread")
        if (record != null) {
            record!!.nfcpayflag = "11"
            initView()
            if (position != -1) {
                MainActivity.recordFragment.upDataList(position, record)
            }
        }
        activityRecordDetailBinding!!.cardViewNfc.visibility = View.GONE
    }

    override fun OnViewClick(v: View) {

    }

    internal inner class keepImageOnClickListener : OnClickListener {
        override fun onClick(v: View) {
            GetandSaveCurrentImage()
        }
    }

    /**
     * 获取和保存当前屏幕的截图
     */
    @SuppressLint("NewApi")
    private fun GetandSaveCurrentImage() {
        activityRecordDetailBinding!!.tvExplainerBefore.text = "缴费凭证"
        activityRecordDetailBinding!!.cardViewKeep.visibility = View.GONE
        getName()
        // 构建Bitmap
        val windowManager = windowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val w = size.x
        val h = size.y
        var Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888)
        // 获取屏幕
        val decorview = this.window.decorView
        decorview.isDrawingCacheEnabled = true
        Bmp = decorview.drawingCache
        // 图片存储路径
        val SavePath = sdCardPath + "/ganghuazhifu/receipt"
        // 保存Bitmap
        try {
            val path = File(SavePath)
            // 文件
            val filepath = "$SavePath/$imageName.png"
            val file = File(filepath)
            if (!path.exists()) {
                path.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            val fos: FileOutputStream?
            fos = FileOutputStream(file)
            Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.flush()
            fos.close()
            ToastUtil.toastSuccess("文件已保存至SDCard/ganghuazhifu/receipt/目录下!")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 获取SDCard的目录路径功能
     */
    private // 判断SDCard是否存在
    val sdCardPath: String
        get() {
            var sdcardDir: File? = null
            val sdcardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
            if (sdcardExist) {
                sdcardDir = Environment.getExternalStorageDirectory()
            }
            return sdcardDir!!.toString()
        }

    /**
     * 获取系统时间，用于命名截图
     */
    private fun getName() {
        val formatter = SimpleDateFormat("yyyy年MM月dd日_HH_mm_ss")
        val curDate = Date(System.currentTimeMillis())// 获取当前时间
        imageName = formatter.format(curDate)

    }

    /**
     * 但是提示框
     */
    @SuppressLint("NewApi")
    protected fun cancleDialog() {
        MaterialDialog.Builder(this@RecordDetailActivity)
                .title("提醒")
                .content("如已确认扣款成功请勿取消订单！")
                .onPositive { dialog, which -> cancleOrder() }
                .negativeText("退出")
                .positiveText("取消订单")
                .show()
    }

    /**
     * 但是提示框
     */
    @SuppressLint("NewApi")
    protected fun refundDialog() {
        MaterialDialog.Builder(this@RecordDetailActivity)
                .title("提示")
                .content("您是否确定申请退款？")
                .onPositive { dialog, which -> Refund() }
                .negativeText("取消")
                .positiveText("确定")
                .show()
    }

    /**
     * 获取网落图片资源
     */
    fun displayImg() {
        Glide
                .with(this)
                .load(HttpURLS.ip + "/www/img/" + record!!.payeecode + ".png")
                .fitCenter()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(activityRecordDetailBinding!!.tvStamp)
    }

    protected fun NFC() {
        val intent = Intent(this, ReadNFCActivity::class.java)
        intent.putExtra("UserNb", record!!.usernb)
        intent.putExtra("Type", shebeiType)
        intent.putExtra("OrderNb", record!!.ordernb)
        startActivity(intent)
    }

    internal var ACCESS_COARSE_LOCATION_REQUEST_CODE = 10
    internal var ACCESS_FINE_LOCATION_REQUEST_CODE = 11

    protected fun BluetoothToShebei() {
        Logger.i("BluetoothToShebei")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    ACCESS_COARSE_LOCATION_REQUEST_CODE)
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION_REQUEST_CODE)
            } else {
                val intent = Intent(this, BluetoothSheBeiActivity::class.java)
                var NFCICSumCount: Int = 0
                if (!TextUtils.isEmpty(record!!.nfcpaytime) && !"[]".equals(record!!.nfcpaytime)) {
                    NFCICSumCount = Integer.valueOf(record!!.nfcpaytime)
                }
                intent.putExtra("UserNb", record!!.usernb)
                intent.putExtra("OrderNb", record!!.ordernb)
                intent.putExtra("OrderMoney", record!!.amount)
                intent.putExtra("NFCICSumCount", NFCICSumCount)
                intent.putExtra("ICcardNo", record!!.iccardno)
                startActivity(intent)
            }
        }
    }

    protected fun Bluetooth() {
        Logger.i("Bluetooth")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    ACCESS_COARSE_LOCATION_REQUEST_CODE)
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION_REQUEST_CODE)
            } else {
                val intent = Intent(this, BluetoothActivity::class.java)
                var NFCICSumCount: Int = 0
                if (!TextUtils.isEmpty(record!!.nfcpaytime) && !"[]".equals(record!!.nfcpaytime)) {
                    NFCICSumCount = Integer.valueOf(record!!.nfcpaytime)
                }
                intent.putExtra("UserNb", record!!.usernb)
                intent.putExtra("OrderNb", record!!.ordernb)
                intent.putExtra("OrderMoney", record!!.amount)
                intent.putExtra("NFCICSumCount", NFCICSumCount)
                intent.putExtra("Type", shebeiType)
                when (shebeiType) {
                    1 -> intent.putExtra("OrderMoney", record!!.amount)
                    2 -> intent.putExtra("OrderMoney", record!!.mount)
                }
                intent.putExtra("ICcardNo", record!!.iccardno)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Logger.d("onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                if(blueType==1) {
                    Bluetooth()
                } else if(blueType==2) {
                    BluetoothToShebei()
                }
            } else {
                // Permission Denied
            }
        } else if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                if(blueType==1) {
                    Bluetooth()
                } else if(blueType==2) {
                    BluetoothToShebei()
                }
            } else {
                // Permission Denied
            }
        }
    }

    companion object {

        val RECORD = "Record"
    }

}
