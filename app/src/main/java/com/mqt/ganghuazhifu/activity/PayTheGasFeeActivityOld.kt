package com.mqt.ganghuazhifu.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.GeneralContactAdapter
import com.mqt.ganghuazhifu.bean.*
import com.mqt.ganghuazhifu.bean.Unit
import com.mqt.ganghuazhifu.databinding.ActivityPayTheGasFeeBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.utils.*
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.text.SimpleDateFormat
import java.util.*

/**
 * 缴纳气费(复用)

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class PayTheGasFeeActivityOld : BaseActivity(), DatePickerDialog.OnDateSetListener, OnRecyclerViewItemClickListener {
    private var province: City? = null
    private var city: City? = null
    private var unit: Unit? = null
    private var generalContactList: ArrayList<GeneralContact>? = null
    private var dialog: DatePickerDialog? = null
    private var adapter: GeneralContactAdapter? = null
    private var isNFCOk: Boolean = false
    private var generalContact: GeneralContact? = null
    private var activityPayTheGasFeeBinding: ActivityPayTheGasFeeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPayTheGasFeeBinding = DataBindingUtil.setContentView<ActivityPayTheGasFeeBinding>(this, R.layout.activity_pay_the_gas_fee)
        type = intent.getIntExtra("TYPE", 1)
        generalContact = Parcels.unwrap<GeneralContact>(intent.getParcelableExtra<Parcelable>("GeneralContact"))
        initView()
        val calendar = Calendar.getInstance(Locale.CHINA)
        dialog = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog!!.setStartTitle("起始")
        dialog!!.setEndTitle("终止")    }

    private fun initView() {
        province = City()
        city = City()
        unit = Unit()
        initNFC()

        setSupportActionBar(activityPayTheGasFeeBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                UnitConversionUtils.dipTopx(this, 50f))
        params.leftMargin = UnitConversionUtils.dipTopx(this, 12f)
        params.rightMargin = UnitConversionUtils.dipTopx(this, 12f)

        if (WelcomeActivity.screenwidth == 0 || WelcomeActivity.screenhigh == 0) {
            WelcomeActivity.screenwidth = EncryptedPreferencesUtils.getScreenSize()[0]
            WelcomeActivity.screenhigh = EncryptedPreferencesUtils.getScreenSize()[1]
        }
        Logger.d("screenwidth--->" + WelcomeActivity.screenwidth)
        Logger.d("screenhigh--->" + WelcomeActivity.screenhigh)

        if (type == 1 || type == 2) {
            params.topMargin = WelcomeActivity.screenhigh - UnitConversionUtils.dipTopx(this, 406f)
        } else {
            params.topMargin = WelcomeActivity.screenhigh - UnitConversionUtils.dipTopx(this, 468f)
        }
        activityPayTheGasFeeBinding!!.buttonNext.layoutParams = params

        activityPayTheGasFeeBinding!!.buttonNext.setOnClickListener(this)
        activityPayTheGasFeeBinding!!.linearLayoutCity.setOnClickListener(this)
        activityPayTheGasFeeBinding!!.linearLayoutUnit.setOnClickListener(this)
        activityPayTheGasFeeBinding!!.linearLayoutTime.setOnClickListener(this)

        adapter = GeneralContactAdapter()
        activityPayTheGasFeeBinding!!.etNum.setAdapter(adapter)
        adapter!!.onRecyclerViewItemClickListener = this

        if (type != 10) {
            initPopupWindow()
            activityPayTheGasFeeBinding!!.scrollViewAll.setOnTouchListener({ v, event ->
                if (activityPayTheGasFeeBinding!!.citypicker.visibility == View.VISIBLE) {
                    activityPayTheGasFeeBinding!!.citypicker.visibility = View.INVISIBLE
                    activityPayTheGasFeeBinding!!.citypicker.setDefaut()
                }
                if (activityPayTheGasFeeBinding!!.unitpicker.visibility == View.VISIBLE) {
                    activityPayTheGasFeeBinding!!.unitpicker.visibility = View.INVISIBLE
                }
                this@PayTheGasFeeActivityOld.currentFocus != null && this@PayTheGasFeeActivityOld.currentFocus!!.windowToken != null && App.manager!!.hideSoftInputFromWindow(this@PayTheGasFeeActivityOld.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            })

            activityPayTheGasFeeBinding!!.relativeLayoutAll.setOnTouchListener({ v, event ->
                if (activityPayTheGasFeeBinding!!.citypicker.visibility == View.VISIBLE) {
                    activityPayTheGasFeeBinding!!.citypicker.visibility = View.INVISIBLE
                    activityPayTheGasFeeBinding!!.citypicker.setDefaut()
                }
                if (activityPayTheGasFeeBinding!!.unitpicker.visibility == View.VISIBLE) {
                    activityPayTheGasFeeBinding!!.unitpicker.visibility = View.INVISIBLE
                }
                false
            })
            initAutoCompleteTextView()
//            init()
            activityPayTheGasFeeBinding!!.tvCity.text = "加载中..."
            val calendar = Calendar.getInstance(Locale.CHINA)
            val endyear = calendar.get(Calendar.YEAR)
            val endmonth = calendar.get(Calendar.MONTH) + 1
            val startyear: Int
            val startmonth: Int
            if (endmonth > 5) {
                startyear = endyear
                startmonth = endmonth - 5
            } else {
                startyear = endyear - 1
                startmonth = endmonth - 5 + 12
            }

            activityPayTheGasFeeBinding!!.tvTime.text = startyear.toString() + "." + DateTextUtils.DateToString(startmonth) + "-" + endyear + "." + DateTextUtils.DateToString(endmonth)

            when (type) {
                11, 9, 1 -> {
                    supportActionBar!!.title = "缴纳气费"
                    activityPayTheGasFeeBinding!!.linearLayoutTime.visibility = View.GONE
                    activityPayTheGasFeeBinding!!.tvNext.text = "下一步"
                }
                2 -> {
                    supportActionBar!!.title = "缴纳营业费"
                    activityPayTheGasFeeBinding!!.linearLayoutTime.visibility = View.GONE
                    activityPayTheGasFeeBinding!!.tvNext.text = "下一步"
                }
                3 -> {
                    supportActionBar!!.title = "气费账单查询"
                    activityPayTheGasFeeBinding!!.tvNext.text = "查询账单"
                }
                4 -> {
                    supportActionBar!!.title = "营业费账单查询"
                    activityPayTheGasFeeBinding!!.tvNext.text = "查询账单"
                }
                5 -> {
                    supportActionBar!!.title = "缴纳水费"
                    activityPayTheGasFeeBinding!!.linearLayoutTime.visibility = View.GONE
                    activityPayTheGasFeeBinding!!.tvNext.text = "下一步"
                }
                6 -> {
                    supportActionBar!!.title = "水费账单查询"
                    activityPayTheGasFeeBinding!!.linearLayoutTime.visibility = View.GONE
                    activityPayTheGasFeeBinding!!.tvNext.text = "查询账单"
                }
                7 -> {
                    supportActionBar!!.title = "预存气费"
                    activityPayTheGasFeeBinding!!.linearLayoutTime.visibility = View.GONE
                    activityPayTheGasFeeBinding!!.tvNext.text = "下一步"
                }
            }
        } else {
            supportActionBar!!.title = "缴纳气费"
            activityPayTheGasFeeBinding!!.linearLayoutTime.visibility = View.GONE
            activityPayTheGasFeeBinding!!.tvNext.text = "下一步"
            activityPayTheGasFeeBinding!!.tvCity.text = generalContact!!.pcityname
            if (generalContact!!.privincecode != "9999") {
                activityPayTheGasFeeBinding!!.tvCity.text = generalContact!!.pcityname + "  " + generalContact!!.ccityname
            } else {
                activityPayTheGasFeeBinding!!.tvCity.text = generalContact!!.ccityname
            }
            activityPayTheGasFeeBinding!!.tvUnit.text = generalContact!!.cdtrnm
            activityPayTheGasFeeBinding!!.etNum.setText(generalContact!!.usernb)
            activityPayTheGasFeeBinding!!.etNum.isEnabled = false
        }
    }

    private fun initAutoCompleteTextView() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForGeneralContact(user.LoginAccount, user.Uid)
        post(HttpURLS.processQuery, true, "GeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
                init()
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    when (type) {
                        0 -> {
                            generalContactList = null
                            init()
                        }
                        1 -> {
                            val ResponseFields1 = response.getString("ResponseFields")
                            if (ResponseFields1 != null) {
                                generalContactList = JSONObject.parseArray<GeneralContact>(ResponseFields1,
                                        GeneralContact::class.java) as ArrayList<GeneralContact>
                                adapter!!.updateList(generalContactList)
                                var ss = false
                                for (generalContact1 in generalContactList!!) {
                                    if (generalContact1.isdefault == "1") {
                                        province!!.CityCode = generalContact1.privincecode
                                        city!!.CityCode = generalContact1.citycode
                                        unit!!.PayeeCode = generalContact1.payeecode
                                        unit!!.PayeeNm = generalContact1.cdtrnm
                                        activityPayTheGasFeeBinding!!.unitpicker.resetView(city!!.CityCode, DataBaiduPush.getPmttpType())
                                        activityPayTheGasFeeBinding!!.tvCity.text = generalContact1.pcityname + " " + generalContact1.ccityname
                                        activityPayTheGasFeeBinding!!.tvUnit.text = generalContact1.cdtrnm
                                        activityPayTheGasFeeBinding!!.etNum.setText(generalContact1.usernb)
                                        ss = true
                                        break
                                    }
                                }
                                if (!ss && generalContactList!!.size > 0) {
                                    val generalContact1 = generalContactList!![0]
                                    province!!.CityCode = generalContact1.privincecode
                                    city!!.CityCode = generalContact1.citycode
                                    unit!!.PayeeCode = generalContact1.payeecode
                                    unit!!.PayeeNm = generalContact1.cdtrnm
                                    activityPayTheGasFeeBinding!!.unitpicker.resetView(city!!.CityCode, DataBaiduPush.getPmttpType())
                                    activityPayTheGasFeeBinding!!.tvCity.text = generalContact1.pcityname + " " + generalContact1.ccityname
                                    activityPayTheGasFeeBinding!!.tvUnit.text = generalContact1.cdtrnm
                                    activityPayTheGasFeeBinding!!.etNum.setText(generalContact1.usernb)
                                }
                            }
                        }
                        2 -> {
                            val ResponseFields = response.getJSONObject("ResponseFields")
                            val QryResults = ResponseFields.getString("QryResults")
                            if (QryResults != null) {
                                generalContactList = ArrayList<GeneralContact>()
                                generalContactList!!.add(JSONObject.parseObject<GeneralContact>(QryResults, GeneralContact::class.java))
                                adapter!!.updateList(generalContactList)
                                var ss = false
                                for (generalContact1 in generalContactList!!) {
                                    if (generalContact1.isdefault == "1") {
                                        province!!.CityCode = generalContact1.privincecode
                                        city!!.CityCode = generalContact1.citycode
                                        unit!!.PayeeCode = generalContact1.payeecode
                                        unit!!.PayeeNm = generalContact1.cdtrnm
                                        activityPayTheGasFeeBinding!!.unitpicker.resetView(city!!.CityCode, DataBaiduPush.getPmttpType())
                                        activityPayTheGasFeeBinding!!.tvCity.text = generalContact1.pcityname + " " + generalContact1.ccityname
                                        activityPayTheGasFeeBinding!!.tvUnit.text = generalContact1.cdtrnm
                                        activityPayTheGasFeeBinding!!.etNum.setText(generalContact1.usernb)
                                        ss = true
                                        break
                                    }
                                }
                                if (!ss && generalContactList!!.size > 0) {
                                    val generalContact1 = generalContactList!![0]
                                    province!!.CityCode = generalContact1.privincecode
                                    city!!.CityCode = generalContact1.citycode
                                    unit!!.PayeeCode = generalContact1.payeecode
                                    unit!!.PayeeNm = generalContact1.cdtrnm
                                    activityPayTheGasFeeBinding!!.unitpicker.resetView(city!!.CityCode, DataBaiduPush.getPmttpType())
                                    activityPayTheGasFeeBinding!!.tvCity.text = generalContact1.pcityname + " " + generalContact1.ccityname
                                    activityPayTheGasFeeBinding!!.tvUnit.text = generalContact1.cdtrnm
                                    activityPayTheGasFeeBinding!!.etNum.setText(generalContact1.usernb)
                                }
                            }
                        }
                    }
                } else {
                    init()
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
        val visible: Int
        when (v.id) {
            R.id.et_num -> activityPayTheGasFeeBinding!!.etNum.requestFocus()
            R.id.linearLayout_city -> {
                visible = activityPayTheGasFeeBinding!!.citypicker.visibility
                if (visible == View.VISIBLE) {
                    activityPayTheGasFeeBinding!!.citypicker.visibility = View.INVISIBLE
                    activityPayTheGasFeeBinding!!.citypicker.setDefaut()
                } else {
                    hideSoftInput()
                    activityPayTheGasFeeBinding!!.unitpicker.visibility = View.INVISIBLE
                    activityPayTheGasFeeBinding!!.citypicker.visibility = View.VISIBLE
                    activityPayTheGasFeeBinding!!.etNum.hint = "请输入户号"
                    activityPayTheGasFeeBinding!!.etNum.setText("")
                }
            }
            R.id.linearLayout_unit -> {
                visible = activityPayTheGasFeeBinding!!.unitpicker.visibility
                if (visible == View.VISIBLE) {
                    activityPayTheGasFeeBinding!!.unitpicker.visibility = View.INVISIBLE
                } else {
                    hideSoftInput()
                    activityPayTheGasFeeBinding!!.citypicker.visibility = View.INVISIBLE
                    activityPayTheGasFeeBinding!!.unitpicker.visibility = View.VISIBLE
                    activityPayTheGasFeeBinding!!.etNum.hint = "请输入户号"
                    activityPayTheGasFeeBinding!!.etNum.setText("")
                }
            }
            R.id.linearLayout_time -> if (dialog != null) {
                dialog!!.setYearRange(1985, 2028)
                dialog!!.show(fragmentManager, TAG)
            }
            R.id.button_next -> when (type) {
                11 -> serach11()
                1 ->
                    when (unit!!.PayeeCode) {
                        "320000320100019999" ->
                            // 南京港华燃气公司编号 = 320000320100019999
                            serach_nanjing()
                        "320000321100019998" ->
                            // 宝华港华燃气公司编号 = 320000321100019998
                            serach_baohua()
                        else -> serach1()
                    }
                9 ->
                    when (unit!!.PayeeCode) {
                        "320000320100019999" ->
                            // 南京港华燃气公司编号 = 320000320100019999
                            serach_nanjing9()
                        "320000321100019998" ->
                            // 宝华港华燃气公司编号 = 320000321100019998
                            serach_baohua9()
                        else -> serach9()
                    }
                2 -> serach2()
                3 -> serach3()
                4 -> serach4()
                5 -> serach5()
                6 -> serach6()
                10 -> if (null != generalContact!!.payeecode) {
                    when (generalContact!!.payeecode) {
                        "320000320100019999" ->
                            // 南京港华燃气公司编号 = 320000320100019999
                            serach_nanjing10()
                        "320000321100019998" ->
                            // 宝华港华燃气公司编号 = 320000321100019998
                            serach_baohua10()
                        else -> serach10()
                    }
                } else {
                    ToastUtil.toastInfo("该城市没有缴费单位")
                }
            }
        }
    }

    /**
     * 水费欠费查询
     */
    private fun serach5() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {

            val body = HttpRequestParams
                    .getParamsForWaterFee(
                            if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode,
                            city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), unit!!.PayeeCode)

            post(HttpURLS.waterFeeQuery, true, "waterFeeQuery", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = WaterFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.FeeCount = ResponseFields.getString("FeeCount")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserName = ResponseFields.getString("UserName")
                        // result.ChargeType = ResponseFields
                        // .getString("ChargeType");
                        result.AB = ResponseFields.getString("AB")
                        result.AllWaterFee = ResponseFields.getString("AllWaterFee")
                        result.TSH = ResponseFields.getString("TSH")
                        result.FEE_MON = ResponseFields.getString("FEE_MON")
                        result.FEE_YJSF = ResponseFields.getString("FEE_YJSF")
                        result.FEE_YJZNJ = ResponseFields.getString("FEE_YJZNJ")
                        var lists: MutableList<WaterFeeRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到水费欠费记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<WaterFeeRecord>(FeeCountDetail, WaterFeeRecord::class.java))
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.WaterFeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForWaterFeeActivity::class.java)
                            Logger.d(unit!!.PayeeCode)
                            // intent.putExtra("TYPE", 1);
                            intent.putExtra("WaterFeeResult", Parcels.wrap<WaterFeeResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<WaterFeeRecord>(FeeCountDetail, WaterFeeRecord::class.java)
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.WaterFeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForWaterFeeActivity::class.java)

                            Logger.d(unit!!.PayeeCode)
                            // intent.putExtra("TYPE", 1);
                            intent.putExtra("WaterFeeResult", Parcels.wrap<WaterFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            // 显示5秒
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 水费账单查询
     */
    private fun serach6() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {

            val body = HttpRequestParams
                    .getParamsForWaterBill(
                            if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode,
                            city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), unit!!.PayeeCode)
            post(HttpURLS.waterBillQuery, true, "waterBillQuery", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = WaterBillResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserName = ResponseFields.getString("UserName")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        result.METER_BOOK = ResponseFields.getString("METER_BOOK")
                        result.BELONG_Station = ResponseFields.getString("BELONG_Station")
                        result.AdvPay_Balance = ResponseFields.getString("AdvPay_Balance")
                        result.FeeCount = ResponseFields.getString("FeeCount")
                        var lists: MutableList<WaterBillRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到水费欠费记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<WaterBillRecord>(FeeCountDetail, WaterBillRecord::class.java))
                            result.WaterBillFeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForWaterBillActivity::class.java)
                            Logger.d(unit!!.PayeeCode)
                            // intent.putExtra("TYPE", 1);
                            intent.putExtra("WaterBillResult", Parcels.wrap<WaterBillResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<WaterBillRecord>(FeeCountDetail, WaterBillRecord::class.java)
                            result.WaterBillFeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForWaterBillActivity::class.java)

                            Logger.d(unit!!.PayeeCode)
                            // intent.putExtra("TYPE", 1);
                            intent.putExtra("WaterBillResult", Parcels.wrap<WaterBillResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 南京燃气费欠费查询（10011033）
     */
    private fun serach_nanjing() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val body = HttpRequestParams
                    .getParamsForNanJingGasFee(
                            if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode,
                            city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), unit!!.PayeeCode)

            post(HttpURLS.njGasArrearsQuery, true, "njGasFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    // TODO Auto-generated method stub
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = GasFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        if (ResponseFields.containsKey("UserName")) {
                            result.UserName = ResponseFields.getString("UserName")
                        } else {
                            result.UserName = ""
                        }
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.FeeCount = ResponseFields.getString("FeeCount")
                        result.QuerySeq = ResponseFields.getString("QuerySeq")
                        var lists: MutableList<GasFeeRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到水费欠费记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java))
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)
                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java)
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)

                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 宝华燃气费欠费查询（10011033）
     */
    private fun serach_baohua() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val body = HttpRequestParams
                    .getParamsForBaoHuaGasFee(
                            if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode,
                            city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), unit!!.PayeeCode)
            post(HttpURLS.bhGasArrearsQuery, true, "bhGasFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    // TODO Auto-generated method stub
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = GasFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.ProvinceCode = ResponseFields.getString("ProvinceCode")
                        result.CityCode = ResponseFields.getString("CityCode")
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        if (ResponseFields.containsKey("UserName")) {
                            result.UserName = ResponseFields.getString("UserName")
                        } else {
                            result.UserName = ""
                        }
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.DueMonth = ResponseFields.getString("DueMonth")
                        //                                result.QuerySeq = ResponseFields.getString("QuerySeq");
                        var lists: MutableList<GasFeeRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到水费欠费记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java))
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)
                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java)
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)

                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 南京燃气费欠费查询（10011033）
     */
    private fun serach_nanjing9() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val body = HttpRequestParams
                    .getParamsForNanJingGasFeeBlue(
                            if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode,
                            city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), unit!!.PayeeCode)

            post(HttpURLS.njGasArrearsQuery, true, "njGasFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    // TODO Auto-generated method stub
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = GasFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        if (ResponseFields.containsKey("UserName")) {
                            result.UserName = ResponseFields.getString("UserName")
                        } else {
                            result.UserName = ""
                        }
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.FeeCount = ResponseFields.getString("FeeCount")
                        result.QuerySeq = ResponseFields.getString("QuerySeq")
                        var lists: MutableList<GasFeeRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到水费欠费记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java))
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)
                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java)
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)

                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 宝华燃气费欠费查询（10011033）
     */
    private fun serach_baohua9() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val body = HttpRequestParams
                    .getParamsForBaoHuaGasFeeBlue(
                            if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode,
                            city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), unit!!.PayeeCode)
            post(HttpURLS.bhGasArrearsQuery, true, "bhGasFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    // TODO Auto-generated method stub
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = GasFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.ProvinceCode = ResponseFields.getString("ProvinceCode")
                        result.CityCode = ResponseFields.getString("CityCode")
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        if (ResponseFields.containsKey("UserName")) {
                            result.UserName = ResponseFields.getString("UserName")
                        } else {
                            result.UserName = ""
                        }
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.DueMonth = ResponseFields.getString("DueMonth")
                        //                                result.QuerySeq = ResponseFields.getString("QuerySeq");
                        var lists: MutableList<GasFeeRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到水费欠费记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java))
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)
                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java)
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)

                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 宝华燃气费欠费查询（10011033）
     */
    private fun serach_baohua10() {
        if (checkEmpty()) {
            val body = HttpRequestParams
                    .getParamsForBaoHuaGasFee(generalContact!!.privincecode,
                            generalContact!!.citycode, generalContact!!.usernb, generalContact!!.payeecode)
            post(HttpURLS.bhGasArrearsQuery, true, "bhGasFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    // TODO Auto-generated method stub
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = GasFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.ProvinceCode = ResponseFields.getString("ProvinceCode")
                        result.CityCode = ResponseFields.getString("CityCode")
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        if (ResponseFields.containsKey("UserName")) {
                            result.UserName = ResponseFields.getString("UserName")
                        } else {
                            result.UserName = ""
                        }
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.DueMonth = ResponseFields.getString("DueMonth")
                        //                                result.QuerySeq = ResponseFields.getString("QuerySeq");
                        var lists: MutableList<GasFeeRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到气费欠费记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java))
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)
                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java)
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)

                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 南京燃气费欠费查询（10011033）
     */
    private fun serach_nanjing10() {
        if (checkEmpty()) {
            val body = HttpRequestParams
                    .getParamsForNanJingGasFee(
                            generalContact!!.privincecode,
                            generalContact!!.citycode, generalContact!!.usernb, generalContact!!.payeecode)
            post(HttpURLS.njGasArrearsQuery, true, "njGasFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    // TODO Auto-generated method stub
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = GasFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        if (ResponseFields.containsKey("UserName")) {
                            result.UserName = ResponseFields.getString("UserName")
                        } else {
                            result.UserName = ""
                        }
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.FeeCount = ResponseFields.getString("FeeCount")
                        result.QuerySeq = ResponseFields.getString("QuerySeq")
                        var lists: MutableList<GasFeeRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到气费欠费记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java))
                            result.CityCode = generalContact!!.citycode
                            result.ProvinceCode = generalContact!!.privincecode
                            result.PayeeCode = generalContact!!.payeecode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)
                            Logger.d(generalContact!!.payeecode)

                            if (generalContact!!.payeecode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java)
                            result.CityCode = generalContact!!.citycode
                            result.ProvinceCode = generalContact!!.privincecode
                            result.PayeeCode = generalContact!!.payeecode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)

                            Logger.d(generalContact!!.payeecode)

                            if (generalContact!!.payeecode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 缴纳气费
     */
    private fun serach1() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForGasFee(
                    if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                        city!!.PcityCode
                    else
                        province!!.CityCode,
                    city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), "1", unit!!.PayeeCode, null)

            post(HttpURLS.gasArrearsQuery, true, "GasFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.i(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = GasFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.HasBusifee = ResponseFields.getString("HasBusifee")
                        result.FeeCount = ResponseFields.getString("FeeCount")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserName = ResponseFields.getString("UserName")
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.EasyNo = ResponseFields.getString("EasyNo")
                        result.NFCFlag = ResponseFields.getString("NfcFlag")
                        result.SecurAlert = ResponseFields.getString("NfcSecurAlert")
                        result.LimitGasfee = ResponseFields.getFloatValue("LimitGasfee")
                        result.NFCNotWriteGas = ResponseFields.getFloatValue("NFCNotWriteGas")

                        if ("11" == result.NFCFlag) {
                            Companion.type = 7
                        }

                        if (!isNFCOk && "11" == result.NFCFlag) {
                            MaterialDialog.Builder(this@PayTheGasFeeActivityOld)
                                    .title("提醒")
                                    .content("手机不支持NFC功能，请更换手机")
                                    .positiveText("确定")
                                    .show()
                        } else {
                            var lists: MutableList<GasFeeRecord> = ArrayList()
                            if (FeeCountDetail == null) {
                                if(Companion.type != 7) {
                                    ToastUtil.toastInfo("没有查到气费欠费记录")
                                    return@OnHttpRequestListener
                                }
                            } else if (FeeCountDetail.startsWith("{")) {
                                lists.add(JSONObject.parseObject<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java))

                            } else if (FeeCountDetail.startsWith("[")) {
                                lists = JSONObject.parseArray<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java)
                            }
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)
                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }

                            intent.putExtra("TYPE", Companion.type)

                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 缴纳气费
     */
    private fun serach9() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForGasFeeBlue(
                    if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                        city!!.PcityCode
                    else
                        province!!.CityCode,
                    city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), "1", unit!!.PayeeCode, null)
            Logger.e(body.toString())
            post(HttpURLS.gasArrearsQuery, true, "GasFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.i(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {

                        val result = GasFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.HasBusifee = ResponseFields.getString("HasBusifee")
                        result.FeeCount = ResponseFields.getString("FeeCount")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserName = ResponseFields.getString("UserName")
                        result.AllGasfee = ResponseFields.getString("AllGasfee")
                        result.EasyNo = ResponseFields.getString("EasyNo")
                        result.NFCFlag = ResponseFields.getString("NfcFlag")
                        result.SecurAlert = ResponseFields.getString("NfcSecurAlert")
                        result.LimitGasfee = ResponseFields.getFloatValue("LimitGasfee")
                        result.NFCNotWriteGas = ResponseFields.getFloatValue("NFCNotWriteGas")
                        if(ResponseFields.containsKey("IcCardno")) {
                            result.IcCardno = ResponseFields.getString("IcCardno")
                        }
                        if(ResponseFields.containsKey("NfcICSumCount")) {
                            result.NfcSumcount = ResponseFields.getString("NfcICSumCount")
                        }

                        if ("10" == result.NFCFlag) {
                            MaterialDialog.Builder(this@PayTheGasFeeActivityOld)
                                    .title("提醒")
                                    .content("该表具不支持蓝牙充值，请更换气费充值！")
                                    .positiveText("确定")
                                    .show()
                        } else if (result.NfcSumcount.toInt() == 0) {
                            MaterialDialog.Builder(this@PayTheGasFeeActivityOld)
                                    .title("提醒")
                                    .content("系统充值次数异常，请前往营业厅查询")
                                    .positiveText("确定")
                                    .show()
                        } else {
                            var lists: MutableList<GasFeeRecord> = ArrayList()
                            if (FeeCountDetail == null) {
                            } else if (FeeCountDetail.startsWith("{")) {
                                lists.add(JSONObject.parseObject<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java))
                            } else if (FeeCountDetail.startsWith("[")) {
                                lists = JSONObject.parseArray<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java)
                            }
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)
                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }

                            intent.putExtra("TYPE", Companion.type)

                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 缴纳气费
     */
    private fun serach10() {
        val body = HttpRequestParams.getParamsForGasFee(generalContact!!.privincecode, generalContact!!.citycode,
                generalContact!!.usernb, "1", generalContact!!.payeecode, null)
        post(HttpURLS.gasArrearsQuery, true, "GasFee", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getJSONObject("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")

                if (ProcessCode == "0000") {
                    val result = GasFeeResult()
                    val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                    result.HasBusifee = ResponseFields.getString("HasBusifee")
                    result.FeeCount = ResponseFields.getString("FeeCount")
                    result.UserAddr = ResponseFields.getString("UserAddr")
                    result.UserNb = ResponseFields.getString("UserNb")
                    result.UserName = ResponseFields.getString("UserName")
                    result.AllGasfee = ResponseFields.getString("AllGasfee")
                    result.EasyNo = ResponseFields.getString("EasyNo")
                    result.NFCFlag = ResponseFields.getString("NfcFlag")
                    result.SecurAlert = ResponseFields.getString("NfcSecurAlert")
                    result.LimitGasfee = ResponseFields.getFloatValue("LimitGasfee")
                    result.NFCNotWriteGas = ResponseFields.getFloatValue("NFCNotWriteGas")

                    if ("11" == result.NFCFlag) {
                        Companion.type = 7
                    }

                    if (!isNFCOk && "11" == result.NFCFlag) {
                        MaterialDialog.Builder(this@PayTheGasFeeActivityOld)
                                .title("提醒")
                                .content("手机不支持NFC功能，请更换手机")
                                .positiveText("确定")
                                .show()
                    } else {
                        var lists: MutableList<GasFeeRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到气费欠费记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java))
                            result.CityCode = generalContact!!.citycode
                            result.ProvinceCode = generalContact!!.privincecode
                            result.PayeeCode = generalContact!!.payeecode

                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld, ResultForGasFeeActivity::class.java)
                            Logger.d(generalContact!!.payeecode)

                            if (generalContact!!.payeecode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }

                            intent.putExtra("TYPE", Companion.type)

                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<GasFeeRecord>(FeeCountDetail, GasFeeRecord::class.java)
                            result.CityCode = generalContact!!.citycode
                            result.ProvinceCode = generalContact!!.privincecode
                            result.PayeeCode = generalContact!!.payeecode
                            result.FeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld, ResultForGasFeeActivity::class.java)

                            Logger.d(generalContact!!.payeecode)

                            if (generalContact!!.payeecode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            if ("11" == result.NFCFlag && Companion.type != 9) {
                                intent.putExtra("TYPE", 7)
                            } else {
                                intent.putExtra("TYPE", Companion.type)
                            }
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }
                    }
                } else {
                    ToastUtil.toastError(ProcessDes)
                    if (ProcessDes.startsWith("由于系统")) {
                        activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                        activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                        activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                    }
                }
            }
        })
    }

    /**
     * IC卡气量表缴费
     */
    private fun serach11() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForGasICMesQuery(if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                city!!.PcityCode
            else
                province!!.CityCode,
                    city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), "1", unit!!.PayeeCode)
            post(HttpURLS.gasICMesQuery, true, "GasIC", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result = GasFeeResult()
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserName = ResponseFields.getString("UserName")
                        result.EasyNo = ResponseFields.getString("EasyNo")
                        result.IcCardno = ResponseFields.getString("IcCardno")
                        result.UserCanAmount = ResponseFields.getFloatValue("UserCanAmount")
                        result.PriceId = ResponseFields.getFloatValue("PriceId")

                        if ("11" != result.NFCFlag) {
                            MaterialDialog.Builder(this@PayTheGasFeeActivityOld)
                                    .title("提醒")
                                    .content("该用户不属于NFC表具，请选择普通缴费")
                                    .positiveText("确定")
                                    .show()
                        } else {
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld, ResultForGasFeeActivity::class.java)
                            intent.putExtra("TYPE", 11)
                            intent.putExtra("GasFeeResult", Parcels.wrap<GasFeeResult>(result))
                            startActivity(intent)
                        }

                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 缴纳营业费
     */
    private fun serach2() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForBusiFee(
                    if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                        city!!.PcityCode
                    else
                        province!!.CityCode,
                    city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), "1", unit!!.PayeeCode, null)
            post(HttpURLS.gasBusinessQuery, true, "BusiFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())

                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        val result = BusiFeeResult()
                        val BusifeeCountDetail = ResponseFields.getString("BusifeeCountDetail")
                        result.BusifeeCount = ResponseFields.getString("BusifeeCount")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserName = ResponseFields.getString("UserName")
                        result.AllBusifee = ResponseFields.getString("AllBusifee")
                        result.EasyNo = ResponseFields.getString("EasyNo")
                        var lists: MutableList<BusiFeeRecord> = ArrayList()
                        if (BusifeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到营业费欠费记录")
                        } else if (BusifeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<BusiFeeRecord>(BusifeeCountDetail, BusiFeeRecord::class.java))
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.BusifeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)

                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 2)
                            intent.putExtra("BusiFeeResult", Parcels.wrap<BusiFeeResult>(result))
                            startActivity(intent)
                        } else if (BusifeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<BusiFeeRecord>(BusifeeCountDetail, BusiFeeRecord::class.java)
                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            result.BusifeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasFeeActivity::class.java)
                            Logger.d(unit!!.PayeeCode)

                            if (unit!!.PayeeCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 2)
                            intent.putExtra("BusiFeeResult", Parcels.wrap<BusiFeeResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 查询气费
     */
    private fun serach3() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val ss = activityPayTheGasFeeBinding!!.tvTime.text.toString().trim({ it <= ' ' }).split(("-").toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            var StartDate = ss[0]
            var EndDate = ss[1]
            StartDate = StartDate.replace(".", "")
            EndDate = EndDate.replace(".", "")
            val body = HttpRequestParams.getParamsForGasBill(
                    if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                        city!!.PcityCode
                    else
                        province!!.CityCode,
                    city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), "1", unit!!.PayeeCode, StartDate, EndDate)
            post(HttpURLS.gasArrearsZhangDanQuery, true, "GasBill", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())

                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        val result = GasBillResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result.EasyNo = ResponseFields.getString("EasyNo")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserName = ResponseFields.getString("UserName")
                        result.FeeCount = ResponseFields.getString("FeeCount")
                        result.HasBusifee = ResponseFields.getString("HasBusifee")
                        result.HasBusifee = ResponseFields.getString("GasPrice")
                        var lists = ArrayList<GasBillRecord>()
                        if (FeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到气费账单记录")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<GasBillRecord>(FeeCountDetail, GasBillRecord::class.java))
                            result.FeeCountDetail = doSomeThing(lists)
//                                    result.FeeCountDetail = lists

                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasBillActivity::class.java)
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasBillResult", Parcels.wrap<GasBillResult>(result))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<GasBillRecord>(FeeCountDetail,
                                    GasBillRecord::class.java) as ArrayList<GasBillRecord>
                            // result.FeeCountDetail =
                            // doSomeThing(lists);
                            result.FeeCountDetail = lists

                            result.CityCode = city!!.CityCode
                            result.ProvinceCode = if (province == null || province!!.CityCode == null
                                    || province!!.CityCode == "9999")
                                city!!.PcityCode
                            else
                                province!!.CityCode
                            result.PayeeCode = unit!!.PayeeCode
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasBillActivity::class.java)
                            intent.putExtra("TYPE", 1)
                            intent.putExtra("GasBillResult", Parcels.wrap<GasBillResult>(result))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    /**
     * 气费账单 相同月份 处理
     */
    private fun doSomeThing(lists: ArrayList<GasBillRecord>): ArrayList<GasBillRecord> {
        val mergeLists = ArrayList<GasBillRecord>()
        var p: GasBillRecord? = null
        for (i in 1..lists.size) {
            if (i < lists.size) {
                if (lists[i].FeeMonth != lists[i - 1].FeeMonth) {
                    p = lists[i - 1]
                    mergeLists.add(p)
                    p = null
                } else if (lists[i].FeeMonth == lists[i - 1].FeeMonth) {
                    // 设置上期止码
                    val a0 = Integer.valueOf(if (lists[i - 1].LastReading == null)
                        "0"
                    else
                        lists[i - 1].LastReading)!!
                    val b0 = Integer.valueOf(
                            if (lists[i].LastReading == null) "0" else lists[i].LastReading)!!
                    lists[i].LastReading = (a0 + b0).toString()
                    // 设置气量
                    val a1 = Integer.valueOf(
                            if (lists[i - 1].GasNb == null) "0" else lists[i - 1].GasNb)!!
                    val b1 = Integer
                            .valueOf(if (lists[i].GasNb == null) "0" else lists[i].GasNb)!!
                    lists[i].GasNb = (a1 + b1).toString()
                    // 当前欠费总额
                    val a2 = java.lang.Float.valueOf(if (lists[i - 1].CurrentTotalAmount == null)
                        "0"
                    else
                        lists[i - 1].CurrentTotalAmount)!!
                    val b2 = java.lang.Float.valueOf(if (lists[i].CurrentTotalAmount == null)
                        "0"
                    else
                        lists[i].CurrentTotalAmount)!!
                    lists[i].CurrentTotalAmount = (a2 + b2).toString()
                    // 已缴气费
                    val a3 = java.lang.Float.valueOf(if (lists[i - 1].PayAmount == null)
                        "0"
                    else
                        lists[i - 1].PayAmount)!!
                    val b3 = java.lang.Float.valueOf(
                            if (lists[i].PayAmount == null) "0" else lists[i].PayAmount)!!
                    lists[i].PayAmount = (a3 + b3).toString()

                    // 设置本期止码
                    val a4 = Integer.valueOf(if (lists[i - 1].CurrentReading == null)
                        "0"
                    else
                        lists[i - 1].CurrentReading)!!
                    val b4 = Integer.valueOf(if (lists[i].CurrentReading == null)
                        "0"
                    else
                        lists[i].CurrentReading)!!
                    lists[i].CurrentReading = (a4 + b4).toString()
                    p = lists[i]
                }
            } else if (i == lists.size) {
                if (p == null) {
                    p = lists[i - 1]
                    mergeLists.add(p)
                    p = null
                } else {
                    mergeLists.add(p)
                    p = null
                }
            }
        }
        return mergeLists
    }

    /**
     * 查询营业费
     */
    private fun serach4() {
        if (!isLetterDigitOrChinese(activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }))) {
            ToastUtil.toastWarning("只允许输入数字与字母")
            return
        }
        if (checkEmpty()) {
            val ss = activityPayTheGasFeeBinding!!.tvTime.text.toString().trim({ it <= ' ' }).split(("-").toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            var StartDate = ss[0]
            var EndDate = ss[1]
            StartDate = StartDate.replace(".", "")
            EndDate = EndDate.replace(".", "")
            val body = HttpRequestParams.getParamsForBusiBill(
                    if (province == null || province!!.CityCode == null || province!!.CityCode == "9999")
                        city!!.PcityCode
                    else
                        province!!.CityCode,
                    city!!.CityCode, activityPayTheGasFeeBinding!!.etNum.text.toString().trim({ it <= ' ' }), "1", unit!!.PayeeCode, StartDate, EndDate)
            post(HttpURLS.gasBusinessZhangDanQuery, true, "BusiBill", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        val result = BusiBillResult()
                        val BusifeeCountDetail = ResponseFields.getString("BusifeeCountDetail")
                        result.EasyNo = ResponseFields.getString("EasyNo")
                        result.UserAddr = ResponseFields.getString("UserAddr")
                        result.UserNb = ResponseFields.getString("UserNb")
                        result.UserName = ResponseFields.getString("UserName")
                        result.BusifeeCount = ResponseFields.getString("BusifeeCount")
                        var lists: MutableList<BusiBillRecord> = ArrayList()
                        if (BusifeeCountDetail == null) {
                            ToastUtil.toastInfo("没有查到营业费账单记录")
                        } else if (BusifeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject<BusiBillRecord>(BusifeeCountDetail, BusiBillRecord::class.java))
                            result.BusifeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasBillActivity::class.java)
                            intent.putExtra("TYPE", 2)
                            intent.putExtra("BusiBillResult", Parcels.wrap<BusiBillResult>(result))
                            startActivity(intent)
                        } else if (BusifeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray<BusiBillRecord>(BusifeeCountDetail, BusiBillRecord::class.java)
                            result.BusifeeCountDetail = lists
                            Logger.d(result.toString())
                            val intent = Intent(this@PayTheGasFeeActivityOld,
                                    ResultForGasBillActivity::class.java)
                            intent.putExtra("TYPE", 2)
                            intent.putExtra("BusiBillResult", Parcels.wrap<BusiBillResult>(result))
                            startActivity(intent)
                        }
                        // result.CityCode = city.CityCode;
                        // result.ProvinceCode =
                        // province==null||province.CityCode==null||province.CityCode.equals("9999")?city.PcityCode:province.CityCode;
                        // result.PayeeCode = unit.PayeeCode;
                    } else {
                        ToastUtil.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityPayTheGasFeeBinding!!.buttonNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityPayTheGasFeeBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityPayTheGasFeeBinding!!.buttonNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    private fun checkEmpty(): Boolean {
        if (TextUtils.isEmpty(city!!.CityCode)) {
            ToastUtil.toastWarning("请选择城市")
            return false
        }
        if (TextUtils.isEmpty(unit!!.PayeeCode)) {
            ToastUtil.toastWarning("请选择缴费单位")
            return false
        }
        if (type == 3 || type == 4) {
            if (TextUtils.isEmpty(activityPayTheGasFeeBinding!!.tvTime.text.toString())) {
                ToastUtil.toastWarning("请选择起始时间")
                return false
            }
        }
        if (TextUtils.isEmpty(activityPayTheGasFeeBinding!!.etNum.text.toString())) {
            ToastUtil.toastWarning("请填写户号")
            return false
        }
        return true
    }

    //声明AMapLocationClient类对象
    var mLocationClient: AMapLocationClient? = null
    //声明定位回调监听器
    var mLocationListener: AMapLocationListener = AMapLocationListener { aMapLocation ->
        if (aMapLocation != null && aMapLocation.errorCode == 0) {
            // 定位成功回调信息，设置相关消息
            Logger.e("Latitude--->" + aMapLocation.latitude)
            Logger.e("Longitude--->" + aMapLocation.longitude)
            Logger.e("Accurancy--->" + (aMapLocation.accuracy).toString())
            Logger.e("Method--->" + aMapLocation.provider)
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date(aMapLocation.time)
            Logger.e("Time--->" + df.format(date))
            Logger.e("Des--->" + aMapLocation.address)
            if (aMapLocation.province == null) {
                Logger.e("Province--->" + "null")
            } else {
                Logger.e("Province--->" + aMapLocation.province)
            }
            activityPayTheGasFeeBinding!!.tvCity.text = aMapLocation.city
            city!!.CityName = aMapLocation.city

            Logger.e("city--->" + aMapLocation.city)
            Logger.e("adCode--->" + aMapLocation.adCode)

            city!!.CityCode = aMapLocation.adCode.substring(0, 4) + "00"

            city!!.PcityCode = city!!.CityCode.substring(0, 2) + "0000"

            Logger.e("CityCode--->" + city!!.CityCode)
            Logger.e("PcityCode--->" + city!!.PcityCode)

            activityPayTheGasFeeBinding!!.unitpicker?.initView(city!!.CityCode, DataBaiduPush.getPmttpType())

            Logger.e("PcityCode--->" + city!!.PcityCode)

        }
        if (mLocationClient != null) {
            mLocationClient!!.stopLocation()//停止定位后，本地定位服务并不会被销毁
            mLocationClient!!.onDestroy()//销毁定位客户端，同时销毁本地定位服务。
        }
    }

    //声明AMapLocationClientOption对象
    var mLocationOption: AMapLocationClientOption? = null

    internal var ACCESS_COARSE_LOCATION_REQUEST_CODE = 10
    internal var ACCESS_FINE_LOCATION_REQUEST_CODE = 11
    internal var WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 12
    internal var READ_EXTERNAL_STORAGE_REQUEST_CODE = 13
    internal var READ_PHONE_STATE_REQUEST_CODE = 14

    /**
     * 初始化定位
     */
    private fun init() {
        //		//SDK在Android 6.0下需要进行运行检测的权限如下：
        //		Manifest.permission.ACCESS_COARSE_LOCATION,
        //				Manifest.permission.ACCESS_FINE_LOCATION,
        //				Manifest.permission.WRITE_EXTERNAL_STORAGE,
        //				Manifest.permission.READ_EXTERNAL_STORAGE,
        //				Manifest.permission.READ_PHONE_STATE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    ACCESS_COARSE_LOCATION_REQUEST_CODE)//自定义的code
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_FINE_LOCATION_REQUEST_CODE)//自定义的code
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE)//自定义的code
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE)//自定义的code
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE),
                    READ_PHONE_STATE_REQUEST_CODE)//自定义的code
        } else {
            //初始化定位
            mLocationClient = AMapLocationClient(applicationContext)
            //设置定位回调监听
            mLocationClient!!.setLocationListener(mLocationListener)
            //初始化AMapLocationClientOption对象
            mLocationOption = AMapLocationClientOption()
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //获取一次定位结果：
            //该方法默认为false。
            mLocationOption!!.isOnceLocation = true
            //获取最近3s内精度最高的一次定位结果：
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            mLocationOption!!.isOnceLocationLatest = true
            //给定位客户端对象设置定位参数
            mLocationClient!!.setLocationOption(mLocationOption)
            //启动定位
            mLocationClient!!.startLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Logger.d("onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init()
            } else {
            }
        } else if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                init()
            } else {
                // Permission Denied
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                init()
            } else {
                // Permission Denied
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                init()
            } else {
                // Permission Denied
            }
        } else if (requestCode == READ_PHONE_STATE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                init()
            } else {
                // Permission Denied
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun initPopupWindow() {
        activityPayTheGasFeeBinding!!.citypicker.initView(DataBaiduPush.getPmttp())
        activityPayTheGasFeeBinding!!.unitpicker.setActivity(this)
        activityPayTheGasFeeBinding!!.citypicker.setOnSelectedListener({ province1, city1 ->
            hideSoftInput()
            // ToastUtil.toast(PayTheGasFeeActivityOld.this, "单击--2");
            activityPayTheGasFeeBinding!!.citypicker.visibility = View.INVISIBLE
            activityPayTheGasFeeBinding!!.citypicker.setDefaut()
            if (province1 != null && city1 != null) {
                Logger.d(province1.CityName.trim({ it <= ' ' }) + ":" + city1.CityName.trim({ it <= ' ' }))
                if (province1.CityCode != "9999") {
                    activityPayTheGasFeeBinding!!.tvCity.text = province1.CityName.trim({ it <= ' ' }) + "  " + city1.CityName.trim({ it <= ' ' })
                } else {
                    activityPayTheGasFeeBinding!!.tvCity.text = city1.CityName.trim({ it <= ' ' })
                }
                this@PayTheGasFeeActivityOld.province = province1
                this@PayTheGasFeeActivityOld.city = city1
                activityPayTheGasFeeBinding!!.unitpicker.initView(city1.CityCode, DataBaiduPush.getPmttpType())
            } else {
                ToastUtil.toastWarning("城市列表还未加载，请重新选择")
            }
        })

        activityPayTheGasFeeBinding!!.unitpicker.setOnInitdataedListener({ unit1 ->
            if (unit1 != null) {
                Logger.d(unit1.PayeeNm.trim({ it <= ' ' }) + ":" + unit1.PayeeCode.trim({ it <= ' ' }))
                activityPayTheGasFeeBinding!!.tvUnit.text = unit1.PayeeNm.trim({ it <= ' ' })
                this@PayTheGasFeeActivityOld.unit = unit1
            } else {
                activityPayTheGasFeeBinding!!.tvUnit.text = ""
            }
        })

        activityPayTheGasFeeBinding!!.unitpicker.setOnSelectedListener({ unit12 ->
            activityPayTheGasFeeBinding!!.unitpicker.visibility = View.INVISIBLE
            if (unit12 != null) {
                Logger.d(unit12.PayeeNm.trim({ it <= ' ' }) + ":" + unit12.PayeeCode.trim({ it <= ' ' }))
                activityPayTheGasFeeBinding!!.tvUnit.text = unit12.PayeeNm.trim({ it <= ' ' })
                this@PayTheGasFeeActivityOld.unit = unit12
            } else {
                ToastUtil.toastWarning("缴费单位列表还未加载或无缴费单位")
            }
        })
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        if (year > yearEnd) {
            MaterialDialog.Builder(this)
                    .title("提示")
                    .content("只能查询最近6个月账单！")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .build().show()
        } else if (monthOfYear > monthOfYearEnd) {
            MaterialDialog.Builder(this)
                    .title("提示")
                    .content("只能查询最近6个月账单！")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .build().show()
        } else if (yearEnd - year > 1) {
            MaterialDialog.Builder(this)
                    .title("提示")
                    .content("只能查询最近6个月账单！")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .build().show()
        } else if ((yearEnd == year && monthOfYearEnd - monthOfYear > 5) || (yearEnd == year + 1 && monthOfYearEnd + 12 - monthOfYear > 5)) {
            MaterialDialog.Builder(this)
                    .title("提示")
                    .content("只能查询最近6个月账单！")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .build().show()
        } else {
            activityPayTheGasFeeBinding!!.tvTime.text = year.toString() + "." +
                    DateTextUtils.DateToString(monthOfYear + 1) + "-" + yearEnd + "." +
                    DateTextUtils.DateToString(monthOfYearEnd + 1)
        }

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (activityPayTheGasFeeBinding!!.citypicker != null && activityPayTheGasFeeBinding!!.citypicker.visibility == View.VISIBLE) {
                activityPayTheGasFeeBinding!!.citypicker.visibility = View.INVISIBLE
                activityPayTheGasFeeBinding!!.citypicker.setDefaut()
                return false
            }
            if (activityPayTheGasFeeBinding!!.unitpicker != null && activityPayTheGasFeeBinding!!.unitpicker.visibility == View.VISIBLE) {
                activityPayTheGasFeeBinding!!.unitpicker.visibility = View.INVISIBLE
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun hideSoftInput(): Boolean {
        App.manager!!.hideSoftInputFromWindow(activityPayTheGasFeeBinding!!.tvUnit.windowToken, 0)
        return false
    }

    /**
     * 检测设备是否支持NFC并检测是否已开启NFC
     */
    @SuppressLint("NewApi")
    private fun initNFC() {
        val mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        isNFCOk = mNfcAdapter != null
    }

    override fun onItemClick(view: View, position: Int) {
        province!!.CityCode = generalContactList!![position].privincecode
        city!!.CityCode = generalContactList!![position].citycode
        unit!!.PayeeCode = generalContactList!![position].payeecode
        activityPayTheGasFeeBinding!!.citypicker.getaddressinfo(2)
        activityPayTheGasFeeBinding!!.unitpicker.resetView(city!!.CityCode, DataBaiduPush.getPmttpType())
        activityPayTheGasFeeBinding!!.tvCity.text = generalContactList!![position].pcityname + generalContactList!![position].ccityname
        activityPayTheGasFeeBinding!!.tvUnit.text = generalContactList!![position].cdtrnm
        activityPayTheGasFeeBinding!!.etNum.setText(generalContactList!![position].usernb)
        activityPayTheGasFeeBinding!!.etNum.hidePopWindow()
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    companion object {

        private var type: Int = 0// 1:缴纳气费;2:缴纳营业费;3:查询气费;4:查询营业费;5：缴纳水费;6:查询水费账单;7:NFC预存气费;8:营业费预存;9:蓝牙读卡器缴气费;10:常用联系人缴费;11:IC卡气量表缴费

        fun isLetterDigitOrChinese(str: String): Boolean {
            val regex = "^[a-z0-9A-Z]+$"
            return str.matches((regex).toRegex())
        }
    }
}
