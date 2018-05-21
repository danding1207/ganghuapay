package com.mqt.ganghuazhifu.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.http.SslError
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Parcelable
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.*
import com.mqt.ganghuazhifu.databinding.ActivityResultForGasFeeBinding
import com.mqt.ganghuazhifu.event.ConstantKotlin
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.TextValidation
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.math.BigDecimal

/**
 * 缴纳气费(营业费)欠费查询结果
 *
 * @author yang.lei
 * @date 2014-12-24
 *
 * 2018-02-06（yang.lei）：添加易通表具（金额表和气量表）充值方式（NFC 和蓝牙）的选择；整合优化各个界面传递订单类型的方式-枚举
 *
 *
 */
class ResultForGasFeeActivity : BaseActivity() {

    private var gasFeeResult: GasFeeResult? = null
    private var busiFeeResult: BusiFeeResult? = null
    private var amount: Float = 0.toFloat()
    private var isNFCOk: Boolean = false
    private var orderType: ConstantKotlin.OrderType = ConstantKotlin.OrderType.GASFEEARREARS
    private var StaffPhone: String? = null
    private var activityResultForGasFeeBinding: ActivityResultForGasFeeBinding? = null
    private var ytbType: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultForGasFeeBinding = DataBindingUtil.setContentView<ActivityResultForGasFeeBinding>(this,
                R.layout.activity_result_for_gas_fee)

        Logger.e("ConstantKotlin.OrderType.name:  " + intent.getStringExtra("OrderType"))
        orderType = ConstantKotlin.OrderType.valueOf(intent.getStringExtra("OrderType"))

