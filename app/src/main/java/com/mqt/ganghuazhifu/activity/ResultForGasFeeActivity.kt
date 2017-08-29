package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.text.*
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.BusiFeeResult
import com.mqt.ganghuazhifu.bean.GasFeeResult
import com.mqt.ganghuazhifu.bean.IcChangeResult
import com.mqt.ganghuazhifu.bean.Order
import com.mqt.ganghuazhifu.databinding.ActivityResultForGasFeeBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.TextValidation
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.mqt.ganghuazhifu.view.PaymentMethodDialog
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.math.BigDecimal

/**
 * 缴纳气费(营业费)欠费查询结果

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class ResultForGasFeeActivity : BaseActivity() {

    private var gasFeeResult: GasFeeResult? = null
    private var busiFeeResult: BusiFeeResult? = null
    private var amount: Float = 0.toFloat()
    private var type: Int = 0// 1:缴纳气费;2:缴纳营业费;7:NFC预存气费;8:营业费预存;
    // 9:蓝牙读卡器缴气费;11:IC卡气量表缴费;12:蓝牙表预存气费;13:气量表缴费蓝牙
    private var StaffPhone: String? = null
    //    private var flag: Boolean = false
    private var activityResultForGasFeeBinding: ActivityResultForGasFeeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultForGasFeeBinding = DataBindingUtil.setContentView<ActivityResultForGasFeeBinding>(this,
                R.layout.activity_result_for_gas_fee)
        type = intent.getIntExtra("TYPE", 1)
        StaffPhone = intent.getStringExtra("StaffPhone")
        gasFeeResult = Parcels.unwrap<GasFeeResult>(intent.getParcelableExtra<Parcelable>("GasFeeResult"))
        busiFeeResult = Parcels.unwrap<BusiFeeResult>(intent.getParcelableExtra<Parcelable>("BusiFeeResult"))
//        flag = intent.getBooleanExtra("FLAG", false)
        initView()
        Logger.d("type--->" + type)
        when (type) {
            13, 11 -> {
                if (!TextUtils.isEmpty(gasFeeResult!!.NFCSecurAlert)) {
                    MaterialDialog.Builder(this)
                            .title("提示")
                            .content(gasFeeResult!!.NFCSecurAlert)
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
                            if (!TextUtils.isEmpty(s.toString()) && java.lang.Float.parseFloat(
                                    s.toString()) > gasFeeResult!!.UserCanAmount) {
                                activityResultForGasFeeBinding!!.tvLimit.text = "您的本次可购气量为:" + gasFeeResult!!.NFCLimitGasFee + "m³"
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.VISIBLE
                            } else {
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.GONE
                            }
                        }
                    }
                })
                if (gasFeeResult != null) {
                    setDatatoView()
                }
            }
            7, 9 -> {
                if (!TextUtils.isEmpty(gasFeeResult!!.NFCSecurAlert)) {
                    MaterialDialog.Builder(this)
                            .title("提示")
                            .content(gasFeeResult!!.NFCSecurAlert)
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
                        if (gasFeeResult!!.NFCLimitGasFee != 0f) {
                            if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(".") && java.lang.Float.parseFloat(
                                    s.toString()) > gasFeeResult!!.NFCLimitGasFee - gasFeeResult!!.NFCNotWriteGas) {
                                if (gasFeeResult!!.NFCNotWriteGas > 0)
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" + gasFeeResult!!.NFCLimitGasFee + "，未写卡金额" + gasFeeResult!!.NFCNotWriteGas + "，再次预存最大金额" + (gasFeeResult!!.NFCLimitGasFee - gasFeeResult!!.NFCNotWriteGas)
                                else
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" + gasFeeResult!!.NFCLimitGasFee
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.VISIBLE
                            } else {
                                activityResultForGasFeeBinding!!.tvLimit.text = ""
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.GONE
                            }
                        }
                    }
                })
                if (gasFeeResult != null) {
                    setDatatoView()
                }
            }
            12 -> {
                if (!TextUtils.isEmpty(gasFeeResult!!.NFCSecurAlert)) {
                    MaterialDialog.Builder(this)
                            .title("提示")
                            .content(gasFeeResult!!.NFCSecurAlert)
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
                        if (gasFeeResult!!.NFCLimitGasFee != 0f) {
                            if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(".") && java.lang.Float.parseFloat(
                                    s.toString()) > gasFeeResult!!.NFCLimitGasFee - gasFeeResult!!.NFCNotWriteGas) {
                                if (gasFeeResult!!.NFCNotWriteGas > 0)
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" + gasFeeResult!!.NFCLimitGasFee + "，未写卡金额" + gasFeeResult!!.NFCNotWriteGas + "，再次预存最大金额" + (gasFeeResult!!.NFCLimitGasFee - gasFeeResult!!.NFCNotWriteGas)
                                else
                                    activityResultForGasFeeBinding!!.tvLimit.text = "您的限购金额为" + gasFeeResult!!.NFCLimitGasFee
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.VISIBLE
                            } else {
                                activityResultForGasFeeBinding!!.tvLimit.text = ""
                                activityResultForGasFeeBinding!!.llLimit.visibility = View.GONE
                            }
                        }
                    }
                })
                if (gasFeeResult != null) {
                    setDatatoView()
                }
            }
            8, 10, 1 -> if (gasFeeResult != null) {
                setDatatoView()
            }
            2 -> if (busiFeeResult != null) {
                setDatatoView()
            }
        }
    }

    private fun setDatatoView() {
        val name: String
        val address: String
        val nameBuilder: StringBuilder
        when (type) {
            8 -> {
                activityResultForGasFeeBinding!!.tvOweAmount.visibility = View.GONE
                when (gasFeeResult!!.PayeeCode) {
                    "320000320100019999" -> {
                        activityResultForGasFeeBinding!!.llMoneyAll.visibility = View.GONE
                        activityResultForGasFeeBinding!!.llNanjingFeeCount.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.llNanjingQuerySeq.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.llPhonographyNumber.visibility = View.GONE
                        activityResultForGasFeeBinding!!.tvNanjingFeeCount.text = "账期数目：" + gasFeeResult!!.FeeCount
                        activityResultForGasFeeBinding!!.tvNanjingQuerySeq.text = "查询序列号：" + gasFeeResult!!.QuerySeq
                    }
                    "320000321100019998" -> {
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
                name = gasFeeResult!!.UserName
                address = gasFeeResult!!.UserAddr
                nameBuilder = StringBuilder(name)
                if (name.length <= 1) {

                } else if (name.length <= 3) {
                    nameBuilder.setCharAt(0, '*')
                } else {

                    for (i in 0..name.length - 2 - 1) {
                        nameBuilder.setCharAt(i, '*')
                    }
                }
                activityResultForGasFeeBinding!!.tvAccountName.text = "客户名：" + nameBuilder.toString()
                activityResultForGasFeeBinding!!.tvAccountAddress.text = "地址：" + address
                activityResultForGasFeeBinding!!.tvOweAmount.text = "应收总额：" + gasFeeResult!!.AllGasfee + "元"
            }
            13, 12, 7, 9, 11, 10, 1 -> {
                when (gasFeeResult!!.PayeeCode) {
                    "320000320100019999" -> {
                        activityResultForGasFeeBinding!!.llMoneyAll.visibility = View.GONE
                        activityResultForGasFeeBinding!!.llNanjingFeeCount.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.llNanjingQuerySeq.visibility = View.VISIBLE
                        activityResultForGasFeeBinding!!.llPhonographyNumber.visibility = View.GONE
                        activityResultForGasFeeBinding!!.tvNanjingFeeCount.text = "账期数目：" + gasFeeResult!!.FeeCount
                        activityResultForGasFeeBinding!!.tvNanjingQuerySeq.text = "查询序列号：" + gasFeeResult!!.QuerySeq
                    }
                    "320000321100019998" -> {
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
                name = gasFeeResult!!.UserName
                address = gasFeeResult!!.UserAddr
                nameBuilder = StringBuilder(name)
                if (name.length <= 1) {
                } else if (name.length <= 3) {
                    nameBuilder.setCharAt(0, '*')
                } else {
                    for (i in 0..name.length - 2 - 1) {
                        nameBuilder.setCharAt(i, '*')
                    }
                }
                activityResultForGasFeeBinding!!.tvAccountName.text = "客户名：" + nameBuilder.toString()
                activityResultForGasFeeBinding!!.tvAccountAddress.text = "地址：" + address
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
            2 -> {
                activityResultForGasFeeBinding!!.llMoneyAll.visibility = View.VISIBLE
                activityResultForGasFeeBinding!!.llNanjingFeeCount.visibility = View.GONE
                activityResultForGasFeeBinding!!.llNanjingQuerySeq.visibility = View.GONE
                activityResultForGasFeeBinding!!.llPhonographyNumber.visibility = View.VISIBLE
                activityResultForGasFeeBinding!!.tvAccountNumber.text = "户号：" + busiFeeResult!!.UserNb
                activityResultForGasFeeBinding!!.tvPhonographyNumber.text = "速记号：" + if (busiFeeResult!!.EasyNo != null) busiFeeResult!!.EasyNo else ""

                if (busiFeeResult!!.PayeeCode.equals("440000440100010072")) {
                    activityResultForGasFeeBinding!!.llYuAmount.visibility = View.VISIBLE
                    activityResultForGasFeeBinding!!.tvYuAmount.text = "预存余额：" + busiFeeResult!!.PRESAVING + "元"

                }

                name = busiFeeResult!!.UserName
                address = busiFeeResult!!.UserAddr

                nameBuilder = StringBuilder(name)
                if (name.length <= 1) {
                } else if (name.length <= 3) {
                    nameBuilder.setCharAt(0, '*')
                } else {
                    for (i in 0..name.length - 2 - 1) {
                        nameBuilder.setCharAt(i, '*')
                    }
                }
                activityResultForGasFeeBinding!!.tvAccountName.text = "客户名：" + nameBuilder.toString()
                activityResultForGasFeeBinding!!.tvAccountAddress.text = "地址：" + address
                activityResultForGasFeeBinding!!.tvOweAmount.text = "应收总额：" + busiFeeResult!!.AllBusifee + "元"
            }
        }
    }

    private fun initView() {
        setSupportActionBar(activityResultForGasFeeBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (type != 8 && type != 7 && type != 11 && type != 13) {
            activityResultForGasFeeBinding!!.tvTitleRight.visibility = View.VISIBLE
            activityResultForGasFeeBinding!!.tvTitleRight.setOnClickListener(this)
        }
        TextValidation.setRegularValidationNumberDecimal(this, activityResultForGasFeeBinding!!.etYucun)
        supportActionBar!!.title = "查询结果"
        activityResultForGasFeeBinding!!.buttonPayment.setOnClickListener(this)
        activityResultForGasFeeBinding!!.tvExplainer.text = "请在缴费前注意检查户号、客户名和地址"
        when (type) {
            10, 1 -> when (gasFeeResult!!.PayeeCode) {
                "320000320100019999" -> {
                    activityResultForGasFeeBinding!!.tvButtonText.text = "缴费"
                    activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                }
                "320000321100019998" -> {
                    activityResultForGasFeeBinding!!.tvButtonText.text = "缴费"
                    activityResultForGasFeeBinding!!.tvMoneyAll.visibility = View.GONE
                    activityResultForGasFeeBinding!!.etYucun.visibility = View.GONE
                }
                else -> {
                    activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                    activityResultForGasFeeBinding!!.tvButtonText.text = "缴费/预存"
                }
            }
            9 -> {
                activityResultForGasFeeBinding!!.tvButtonText.text = "缴费"
                activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
            }
            7 -> {
                activityResultForGasFeeBinding!!.tvButtonText.text = "NFC预存"
                activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
            }
            12 -> {
                activityResultForGasFeeBinding!!.tvButtonText.text = "蓝牙表预存"
                activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
            }
            13, 11 -> {
                activityResultForGasFeeBinding!!.tvButtonText.text = "IC卡预存"
                activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                activityResultForGasFeeBinding!!.tvMoneyAll.text = "预存总气量：m³"
            }
            8 -> {
                activityResultForGasFeeBinding!!.tvButtonText.text = "营业费预存"
                activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                activityResultForGasFeeBinding!!.tvMoneyAll.text = "预存总金额：￥"
            }
            2 ->
                when (busiFeeResult!!.PayeeCode) {
                    "440000440100010072" -> {
                        if (busiFeeResult!!.PRESAVING.isNotEmpty() &&
                                busiFeeResult!!.PRESAVING.toDouble() > 0 || (busiFeeResult!!.AllBusifee.isNotEmpty() &&
                                busiFeeResult!!.AllBusifee.toDouble() == 0.00)) {
                            //预存
                            activityResultForGasFeeBinding!!.tvButtonText.text = "预存"
                            activityResultForGasFeeBinding!!.etYucun.visibility = View.VISIBLE
                            activityResultForGasFeeBinding!!.tvMoneyAll.text = "实缴总金额：￥"
                        } else {
                            activityResultForGasFeeBinding!!.llYucunTip.visibility = View.VISIBLE
                            activityResultForGasFeeBinding!!.tvButtonText.text = "缴费"
                            activityResultForGasFeeBinding!!.etYucun.visibility = View.INVISIBLE
                            activityResultForGasFeeBinding!!.tvMoneyAll.text = "实缴总金额：￥" + busiFeeResult!!.AllBusifee + "元"
                        }
                    }
                    else -> {
                        activityResultForGasFeeBinding!!.tvButtonText.text = "缴费"
                        activityResultForGasFeeBinding!!.etYucun.visibility = View.INVISIBLE
                        activityResultForGasFeeBinding!!.tvMoneyAll.text = "实缴总金额：￥" + busiFeeResult!!.AllBusifee + "元"
                    }
                }

        }

        val user = EncryptedPreferencesUtils.getUser()

        if (user.GeneralContactCount == 0 && type != 2) {
            MaterialDialog.Builder(this)
                    .title("提醒")
                    .content("是否将该户号添加为常用联系人？")
                    .onPositive { dialog, which -> addGeneralContact() }
                    .positiveText("确定")
                    .negativeText("取消")
                    .show()
        }
    }

    private fun addGeneralContact() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForAddGeneralContact(user.Uid, gasFeeResult!!.PayeeCode, gasFeeResult!!.UserNb, gasFeeResult!!.UserName, "1", "")
        post(HttpURLS.addGeneralContact, true, "AddGeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
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
                intent.putExtra("TYPE", type)
                intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(gasFeeResult))
                intent.putExtra("BusiFeeResult", Parcels.wrap<BusiFeeResult>(busiFeeResult))
                startActivity(intent)
            }
            R.id.button_payment -> {

                when (type) {
                    13, 11 -> icChange()
                    else -> makeOrder()
                }

//                PaymentMethodDialog(this, PaymentMethodDialog.OnPaymentMethodSelectedListener {
//                    position ->
//                    run {
//                        when (position) {
//                            0 -> {
//                                when (type) {
//                                    13, 11 -> icChange()
//                                    else -> makeOrder()
//                                }
//                            }
//                            1 -> {
//                                when (type) {
//                                    13, 11 -> icChange()
//                                    else -> makeOrder()
//                                }
//                            }
//                        }
//                    }
//                }).showDialog()

            }
        }
    }

    fun icChange() {
        if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text.toString()) && activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat() > 0) {
            val body = HttpRequestParams.getParamsForGasICPayMesQuery(gasFeeResult!!.UserNb, gasFeeResult!!.ICcardNo, activityResultForGasFeeBinding!!.etYucun.text.toString(),
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
                                            makeOrder11(gasFeeResult!!.AllGasfee.toFloat())
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
                                            makeOrder11(icChangeResult.Amount.toFloat())
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
                            makeOrder11(gasFeeResult!!.AllGasfee.toFloat())
                        }
                        .positiveText("确定")
                        .negativeText("取消")
                        .content("您未输入预存气量，是否缴纳欠费：" + gasFeeResult!!.AllGasfee + "元")
                        .show()
            }
        }
    }

    fun makeOrder11(amount: Float) {
        if (amount > 0) {
            var Pmttp: String = "010001"
            var UserName: String = gasFeeResult!!.UserName
            var CityCode: String = gasFeeResult!!.CityCode
            var ProvinceCode: String = gasFeeResult!!.ProvinceCode
            var PayeeCode: String = gasFeeResult!!.PayeeCode
            var UserAddr: String = gasFeeResult!!.UserAddr
            var FeeCount: String? = null
            var QuerySeq: String? = null
            var DueMonth: String? = null
            var NFCFlag: String? = gasFeeResult!!.NFCFlag
            var PrestoreFlag: String? = null
            var StaffPhone: String? = null
            var UserNb: String? = gasFeeResult!!.UserNb
            var NFCICSumCount: Int = gasFeeResult!!.NFCICSumCount
            var ICcardNo: String = gasFeeResult!!.ICcardNo
            var QueryId: String = gasFeeResult!!.QueryId

            when (type) {
                11 -> NFCFlag = "14"
                13 -> NFCFlag = "15"
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
            if (gasFeeResult != null && gasFeeResult!!.PayeeCode == "320000320100019999" || gasFeeResult!!.PayeeCode == "320000321100019998") {
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
        if (type == 1 || type == 9 || type == 10) {
            if (gasFeeResult != null && (gasFeeResult!!.PayeeCode == "320000320100019999" || gasFeeResult!!.PayeeCode == "320000321100019998")) {
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
                ToastUtil.toastWarning("输入金额必须不小于欠费金额！")
                return
            }
        } else if (type == 7 || type == 12) {
            if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text)) {
                amount = activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat()
            } else {
                amount = 0f
            }

            if (gasFeeResult!!.NFCLimitGasFee != 0f && amount > gasFeeResult!!.NFCLimitGasFee - gasFeeResult!!.NFCNotWriteGas) {
                ToastUtil.toastWarning("预存金额大于限购金额！")
                return
            } else if (amount <= 0) {
                ToastUtil.toastWarning("请输入预存金额！")
                return
            }

        } else if (type == 8) {
            if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text)) {
                amount = activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat()
            } else {
                amount = 0f
            }
            if (amount <= 0) {
                ToastUtil.toastWarning("请输入预存金额！")
                return
            }
        } else if (type == 2) {
            if (busiFeeResult!!.PayeeCode.equals("440000440100010072")) {

                if (busiFeeResult!!.PRESAVING.isNotEmpty() &&
                        busiFeeResult!!.PRESAVING.toDouble() > 0 || (busiFeeResult!!.AllBusifee.isNotEmpty() &&
                        busiFeeResult!!.AllBusifee.toDouble() == 0.00)) {
                    //预存

                    if (!TextUtils.isEmpty(activityResultForGasFeeBinding!!.etYucun.text)) {
                        amount = activityResultForGasFeeBinding!!.etYucun.text.toString().toFloat()
                    } else {
                        amount = 0f
                    }
                    if (amount <= 0) {
                        ToastUtil.toastWarning("请输入预存金额！")
                        return
                    } else if (amount < (busiFeeResult!!.AllBusifee.toFloat() - busiFeeResult!!.PRESAVING.toFloat())) {
                        ToastUtil.toastWarningLong("预存金额不能小于预存余额和欠费的差额！")
                        return
                    }
                } else {
                    amount = busiFeeResult!!.AllBusifee.toFloat()
                }

            }
        }

        if (amount > 0) {
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
            var NFCICSumCount: Int = 0
            var ICcardNo: String? = null
            var QueryId: String? = null
            var UserNb: String? = null

            when (type) {
                9 -> {
                    NFCFlag = "12"
                    Pmttp = "010001"
                    NFCICSumCount = gasFeeResult!!.NFCICSumCount
                    ICcardNo = gasFeeResult!!.ICcardNo
                    UserNb = gasFeeResult!!.UserNb
                    UserName = gasFeeResult!!.UserName
                    CityCode = gasFeeResult!!.CityCode
                    ProvinceCode = gasFeeResult!!.ProvinceCode
                    PayeeCode = gasFeeResult!!.PayeeCode
                    UserAddr = gasFeeResult!!.UserAddr
                    if (gasFeeResult!!.PayeeCode == "320000320100019999") {
                        FeeCount = gasFeeResult!!.FeeCount
                        QuerySeq = gasFeeResult!!.QuerySeq
                    } else if (gasFeeResult!!.PayeeCode == "320000321100019998") {
                        DueMonth = gasFeeResult!!.DueMonth
                    }
                    QueryId = gasFeeResult!!.QueryId
                }
                10, 1 -> {
                    Pmttp = "010001"
                    UserNb = gasFeeResult!!.UserNb
                    UserName = gasFeeResult!!.UserName
                    CityCode = gasFeeResult!!.CityCode
                    ProvinceCode = gasFeeResult!!.ProvinceCode
                    PayeeCode = gasFeeResult!!.PayeeCode
                    UserAddr = gasFeeResult!!.UserAddr
                    if (gasFeeResult!!.PayeeCode == "320000320100019999") {
                        FeeCount = gasFeeResult!!.FeeCount
                        QuerySeq = gasFeeResult!!.QuerySeq
                    } else if (gasFeeResult!!.PayeeCode == "320000321100019998") {
                        DueMonth = gasFeeResult!!.DueMonth
                    }
                    QueryId = gasFeeResult!!.QueryId
                }
                7 -> {
                    Pmttp = "010001"
                    UserNb = gasFeeResult!!.UserNb
                    UserName = gasFeeResult!!.UserName
                    CityCode = gasFeeResult!!.CityCode
                    ProvinceCode = gasFeeResult!!.ProvinceCode
                    PayeeCode = gasFeeResult!!.PayeeCode
                    UserAddr = gasFeeResult!!.UserAddr
                    NFCFlag = "11"
                    if (gasFeeResult!!.PayeeCode == "320000320100019999") {
                        FeeCount = gasFeeResult!!.FeeCount
                        QuerySeq = gasFeeResult!!.QuerySeq
                    } else if (gasFeeResult!!.PayeeCode == "320000321100019998") {
                        DueMonth = gasFeeResult!!.DueMonth
                    }
                    QueryId = gasFeeResult!!.QueryId
                }
                12 -> {
                    Pmttp = "010001"
                    UserNb = gasFeeResult!!.UserNb
                    UserName = gasFeeResult!!.UserName
                    CityCode = gasFeeResult!!.CityCode
                    ProvinceCode = gasFeeResult!!.ProvinceCode
                    PayeeCode = gasFeeResult!!.PayeeCode
                    UserAddr = gasFeeResult!!.UserAddr
                    NFCFlag = "13"
                    if (gasFeeResult!!.PayeeCode == "320000320100019999") {
                        FeeCount = gasFeeResult!!.FeeCount
                        QuerySeq = gasFeeResult!!.QuerySeq
                    } else if (gasFeeResult!!.PayeeCode == "320000321100019998") {
                        DueMonth = gasFeeResult!!.DueMonth
                    }
                    QueryId = gasFeeResult!!.QueryId
                }
                8 -> {
                    Pmttp = "010002"
                    UserNb = gasFeeResult!!.UserNb
                    UserName = gasFeeResult!!.UserName
                    CityCode = gasFeeResult!!.CityCode
                    ProvinceCode = gasFeeResult!!.ProvinceCode
                    PayeeCode = gasFeeResult!!.PayeeCode
                    UserAddr = gasFeeResult!!.UserAddr
                    NFCFlag = "10"
                    if (gasFeeResult!!.PayeeCode == "320000320100019999") {
                        FeeCount = gasFeeResult!!.FeeCount
                        QuerySeq = gasFeeResult!!.QuerySeq
                    } else if (gasFeeResult!!.PayeeCode == "320000321100019998") {
                        DueMonth = gasFeeResult!!.DueMonth
                    }
                    QueryId = gasFeeResult!!.QueryId
                    PrestoreFlag = "10"
                    StaffPhone = this@ResultForGasFeeActivity.StaffPhone
                }
                2 -> {
                    UserNb = busiFeeResult!!.UserNb
                    UserName = busiFeeResult!!.UserName
                    CityCode = busiFeeResult!!.CityCode
                    ProvinceCode = busiFeeResult!!.ProvinceCode
                    PayeeCode = busiFeeResult!!.PayeeCode
                    UserAddr = busiFeeResult!!.UserAddr
                    QueryId = busiFeeResult!!.QueryId
                    if (busiFeeResult!!.PayeeCode.equals("440000440100010072")) {
                        if (busiFeeResult!!.PRESAVING.isNotEmpty() &&
                                busiFeeResult!!.PRESAVING.toDouble() > 0 || (busiFeeResult!!.AllBusifee.isNotEmpty() &&
                                busiFeeResult!!.AllBusifee.toDouble() == 0.00)) {
                            //预存
                            Pmttp = "010003"
                        } else {
                            Pmttp = "010002"
                        }
                    }
                }
            }

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
            if (gasFeeResult != null && gasFeeResult!!.PayeeCode == "320000320100019999" || gasFeeResult!!.PayeeCode == "320000321100019998") {
                if (amount == 0f) {
                    ToastUtil.toastInfo("没有欠费账单!")
                }
            } else {
                ToastUtil.toastWarning("请填写缴费/预存金额！")
            }
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
