package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.Order
import com.mqt.ganghuazhifu.bean.WaterFeeResult
import com.mqt.ganghuazhifu.databinding.ActivityResultForWaterFeeBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.util.*

/**
 * 缴纳水费欠费查询结果

 * @author bo.sun
 * *
 * @date 2015-10-15
 */
class ResultForWaterFeeActivity : BaseActivity() {

    private var activityResultForWaterFeeBinding: ActivityResultForWaterFeeBinding? = null

    private var waterFeeResult: WaterFeeResult? = null
    private var Pmttp = "020001"
    private var Amount = ""
    private var UserNb = ""
    private var UserNm = ""
    private var UserAddr = ""
    private var CityCode = ""
    private var ProvinceCode = ""
    private var PayeeCode = ""
    private var QueryId = ""
    private var LoginAccount: String? = null
    internal var FeeCountDetail = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultForWaterFeeBinding = DataBindingUtil.setContentView<ActivityResultForWaterFeeBinding>(this,
                R.layout.activity_result_for_water_fee)
        waterFeeResult = Parcels.unwrap<WaterFeeResult>(intent.getParcelableExtra<Parcelable>("WaterFeeResult"))
        initView()
        if (waterFeeResult != null) {
            setDatatoView()
        }
    }

    private fun setDatatoView() {

        val name: String = waterFeeResult!!.UserName
        val address: String = waterFeeResult!!.UserAddr
        val nameBuilder: StringBuilder

        activityResultForWaterFeeBinding!!.wtAccountNumber.text = waterFeeResult!!.UserNb
        activityResultForWaterFeeBinding!!.wtAdvance.text = "￥" + waterFeeResult!!.AB
        //		wt_charge_type.setText("缴费方式：" + waterFeeResult.ChargeType);
        nameBuilder = StringBuilder(name)

        if (name.length <= 1) {

        } else if (name.length <= 3) {
            nameBuilder.setCharAt(0, '*')
        } else {
            for (i in 0..name.length - 2 - 1) {
                nameBuilder.setCharAt(i, '*')
            }
        }
        activityResultForWaterFeeBinding!!.wtAccountName.text = nameBuilder.toString()
        activityResultForWaterFeeBinding!!.wtAccountAddress.text = address
        activityResultForWaterFeeBinding!!.wtAllWaterFee.text = "￥" + waterFeeResult!!.AllWaterFee
        //		if(waterFeeResult.TSH == null){
        //			wt_TSH.setText("");
        //		}else{
        //			wt_TSH.setText(waterFeeResult.TSH);
        //		}
        activityResultForWaterFeeBinding!!.wtFEEYJSF.text = "￥" + waterFeeResult!!.FEE_YJSF
        activityResultForWaterFeeBinding!!.wtFEEYJZNJ.text = "￥" + waterFeeResult!!.FEE_YJZNJ
        activityResultForWaterFeeBinding!!.wtFEEMON.text = "欠费记录账务年月区间：" + waterFeeResult!!.FEE_MON

        //		adapter = new CheckboxAdapter(this,waterFeeResult.WaterFeeCountDetail);
        //		listView_water_bill.setAdapter(adapter);
    }

    private fun initView() {
        setSupportActionBar(activityResultForWaterFeeBinding!!.toolbar)
        supportActionBar!!.title = "查询结果"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activityResultForWaterFeeBinding!!.wtButtonPayment.setOnClickListener(this)
        val calendar = Calendar.getInstance(Locale.CHINA)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        //支付按钮的响应方法
        when (v.id) {
            R.id.wt_button_payment -> {
                val user = EncryptedPreferencesUtils.getUser()
                Pmttp = "020001"
                Amount = waterFeeResult!!.AllWaterFee
                UserNb = waterFeeResult!!.UserNb
                UserNm = waterFeeResult!!.UserName
                UserAddr = waterFeeResult!!.UserAddr
                CityCode = waterFeeResult!!.CityCode
                ProvinceCode = waterFeeResult!!.ProvinceCode
                PayeeCode = waterFeeResult!!.PayeeCode
                LoginAccount = user.LoginAccount
                QueryId = waterFeeResult!!.QueryId

                var listSize = 0
                for (i in waterFeeResult!!.WaterFeeCountDetail.indices) {
                    listSize = i + 1
                }
                val infor = arrayOfNulls<String>(listSize)
                val sb = StringBuffer()
                for (j in waterFeeResult!!.WaterFeeCountDetail.indices) {
                    var abc: String
                    if (waterFeeResult!!.WaterFeeCountDetail[j] != null) {
                        val FeeId = waterFeeResult!!.WaterFeeCountDetail[j].FeeId//水费记录号
                        val Amount1 = waterFeeResult!!.WaterFeeCountDetail[j].Amount//账单金额
                        val LateAmount = waterFeeResult!!.WaterFeeCountDetail[j].LateAmount//违约金
                        if (j == 0) {
                            abc = "{" +
                                    "\"FeeId\":\"" + FeeId + "\"," +
                                    //								 "\"FeeMonth\":\"" + FeeMonth + "\"," +
                                    //								 "\"LastBal\":\"" + LastBal + "\"," +
                                    "\"Amount\":\"" + Amount1 + "\"," +
                                    //								 "\"PaymentAmount\":\"" + PaymentAmount + "\"," +
                                    "\"LateAmount\":\"" + LateAmount +
                                    "\"}"
                        } else {

                            abc = ",{" +
                                    "\"FeeId\":\"" + FeeId + "\"," +
                                    //								 "\"FeeMonth\":\"" + FeeMonth + "\"," +
                                    //								 "\"LastBal\":\"" + LastBal + "\"," +
                                    "\"Amount\":\"" + Amount1 + "\"," +
                                    //								 "\"PaymentAmount\":\"" + PaymentAmount + "\"," +
                                    "\"LateAmount\":\"" + LateAmount +
                                    "\"}"
                        }
                        if (waterFeeResult!!.WaterFeeCountDetail.size == 0) {
                            ToastUtil.toastInfo("账单查询结果显示，您没有欠费!")
                            return
                        } else {
                            infor[j] = abc
                            sb.append(infor[j])
                        }
                    }
                }
                val s = sb.toString()
                FeeCountDetail = "[$s]"
                orderSubmit()
            }
        }
    }

    private fun orderSubmit() {
        if (java.lang.Float.parseFloat(Amount) > 0) {
            val user = EncryptedPreferencesUtils.getUser()
            val body = HttpRequestParams.getParamsForWaterOrderSubmit(
                    Pmttp,
                    Amount,
                    UserNb,
                    UserNm,
                    UserAddr,
                    CityCode,
                    ProvinceCode,
                    PayeeCode,
                    user.LoginAccount,
                    FeeCountDetail,
                    QueryId)
            post(HttpURLS.waterOrderSubmit, true, "waterOrderSubmit", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                } else {
                    Logger.d(response.toString())
                    val ResponseHead = response
                            .getJSONObject("ResponseHead")
                    val ResponseFields = response
                            .getJSONObject("ResponseFields")
                    val ProcessCode = ResponseHead
                            .getString("ProcessCode")
                    val ProcessDes = ResponseHead
                            .getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        val OrderNb = ResponseFields.getString("OrderNb")
                        val OrderSetTime = ResponseFields.getString("OrderSetTime")
                        val PayeeNm = ResponseFields.getString("PayeeNm")
                        val order = Order()
                        order.OrderNb = OrderNb
                        order.OrderSetTime = OrderSetTime
                        order.ProductName = PayeeNm
                        order.OrderAmount = Amount
                        order.usernb = UserNb
                        Logger.d(order.toString())
                        ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity::class.java)
                        val intent = Intent(this@ResultForWaterFeeActivity, PayActivity::class.java)
                        intent.putExtra("Order", Parcels.wrap(order))
                        startActivity(intent)
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        } else if (java.lang.Float.parseFloat(Amount) == 0f) {
            ToastUtil.toastInfo("账单查询结果显示，您没有欠费!")
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}