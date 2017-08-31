package com.mqt.ganghuazhifu.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.PaymentResult
import com.mqt.ganghuazhifu.bean.ResponseHead
import com.mqt.ganghuazhifu.databinding.ActivityUnityPayResultBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.DataBaiduPush
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

/**
 * 联动优势支付——支付结果

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UnityPayResultActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var result: PaymentResult? = null
    private var Ordernb: String? = null
    private var activityUnityPayResultBinding: ActivityUnityPayResultBinding? = null

    var type = 1
    var shebeitype = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUnityPayResultBinding = DataBindingUtil.setContentView<ActivityUnityPayResultBinding>(this,
                R.layout.activity_unity_pay_result)
        Ordernb = intent.getStringExtra("Ordernb")

        initView()
        initData(Ordernb!!)
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.getScreenManager().popAllActivityJumpOverOneExceptOne(UnityPayResultActivity::class.java, MainActivity::class.java)
    }

    private fun initView() {
        setSupportActionBar(activityUnityPayResultBinding!!.toolbar)
        supportActionBar!!.title = "支付结果"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityUnityPayResultBinding!!.buttonGoHome.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.button_go_home -> ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity::class.java)
        }
    }

    fun initData(Ordernb: String) {
        val body = HttpRequestParams.getParamsForOrderConfig(Ordernb, "01")
        post(HttpURLS.payConfirmation, true, "OrderConfig", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseFields = response.getString("ResponseFields")
                val Response = response.getString("ResponseHead")
                if (Response != null) {
                    val head = JSONObject.parseObject(Response, ResponseHead::class.java)
                    if (head != null && head.ProcessCode == "0000") {
                        if (ResponseFields != null) {
                            result = JSONObject.parseObject(ResponseFields, PaymentResult::class.java)
                            if (result != null) {

                                if (result!!.Status == "PR00"
                                        && result!!.PayStatus == "PR06")
                                    when (result!!.NFCFlag) {
                                        "11" -> {
                                            shebeitype = 1
                                            activityUnityPayResultBinding!!.buttonGoNfc.visibility = View.VISIBLE
                                            activityUnityPayResultBinding!!.tvGoNfc.text = "NFC刷表"
                                            activityUnityPayResultBinding!!.buttonGoNfc.setOnClickListener { v -> NFC() }
                                            MaterialDialog.Builder(this@UnityPayResultActivity)
                                                    .title("提醒").content("支付成功，是否立即以NFC把金额刷到燃气表？")
                                                    .onPositive { dialog, which -> NFC() }
                                                    .positiveText("确定").negativeText("取消").show()
                                        }
                                        "12" -> {
                                            shebeitype = 1
                                            activityUnityPayResultBinding!!.buttonGoNfc.visibility = View.VISIBLE
                                            activityUnityPayResultBinding!!.tvGoNfc.text = "蓝牙写卡"
                                            activityUnityPayResultBinding!!.buttonGoNfc.setOnClickListener { v -> Bluetooth() }
                                            MaterialDialog.Builder(this@UnityPayResultActivity)
                                                    .title("提醒").content("支付成功，是否立即以蓝牙读卡器把金额写到CPU卡？")
                                                    .onPositive { dialog, which -> Bluetooth() }
                                                    .positiveText("确定").negativeText("取消").show()
                                        }
                                        "13" -> {
                                            activityUnityPayResultBinding!!.buttonGoNfc.visibility = View.VISIBLE
                                            activityUnityPayResultBinding!!.tvGoNfc.text = "蓝牙写表"
                                            activityUnityPayResultBinding!!.buttonGoNfc.setOnClickListener { v -> BluetoothToShebei() }
                                            MaterialDialog.Builder(this@UnityPayResultActivity)
                                                    .title("提醒").content("支付成功，是否立即以蓝牙金额刷到燃气表？")
                                                    .onPositive { dialog, which -> BluetoothToShebei() }
                                                    .positiveText("确定").negativeText("取消").show()
                                        }
                                        "14" -> {
                                            shebeitype = 2
                                            activityUnityPayResultBinding!!.buttonGoNfc.visibility = View.VISIBLE
                                            activityUnityPayResultBinding!!.tvGoNfc.text = "NFC写表"
                                            activityUnityPayResultBinding!!.buttonGoNfc.setOnClickListener { v -> NFC() }
                                            MaterialDialog.Builder(this@UnityPayResultActivity)
                                                    .title("提醒").content("支付成功，是否立即以NFC把气量刷到燃气表？")
                                                    .onPositive { dialog, which -> BluetoothToShebei() }
                                                    .positiveText("确定").negativeText("取消").show()
                                        }
                                        "15" -> {
                                            shebeitype = 2
                                            activityUnityPayResultBinding!!.buttonGoNfc.visibility = View.VISIBLE
                                            activityUnityPayResultBinding!!.tvGoNfc.text = "蓝牙写卡"
                                            activityUnityPayResultBinding!!.buttonGoNfc.setOnClickListener { v -> Bluetooth() }
                                            MaterialDialog.Builder(this@UnityPayResultActivity)
                                                    .title("提醒").content("支付成功，是否立即以蓝牙读卡器把气量写到CPU卡？")
                                                    .onPositive { dialog, which -> Bluetooth() }
                                                    .positiveText("确定").negativeText("取消").show()
                                        }
                                        else -> activityUnityPayResultBinding!!.buttonGoNfc.visibility = View.GONE
                                    }

                                // tv_explainer.setText(text);
                                activityUnityPayResultBinding!!.tvOrderNb.text = "订单号：" + result!!.OrderNb
                                when (result!!.Pmttp) {
                                    "010001" -> activityUnityPayResultBinding!!.tvCartegray.text = "业务类型：气费"
                                    "010002" -> activityUnityPayResultBinding!!.tvCartegray.text = "业务类型：营业费"
                                    "020001" -> activityUnityPayResultBinding!!.tvCartegray.text = "业务类型：水费"
                                }

                                if (result!!.UserNm == null) {
                                    result!!.UserNm = ""
                                    activityUnityPayResultBinding!!.buttonGoHome.visibility = View.VISIBLE
                                    activityUnityPayResultBinding!!.tvUnit.text = "收款单位：" + result!!.PayeeName
                                    activityUnityPayResultBinding!!.tvHuhao.text = "缴费户号：" + result!!.UserNb
                                    activityUnityPayResultBinding!!.tvName.text = "缴费户名：" + result!!.UserNm
                                    activityUnityPayResultBinding!!.tvAmount.text = "交易金额：" + result!!.Amount + "元"
                                } else {
                                    val nameBuilder = StringBuilder(result!!.UserNm)
                                    if (result!!.UserNm.length <= 1) {

                                    } else if (result!!.UserNm.length <= 3) {
                                        nameBuilder.setCharAt(0, '*')
                                    } else {
                                        for (i in 0..result!!.UserNm.length - 2 - 1) {
                                            nameBuilder.setCharAt(i, '*')
                                        }
                                    }

                                    activityUnityPayResultBinding!!.buttonGoHome.visibility = View.VISIBLE
                                    activityUnityPayResultBinding!!.tvUnit.text = "收款单位：" + result!!.PayeeName
                                    activityUnityPayResultBinding!!.tvHuhao.text = "缴费户号：" + result!!.UserNb
                                    activityUnityPayResultBinding!!.tvName.text = "缴费户名：" + nameBuilder.toString()
                                    activityUnityPayResultBinding!!.tvAmount.text = "交易金额：" + result!!.Amount + "元"
                                }

                                if (result!!.OrderSetTime != null) {
                                    result!!.OrderSetTime = result!!.OrderSetTime.replace("T", " ")
                                } else {
                                    result!!.OrderSetTime = ""
                                }

                                if (result!!.PaymentTime != null) {
                                    result!!.PaymentTime = result!!.PaymentTime.replace("T", " ")
                                } else {
                                    result!!.PaymentTime = ""
                                }
                                if (result!!.PayTime != null) {
                                    result!!.PayTime = result!!.PayTime.replace("T", " ")
                                } else {
                                    result!!.PayTime = ""
                                }

                                activityUnityPayResultBinding!!.tvOrderSetTime.text = "订单创建时间：" + result!!.OrderSetTime
                                activityUnityPayResultBinding!!.tvPayTime.text = "付款时间：" + result!!.PaymentTime
                                activityUnityPayResultBinding!!.tvJiaofeiTime.text = "缴费时间：" + result!!.PayTime

                                when (result!!.Status) {
                                    "PR00" -> {
                                        // 已付款
                                        activityUnityPayResultBinding!!.tvPayStatus.text = "交易状态：已付款"
                                        activityUnityPayResultBinding!!.tvExplainer.text = "恭喜你，支付成功！"
                                        activityUnityPayResultBinding!!.ivExplainer.setImageResource(R.drawable.pay_success)
                                    }
                                    "PR01" -> {
                                        // 待付款
                                        activityUnityPayResultBinding!!.tvPayStatus.text = "交易状态：待付款"
                                        activityUnityPayResultBinding!!.tvExplainer.text = "很遗憾，支付失败！"
                                        activityUnityPayResultBinding!!.ivExplainer.setImageResource(R.drawable.pay_fail)
                                    }
                                    "PR02" -> {
                                        // 已取消
                                        activityUnityPayResultBinding!!.tvPayStatus.text = "交易状态：已取消"
                                        activityUnityPayResultBinding!!.tvExplainer.text = "很遗憾，支付失败！"
                                        activityUnityPayResultBinding!!.ivExplainer.setImageResource(R.drawable.pay_fail)
                                    }
                                    "PR03" -> {
                                        // 支付失败
                                        activityUnityPayResultBinding!!.tvPayStatus.text = "交易状态：支付失败"
                                        activityUnityPayResultBinding!!.tvExplainer.text = "很遗憾，支付失败！"
                                        activityUnityPayResultBinding!!.ivExplainer.setImageResource(R.drawable.pay_fail)
                                    }
                                    "PR04" -> {
                                        // 待退款
                                        activityUnityPayResultBinding!!.tvPayStatus.text = "交易状态：待退款"
                                        activityUnityPayResultBinding!!.tvExplainer.text = "很遗憾，支付失败！"
                                        activityUnityPayResultBinding!!.ivExplainer.setImageResource(R.drawable.pay_fail)
                                    }
                                    "PR05" -> {
                                        // 已退款
                                        activityUnityPayResultBinding!!.tvPayStatus.text = "交易状态：已退款"
                                        activityUnityPayResultBinding!!.tvExplainer.text = "很遗憾，支付失败！"
                                        activityUnityPayResultBinding!!.ivExplainer.setImageResource(R.drawable.pay_fail)
                                    }
                                }

                                when (result!!.PayStatus) {
                                    "PR06" -> {

                                        if(result!!.PayeeName.equals("江苏宝华天然气有限公司")) {

                                            MaterialDialog.Builder(this@UnityPayResultActivity)
                                                    .title("提醒").content("请再次查询，查看有无欠费")
                                                    .onPositive { dialog, which ->
                                                        run {
                                                            val intent = Intent(this@UnityPayResultActivity, PayTheGasFeeActivity::class.java)
                                                            intent.putExtra("TYPE", 1)
                                                            DataBaiduPush.setPmttp("010001")
                                                            DataBaiduPush.setPmttpType("01")
                                                            startActivity(intent)
                                                            finish()
                                                        }
                                                    }
                                                    .positiveText("确定").negativeText("取消").show()
                                        }
                                        // 缴费成功
                                        activityUnityPayResultBinding!!.tvOrderStatus.text = "缴费状态：缴费成功"
                                    }
                                    "PR07" -> {
                                        // 缴费失败
                                        activityUnityPayResultBinding!!.tvOrderStatus.text = "缴费状态：缴费失败"
                                    }
                                    "PR08" -> {
                                        // 未缴费
                                        activityUnityPayResultBinding!!.tvOrderStatus.text = "缴费状态：未缴费"
                                    }
                                }

                                if (result!!.ErrorMsg != null) {
                                    activityUnityPayResultBinding!!.tvFailReason.text = "操作描述：" + result!!.ErrorMsg
                                } else {
                                    activityUnityPayResultBinding!!.tvFailReason.text = ""
                                }
                            }
                        }
                    } else {
                        if (head != null && head.ProcessDes != null) {
                            ToastUtil.Companion.toastError(head.ProcessDes)
                        }
                    }
                }
            }
        })
    }

    fun NFC() {
        val intent = Intent(this, ReadNFCActivity::class.java)
        intent.putExtra("UserNb", result!!.UserNb)
        intent.putExtra("OrderNb", result!!.OrderNb)
        intent.putExtra("Type", shebeitype)
        startActivity(intent)
    }

    internal var ACCESS_COARSE_LOCATION_REQUEST_CODE = 10
    internal var ACCESS_FINE_LOCATION_REQUEST_CODE = 11
    fun Bluetooth() {
        type = 1
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
                intent.putExtra("UserNb", result!!.UserNb)
                intent.putExtra("OrderNb", result!!.OrderNb)
                intent.putExtra("Type", shebeitype)
                when (shebeitype) {
                    1 -> intent.putExtra("OrderMoney", result!!.Amount)
                    2 -> intent.putExtra("OrderMoney", result!!.Mount)
                }
                intent.putExtra("NFCICSumCount", result!!.NFCICSumCount)
                intent.putExtra("ICcardNo", result!!.ICcardNo)
                startActivity(intent)
            }
        }
    }

    fun BluetoothToShebei() {
        type = 2
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
                intent.putExtra("UserNb", result!!.UserNb)
                intent.putExtra("OrderNb", result!!.OrderNb)
                intent.putExtra("OrderMoney", result!!.Amount)
                intent.putExtra("NFCICSumCount", result!!.NFCICSumCount)
                intent.putExtra("ICcardNo", result!!.ICcardNo)
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
                when (type) {
                    1 -> Bluetooth()
                    2 -> BluetoothToShebei()
                }
            } else {
                // Permission Denied
            }
        } else if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                when (type) {
                    1 -> Bluetooth()
                    2 -> BluetoothToShebei()
                }
            } else {
                // Permission Denied
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                //主界面左上角的icon点击反应
                ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity::class.java)
        }
        return true
    }

    companion object {
        fun startActivity(context: Context, Ordernb: String) {
            val intent = Intent(context, UnityPayResultActivity::class.java)
            intent.putExtra("Ordernb", Ordernb)
            context.startActivity(intent)
        }
    }

    private fun join(args: Array<String?>): String {
        val sb = StringBuffer()
        for (ss in args) {
            sb.append(ss)
        }
        return sb.toString()
    }

    private fun newStringArray(args0: Array<String?>, key: String): Array<String?> {
        val n = arrayOfNulls<String?>(args0.size + 1)
        for (i in args0.indices) {
            n[i] = args0[i]
        }
        n[args0.size] = key
        return n
    }

}
