package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.City
import com.mqt.ganghuazhifu.bean.GasFeeRecord
import com.mqt.ganghuazhifu.bean.GasFeeResult
import com.mqt.ganghuazhifu.bean.Unit
import com.mqt.ganghuazhifu.databinding.ActivityYuCunBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.util.*

/**
 * 营业费预存（废弃）
 *
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class YucunActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var provinceCode: String? = null
    private var cityCode: String? = null
    private var unitCode: String? = null
    private var phone: String? = null
    private var provinceName: String? = null
    private var cityName: String? = null
    private var unitName: String? = null
    private var activityYuCunBinding: ActivityYuCunBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityYuCunBinding = DataBindingUtil.setContentView<ActivityYuCunBinding>(this, R.layout.activity_yu_cun)
        provinceCode = intent.getStringExtra("provinceCode")
        cityCode = intent.getStringExtra("cityCode")
        unitCode = intent.getStringExtra("unitCode")
        phone = intent.getStringExtra("phone")
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityYuCunBinding!!.toolbar)
        supportActionBar!!.title = "预存营业费"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityYuCunBinding!!.cardViewNext.setOnClickListener(this)
        Logger.i("provinceCode---->" + provinceCode!!)
        Logger.i("cityCode---->" + cityCode!!)
        Logger.i("unitCode---->" + unitCode!!)
        Logger.i("phone---->" + phone!!)
        initProvinceName("010002")
    }

    private fun initProvinceName(pmtty: String) {
        val body = HttpRequestParams.getParamsForCityCodeQuery("086", "01", pmtty)
        showRoundProcessDialog()
        post(HttpURLS.cityCodeQuery, true, "cityCodeQuery", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
                dismissRoundProcessDialog()
                ToastUtil.Companion.toastWarning("获取省份信息错误，请重新扫码!")
            } else {
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                val a = response.getString("ResponseFields")
                var provinces = ArrayList<City>()
                if (ProcessCode == "0000") {
                    if (type == 1) {
                        val ResponseFields = response.getString("ResponseFields")
                        if (ResponseFields != null) {
                            provinces = JSONObject.parseArray(ResponseFields, City::class.java) as ArrayList<City>
                        }
                    } else if (type == 2) {
                        val ResponseFields1 = response.getJSONObject("ResponseFields")
                        if (ResponseFields1 != null) {
                            val proinfo = ResponseFields1.getString("CityDetail")
                            provinces = ArrayList<City>()
                            if (proinfo.startsWith("[")) {
                                provinces = JSONObject.parseArray(proinfo, City::class.java) as ArrayList<City>
                            } else if (proinfo.startsWith("{")) {
                                provinces.add(JSONObject.parseObject(proinfo, City::class.java))
                            }
                        }
                    }
                    var s: Boolean = false
                    for (city in provinces) {
                        if (city.CityCode == provinceCode) {
                            provinceName = city.CityName
                            initCityName(provinceCode.toString(), "010002")
                            s = true
                            break
                        }
                    }
                    dismissRoundProcessDialog()
                    if (!s)
                        ToastUtil.Companion.toastError("省份信息不匹配，请重新扫码！")
                }
            }
        })
    }

    private fun initCityName(provinceCode: String, pmtty: String) {
        val body = HttpRequestParams.getParamsForCityCodeQuery(provinceCode, "02", pmtty)
        post(HttpURLS.cityCodeQuery, true, "cityCodeQuery", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
                dismissRoundProcessDialog()
                ToastUtil.Companion.toastWarning("获取城市失败，请重新扫码!")
            } else {
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                var citys = ArrayList<City>()
                if (ProcessCode == "0000") {

                    val ResponseFields = response.getString("ResponseFields")
                    if (type == 1) {
                        citys = JSONObject.parseArray(ResponseFields, City::class.java) as ArrayList<City>
                    } else if (type == 2) {
                        val ResponseField1 = response.getJSONObject("ResponseFields")
                        val cityDetail = ResponseField1.getString("CityDetail")
                        citys.add(JSONObject.parseObject(cityDetail, City::class.java) as City)
                    }

                    var s: Boolean = false
                    for (city in citys) {
                        if (cityCode!!.trim { it <= ' ' } == city.CityCode.trim { it <= ' ' }) {
                            cityName = city.CityName
                            activityYuCunBinding!!.tvCity.text = provinceName + " " + cityName
                            initUnitName(cityCode!!)
                            s = true
                            break
                        }
                    }
                    dismissRoundProcessDialog()
                    if (!s)
                        ToastUtil.Companion.toastError("城市信息不匹配，请重新扫码！")
                }
            }
        })
    }

    private fun initUnitName(code: String) {
        val body = HttpRequestParams.getParamsForPayeesQuery(code, "01")
        post(HttpURLS.payeesQuery, true, "PayeesQuery", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
                dismissRoundProcessDialog()
                ToastUtil.Companion.toastWarning("获取缴费单位失败，请重新扫码!")
            } else {
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                var unit = ArrayList<Unit>()
                if (ProcessCode == "0000") {
                    if (type == 1) {
                        val ResponseFields = response.getString("ResponseFields")
                        if (ResponseFields != null) {
                            unit = ArrayList<Unit>()
                            if (ProcessCode == "0000") {
                                unit = JSONObject.parseArray(ResponseFields, Unit::class.java) as ArrayList<Unit>
                            }
                        }
                    } else if (type == 2) {
                        val ResponseFields1 = response.getJSONObject("ResponseFields")
                        if (ResponseFields1 != null) {
                            val PayeesDetail = ResponseFields1.getString("PayeesDetail")
                            unit = ArrayList<Unit>()
                            if (ProcessCode == "0000") {
                                unit.add(JSONObject.parseObject(PayeesDetail, Unit::class.java))
                            }
                        }
                    }
                }
                dismissRoundProcessDialog()
                var s: Boolean = false
                for (uni in unit) {
                    if (unitCode!!.trim { it <= ' ' } == uni.PayeeCode.trim { it <= ' ' }) {
                        unitName = uni.PayeeNm
                        activityYuCunBinding!!.tvUnit.text = unitName
                        s = true
                        break
                    }
                }
                if (!s)
                    ToastUtil.Companion.toastError("缴费单位信息不匹配，请重新扫码！")
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
        serach2()
    }

    /**
     * 缴纳营业费
     */
    private fun serach2() {
        if (!isLetterDigitOrChinese(activityYuCunBinding!!.etNum.text.toString().trim { it <= ' ' })) {
            ToastUtil.Companion.toastWarning("只允许输入数字与字母!")
            return
        }
        if (checkEmpty()) {
            val body = HttpRequestParams.getParamsForGasFee(provinceCode, cityCode,
                    activityYuCunBinding!!.etNum.text.toString().trim { it <= ' ' }, "1", unitCode, "10")
            post(HttpURLS.gasArrearsQuery, true, "BusiFee", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ResponseFields = response.getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")

                    if (ProcessCode == "0000") {
                        val result1 = GasFeeResult()
                        val FeeCountDetail = ResponseFields.getString("FeeCountDetail")
                        result1.HasBusifee = ResponseFields.getString("HasBusifee")
                        result1.FeeCount = ResponseFields.getString("FeeCount")
                        result1.UserAddr = ResponseFields.getString("UserAddr")
                        result1.UserNb = ResponseFields.getString("UserNb")
                        result1.UserName = ResponseFields.getString("UserName")
                        result1.AllGasfee = ResponseFields.getString("AllGasfee")
                        result1.EasyNo = ResponseFields.getString("EasyNo")
                        result1.NFCFlag = ResponseFields.getString("NFCFlag")
//                        result1.NFCSecurAlert = ResponseFields.getString("NFCSecurAlert")
//                        result1.NFCLimitGasFee = ResponseFields.getFloatValue("NFCLimitGasFee")
//                        result1.NFCNotWriteGas = ResponseFields.getFloatValue("NFCNotWriteGas")
//                        result1.NFCICSumCount = ResponseFields.getInteger("NFCICSumCount")

                        var lists: MutableList<GasFeeRecord> = ArrayList()
                        if (FeeCountDetail == null) {
                            ToastUtil.Companion.toastInfo("没有查到气费欠费记录!")
                        } else if (FeeCountDetail.startsWith("{")) {
                            lists.add(JSONObject.parseObject(FeeCountDetail, GasFeeRecord::class.java))
                            result1.CityCode = cityCode
                            result1.ProvinceCode = provinceCode
                            result1.PayeeCode = unitCode
                            result1.FeeCountDetail = lists
                            Logger.d(result1.toString())
                            val intent = Intent(this@YucunActivity, ResultForGasFeeActivity::class.java)

                            if (unitCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }

                            intent.putExtra("TYPE", 8)
                            intent.putExtra("StaffPhone", phone)
                            intent.putExtra("GasFeeResult", Parcels.wrap(result1))
                            startActivity(intent)
                        } else if (FeeCountDetail.startsWith("[")) {
                            lists = JSONObject.parseArray(FeeCountDetail, GasFeeRecord::class.java)
                            result1.CityCode = cityCode
                            result1.ProvinceCode = provinceCode
                            result1.PayeeCode = unitCode
                            result1.FeeCountDetail = lists

                            Logger.d(result1.toString())
                            val intent = Intent(this@YucunActivity, ResultForGasFeeActivity::class.java)

                            if (unitCode == "320000320500010057") {
                                intent.putExtra("UNITTYPE", 2)// 吴江港华燃气有限公司
                            } else {
                                intent.putExtra("UNITTYPE", 1)
                            }
                            intent.putExtra("TYPE", 8)
                            intent.putExtra("GasFeeResult", Parcels.wrap(result1))
                            startActivity(intent)
                        }
                    } else {
                        ToastUtil.Companion.toastError(ProcessDes)
                        if (ProcessDes.startsWith("由于系统")) {
                            activityYuCunBinding!!.cardViewNext.setCardBackgroundColor(Color.parseColor("#7D7D7D"))
                            activityYuCunBinding!!.tvNext.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                            activityYuCunBinding!!.cardViewNext.isClickable = false
                        }
                    }
                }
            })
        }
    }

    private fun checkEmpty(): Boolean {
        if (TextUtils.isEmpty(activityYuCunBinding!!.etNum.text.toString())) {
            ToastUtil.Companion.toastWarning("请填写户号!")
            return false
        }
        return true
    }

    fun isLetterDigitOrChinese(str: String): Boolean {
        val regex = "^[a-z0-9A-Z]+$"
        return str.matches(regex.toRegex())
    }

    companion object {
        fun startActivity(context: Context, provinceCode: String, cityCode: String, unitCode: String, phone: String) {
            val intent = Intent(context, YucunActivity::class.java)
            intent.putExtra("provinceCode", provinceCode)
            intent.putExtra("cityCode", cityCode)
            intent.putExtra("unitCode", unitCode)
            intent.putExtra("phone", phone)
            context.startActivity(intent)
        }
    }

}