        StaffPhone = intent.getStringExtra("StaffPhone")
        gasFeeResult = Parcels.unwrap<GasFeeResult>(intent.getParcelableExtra<Parcelable>("GasFeeResult"))
        busiFeeResult = Parcels.unwrap<BusiFeeResult>(intent.getParcelableExtra<Parcelable>("BusiFeeResult"))
        initView()

    }

    private fun setDatatoView() {
        when (orderType) {
            ConstantKotlin.OrderType.NFCGASFEEICPREDEPOSIT,
            ConstantKotlin.OrderType.GASFEEPREDEPOSIT,
            ConstantKotlin.OrderType.NFCGASFEEPREDEPOSIT,
            ConstantKotlin.OrderType.BLUETOOTHGASFEEPREDEPOSIT,
            ConstantKotlin.OrderType.GASFEEARREARS -> {
                when (gasFeeResult!!.PayeeCode) {
                    ConstantKotlin.NanJingCode -> {
                        activityResultForGasFeeBinding!!.llMoneyAll.visibility = View.GONE
                        activityResultForGasFeeBinding!!.llNanjingFeeCount.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.llNanjingQuerySeq.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.llPhonographyNumber.visibility = View.GONE
                        activityResultForGasFeeBinding!!.tvNanjingFeeCount.text = "账期数目：" + gasFeeResult!!.FeeCount
                        activityResultForGasFeeBinding!!.tvNanjingQuerySeq.text = "查询序列号：" + gasFeeResult!!.QuerySeq
                    }
                    ConstantKotlin.BaoHuaCode -> {
                        activityResultForGasFeeBinding!!.llMoneyAll.visibility = View.GONE
                        activityResultForGasFeeBinding!!.llNanjingFeeCount.visibility = View.GONE
                        activityResultForGasFeeBinding!!.llNanjingQuerySeq.visibility = View.GONE
                        activityResultForGasFeeBinding!!.llPhonographyNumber.visibility = View.GONE
                        activityResultForGasFeeBinding!!.tvPhonographyNumber.text = "速记号：" + if (gasFeeResult!!.EasyNo != null) gasFeeResult!!.EasyNo else ""
                    }
                    else -> {
                        activityResultForGasFeeBinding!!.llMoneyAll.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.llNanjingFeeCount.visibility = View.GONE
                        activityResultForGasFeeBinding!!.llNanjingQuerySeq.visibility = View.GONE
                        activityResultForGasFeeBinding!!.llPhonographyNumber.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.tvPhonographyNumber.text = "速记号：" + if (gasFeeResult!!.EasyNo != null) gasFeeResult!!.EasyNo else ""
                    }
                }
                activityResultForGasFeeBinding!!.tvAccountNumber.text = "户号：" + gasFeeResult!!.UserNb
                activityResultForGasFeeBinding!!.tvAccountName.text = "客户名：" + gasFeeResult!!.UserName
                activityResultForGasFeeBinding!!.tvAccountAddress.text = "地址：" + gasFeeResult!!.UserAddr
                activityResultForGasFeeBinding!!.tvOweAmount.text = "应收总额：" + gasFeeResult!!.AllGasfee + "元"
                activityResultForGasFeeBinding!!.switchButton.state = false
                activityResultForGasFeeBinding!!.switchButton.setOnSwitchButtonStateChangedListener {
                    isOpen ->
                    run {
                        if (isOpen) {
                            activityResultForGasFeeBinding!!.llFapiaoleixing.visibility = View.VISIBLE
                            activityResultForGasFeeBinding!!.llFapiaoneirong.visibility = View.VISIBLE
                            activityResultForGasFeeBinding!!.llFapiaotaitou.visibility = View.VISIBLE
                        } else {
                            activityResultForGasFeeBinding!!.llFapiaoleixing.visibility = View.GONE
                            activityResultForGasFeeBinding!!.llFapiaoneirong.visibility = View.GONE
                            activityResultForGasFeeBinding!!.llFapiaotaitou.visibility = View.GONE
                        }
                    }
                }
            }
            ConstantKotlin.OrderType.OPERATINGFEEARREARS -> {
                activityResultForGasFeeBinding!!.llMoneyAll.visibility = View.VISIBLE
                activityResultForGasFeeBinding!!.llNanjingFeeCount.visibility = View.GONE
                activityResultForGasFeeBinding!!.llNanjingQuerySeq.visibility = View.GONE
                activityResultForGasFeeBinding!!.llPhonographyNumber.visibility = View.VISIBLE
                activityResultForGasFeeBinding!!.tvAccountNumber.text = "户号：" + busiFeeResult!!.UserNb
                activityResultForGasFeeBinding!!.tvPhonographyNumber.text = "速记号：" + if (busiFeeResult!!.EasyNo != null) busiFeeResult!!.EasyNo else ""

                if ("11" == busiFeeResult!!.PreStore) {
                    activityResultForGasFeeBinding!!.llYuAmount.visibility = View.VISIBLE
                    activityResultForGasFeeBinding!!.tvYuAmount.text = "营业费预存余额：" + busiFeeResult!!.PRESAVING + "元"
                }

                activityResultForGasFeeBinding!!.tvAccountName.text = "客户名：" + busiFeeResult!!.UserName
                activityResultForGasFeeBinding!!.tvAccountAddress.text = "地址：" + busiFeeResult!!.UserAddr
                activityResultForGasFeeBinding!!.tvOweAmount.text = "营业费应收总额：" + busiFeeResult!!.AllBusifee + "元"
            }
        }
    }

    private fun initView() {
        setSupportActionBar(activityResultForGasFeeBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (orderType == ConstantKotlin.OrderType.GASFEEARREARS ||
                orderType == ConstantKotlin.OrderType.OPERATINGFEEARREARS) {
            activityResultForGasFeeBinding!!.tvTitleRight.visibility = View.VISIBLE
            activityResultForGasFeeBinding!!.tvTitleRight.setOnClickListener(this)
        }

        activityResultForGasFeeBinding!!.tvNfcLiucheng.setOnClickListener(this)
        activityResultForGasFeeBinding!!.tvBluetoothLiucheng.setOnClickListener(this)

        TextValidation.setRegularValidationNumberDecimal(this, activityResultForGasFeeBinding!!.etYucun)
        supportActionBar!!.title = "查询结果"
        activityResultForGasFeeBinding!!.buttonPayment.setOnClickListener(this)
        activityResultForGasFeeBinding!!.tvExplainer.text = "请在缴费前注意检查户号、客户名和地址"
        when (orderType) {
            ConstantKotlin.OrderType.GASFEEARREARS -> {
                when (gasFeeResult!!.PayeeCode) {
                    ConstantKotlin.NanJingCode -> {
                        activityResultForGasFeeBinding!!.tvButtonText.text = "缴费"
                        activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                    }
                    ConstantKotlin.BaoHuaCode -> {
                        activityResultForGasFeeBinding!!.tvButtonText.text = "缴费"
                        activityResultForGasFeeBinding!!.tvMoneyAll.visibility = View.GONE
                        activityResultForGasFeeBinding!!.etYucun.visibility = View.GONE
                    }
                    else -> {
                        activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.tvButtonText.text = "缴费/预存"
                    }
                }
                if (gasFeeResult != null) setDatatoView()
            }
            ConstantKotlin.OrderType.GASFEEPREDEPOSIT -> {
                activityResultForGasFeeBinding!!.tvButtonText.text = "预存"
                activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE

                initNFC()
                activityResultForGasFeeBinding!!.llYitongbiaoType.visibility = View.VISIBLE
                activityResultForGasFeeBinding!!.llCheckBoxNfc.setOnClickListener {
                    ytbType = 1
                    activityResultForGasFeeBinding!!.checkBoxNfc.isChecked = true
                    activityResultForGasFeeBinding!!.checkBoxBluetooth.isChecked = false
                }
                activityResultForGasFeeBinding!!.llCheckBoxBluetooth.setOnClickListener {
                    ytbType = 2
                    activityResultForGasFeeBinding!!.checkBoxNfc.isChecked = false
                    activityResultForGasFeeBinding!!.checkBoxBluetooth.isChecked = true
                }
                if (!TextUtils.isEmpty(gasFeeResult!!.SecurAlert)) {
                    MaterialDialog.Builder(this)
                            .title("提示")
                            .content(gasFeeResult!!.SecurAlert)
                            .positiveText("确定")
                            .show()
                }
                activityResultForGasFeeBinding!!.etYucun.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                    (start..end - 1)
                            .filter { !Character.isDigit(source[it]) && Character.toString(source[it]) != "." }
                            .forEach { return@InputFilter "" }
                    null
                })
                activityResultForGasFeeBinding!!.etYucun.addTextChangedListener(object : TextWatcher {
                    private var beforeText: String? = null
                    private var last = 2
                    private var isDecimal = false
                    private var hasDecimalPoint = false
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        Logger.i("onTextChanged--->" + s.toString())
                        Logger.i("start--->" + start)
                        Logger.i("before--->" + before)
                        // 以小数点开头 自动显示 0.
                        if (before == 0 && TextUtils.isEmpty(beforeText) && s.toString() == ".") {
                            activityResultForGasFeeBinding!!.etYucun.setText("0.")
                            isDecimal = true
                            hasDecimalPoint = true
                            val text = activityResultForGasFeeBinding!!.etYucun.text
                            if (text != null) {
                                Selection.setSelection(text, text.length)
                            }
                        }

                        // 以0开头 再按0 不会变；按.
                        if (before == 0 && TextUtils.isEmpty(beforeText) && s.toString() == "0") {
                            isDecimal = true
                        }

                        // 第二位
                        if (before == 0 && start == 1) {
                            if (!beforeText!!.endsWith(".") && s.toString().endsWith(".")) {
                                hasDecimalPoint = true
                            } else {
                                if (isDecimal) {
                                    val ss = s.toString().substring(1, s.toString().length)
                                    if (ss != "0") {
                                        isDecimal = false
                                    }
                                    activityResultForGasFeeBinding!!.etYucun.setText(ss)
                                    val text = activityResultForGasFeeBinding!!.etYucun.text
                                    if (text != null) {
                                        Selection.setSelection(text, text.length)
                                    }
                                }
                            }
                        }
                        // 输入了 小数点 开始计数2位
                        if (before == 0 && !beforeText!!.endsWith(".") && s.toString().endsWith(".")) {
                            hasDecimalPoint = true
                        }
                        Logger.i("last--->" + last)
                        // 有小数点 输入1位 计数-1
                        if (hasDecimalPoint && before == 0) {
                            if (last > 0) {
                                last--
                            } else {
                                activityResultForGasFeeBinding!!.etYucun.setText(beforeText)
                                val text = activityResultForGasFeeBinding!!.etYucun.text
                                if (text != null) {
                                    Selection.setSelection(text, text.length)
                                }
                            }
                        }
                        if (hasDecimalPoint && before == 1) {
                            if (beforeText!!.endsWith(".") && !s.toString().endsWith(".")) {
                                // 删除1位 并且 删除小数点 计数设为2
                                hasDecimalPoint = false
                                last = 2
                            } else {
                                // 删除1位 并且 有小数点 计数+1
                                last++
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        Logger.i("beforeTextChanged--->" + s.toString())
                        beforeText = s.toString()
                    }

                    override fun afterTextChanged(s: Editable) {
                        Logger.i("afterTextChanged--->" + s.toString())
                        if (gasFeeResult!!.LimitGasfee != 0f) {
                            if (!TextUtils.isEmpty(s.toString()) && s.toString() != "." &&
                                    s.toString().toFloat() > gasFeeResult!!.LimitGasfee - gasFeeResult!!.NFCNotWriteGas) {
                                if (gasFeeResult!!.NFCNotWriteGas > 0)
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" +
                                            gasFeeResult!!.LimitGasfee + "，未写卡金额" + gasFeeResult!!.NFCNotWriteGas +
                                            "，再次预存最大金额" + (gasFeeResult!!.LimitGasfee - gasFeeResult!!.NFCNotWriteGas)
                                else
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" +
                                            gasFeeResult!!.LimitGasfee
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.VISIBLE
                            } else {
                                activityResultForGasFeeBinding!!.tvLimit.text = ""
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.GONE
                            }
                        }
                    }
                })
                if (gasFeeResult != null) setDatatoView()

            }

            ConstantKotlin.OrderType.NFCGASFEEPREDEPOSIT -> {
                activityResultForGasFeeBinding!!.tvButtonText.text = "NFC预存"
                activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE

                initNFC()
                activityResultForGasFeeBinding!!.llYitongbiaoType.visibility = View.GONE

                if (!TextUtils.isEmpty(gasFeeResult!!.SecurAlert)) {
                    MaterialDialog.Builder(this)
                            .title("提示")
                            .content(gasFeeResult!!.SecurAlert)
                            .positiveText("确定")
                            .show()
                }
                activityResultForGasFeeBinding!!.etYucun.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                    (start..end - 1)
                            .filter { !Character.isDigit(source[it]) && Character.toString(source[it]) != "." }
                            .forEach { return@InputFilter "" }
                    null
                })
                activityResultForGasFeeBinding!!.etYucun.addTextChangedListener(object : TextWatcher {
                    private var beforeText: String? = null
                    private var last = 2
                    private var isDecimal = false
                    private var hasDecimalPoint = false
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        Logger.i("onTextChanged--->" + s.toString())
                        Logger.i("start--->" + start)
                        Logger.i("before--->" + before)
                        // 以小数点开头 自动显示 0.
                        if (before == 0 && TextUtils.isEmpty(beforeText) && s.toString() == ".") {
                            activityResultForGasFeeBinding!!.etYucun.setText("0.")
                            isDecimal = true
                            hasDecimalPoint = true
                            val text = activityResultForGasFeeBinding!!.etYucun.text
                            if (text != null) {
                                Selection.setSelection(text, text.length)
                            }
                        }

                        // 以0开头 再按0 不会变；按.
                        if (before == 0 && TextUtils.isEmpty(beforeText) && s.toString() == "0") {
                            isDecimal = true
                        }

                        // 第二位
                        if (before == 0 && start == 1) {
                            if (!beforeText!!.endsWith(".") && s.toString().endsWith(".")) {
                                hasDecimalPoint = true
                            } else {
                                if (isDecimal) {
                                    val ss = s.toString().substring(1, s.toString().length)
                                    if (ss != "0") {
                                        isDecimal = false
                                    }
                                    activityResultForGasFeeBinding!!.etYucun.setText(ss)
                                    val text = activityResultForGasFeeBinding!!.etYucun.text
                                    if (text != null) {
                                        Selection.setSelection(text, text.length)
                                    }
                                }
                            }
                        }
                        // 输入了 小数点 开始计数2位
                        if (before == 0 && !beforeText!!.endsWith(".") && s.toString().endsWith(".")) {
                            hasDecimalPoint = true
                        }
                        Logger.i("last--->" + last)
                        // 有小数点 输入1位 计数-1
                        if (hasDecimalPoint && before == 0) {
                            if (last > 0) {
                                last--
                            } else {
                                activityResultForGasFeeBinding!!.etYucun.setText(beforeText)
                                val text = activityResultForGasFeeBinding!!.etYucun.text
                                if (text != null) {
                                    Selection.setSelection(text, text.length)
                                }
                            }
                        }
                        if (hasDecimalPoint && before == 1) {
                            if (beforeText!!.endsWith(".") && !s.toString().endsWith(".")) {
                                // 删除1位 并且 删除小数点 计数设为2
                                hasDecimalPoint = false
                                last = 2
                            } else {
                                // 删除1位 并且 有小数点 计数+1
                                last++
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        Logger.i("beforeTextChanged--->" + s.toString())
                        beforeText = s.toString()
                    }

                    override fun afterTextChanged(s: Editable) {
                        Logger.i("afterTextChanged--->" + s.toString())
                        if (gasFeeResult!!.LimitGasfee != 0f) {
                            if (!TextUtils.isEmpty(s.toString()) && s.toString() != "." &&
                                    s.toString().toFloat() > gasFeeResult!!.LimitGasfee - gasFeeResult!!.NFCNotWriteGas) {
                                if (gasFeeResult!!.NFCNotWriteGas > 0)
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" +
                                            gasFeeResult!!.LimitGasfee + "，未写卡金额" + gasFeeResult!!.NFCNotWriteGas +
                                            "，再次预存最大金额" + (gasFeeResult!!.LimitGasfee - gasFeeResult!!.NFCNotWriteGas)
                                else
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" +
                                            gasFeeResult!!.LimitGasfee
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.VISIBLE
                            } else {
                                activityResultForGasFeeBinding!!.tvLimit.text = ""
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.GONE
                            }
                        }
                    }
                })
                if (gasFeeResult != null) setDatatoView()

            }
            ConstantKotlin.OrderType.BLUETOOTHGASFEEPREDEPOSIT -> {
                activityResultForGasFeeBinding!!.tvButtonText.text = "预存"
                activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                if (!TextUtils.isEmpty(gasFeeResult!!.SecurAlert)) {
                    MaterialDialog.Builder(this)
                            .title("提示")
                            .content(gasFeeResult!!.SecurAlert)
                            .positiveText("确定")
                            .show()
                }
                activityResultForGasFeeBinding!!.etYucun.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                    (start..end - 1)
                            .filter { !Character.isDigit(source[it]) && Character.toString(source[it]) != "." }
                            .forEach { return@InputFilter "" }
                    null
                })
                activityResultForGasFeeBinding!!.etYucun.addTextChangedListener(object : TextWatcher {
                    private var beforeText: String? = null
                    private var last = 2
                    private var isDecimal = false
                    private var hasDecimalPoint = false
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        Logger.i("onTextChanged--->" + s.toString())
                        Logger.i("start--->" + start)
                        Logger.i("before--->" + before)

                        // 以小数点开头 自动显示 0.
                        if (before == 0 && TextUtils.isEmpty(beforeText) && s.toString() == ".") {
                            activityResultForGasFeeBinding!!.etYucun.setText("0.")
                            isDecimal = true
                            hasDecimalPoint = true
                            val text = activityResultForGasFeeBinding!!.etYucun.text
                            if (text != null) {
                                Selection.setSelection(text, text.length)
                            }
                        }

                        // 以0开头 再按0 不会变；按.
                        if (before == 0 && TextUtils.isEmpty(beforeText) && s.toString() == "0") {
                            isDecimal = true
                        }

                        // 第二位
                        if (before == 0 && start == 1) {
                            if (!beforeText!!.endsWith(".") && s.toString().endsWith(".")) {
                                hasDecimalPoint = true
                            } else {
                                if (isDecimal) {
                                    val ss = s.toString().substring(1, s.toString().length)
                                    if (ss != "0") {
                                        isDecimal = false
                                    }
                                    activityResultForGasFeeBinding!!.etYucun.setText(ss)
                                    val text = activityResultForGasFeeBinding!!.etYucun.text
                                    if (text != null) {
                                        Selection.setSelection(text, text.length)
                                    }
                                }
                            }
                        }
                        // 输入了 小数点 开始计数2位
                        if (before == 0 && !beforeText!!.endsWith(".") && s.toString().endsWith(".")) {
                            hasDecimalPoint = true
                        }
                        Logger.i("last--->" + last)
                        // 有小数点 输入1位 计数-1
                        if (hasDecimalPoint && before == 0) {
                            if (last > 0) {
                                last--
                            } else {
                                activityResultForGasFeeBinding!!.etYucun.setText(beforeText)
                                val text = activityResultForGasFeeBinding!!.etYucun.text
                                if (text != null) {
                                    Selection.setSelection(text, text.length)
                                }
                            }
                        }
                        if (hasDecimalPoint && before == 1) {
                            if (beforeText!!.endsWith(".") && !s.toString().endsWith(".")) {
                                // 删除1位 并且 删除小数点 计数设为2
                                hasDecimalPoint = false
                                last = 2
                            } else {
                                // 删除1位 并且 有小数点 计数+1
                                last++
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        Logger.i("beforeTextChanged--->" + s.toString())
                        beforeText = s.toString()
                    }

                    override fun afterTextChanged(s: Editable) {
                        Logger.i("afterTextChanged--->" + s.toString())
                        if (gasFeeResult!!.LimitGasfee != 0f) {
                            if (!TextUtils.isEmpty(s.toString()) && s.toString() != "." &&
                                    s.toString().toFloat() > gasFeeResult!!.LimitGasfee - gasFeeResult!!.NFCNotWriteGas) {
                                if (gasFeeResult!!.NFCNotWriteGas > 0)
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" + gasFeeResult!!.LimitGasfee +
                                            "，未写卡金额" + gasFeeResult!!.NFCNotWriteGas + "，再次预存最大金额" +
                                            (gasFeeResult!!.LimitGasfee - gasFeeResult!!.NFCNotWriteGas)
                                else
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" + gasFeeResult!!.LimitGasfee
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.VISIBLE
                            } else {
                                activityResultForGasFeeBinding!!.tvLimit.text = ""
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.GONE
                            }
                        }
                    }
                })
                if (gasFeeResult != null) setDatatoView()
            }
            ConstantKotlin.OrderType.NFCGASFEEICPREDEPOSIT -> {
                activityResultForGasFeeBinding!!.tvButtonText.text = "预存"
                activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                activityResultForGasFeeBinding!!.tvMoneyAll.text = "预存总气量：m³"

                initNFC()
                activityResultForGasFeeBinding!!.llYitongbiaoType.visibility = View.VISIBLE
                activityResultForGasFeeBinding!!.llCheckBoxNfc.setOnClickListener {
                    ytbType = 1
                    activityResultForGasFeeBinding!!.checkBoxNfc.isChecked = true
                    activityResultForGasFeeBinding!!.checkBoxBluetooth.isChecked = false
                }
                activityResultForGasFeeBinding!!.llCheckBoxBluetooth.setOnClickListener {
                    ytbType = 2
                    activityResultForGasFeeBinding!!.checkBoxNfc.isChecked = false
                    activityResultForGasFeeBinding!!.checkBoxBluetooth.isChecked = true
                }
                if (!TextUtils.isEmpty(gasFeeResult!!.SecurAlert)) {
                    MaterialDialog.Builder(this)
                            .title("提示")
                            .content(gasFeeResult!!.SecurAlert)
                            .positiveText("确定")
                            .show()
                }
                activityResultForGasFeeBinding!!.tvMoneyAll.text = "预存气量(m³) :"
                activityResultForGasFeeBinding!!.etYucun.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                    (start..end - 1)
                            .filter { !Character.isDigit(source[it]) }
                            .forEach { return@InputFilter "" }
                    null
                })
                activityResultForGasFeeBinding!!.etYucun.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        Logger.i("afterTextChanged--->" + s.toString())
                        if (gasFeeResult!!.UserCanAmount != 0f) {
                            if (!TextUtils.isEmpty(s.toString()) &&
                                    s.toString().toFloat() > gasFeeResult!!.UserCanAmount) {
                                activityResultForGasFeeBinding!!.tvLimit.text = "您的本次可购气量为:" + gasFeeResult!!.LimitGasfee + "m³"
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.VISIBLE
                            } else {
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.GONE
                            }
                        }
                    }
                })
                if (gasFeeResult != null) setDatatoView()

            }
            ConstantKotlin.OrderType.OPERATINGFEEARREARS -> {
                if ("11" == busiFeeResult!!.PreStore) {
                    if (busiFeeResult!!.PRESAVING.isNotEmpty() &&
                            busiFeeResult!!.PRESAVING.toDouble() > 0 || (busiFeeResult!!.AllBusifee.isNotEmpty() &&
                            busiFeeResult!!.AllBusifee.toDouble() == 0.00)) {
                        activityResultForGasFeeBinding!!.tvButtonText.text = "预存"
                        activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.tvMoneyAll.text = "实缴总金额：￥"
                    } else {
                        activityResultForGasFeeBinding!!.llYucunTip.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.tvButtonText.text = "缴费"
                        activityResultForGasFeeBinding!!.etYucun.visibility = View.INVISIBLE
                        activityResultForGasFeeBinding!!.tvMoneyAll.text = "实缴总金额：￥" + busiFeeResult!!.AllBusifee + "元"
                    }
                } else {
                    activityResultForGasFeeBinding!!.tvButtonText.text = "缴费"
                    activityResultForGasFeeBinding!!.etYucun.visibility = View.INVISIBLE
                    activityResultForGasFeeBinding!!.tvMoneyAll.text = "实缴总金额：￥" + busiFeeResult!!.AllBusifee + "元"
                }
                if (busiFeeResult != null) setDatatoView()
            }
        }

        val user = EncryptedPreferencesUtils.getUser()
        if (user.GeneralContactCount == 0) {
            MaterialDialog.Builder(this)
                    .title("提醒")
                    .content("是否将该户号添加为常用联系人？")
                    .onPositive { dialog, which -> addGeneralContact(user) }
                    .positiveText("确定")
                    .negativeText("取消")
                    .show()
        }
    }

    private fun addGeneralContact(user: User) {
        val body = HttpRequestParams.getParamsForAddGeneralContact(user.Uid, gasFeeResult!!.PayeeCode, gasFeeResult!!.UserNb, gasFeeResult!!.UserName, "1", "")
        post(HttpURLS.addGeneralContact, true, "AddGeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    EncryptedPreferencesUtils.setGeneralContactCount(1)
                    ToastUtil.toastSuccess("绑定成功!")
                } else {
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_title_right -> {
                val intent = Intent(this@ResultForGasFeeActivity, ResultForGasFeeDetailsActivity::class.java)
                intent.putExtra("OrderType", orderType.name)
                intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(gasFeeResult))
                intent.putExtra("BusiFeeResult", Parcels.wrap<BusiFeeResult>(busiFeeResult))
                startActivity(intent)
            }
            R.id.button_payment -> {
                when (orderType) {
                    ConstantKotlin.OrderType.NFCGASFEEICPREDEPOSIT -> {
                        val style = SpannableStringBuilder()
                        when (ytbType) {
                            1 -> {
                                if (!isNFCOk) {
                                    MaterialDialog.Builder(this)
                                            .title("提醒")
                                            .content("手机不支持NFC功能，请更换手机！")
                                            .positiveText("确定")
                                            .show()
                                    return
                                }
                                //设置文字
                                style.append("您是否确定选择 NFC 充值方式，请确保您的手机支持 NFC 功能！查看流程")

                                //设置部分文字点击事件
                                val clickableSpan = object : ClickableSpan() {
                                    override fun onClick(widget: View) {

                                        val intent = Intent(this@ResultForGasFeeActivity,
                                                NFCLiuChengActivity::class.java)
                                        intent.putExtra("TYPE", 1)
                                        startActivity(intent)

                                    }
                                }
                                style.setSpan(clickableSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                //设置部分文字颜色
                                val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#0000FF"))
                                style.setSpan(foregroundColorSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                            2 -> {
                                //设置文字
                                style.append("您是否确定选择蓝牙充值方式，请确保您附近有具备充值能力的蓝牙读卡器！查看流程")
                                //设置部分文字点击事件
                                val clickableSpan = object : ClickableSpan() {
                                    override fun onClick(widget: View) {

                                        val intent = Intent(this@ResultForGasFeeActivity,
                                                NFCLiuChengActivity::class.java)
                                        intent.putExtra("TYPE", 2)
                                        startActivity(intent)

                                    }
                                }
                                style.setSpan(clickableSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                //设置部分文字颜色
                                val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#0000FF"))
                                style.setSpan(foregroundColorSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                        MaterialDialog.Builder(this)
                                .title("提醒")
                                .cancelable(false)
                                .canceledOnTouchOutside(false)
                                .onPositive { dialog, which ->
                                    icChange()
                                }
                                .positiveText("确定")
                                .negativeText("取消")
                                .content(style)
                                .show()
                    }
                    ConstantKotlin.OrderType.GASFEEPREDEPOSIT -> {
                        val style = SpannableStringBuilder()
                        when (ytbType) {
                            1 -> {
                                if (!isNFCOk) {
                                    MaterialDialog.Builder(this)
                                            .title("提醒")
                                            .content("手机不支持NFC功能，请更换手机！")
                                            .positiveText("确定")
                                            .show()
                                    return
                                }

                                //设置文字
                                style.append("您是否确定选择 NFC 充值方式，请确保您的手机支持 NFC 功能！查看流程")

                                //设置部分文字点击事件
                                val clickableSpan = object : ClickableSpan() {
                                    override fun onClick(widget: View) {

                                        val intent = Intent(this@ResultForGasFeeActivity,
                                                NFCLiuChengActivity::class.java)
                                        intent.putExtra("TYPE", 1)
                                        startActivity(intent)

                                    }
                                }
                                style.setSpan(clickableSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                //设置部分文字颜色
                                val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#0000FF"))
                                style.setSpan(foregroundColorSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                            2 -> {
                                //设置文字
                                style.append("您是否确定选择蓝牙充值方式，请确保您附近有具备充值能力的蓝牙读卡器！查看流程")
                                //设置部分文字点击事件
                                val clickableSpan = object : ClickableSpan() {
                                    override fun onClick(widget: View) {

                                        val intent = Intent(this@ResultForGasFeeActivity,
                                                NFCLiuChengActivity::class.java)
                                        intent.putExtra("TYPE", 2)
                                        startActivity(intent)

                                    }
                                }
                                style.setSpan(clickableSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                //设置部分文字颜色
                                val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#0000FF"))
                                style.setSpan(foregroundColorSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }

                        MaterialDialog.Builder(this)
                                .title("提醒")
                                .cancelable(false)
                                .canceledOnTouchOutside(false)
                                .onPositive { dialog, which ->
                                    makeOrder()
                                }
                                .positiveText("确定")
                                .negativeText("取消")
                                .content(style)
                                .show()
                    }
                    ConstantKotlin.OrderType.NFCGASFEEPREDEPOSIT -> {
                        val style = SpannableStringBuilder()

                        if (!isNFCOk) {
                            MaterialDialog.Builder(this)
                                    .title("提醒")
                                    .content("手机不支持NFC功能，请更换手机！")
                                    .positiveText("确定")
                                    .show()
                            return
                        }

                        //设置文字
                        style.append("您是否确定选择 NFC 充值方式，请确保您的手机支持 NFC 功能！查看流程")

                        //设置部分文字点击事件
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(widget: View) {

                                val intent = Intent(this@ResultForGasFeeActivity,
                                        NFCLiuChengActivity::class.java)
                                intent.putExtra("TYPE", 1)
                                startActivity(intent)

                            }
                        }
                        style.setSpan(clickableSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        //设置部分文字颜色
                        val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#0000FF"))
                        style.setSpan(foregroundColorSpan, 34, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


                        MaterialDialog.Builder(this)
                                .title("提醒")
                                .cancelable(false)
                                .canceledOnTouchOutside(false)
                                .onPositive { dialog, which ->
                                    makeOrder()
                                }
                                .positiveText("确定")
                                .negativeText("取消")
                                .content(style)
                                .show()
                    }
                    else -> {
                        makeOrder()
                    }
                }
            }
        }
    }

    fun icChange() {
        if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text.toString()) && activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat() > 0) {
            val body = HttpRequestParams.getParamsForGasICPayMesQuery(gasFeeResult!!.UserNb,
                    gasFeeResult!!.IcCardno, activityResultForGasFeeBinding!!.etYucun.text.toString(),
                    gasFeeResult!!.PayeeCode, "1", gasFeeResult!!.AllGasfee, gasFeeResult!!.QueryId)
            post(HttpURLS.gasICPayMesQuery, true, "ICPayMesQuery", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.i(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {

                        var icChangeResult: IcChangeResult = Gson().fromJson(ResponseFields.toJSONString(), IcChangeResult::class.java)

                        if (icChangeResult != null) {
                            Logger.i(icChangeResult.toString())

                            if (!TextUtils.isEmpty(gasFeeResult!!.AllGasfee) && gasFeeResult!!.AllGasfee.toFloat() > 0) {
                                MaterialDialog.Builder(this)
                                        .title("提醒")
                                        .cancelable(false)
                                        .canceledOnTouchOutside(false)
                                        .onPositive { dialog, which ->
                                            makeOrderIC(gasFeeResult!!.AllGasfee.toFloat())
                                        }
                                        .positiveText("确定")
                                        .negativeText("取消")
                                        .content("您是否确定，缴纳欠费：" + gasFeeResult!!.AllGasfee + "元 并预存气量：" + activityResultForGasFeeBinding!!.etYucun.text.toString()
                                                + "m³， 对应金额为：" + icChangeResult.Amount + "元。合计：" + (icChangeResult.Amount.toFloat() + gasFeeResult!!.AllGasfee.toFloat()) + "元")
                                        .show()
                            } else {
                                MaterialDialog.Builder(this)
                                        .title("提醒")
                                        .cancelable(false)
                                        .canceledOnTouchOutside(false)
                                        .onPositive { dialog, which ->
                                            makeOrderIC(icChangeResult.Amount.toFloat())
                                        }
                                        .positiveText("确定")
                                        .negativeText("取消")
                                        .content("您是否确定，预存气量：" + activityResultForGasFeeBinding!!.etYucun.text.toString()
                                                + "m³， 对应金额为：" + icChangeResult.Amount + "元")
                                        .show()
                            }
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        } else {
            if (!TextUtils.isEmpty(gasFeeResult!!.AllGasfee) && gasFeeResult!!.AllGasfee.toFloat() > 0) {
                MaterialDialog.Builder(this)
                        .title("提醒")
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .onPositive { dialog, which ->
                            makeOrderIC(gasFeeResult!!.AllGasfee.toFloat())
                        }
                        .positiveText("确定")
                        .negativeText("取消")
                        .content("您未输入预存气量，是否缴纳欠费：" + gasFeeResult!!.AllGasfee + "元")
                        .show()
            }
        }
    }

    fun makeOrderIC(amount: Float) {
        if (amount > 0) {
            val Pmttp: String = "010001"
            val UserName: String = gasFeeResult!!.UserName
            val CityCode: String = gasFeeResult!!.CityCode
            val ProvinceCode: String = gasFeeResult!!.ProvinceCode
            val PayeeCode: String = gasFeeResult!!.PayeeCode
            val UserAddr: String = gasFeeResult!!.UserAddr
            val FeeCount: String? = null
            val QuerySeq: String? = null
            val DueMonth: String? = null
            var NFCFlag: String? = null
            val PrestoreFlag: String? = null
            val StaffPhone: String? = null
            val UserNb: String? = gasFeeResult!!.UserNb
            val NFCICSumCount: String? = if (gasFeeResult!!.NfcSumcount == null) "0" else gasFeeResult!!.NfcSumcount
            val ICcardNo: String = gasFeeResult!!.IcCardno
            val QueryId: String = gasFeeResult!!.QueryId

            when (ytbType) {
                1 -> NFCFlag = "14"
                2 -> NFCFlag = "15"
            }

            val user = EncryptedPreferencesUtils.getUser()
            val decimal = BigDecimal(amount.toDouble())
            val body = HttpRequestParams.getParamsForOrderSubmit(Pmttp,
                    decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), UserNb, UserName, UserAddr, CityCode,
                    ProvinceCode, PayeeCode, user.LoginAccount, FeeCount, QuerySeq, DueMonth, NFCFlag, PrestoreFlag,
                    StaffPhone, NFCICSumCount, ICcardNo, QueryId,
                    activityResultForGasFeeBinding!!.etYucun.text.toString())
            post(HttpURLS.orderSubmit, true, "OrderSubmit", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.i(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        val OrderNb = ResponseFields.getString("OrderNb")
                        val OrderSetTime = ResponseFields.getString("OrderSetTime")
                        val PayeeNm = ResponseFields.getString("PayeeNm")
                        val order = Order()
                        order.OrderNb = OrderNb
                        order.OrderSetTime = OrderSetTime
                        order.ProductName = PayeeNm
                        order.OrderAmount = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                        order.usernb = UserNb
                        Logger.i(order.toString())
                        ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity::class.java)
                        val intent1 = Intent(this@ResultForGasFeeActivity, PayActivity::class.java)
                        intent1.putExtra("Order", Parcels.wrap(order))
                        startActivity(intent1)
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        } else {
            if (gasFeeResult != null &&
                    (gasFeeResult!!.PayeeCode == ConstantKotlin.NanJingCode
                            || gasFeeResult!!.PayeeCode == ConstantKotlin.BaoHuaCode)) {
                if (amount == 0f) {
                    ToastUtil.toastInfo("没有欠费账单!")
                }
            } else {
                ToastUtil.toastWarning("请填写缴费/预存金额！")
            }
        }
    }

    fun makeOrder() {
        if (gasFeeResult != null) {
            Logger.i("PayeeCode: " + gasFeeResult!!.PayeeCode)
        } else if (busiFeeResult != null) {
            Logger.i("PayeeCode: " + busiFeeResult!!.PayeeCode)
        }

        var Pmttp: String? = null
        var UserName: String? = null
        var CityCode: String? = null
        var ProvinceCode: String? = null
        var PayeeCode: String? = null
        var UserAddr: String? = null
        var FeeCount: String? = null
        var QuerySeq: String? = null
        var DueMonth: String? = null
        var NFCFlag: String? = null
        var PrestoreFlag: String? = null
        var StaffPhone: String? = null
        var NFCICSumCount: String? = null
        var ICcardNo: String? = null
        var QueryId: String? = null
        var UserNb: String? = null

        when (orderType) {
            ConstantKotlin.OrderType.GASFEEARREARS -> {
                if (gasFeeResult != null && (gasFeeResult!!.PayeeCode == ConstantKotlin.NanJingCode ||
                        gasFeeResult!!.PayeeCode == ConstantKotlin.BaoHuaCode)) {
                    if (!TextUtils.isEmpty(gasFeeResult!!.AllGasfee)) {
                        amount = gasFeeResult!!.AllGasfee.toFloat()
                    } else {
                        amount = 0f
                    }
                } else {
                    if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text)) {
                        amount = activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat()
                    } else {
                        amount = 0f
                    }
                }
                if (amount <= 0) {
                    ToastUtil.toastWarning("请输入预存金额！")
                    return
                } else if (amount < gasFeeResult!!.AllGasfee.toFloat()) {
                    ToastUtil.toastWarning("实缴总金额不能小于应收总额！")
                    return
                }

                Pmttp = "010001"
                UserNb = gasFeeResult!!.UserNb
                UserName = gasFeeResult!!.UserName
                CityCode = gasFeeResult!!.CityCode
                ProvinceCode = gasFeeResult!!.ProvinceCode
                PayeeCode = gasFeeResult!!.PayeeCode
                UserAddr = gasFeeResult!!.UserAddr
                FeeCount = gasFeeResult!!.FeeCount
                QuerySeq = gasFeeResult!!.QuerySeq
                DueMonth = gasFeeResult!!.DueMonth
                QueryId = gasFeeResult!!.QueryId
            }

            ConstantKotlin.OrderType.OPERATINGFEEARREARS -> {
                if ("11" == busiFeeResult!!.PreStore) {
                    if (busiFeeResult!!.PRESAVING.isNotEmpty() &&
                            busiFeeResult!!.PRESAVING.toDouble() > 0 || (busiFeeResult!!.AllBusifee.isNotEmpty() &&
                            busiFeeResult!!.AllBusifee.toDouble() == 0.00)) {
                        if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text)) {
                            amount = activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat()
                        } else {
                            amount = 0f
                        }
                        if (amount <= 0) {
                            ToastUtil.toastWarning("请输入预存金额！")
                            return
                        } else if (amount < (busiFeeResult!!.AllBusifee.toFloat() - busiFeeResult!!.PRESAVING.toFloat())) {
                            ToastUtil.toastWarningLong("下单金额不能小于预存余额和欠费的差额！")
                            return
                        }
                    } else {
                        amount = busiFeeResult!!.AllBusifee.toFloat()
                    }
                } else {
                    amount = busiFeeResult!!.AllBusifee.toFloat()
                }

                UserNb = busiFeeResult!!.UserNb
                UserName = busiFeeResult!!.UserName
                CityCode = busiFeeResult!!.CityCode
                ProvinceCode = busiFeeResult!!.ProvinceCode
                PayeeCode = busiFeeResult!!.PayeeCode
                UserAddr = busiFeeResult!!.UserAddr
                QueryId = busiFeeResult!!.QueryId
                Pmttp = "010002"
                if ("11" == busiFeeResult!!.PreStore) {
                    if (busiFeeResult!!.PRESAVING.isNotEmpty() &&
                            busiFeeResult!!.PRESAVING.toDouble() > 0 || (busiFeeResult!!.AllBusifee.isNotEmpty() &&
                            busiFeeResult!!.AllBusifee.toDouble() == 0.00)) {
                        Pmttp = "010003"
                    }
                }
            }

            ConstantKotlin.OrderType.NFCGASFEEPREDEPOSIT -> {
                if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text)) {
                    amount = activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat()
                } else {
                    amount = 0f
                }

                if (gasFeeResult!!.LimitGasfee != 0f && amount >
                        gasFeeResult!!.LimitGasfee - gasFeeResult!!.NFCNotWriteGas) {
                    ToastUtil.toastWarning("预存金额大于限购金额！")
                    return
                } else if (amount <= 0) {
                    ToastUtil.toastWarning("请输入预存金额！")
                    return
                }

                NFCFlag = "11"
                Pmttp = "010001"
                UserNb = gasFeeResult!!.UserNb
                UserName = gasFeeResult!!.UserName
                CityCode = gasFeeResult!!.CityCode
                ProvinceCode = gasFeeResult!!.ProvinceCode
                PayeeCode = gasFeeResult!!.PayeeCode
                UserAddr = gasFeeResult!!.UserAddr
                FeeCount = gasFeeResult!!.FeeCount
                QuerySeq = gasFeeResult!!.QuerySeq
                DueMonth = gasFeeResult!!.DueMonth
                QueryId = gasFeeResult!!.QueryId

            }

            ConstantKotlin.OrderType.GASFEEPREDEPOSIT -> {
                if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text)) {
                    amount = activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat()
                } else {
                    amount = 0f
                }

                if (gasFeeResult!!.LimitGasfee != 0f && amount >
                        gasFeeResult!!.LimitGasfee - gasFeeResult!!.NFCNotWriteGas) {
                    ToastUtil.toastWarning("预存金额大于限购金额！")
                    return
                } else if (amount <= 0) {
                    ToastUtil.toastWarning("请输入预存金额！")
                    return
                }

                when (ytbType) {
                    1 -> {
                        NFCFlag = "11"
                        Pmttp = "010001"
                        UserNb = gasFeeResult!!.UserNb
                        UserName = gasFeeResult!!.UserName
                        CityCode = gasFeeResult!!.CityCode
                        ProvinceCode = gasFeeResult!!.ProvinceCode
                        PayeeCode = gasFeeResult!!.PayeeCode
                        UserAddr = gasFeeResult!!.UserAddr
                        FeeCount = gasFeeResult!!.FeeCount
                        QuerySeq = gasFeeResult!!.QuerySeq
                        DueMonth = gasFeeResult!!.DueMonth
                        QueryId = gasFeeResult!!.QueryId
                    }
                    2 -> {
                        NFCFlag = "12"
                        Pmttp = "010001"
                        NFCICSumCount = if (gasFeeResult!!.NfcSumcount == null) "0" else gasFeeResult!!.NfcSumcount
                        ICcardNo = gasFeeResult!!.IcCardno
                        UserNb = gasFeeResult!!.UserNb
                        UserName = gasFeeResult!!.UserName
                        CityCode = gasFeeResult!!.CityCode
                        ProvinceCode = gasFeeResult!!.ProvinceCode
                        PayeeCode = gasFeeResult!!.PayeeCode
                        UserAddr = gasFeeResult!!.UserAddr
                        FeeCount = gasFeeResult!!.FeeCount
                        QuerySeq = gasFeeResult!!.QuerySeq
                        DueMonth = gasFeeResult!!.DueMonth
                        QueryId = gasFeeResult!!.QueryId
                    }
                }
            }

            ConstantKotlin.OrderType.BLUETOOTHGASFEEPREDEPOSIT -> {
                if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text)) {
                    amount = activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat()
                } else {
                    amount = 0f
                }

                if (gasFeeResult!!.LimitGasfee != 0f && amount >
                        gasFeeResult!!.LimitGasfee - gasFeeResult!!.NFCNotWriteGas) {
                    ToastUtil.toastWarning("预存金额大于限购金额！")
                    return
                } else if (amount <= 0) {
                    ToastUtil.toastWarning("请输入预存金额！")
                    return
                }

                Pmttp = "010001"
                UserNb = gasFeeResult!!.UserNb
                UserName = gasFeeResult!!.UserName
                CityCode = gasFeeResult!!.CityCode
                ProvinceCode = gasFeeResult!!.ProvinceCode
                PayeeCode = gasFeeResult!!.PayeeCode
                UserAddr = gasFeeResult!!.UserAddr
                NFCFlag = "13"
                FeeCount = gasFeeResult!!.FeeCount
                QuerySeq = gasFeeResult!!.QuerySeq
                DueMonth = gasFeeResult!!.DueMonth
                QueryId = gasFeeResult!!.QueryId
            }

        }

        if (amount > 0) {
            val user = EncryptedPreferencesUtils.getUser()
            val decimal = BigDecimal(amount.toDouble())
            val body = HttpRequestParams.getParamsForOrderSubmit(Pmttp,
                    decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), UserNb, UserName, UserAddr, CityCode,
                    ProvinceCode, PayeeCode, user.LoginAccount, FeeCount, QuerySeq, DueMonth, NFCFlag, PrestoreFlag,
                    StaffPhone, NFCICSumCount, ICcardNo, QueryId, null)
            post(HttpURLS.orderSubmit, true, "OrderSubmit", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.i(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        val OrderNb = ResponseFields.getString("OrderNb")
                        val OrderSetTime = ResponseFields.getString("OrderSetTime")
                        val PayeeNm = ResponseFields.getString("PayeeNm")

                        val order = Order()
                        order.OrderNb = OrderNb
                        order.OrderSetTime = OrderSetTime
                        order.ProductName = PayeeNm
                        order.OrderAmount = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                        order.usernb = UserNb

                        Logger.i(order.toString())

                        ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity::class.java)
                        val intent1 = Intent(this@ResultForGasFeeActivity, PayActivity::class.java)
                        intent1.putExtra("Order", Parcels.wrap(order))
                        startActivity(intent1)
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        } else {
            if (gasFeeResult != null &&
                    (gasFeeResult!!.PayeeCode == ConstantKotlin.NanJingCode
                            || gasFeeResult!!.PayeeCode == ConstantKotlin.BaoHuaCode)) {
                if (amount == 0f) {
                    ToastUtil.toastInfo("没有欠费账单!")
                }
            } else {
                ToastUtil.toastWarning("请填写缴费/预存金额！")
            }
        }
    }


    /**
     * 检测设备是否支持NFC并检测是否已开启NFC
     */
    @SuppressLint("NewApi")
    private fun initNFC() {
        val mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        isNFCOk = mNfcAdapter != null
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    private inner class WebViewClientDemo : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Logger.d("shouldOverrideUrlLoading-url:" + url)
            view.loadUrl(url)// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Logger.d("onPageFinished-url:" + url)
        }

        override fun onReceivedSslError(view: WebView,
                                        handler: SslErrorHandler, error: SslError) {
            handler.proceed()// 接受所有网站的证书
        }

    }

}
